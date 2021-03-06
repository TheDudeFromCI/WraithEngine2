/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import wraith.lib.util.Algorithms;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @author TheDudeFromCI
 */
public class Tile{
	private final String uuid;
	private final TileCategory cat;
	public Tile(String uuid, TileCategory cat){
		this.uuid = uuid;
		this.cat = cat;
	}
	public Tile(String uuid, BufferedImage image, TileCategory cat){
		this.uuid = uuid;
		this.cat = cat;
		try{
			ImageIO.write(image, "png", Algorithms.getFile("Chipsets", cat.getUUID(), uuid+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	public String getUUID(){
		return uuid;
	}
	public TileCategory getCategory(){
		return cat;
	}
}
