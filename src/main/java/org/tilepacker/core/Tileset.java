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

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.imageout.ImageOut;

/**
 * Stores a tileset
 * 
 * @author Thomas Cashman
 */
public class Tileset {
	public static int MAX_WIDTH;
	public static int MAX_HEIGHT;
	private List<Rectangle> availableRectangles;
	private List<Rectangle> usedRectangles;

	/**
	 * Constructor
	 */
	public Tileset() {
		availableRectangles = new ArrayList<Rectangle>();
		usedRectangles = new ArrayList<Rectangle>();

		availableRectangles.add(new Rectangle(0, 0, getMaximumWidthInTiles(),
				getMaxiumumHeightInTiles()));
	}

	/**
	 * Adds a tile to the tileset
	 * 
	 * @param tile
	 *            The {@link Tile} to be added
	 * @return True on success, false if the tileset is full
	 */
	public boolean add(SpriteSheet sheet) {
		for (int i = availableRectangles.size() - 1; i >= 0
				&& i < availableRectangles.size(); i--) {
			Rectangle rect = availableRectangles.get(i);
			if (rect.canContain(sheet)) {
				Rectangle copyRect = new Rectangle(rect.getX(), rect.getY(),
						rect.getWidth(), rect.getHeight());

				availableRectangles.remove(i);
				rect.addTiles(sheet);
				usedRectangles.add(rect);
				availableRectangles.add(copyRect);

				List<Rectangle> remaining = new ArrayList<Rectangle>();

				for (int j = availableRectangles.size() - 1; j >= 0; j--) {
					Rectangle otherRect = availableRectangles.get(j);
					if (rect.intersects(otherRect)) {
						Rectangle andRect = Rectangle.and(rect, otherRect);
						remaining.addAll(Rectangle.subtract(otherRect, andRect));
						availableRectangles.remove(j);
					}
				}

				for(int j = remaining.size() - 1; j >= 0; j--) {
					for(int k = 0; k < availableRectangles.size(); k++) {
						if(availableRectangles.get(k).equals(remaining.get(j))) {
							remaining.remove(j);
							break;
						}
					}
				}
				availableRectangles.addAll(remaining);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the maximum width in tiles
	 * 
	 * @return
	 */
	public static int getMaximumWidthInTiles() {
		return MAX_WIDTH / (Tile.WIDTH + (Tile.PADDING * 2));
	}

	/**
	 * Returns the maximum height in tiles
	 * 
	 * @return
	 */
	public static int getMaxiumumHeightInTiles() {
		return MAX_HEIGHT / (Tile.HEIGHT + (Tile.PADDING * 2));
	}

	/**
	 * Saves the tileset to an image
	 * 
	 * @param destinationFile
	 *            The destination filepath
	 * @param format
	 *            The file format
	 * @throws SlickException
	 */
	public void save(String destinationFile, String format)
			throws SlickException {
		Image tilesetImage = new Image(MAX_WIDTH, MAX_HEIGHT);
		Graphics g = tilesetImage.getGraphics();
		g.setColor(Color.magenta);
		g.fillRect(0, 0, MAX_WIDTH, MAX_HEIGHT);
		g.setAntiAlias(true);
		for (int i = 0; i < usedRectangles.size(); i++) {
			Rectangle rectangle = usedRectangles.get(i);

			for (int x = 0; x < rectangle.getWidth(); x++) {
				for (int y = 0; y < rectangle.getHeight(); y++) {
					int tileX = rectangle.getX() + x;
					int tileY = rectangle.getY() + y;

					Tile tile = rectangle.getTiles()[x][y];

					g.drawImage(
							tile.getTileImage(),
							((tileX * (Tile.WIDTH + (Tile.PADDING * 2))) + Tile.PADDING),
							((tileY * (Tile.HEIGHT + (Tile.PADDING * 2))) + Tile.PADDING));
				}
			}
		}
		g.flush();
		ImageOut.write(tilesetImage, format, destinationFile);
	}
}
