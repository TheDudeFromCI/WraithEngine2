/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.io.File;
import java.util.ArrayList;

/**
 * @author thedudefromci
 */
public class TileCategory{
	private static final short FILE_VERSION = 0;
	private final String uuid;
	private ArrayList<Tile> tiles = new ArrayList(64);
	private ArrayList<EntityType> entities = new ArrayList(64);
	private String name;
	private boolean loaded;
	public TileCategory(String uuid){
		this.uuid = uuid;
		loaded = false;
		partLoad();
	}
	public String getUUID(){
		return uuid;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
		save();
	}
	private void partLoad(){
		File file = Algorithms.getFile("Categories", uuid+".dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		short version = bin.getShort();
		switch(version){
			case 0:
				name = bin.getString();
				break;
			default:
				throw new RuntimeException();
		}
	}
	public boolean isLoaded(){
		return loaded;
	}
	public void load(){
		if(loaded){
			throw new RuntimeException("Already loaded!");
		}
		loaded = true;
		tiles = new ArrayList(64);
		entities = new ArrayList(64);
		File file = Algorithms.getFile("Categories", uuid+".dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		short version = bin.getShort();
		switch(version){
			case 0:
				name = bin.getString();
				int tileCount = bin.getInt();
				for(int i = 0; i<tileCount; i++){
					tiles.add(new Tile(bin.getString()));
				}
				int entityCount = bin.getInt();
				String entityUuid;
				int height;
				for(int i = 0; i<entityCount; i++){
					entityUuid = bin.getString();
					height = bin.getInt();
					entities.add(new EntityType(entityUuid, height));
				}
				break;
			default:
				throw new RuntimeException();
		}
	}
	public void unload(){
		if(!loaded){
			return;
		}
		loaded = false;
		tiles = null;
		entities = null;
	}
	private void save(){
		boolean manuallyLoaded = !loaded;
		if(manuallyLoaded){
			load();
		}
		BinaryFile bin = new BinaryFile(8+entities.size()*4+2);
		bin.addShort(FILE_VERSION);
		bin.addStringAllocated(name);
		bin.addInt(tiles.size());
		for(Tile tile : tiles){
			bin.addStringAllocated(tile.getUUID());
		}
		bin.addInt(entities.size());
		for(EntityType entity : entities){
			bin.addStringAllocated(entity.getUUID());
			bin.addInt(entity.getHeight());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Categories", uuid+".dat"));
		if(manuallyLoaded){
			unload();
		}
	}
	@Override
	public String toString(){
		return name;
	}
}
