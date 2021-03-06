/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class Algorithms{
	private static String outputFolder;
	private static String assetFolder;
	public static void initalize(String outputFolder, String assetFolder){
		Algorithms.outputFolder = stripQuotes(outputFolder);
		Algorithms.assetFolder = stripQuotes(assetFolder);
	}
	public static void deleteFile(File file){
		if(!file.exists()){
			return;
		}
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				deleteFile(f);
			}
		}
		file.delete();
	}
	public static File getAsset(String name){
		if(assetFolder==null){
			throw new RuntimeException();
		}
		File file = new File(assetFolder+File.separatorChar+name);
		if(!file.exists()){
			if(file.getName().contains(".")){
				file.getParentFile().mkdirs();
			}else{
				file.mkdirs();
			}
		}
		return file;
	}
	public static File getRawFile(String dir, String... path){
		StringBuilder sb = new StringBuilder(0);
		sb.append(dir);
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
	public static File getFile(String... path){
		if(outputFolder==null){
			throw new RuntimeException();
		}
		return getRawFile(outputFolder, path);
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
	 * @param fileType
	 *            - The type of file to choose.
	 * @return The image file the user has chosen, or null if no image was chosen.
	 */
	public static File userChooseFile(String title, String button, String fileType){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file){
				return file.isDirectory()||file.getName().endsWith("."+fileType.toLowerCase());
			}
			@Override
			public String getDescription(){
				return "*."+fileType.toUpperCase()+" Files";
			}
		});
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(title);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.showDialog(null, button);
		return fileChooser.getSelectedFile();
	}
	public static File userChooseFile(String title, String button, String fileType, File dir){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file){
				return file.isDirectory()||file.getName().endsWith("."+fileType.toLowerCase());
			}
			@Override
			public String getDescription(){
				return "*."+fileType.toUpperCase()+" Files";
			}
		});
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(title);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(dir);
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
	public static BufferedImage trimTransparency(BufferedImage in){
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int[] rgb = new int[in.getWidth()*in.getHeight()];
		in.getRGB(0, 0, in.getWidth(), in.getHeight(), rgb, 0, in.getWidth());
		int x, y, i, a;
		for(y = 0; y<in.getHeight(); y++){
			for(x = 0; x<in.getWidth(); x++){
				// TODO Skip already checked regions for a slight speed increase.
				i = y*in.getWidth()+x;
				a = (rgb[i]>>24)&0xFF;
				if(a==0){
					continue;
				}
				minX = Math.min(minX, x);
				minY = Math.min(minY, y);
				maxX = Math.max(maxX, x);
				maxY = Math.max(maxY, y);
			}
		}
		if(minX==Integer.MAX_VALUE){
			// If nothing is visible.
			throw new IllegalArgumentException("Image contains no visible pixels!");
		}
		if(minX==0&&minY==0&&maxX==in.getWidth()-1&&maxY==in.getHeight()-1){
			// All transparency is already trimmed.
			return in;
		}
		BufferedImage out = new BufferedImage(maxX-minX+1, maxY-minY+1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = out.createGraphics();
		g.drawImage(in, 0, 0, out.getWidth(), out.getHeight(), minX, minY, maxX, maxY, null);
		g.dispose();
		return out;
	}
	public static void copyFile(File file, File outFile, boolean copyRootFolder) throws IOException{
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				copyFile(f, copyRootFolder?new File(outFile, file.getName()):outFile, true);
			}
		}else{
			outFile.mkdirs();
			try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outFile, file.getName())))){
				byte[] buffer = new byte[4096];
				int nBytes;
				while((nBytes = in.read(buffer))!=-1){
					out.write(buffer, 0, nBytes);
				}
				out.flush();
			}
		}
	}
	public static void copyFileRename(File file, File newPath) throws IOException{
		newPath.getParentFile().mkdirs();
		try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newPath))){
			byte[] buffer = new byte[4096];
			int nBytes;
			while((nBytes = in.read(buffer))!=-1){
				out.write(buffer, 0, nBytes);
			}
			out.flush();
		}
	}
	public static String readFileText(File file) throws IOException{
		StringBuilder sb = new StringBuilder(64);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s;
		while((s = in.readLine())!=null){
			if(sb.length()>0){
				sb.append('\n');
			}
			sb.append(s);
		}
		return sb.toString();
	}
	public static String stripQuotes(String s){
		if(s==null){
			return s;
		}
		if(s.startsWith("\"")){
			s = s.substring(1);
		}
		if(s.endsWith("\"")){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
	public static Area createClip(BufferedImage image, float w, float h){
		Area area = new Area();
		int a, b;
		int u = image.getWidth();
		int v = image.getHeight();
		int[] rgb = new int[u*v];
		image.getRGB(0, 0, u, v, rgb, 0, u);
		for(a = 0; a<u; a++){
			for(b = 0; b<v; b++){
				if(((rgb[b*u+a]>>24)&0xFF)>0){
					area.add(new Area(new Rectangle((int)Math.floor((float)a/u*w), (int)Math.floor((float)b/v*h), 1, 1)));
				}
			}
		}
		return area;
	}
	public static String getFormattedData(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		StringBuilder sb = new StringBuilder(16);
		sb.append(year).append('-').append(month).append('-').append(day);
		sb.append('_');
		sb.append(hour).append('-').append(minute);
		return sb.toString();
	}
}
