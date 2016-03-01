/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Entity{
	private static int loadEntityHeight(String cat, String id){
		try{
			File file = Algorithms.getFile("Categories", cat+".dat");
			BinaryFile bin = new BinaryFile(file);
			bin.decompress(false);
			short version = bin.getShort();
			switch(version){
				case 0:{
					bin.getString(); // Skip name.
					int tileCount = bin.getInt();
					for(int i = 0; i<tileCount; i++){
						bin.getString(); // Skip tiles.
					}
					int entityCount = bin.getInt();
					String uuid;
					int height;
					for(int i = 0; i<entityCount; i++){
						uuid = bin.getString();
						height = bin.getInt();
						bin.getBoolean(); // It's not complex.
						if(uuid.equals(id)){
							// Yay! We found it.
							return height;
						}
					}
					throw new RuntimeException("Entity height not found!");
				}
				default:
					throw new RuntimeException("Unknown file version! "+version);
			}
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
			return 0;
		}
	}
	private final BufferedImage image;
	private final EntityModel model;
	private final int height;
	private final String layer;
	public Entity(String cat, String uuid, String layer){
		this.layer = layer;
		BufferedImage imageTemp;
		try{
			imageTemp = ImageIO.read(Algorithms.getFile("Entities", cat, uuid+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
			imageTemp = null;
		}
		image = imageTemp;
		height = loadEntityHeight(cat, uuid);
		model = new EntityModel(this);
	}
	public BufferedImage getImage(){
		return image;
	}
	public EntityModel getModel(){
		return model;
	}
	public int getHeight(){
		return height;
	}
	public String getLayer(){
		return layer;
	}
}
