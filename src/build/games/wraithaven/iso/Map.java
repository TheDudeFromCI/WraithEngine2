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
import java.io.File;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class Map implements MapInterface{
	private final String uuid;
	private final ArrayList<Map> childMaps = new ArrayList(1);
	private final ChipsetList chipsetList;
	private Tile[] tiles;
	private int width;
	private int height;
	private String name;
	private boolean loaded;
	private boolean needsSaving;
	public Map(ChipsetList chipsetList, String uuid){
		this.chipsetList = chipsetList;
		this.uuid = uuid;
		loadProperties();
	}
	public Map(ChipsetList chipsetList, String uuid, String name, int width, int height){
		this.chipsetList = chipsetList;
		this.uuid = uuid;
		this.name = name;
		this.width = width;
		this.height = height;
		saveProperties();
	}
	public boolean needsSaving(){
		return needsSaving;
	}
	public void save(){
		if(!loaded){
			throw new RuntimeException();
		}
		BinaryFile bin = new BinaryFile(tiles.length*4+4);
		ArrayList<Tile> tileReferences = new ArrayList(16);
		for(Tile t : tiles){
			if(t!=null&&!tileReferences.contains(t)){
				tileReferences.add(t);
			}
		}
		bin.addInt(tileReferences.size());
		for(Tile t : tileReferences){
			bin.addStringAllocated(t.getUUID());
		}
		for(Tile t : tiles){
			if(t==null){
				bin.addInt(-1);
			}else{
				bin.addInt(tileReferences.indexOf(t));
			}
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("Worlds", "Tiles", uuid+".dat"));
	}
	public Tile getTile(int x, int z){
		return tiles[z*width+x];
	}
	public void load(){
		if(loaded){
			throw new RuntimeException();
		}
		loaded = true;
		File file = Algorithms.getFile("Worlds", "Tiles", uuid+".dat");
		if(!file.exists()){
			tiles = new Tile[width*height];
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(true);
		tiles = new Tile[width*height];
		Tile[] tileReferences = new Tile[bin.getInt()];
		for(int i = 0; i<tileReferences.length; i++){
			tileReferences[i] = chipsetList.getTile(bin.getString());
		}
		for(int i = 0; i<tiles.length; i++){
			int id = bin.getInt();
			if(id==-1){
				continue;
			}
			tiles[i] = tileReferences[id];
		}
	}
	public void dispose(){
		if(!loaded){
			throw new RuntimeException();
		}
		loaded = false;
		tiles = null;
		needsSaving = false;
	}
	private void saveProperties(){
		BinaryFile bin = new BinaryFile(4+8);
		bin.addInt(width);
		bin.addInt(height);
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
		width = bin.getInt();
		height = bin.getInt();
		name = bin.getString();
		int childMapCount = bin.getInt();
		for(int i = 0; i<childMapCount; i++){
			childMaps.add(new Map(chipsetList, bin.getString()));
		}
	}
	public void setTile(int x, int z, Tile tile){
		tiles[z*width+x] = tile;
		needsSaving = true;
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
	public void addTile(Tile tile, int x, int z){
		tiles[z*width+x] = tile;
		needsSaving = true;
	}
	public Tile[] getAllTiles(){
		return tiles;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	@Override
	public String toString(){
		return name;
	}
}
