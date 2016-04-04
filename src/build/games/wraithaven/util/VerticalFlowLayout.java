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
import java.awt.LayoutManager;

/**
 * @author TheDudeFromCI
 */
public class VerticalFlowLayout implements LayoutManager{
	public static final int LEFT_ALIGN = 0;
	public static final int RIGHT_ALIGN = 1;
	public static final int CENTER_ALIGN = 2;
	public static final int FILL_SPACE = 3;
	private int vgap;
	private int align;
	public VerticalFlowLayout(){
		this.vgap = 0;
		this.align = LEFT_ALIGN;
	}
	public VerticalFlowLayout(int vgap){
		this.vgap = vgap;
		this.align = LEFT_ALIGN;
	}
	public VerticalFlowLayout(int vgap, int align){
		this.vgap = vgap;
		this.align = align;
	}
	public void setVGap(int vgap){
		this.vgap = vgap;
	}
	public void setAlignment(int align){
		this.align = align;
	}
	@Override
	public void layoutContainer(Container parent){
		int y = 0;
		int width = parent.getWidth();
		Component[] components = parent.getComponents();
		for(Component c : components){
			if(c.isVisible()){
				Dimension d = c.getPreferredSize();
				switch(align){
					case LEFT_ALIGN:
						c.setBounds(0, y, d.width, d.height);
						break;
					case RIGHT_ALIGN:
						c.setBounds(width-d.width, y, d.width, d.height);
						break;
					case CENTER_ALIGN:
						c.setBounds((width-d.width)/2, y, d.width, d.height);
						break;
					case FILL_SPACE:
						c.setBounds(0, y, width, d.height);
						break;
					default:
						// Resort to the default left alignment.
						c.setBounds(0, y, d.width, d.height);
						break;
				}
				y += d.height+this.vgap;
			}
		}
	}
	@Override
	public Dimension preferredLayoutSize(Container parent){
		int width = 0;
		int height = 0;
		Component[] components = parent.getComponents();
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
	public Dimension minimumLayoutSize(Container parent){
		return new Dimension(0, 0);
	}
	@Override
	public void addLayoutComponent(String name, Component comp){}
	@Override
	public void removeLayoutComponent(Component comp){}
}
