/**
 * Copyright (c) 2015, Thomas Cashman
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of the TilePacker nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL tilepacker.org BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.tilepacker.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Main entry class
 * 
 * @author Thomas Cashman
 */
public class TilePacker {
	public static boolean FIX_TEARING = false;
	public static String FORMAT = "PNG";
	public static File TARGET_DIRECTORY;

	private final Serializer serializer;
	private final File configFile;
	private final File configFileDir;
	private final TilePackerConfig config;
	private final List<TileConfig> inputFiles;
	private List<Tileset> tilesets;
	
	/**
	 * Constructor
	 */
	public TilePacker(String configFileDir, boolean rewrite) {
		this(new File(configFileDir), rewrite);
	}

	/**
	 * Constructor
	 */
	public TilePacker(File configFileDir, boolean rewrite) {
		super();
		
		this.configFileDir = configFileDir;
		this.configFile = new File(configFileDir, "config.xml");
		tilesets = new ArrayList<Tileset>();

		serializer = new Persister();
		
		if(configFile.exists()) {
			try {
				config = serializer.read(TilePackerConfig.class, configFile);
				
				if(rewrite) {
					config.getTiles().clear();
				}
			} catch (Exception e) {
				throw new TilePackerException("Error reading config file", e);
			}
		} else {
			config = new TilePackerConfig();
			config.setTiles(new ArrayList<TileConfig>());
		}
		findTileFiles(config, this.configFileDir);

		Tile.WIDTH = config.getTileWidth();
		Tile.HEIGHT = config.getTileHeight();
		Tile.PADDING = config.getTilePadding();

		Tileset.MAX_WIDTH = config.getTilesetWidth();
		Tileset.MAX_HEIGHT = config.getTilesetHeight();
		
		Tileset.PREMULTIPLY_ALPHA = config.isPremultiplyAlpha();
		Tileset.BACKGROUND_COLOR = config.getBackgroundColor();

		TilePacker.FIX_TEARING = config.isPreventTearing();
		if(TilePacker.FIX_TEARING && Tile.PADDING < 1) {
			throw new TilePackerException("ERROR: If tearing prevention is enabled, the tile padding must be greater than 0");
		}
		TilePacker.FORMAT = config.getOutputFormat();
		TilePacker.TARGET_DIRECTORY = new File(configFile.getParent(), config.getOutputPath());
		inputFiles = config.getTiles();
	}
	
	public void findTileFiles(TilePackerConfig config, File directory) {
		for(File file : directory.listFiles()) {
			if(file.isDirectory()) {
				findTileFiles(config, file);
			} else if(file.getAbsolutePath().toLowerCase().endsWith("png")
					|| file.getAbsolutePath().toLowerCase().endsWith("jpg")) {
				if(!config.containsTileConfig(configFileDir, file)) {
					TileConfig tileConfig = new TileConfig();
					tileConfig.setPath(getRelativePath(configFileDir, file));
					config.getTiles().add(tileConfig);
				}
			}
		}
	}
	
	public void run(ClassLoader classLoader) throws IOException {
		tilesets.add(new Tileset());
		
		Queue<TileImage> imagesToPack = new LinkedList<TileImage>();

		for (int i = 0; i < inputFiles.size(); i++) {
			TileConfig tileConfig = inputFiles.get(i);
			
			String path = tileConfig.getPath();
			if (!path.endsWith("." + FORMAT.toLowerCase())) {
				throw new TilePackerException("ERROR: " + path + " does not match format " + FORMAT.toLowerCase());
			}
			System.out.println("INFO: Reading " + path);
			File tileFile = new File(configFileDir, path);
			if(!tileFile.exists()) {
				throw new TilePackerException("ERROR: " + tileFile.getAbsolutePath() + " does not exist");
			}
			
			if(tileConfig.isPlaced()) {
				for(int j = 0; j < tileConfig.getPlacement().size(); j++) {
					TilePlacement tilePlacement = tileConfig.getPlacement().get(j);
					TileImage spriteSheet = new TileImage(tileConfig, tileFile, tilePlacement);
					addToQueue(imagesToPack, spriteSheet);
				}
			} else {
				TileImage spriteSheet = new TileImage(tileConfig, tileFile);
				addToQueue(imagesToPack, spriteSheet);
			}
			tileConfig.getPlacement().clear();
		}
		
		while(!imagesToPack.isEmpty()) {
			TileImage nextImage = imagesToPack.poll();
			
			boolean added = false;
			
			if(nextImage.isPlaced()) {
				while(nextImage.getTileset() >= tilesets.size()) {
					tilesets.add(new Tileset());
				}
				Tileset tileset = tilesets.get(nextImage.getTileset());
				tileset.add(nextImage);
				added = true;
				
				if (tileset.isFull()) {
					System.out.println("INFO: Tileset " + nextImage.getTileset() + " is now full. Saving to disk.");
					tileset.save(new File(TARGET_DIRECTORY, nextImage.getTileset() + "." + FORMAT.toLowerCase()).getAbsolutePath(), FORMAT);
				}
			} else {
				for (int j = 0; j < tilesets.size(); j++) {
					Tileset tileset = tilesets.get(j);

					if (tileset.add(nextImage)) {
						nextImage.setTileset(j);
						added = true;
						System.out.println("INFO: Added " + nextImage.getTileConfig().getPath() + " to tileset " + j);
						
						if (tileset.isFull()) {
							System.out.println("INFO: Tileset " + j + " is now full. Saving to disk.");
							tileset.save(new File(TARGET_DIRECTORY, j + "." + FORMAT.toLowerCase()).getAbsolutePath(), FORMAT);
						}
						break;
					}
				}
			}

			if (!added) {
				Tileset tileset = new Tileset();
				if (!tileset.add(nextImage)) {
					throw new TilePackerException("ERROR: Tile image too large");
				}
				System.out.println("INFO: Added " + nextImage.getTileConfig().getPath() + " to tileset " + tilesets.size());
				tilesets.add(tileset);
				nextImage.setTileset(tilesets.size() - 1);
			}
			nextImage.storePlacementConfig();
		}
		
		for (int i = 0; i < tilesets.size(); i++) {
			Tileset tileset = tilesets.get(i);
			if(tileset.isSaved()) {
				continue;
			}
			System.out.println("INFO: Saving tileset - " + i);
			tileset.save(new File(TARGET_DIRECTORY, i + "." + FORMAT.toLowerCase()).getAbsolutePath(), FORMAT);
		}
		
		try {
			serializer.write(config, configFile);
		} catch (Exception e) {
			throw new TilePackerException("Error storing placement config", e);
		}
	}
	
	public void addToQueue(Queue<TileImage> queue, TileImage tileImage) {
		int horizontalTileCount = tileImage.getHorizontalTileCount();
		int verticalTileCount = tileImage.getVerticalTileCount();
		
		if(horizontalTileCount > Tileset.getMaximumWidthInTiles()) {
			int halfWidth = horizontalTileCount / 2;
			addToQueue(queue, TileImage.getSubImage(tileImage, 0, 0, halfWidth, verticalTileCount));
			addToQueue(queue, TileImage.getSubImage(tileImage, halfWidth, 0, horizontalTileCount - halfWidth, verticalTileCount));
		} else if(verticalTileCount > Tileset.getMaxiumumHeightInTiles()) {
			int halfHeight = verticalTileCount / 2;
			addToQueue(queue, TileImage.getSubImage(tileImage, 0, 0, horizontalTileCount, halfHeight));
			addToQueue(queue, TileImage.getSubImage(tileImage, 0, halfHeight, horizontalTileCount, verticalTileCount - halfHeight));
		} else {
			queue.add(tileImage);
		}
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			TilePackerConfig config = new TilePackerConfig();
			config.setTiles(new ArrayList<TileConfig>());
			
			Serializer serializer = new Persister();
			File file = new File("config.xml");
			try {
				serializer.write(config, file);
				System.out.println("Wrote example configuration to " + file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		try {
			if(args.length == 1) {
				new TilePacker(args[0], false).run(TilePacker.class.getClassLoader());
			} else {
				new TilePacker(args[0], true).run(TilePacker.class.getClassLoader());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getRelativePath(File configDirectory, File imageFile) {
		return configDirectory.toURI().relativize(imageFile.toURI()).getPath();
	}
}
