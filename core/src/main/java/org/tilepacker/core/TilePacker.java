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
import java.util.List;

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

	private final String configFileDir;
	private final List<String> inputFiles;
	private List<Tileset> tilesets;

	/**
	 * Constructor
	 */
	public TilePacker(File configFile) {
		super();
		this.configFileDir = configFile.getParent();
		tilesets = new ArrayList<Tileset>();

		Serializer serializer = new Persister();
		TilePackerConfig config;
		try {
			config = serializer.read(TilePackerConfig.class, configFile);
		} catch (Exception e) {
			throw new TilePackerException("Error reading config file", e);
		}

		Tile.WIDTH = config.getTileWidth();
		Tile.HEIGHT = config.getTileHeight();
		Tile.PADDING = config.getTilePadding();

		Tileset.MAX_WIDTH = config.getTilesetWidth();
		Tileset.MAX_HEIGHT = config.getTilesetHeight();

		TilePacker.FIX_TEARING = config.isPreventTearing();
		if(TilePacker.FIX_TEARING && Tile.PADDING < 1) {
			throw new TilePackerException("ERROR: If tearing prevention is enabled, the tile padding must be greater than 0");
		}
		TilePacker.FORMAT = config.getOutputFormat();
		TilePacker.TARGET_DIRECTORY = new File(configFile.getParent(), config.getOutputPath());

		inputFiles = config.getTiles();
	}
	
	public void run(ClassLoader classLoader) throws IOException {
		tilesets.add(new Tileset());

		for (int i = 0; i < inputFiles.size(); i++) {
			String path = inputFiles.get(i);
			if (!path.endsWith("." + FORMAT.toLowerCase())) {
				throw new TilePackerException("ERROR: " + path + " does not match format " + FORMAT.toLowerCase());
			}
			System.out.println("INFO: Reading " + path);
			File tileFile = new File(configFileDir, path);
			if(!tileFile.exists()) {
				throw new TilePackerException("ERROR: " + path + " does not exist");
			}
			TileImage spriteSheet = new TileImage(tileFile, Tile.WIDTH, Tile.HEIGHT);

			boolean added = false;
			for (int j = 0; j < tilesets.size(); j++) {
				Tileset tileset = tilesets.get(j);

				if (tileset.add(spriteSheet)) {
					added = true;
					System.out.println("INFO: Added " + path + " to tileset " + j);
					
					if (tileset.isFull()) {
						System.out.println("INFO: Tileset " + j + " is now full. Saving to disk.");
						tileset.save(new File(TARGET_DIRECTORY, j + "." + FORMAT.toLowerCase()).getAbsolutePath(), FORMAT);
					}
					break;
				}
			}

			if (!added) {
				Tileset tileset = new Tileset();
				if (!tileset.add(spriteSheet)) {
					throw new TilePackerException("ERROR: Image too large - " + path);
				}
				System.out.println("INFO: Added " + path + " to tileset " + tilesets.size());
				tilesets.add(tileset);
			}
		}
		
		for (int i = 0; i < tilesets.size(); i++) {
			Tileset tileset = tilesets.get(i);
			if(tileset.isSaved()) {
				continue;
			}
			System.out.println("INFO: Saving tileset - " + i);
			tileset.save(new File(TARGET_DIRECTORY, i + "." + FORMAT.toLowerCase()).getAbsolutePath(), FORMAT);
		}
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			System.err.println("Usage: java -jar tilepacker-core-standalone.jar ./path/to/config.xml");
			System.exit(0);
		}
		try {
			new TilePacker(new File(args[0])).run(TilePacker.class.getClassLoader());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}