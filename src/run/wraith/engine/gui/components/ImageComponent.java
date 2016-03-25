/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui.components;

import java.io.File;
import java.util.ArrayList;
import run.wraith.engine.gui.Gui;
import run.wraith.engine.gui.ImageModelInstance;
import run.wraith.engine.gui.Menu;
import run.wraith.engine.gui.MenuComponent;
import run.wraith.engine.opengl.renders.ModelInstance;
import run.wraith.engine.opengl.renders.Texture;
import wraith.lib.gui.Anchor;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class ImageComponent implements MenuComponent{
	private final ArrayList<MenuComponent> children = new ArrayList(4);
	private final ImageModelInstance model;
	private final Anchor anchor;
	public ImageComponent(Gui gui, Menu menu, String uuid){
		Texture texture;
		{
			// Load Texture
			File file = Algorithms.getFile("Menus", menu.getUUID(), uuid+".png");
			if(file.exists()){
				texture = new Texture(file, false);
			}else{
				file = Algorithms.getAsset("No Image.png");
				texture = new Texture(file, false);
			}
		}
		model = new ImageModelInstance(gui.getModel(), texture);
		anchor = new Anchor();
	}
	@Override
	public void load(BinaryFile bin, short version){
		switch(version){
			case 0:{
				bin.getString(); // Name
				bin.getBoolean(); // Is !Default image.
				anchor.load(bin);
				break;
			}
			default:
				throw new RuntimeException("Unknown file version! '"+version+"'");
		}
	}
	@Override
	public void dispose(){
		model.dispose();
	}
	@Override
	public ArrayList<MenuComponent> getChildren(){
		return children;
	}
	@Override
	public ModelInstance getModel(){
		return model;
	}
	@Override
	public Anchor getAnchor(){
		return anchor;
	}
}
