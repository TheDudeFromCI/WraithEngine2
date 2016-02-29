/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class Tile{
	private final BufferedImage image;
	private final TileModel model;
	public Tile(String cat, String uuid){
		// TODO Load tile.
		BufferedImage imageTemp;
		try{
			imageTemp = ImageIO.read(Algorithms.getFile("Chipsets", cat, uuid+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
			imageTemp = null;
		}
		image = imageTemp;
		model = new TileModel(this);
	}
	public BufferedImage getImage(){
		return image;
	}
	public TileModel getModel(){
		return model;
	}
}
