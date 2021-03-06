/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui;

import java.util.ArrayList;
import run.wraith.engine.code.Clickable;
import run.wraith.engine.opengl.renders.ModelInstance;
import wraith.lib.gui.Anchor;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public interface MenuComponent extends Clickable{
	public void dispose();
	public ArrayList<MenuComponent> getChildren();
	public ModelInstance getModel();
	public void load(BinaryFile bin, short version);
	public Anchor getAnchor();
	public Layout getLayout();
	public MenuPosLoc getPositionAndLocation();
	public int getDepth();
}
