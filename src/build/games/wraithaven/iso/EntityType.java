/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.BinaryFile;

/**
 * @author TheDudeFromCI
 */
public class EntityType implements EntityInterface{
	public static int BIN_STORAGE_SIZE = 4+1;
	private final String uuid;
	private final int height;
	private final TileCategory cat;
	private final boolean complex;
	public EntityType(String uuid, int height, TileCategory cat, boolean complex){
		this.uuid = uuid;
		this.height = height;
		this.cat = cat;
		this.complex = complex;
	}
	public EntityType(BinaryFile bin, short fileVersion, TileCategory cat){
		this.cat = cat;
		switch(fileVersion){
			case 0:
				uuid = bin.getString();
				height = bin.getInt();
				complex = bin.getBoolean();
				break;
			default:
				throw new RuntimeException();
		}
	}
	public String getUUID(){
		return uuid;
	}
	public int getHeight(){
		return height;
	}
	public TileCategory getCategory(){
		return cat;
	}
	public void store(BinaryFile bin){
		bin.addStringAllocated(uuid);
		bin.addInt(height);
		bin.addBoolean(complex);
	}
	public boolean isComplex(){
		return complex;
	}
}
