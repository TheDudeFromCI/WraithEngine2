/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class VerticalFlowLayout implements LayoutManager2{
	private final ArrayList<Component> components = new ArrayList(4);
	private int hgap = 0;
	private int vgap = 0;
	public VerticalFlowLayout(){}
	public VerticalFlowLayout(int hgap, int vgap){
		this.hgap = hgap;
		this.vgap = vgap;
	}
	public void setHGap(int hgap){
		this.hgap = hgap;
	}
	public void setVGap(int vgap){
		this.vgap = vgap;
	}
	@Override
	public void addLayoutComponent(Component comp, Object constraints){
		this.components.add(comp);
	}
	@Override
	public float getLayoutAlignmentX(Container target){
		return 0;
	}
	@Override
	public float getLayoutAlignmentY(Container target){
		return 0;
	}
	@Override
	public void invalidateLayout(Container target){}
	@Override
	public void addLayoutComponent(String name, Component comp){
		this.components.add(comp);
	}
	@Override
	public void layoutContainer(Container parent){
		int x = 0;
		int y = 0;
		int columnWidth = 0;
		for(Component c : this.components){
			if(c.isVisible()){
				Dimension d = c.getPreferredSize();
				columnWidth = Math.max(columnWidth, d.width);
				if(y+d.height>parent.getHeight()){
					x += columnWidth+this.hgap;
					y = 0;
				}
				c.setBounds(x, y, d.width, d.height);
				y += d.height+this.vgap;
			}
		}
	}
	@Override
	public Dimension minimumLayoutSize(Container parent){
		return new Dimension(0, 0);
	}
	@Override
	public Dimension preferredLayoutSize(Container parent){
		int width = 0;
		int height = 0;
		for(Component c : components){
			width = (int)Math.max(width, c.getPreferredSize().getWidth());
			if(height>0){
				height += vgap;
			}
			height += c.getPreferredSize().getHeight();
		}
		return new Dimension(width, height);
	}
	@Override
	public Dimension maximumLayoutSize(Container target){
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	@Override
	public void removeLayoutComponent(Component comp){
		this.components.remove(comp);
	}
}
