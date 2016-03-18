/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import build.games.wraithaven.core.InputDialogBuilder;
import build.games.wraithaven.gui.MenuComponent;
import build.games.wraithaven.gui.MenuComponentHeirarchy;
import java.util.ArrayList;
import javax.swing.JComponent;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class ImageComponent implements MenuComponent{
	private static final int ID = 0;
	private final ArrayList<MenuComponent> children = new ArrayList(4);
	private boolean collapsed;
	private boolean mousedOver;
	private MenuComponentHeirarchy parent;
	private String name = "Image Component";
	@Override
	public void load(BinaryFile bin, short version){}
	@Override
	public void save(BinaryFile bin){}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public ArrayList<MenuComponent> getChildren(){
		return children;
	}
	@Override
	public void addChild(MenuComponent com){
		children.add(com);
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
		return parent;
	}
	@Override
	public void removeChild(MenuComponent com){
		children.remove(com);
	}
	@Override
	public void setParent(MenuComponentHeirarchy com){
		parent = com;
	}
	@Override
	public InputDialogBuilder getCreationDialog(){
		return new InputDialogBuilder(){
			{
				// Builder
			}
			@Override
			public JComponent getDefaultFocus(){
				return null;
			}
		};
	}
	@Override
	public String getName(){
		return name;
	}
}
