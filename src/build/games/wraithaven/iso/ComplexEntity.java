/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.BinaryFile;

/**
 * @author TheDudeFromCI
 */
public class ComplexEntity implements EntityInterface{
	private final EntityType[] entities;
	private final int[] positions;
	private final String uuid;
	public ComplexEntity(String uuid, EntityType[] entities, int[] positions){
		this.uuid = uuid;
		this.entities = entities;
		this.positions = positions;
	}
	public ComplexEntity(BinaryFile bin, TileCategory cat){
		uuid = bin.getString();
		entities = new EntityType[bin.getInt()];
		for(int i = 0; i<entities.length; i++){
			entities[i] = cat.getEntity(bin.getString());
		}
		positions = new int[entities.length*2];
		for(int i = 0; i<positions.length; i++){
			positions[i] = bin.getInt();
		}
	}
	public void save(BinaryFile bin){
		bin.addStringAllocated(uuid);
		bin.allocateBytes(4+positions.length*4);
		bin.addInt(entities.length);
		for(EntityType e : entities){
			bin.addStringAllocated(e.getUUID());
		}
		for(int i : positions){
			bin.addInt(i);
		}
	}
	public void place(Map map, int x, int z, Layer layer){
		TileInstance tile;
		int a, b;
		for(int i = 0; i<entities.length; i++){
			a = x+positions[i*2];
			b = z+positions[i*2+1];
			tile = map.getTile(a, b);
			if(tile!=null){
				tile.setEntity(entities[i], layer);
			}
		}
	}
	public String getUUID(){
		return uuid;
	}
}
