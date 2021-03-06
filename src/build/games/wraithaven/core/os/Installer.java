/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.os;

import build.games.wraithaven.util.ResourceUtils;
import java.io.File;
import java.io.IOException;
import wraith.lib.util.Algorithms;
import wraith.lib.util.SortedMap;

/**
 * @author thedudefromci
 */
public class Installer{
	private final OS os;
	private final SortedMap<String,String> args;
	public Installer(SortedMap<String,String> args){
		this.args = args;
		os = OS.determineOS();
	}
	public void unloadAssets(){
		boolean forceReload = args.contains("reloadAssets");
		File file = os.getFolder();
		if(!file.exists()){
			file.mkdirs();
		}
		File assetFolder = new File(file, "Assets");
		File nativeFolder = new File(file, "Native");
		if(forceReload){
			Algorithms.deleteFile(assetFolder);
			Algorithms.deleteFile(nativeFolder);
			assetFolder.mkdirs();
			extractAssets(assetFolder);
			nativeFolder.mkdirs();
			extractNative(nativeFolder);
		}else if(!assetFolder.exists()){
			assetFolder.mkdirs();
			extractAssets(assetFolder);
		}else if(!nativeFolder.exists()){
			nativeFolder.mkdirs();
			extractNative(nativeFolder);
		}else if(!checkVersion(assetFolder)){
			Algorithms.deleteFile(assetFolder);
			Algorithms.deleteFile(nativeFolder);
			assetFolder.mkdirs();
			extractAssets(assetFolder);
			nativeFolder.mkdirs();
			extractNative(nativeFolder);
		}
	}
	private void extractAssets(File assetFolder){
		try{
			ResourceUtils.exportFolder("Assets", assetFolder);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void extractNative(File assetFolder){
		try{
			ResourceUtils.exportFolder("Native", assetFolder);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private boolean checkVersion(File assetFolder){
		File file = new File(assetFolder, "version.txt");
		if(!file.exists()){
			return false;
		}
		try{
			String version = Algorithms.readFileText(file);
			String version2 = ResourceUtils.readAllText("Assets/version.txt");
			System.out.println("Client Asset Version: '"+version+"'");
			System.out.println("Server Asset Version: '"+version2+"'");
			return version.equals(version2);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return false;
	}
	public File getDataFolder(){
		return os.getFolder();
	}
}
