/**
 * Copyright 2012 www.tilepacker.org
 */
package org.tilepacker.core;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Stores the command line options for the application
 * 
 * @author Thomas Cashman
 */
public class TilePackerOptions extends Options {
	private static final long serialVersionUID = 1L;
	public static final String SOURCE_DIRECTORY = "s";
	public static final String TARGET_DIRECTORY = "t";
	public static final String FORMAT = "f";
	public static final String TILESET_WIDTH = "w";
	public static final String TILESET_HEIGHT = "h";
	public static final String TILE_WIDTH = "tw";
	public static final String TILE_HEIGHT = "th";

	/**
	 * Constructor
	 */
	public TilePackerOptions() {
		super();
		this.addOption(getRequiredOption(SOURCE_DIRECTORY, "sourceDirectory",
				true, "path to directory containing the source tiles"));
		this.addOption(getRequiredOption(TILESET_WIDTH, "width", true,
				"maximum width of the tilesets"));
		this.addOption(getRequiredOption(TILESET_HEIGHT, "height", true,
				"maximum height of the tilesets"));
		this.addOption(getRequiredOption(TILE_WIDTH, "tileWIdth", true,
				"the width of each tile"));
		this.addOption(getRequiredOption(TILE_HEIGHT, "tileHeight", true,
				"the height of each tile"));
		this.addOption(getRequiredOption(TARGET_DIRECTORY, "targetDirectory",
				true, "path to the output directory"));
		this.addOption(getRequiredOption(FORMAT, "format", true,
				"output image format - PNG, JPG or TGA"));
	}

	private Option getRequiredOption(String shortParam, String longParam,
			boolean hasArgs, String description) {
		Option option = new Option(shortParam, longParam, hasArgs, description);
		option.setRequired(true);
		return option;
	}
}
