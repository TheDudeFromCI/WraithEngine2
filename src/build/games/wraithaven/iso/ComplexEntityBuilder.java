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
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * @author TheDudeFromCI
 */
public class ComplexEntityBuilder{
	private final ArrayList<EntityType> entities = new ArrayList(4);
	private final ArrayList<Integer> positions = new ArrayList(8);
	private BufferedImage preview;
	public void addEntity(EntityType e, int x, int y){
		entities.add(e);
		positions.add(x);
		positions.add(y);
	}
	public void setPreview(BufferedImage preview){
		this.preview = preview;
	}
	public ComplexEntity build(){
		if(preview==null){
			throw new RuntimeException("Preview image not defined!");
		}
		String uuid = Algorithms.randomUUID();
		EntityType[] entityList = new EntityType[entities.size()];
		int[] positionList = new int[positions.size()];
		for(int i = 0; i<entityList.length; i++){
			entityList[i] = entities.get(i);
		}
		for(int i = 0; i<positionList.length; i++){
			positionList[i] = positions.get(i);
		}
		try{
			ImageIO.write(preview, "png", Algorithms.getFile("Entities", "Complex", uuid+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return new ComplexEntity(uuid, entityList, positionList);
	}
}
