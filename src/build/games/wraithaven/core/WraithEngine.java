/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

import build.games.wraithaven.core.os.Installer;
import java.io.File;
import javax.swing.UIManager;
import wraith.lib.util.Algorithms;
import wraith.lib.util.SortedMap;

@SuppressWarnings("serial")
public class WraithEngine{
	private static String workspaceFolder;
	private static String outputFolder;
	private static String assetFolder;
	private static String nativeFolder;
	public static String projectName;
	public static int projectBitSize;
	public static String projectUUID;
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception exception){
			exception.printStackTrace();
		}
		SortedMap<String,String> map = new SortedMap(1);
		loadProgramArguments(map, args);
		Installer installer = new Installer(map);
		installer.unloadAssets();
		final String workspaceName = "Projects";
		String dataFolder;
		if(map.contains("path")){
			dataFolder = map.get("path");
		}else{
			dataFolder = installer.getDataFolder().getAbsolutePath();
		}
		workspaceFolder = dataFolder+File.separatorChar+workspaceName;
		outputFolder = workspaceFolder;
		assetFolder = dataFolder+File.separatorChar+"Assets";
		nativeFolder = dataFolder+File.separatorChar+"Native";
		Algorithms.initalize(outputFolder, assetFolder);
		new ProjectList();
	}
	private static void loadProgramArguments(SortedMap<String,String> map, String[] args){
		for(String s : args){
			String[] a = s.split(":");
			if(a.length==1){
				map.put(a[0], "true");
			}else{
				map.put(a[0], a[1]);
			}
		}
	}
	public static String getOutputFolder(){
		return outputFolder;
	}
	public static String getAssetFolder(){
		return assetFolder;
	}
	public static String getWorkspace(){
		return workspaceFolder;
	}
	public static String getNativeFolder(){
		return nativeFolder;
	}
	public static void updateFolders(String out, String asset){
		outputFolder = out;
		assetFolder = asset;
		Algorithms.initalize(outputFolder, assetFolder);
	}
}
