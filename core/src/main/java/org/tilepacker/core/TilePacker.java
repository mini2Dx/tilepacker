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
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Main entry class
 * 
 * @author Thomas Cashman
 */
public class TilePacker extends BasicGame {
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
		super("TilePacker");
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
	
	public void run(ClassLoader classLoader) {
		AppGameContainer gc;
		try {
			NativeLoader.loadNatives(classLoader);
			gc = new AppGameContainer(this, Tileset.MAX_WIDTH, Tileset.MAX_HEIGHT, false);
			gc.setForceExit(false);
			gc.start();
		} catch (SlickException e) {
		}
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		tilesets.add(new Tileset());

		for (int i = 0; i < inputFiles.size(); i++) {
			String path = inputFiles.get(i);
			if (path.endsWith("." + FORMAT.toLowerCase())) {
				System.out.println("INFO: Reading " + path);
				SpriteSheet spriteSheet = new SpriteSheet(new File(configFileDir, path).getAbsolutePath(), Tile.WIDTH,
						Tile.HEIGHT);

				boolean added = false;
				for (int j = 0; j < tilesets.size(); j++) {
					Tileset tileset = tilesets.get(j);

					if (tileset.add(spriteSheet)) {
						added = true;
						break;
					}
				}

				if (!added) {
					Tileset tileset = new Tileset();
					if (!tileset.add(spriteSheet)) {
						throw new TilePackerException("ERROR: Image too large - " + path);
					}
					tilesets.add(tileset);
				}
			}
		}
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		for (int i = 0; i < tilesets.size(); i++) {
			System.out.println("INFO: Saving tileset - " + i);
			tilesets.get(i).save(new File(TARGET_DIRECTORY, i + "." + FORMAT.toLowerCase()).getAbsolutePath(), FORMAT);
		}
		gc.exit();
	}

	public static void main(String[] args) {
		if(args.length == 0) {
			System.err.println("Usage: java -jar tilepacker-core-standalone.jar ./path/to/config.xml");
			System.exit(0);
		}
		new TilePacker(new File(args[0])).run(TilePacker.class.getClassLoader());
	}
}
