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
	private final TileConfig tileConfig;
	private final TilePlacement placement;
	
	private final int widthInPixels, heightInPixels;

	private BufferedImage originalImage, cutImage;

	public TileImage(TileConfig tileConfig, File imageFile) {
		this.parent = null;
		this.file = imageFile;
		this.tileConfig = tileConfig;

		loadImage();
		
		this.widthInPixels = originalImage.getWidth();
		this.heightInPixels = originalImage.getHeight();
		
		placement = new TilePlacement();
		placement.setSubImageX(0);
		placement.setSubImageY(0);
		placement.setSubImageWidth(widthInPixels / Tile.WIDTH);
		placement.setSubImageHeight(heightInPixels / Tile.HEIGHT);

		dispose();
	}
	
	public TileImage(TileConfig tileConfig, File imageFile, TilePlacement placement) {
		this.parent = null;
		this.file = imageFile;
		this.tileConfig = tileConfig;
		this.placement = placement;
		this.widthInPixels = placement.getSubImageWidth() * Tile.WIDTH;
		this.heightInPixels = placement.getSubImageHeight() * Tile.HEIGHT;
	}

	public TileImage(TileImage parent, TilePlacement placement) {
		this.parent = parent;
		this.originalImage = null;
		this.file = parent.getFile();
		this.tileConfig = parent.getTileConfig();
		this.placement = placement;
		
		this.widthInPixels = placement.getSubImageWidth() * Tile.WIDTH;
		this.heightInPixels = placement.getSubImageHeight() * Tile.HEIGHT;
	}
	
	public void storePlacementConfig() {
		tileConfig.getPlacement().add(placement);
	}

	public void loadImage() {
		if (parent == null) {
			try {
				if (originalImage == null) {
					BufferedImage image = ImageIO.read(file);
					if(image.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
						BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
						newImage.getGraphics().drawImage(image, 0, 0, null);
						image = newImage;
					}
					originalImage = image;
				}
				if (cutImage == null) {
					cutImage = originalImage;
				}
			} catch (IOException ex) {
				throw new RuntimeException("Error reading image: " + file, ex);
			}
		} else {
			parent.loadImage();
			cutImage = parent.getOriginalImage().getSubimage(placement.getSubImageX() * Tile.WIDTH, placement.getSubImageY() * Tile.HEIGHT,
					placement.getSubImageWidth() * Tile.WIDTH, placement.getSubImageHeight() * Tile.HEIGHT);
			parent.dispose();
		}
	}

	public void dispose() {
		cutImage = null;
	}
	
	public boolean isPlaced() {
		return placement.isPlaced();
	}
	
	public int getTileset() {
		return placement.getTileset();
	}
	
	public void setTileset(int tileset) {
		placement.setTileset(tileset);
	}

	public int getTilesetX() {
		return placement.getTilesetX();
	}
	
	public void setTilesetX(int tilesetX) {
		placement.setTilesetX(tilesetX);
	}

	public int getTilesetY() {
		return placement.getTilesetY();
	}
	
	public void setTilesetY(int tilesetY) {
		placement.setTilesetY(tilesetY);
	}
	
	public int getSubImageX() {
		return placement.getSubImageX();
	}

	public int getSubImageY() {
		return placement.getSubImageY();
	}
	
	public int getHorizontalTileCount() {
		return placement.getSubImageWidth();
	}

	public int getVerticalTileCount() {
		return placement.getSubImageHeight();
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
		TilePlacement placement = new TilePlacement();
		placement.setSubImageX(parent.getPlacement().getSubImageX() + x);
		placement.setSubImageY(parent.getPlacement().getSubImageY() + y);
		placement.setSubImageWidth(width);
		placement.setSubImageHeight(height);
		return new TileImage(parent, placement);
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

	public TileConfig getTileConfig() {
		return tileConfig;
	}

	public TilePlacement getPlacement() {
		return placement;
	}
}
