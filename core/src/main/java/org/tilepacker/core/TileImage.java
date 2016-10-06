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
	private final String filepath;

	private final int tileX, tileY;
	private final int horizontalTileCount, verticalTileCount;
	private final int widthInPixels, heightInPixels;

	private BufferedImage originalImage, cutImage;

	public TileImage(File imageFile, String path) {
		this.parent = null;
		this.file = imageFile;
		this.filepath = path;

		loadImage();

		this.tileX = 0;
		this.tileY = 0;
		this.widthInPixels = originalImage.getWidth();
		this.heightInPixels = originalImage.getHeight();
		this.horizontalTileCount = widthInPixels / Tile.WIDTH;
		this.verticalTileCount = heightInPixels / Tile.HEIGHT;

		dispose();
	}

	public TileImage(TileImage parent, int tileX, int tileY, int horizontalTiles, int verticalTiles) {
		this.parent = parent;
		this.originalImage = null;
		this.file = parent.getFile();
		
		String parentFilepath = parent.getFilepath();
		if(parentFilepath.contains("split")) {
			this.filepath = parentFilepath;
		} else {
			this.filepath = parentFilepath+ " (split)";
		}

		this.tileX = tileX;
		this.tileY = tileY;
		this.horizontalTileCount = horizontalTiles;
		this.verticalTileCount = verticalTiles;
		this.widthInPixels = horizontalTiles * Tile.WIDTH;
		this.heightInPixels = verticalTiles * Tile.HEIGHT;
	}

	public void loadImage() {
		if (parent == null) {
			try {
				if (originalImage == null) {
					originalImage = ImageIO.read(file);
				}
				if (cutImage == null) {
					cutImage = originalImage;
				}
			} catch (IOException ex) {
				throw new RuntimeException("Error reading image: " + file, ex);
			}
		} else {
			parent.loadImage();
			cutImage = parent.getOriginalImage().getSubimage(tileX * Tile.WIDTH, tileY * Tile.HEIGHT,
					horizontalTileCount * Tile.WIDTH, verticalTileCount * Tile.HEIGHT);
			parent.dispose();
		}
	}

	public void dispose() {
		cutImage = null;
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

	public int getWidthInPixels() {
		return widthInPixels;
	}

	public int getHeightInPixels() {
		return heightInPixels;
	}

	public TileImage getSubImage(int x, int y) {
		return getSubImage(x, y, 1, 1);
	}
	
	public TileImage getSubImage(int x, int y, int width, int height) {
		return getSubImage(this, x, y, width, height);
	}
	
	public static TileImage getSubImage(TileImage parent, int x, int y, int width, int height) {
		return new TileImage(parent, parent.getTileX() + x, parent.getTileY() + y, width, height);
	}

	public BufferedImage getOriginalImage() {
		if(parent == null) {
			return originalImage;
		}
		return parent.getOriginalImage();
	}
	
	public BufferedImage getCutImage() {
		return cutImage;
	}
	
	public File getFile() {
		return file;
	}

	public String getFilepath() {
		return filepath;
	}
}
