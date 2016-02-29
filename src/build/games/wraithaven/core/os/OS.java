/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.os;

import java.io.File;

/**
 * @author thedudefromci
 */
public enum OS{
	// TODO Add more.
	WINDOWS("%HOME%/WraithEngine"),
	LINUX("%HOME%/WraithEngine"),
	OTHER("%DIR%");
	public static OS determineOS(){
		String name = System.getProperty("os.name").toLowerCase();
		if(name.contains("win")){
			return OS.WINDOWS;
		}
		if(name.contains("nix")||name.contains("nux")||name.contains("aix")){
			return OS.LINUX;
		}
		return OS.OTHER;
	}
	private final String folderLocation;
	private OS(String folderLocation){
		folderLocation = folderLocation.replace("%HOME%", System.getProperty("user.home"));
		folderLocation = folderLocation.replace("%DIR%", System.getProperty("user.dir"));
		this.folderLocation = folderLocation;
	}
	public File getFolder(){
		return new File(folderLocation);
	}
}
