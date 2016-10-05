/**
 * Copyright 2016 Thomas Cashman
 */
package org.tilepacker.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *
 * @author Thomas Cashman
 */
public class TileImage {
	private final TileImage parent;
	private final File file;

	private final int tileX, tileY, tileWidth, tileHeight;
	private final int horizontalTileCount, verticalTileCount;
	private final int widthInPixels, heightInPixels;

	private BufferedImage image;

	public TileImage(File imageFile, int tileWidth, int tileHeight) {
		this.parent = null;
		this.file = imageFile;

		loadImage();

		this.tileX = 0;
		this.tileY = 0;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.widthInPixels = image.getWidth();
		this.heightInPixels = image.getHeight();
		this.horizontalTileCount = widthInPixels / tileWidth;
		this.verticalTileCount = heightInPixels / tileHeight;

		dispose();
	}

	public TileImage(TileImage parent, int tileX, int tileY, int horizontalTiles, int verticalTiles) {
		this.parent = parent;
		this.image = null;
		this.file = null;

		this.tileX = tileX;
		this.tileY = tileY;
		this.tileWidth = parent.tileWidth;
		this.tileHeight = parent.tileHeight;
		this.horizontalTileCount = horizontalTiles;
		this.verticalTileCount = verticalTiles;
		this.widthInPixels = parent.getTileWidthInPixels();
		this.heightInPixels = parent.getTileHeightInPixels();
	}

	public void loadImage() {
		if (parent == null) {
			try {
				if (image == null) {
					image = ImageIO.read(file);
				}
			} catch (IOException ex) {
				throw new RuntimeException("Error reading image: " + file, ex);
			}
		} else {
			parent.loadImage();
			image = parent.getBackingImage().getSubimage(tileX * tileWidth, tileY * tileHeight,
					horizontalTileCount * tileWidth, verticalTileCount * tileHeight);
			parent.dispose();
		}
	}

	public void dispose() {
		image = null;
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public int getHorizontalTileCount() {
		return horizontalTileCount;
	}

	public int getVerticalTileCount() {
		return verticalTileCount;
	}

	public int getTileWidthInPixels() {
		return widthInPixels;
	}

	public int getTileHeightInPixels() {
		return heightInPixels;
	}

	public TileImage getSubImage(int x, int y) {
		return new TileImage(this, tileX + x, tileY + y, 1, 1);
	}

	public BufferedImage getBackingImage() {
		return image;
	}
}
