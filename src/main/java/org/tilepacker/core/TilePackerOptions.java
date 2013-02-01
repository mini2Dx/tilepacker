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
	public static final String TILE_PADDING = "tp";

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
		this.addOption(getRequiredOption(TILE_WIDTH, "tileWidth", true,
				"the width of each tile"));
		this.addOption(getRequiredOption(TILE_HEIGHT, "tileHeight", true,
				"the height of each tile"));
		this.addOption(getOption(TILE_PADDING, "tilePadding", true,
				"padding around each tile"));
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
	
	private Option getOption(String shortParam, String longParam,
			boolean hasArgs, String description) {
		Option option = new Option(shortParam, longParam, hasArgs, description);
		option.setRequired(false);
		return option;
	}
}
