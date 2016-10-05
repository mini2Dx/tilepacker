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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Stores a tileset
 * 
 * @author Thomas Cashman
 */
public class Tileset {
	public static int MAX_WIDTH;
	public static int MAX_HEIGHT;
	public static boolean PREMULTIPLY_ALPHA = false;
	public static String BACKGROUND_COLOR = null;

	private final SmallToLargeRectangleComparator rectangleComparator = new SmallToLargeRectangleComparator();
	private final List<Rectangle> tmpRemaining = new ArrayList<Rectangle>(1);

	private List<Rectangle> availableRectangles;
	private List<Rectangle> usedRectangles;
	private boolean saved = false;

	/**
	 * Constructor
	 */
	public Tileset() {
		availableRectangles = new ArrayList<Rectangle>(1);
		usedRectangles = new ArrayList<Rectangle>(1);

		availableRectangles.add(new Rectangle(0, 0, getMaximumWidthInTiles(), getMaxiumumHeightInTiles()));
	}

	/**
	 * Adds a tile to the tileset
	 * 
	 * @param image
	 *            The {@link TileImage} to be added
	 * @return True on success, false if the tileset is full
	 */
	public boolean add(TileImage image) {
		if (saved) {
			return false;
		}
		for (int i = 0; i < availableRectangles.size(); i++) {
			Rectangle rect = availableRectangles.get(i);
			if (!rect.canContain(image)) {
				continue;
			}
			Rectangle copyRect = new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

			availableRectangles.remove(i);
			rect.addTiles(image);
			usedRectangles.add(rect);
			availableRectangles.add(copyRect);

			for (int j = 0; j < availableRectangles.size(); j++) {
				Rectangle otherRect = availableRectangles.get(j);
				if (!rect.intersects(otherRect)) {
					continue;
				}
				Rectangle andRect = Rectangle.and(rect, otherRect);
				Rectangle.subtract(tmpRemaining, otherRect, andRect);
				availableRectangles.remove(j);
				j--;
			}

			for (int j = tmpRemaining.size() - 1; j >= 0; j--) {
				for (int k = 0; k < availableRectangles.size(); k++) {
					if (availableRectangles.get(k).equals(tmpRemaining.get(j))) {
						tmpRemaining.remove(j);
						break;
					}
				}
			}
			availableRectangles.addAll(tmpRemaining);
			Collections.sort(availableRectangles, rectangleComparator);
			tmpRemaining.clear();
			return true;
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
	 * Returns if no more sprites can be placed in this tileset
	 * 
	 * @return
	 */
	public boolean isFull() {
		return availableRectangles.size() == 0;
	}

	/**
	 * Returns if this tileset has been saved to disk
	 * 
	 * @return
	 */
	public boolean isSaved() {
		return saved;
	}

	/**
	 * Saves the tileset to an image
	 * 
	 * @param destinationFile
	 *            The destination filepath
	 * @param format
	 *            The file format
	 * @throws IOException 
	 */
	public void save(String destinationFile, String format) throws IOException {
		if(saved) {
			return;
		}
		BufferedImage canvas = new BufferedImage(Tileset.MAX_WIDTH, Tileset.MAX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		if(BACKGROUND_COLOR != null) {
			String [] colorComponents = BACKGROUND_COLOR.split(",");
			if(colorComponents.length != 3) {
				throw new TilePackerException("Background color must be in format R,G,B");
			}
			Color color = new Color(Integer.parseInt(colorComponents[0]), 
					Integer.parseInt(colorComponents[1]), 
					Integer.parseInt(colorComponents[2]));
			drawToImage(canvas, 0, 0, canvas.getWidth(), canvas.getHeight(), color.getRGB());
		}
		
		for (int i = 0; i < usedRectangles.size(); i++) {
			Rectangle rectangle = usedRectangles.get(i);

			for (int x = 0; x < rectangle.getWidth(); x++) {
				for (int y = 0; y < rectangle.getHeight(); y++) {
					int tileX = rectangle.getX() + x;
					int tileY = rectangle.getY() + y;

					Tile tile = rectangle.getTiles()[x][y];
					TileImage image = tile.getTileImage();
					image.loadImage();

					int renderX = ((tileX * (Tile.WIDTH + (Tile.PADDING * 2))) + Tile.PADDING);
					int renderY = ((tileY * (Tile.HEIGHT + (Tile.PADDING * 2))) + Tile.PADDING);

					if (TilePacker.FIX_TEARING) {
						//Left
						drawToImage(image.getBackingImage(), 0, 0, 1, Tile.HEIGHT, canvas, renderX - 1, renderY);
						//Right
						drawToImage(image.getBackingImage(), Tile.WIDTH - 1, 0, 1, Tile.HEIGHT, canvas, renderX + Tile.WIDTH, renderY);
						//Top
						drawToImage(image.getBackingImage(), 0, 0, Tile.WIDTH, 1, canvas, renderX, renderY - 1);
						//Bottom
						drawToImage(image.getBackingImage(), 0, Tile.HEIGHT - 1, Tile.WIDTH, 1, canvas, renderX, renderY + Tile.HEIGHT);
					}
					drawToImage(image.getBackingImage(), 0, 0, Tile.WIDTH, Tile.HEIGHT, canvas, renderX, renderY);
					image.dispose();
				}
			}
		}
		
		switch(format.toLowerCase()) {
		case "png":
			if(PREMULTIPLY_ALPHA) {
				canvas.getColorModel().coerceData(canvas.getRaster(), true);
			}
			ImageIO.write(canvas, "png", new File(destinationFile));
			break;
		case "jpg":
		case "jpeg":
			break;
		}
		
		for (int i = 0; i < usedRectangles.size(); i++) {
			Rectangle rectangle = usedRectangles.get(i);
			rectangle.dispose();
		}
		saved = true;
	}

	private void drawToImage(BufferedImage source, int sourceX, int sourceY, int sourceWidth, int sourceHeight,
			BufferedImage destination, int destinationX, int destinationY) {
		for(int x = sourceX; x < sourceWidth; x++) {
			if(x < 0) {
				continue;
			}
			if(x >= source.getWidth()) {
				continue;
			}
			int destX = destinationX + (x - sourceX);
			if(destX >= destination.getWidth()) {
				continue;
			}
			
			for(int y = sourceY; y < sourceHeight; y++) {
				if(y < 0) {
					continue;
				}
				if(y >= source.getHeight()) {
					continue;
				}
				
				int destY = destinationY + (y - sourceY);
				if(destY >= destination.getHeight()) {
					continue;
				}
				destination.setRGB(destX, destY, source.getRGB(x, y));
			}
		}
	}
	
	private void drawToImage(BufferedImage destination, int destinationX, int destinationY, int destinationWidth, int destinationHeight, int rgb) {
		for(int x = destinationX; x < destinationWidth; x++) {
			if(x < 0) {
				continue;
			}
			if(x >= destination.getWidth()) {
				continue;
			}
			
			for(int y = destinationY; y < destinationHeight; y++) {
				if(y < 0) {
					continue;
				}
				if(y >= destination.getHeight()) {
					continue;
				}
				destination.setRGB(x, y, rgb);
			}
		}
	}
}
