/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine;

import run.wraith.engine.opengl.loop.InputHandler;
import run.wraith.engine.opengl.loop.MainLoop;
import run.wraith.engine.opengl.loop.WindowInitalizer;
import run.wraith.engine.opengl.renders.iso.MapRenderer;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class WraithGame{
	public static void main(String[] args){
		loadArgs(args);
		MapRenderer render = new MapRenderer();
		MainLoop loop = new MainLoop(){
			@Override
			protected void dispose(){}
			@Override
			protected void preloop(){
				render.initalize();
			}
		};
		loop.setFpsCap(60);
		InputHandler input = new InputHandler(){
			@Override
			public void keyPressed(long window, int key, int action){}
			@Override
			public void mouseClicked(long window, int button, int action){}
			@Override
			public void mouseMove(long window, double x, double y){}
			@Override
			public void mouseWheel(long window, double x, double y){}
		};
		WindowInitalizer windowInitalizer = new WindowInitalizer(800, 600, false, false, "Wraith Game", input);
		loop.buildWindow(windowInitalizer);
		loop.begin(render, true);
	}
	private static void loadArgs(String[] args){
		final String DATA = "-data:";
		// TODO Add game compression!
		// final String COMPRESSED = "-compressed:";
		boolean dataSet = false;
		for(String s : args){
			if(s.startsWith(DATA)){
				if(dataSet){
					System.err.println("Data folder already set!");
					continue;
				}
				dataSet = true;
				String dataFolder = s.substring(DATA.length());
				Algorithms.initalize(dataFolder, null);
				System.out.println("Program data folder set.\n  '"+dataFolder+"'");
			}else{
				System.err.println("Unknown program argument: '"+s+"'");
			}
		}
		if(dataSet){
			System.out.println("Required flags set.");
		}else{
			System.err.println("Required flags not all set!");
			System.exit(1);
		}
	}
}
