/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.BinaryFile;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MapLayer{
	public static final int MAP_TILES_WIDTH = 20;
	public static final int MAP_TILES_HEIGHT = 15;
	private final Tile[] tiles;
	private final int layer;
	private int tileCount;
	private BufferedImage image;
	public MapLayer(int layer){
		this.layer = layer;
		tiles = new Tile[MAP_TILES_WIDTH*MAP_TILES_HEIGHT];
	}
	public BufferedImage getImage(){
		return image;
	}
	public int getLayer(){
		return layer;
	}
	public Tile[] getAllTiles(){
		return tiles;
	}
	public Tile getTile(int x, int y){
		return tiles[y*MAP_TILES_WIDTH+x];
	}
	public boolean isEmpty(){
		return tileCount==0;
	}
	public void load(BinaryFile bin, ChipsetList chipsetList){
		tileCount = bin.getInt();
		for(int i = 0; i<tiles.length; i++){
			if(bin.getBoolean()){
				try{
					String uuid = bin.getString();
					int id = bin.getInt();
					tiles[i] = chipsetList.getChipset(uuid).getTile(id);
				}catch(Exception exception){
					exception.printStackTrace();
					tiles[i] = null;
				}
			}else{
				tiles[i] = null;
			}
		}
		redraw();
	}
	public void redraw(){
		image =
			new BufferedImage(WraithEngine.projectBitSize*MAP_TILES_WIDTH, WraithEngine.projectBitSize*MAP_TILES_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		int x, y;
		int index;
		for(x = 0; x<MAP_TILES_WIDTH; x++){
			for(y = 0; y<MAP_TILES_HEIGHT; y++){
				index = y*MAP_TILES_WIDTH+x;
				if(tiles[index]==null){
					continue;
				}
				g.drawImage(tiles[index].getImage(), x*WraithEngine.projectBitSize, y*WraithEngine.projectBitSize, null);
			}
		}
		g.dispose();
	}
	public void save(BinaryFile bin){
		bin.allocateBytes(4);
		bin.addInt(tileCount);
		for(Tile tile : tiles){
			bin.allocateBytes(1);
			if(tile==null){
				bin.addBoolean(false);
			}else{
				bin.addBoolean(true);
				byte[] bytes = tile.getChipset().getUUID().getBytes();
				bin.allocateBytes(bytes.length+8);
				bin.addInt(bytes.length);
				bin.addBytes(bytes, 0, bytes.length);
				bin.addInt(tile.getId());
			}
		}
	}
	public void setTile(int x, int y, Tile tile){
		int index = y*MAP_TILES_WIDTH+x;
		if(tiles[index]==null^tile==null){
			if(tile==null){
				tileCount--;
			}else{
				tileCount++;
			}
		}
		tiles[index] = tile;
		redraw();
	}
}
