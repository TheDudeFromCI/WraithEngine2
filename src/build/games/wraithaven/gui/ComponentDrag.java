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
public class ComponentDrag{
	private final MenuEditor menuEditor;
	private final MenuComponent component;
	private final float startX;
	private final float startY;
	private final int mouseXStart;
	private final int mouseYStart;
	private boolean active;
	public ComponentDrag(MenuEditor menuEditor, MenuComponent component, int mouseX, int mouseY){
		this.menuEditor = menuEditor;
		this.component = component;
		Anchor a = component.getAnchor();
		startX = a.getParentX();
		startY = a.getParentY();
		mouseXStart = mouseX;
		mouseYStart = mouseY;
	}
	public MenuComponent getComponent(){
		return component;
	}
	public float getStartX(){
		return startX;
	}
	public float getStartY(){
		return startY;
	}
	public void setPosition(float x, float y){
		Anchor a = component.getAnchor();
		a.setParentPosition(x, y);
	}
	public void reset(){
		Anchor a = component.getAnchor();
		a.setParentPosition(startX, startY);
	}
	public void updatePosition(int mouseX, int mouseY, int screenWidth, int screenHeight){
		if(!active){
			if(Math.abs(mouseX-mouseXStart)>=3||Math.abs(mouseY-mouseYStart)>=3){
				active = true;
			}else{
				return;
			}
		}
		float parentWidth, parentHeight;
		{
			// If parent is screen, use screen size. Otherwise use component size.
			if(component.getParent() instanceof MenuComponent){
				Anchor a = ((MenuComponent)component.getParent()).getAnchor();
				parentWidth = a.getWidth();
				parentHeight = a.getHeight();
			}else{
				parentWidth = screenWidth;
				parentHeight = screenHeight;
			}
		}
		float x = (mouseX-mouseXStart)/parentWidth+startX;
		float y = (mouseY-mouseYStart)/parentHeight+startY;
		setPosition(x, y);
		menuEditor.updateAllLayouts();
	}
}
