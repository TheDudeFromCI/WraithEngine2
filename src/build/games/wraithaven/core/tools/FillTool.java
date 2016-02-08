/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.tools;

import java.awt.Point;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class FillTool{
	private final Fillable fillable;
	private Object sel;
	public FillTool(Fillable fillable){
		this.fillable = fillable;
	}
	public void setSelection(Object sel){
		this.sel = sel;
	}
	public void fill(int x, int y){
		ArrayList<Point> open = new ArrayList(fillable.getWidth()*fillable.getHeight());
		ArrayList<Point> closed = new ArrayList(fillable.getWidth()*fillable.getHeight());
		open.add(new Point(x, y));
		Object from = fillable.getTile(x, y);
		Point p, p2;
		int i;
		while(!open.isEmpty()){
			p = open.remove(0);
			closed.add(p);
			if(fillable.tilesMatch(fillable.getTile(p.x, p.y), from)){
				fillable.setTile(p.x, p.y, sel);
				if(p.x>0){
					p2 = new Point(p.x-1, p.y);
					if(!open.contains(p2)&&!closed.contains(p2)){
						open.add(p2);
					}
				}
				if(p.y>0){
					p2 = new Point(p.x, p.y-1);
					if(!open.contains(p2)&&!closed.contains(p2)){
						open.add(p2);
					}
				}
				if(p.x<fillable.getWidth()-1){
					p2 = new Point(p.x+1, p.y);
					if(!open.contains(p2)&&!closed.contains(p2)){
						open.add(p2);
					}
				}
				if(p.y<fillable.getHeight()-1){
					p2 = new Point(p.x, p.y+1);
					if(!open.contains(p2)&&!closed.contains(p2)){
						open.add(p2);
					}
				}
			}
		}
	}
}
