/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso.preview;

import org.lwjgl.glfw.GLFW;
import run.wraith.engine.mapstyles.iso.Map;
import run.wraith.engine.opengl.loop.InputHandler;

/**
 * @author thedudefromci
 */
public class MapPreviewInputHandler implements InputHandler{
	private Map map;
	private boolean mouseDown;
	private double mouseX;
	private double mouseY;
	private double downX;
	private double downY;
	private double scrollX;
	private double scrollY;
	private double scrollXStart;
	private double scrollYStart;
	public void loadMap(Map map){
		this.map = map;
	}
	@Override
	public void keyPressed(long window, int key, int action){}
	@Override
	public void mouseClicked(long window, int button, int action){
		if(map==null){
			return;
		}
		mouseDown = action==GLFW.GLFW_PRESS;
		if(mouseDown){
			downX = mouseX;
			downY = mouseY;
			scrollXStart = scrollX;
			scrollYStart = scrollY;
		}
	}
	@Override
	public void mouseMove(long window, double x, double y){
		if(map==null){
			return;
		}
		mouseX = x;
		mouseY = y;
		if(!mouseDown){
			return;
		}
		scrollX = mouseX-downX+scrollXStart;
		scrollY = mouseY-downY+scrollYStart;
		map.getCamera().moveTo((float)(-scrollX), (float)(-scrollY), 0);
	}
	@Override
	public void mouseWheel(long window, double x, double y){}
}
