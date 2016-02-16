/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import build.games.wraithaven.core.WraithEngine;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Algorithms{
	public static void deleteFile(File file){
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				deleteFile(f);
			}
		}
		file.delete();
	}
	public static File getAsset(String name){
		File file = new File(WraithEngine.assetFolder+File.separatorChar+name);
		if(!file.exists()){
			if(file.getName().contains(".")){
				file.getParentFile().mkdirs();
			}else{
				file.mkdirs();
			}
		}
		return file;
	}
	public static File getFile(String... path){
		StringBuilder sb = new StringBuilder(0);
		sb.append(WraithEngine.outputFolder);
		for(String s : path){
			sb.append(File.separatorChar);
			sb.append(s);
		}
		File file = new File(sb.toString());
		if(!file.exists()){
			if(file.getName().contains(".")){
				file.getParentFile().mkdirs();
			}else{
				file.mkdirs();
			}
		}
		return file;
	}
	public static String randomUUID(){
		final int uuidSize = 24;
		StringBuilder sb = new StringBuilder(uuidSize);
		for(int i = 0; i<uuidSize; i++){
			sb.append(Integer.toHexString((int)(Math.random()*16)));
		}
		return sb.toString();
	}
	/**
	 * A quick method for asking the user to load an image file. Currently only supports PNG files.
	 *
	 * @param title
	 *            - The title of the window.
	 * @param button
	 *            - The name on the button.
	 * @return The image file the user has chosen, or null if no image was chosen.
	 */
	public static File userChooseImage(String title, String button){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file){
				return file.isDirectory()||file.getName().endsWith(".png");
			}
			@Override
			public String getDescription(){
				return "*.PNG Files";
			}
		});
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(title);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.showDialog(null, button);
		return fileChooser.getSelectedFile();
	}
	public static BufferedImage smoothResize(BufferedImage image, int size){
		BufferedImage buf = image;
		int s = image.getWidth();
		do{
			if(s>size){
				s /= 2;
				if(s<size){
					s = size;
				}
			}else{
				s *= 2;
				if(s>size){
					s = size;
				}
			}
			BufferedImage out = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = out.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(buf, 0, 0, s, s, null);
			g.dispose();
			buf = out;
		}while(s!=size); // This loop enhances quality, while resizing!
		return buf;
	}
}
