/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

/**
 * @author thedudefromci
 */
public class CompResizeDrag{
	private static final int SMALLEST_SIZE = 10;
	private final MenuComponent object;
	private final int mouseXStart;
	private final int mouseYStart;
	private final int corner;
	private final float startWidth;
	private final float startHeight;
	private final float anchorStartX;
	private final float anchorStartY;
	public CompResizeDrag(MenuComponent object, int mouseXStart, int mouseYStart, int corner){
		this.object = object;
		this.mouseXStart = mouseXStart;
		this.mouseYStart = mouseYStart;
		this.corner = corner;
		Anchor a = object.getAnchor();
		startWidth = a.getWidth();
		startHeight = a.getHeight();
		anchorStartX = a.getChildX();
		anchorStartY = a.getChildY();
	}
	public void update(int x, int y, boolean maintainRatio){
		Anchor a = object.getAnchor();
		x = (x-mouseXStart);
		y = (y-mouseYStart);
		if(maintainRatio){
			y = x;
		}
		switch(corner){
			case 0:
				a.setSize(Math.max(startWidth-x, SMALLEST_SIZE), Math.max(startHeight-y, SMALLEST_SIZE));
				a.setChildPosition(1-(1-anchorStartX)*startWidth/a.getWidth(), 1-(1-anchorStartY)*startHeight/a.getHeight());
				break;
			case 1:
				a.setSize(Math.max(startWidth+x, SMALLEST_SIZE), Math.max(startHeight-y, SMALLEST_SIZE));
				a.setChildPosition(anchorStartX*startWidth/a.getWidth(), 1-(1-anchorStartY)*startHeight/a.getHeight());
				break;
			case 2:
				a.setSize(Math.max(startWidth-x, SMALLEST_SIZE), Math.max(startHeight+y, SMALLEST_SIZE));
				a.setChildPosition(1-(1-anchorStartX)*startWidth/a.getWidth(), anchorStartY*startHeight/a.getHeight());
				break;
			case 3:
				a.setSize(Math.max(startWidth+x, SMALLEST_SIZE), Math.max(startHeight+y, SMALLEST_SIZE));
				a.setChildPosition(anchorStartX*startWidth/a.getWidth(), anchorStartY*startHeight/a.getHeight());
				break;
		}
	}
	public String getSize(){
		Anchor a = object.getAnchor();
		return Math.round(a.getWidth())+"x"+Math.round(a.getHeight());
	}
}
