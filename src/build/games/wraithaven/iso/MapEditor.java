/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.MapContainer;
import build.games.wraithaven.core.MapInterface;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class MapEditor extends JPanel implements MapContainer{
	private final Toolbar toolbar;
	private final MapEditorPainter painter;
	private final MapEditorTab iso;
	public MapEditor(MapEditorTab iso){
		this.iso = iso;
		toolbar = new Toolbar(this);
		painter = new MapEditorPainter(iso, toolbar, this);
		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(painter, BorderLayout.CENTER);
	}
	public boolean needsSaving(){
		if(painter.getMap()==null){
			return false;
		}
		return painter.getMap().needsSaving();
	}
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
	@Override
	public Map getSelectedMap(){
		return painter.getMap();
	}
	@Override
	public void selectMap(MapInterface map){
		painter.selectMap((Map)map);
	}
	@Override
	public MapInterface loadMap(String uuid){
		return new Map(iso, uuid);
	}
	@Override
	public MapInterface generateMap(String uuid, String name, int width, int height){
		return new Map(iso, uuid, name, width, height);
	}
	public MapEditorPainter getPainter(){
		return painter;
	}
	public MapImageStorage getImageStorage(){
		return painter.getMapImageStorage();
	}
	private void removeTile(Map map, Tile tile){
		for(Map m : map.getChildMaps()){
			removeTile(m, tile);
		}
		boolean loaded = map.isLoaded();
		if(!loaded){
			map.load();
		}
		TileInstance[] t = map.getAllTiles();
		for(int i = 0; i<t.length; i++){
			if(t[i]==null){
				continue;
			}
			if(t[i].getTile()==tile){
				t[i] = null;
			}
		}
		map.setNeedsSaving();
		if(!loaded){
			map.save();
			map.dispose();
		}
	}
	public void removeTile(Tile tile){
		for(MapInterface map : iso.getWorldList().getMainMaps()){
			removeTile((Map)map, tile);
		}
		save();
		painter.repaint();
	}
	private void removeEntity(Map map, EntityType entity){
		for(Map m : map.getChildMaps()){
			removeEntity(m, entity);
		}
		boolean loaded = map.isLoaded();
		if(!loaded){
			map.load();
		}
		TileInstance[] t = map.getAllTiles();
		for(TileInstance t1 : t){
			if(t1==null){
				continue;
			}
			t1.removeEntity(entity);
		}
		map.setNeedsSaving();
		if(!loaded){
			map.save();
			map.dispose();
		}
	}
	public void removeEntity(EntityType entity){
		for(MapInterface map : iso.getWorldList().getMainMaps()){
			removeEntity((Map)map, entity);
		}
		save();
		painter.repaint();
	}
	@Override
	public void closeMapNoSave(){
		painter.closeMapNoSave();
	}
}
