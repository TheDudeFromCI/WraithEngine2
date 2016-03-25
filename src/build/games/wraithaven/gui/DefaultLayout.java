/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import wraith.lib.gui.Anchor;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTextField;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public abstract class DefaultLayout implements MenuComponent, ComponentLayout{
	protected final ArrayList<MenuComponentHeirarchy> children = new ArrayList(4);
	protected final String uuid;
	protected final Anchor anchor;
	protected boolean collapsed;
	protected boolean mousedOver;
	protected MenuComponentHeirarchy parent;
	protected String name = "Empty Layout";
	public DefaultLayout(String uuid){
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
			private final JTextField nameInput;
			{
				// Builder
				setLayout(new VerticalFlowLayout(0, 5));
				{
					// Name
					nameInput = new JTextField();
					nameInput.setColumns(20);
					nameInput.setText(name);
					add(nameInput);
				}
			}
			@Override
			public JComponent getDefaultFocus(){
				return nameInput;
			}
			@Override
			public void build(MenuComponent component){
				DefaultLayout c = (DefaultLayout)component;
				c.name = name;
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
	protected void setChildPos(Anchor a, float x, float y, float w, float h){
		a.setChildPosition(0, 0);
		a.setParentPosition(x/anchor.getWidth(), y/anchor.getHeight());
		a.setSize(w, h);
	}
}
