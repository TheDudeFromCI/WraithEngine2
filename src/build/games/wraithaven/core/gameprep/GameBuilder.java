/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.gameprep;

import build.games.wraithaven.core.MapStyle;
import build.games.wraithaven.core.WraithEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class GameBuilder{
	private final File outFolder;
	private final File gameProperties;
	private final MapStyle mapStyle;
	public GameBuilder(MapStyle mapStyle){
		this.mapStyle = mapStyle;
		outFolder = Algorithms.getFile("Compiled");
		gameProperties = Algorithms.getFile(); // Get data folder.
	}
	public void compile(){
		{
			// Request map save.
			SaveHandler save = mapStyle.getSaveHandler();
			if(save.needsSaving()){
				if(!save.requestSave()){
					// If they won't save, we can't run.
					return;
				}
			}
		}
		try{
			if(outFolder.exists()){
				// This is just to clean up any old resources.
				Algorithms.deleteFile(outFolder);
			}
			outFolder.mkdirs();
			// TODO Export game properties files.
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	public void run(String... args) throws IOException{
		// Run the program with the correct arguments.
		String[] flags = Arrays.copyOf(args, args.length+4);
		{
			int i = 0;
			flags[i++] = "java";
			flags[i++] = "-jar";
			flags[i++] = "WraithGame.jar";
			for(String s : args){
				flags[i++] = s;
			}
			flags[i++] = "-data:\""+gameProperties.getAbsolutePath()+"\"";
		}
		Runtime rt = Runtime.getRuntime();
		final Process process = rt.exec(flags, null, new File(WraithEngine.getNativeFolder()));
		// This thread handles debug events.
		Thread thread1 = new Thread(new Runnable(){
			@Override
			public void run(){
				try(BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))){
					String line;
					while((line = in.readLine())!=null){
						System.out.println(line);
					}
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		});
		thread1.setDaemon(false);
		thread1.start();
		// This thread handles error events.
		Thread thread2 = new Thread(new Runnable(){
			@Override
			public void run(){
				try(BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
					String line;
					while((line = in.readLine())!=null){
						System.err.println(line);
					}
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		});
		thread2.setDaemon(false);
		thread2.start();
	}
}
