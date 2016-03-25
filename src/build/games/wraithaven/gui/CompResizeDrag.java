/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import wraith.lib.gui.Anchor;

/**
 * @author thedudefromci
 */
public class CompResizeDrag{
	private static final int SMALLEST_SIZE = 10;
	private final MenuEditor menuEditor;
	private final MenuComponent object;
	private final int mouseXStart;
	private final int mouseYStart;
	private final int corner;
	private final float parentWidth;
	private final float parentHeight;
	private final float startWidth;
	private final float startHeight;
	private final float anchorStartX;
	private final float anchorStartY;
	private final float parentStartX;
	private final float parentStartY;
	public CompResizeDrag(
		MenuEditor menuEditor, MenuComponent object, int mouseXStart, int mouseYStart, int corner, float parentWidth, float parentHeight){
		this.menuEditor = menuEditor;
		this.object = object;
		this.mouseXStart = mouseXStart;
		this.mouseYStart = mouseYStart;
		this.corner = corner;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		Anchor a = object.getAnchor();
		startWidth = a.getWidth();
		startHeight = a.getHeight();
		anchorStartX = a.getChildX();
		anchorStartY = a.getChildY();
		parentStartX = a.getParentX();
		parentStartY = a.getParentY();
	}
	public void update(int x, int y, boolean maintainRatio){
		Anchor a = object.getAnchor();
		x = (x-mouseXStart);
		y = (y-mouseYStart);
		if(maintainRatio&&corner!=-1){
			// Make sure we scale evenly.
			// Ignore this if we are moving the center.
			y = x;
		}
		switch(corner){
			case -1: // Center
				float px, py;
				px = anchorStartX+x/startWidth;
				py = anchorStartY+y/startHeight;
				px = Math.round(px*20)/20f;
				py = Math.round(py*20)/20f;
				a.setChildPosition(px, py);
				px = (parentWidth*parentStartX-startWidth*anchorStartX+a.getChildX()*startWidth)/parentWidth;
				py = (parentHeight*parentStartY-startHeight*anchorStartY+a.getChildY()*startHeight)/parentHeight;
				a.setParentPosition(px, py);
				break;
			case 0: // Top Left
				a.setSize(Math.max(startWidth-x, SMALLEST_SIZE), Math.max(startHeight-y, SMALLEST_SIZE));
				a.setChildPosition(1-(1-anchorStartX)*startWidth/a.getWidth(), 1-(1-anchorStartY)*startHeight/a.getHeight());
				break;
			case 1:// Top Right
				a.setSize(Math.max(startWidth+x, SMALLEST_SIZE), Math.max(startHeight-y, SMALLEST_SIZE));
				a.setChildPosition(anchorStartX*startWidth/a.getWidth(), 1-(1-anchorStartY)*startHeight/a.getHeight());
				break;
			case 2:// Bottom Left
				a.setSize(Math.max(startWidth-x, SMALLEST_SIZE), Math.max(startHeight+y, SMALLEST_SIZE));
				a.setChildPosition(1-(1-anchorStartX)*startWidth/a.getWidth(), anchorStartY*startHeight/a.getHeight());
				break;
			case 3: // Bottom Right
				a.setSize(Math.max(startWidth+x, SMALLEST_SIZE), Math.max(startHeight+y, SMALLEST_SIZE));
				a.setChildPosition(anchorStartX*startWidth/a.getWidth(), anchorStartY*startHeight/a.getHeight());
				break;
		}
		menuEditor.updateAllLayouts();
	}
}
