/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import build.games.wraithaven.gui.Anchor;
import build.games.wraithaven.gui.AutoResizableComponent;
import build.games.wraithaven.gui.Menu;
import build.games.wraithaven.gui.MenuComponent;
import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.gui.MenuComponentHeirarchy;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTextField;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class EmptyComponent implements MenuComponent, AutoResizableComponent{
	private static final int ID = 1;
	private final ArrayList<MenuComponentHeirarchy> children = new ArrayList(4);
	private final String uuid;
	private final Anchor anchor;
	private boolean collapsed;
	private boolean mousedOver;
	private MenuComponentHeirarchy parent;
	private String name = "Empty Component";
	public EmptyComponent(String uuid){
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
				EmptyComponent c = (EmptyComponent)component;
				c.name = nameInput.getText();
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
	public void draw(Graphics2D g, float x, float y, float w, float h){
		g.setColor(Color.blue);
		g.drawLine(Math.round(x), Math.round(y+h/2), Math.round(x+w), Math.round(y+h/2));
		g.setColor(Color.red);
		g.drawLine(Math.round(x+w/2), Math.round(y), Math.round(x+w/2), Math.round(y+h));
	}
	@Override
	public void resize(){}
}
