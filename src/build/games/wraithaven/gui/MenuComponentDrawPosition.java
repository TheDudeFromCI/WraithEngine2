/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.Graphics2D;

/**
 * @author thedudefromci
 */
public class MenuComponentDrawPosition implements Comparable<MenuComponentDrawPosition>{
	private final MenuComponent component;
	private final int depth;
	private final float x;
	private final float y;
	private final float w;
	private final float h;
	public MenuComponentDrawPosition(MenuComponent component, int depth, float x, float y, float w, float h){
		this.component = component;
		this.depth = depth;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	@Override
	public int compareTo(MenuComponentDrawPosition o){
		return Integer.compare(depth, o.depth);
	}
	public void draw(Graphics2D g){
		component.draw(g, x, y, w, h);
	}
}
