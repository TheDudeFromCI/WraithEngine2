/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.tools.FillTool;
import build.games.wraithaven.core.tools.Fillable;
import build.games.wraithaven.core.tools.RectangleTool;

/**
 * @author TheDudeFromCI
 */
public class MapSectionFillable implements Fillable{
	private final MapSection mapSection;
	private final Map map;
	private final int layer;
	public MapSectionFillable(Map map, MapSection mapSection, int layer){
		this.map = map;
		this.mapSection = mapSection;
		this.layer = layer;
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
		return mapSection.getTile(x, y, layer);
	}
	@Override
	public void setTile(int x, int y, Object tile){
		mapSection.setTile(x, y, layer, (Tile)tile);
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
}
