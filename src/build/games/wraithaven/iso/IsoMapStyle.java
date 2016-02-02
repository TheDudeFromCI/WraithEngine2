/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractChipsetList;
import build.games.wraithaven.core.AbstractMapEditor;
import build.games.wraithaven.core.MapInterface;
import build.games.wraithaven.core.MapStyle;
import java.io.File;

/**
 * @author TheDudeFromCI
 */
public class IsoMapStyle implements MapStyle{
	private final ChipsetList chipsetList;
	private final MapEditor mapEditor;
	public IsoMapStyle(){
		chipsetList = new ChipsetList();
		mapEditor = new MapEditor(chipsetList);
	}
	@Override
	public AbstractChipsetList getChipsetList(){
		return chipsetList;
	}
	@Override
	public AbstractMapEditor getMapEditor(){
		return mapEditor;
	}
	@Override
	public void openChipsetPreview(File file){
		new ChipsetImporter(chipsetList, file);
	}
	@Override
	public MapInterface loadMap(String uuid){
		return new Map(chipsetList, uuid);
	}
	@Override
	public MapInterface generateNewMap(String uuid, String name, int width, int height){
		return new Map(chipsetList, uuid, name, width, height);
	}
	@Override
	public void selectMap(MapInterface map){
		mapEditor.selectMap((Map)map);
	}
}
