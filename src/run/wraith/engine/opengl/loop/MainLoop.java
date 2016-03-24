/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.loop;

import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import run.wraith.engine.core.RunProtocol;

/**
 * @author thedudefromci
 */
public class MainLoop{
	private GLFWCursorPosCallback cursorPosCallback;
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback scrollCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
	private long window;
	private boolean windowOpen = false;
	private int fpsCap = 30;
	private boolean rebuildWindow = false;
	private WindowInitalizer nextWindowStats;
	private RunProtocol runProtocol;
	public RunProtocol getProtocol(){
		return runProtocol;
	}
	public void setProtocol(RunProtocol protocol){
		runProtocol = protocol;
	}
	/**
	 * This starts Open GL. This blocks until OpenGL terminates. Must be called on main thread. A window must be visible when this is called.
	 *
	 * @param exitGame
	 *            - Whether or not the program should exit when OpenGL is terminated. If true, the program will auto exist. If false, OpenGL is cleaned
	 *            up, and this method returns as normal.
	 */
	public void begin(boolean exitGame){
		try{
			loop();
		}catch(Exception exception){
			exception.printStackTrace();
		}
		cleanup();
		if(exitGame){
			System.exit(0);
		}
	}
	/**
	 * This creates the game window. If a window already exists, it disposes the old one, and replaces it. Must be called on main thread.
	 *
	 * @param windowInitalizer
	 *            - The window properties.
	 */
	public void buildWindow(WindowInitalizer windowInitalizer){
		if(windowOpen){
			rebuildWindow = true;
			nextWindowStats = windowInitalizer;
			return;
		}
		window(windowInitalizer);
	}
	public int getFpsCap(){
		return fpsCap;
	}
	public long getWindowId(){
		return window;
	}
	public void setFpsCap(int cap){
		fpsCap = cap;
	}
	private void cleanup(){
		runProtocol.dispose();
		keyCallback.release();
		mouseButtonCallback.release();
		cursorPosCallback.release();
		scrollCallback.release();
		windowSizeCallback.release();
		errorCallback.release();
		destoryWindow();
	}
	private void destoryWindow(){
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	private void loop(){
		runProtocol.preLoop();
		double lastFrameTime = 0;
		double currentFrameTime;
		double delta;
		long sleepTime;
		long yieldTime;
		long overSleep;
		long variableYieldTime = 0;
		long lastTime = 0;
		long t;
		do{
			RenderLoop renderLoop = runProtocol.getRenderLoop();
			rebuildWindow = false;
			while(glfwWindowShouldClose(window)==GL_FALSE&&!rebuildWindow){
				currentFrameTime = glfwGetTime();
				delta = currentFrameTime-lastFrameTime;
				lastFrameTime = currentFrameTime;
				renderLoop.update(delta, currentFrameTime);
				renderLoop.render();
				glfwSwapBuffers(window);
				glfwPollEvents();
				if(fpsCap>0){
					sleepTime = 1000000000/fpsCap;
					yieldTime = Math.min(sleepTime, variableYieldTime+sleepTime%(1000*1000));
					overSleep = 0;
					try{
						while(true){
							t = System.nanoTime()-lastTime;
							if(t<sleepTime-yieldTime){
								Thread.sleep(1);
							}else if(t<sleepTime){
								Thread.yield();
							}else{
								overSleep = t-sleepTime;
								break;
							}
						}
					}catch(InterruptedException e){
						// We don't need to hear you rant.
					}finally{
						lastTime = System.nanoTime()-Math.min(overSleep, sleepTime);
						if(overSleep>variableYieldTime){
							variableYieldTime = Math.min(variableYieldTime+200*1000, sleepTime);
						}else if(overSleep<variableYieldTime-200*1000){
							variableYieldTime = Math.max(variableYieldTime-2*1000, 0);
						}
					}
				}
			}
			if(rebuildWindow){
				destoryWindow();
				window(nextWindowStats);
			}
		}while(rebuildWindow);
	}
	private void window(final WindowInitalizer windowInitalizer){
		windowOpen = true;
		nextWindowStats = null;
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		if(glfwInit()!=GL11.GL_TRUE){
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		window = GLFW.glfwCreateWindow(windowInitalizer.getWindowWidth(), windowInitalizer.getWindowHeight(), windowInitalizer.getWindowTitle(),
			windowInitalizer.isFullscreen()?glfwGetPrimaryMonitor():NULL, NULL);
		if(window==NULL){
			throw new RuntimeException("Failed to create the GLFW window");
		}
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback(){
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods){
				windowInitalizer.getInputHandler().keyPressed(window, key, action);
			}
		});
		glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback(){
			@Override
			public void invoke(long window, int button, int action, int mods){
				windowInitalizer.getInputHandler().mouseClicked(window, button, action);
			}
		});
		glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback(){
			@Override
			public void invoke(long window, double xpos, double ypos){
				windowInitalizer.getInputHandler().mouseMove(window, xpos, ypos);
			}
		});
		glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset){
				windowInitalizer.getInputHandler().mouseWheel(window, xoffset, yoffset);
			}
		});
		glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback(){
			@Override
			public void invoke(long window, int width, int height){
				runProtocol.getRenderLoop().windowResized(width, height);
			}
		});
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if(!windowInitalizer.isFullscreen()){
			glfwSetWindowPos(window, (vidmode.width()-windowInitalizer.getWindowWidth())/2, (vidmode.height()-windowInitalizer.getWindowHeight())/2);
		}
		glfwMakeContextCurrent(window);
		glfwSwapInterval(windowInitalizer.isvSync()?1:0);
		glfwShowWindow(window);
		GL.createCapabilities();
	}
}
