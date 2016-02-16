/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author TheDudeFromCI
 */
public class TileInstance{
	private final Tile tile;
	private final HashMap<Layer,EntityType> entities = new HashMap(1);
	private int height;
	public TileInstance(Tile tile){
		this.tile = tile;
	}
	public TileInstance(Tile tile, int height){
		this.tile = tile;
		this.height = height;
	}
	public Tile getTile(){
		return tile;
	}
	public int getHeight(){
		return height;
	}
	public void setHeight(int height){
		this.height = height;
	}
	public EntityType getEntity(Layer selectedLayer){
		if(entities.containsKey(selectedLayer)){
			return entities.get(selectedLayer);
		}
		return null;
	}
	public void setEntity(EntityType entity, Layer selectedLayer){
		if(entity==null){
			entities.remove(selectedLayer);
		}else{
			entities.put(selectedLayer, entity);
		}
	}
	public HashMap<Layer,EntityType> getAllEntities(){
		return entities;
	}
	public void removeEntity(EntityType entity){
		ArrayList<Layer> toRemove = new ArrayList(1);
		for(Layer layer : entities.keySet()){
			if(entities.get(layer)==entity){
				toRemove.add(layer);
			}
		}
		for(Layer layer : toRemove){
			entities.remove(layer);
		}
	}
}
