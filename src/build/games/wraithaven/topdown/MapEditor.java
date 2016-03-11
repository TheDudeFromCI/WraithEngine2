/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.MapContainer;
import build.games.wraithaven.core.MapInterface;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class MapEditor extends JPanel implements MapContainer{
	private final WorldScreen worldScreen;
	private final WorldScreenToolbar toolbar;
	private final TopDownMapStyle mapStyle;
	public MapEditor(TopDownMapStyle mapStyle){
		this.mapStyle = mapStyle;
		worldScreen = new WorldScreen(mapStyle);
		toolbar = new WorldScreenToolbar(this);
		init();
	}
	private void init(){
		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(worldScreen, BorderLayout.CENTER);
	}
	public WorldScreen getWorldScreen(){
		return worldScreen;
	}
	public WorldScreenToolbar getToolbar(){
		return toolbar;
	}
	public boolean needsSaving(){
		return worldScreen.needsSaving();
	}
	public void save(){
		worldScreen.save();
	}
	@Override
	public Map getSelectedMap(){
		return worldScreen.getSelectedMap();
	}
	@Override
	public void selectMap(MapInterface map){
		worldScreen.selectMap((Map)map);
	}
	@Override
	public MapInterface loadMap(String uuid){
		return new Map(mapStyle, uuid);
	}
	@Override
	public MapInterface generateMap(String uuid, String name, int width, int height){
		return new Map(mapStyle, uuid, name, width, height);
	}
	@Override
	public void closeMapNoSave(){
		worldScreen.closeMapNoSave();
	}
}
