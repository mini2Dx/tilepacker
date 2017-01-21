/**
 * Copyright (c) 2017, Thomas Cashman
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

import org.simpleframework.xml.Element;

/**
 *
 */
public class TilePlacement implements Comparable<TilePlacement> {
	@Element(required=false)
	private int tileset;
	@Element(required=false)
	private int tilesetX = -1;
	@Element(required=false)
	private int tilesetY = -1;
	@Element(required=false)
	private int subImageX;
	@Element(required=false)
	private int subImageY;
	@Element(required=false)
	private int subImageWidth;
	@Element(required=false)
	private int subImageHeight;
	
	public boolean isPlaced() {
		if(tilesetX < 0) {
			return false;
		}
		if(tilesetY < 0) {
			return false;
		}
		return true;
	}
	
	public int getArea() {
		return subImageWidth * subImageHeight;
	}
	
	public int getTileset() {
		return tileset;
	}
	
	public void setTileset(int tileset) {
		this.tileset = tileset;
	}
	
	public int getTilesetX() {
		return tilesetX;
	}
	
	public void setTilesetX(int tilesetX) {
		this.tilesetX = tilesetX;
	}
	
	public int getTilesetY() {
		return tilesetY;
	}
	
	public void setTilesetY(int tilesetY) {
		this.tilesetY = tilesetY;
	}
	
	public int getSubImageX() {
		return subImageX;
	}
	
	public void setSubImageX(int imageX) {
		this.subImageX = imageX;
	}
	
	public int getSubImageY() {
		return subImageY;
	}
	
	public void setSubImageY(int imageY) {
		this.subImageY = imageY;
	}
	
	public int getSubImageWidth() {
		return subImageWidth;
	}
	
	public void setSubImageWidth(int imageWidth) {
		this.subImageWidth = imageWidth;
	}
	
	public int getSubImageHeight() {
		return subImageHeight;
	}
	
	public void setSubImageHeight(int imageHeight) {
		this.subImageHeight = imageHeight;
	}

	@Override
	public int compareTo(TilePlacement o) {
		if(isPlaced() && !o.isPlaced()) {
			return -1;
		}
		if(!isPlaced() && o.isPlaced()) {
			return 1;
		}
		
		if(isPlaced()) {
			int xComparison = Integer.compare(getTilesetX(), o.getTilesetX());
			if(xComparison == 0) {
				return Integer.compare(getTilesetY(), o.getTilesetY());
			}
			return xComparison;
		} else {
			int sizeComparison = Integer.compare(getArea(), o.getArea());
			if(sizeComparison == 0) {
				int xComparison = Integer.compare(getTilesetX(), o.getTilesetX());
				if(xComparison == 0) {
					return Integer.compare(getTilesetY(), o.getTilesetY());
				}
				return xComparison;
			}
			return sizeComparison;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + subImageHeight;
		result = prime * result + subImageWidth;
		result = prime * result + subImageX;
		result = prime * result + subImageY;
		result = prime * result + tileset;
		result = prime * result + tilesetX;
		result = prime * result + tilesetY;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TilePlacement other = (TilePlacement) obj;
		if (subImageHeight != other.subImageHeight)
			return false;
		if (subImageWidth != other.subImageWidth)
			return false;
		if (subImageX != other.subImageX)
			return false;
		if (subImageY != other.subImageY)
			return false;
		if (tileset != other.tileset)
			return false;
		if (tilesetX != other.tilesetX)
			return false;
		if (tilesetY != other.tilesetY)
			return false;
		return true;
	}
}
