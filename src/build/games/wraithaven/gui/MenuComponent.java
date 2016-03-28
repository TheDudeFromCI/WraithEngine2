/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.Graphics2D;
import wraith.lib.gui.Anchor;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public interface MenuComponent extends MenuComponentHeirarchy{
	public void load(Menu menu, BinaryFile bin, short version);
	public void save(Menu menu, BinaryFile bin);
	public int getId();
	public MenuComponentDialog getCreationDialog();
	public String getName();
	public String getUUID();
	public Anchor getAnchor();
	public void draw(Graphics2D g, float x, float y, float w, float h);
	public ComponentLayout getLayout();
	public void setLayout(ComponentLayout layout);
}
