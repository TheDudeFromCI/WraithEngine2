/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.InputAdapter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityImporterImagePainter extends JPanel{
	private static int nextMultiple(int x){
		return (int)(Math.ceil(x/(double)WraithEngine.projectBitSize)*WraithEngine.projectBitSize);
	}
	private static Polygon generateIsoSquare(){
		int[] x = new int[4];
		int[] y = new int[4];
		int r = WraithEngine.projectBitSize/2;
		int f = WraithEngine.projectBitSize/4;
		x[0] = 0;
		y[0] = 0;
		x[1] = r;
		y[1] = f;
		x[2] = 0;
		y[2] = r;
		x[3] = -r;
		y[3] = f;
		return new Polygon(x, y, 4);
	}
	private final BufferedImage image;
	private final int width;
	private final int height;
	private final InputAdapter drag;
	private final InputAdapter tileSelect;
	private final Polygon isoSquare;
	private final ArrayList<Point> tiles = new ArrayList(16);
	private boolean confirmed;
	private int posX;
	private int posY;
	private boolean showCursor;
	private int cursorX;
	private int cursorY;
	public EntityImporterImagePainter(BufferedImage image){
		this.image = image;
		width = nextMultiple(image.getWidth());
		height = nextMultiple(image.getHeight());
		isoSquare = generateIsoSquare();
		setPreferredSize(new Dimension(width, height));
		drag = new InputAdapter(){
			private int downX;
			private int downY;
			private int posStartX;
			private int posStartY;
			@Override
			public void mousePressed(MouseEvent event){
				downX = event.getX();
				downY = event.getY();
				posStartX = posX;
				posStartY = posY;
			}
			@Override
			public void mouseDragged(MouseEvent event){
				// Drag around the image.
				posX = (event.getX()-downX)+posStartX;
				posY = (event.getY()-downY)+posStartY;
				// Don't let the user move the image outside of the frame.
				posX = Math.max(posX, 0);
				posY = Math.max(posY, 0);
				posX = Math.min(posX, width-image.getWidth());
				posY = Math.min(posY, height-image.getHeight());
				// Draw
				repaint();
			}
		};
		addMouseListener(drag);
		addMouseMotionListener(drag);
		tileSelect = new InputAdapter(){
			@Override
			public void mouseMoved(MouseEvent event){
				cursor(event.getX(), event.getY());
			}
			@Override
			public void mouseExited(MouseEvent event){
				showCursor = false;
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent event){
				showCursor = true;
				cursor(event.getX(), event.getY());
			}
			@Override
			public void mousePressed(MouseEvent event){
				cursor(event.getX(), event.getY());
				cursorClick();
				repaint();
			}
			private void cursor(int x, int y){
				y += WraithEngine.projectBitSize/4;
				int tileX = (int)Math.floor((x/(float)(WraithEngine.projectBitSize/2)+(y/(float)(WraithEngine.projectBitSize/4)))/2);
				int tileY = (int)Math.floor((y/(float)(WraithEngine.projectBitSize/4)-(x/(float)(WraithEngine.projectBitSize/2)))/2);
				cursorX = (tileX-tileY)*(WraithEngine.projectBitSize/2);
				cursorY = (tileX+tileY)*(WraithEngine.projectBitSize/4)-WraithEngine.projectBitSize/4;
				if(cursorX==0||cursorX==width){
					cursorX = -100;
					cursorY = -100;
				}
				repaint();
			}
		};
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setClip(0, 0, width, height);
		drawGrid(g, width, height);
		Composite composite = null;
		if(confirmed){
			composite = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		}
		g.drawImage(image, posX, posY, null);
		if(confirmed){
			g.setComposite(composite);
		}
		g.setColor(Color.gray);
		g.drawRect(0, 0, width-1, height-1);
		g.dispose();
	}
	public int getIdealWidth(){
		return width;
	}
	public int getIdealHeight(){
		return height;
	}
	public void setConfirmed(){
		confirmed = true;
		removeMouseListener(drag);
		removeMouseMotionListener(drag);
		addMouseListener(tileSelect);
		addMouseMotionListener(tileSelect);
	}
	public int getImageX(){
		return posX;
	}
	public int getImageY(){
		return posY;
	}
	public BufferedImage getImage(){
		return image;
	}
	public void drawGrid(Graphics2D g, int w, int h){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int s = WraithEngine.projectBitSize;
		int x, y;
		if(showCursor){
			g.translate(cursorX, cursorY);
			g.setColor(Color.pink);
			g.fillPolygon(isoSquare);
			g.translate(-cursorX, -cursorY);
		}
		g.setColor(Color.red);
		for(Point p : tiles){
			g.translate(p.x, p.y);
			g.fillPolygon(isoSquare);
			g.translate(-p.x, -p.y);
		}
		g.setColor(Color.gray);
		boolean off = true;
		for(y = 0; y<h; y += s/4){
			for(x = off?s/2:0; x<=w; x += s){
				drawSquare(g, x, y);
			}
			off = !off;
		}
	}
	private void drawSquare(Graphics2D g, int x, int y){
		g.translate(x, y);
		g.drawPolygon(isoSquare);
		g.translate(-x, -y);
	}
	private void cursorClick(){
		if(cursorX==-100&&cursorY==-100){
			// We don't want to add a click for partial tiles.
			return;
		}
		Point p = new Point(cursorX, cursorY){
			@Override
			public boolean equals(Object o){
				if(o instanceof Point){
					Point p = (Point)o;
					return p.x==x&&p.y==y;
				}
				return false;
			}
		};
		if(tiles.contains(p)){
			tiles.remove(p);
		}else{
			tiles.add(p);
		}
		repaint();
	}
	public ArrayList<Point> getTiles(){
		return tiles;
	}
	public int getLayers(boolean below){
		if(below){
			int maxY = Integer.MIN_VALUE;
			for(Point p : tiles){
				maxY = Math.max(maxY, p.y);
			}
			return (int)Math.ceil((height-maxY)/(float)WraithEngine.projectBitSize)+1;
		}else{
			int minY = Integer.MAX_VALUE;
			for(Point p : tiles){
				minY = Math.min(minY, p.y);
			}
			return (int)Math.ceil(minY/(float)WraithEngine.projectBitSize)+1;
		}
	}
}
