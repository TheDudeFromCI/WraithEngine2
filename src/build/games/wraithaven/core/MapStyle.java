/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

import java.io.File;

/**
 * @author TheDudeFromCI
 */
public interface MapStyle{
	public AbstractChipsetList getChipsetList();
	public AbstractMapEditor getMapEditor();
	public void openChipsetPreview(File file);
	public MapInterface loadMap(String uuid);
	public MapInterface generateNewMap(String uuid, String name, int width, int height);
	public void selectMap(MapInterface map);
	public MapInterface getSelectedMap();
	public boolean useChipsetScrollbar();
}
