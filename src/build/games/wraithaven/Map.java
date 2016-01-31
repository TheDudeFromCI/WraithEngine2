/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven;

import java.io.File;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class Map{
	private final ArrayList<MapSection> mapSections = new ArrayList(16);
	private final ArrayList<Map> childMaps = new ArrayList(0);
	private final String uuid;
	private final WorldBuilder worldBuilder;
	private String name;
	private boolean mapsLoaded;
	public Map(WorldBuilder worldBuilder, String uuid){
		this.worldBuilder = worldBuilder;
		this.uuid = uuid;
		loadProperties();
	}
	public Map(WorldBuilder worldBuilder, String uuid, String name){
		this.worldBuilder = worldBuilder;
		this.uuid = uuid;
		this.name = name;
		saveProperties();
	}
	public Map getChild(int index){
		return childMaps.get(index);
	}
	public void addChild(Map map){
		childMaps.add(map);
		saveProperties();
	}
	public void removeChild(Map map){
		childMaps.remove(map);
		saveProperties();
	}
	public int getChildCount(){
		return childMaps.size();
	}
	public int getIndexOf(Map child){
		return childMaps.indexOf(child);
	}
	public boolean needsSaving(){
		for(MapSection sec : mapSections){
			if(sec.needsSaving()){
				return true;
			}
		}
		return false;
	}
	private void loadProperties(){
		File file = Algorithms.getFile("Worlds", uuid, "Properties.dat");
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		name = bin.getString();
		int childCount = bin.getInt();
		for(int i = 0; i<childCount; i++){
			Map map = new Map(worldBuilder, bin.getString());
			childMaps.add(map);
		}
	}
	public void save(){
		for(MapSection sec : mapSections){
			sec.save();
		}
	}
	public void loadMaps(){
		mapsLoaded = true;
		mapSections.clear();
		File file = Algorithms.getFile("Worlds", uuid, "List.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int mapCount = bin.getInt();
		for(int i = 0; i<mapCount; i++){
			mapSections.add(new MapSection(worldBuilder.getChipsetList(), worldBuilder.getWorldScreenToolbar(), this, bin.getInt(), bin.getInt()));
		}
	}
	private void saveProperties(){
		BinaryFile bin = new BinaryFile(0);
		byte[] bytes = name.getBytes();
		bin.allocateBytes(bytes.length+8);
		bin.addInt(bytes.length);
		bin.addBytes(bytes, 0, bytes.length);
		bin.addInt(childMaps.size());
		for(Map child : childMaps){
			bytes = child.getUUID().getBytes();
			bin.allocateBytes(bytes.length+4);
			bin.addInt(bytes.length);
			bin.addBytes(bytes, 0, bytes.length);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", uuid, "Properties.dat"));
	}
	private void saveMaps(){
		BinaryFile bin = new BinaryFile(4+mapSections.size()*8);
		bin.addInt(mapSections.size());
		for(MapSection map : mapSections){
			bin.addInt(map.getMapX());
			bin.addInt(map.getMapY());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", uuid, "List.dat"));
	}
	public String getUUID(){
		return uuid;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
		saveProperties();
	}
	public void dispose(){
		mapsLoaded = false;
		mapSections.clear();
	}
	public void addMapSection(MapSection mapSection){
		if(!mapsLoaded){
			throw new RuntimeException("Maps not loaded!");
		}
		mapSections.add(mapSection);
		saveMaps();
	}
	public void removeMapSection(MapSection mapSection){
		if(!mapsLoaded){
			throw new RuntimeException("Maps not loaded!");
		}
		mapSections.remove(mapSection);
		saveMaps();
	}
	public ArrayList<MapSection> getMapSections(){
		return mapSections;
	}
	@Override
	public String toString(){
		return name;
	}
}
