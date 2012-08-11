/**
 * Copyright 2012 www.tilepacker.org
 */
package org.tilepacker.core;

import org.newdawn.slick.Image;

/**
 * Stores a single tile
 * 
 * @author Thomas Cashman
 */
public class Tile {
	public static int WIDTH = 32;
	public static int HEIGHT = 32;
	
	private Image tileImage;
	
	/**
	 * Constructor
	 * @param tileImage The tile's {@link Image} instance
	 */
	public Tile(Image tileImage) {
		this.tileImage = tileImage;
	}

	/**
	 * The tile image
	 * @return
	 */
	public Image getTileImage() {
		return tileImage;
	}
}
