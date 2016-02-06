/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityList extends JPanel{
	private static Polygon generateCursor(){
		int[] x = new int[4];
		int[] y = new int[4];
		x[0] = 0;
		y[0] = 0;
		x[1] = PREVIEW_ICON_SIZE;
		y[1] = 0;
		x[2] = PREVIEW_ICON_SIZE;
		y[2] = PREVIEW_ICON_SIZE;
		x[3] = 0;
		y[3] = PREVIEW_ICON_SIZE;
		return new Polygon(x, y, 4);
	}
	private static final int PREVIEW_WIDTH = 4;
	private static final int PREVIEW_ICON_SIZE = 64;
	private final ArrayList<EntityType> entityTypes = new ArrayList(16);
	private final HashMap<EntityType,BufferedImage> previews = new HashMap(16);
	private final CursorSelection cursorSelection;
	private final Polygon cursor;
	public EntityList(CursorSelection cursorSelection){
		this.cursorSelection = cursorSelection;
		cursor = generateCursor();
		load();
		updatePrefferedSize();
	}
	private void updatePrefferedSize(){
		setPreferredSize(new Dimension(PREVIEW_WIDTH*PREVIEW_ICON_SIZE, Math.max((int)Math.ceil(entityTypes.size()/(double)PREVIEW_WIDTH), 150)));
	}
	private void load(){
		File file = Algorithms.getFile("Entities", "Previews", "List.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int count = bin.getInt();
		EntityType e;
		BufferedImage img;
		for(int i = 0; i<count; i++){
			e = new EntityType(bin.getString());
			entityTypes.add(e);
			try{
				img = ImageIO.read(Algorithms.getFile("Entities", "Previews", e.getUUID()+".png"));
				previews.put(e, img);
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
	}
	public EntityType getType(String uuid){
		for(EntityType e : entityTypes){
			if(e.getUUID().equals(uuid)){
				return e;
			}
		}
		return null;
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(entityTypes.size());
		for(EntityType e : entityTypes){
			bin.addStringAllocated(e.getUUID());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Entities", "Previews", "List.dat"));
	}
	public void removeEntityType(EntityType e){
		entityTypes.remove(e);
		previews.remove(e);
		Algorithms.deleteFile(Algorithms.getFile("Entities", "Previews", e.getUUID()+".png"));
		save();
		repaint();
	}
	public void addEntityType(EntityType e, BufferedImage originalImage){
		entityTypes.add(e);
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		if(originalImage.getWidth()>PREVIEW_ICON_SIZE){
			w = PREVIEW_ICON_SIZE;
			h = (w*originalImage.getHeight())/originalImage.getWidth();
		}
		if(h>PREVIEW_ICON_SIZE){
			h = PREVIEW_ICON_SIZE;
			w = (h*originalImage.getWidth())/originalImage.getHeight();
		}
		int x = (PREVIEW_ICON_SIZE-w)/2;
		int y = (PREVIEW_ICON_SIZE-h)/2;
		BufferedImage buf = new BufferedImage(PREVIEW_ICON_SIZE, PREVIEW_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = buf.createGraphics();
		g.drawImage(originalImage, x, y, w, h, null);
		g.dispose();
		previews.put(e, buf);
		try{
			ImageIO.write(originalImage, "png", Algorithms.getFile("Entities", "Fulls", e.getUUID()+".png"));
			ImageIO.write(buf, "png", Algorithms.getFile("Entities", "Previews", e.getUUID()+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
		save();
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.lightGray);
		int width = getWidth();
		int height = getHeight();
		g.fillRect(0, 0, width, height);
		int x = 0;
		int y = 0;
		for(EntityType e : entityTypes){
			g.drawImage(previews.get(e), x, y, null);
			x += PREVIEW_ICON_SIZE;
			if(x==PREVIEW_ICON_SIZE*PREVIEW_WIDTH){
				x = 0;
				y += PREVIEW_ICON_SIZE;
			}
		}
		if(cursorSelection.isEntityActive()){
			g.setStroke(new BasicStroke(3));
			g.translate(cursorSelection.getSelectedTileIndex()%PREVIEW_WIDTH*PREVIEW_ICON_SIZE,
				cursorSelection.getSelectedTileIndex()/PREVIEW_WIDTH*PREVIEW_ICON_SIZE);
			g.setColor(Color.white);
			g.drawPolygon(cursor);
		}
		g.dispose();
	}
	public ArrayList<EntityType> getAllTypes(){
		return entityTypes;
	}
}
