/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractMapEditor;
import java.awt.Color;
import java.awt.Graphics;

/**
 * @author TheDudeFromCI
 */
public class MapEditor extends AbstractMapEditor{
	private Map map;
	@Override
	public boolean needsSaving(){
		if(map==null){
			return false;
		}
		return map.needsSaving();
	}
	@Override
	public void save(){
		if(map!=null){
			map.save();
		}
	}
	public void selectMap(Map map){
		if(this.map!=null){
			this.map.dispose();
		}
		this.map = map;
		map.load();
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(map==null?Color.gray:Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(map!=null){
			// TODO
		}
		g.dispose();
	}
}
