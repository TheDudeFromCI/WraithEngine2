/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author thedudefromci
 */
public class ResourceUtils{
	private static final int BUFFER_SIZE = 4096;
	public static void exportFile(String path, File file) throws IOException{
		try(InputStream in = ResourceUtils.class.getResourceAsStream("/"+path);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
			byte[] buffer = new byte[BUFFER_SIZE];
			int nBytes;
			while((nBytes = in.read(buffer))!=-1){
				out.write(buffer, 0, nBytes);
			}
			out.flush();
		}
	}
	public static void exportFolder(String path, File file) throws IOException{
		file.mkdirs();
		File jarFile = new File(ResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		System.out.println("Unpacking Jar.");
		try(JarFile jar = new JarFile(jarFile)){
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			JarEntry entry;
			while(entries.hasMoreElements()){
				entry = entries.nextElement();
				String name = entry.getName();
				if(name.startsWith(path+"/")){ // filter according to the path
					System.out.print("Extracting: "+name+"... ");
					if(entry.isDirectory()){
						new File(file, name.substring(path.length()+1)).mkdir();
					}else{
						exportFile(name, new File(file, name.substring(path.length()+1)));
					}
					System.out.println("Done.");
				}
			}
		}
	}
	public static String readAllText(String path){
		StringBuilder sb = new StringBuilder(64);
		try(Scanner in = new Scanner(ResourceUtils.class.getResourceAsStream("/"+path))){
			while(in.hasNext()){
				if(sb.length()>0){
					sb.append('\n');
				}
				sb.append(in.nextLine());
			}
		}
		return sb.toString();
	}
}
