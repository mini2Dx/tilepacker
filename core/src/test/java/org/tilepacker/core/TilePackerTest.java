/**
 * Copyright (c) 2016, Thomas Cashman
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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 *
 * @author Thomas Cashman
 */
public class TilePackerTest {
	private static final int TILE_WIDTH = 32;
	private static final int TILE_HEIGHT = 32;
	private static final int TILESET_WIDTH = 256;
	private static final int TILESET_HEIGHT = 256;
	
	private Mockery mockery;
	private TileImage tileImage;
	
	private TilePackerConfig config;
	private File configFile;
	
	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		
		tileImage = mockery.mock(TileImage.class);
		
		config = new TilePackerConfig();
		config.setTilesetWidth(TILE_WIDTH);
		config.setTileHeight(TILE_HEIGHT);
		config.setTilesetWidth(TILESET_WIDTH);
		config.setTilesetHeight(TILESET_HEIGHT);
		config.setTiles(new ArrayList<String>(1));
		
		configFile = TilePackerTestUtils.createTestConfigFile(config);
		
		Assert.assertEquals(0, TILESET_WIDTH % TILE_WIDTH);
		Assert.assertEquals(0, TILESET_HEIGHT % TILE_HEIGHT);
	}
	
	@After
	public void teardown() {
		mockery.assertIsSatisfied();
		
		if(configFile != null && configFile.exists()) {
			configFile.delete();
		}
	}

	@Test
	public void testAddToQueue() {
		mockery.checking(new Expectations() {
			{
				oneOf(tileImage).getHorizontalTileCount();
				will(returnValue(1));
				oneOf(tileImage).getVerticalTileCount();
				will(returnValue(1));
			}
		});
		
		final Queue<TileImage> queue = new LinkedList<TileImage>();
		
		TilePacker tilePacker = new TilePacker(configFile);
		tilePacker.addToQueue(queue, tileImage);
		
		Assert.assertEquals(1, queue.size());
	}
	
	@Test
	public void testAddToQueueWithImageWiderThanTileset() {
		final int imageWidth = TILESET_WIDTH + (TILE_WIDTH * 2);
		final int tileCount = (TILESET_WIDTH / TILE_WIDTH) + 2;
		
		mockery.checking(new Expectations() {
			{
				exactly(1).of(tileImage).getHorizontalTileCount();
				will(returnValue(tileCount));
				exactly(2).of(tileImage).getTileX();
				will(returnValue(0));
				exactly(2).of(tileImage).getTileY();
				will(returnValue(0));
				exactly(2).of(tileImage).getFile();
				will(returnValue(null));
				exactly(2).of(tileImage).getFilepath();
				will(returnValue(""));
				oneOf(tileImage).getVerticalTileCount();
				will(returnValue(1));
			}
		});
		
		final Queue<TileImage> queue = new LinkedList<TileImage>();
		
		TilePacker tilePacker = new TilePacker(configFile);
		tilePacker.addToQueue(queue, tileImage);
		
		Assert.assertEquals(2, queue.size());
		
		TileImage subImage1 = queue.poll();
		TileImage subImage2 = queue.poll();
		
		Assert.assertEquals(TILE_HEIGHT, subImage1.getHeightInPixels());
		Assert.assertEquals(TILE_HEIGHT, subImage2.getHeightInPixels());
		
		Assert.assertEquals(imageWidth, subImage1.getWidthInPixels() + subImage2.getWidthInPixels());
		Assert.assertEquals(tileCount, subImage1.getHorizontalTileCount() + subImage2.getHorizontalTileCount());
	}
	
	@Test
	public void testAddToQueueWithUnevenImageWiderThanTileset() {
		final int imageWidth = TILESET_WIDTH + TILE_WIDTH;
		final int tileCount = (TILESET_WIDTH / TILE_WIDTH) + 1;
		
		mockery.checking(new Expectations() {
			{
				exactly(1).of(tileImage).getHorizontalTileCount();
				will(returnValue(tileCount));
				oneOf(tileImage).getVerticalTileCount();
				will(returnValue(1));
				exactly(2).of(tileImage).getTileX();
				will(returnValue(0));
				exactly(2).of(tileImage).getTileY();
				will(returnValue(0));
				exactly(2).of(tileImage).getFile();
				will(returnValue(null));
				exactly(2).of(tileImage).getFilepath();
				will(returnValue(""));
			}
		});
		
		final Queue<TileImage> queue = new LinkedList<TileImage>();
		
		TilePacker tilePacker = new TilePacker(configFile);
		tilePacker.addToQueue(queue, tileImage);
		
		Assert.assertEquals(2, queue.size());
		
		TileImage subImage1 = queue.poll();
		TileImage subImage2 = queue.poll();
		
		Assert.assertEquals(TILE_HEIGHT, subImage1.getHeightInPixels());
		Assert.assertEquals(TILE_HEIGHT, subImage2.getHeightInPixels());
		
		Assert.assertEquals(imageWidth, subImage1.getWidthInPixels() + subImage2.getWidthInPixels());
		Assert.assertEquals(tileCount, subImage1.getHorizontalTileCount() + subImage2.getHorizontalTileCount());
	}
	
	@Test
	public void testAddToQueueWithImageTallerThanTileset() {
		final int imageHeight = TILESET_HEIGHT + (TILE_HEIGHT * 2);
		final int tileCount = (TILESET_HEIGHT / TILE_HEIGHT) + 2;
		
		mockery.checking(new Expectations() {
			{
				exactly(1).of(tileImage).getHorizontalTileCount();
				will(returnValue(1));
				exactly(1).of(tileImage).getVerticalTileCount();
				will(returnValue(tileCount));
				exactly(2).of(tileImage).getTileX();
				will(returnValue(0));
				exactly(2).of(tileImage).getTileY();
				will(returnValue(0));
				exactly(2).of(tileImage).getFile();
				will(returnValue(null));
				exactly(2).of(tileImage).getFilepath();
				will(returnValue(""));
			}
		});
		
		final Queue<TileImage> queue = new LinkedList<TileImage>();
		
		TilePacker tilePacker = new TilePacker(configFile);
		tilePacker.addToQueue(queue, tileImage);
		
		Assert.assertEquals(2, queue.size());
		
		TileImage subImage1 = queue.poll();
		TileImage subImage2 = queue.poll();
		
		Assert.assertEquals(TILE_WIDTH, subImage1.getWidthInPixels());
		Assert.assertEquals(TILE_WIDTH, subImage2.getWidthInPixels());
		
		Assert.assertEquals(imageHeight, subImage1.getHeightInPixels() + subImage2.getHeightInPixels());
		Assert.assertEquals(tileCount, subImage1.getVerticalTileCount() + subImage2.getVerticalTileCount());
	}
	
	@Test
	public void testAddToQueueWithUnevenImageTallerThanTileset() {
		final int imageHeight = TILESET_HEIGHT + TILE_HEIGHT;
		final int tileCount = (TILESET_HEIGHT / TILE_HEIGHT) + 1;
		
		mockery.checking(new Expectations() {
			{
				exactly(1).of(tileImage).getHorizontalTileCount();
				will(returnValue(1));
				exactly(1).of(tileImage).getVerticalTileCount();
				will(returnValue(tileCount));
				exactly(2).of(tileImage).getTileX();
				will(returnValue(0));
				exactly(2).of(tileImage).getTileY();
				will(returnValue(0));
				exactly(2).of(tileImage).getFile();
				will(returnValue(null));
				exactly(2).of(tileImage).getFilepath();
				will(returnValue(""));
			}
		});
		
		final Queue<TileImage> queue = new LinkedList<TileImage>();
		
		TilePacker tilePacker = new TilePacker(configFile);
		tilePacker.addToQueue(queue, tileImage);
		
		Assert.assertEquals(2, queue.size());
		
		TileImage subImage1 = queue.poll();
		TileImage subImage2 = queue.poll();
		
		Assert.assertEquals(TILE_WIDTH, subImage1.getWidthInPixels());
		Assert.assertEquals(TILE_WIDTH, subImage2.getWidthInPixels());
		
		Assert.assertEquals(imageHeight, subImage1.getHeightInPixels() + subImage2.getHeightInPixels());
		Assert.assertEquals(tileCount, subImage1.getVerticalTileCount() + subImage2.getVerticalTileCount());
	}
}
