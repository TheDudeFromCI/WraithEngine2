/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.MapInterface;
import build.games.wraithaven.core.WorldList;
import build.games.wraithaven.util.Algorithms;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ChipsetListComponent{
	private final Chipset chipset;
	private boolean expanded;
	private BufferedImage image;
	public ChipsetListComponent(Chipset chipset){
		this.chipset = chipset;
	}
	public Chipset getChipset(){
		return chipset;
	}
	public BufferedImage getImage(){
		return image;
	}
	public String getName(){
		return chipset.getName();
	}
	public boolean isExpanded(){
		return expanded;
	}
	public int getSize(){
		return chipset.getSize();
	}
	private void delete(Map map){
		for(Map m : map.getChildMaps()){
			delete(m);
		}
		int x, y;
		Tile tile;
		boolean loaded = map.isLoaded();
		if(!loaded){
			map.loadMaps();
		}
		for(MapSection sec : map.getMapSections()){
			for(MapLayer layer : sec.getAllLayers()){
				for(x = 0; x<map.getWidth(); x++){
					for(y = 0; y<map.getHeight(); y++){
						tile = layer.getTile(x, y);
						if(tile!=null&&tile.getChipset()==chipset){
							sec.setTile(x, y, layer.getLayer(), null);
						}
					}
				}
			}
		}
		map.save();
		if(!loaded){
			map.dispose();
		}
	}
	public void delete(WorldList worldList){
		for(MapInterface map : worldList.getParentMaps()){
			delete((Map)map);
		}
		Algorithms.deleteFile(Algorithms.getFile("Chipsets", chipset.getUUID()));
	}
	public void setExpanded(boolean expanded){
		this.expanded = expanded;
		if(expanded){
			try{
				image = ImageIO.read(Algorithms.getFile("Chipsets", chipset.getUUID(), "preview.png"));
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}else{
			image = null; // To dispose unused resources and save memory.
		}
	}
}
