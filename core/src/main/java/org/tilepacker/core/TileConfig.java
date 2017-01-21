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

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 *
 */
public class TileConfig implements Comparable<TileConfig> {
	@Element
	private String path;
	@ElementList(name="placement", required=false)
	private List<TilePlacement> placement;
	
	public boolean isPlaced() {
		if(placement == null || placement.isEmpty()) {
			return false;
		}
		for(TilePlacement config : placement) {
			if(!config.isPlaced()) {
				return false;
			}
		}
		return true;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<TilePlacement> getPlacement() {
		if(placement == null) {
			placement = new ArrayList<TilePlacement>(1);
		}
		return placement;
	}

	public void setPlacement(List<TilePlacement> placement) {
		this.placement = placement;
	}

	@Override
	public int compareTo(TileConfig o) {
		if(isPlaced() && !o.isPlaced()) {
			return -1;
		}
		if(!isPlaced() && o.isPlaced()) {
			return 1;
		}
		return path.compareTo(o.path);
	}
}
