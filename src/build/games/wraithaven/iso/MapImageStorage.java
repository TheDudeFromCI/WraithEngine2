/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * @author TheDudeFromCI
 */
public class MapImageStorage{
	private final HashMap<Tile,BufferedImage> images = new HashMap(16);
	public BufferedImage getImage(Tile tile){
		if(images.containsKey(tile)){
			return images.get(tile);
		}
		try{
			BufferedImage image = ImageIO.read(Algorithms.getFile("Chipsets", tile.getUUID()+".png"));
			images.put(tile, image);
			return image;
		}catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}
	public void clear(){
		images.clear();
	}
}