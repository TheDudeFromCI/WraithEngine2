/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.util.ArrayList;

/**
 * @author thedudefromci
 */
public interface MenuComponentHeirarchy{
	public ArrayList<MenuComponentHeirarchy> getChildren();
	public void addChild(MenuComponentHeirarchy com);
	public boolean isCollapsed();
	public void setCollapsed(boolean collapsed);
	public boolean isMousedOver();
	public void setMousedOver(boolean mousedOver);
	public MenuComponentHeirarchy getParent();
	public void removeChild(MenuComponentHeirarchy com);
	public void setParent(MenuComponentHeirarchy com);
	public void move(MenuComponentHeirarchy com, int index);
}
