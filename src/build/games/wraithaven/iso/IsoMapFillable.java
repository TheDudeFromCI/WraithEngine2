/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.tools.CircleTool;
import build.games.wraithaven.core.tools.FillTool;
import build.games.wraithaven.core.tools.Fillable;
import build.games.wraithaven.core.tools.RectangleTool;

/**
 * @author TheDudeFromCI
 */
public class IsoMapFillable implements Fillable{
	private final Map map;
	public IsoMapFillable(Map map){
		this.map = map;
	}
	@Override
	public int getWidth(){
		return map.getWidth();
	}
	@Override
	public int getHeight(){
		return map.getHeight();
	}
	@Override
	public Object getTile(int x, int y){
		TileInstance t = map.getTile(x, y);
		return t==null?null:t.getTile();
	}
	@Override
	public void setTile(int x, int y, Object tile){
		map.setTile(x, y, (Tile)tile);
	}
	@Override
	public boolean tilesMatch(Object a, Object b){
		return a==b;
	}
	public void fill(int x, int y, Object selected){
		FillTool tool = new FillTool(this);
		tool.setSelection(selected);
		tool.fill(x, y);
	}
	public void rectangle(int x1, int y1, int x2, int y2, Object selected){
		RectangleTool tool = new RectangleTool(this);
		tool.setSelection(selected);
		tool.setStartPos(x1, y1);
		tool.setEndPos(x2, y2);
	}
	public void circle(int x1, int y1, int x2, int y2, Object selected){
		CircleTool tool = new CircleTool(this);
		tool.setSelection(selected);
		tool.setStartPos(x1, y1);
		tool.setEndPos(x2, y2);
	}
}
