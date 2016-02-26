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

@SuppressWarnings("serial")
public class WraithEngine{
	public static String workspaceFolder;
	public static String outputFolder;
	public static String assetFolder;
	public static String projectName;
	public static int projectBitSize;
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception exception){
			exception.printStackTrace();
		}
		Installer installer = new Installer();
		installer.unloadAssets();
		final String workspaceName = "WraithEngine Projects";
		String dataFolder;
		if(args.length>0){
			dataFolder = args[0];
		}else{
			dataFolder = installer.getDataFolder().getAbsolutePath();
		}
		outputFolder = dataFolder+File.separatorChar+workspaceName;
		assetFolder = dataFolder+File.separatorChar+"Assets";
		Algorithms.initalize(outputFolder, assetFolder);
		new ProjectList();
	}
}
