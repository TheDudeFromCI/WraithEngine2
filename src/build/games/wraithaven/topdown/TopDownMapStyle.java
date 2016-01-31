/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.AbstractChipsetList;
import build.games.wraithaven.core.AbstractMapEditor;
import build.games.wraithaven.core.MapInterface;
import build.games.wraithaven.core.MapStyle;
import build.games.wraithaven.util.WrongImageSizeException;
import java.io.File;

/**
 * @author TheDudeFromCI
 */
public class TopDownMapStyle implements MapStyle{
	private final ChipsetList chipsetList;
	private final MapEditor mapEditor;
	public TopDownMapStyle(){
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
		try{
			new ChipsetPreview(chipsetList, new ChipsetImporter(file));
		}catch(WrongImageSizeException exception){
			// Nothing to worry about.
			// At this point, the window hasn't even attempted to build, so no resources wasted.
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	@Override
	public MapInterface loadMap(String uuid){
		return new Map(this, uuid);
	}
	@Override
	public void selectMap(MapInterface map){
		mapEditor.getWorldScreen().selectMap((Map)map);
	}
	@Override
	public MapInterface generateNewMap(String uuid, String name){
		return new Map(this, uuid, name);
	}
}
