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
	private static final int CROSSHAIR_SIZE = 7;
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
		x += w/2;
		y += h/2;
		g.setColor(Color.blue);
		g.drawLine(Math.round(x-CROSSHAIR_SIZE), Math.round(y), Math.round(x+CROSSHAIR_SIZE), Math.round(y));
		g.setColor(Color.red);
		g.drawLine(Math.round(x), Math.round(y-CROSSHAIR_SIZE), Math.round(x), Math.round(y+CROSSHAIR_SIZE));
	}
	@Override
	public void resize(float parentWidth, float parentHeight){
		if(children.isEmpty()){
			anchor.setSize(20, 20);
			anchor.setChildPosition(0.5f, 0.5f);
			return;
		}
		float[] bounds = new float[]{
			Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE
		};
		findBounds(this, bounds, 0, 0, anchor.getWidth(), anchor.getHeight());
		MenuComponent c;
		Anchor a;
		float x, y;
		float w2 = bounds[2]-bounds[0];
		float h2 = bounds[3]-bounds[1];
		for(MenuComponentHeirarchy com : children){
			if(com instanceof MenuComponent){
				c = (MenuComponent)com;
				a = c.getAnchor();
				// X2 = (W1*X1-O)/W2
				x = (anchor.getWidth()*a.getParentX()-bounds[0])/w2;
				y = (anchor.getHeight()*a.getParentY()-bounds[1])/h2;
				a.setParentPosition(x, y);
			}
		}
		x = (parentWidth*anchor.getParentX()-anchor.getWidth()*anchor.getChildX())/parentWidth;
		y = (parentHeight*anchor.getParentY()-anchor.getHeight()*anchor.getChildY())/parentHeight;
		float x2 = x+bounds[0]/parentWidth;
		float y2 = y+bounds[1]/parentHeight;
		x = x2+w2*anchor.getChildX()/parentWidth;
		y = y2+h2*anchor.getChildY()/parentHeight;
		anchor.setParentPosition(x, y);
		anchor.setSize(w2, h2);
	}
	private void findBounds(MenuComponentHeirarchy c, float[] bounds, float x, float y, float w, float h){
		if(c!=this&&c instanceof MenuComponent){
			Anchor a = ((MenuComponent)c).getAnchor();
			x = x+w*a.getParentX()-a.getWidth()*a.getChildX();
			y = y+h*a.getParentY()-a.getHeight()*a.getChildY();
			w = a.getWidth();
			h = a.getHeight();
			bounds[0] = Math.min(bounds[0], x);
			bounds[1] = Math.min(bounds[1], y);
			bounds[2] = Math.max(bounds[2], x+w);
			bounds[3] = Math.max(bounds[3], y+h);
		}
		for(MenuComponentHeirarchy c2 : c.getChildren()){
			findBounds(c2, bounds, x, y, w, h);
		}
	}
}
