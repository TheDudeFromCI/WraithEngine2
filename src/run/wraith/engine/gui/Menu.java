/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui;

import java.util.ArrayList;
import org.joml.Matrix4f;
import run.wraith.engine.gui.components.EmptyComponent;
import run.wraith.engine.gui.components.ImageComponent;
import run.wraith.engine.opengl.renders.ModelInstance;
import run.wraith.engine.opengl.renders.Universe;
import wraith.lib.gui.Anchor;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Menu{
	private final ArrayList<MenuComponent> children = new ArrayList(4);
	private final String uuid;
	private final Gui gui;
	public Menu(Gui gui, String uuid){
		this.gui = gui;
		this.uuid = uuid;
		load(uuid);
	}
	private void load(String uuid){
		try{
			BinaryFile bin = new BinaryFile(Algorithms.getFile("Menus", uuid+".dat"));
			bin.decompress(true);
			short version = bin.getShort();
			switch(version){
				case 1:{
					bin.getString(); // Menu Name (Not Important)
					loadChildren(bin, children, version, 0);
					break;
				}
				default:
					throw new RuntimeException("Unknown file version! '"+version+"'");
			}
			updateComponentLayouts();
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
		}
	}
	private void loadChildren(BinaryFile bin, ArrayList<MenuComponent> compList, short version, int depth){
		int childCount = bin.getInt();
		for(int i = 0; i<childCount; i++){
			int compId = bin.getInt();
			String compUuid = bin.getString();
			MenuComponent comp = newComponent(gui, compId, compUuid, depth);
			comp.load(bin, version);
			compList.add(comp);
			loadChildren(bin, comp.getChildren(), version, depth+1);
		}
	}
	private MenuComponent newComponent(Gui gui, int typeId, String uuid, int depth){
		switch(typeId){
			case 0:
				return new ImageComponent(gui, this, uuid, depth);
			case 1:
				return new EmptyComponent();
			default:
				throw new RuntimeException("Unknown component type! '"+typeId+"'");
		}
	}
	public void dispose(){
		dispose(null, children);
	}
	private void dispose(MenuComponent comp, ArrayList<MenuComponent> children){
		if(comp!=null){
			comp.dispose();
		}
		for(MenuComponent m : children){
			m.dispose();
		}
	}
	public void addAllComponents(Universe universe){
		addAllComponents(universe, children);
	}
	private void addAllComponents(Universe universe, ArrayList<MenuComponent> children){
		for(MenuComponent c : children){
			ModelInstance model = c.getModel();
			if(model!=null){
				universe.addModel(model);
			}
			addAllComponents(universe, c.getChildren());
		}
	}
	public void removeAllComponents(Universe universe){
		removeAllComponents(universe, children);
	}
	private void removeAllComponents(Universe universe, ArrayList<MenuComponent> children){
		for(MenuComponent c : children){
			ModelInstance model = c.getModel();
			if(model!=null){
				universe.removeModel(model);
			}
			removeAllComponents(universe, c.getChildren());
		}
	}
	public String getUUID(){
		return uuid;
	}
	public void updateComponentLayouts(){
		updateHeirarchy(children, null, 0, 0, gui.getWidth(), gui.getHeight());
	}
	private void updateHeirarchy(ArrayList<MenuComponent> children, MenuComponent current, float x, float y, float w, float h){
		if(current!=null){
			Anchor a = current.getAnchor();
			x = x+w*a.getParentX()-a.getWidth()*a.getChildX();
			y = y+h*a.getParentY()-a.getHeight()*a.getChildY();
			w = a.getWidth();
			h = a.getHeight();
			ModelInstance model = current.getModel();
			if(model!=null){
				Matrix4f pos = model.getPosition();
				pos.identity();
				pos.translate(x, y, 0);
				pos.scale(w, h, 0);
			}
			if(current.getLayout()!=null){
				current.getLayout().updateLayout(current.getAnchor(), current.getChildren());
			}
		}
		for(MenuComponent child : children){
			updateHeirarchy(child.getChildren(), child, x, y, w, h);
		}
	}
}
