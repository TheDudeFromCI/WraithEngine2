/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

public class ChipsetTileSelection{
	private Chipset chipset;
	private int[] index;
	private int x;
	private int y;
	private int width = 1;
	private int height = 1;
	public Chipset getChipset(){
		return chipset;
	}
	public int[] getIndex(){
		return index;
	}
	public int getSelectionX(){
		return x;
	}
	public int getSelectionY(){
		return y;
	}
	public boolean isActive(){
		return chipset!=null;
	}
	public void reset(){
		chipset = null;
		width = 1;
		height = 1;
	}
	public void select(Chipset chipset, int[] index, int x, int y, int width, int height){
		this.chipset = chipset;
		this.index = index;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
}
