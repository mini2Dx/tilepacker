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

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root
public class TilePackerConfig {
	@Element
	private int tileWidth = 32;
	@Element
	private int tileHeight = 32;
	@Element
	private int tilesetWidth = 512;
	@Element
	private int tilesetHeight = 512;
	@Element(required=false)
	private int tilePadding = 0;
	@Element
	private String outputFormat = "PNG";
	@Element
	private String outputPath = "./";
	@Element(required=false)
	private boolean preventTearing = false;
	@Element(required=false)
	private boolean premultiplyAlpha = false;
	@Element(required=false)
	private String backgroundColor = null;
	@ElementList(name="tiles")
	private List<String> tiles;
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
	
	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}
	
	public int getTilesetWidth() {
		return tilesetWidth;
	}
	
	public void setTilesetWidth(int tilesetWidth) {
		this.tilesetWidth = tilesetWidth;
	}
	
	public int getTilesetHeight() {
		return tilesetHeight;
	}
	
	public void setTilesetHeight(int tilesetHeight) {
		this.tilesetHeight = tilesetHeight;
	}
	
	public int getTilePadding() {
		return tilePadding;
	}
	
	public void setTilePadding(int tilePadding) {
		this.tilePadding = tilePadding;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	
	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public boolean isPreventTearing() {
		return preventTearing;
	}
	
	public void setPreventTearing(boolean preventTearing) {
		this.preventTearing = preventTearing;
	}

	public boolean isPremultiplyAlpha() {
		return premultiplyAlpha;
	}

	public void setPremultiplyAlpha(boolean premultiplyAlpha) {
		this.premultiplyAlpha = premultiplyAlpha;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public List<String> getTiles() {
		return tiles;
	}

	public void setTiles(List<String> tiles) {
		this.tiles = tiles;
	}
}
