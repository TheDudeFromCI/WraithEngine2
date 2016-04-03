/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * @author thedudefromci
 */
public class InverseBorderLayout implements LayoutManager{
	private int hgap;
	public InverseBorderLayout(){
		hgap = 0;
	}
	public InverseBorderLayout(int hgap){
		this.hgap = hgap;
	}
	@Override
	public void addLayoutComponent(String name, Component comp){}
	@Override
	public void removeLayoutComponent(Component comp){}
	@Override
	public Dimension preferredLayoutSize(Container parent){
		int compCount = parent.getComponentCount();
		Component left = compCount>=1?parent.getComponent(0):null;
		Component right = compCount>=2?parent.getComponent(1):null;
		Component center = compCount>=3?parent.getComponent(2):null;
		Component bottom = compCount>=4?parent.getComponent(3):null;
		int width = 0;
		int height = 0;
		int sideWidth = 0;
		Dimension pref;
		if(left!=null){
			pref = left.getPreferredSize();
			sideWidth = Math.max(sideWidth, pref.width);
			height = Math.max(height, pref.height);
		}
		if(center!=null){
			pref = center.getPreferredSize();
			width += pref.width;
			height = Math.max(height, pref.height);
			width += hgap*2;
		}
		if(right!=null){
			pref = right.getPreferredSize();
			sideWidth = Math.max(sideWidth, pref.width);
			height = Math.max(height, pref.height);
		}
		if(bottom!=null){
			pref = bottom.getPreferredSize();
			height += pref.height;
		}
		width += sideWidth*2;
		return new Dimension(width, height);
	}
	@Override
	public Dimension minimumLayoutSize(Container parent){
		return new Dimension(0, 0);
	}
	@Override
	public void layoutContainer(Container parent){
		int width = parent.getWidth();
		int height = parent.getHeight();
		int extraWidth = width;
		int compCount = parent.getComponentCount();
		Component left = compCount>=1?parent.getComponent(0):null;
		Component right = compCount>=2?parent.getComponent(1):null;
		Component center = compCount>=3?parent.getComponent(2):null;
		Component bottom = compCount>=4?parent.getComponent(3):null;
		if(bottom!=null&&bottom.isVisible()){
			Dimension pref = bottom.getPreferredSize();
			height -= pref.height;
			bottom.setBounds(0, height, width, pref.height);
		}
		if(center!=null&&center.isVisible()){
			Dimension pref = center.getPreferredSize();
			center.setBounds((width-(pref.width))/2, 0, pref.width, height);
			extraWidth -= pref.width+hgap*2;
		}
		extraWidth /= 2;
		if(left!=null&&left.isVisible()){
			left.setBounds(0, 0, extraWidth, height);
		}
		if(right!=null&&right.isVisible()){
			right.setBounds(width-extraWidth, 0, extraWidth, height);
		}
	}
}
