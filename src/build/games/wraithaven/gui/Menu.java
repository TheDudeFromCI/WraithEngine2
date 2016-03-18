/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.util.ArrayList;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Menu implements MenuComponentHeirarchy{
	private final ArrayList<MenuComponent> components = new ArrayList(8);
	private String name;
	private boolean collapsed;
	private boolean mousedOver;
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return name;
	}
	public void load(BinaryFile bin){
		name = bin.getString();
		components.clear();
		int componentCount = bin.getInt();
		for(int i = 0; i<componentCount; i++){
			MenuComponent com = MenuComponentFactory.newInstance(bin.getInt());
			com.load(bin);
			components.add(com);
		}
	}
	public void save(BinaryFile bin){
		bin.addStringAllocated(name);
		bin.allocateBytes(4+4*components.size());
		bin.addInt(components.size());
		for(MenuComponent com : components){
			bin.addInt(com.getId());
			com.save(bin);
		}
	}
	@Override
	public ArrayList<MenuComponent> getChildren(){
		return components;
	}
	@Override
	public void addChild(MenuComponent com){
		components.add(com);
	}
	@Override
	public boolean isCollapsed(){
		return collapsed;
	}
	@Override
	public void setCollapsed(boolean collapsed){
		this.collapsed = collapsed;
	}
	@Override
	public boolean isMousedOver(){
		return mousedOver;
	}
	@Override
	public void setMousedOver(boolean mousedOver){
		this.mousedOver = mousedOver;
	}
	@Override
	public MenuComponentHeirarchy getParent(){
		return null;
	}
	@Override
	public void removeChild(MenuComponent com){
		components.remove(com);
	}
}
