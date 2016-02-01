/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.MapInterface;
import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class Map implements MapInterface{
	private final String uuid;
	private final ArrayList<Map> childMaps = new ArrayList(1);
	private String name;
	private boolean loaded;
	private boolean needsSaving;
	public Map(String uuid){
		this.uuid = uuid;
		loadProperties();
	}
	public Map(String uuid, String name){
		this.uuid = uuid;
		this.name = name;
		saveProperties();
	}
	public boolean needsSaving(){
		return needsSaving;
	}
	public void save(){
		if(!loaded){
			throw new RuntimeException();
		}
		// TODO
	}
	public void load(){
		if(loaded){
			throw new RuntimeException();
		}
		loaded = true;
		// TODO
	}
	public void dispose(){
		if(!loaded){
			throw new RuntimeException();
		}
		loaded = false;
		// Drop everything.
	}
	private void saveProperties(){
		BinaryFile bin = new BinaryFile(4);
		bin.addStringAllocated(name);
		bin.addInt(childMaps.size());
		for(Map map : childMaps){
			bin.addStringAllocated(map.getUUID());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", uuid+".dat"));
	}
	private void loadProperties(){
		BinaryFile bin = new BinaryFile(Algorithms.getFile("Worlds", uuid+".dat"));
		bin.decompress(false);
		name = bin.getString();
		int childMapCount = bin.getInt();
		for(int i = 0; i<childMapCount; i++){
			childMaps.add(new Map(bin.getString()));
		}
	}
	@Override
	public String getUUID(){
		return uuid;
	}
	@Override
	public MapInterface getChild(int index){
		return childMaps.get(index);
	}
	@Override
	public int getChildCount(){
		return childMaps.size();
	}
	@Override
	public int getIndexOf(MapInterface map){
		return childMaps.indexOf(map);
	}
	@Override
	public void addChild(MapInterface map){
		childMaps.add((Map)map);
		save();
	}
}
