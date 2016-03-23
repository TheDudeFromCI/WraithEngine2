/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author thedudefromci
 */
public class BackupUtils{
	public static void createBackup(File folder, File zip) throws IOException{
		System.out.printf("Creating a backup of '%s' in zip '%s'.\n", folder.getAbsolutePath(), zip.getAbsolutePath());
		int nameClip = folder.getAbsolutePath().length()-folder.getName().length();
		try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))){
			saveZipEntry(out, folder, nameClip);
		}
		System.out.println("Backup complete.");
	}
	private static void saveZipEntry(ZipOutputStream out, File file, int nameClip) throws IOException{
		String entryName = file.getAbsolutePath().substring(nameClip);
		if(file.isDirectory()){
			entryName += '/';
		}
		System.out.printf("  Created entry '%s'.\n", entryName);
		ZipEntry e = new ZipEntry(entryName);
		out.putNextEntry(e);
		if(!file.isDirectory()){
			// Transfer bytes.
			try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))){
				int i;
				byte[] b = new byte[1024];
				while((i = in.read(b))>0){
					out.write(b, 0, i);
				}
			}
		}
		out.closeEntry();
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				saveZipEntry(out, f, nameClip);
			}
		}
	}
	public static void loadBackup(File folder, File zip) throws IOException{
		System.out.printf("Loading backup of '%s', from zip '%s'.\n", folder.getAbsolutePath(), zip.getAbsolutePath());
		System.out.println("  Deleting original data.");
		Algorithms.deleteFile(folder);
		folder = folder.getParentFile();
		System.out.println("  Opening zip.");
		ZipFile zipFile = new ZipFile(zip);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements()){
			ZipEntry e = entries.nextElement();
			try(InputStream in = zipFile.getInputStream(e)){
				File file = new File(folder, e.getName());
				System.out.printf("  Loading entry '%s'... ", e.getName());
				if(e.isDirectory()){
					System.out.println("Dir");
					file.mkdirs();
				}else{
					System.out.println("File");
					try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
						int i;
						byte[] b = new byte[1024];
						while((i = in.read(b))>0){
							out.write(b, 0, i);
						}
					}
				}
			}
		}
		System.out.println("Backup loading complete.");
	}
}
