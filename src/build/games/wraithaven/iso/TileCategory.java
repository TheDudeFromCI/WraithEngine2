/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class TileCategory{
	private static final short FILE_VERSION = 0;
	private final String uuid;
	private final MapEditorTab mapStyle;
	private final ComplexEntityList complexEntityList;
	private ArrayList<Tile> tiles;
	private ArrayList<EntityType> entities;
	private String name;
	public TileCategory(MapEditorTab mapStyle, String uuid){
		this.mapStyle = mapStyle;
		this.uuid = uuid;
		load();
		complexEntityList = new ComplexEntityList(this);
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
	public void load(){
		File file = Algorithms.getFile("Categories", uuid+".dat");
		if(!file.exists()){
			tiles = new ArrayList(64);
			entities = new ArrayList(64);
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		short version = bin.getShort();
		switch(version){
			case 0:
				name = bin.getString();
				int tileCount = bin.getInt();
				tiles = new ArrayList(Math.max(tileCount, 64));
				for(int i = 0; i<tileCount; i++){
					tiles.add(new Tile(bin.getString(), this));
				}
				int entityCount = bin.getInt();
				entities = new ArrayList(Math.max(entityCount, 64));
				for(int i = 0; i<entityCount; i++){
					entities.add(new EntityType(bin, version, this));
				}
				break;
			default:
				throw new RuntimeException();
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(8+entities.size()*EntityType.BIN_STORAGE_SIZE+2);
		bin.addShort(FILE_VERSION);
		bin.addStringAllocated(name);
		bin.addInt(tiles.size());
		for(Tile tile : tiles){
			bin.addStringAllocated(tile.getUUID());
		}
		bin.addInt(entities.size());
		for(EntityType entity : entities){
			entity.store(bin);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Categories", uuid+".dat"));
	}
	@Override
	public String toString(){
		return name;
	}
	public ArrayList<Tile> getTiles(){
		return tiles;
	}
	public ArrayList<EntityType> getEntities(){
		return entities;
	}
	public void addTile(Tile tile){
		tiles.add(tile);
		save();
		mapStyle.updateTileList();
	}
	public Tile getTile(String uuid){
		for(Tile tile : tiles){
			if(tile.getUUID().equals(uuid)){
				return tile;
			}
		}
		return null;
	}
	public int getIndexOf(Tile tile){
		return tiles.indexOf(tile);
	}
	public void addEntityType(EntityType e, BufferedImage originalImage){
		entities.add(e);
		try{
			ImageIO.write(originalImage, "png", Algorithms.getFile("Entities", e.getCategory().getUUID(), e.getUUID()+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
		save();
	}
	public EntityType getEntity(String uuid){
		for(EntityType entity : entities){
			if(entity.getUUID().equals(uuid)){
				return entity;
			}
		}
		return null;
	}
	public int getIndexOf(EntityType entity){
		return entities.indexOf(entity);
	}
	public void delete(){
		for(Tile tile : tiles){
			mapStyle.getMapEditor().removeTile(tile);
		}
		for(EntityType entity : entities){
			mapStyle.getMapEditor().removeEntity(entity);
		}
		Algorithms.deleteFile(Algorithms.getFile("Categories", uuid+".dat"));
		Algorithms.deleteFile(Algorithms.getFile("Entities", uuid));
		Algorithms.deleteFile(Algorithms.getFile("Chipsets", uuid));
	}
	public ComplexEntityList getComplexEntityList(){
		return complexEntityList;
	}
}
