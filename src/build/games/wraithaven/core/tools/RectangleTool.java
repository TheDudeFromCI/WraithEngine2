/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.tools;

/**
 * @author TheDudeFromCI
 */
public class RectangleTool{
	private final Fillable fillable;
	private Object sel;
	private int startX;
	private int startY;
	public RectangleTool(Fillable fillable){
		this.fillable = fillable;
	}
	public void setSelection(Object sel){
		this.sel = sel;
	}
	public void setStartPos(int x, int y){
		startX = x;
		startY = y;
	}
	public void setEndPos(int endX, int endY){
		int x1 = Math.max(Math.min(startX, endX), 0);
		int y1 = Math.max(Math.min(startY, endY), 0);
		int x2 = Math.min(Math.max(startX, endX), fillable.getWidth()-1);
		int y2 = Math.min(Math.max(startY, endY), fillable.getHeight()-1);
		int a, b;
		for(a = x1; a<=x2; a++){
			for(b = y1; b<=y2; b++){
				fillable.setTile(a, b, sel);
			}
		}
	}
}
