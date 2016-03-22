/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import build.games.wraithaven.gui.Anchor;
import build.games.wraithaven.gui.ComponentLayout;
import build.games.wraithaven.gui.Menu;
import build.games.wraithaven.gui.MenuComponent;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.gui.MenuComponentHeirarchy;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class MigLayout implements MenuComponent, ComponentLayout{
	private static final int ID = 2;
	private final ArrayList<MenuComponentHeirarchy> children = new ArrayList(4);
	private final String uuid;
	private final Anchor anchor;
	private boolean collapsed;
	private boolean mousedOver;
	private MenuComponentHeirarchy parent;
	private String name = "Empty Component";
	public MigLayout(String uuid){
		this.uuid = uuid;
		anchor = new Anchor();
		anchor.setSize(20, 20);
	}
	@Override
	public void load(Menu menu, BinaryFile bin, short version){
		switch(version){
			case 0:{
				name = bin.getString();
				anchor.load(bin);
				break;
			}
			default:
				throw new RuntimeException();
		}
	}
	@Override
	public void save(Menu menu, BinaryFile bin){
		bin.addStringAllocated(name);
		anchor.save(bin);
	}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public ArrayList<MenuComponentHeirarchy> getChildren(){
		return children;
	}
	@Override
	public void addChild(MenuComponentHeirarchy com){
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
	public void removeChild(MenuComponentHeirarchy com){
		children.remove(com);
	}
	@Override
	public void setParent(MenuComponentHeirarchy com){
		parent = com;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			{
				// Builder
			}
			@Override
			public JComponent getDefaultFocus(){
				return null;
			}
			@Override
			public void build(MenuComponent component){
				MigLayout c = (MigLayout)component;
			}
		};
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return name;
	}
	@Override
	public void move(MenuComponentHeirarchy com, int index){
		children.remove(com);
		children.add(index, com);
	}
	@Override
	public String getUUID(){
		return uuid;
	}
	@Override
	public Anchor getAnchor(){
		return anchor;
	}
	@Override
	public void draw(Graphics2D g, float x, float y, float w, float h){}
	@Override
	public void updateLayout(){
		// This is just a debug test,
		int i = 0;
		for(MenuComponentHeirarchy h : children){
			if(!(h instanceof MenuComponent)){
				continue;
			}
			MenuComponent c = (MenuComponent)h;
			float width = anchor.getWidth();
			float height = anchor.getHeight();
			switch(i){
				case 0:
					setChildPos(c.getAnchor(), 0, 0, width/2, height/2);
					break;
				case 1:
					setChildPos(c.getAnchor(), width/2, 0, width/2, height/2);
					break;
				case 2:
					setChildPos(c.getAnchor(), 0, height/2, width/2, height/2);
					break;
				case 3:
					setChildPos(c.getAnchor(), width/2, height/2, width/2, height/2);
					break;
				default:
					// Eh, just kinda get rid of you.
					setChildPos(c.getAnchor(), 0, 0, 0, 0);
					break;
			}
			i++;
		}
	}
	private void setChildPos(Anchor a, float x, float y, float w, float h){
		a.setChildPosition(0, 0);
		a.setParentPosition(x/anchor.getWidth(), y/anchor.getHeight());
		a.setSize(w, h);
	}
}
