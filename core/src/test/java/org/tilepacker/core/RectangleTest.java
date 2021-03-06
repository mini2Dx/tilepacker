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

import org.junit.Test;

import junit.framework.Assert;

/**
 * Unit tests for rectangles
 * 
 * @author Thomas Cashman
 */
public class RectangleTest {
	private Rectangle rectangle1;
	private Rectangle rectangle2;

	@Test
	public void testNotIntersecting() {
		rectangle1 = new Rectangle(0, 0, 2, 2);
		rectangle2 = new Rectangle(2, 2, 1, 1);

		Assert.assertEquals(false, rectangle1.intersects(rectangle2));
		Assert.assertEquals(false, rectangle2.intersects(rectangle1));
		Assert.assertNull(Rectangle.and(rectangle1, rectangle2));
		Assert.assertNull(Rectangle.and(rectangle2, rectangle1));
	}

	@Test
	public void testIntersectingVertically() {
		rectangle1 = new Rectangle(0, 0, 3, 3);
		rectangle2 = new Rectangle(0, 2, 3, 3);

		Assert.assertEquals(true, rectangle1.intersects(rectangle2));
		Assert.assertEquals(true, rectangle2.intersects(rectangle1));

		Rectangle andRect1 = Rectangle.and(rectangle1, rectangle2);
		Assert.assertEquals(0, andRect1.getX());
		Assert.assertEquals(2, andRect1.getY());
		Assert.assertEquals(3, andRect1.getWidth());
		Assert.assertEquals(1, andRect1.getHeight());

		Rectangle andRect2 = Rectangle.and(rectangle2, rectangle1);
		Assert.assertEquals(true, andRect1.equals(andRect2));
	}

	@Test
	public void testIntersectingHorizontally() {
		rectangle1 = new Rectangle(0, 0, 3, 3);
		rectangle2 = new Rectangle(2, 0, 3, 3);

		Assert.assertEquals(true, rectangle1.intersects(rectangle2));
		Assert.assertEquals(true, rectangle2.intersects(rectangle1));

		Rectangle andRect1 = Rectangle.and(rectangle1, rectangle2);
		Assert.assertEquals(2, andRect1.getX());
		Assert.assertEquals(0, andRect1.getY());
		Assert.assertEquals(1, andRect1.getWidth());
		Assert.assertEquals(3, andRect1.getHeight());

		Rectangle andRect2 = Rectangle.and(rectangle2, rectangle1);
		Assert.assertEquals(true, andRect1.equals(andRect2));
	}

	@Test
	public void testIntersectingTopLeftToBottomRight() {
		rectangle1 = new Rectangle(0, 0, 3, 3);
		rectangle2 = new Rectangle(2, 2, 3, 3);

		Assert.assertEquals(true, rectangle1.intersects(rectangle2));
		Assert.assertEquals(true, rectangle2.intersects(rectangle1));

		Rectangle andRect1 = Rectangle.and(rectangle1, rectangle2);
		Assert.assertEquals(2, andRect1.getX());
		Assert.assertEquals(2, andRect1.getY());
		Assert.assertEquals(1, andRect1.getWidth());
		Assert.assertEquals(1, andRect1.getHeight());

		Rectangle andRect2 = Rectangle.and(rectangle2, rectangle1);
		Assert.assertEquals(true, andRect1.equals(andRect2));
	}

	@Test
	public void testIntersectingTopRightToBottomLeft() {
		rectangle1 = new Rectangle(0, 2, 3, 3);
		rectangle2 = new Rectangle(2, 0, 3, 3);

		Assert.assertEquals(true, rectangle1.intersects(rectangle2));
		Assert.assertEquals(true, rectangle2.intersects(rectangle1));

		Rectangle andRect1 = Rectangle.and(rectangle1, rectangle2);
		Assert.assertEquals(2, andRect1.getX());
		Assert.assertEquals(2, andRect1.getY());
		Assert.assertEquals(1, andRect1.getWidth());
		Assert.assertEquals(1, andRect1.getHeight());

		Rectangle andRect2 = Rectangle.and(rectangle2, rectangle1);
		Assert.assertEquals(true, andRect1.equals(andRect2));
	}

	@Test
	public void testSubtractLeft() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(0, 0, 1, 4);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(1, remaining.size());

		Rectangle diffRect = remaining.get(0);
		Assert.assertEquals(1, diffRect.getX());
		Assert.assertEquals(0, diffRect.getY());
		Assert.assertEquals(3, diffRect.getWidth());
		Assert.assertEquals(4, diffRect.getHeight());
	}

	@Test
	public void testSubtractRight() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(3, 0, 1, 4);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(1, remaining.size());

		Rectangle diffRect = remaining.get(0);
		Assert.assertEquals(0, diffRect.getX());
		Assert.assertEquals(0, diffRect.getY());
		Assert.assertEquals(3, diffRect.getWidth());
		Assert.assertEquals(4, diffRect.getHeight());
	}

	@Test
	public void testSubtractTop() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(0, 0, 4, 1);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(1, remaining.size());

		Rectangle diffRect = remaining.get(0);
		Assert.assertEquals(0, diffRect.getX());
		Assert.assertEquals(1, diffRect.getY());
		Assert.assertEquals(4, diffRect.getWidth());
		Assert.assertEquals(3, diffRect.getHeight());
	}

	@Test
	public void testSubtractBottom() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(0, 3, 4, 1);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(1, remaining.size());

		Rectangle diffRect = remaining.get(0);
		Assert.assertEquals(0, diffRect.getX());
		Assert.assertEquals(0, diffRect.getY());
		Assert.assertEquals(4, diffRect.getWidth());
		Assert.assertEquals(3, diffRect.getHeight());
	}

	@Test
	public void testSubtractTopLeft() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(0, 0, 1, 1);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(2, remaining.size());

		for (int i = 0; i < remaining.size(); i++) {
			Rectangle diffRect = remaining.get(0);
			if (diffRect.getX() == 1) {
				Assert.assertEquals(1, diffRect.getX());
				Assert.assertEquals(0, diffRect.getY());
				Assert.assertEquals(3, diffRect.getWidth());
				Assert.assertEquals(4, diffRect.getHeight());
			} else {
				Assert.assertEquals(0, diffRect.getX());
				Assert.assertEquals(1, diffRect.getY());
				Assert.assertEquals(4, diffRect.getWidth());
				Assert.assertEquals(3, diffRect.getHeight());
			}
		}
	}

	@Test
	public void testSubtractTopRight() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(2, 2, 2, 2);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(2, remaining.size());

		for (int i = 0; i < remaining.size(); i++) {
			Rectangle diffRect = remaining.get(0);
			if (diffRect.getX() == 0) {
				Assert.assertEquals(0, diffRect.getX());
				Assert.assertEquals(0, diffRect.getY());
				Assert.assertEquals(2, diffRect.getWidth());
				Assert.assertEquals(4, diffRect.getHeight());
			} else {
				Assert.assertEquals(2, diffRect.getX());
				Assert.assertEquals(1, diffRect.getY());
				Assert.assertEquals(2, diffRect.getWidth());
				Assert.assertEquals(2, diffRect.getHeight());
			}
		}
	}

	@Test
	public void testSubtractTopMiddle() {
		rectangle1 = new Rectangle(0, 0, 4, 4);
		rectangle2 = new Rectangle(2, 0, 1, 2);

		List<Rectangle> remaining = new ArrayList<Rectangle>();
		Rectangle.subtract(remaining, rectangle1, rectangle2);
		Assert.assertEquals(3, remaining.size());

		for (int i = 0; i < remaining.size(); i++) {
			Rectangle diffRect = remaining.get(0);
			if (diffRect.getX() == 0) {
				if (diffRect.getY() == 0) {
					Assert.assertEquals(0, diffRect.getX());
					Assert.assertEquals(0, diffRect.getY());
					Assert.assertEquals(2, diffRect.getWidth());
					Assert.assertEquals(4, diffRect.getHeight());
				} else {
					Assert.assertEquals(0, diffRect.getX());
					Assert.assertEquals(2, diffRect.getY());
					Assert.assertEquals(4, diffRect.getWidth());
					Assert.assertEquals(2, diffRect.getHeight());
				}
			} else if (diffRect.getX() == 3) {
				Assert.assertEquals(3, diffRect.getX());
				Assert.assertEquals(0, diffRect.getY());
				Assert.assertEquals(1, diffRect.getWidth());
				Assert.assertEquals(4, diffRect.getHeight());
			}
		}
	}
}
