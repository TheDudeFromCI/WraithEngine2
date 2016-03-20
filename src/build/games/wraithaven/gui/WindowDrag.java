/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

/**
 * @author thedudefromci
 */
public class WindowDrag{
	private final int startSpacing;
	private final int mouseXStart;
	private final int mouseYStart;
	private int spaceX;
	private int spaceY;
	public WindowDrag(int x, int y, int space){
		startSpacing = space;
		mouseXStart = x;
		mouseYStart = y;
	}
	public void update(int x, int y){
		spaceX = mouseXStart-x+startSpacing;
		spaceY = mouseYStart-y+startSpacing;
	}
	public int getSpacingX(){
		return spaceX;
	}
	public int getSpacingY(){
		return spaceY;
	}
}
