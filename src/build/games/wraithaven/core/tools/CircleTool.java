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
public class CircleTool{
	private static float sq(float x){
		return x*x;
	}
	private final Fillable fillable;
	private Object sel;
	private int startX;
	private int startY;
	public CircleTool(Fillable fillable){
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
		float w = (x2-x1+1)/2f;
		float h = (y2-y1+1)/2f;
		float cx = (x2-x1)/2f+x1;
		float cy = (y2-y1)/2f+y1;
		int a, b;
		for(a = x1; a<=x2; a++){
			for(b = y1; b<=y2; b++){
				if(sq(a-cx)/sq(w)+sq(b-cy)/sq(h)>1){
					continue;
				}
				fillable.setTile(a, b, sel);
			}
		}
	}
}
