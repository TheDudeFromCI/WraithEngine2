/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

public class SelectionCursor{
	private boolean seen;
	private int x;
	private int y;
	private boolean overVoid;
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void hide(){
		seen = false;
	}
	public boolean isOverVoid(){
		return overVoid;
	}
	public boolean isSeen(){
		return seen;
	}
	public void moveTo(int x, int y){
		seen = true;
		this.x = x;
		this.y = y;
	}
	public void setOverVoid(boolean overVoid){
		this.overVoid = overVoid;
	}
}
