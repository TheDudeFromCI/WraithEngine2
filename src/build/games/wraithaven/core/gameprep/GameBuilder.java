/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.gameprep;

import build.games.wraithaven.core.MapStyle;
import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.ResourceUtils;
import java.io.File;

/**
 * @author thedudefromci
 */
public class GameBuilder{
	private final MapStyle mapStyle;
	private final File outFolder;
	public GameBuilder(MapStyle mapStyle){
		this.mapStyle = mapStyle;
		outFolder = Algorithms.getFile("Compiled");
	}
	public void prepare(){
		// TODO
	}
	public void unpackResources(){
		try{
			if(outFolder.exists()){
				// This is just to clean up any old resources.
				Algorithms.deleteFile(outFolder);
			}
			outFolder.mkdirs();
			ResourceUtils.exportFolder("Native", outFolder);
			// TODO Export game properties files.
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	public void run(){
		// TODO
	}
}
