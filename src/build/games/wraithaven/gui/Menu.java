/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Menu implements MenuComponentHeirarchy{
	private static final short FILE_VERSION = 0;
	private final ArrayList<MenuComponentHeirarchy> components = new ArrayList(8);
	private final String uuid;
	private String name;
	private boolean collapsed;
	private boolean mousedOver;
	public Menu(String uuid, String name){
		this.uuid = uuid;
		this.name = name;
	}
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
	public void load(){
		String previousName = name;
		File file = Algorithms.getFile("Menus", uuid+".dat");
		if(!file.exists()){
			// Under most conditions, this file should exist.
			return;
		}
		try{
			BinaryFile bin = new BinaryFile(file);
			bin.decompress(true);
			short version = bin.getShort();
			switch(version){
				case 0:{
					name = bin.getString();
					components.clear();
					int componentCount = bin.getInt();
					for(int i = 0; i<componentCount; i++){
						MenuComponent com = MenuComponentFactory.newInstance(bin.getInt());
						com.load(bin, version);
						components.add(com);
						com.setParent(this);
						loadChildren(bin, com, version);
					}
					break;
				}
				default:
					throw new RuntimeException();
			}
		}catch(Exception exception){
			exception.printStackTrace();
			int response = JOptionPane.showConfirmDialog(null, "There has been an error attempting to load this file. Delete this file?", "Error",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if(response==JOptionPane.YES_OPTION){
				file.delete();
			}
			// And clean up.
			name = previousName;
			components.clear();
		}
	}
	private void loadChildren(BinaryFile bin, MenuComponent parent, short version){
		int childCount = bin.getInt();
		for(int i = 0; i<childCount; i++){
			MenuComponent com = MenuComponentFactory.newInstance(bin.getInt());
			com.load(bin, version);
			parent.addChild(com);
			com.setParent(parent);
			loadChildren(bin, com, version);
		}
	}
	public void save(){
		BinaryFile bin = new BinaryFile(6);
		bin.addShort(FILE_VERSION);
		bin.addStringAllocated(name);
		bin.addInt(components.size());
		for(MenuComponentHeirarchy com : components){
			saveHeirarchy(bin, (MenuComponent)com);
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("Menus", uuid+".dat"));
	}
	private void saveHeirarchy(BinaryFile bin, MenuComponent com){
		bin.allocateBytes(8);
		bin.addInt(com.getId());
		com.save(bin);
		bin.addInt(com.getChildren().size());
		for(MenuComponentHeirarchy c : com.getChildren()){
			saveHeirarchy(bin, (MenuComponent)c);
		}
	}
	public void dispose(){
		components.clear();
	}
	public String getUUID(){
		return uuid;
	}
	@Override
	public ArrayList<MenuComponentHeirarchy> getChildren(){
		return components;
	}
	@Override
	public void addChild(MenuComponentHeirarchy com){
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
	public void removeChild(MenuComponentHeirarchy com){
		components.remove(com);
	}
	@Override
	public void setParent(MenuComponentHeirarchy com){}
}
