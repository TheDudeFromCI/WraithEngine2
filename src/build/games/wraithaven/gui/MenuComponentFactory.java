/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.gui.components.EmptyComponent;
import build.games.wraithaven.gui.components.ImageComponent;
import build.games.wraithaven.gui.components.MigLayout;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class MenuComponentFactory{
	public static MenuComponent newComponentInstance(int id, String uuid, short version, Menu menu, BinaryFile bin){
		switch(version){
			case 0:{
				switch(id){
					case 0:{
						ImageComponent comp = new ImageComponent(uuid);
						comp.load(menu, bin, version);
						return comp;
					}
					case 1:{
						EmptyComponent comp = new EmptyComponent(uuid);
						comp.load(menu, bin, version);
						return comp;
					}
					case 2:{
						EmptyComponent comp = new EmptyComponent(Algorithms.randomUUID());
						MigLayout layout = new MigLayout();
						comp.setLayout(layout);
						comp.load(menu, bin, version);
						layout.loadLayout(menu, bin, version);
						return comp;
					}
					default:
						throw new RuntimeException("Id: "+id);
				}
			}
			case 1:{
				switch(id){
					case 0:{
						ImageComponent comp = new ImageComponent(uuid);
						comp.load(menu, bin, version);
						return comp;
					}
					case 1:{
						EmptyComponent comp = new EmptyComponent(uuid);
						comp.load(menu, bin, version);
						return comp;
					}
					default:
						throw new RuntimeException("Id: "+id);
				}
			}
			default:
				throw new RuntimeException("Version: "+version);
		}
	}
	public static ComponentLayout newLayoutInstance(int id, short version, Menu menu, BinaryFile bin){
		switch(version){
			case 0:
			case 1:
				switch(id){
					case 0:{
						MigLayout layout = new MigLayout();
						layout.loadLayout(menu, bin, version);
						return layout;
					}
					default:
						throw new RuntimeException("Id: "+id);
				}
			default:
				throw new RuntimeException("Version: "+version);
		}
	}
}
