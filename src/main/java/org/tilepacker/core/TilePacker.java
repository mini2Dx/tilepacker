/**
 * Copyright (c) 2013, Thomas Cashman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the TilePacker nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.tilepacker.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * Main entry class
 * @author Thomas Cashman
 */
public class TilePacker extends BasicGame {
	public static String FORMAT = "PNG";
	public static String SOURCE_DIRECTORY = "";
	public static String TARGET_DIRECTORY = "";

	private File[] inputFiles;
	private List<Tileset> tilesets;

	/**
	 * Constructor
	 */
	public TilePacker() {
		super("TilePacker");
		tilesets = new ArrayList<Tileset>();
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		File sourceDirectory = new File(SOURCE_DIRECTORY);
		inputFiles = sourceDirectory.listFiles();
		Tileset tileset = new Tileset();
		for(int i = 0; i < inputFiles.length; i++) {
			if(inputFiles[i].getAbsolutePath().endsWith("." + FORMAT.toLowerCase())) {
				System.out.println("INFO: Reading " + inputFiles[i].getAbsolutePath());
				SpriteSheet spriteSheet = new SpriteSheet(inputFiles[i].getAbsolutePath(), Tile.WIDTH, Tile.HEIGHT);
				for(int x = 0; x < spriteSheet.getHorizontalCount(); x++) {
					for(int y = 0; y < spriteSheet.getVerticalCount(); y++) {
						Tile tile = new Tile(spriteSheet.getSubImage(x, y));
						if(!tileset.add(tile)) {
							tilesets.add(tileset);
							tileset = new Tileset();
							tileset.add(tile);
						}
					}
				}
			}
		}
		tilesets.add(tileset);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		for(int i = 0; i < tilesets.size(); i++) {
			System.out.println("INFO: Saving tileset - " + i);
			tilesets.get(i).save(TARGET_DIRECTORY + i + "." + FORMAT.toLowerCase(), FORMAT);
		}
		System.exit(0);
	}

	public static void main(String[] args) {
		CommandLineParser parser = new GnuParser();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(new TilePackerOptions(), args);
		} catch (ParseException exp) {
			System.err.println("ERROR: " + exp.getMessage());
			return;
		}

		Tile.WIDTH = Integer.parseInt(commandLine
				.getOptionValue(TilePackerOptions.TILE_WIDTH));
		Tile.HEIGHT = Integer.parseInt(commandLine
				.getOptionValue(TilePackerOptions.TILE_HEIGHT));
		
		String padding = commandLine
				.getOptionValue(TilePackerOptions.TILE_PADDING);
		if(padding != null && padding.length() > 0) {
			Tile.PADDING = Integer.parseInt(padding);
		} else {
			Tile.PADDING = 0;
		}
		

		Tileset.MAX_WIDTH = Integer.parseInt(commandLine
				.getOptionValue(TilePackerOptions.TILESET_WIDTH));
		Tileset.MAX_HEIGHT = Integer.parseInt(commandLine
				.getOptionValue(TilePackerOptions.TILESET_HEIGHT));
		TilePacker.FORMAT = commandLine
				.getOptionValue(TilePackerOptions.FORMAT);
		TilePacker.SOURCE_DIRECTORY = commandLine
				.getOptionValue(TilePackerOptions.SOURCE_DIRECTORY);
		TilePacker.TARGET_DIRECTORY = commandLine
				.getOptionValue(TilePackerOptions.TARGET_DIRECTORY);

		try {
			AppGameContainer gc = new AppGameContainer(new TilePacker(),
					Tileset.MAX_WIDTH, Tileset.MAX_HEIGHT, false);
			gc.start();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
