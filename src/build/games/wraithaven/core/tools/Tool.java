/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.tools;

import wraith.lib.util.Algorithms;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @author TheDudeFromCI
 */
public enum Tool{
	BASIC("Pencil Cursor.png", 2, false),
	FILL("Paint Bucket.png", 0.03125f, 0.90625f, false),
	RECTANGLE("Rectangle.png", 1, true),
	CIRCLE("Circle.png", 1, true);
	private static final int TOP_LEFT = 0;
	private static final int CENTER = 1;
	private static final int BOTTOM_LEFT = 2;
	private static Point getPoint(BufferedImage image, int pos){
		if(image==null){
			return new Point(0, 0);
		}
		Dimension maxSize = Toolkit.getDefaultToolkit().getBestCursorSize(image.getWidth(), image.getHeight());
		switch(pos){
			case TOP_LEFT:
				return new Point(0, 0);
			case CENTER:
				return new Point(maxSize.width/2, maxSize.height/2);
			case BOTTOM_LEFT:
				return new Point(0, maxSize.height-1);
			default:
				throw new RuntimeException();
		}
	}
	private static Point getPoint(BufferedImage image, float xPos, float yPos){
		Dimension maxSize = Toolkit.getDefaultToolkit().getBestCursorSize(image.getWidth(), image.getHeight());
		return new Point((int)(maxSize.width*xPos), (int)(maxSize.height*yPos));
	}
	private final Cursor cursor;
	private boolean dragBased;
	private Tool(String c, int pos, boolean dragBased){
		this.dragBased = dragBased;
		BufferedImage image = null;
		try{
			image = ImageIO.read(Algorithms.getAsset(c));
		}catch(Exception exception){
			exception.printStackTrace();
		}
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, getPoint(image, pos), c);
	}
	private Tool(String c, float xPos, float yPos, boolean dragBased){
		this.dragBased = dragBased;
		BufferedImage image = null;
		try{
			image = ImageIO.read(Algorithms.getAsset(c));
		}catch(Exception exception){
			exception.printStackTrace();
		}
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, getPoint(image, xPos, yPos), c);
	}
	public Cursor getCursor(){
		return cursor;
	}
	public boolean isDragBased(){
		return dragBased;
	}
}
