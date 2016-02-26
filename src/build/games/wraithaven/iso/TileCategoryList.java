/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;
import java.io.File;
import java.util.ArrayList;

/**
 * @author thedudefromci
 */
public class TileCategoryList{
	private final ArrayList<TileCategory> categories = new ArrayList(32);
	private final IsoMapStyle mapStyle;
	public TileCategoryList(IsoMapStyle mapStyle){
		this.mapStyle = mapStyle;
		load();
	}
	private void load(){
		File file = Algorithms.getFile("Categories.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int size = bin.getInt();
		for(int i = 0; i<size; i++){
			categories.add(new TileCategory(mapStyle, bin.getString()));
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(categories.size());
		for(TileCategory t : categories){
			bin.addStringAllocated(t.getUUID());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Categories.dat"));
	}
	public void addCategory(TileCategory category){
		categories.add(category);
		save();
	}
	public void removeCategory(TileCategory category){
		categories.remove(category);
		save();
	}
	public TileCategory getCategory(String uuid){
		for(TileCategory c : categories){
			if(c.getUUID().equals(uuid)){
				return c;
			}
		}
		return null;
	}
	public int getSize(){
		return categories.size();
	}
	public TileCategory getCategoryAt(int index){
		return categories.get(index);
	}
	public int getIndexOf(TileCategory cat){
		return categories.indexOf(cat);
	}
}
