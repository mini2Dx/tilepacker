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

import org.junit.Test;

import junit.framework.Assert;

/**
 *
 */
public class TileImageTest {
	
	@Test
	public void testSize() {
		
	}

	@Test
	public void testGetSubImage() {
		TileConfig tileConfig = new TileConfig();
		TilePlacement tilePlacement = new TilePlacement();
		tilePlacement.setSubImageWidth(4);
		tilePlacement.setSubImageHeight(3);
		tileConfig.getPlacement().add(tilePlacement);
		
		TileImage parentImage = new TileImage(tileConfig, null, tilePlacement);
		
		TileImage subImage1  = parentImage.getSubImage(0, 0, 2, 3);
		Assert.assertEquals(0, subImage1.getSubImageX());
		Assert.assertEquals(0, subImage1.getSubImageY());
		Assert.assertEquals(2, subImage1.getHorizontalTileCount());
		Assert.assertEquals(3, subImage1.getVerticalTileCount());
		
		TileImage subImage2  = parentImage.getSubImage(1, 2, 3, 2);
		Assert.assertEquals(1, subImage2.getSubImageX());
		Assert.assertEquals(2, subImage2.getSubImageY());
		Assert.assertEquals(3, subImage2.getHorizontalTileCount());
		Assert.assertEquals(2, subImage2.getVerticalTileCount());
	}
}
