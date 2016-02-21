/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.io.File;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class ComplexEntityList{
	private final ArrayList<ComplexEntity> entities = new ArrayList(8);
	private final TileCategory cat;
	public ComplexEntityList(TileCategory cat){
		this.cat = cat;
		load();
	}
	private void load(){
		File file = Algorithms.getFile("Entities", "Complex", cat.getUUID()+".dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int entityCount = bin.getInt();
		for(int i = 0; i<entityCount; i++){
			entities.add(new ComplexEntity(bin, cat));
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(entities.size());
		for(ComplexEntity com : entities){
			com.save(bin);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Entities", "Complex", cat.getUUID()+".dat"));
	}
	public void addComplexEntity(ComplexEntity complex){
		entities.add(complex);
		save();
	}
	public int getSize(){
		return entities.size();
	}
	public ArrayList<ComplexEntity> getAllEntities(){
		return entities;
	}
}
