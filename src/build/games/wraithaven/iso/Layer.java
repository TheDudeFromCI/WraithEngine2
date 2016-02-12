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
public class Layer{
	private String name;
	private boolean visible = true;
	private boolean needsSaving;
	public Layer(String name){
		this.name = name;
		needsSaving = true;
	}
	public Layer(BinaryFile bin){
		name = bin.getString();
		needsSaving = false;
	}
	public void save(BinaryFile bin){
		needsSaving = false;
		bin.addStringAllocated(name);
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
	}
}
