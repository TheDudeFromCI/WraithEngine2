/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui.components;

import java.util.ArrayList;
import run.wraith.engine.gui.MenuComponent;
import run.wraith.engine.opengl.renders.ModelInstance;
import wraith.lib.gui.Anchor;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class EmptyComponent implements MenuComponent{
	private final ArrayList<MenuComponent> children = new ArrayList(4);
	private final Anchor anchor;
	public EmptyComponent(){
		anchor = new Anchor();
	}
	@Override
	public void dispose(){}
	@Override
	public ArrayList<MenuComponent> getChildren(){
		return children;
	}
	@Override
	public ModelInstance getModel(){
		return null;
	}
	@Override
	public void load(BinaryFile bin, short version){
		switch(version){
			case 0:{
				bin.getString();// Name
				anchor.load(bin);
				break;
			}
			default:
				throw new RuntimeException("Unknown file version! '"+version+"'");
		}
	}
	@Override
	public Anchor getAnchor(){
		return anchor;
	}
}