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

/**
 * @author TheDudeFromCI
 */
public class Layer{
	private final String uuid;
	private String name;
	private boolean visible;
	private boolean needsSaving;
	public Layer(String name){
		uuid = Algorithms.randomUUID();
		this.name = name;
		visible = true;
		needsSaving = true;
	}
	public Layer(BinaryFile bin){
		uuid = bin.getString();
		name = bin.getString();
		visible = bin.getBoolean();
		needsSaving = false;
	}
	public void save(BinaryFile bin){
		needsSaving = false;
		bin.addStringAllocated(uuid);
		bin.addStringAllocated(name);
		bin.allocateBytes(1);
		bin.addBoolean(visible);
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
		needsSaving = true;
	}
	public boolean needsSaving(){
		return needsSaving;
	}
	public void setNeedsSaving(){
		needsSaving = true;
	}
	public boolean isVisible(){
		return visible;
	}
	public void setVisible(boolean visible){
		this.visible = visible;
		needsSaving = true;
	}
	public String getUUID(){
		return uuid;
	}
}
