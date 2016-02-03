/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractMapEditor;
import java.awt.BorderLayout;

/**
 * @author TheDudeFromCI
 */
public class MapEditor extends AbstractMapEditor{
	private final Toolbar toolbar;
	private final MapEditorPainter painter;
	public MapEditor(ChipsetList chipsetList){
		toolbar = new Toolbar(this);
		painter = new MapEditorPainter(chipsetList, toolbar, this);
		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(painter, BorderLayout.CENTER);
	}
	@Override
	public boolean needsSaving(){
		if(painter.getMap()==null){
			return false;
		}
		return painter.getMap().needsSaving();
	}
	@Override
	public void save(){
		if(painter.getMap()!=null){
			painter.getMap().save();
			painter.getMapImageStorage().clear(); // To keep memory down, drop all images on save. Just in case some of the loaded tiles are no longer
			// being used.
			painter.updateNeedsSaving();
		}
	}
	public void selectMap(Map map){
		painter.selectMap(map);
	}
}
