/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine;

import java.io.File;
import run.wraith.engine.core.GameProgramContraints;
import run.wraith.engine.core.MapStyle;
import run.wraith.engine.core.RunProtocol;
import run.wraith.engine.mapstyles.iso.preview.MapPreviewProtocol;
import run.wraith.engine.opengl.loop.MainLoop;
import run.wraith.engine.opengl.loop.WindowInitalizer;
import wraith.lib.util.Algorithms;
import wraith.lib.util.SortedMap;

/**
 * @author thedudefromci
 */
public class WraithGame{
	public static void main(String[] args){
		GameProgramContraints gpc = new GameProgramContraints();
		loadArgs(args, gpc);
		initalizeProgram(gpc);
	}
	private static void loadArgs(String[] args, GameProgramContraints gpc){
		final String DATA = "-data:";
		final String MAP_PREVIEW = "-mapPreview:";
		final String MAP_STYLE = "-mapStyle:";
		// TODO Add game compression!
		// final String COMPRESSED = "-compressed";
		boolean dataSet = false;
		boolean mapStyle = false;
		gpc.softwareArgs = new SortedMap(4);
		for(String s : args){
			if(s.startsWith("-")){
				if(s.startsWith(DATA)){
					if(dataSet){
						System.err.println("Data folder already set!");
						continue;
					}
					dataSet = true;
					String dataFolder = s.substring(DATA.length());
					Algorithms.initalize(dataFolder, new File(".").getAbsolutePath());
					System.out.println("Program data folder set.\n  '"+dataFolder+"'");
				}else if(s.startsWith(MAP_PREVIEW)){
					gpc.mapPreviewMode = true;
					gpc.mapId = s.substring(MAP_PREVIEW.length());
					System.out.println("Running as map preview protocol.\n  Map: '"+gpc.mapId+"'");
				}else if(s.startsWith(MAP_STYLE)){
					s = s.substring(MAP_STYLE.length());
					if(s.equals("iso")){
						gpc.mapStyle = MapStyle.ISOMETRIC;
					}else{
						throw new RuntimeException("Unknown map style: '"+s+"'");
					}
					mapStyle = true;
					System.out.println("Set game map style as "+s+".");
				}else{
					throw new RuntimeException("Unknown program argument: '"+s+"'");
				}
			}else{
				String[] a = s.split(":");
				gpc.softwareArgs.put(a[0], a[1]);
			}
		}
		if(dataSet&&mapStyle){
			System.out.println("Required flags set.");
		}else{
			throw new RuntimeException("Required flags not all set!");
		}
	}
	private static void initalizeProgram(GameProgramContraints gpc){
		if(gpc.mapPreviewMode){
			switch(gpc.mapStyle){
				case ISOMETRIC:
					runGame(new MapPreviewProtocol(gpc.mapId, gpc.softwareArgs));
					return;
				default:
					// Should never be called.
					throw new RuntimeException("Unknown map style!");
			}
		}
		throw new RuntimeException("Unhandled game properties!");
	}
	private static void runGame(RunProtocol protocol){
		MainLoop loop = new MainLoop();
		loop.setFpsCap(60);
		loop.setProtocol(protocol);
		protocol.initalize();
		WindowInitalizer windowInitalizer = new WindowInitalizer(800, 600, false, false, "Wraith Game", protocol.getInputHandler());
		loop.buildWindow(windowInitalizer);
		loop.begin(true);
	}
}
