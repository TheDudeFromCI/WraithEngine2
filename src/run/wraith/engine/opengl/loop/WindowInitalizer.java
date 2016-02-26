/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.loop;

/**
 * @author thedudefromci
 */
public class WindowInitalizer{
	private final int windowWidth;
	private final int windowHeight;
	private final boolean fullscreen;
	private final boolean vSync;
	private final String windowTitle;
	private final InputHandler inputHandler;
	public WindowInitalizer(int windowWidth, int windowHeight, boolean fullscreen, boolean vSync, String windowTitle, InputHandler inputHandler){
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.fullscreen = fullscreen;
		this.vSync = vSync;
		this.windowTitle = windowTitle;
		this.inputHandler = inputHandler;
	}
	public int getWindowHeight(){
		return windowHeight;
	}
	public String getWindowTitle(){
		return windowTitle;
	}
	public int getWindowWidth(){
		return windowWidth;
	}
	public boolean isFullscreen(){
		return fullscreen;
	}
	public boolean isvSync(){
		return vSync;
	}
	public InputHandler getInputHandler(){
		return inputHandler;
	}
}
