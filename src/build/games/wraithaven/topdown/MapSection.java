/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.WraithEngine;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class MapSection{
	private final ArrayList<MapLayer> layers = new ArrayList(2);
	private final BufferedImage image;
	private final WorldScreenToolbar worldScreenToolbar;
	private final int mapX;
	private final int mapY;
	private final Map map;
	private final int width;
	private final int height;
	private boolean needsSaving;
	public MapSection(ChipsetList chipsetList, WorldScreenToolbar worldScreenToolbar, Map map, int mapX, int mapY, int width, int height){
		this.map = map;
		this.mapX = mapX;
		this.mapY = mapY;
		this.worldScreenToolbar = worldScreenToolbar;
		this.width = width;
		this.height = height;
		image = new BufferedImage(WraithEngine.projectBitSize*width, WraithEngine.projectBitSize*height, BufferedImage.TYPE_INT_ARGB);
		load(chipsetList);
		redraw();
	}
	public ArrayList<MapLayer> getAllLayers(){
		return layers;
	}
	public BufferedImage getImage(){
		return image;
	}
	public int getMapX(){
		return mapX;
	}
	public int getMapY(){
		return mapY;
	}
	public Tile getTile(int x, int y, int z){
		for(MapLayer layer : layers){
			if(layer.getLayer()==z){
				return layer.getTile(x, y);
			}
		}
		return null;
	}
	private void load(ChipsetList chipsetList){
		File file = Algorithms.getFile("Worlds", map.getUUID(), mapX+","+mapY+".dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int layerCount = bin.getInt();
		for(int i = 0; i<layerCount; i++){
			MapLayer l = new MapLayer(bin.getInt(), width, height);
			l.load(bin, chipsetList);
			layers.add(l);
		}
	}
	public boolean needsSaving(){
		return needsSaving;
	}
	public void delete(){
		File file = Algorithms.getFile("Worlds", map.getUUID(), mapX+","+mapY+".dat");
		if(file.exists()){
			file.delete();
		}
	}
	public void redraw(){
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		if(worldScreenToolbar.hideOtherLayers()){
			int currentLayer = worldScreenToolbar.getEditingLayer();
			for(MapLayer layer : layers){
				if(layer.getLayer()==currentLayer){
					g.drawImage(layer.getImage(), 0, 0, null);
				}
			}
		}else{
			for(MapLayer layer : layers){
				g.drawImage(layer.getImage(), 0, 0, null);
			}
		}
		g.dispose();
	}
	public void save(){
		if(!needsSaving){
			return;
		}
		needsSaving = false;
		BinaryFile bin = new BinaryFile(4+layers.size()*4);
		bin.addInt(layers.size());
		for(MapLayer layer : layers){
			bin.addInt(layer.getLayer());
			layer.save(bin);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", map.getUUID(), mapX+","+mapY+".dat"));
	}
	public void setTile(int x, int y, int z, Tile tile){
		needsSaving = true;
		MapLayer layer = null;
		for(MapLayer l : layers){
			if(l.getLayer()==z){
				layer = l;
				break;
			}
		}
		if(layer==null&&tile==null){
			return;
		}
		if(layer==null){
			layer = new MapLayer(z, width, height);
			layers.add(layer);
		}
		layer.setTile(x, y, tile);
		if(layer.isEmpty()){
			layers.remove(layer);
		}
		redraw();
	}
}
