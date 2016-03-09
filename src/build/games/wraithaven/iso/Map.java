/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.MapInterface;
import java.io.File;
import java.util.ArrayList;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;
import wraith.lib.util.SortedMap;

/**
 * @author TheDudeFromCI
 */
public class Map implements MapInterface{
	private static final short FILE_VERSION_PROPERTIES = 0;
	private static final short FILE_VERSION_TILES = 0;
	private final String uuid;
	private final ArrayList<Map> childMaps = new ArrayList(1);
	private final IsoMapStyle iso;
	private TileInstance[] tiles;
	private int width;
	private int height;
	private String name;
	private boolean loaded;
	private boolean needsSaving;
	private String parent;
	public Map(IsoMapStyle iso, String uuid){
		this.iso = iso;
		this.uuid = uuid;
		loadProperties();
	}
	public Map(IsoMapStyle iso, String uuid, String name, int width, int height){
		this.iso = iso;
		this.uuid = uuid;
		this.name = name;
		this.width = width;
		this.height = height;
		saveProperties();
	}
	public void deleteLayer(Layer layer){
		if(!loaded){
			throw new RuntimeException();
		}
		needsSaving = true;
		for(TileInstance tile : tiles){
			if(tile==null){
				continue;
			}
			tile.setEntity(null, layer);
		}
	}
	public boolean needsSaving(){
		return needsSaving;
	}
	public ArrayList<Map> getChildMaps(){
		return childMaps;
	}
	public void save(){
		if(!loaded){
			throw new RuntimeException();
		}
		if(!needsSaving){
			return;
		}
		needsSaving = false;
		BinaryFile bin = new BinaryFile(tiles.length*12+4+2);
		bin.addShort(FILE_VERSION_TILES);
		{
			// Tiles
			ArrayList<Tile> tileReferences = new ArrayList(16);
			for(TileInstance t : tiles){
				if(t!=null&&!tileReferences.contains(t.getTile())){
					tileReferences.add(t.getTile());
				}
			}
			bin.addInt(tileReferences.size());
			for(Tile t : tileReferences){
				bin.addStringAllocated(t.getCategory().getUUID());
				bin.addStringAllocated(t.getUUID());
			}
			for(TileInstance t : tiles){
				if(t==null){
					bin.addInt(-1);
					bin.addInt(0);
					bin.addInt(0);
				}else{
					bin.addInt(tileReferences.indexOf(t.getTile()));
					bin.addInt(t.getHeight());
					SortedMap<Layer,EntityType> entities = t.getAllEntities();
					entities.sort(null);
					bin.addInt(entities.getSize());
					EntityType entity;
					for(Layer layer : entities){
						entity = entities.get(layer);
						bin.addStringAllocated(layer.getUUID());
						bin.addStringAllocated(entity.getCategory().getUUID());
						bin.addStringAllocated(entity.getUUID());
					}
				}
			}
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("Worlds", "Tiles", uuid+".dat"));
	}
	public TileInstance getTile(int x, int z){
		return tiles[z*width+x];
	}
	public void setNeedsSaving(){
		needsSaving = true;
	}
	public void load(){
		if(loaded){
			throw new RuntimeException();
		}
		loaded = true;
		File file = Algorithms.getFile("Worlds", "Tiles", uuid+".dat");
		if(!file.exists()){
			tiles = new TileInstance[width*height];
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(true);
		short version = bin.getShort();
		switch(version){
			case 0:
				tiles = new TileInstance[width*height];
				Tile[] tileReferences = new Tile[bin.getInt()];
				for(int i = 0; i<tileReferences.length; i++){
					String cat = bin.getString();
					String tile = bin.getString();
					tileReferences[i] = iso.getChipsetList().getTile(cat, tile);
				}
				for(int i = 0; i<tiles.length; i++){
					int id = bin.getInt();
					if(id==-1){
						bin.skip(8);
						continue;
					}
					tiles[i] = new TileInstance(tileReferences[id], bin.getInt());
					int entityCount = bin.getInt();
					for(int j = 0; j<entityCount; j++){
						String layer = bin.getString();
						String cat = bin.getString();
						String entity = bin.getString();
						tiles[i].setEntity(iso.getChipsetList().getEntity(cat, entity), iso.getChipsetList().getEntityLayers().getType(layer));
					}
				}
				break;
			default:
				throw new RuntimeException();
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
		BinaryFile bin = new BinaryFile(4+8+1+2);
		bin.addShort(FILE_VERSION_PROPERTIES);
		bin.addInt(width);
		bin.addInt(height);
		bin.addStringAllocated(name);
		bin.addInt(childMaps.size());
		for(Map map : childMaps){
			bin.addStringAllocated(map.getUUID());
		}
		bin.addBoolean(parent!=null);
		if(parent!=null){
			bin.addStringAllocated(parent);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", uuid+".dat"));
	}
	private void loadProperties(){
		BinaryFile bin = new BinaryFile(Algorithms.getFile("Worlds", uuid+".dat"));
		bin.decompress(false);
		short version = bin.getShort();
		switch(version){
			case 0:
				width = bin.getInt();
				height = bin.getInt();
				name = bin.getString();
				int childMapCount = bin.getInt();
				for(int i = 0; i<childMapCount; i++){
					childMaps.add(new Map(iso, bin.getString()));
				}
				if(bin.getBoolean()){
					parent = bin.getString();
				}
				break;
			default:
				throw new RuntimeException();
		}
	}
	public void setTile(int x, int z, Tile tile){
		if(tile==null){
			tiles[z*width+x] = null;
		}else{
			tiles[z*width+x] = new TileInstance(tile);
		}
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
		saveProperties();
	}
	public TileInstance[] getAllTiles(){
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
		return iso.getWorldList().getMap(parent);
	}
	@Override
	public void setParent(MapInterface parent){
		this.parent = parent.getUUID();
		saveProperties();
	}
	@Override
	public void removeChild(MapInterface map){
		childMaps.remove(map);
		saveProperties();
	}
	public boolean isLoaded(){
		return loaded;
	}
}
