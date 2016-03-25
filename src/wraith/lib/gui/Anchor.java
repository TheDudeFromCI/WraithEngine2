/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.gui;

import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Anchor{
	private float parentX = 0.5f;
	private float parentY = 0.5f;
	private float childX = 0.5f;
	private float childY = 0.5f;
	private float width;
	private float height;
	public float getParentX(){
		return parentX;
	}
	public float getParentY(){
		return parentY;
	}
	public float getChildX(){
		return childX;
	}
	public float getChildY(){
		return childY;
	}
	public void setParentPosition(float x, float y){
		parentX = x;
		parentY = y;
	}
	public void setChildPosition(float x, float y){
		childX = x;
		childY = y;
	}
	public void setSize(float width, float height){
		this.width = width;
		this.height = height;
	}
	public void save(BinaryFile bin){
		bin.allocateBytes(6*4);
		bin.addFloat(parentX);
		bin.addFloat(parentY);
		bin.addFloat(childX);
		bin.addFloat(childY);
		bin.addFloat(width);
		bin.addFloat(height);
	}
	public void load(BinaryFile bin){
		parentX = bin.getFloat();
		parentY = bin.getFloat();
		childX = bin.getFloat();
		childY = bin.getFloat();
		width = bin.getFloat();
		height = bin.getFloat();
	}
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
}
