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
	public void update(int x, int y){
		Anchor a = object.getAnchor();
		switch(corner){
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				a.setSize(startWidth+(x-mouseXStart), startHeight+(y-mouseYStart));
				a.setChildPosition(anchorStartX*startWidth/a.getWidth(), anchorStartY*startHeight/a.getHeight());
				break;
		}
	}
}
