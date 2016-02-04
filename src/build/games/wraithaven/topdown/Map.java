/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.MapInterface;
import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.io.File;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class Map implements MapInterface{
	public static final short MAP_FILE_VERSION = 1;
	private final ArrayList<MapSection> mapSections = new ArrayList(16);
	private final ArrayList<Map> childMaps = new ArrayList(0);
	private final String uuid;
	private final TopDownMapStyle mapStyle;
	private String name;
	private boolean mapsLoaded;
	private String parent;
	public Map(TopDownMapStyle mapStyle, String uuid){
		this.mapStyle = mapStyle;
		this.uuid = uuid;
		loadProperties();
	}
	public Map(TopDownMapStyle mapStyle, String uuid, String name){
		this.mapStyle = mapStyle;
		this.uuid = uuid;
		this.name = name;
		saveProperties();
	}
	@Override
	public MapInterface getChild(int index){
		return childMaps.get(index);
	}
	@Override
	public void addChild(MapInterface map){
		childMaps.add((Map)map);
		saveProperties();
	}
	public void removeChild(Map map){
		childMaps.remove(map);
		saveProperties();
	}
	@Override
	public int getChildCount(){
		return childMaps.size();
	}
	@Override
	public int getIndexOf(MapInterface child){
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
		short version = bin.getShort();
		switch(version){
			case 0:{
				name = bin.getString();
				int childCount = bin.getInt();
				for(int i = 0; i<childCount; i++){
					Map map = new Map(mapStyle, bin.getString());
					childMaps.add(map);
				}
			}
				break;
			case 1:{
				name = bin.getString();
				int childCount = bin.getInt();
				for(int i = 0; i<childCount; i++){
					Map map = new Map(mapStyle, bin.getString());
					childMaps.add(map);
				}
				if(bin.getBoolean()){
					parent = bin.getString();
				}
			}
				break;
			default:
				throw new RuntimeException();
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
		short version = bin.getShort();
		switch(version){
			case 0:
			case 1:
				int mapCount = bin.getInt();
				for(int i = 0; i<mapCount; i++){
					mapSections.add(new MapSection((ChipsetList)mapStyle.getChipsetList(), ((MapEditor)mapStyle.getMapEditor()).getToolbar(), this,
						bin.getInt(), bin.getInt()));
				}
				break;
			default:
				throw new RuntimeException();
		}
	}
	private void saveProperties(){
		BinaryFile bin = new BinaryFile(7);
		bin.addShort(MAP_FILE_VERSION);
		bin.addStringAllocated(name);
		bin.addInt(childMaps.size());
		for(Map child : childMaps){
			bin.addStringAllocated(child.getUUID());
		}
		bin.addBoolean(parent!=null);
		if(parent!=null){
			bin.addStringAllocated(parent);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", uuid, "Properties.dat"));
	}
	private void saveMaps(){
		BinaryFile bin = new BinaryFile(4+mapSections.size()*8+2);
		bin.addShort(MAP_FILE_VERSION);
		bin.addInt(mapSections.size());
		for(MapSection map : mapSections){
			bin.addInt(map.getMapX());
			bin.addInt(map.getMapY());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", uuid, "List.dat"));
	}
	@Override
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
	@Override
	public void delete(){
		for(Map map : childMaps){
			map.delete();
		}
		Algorithms.deleteFile(Algorithms.getFile("Worlds", uuid));
	}
	@Override
	public MapInterface getParent(){
		if(parent==null){
			return null;
		}
		return WraithEngine.INSTANCE.getWorldList().getMap(parent);
	}
	@Override
	public void setParent(MapInterface parent){
		this.parent = parent.getUUID();
	}
	@Override
	public void removeChild(MapInterface map){
		childMaps.remove(map);
		saveMaps();
	}
}
