/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.WraithEngine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityImporterGrid extends JPanel{
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
	private final Polygon isoSquare;
	private int width;
	private int height;
	private boolean showCursor;
	private int cursorX;
	private int cursorY;
	private ArrayList<Point> tiles = new ArrayList(16);
	public EntityImporterGrid(){
		isoSquare = generateIsoSquare();
	}
	public void updateCursor(boolean show, int x, int y){
		showCursor = show;
		cursorX = x;
		cursorY = y;
		repaint();
	}
	public void cursorClick(int x, int y){
		cursorX = x;
		cursorY = y;
		Point p = new Point(x, y){
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
	public void build(int width, int height){
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
	}
	@Override
	public void paintComponent(Graphics g){
		drawGrid((Graphics2D)g, width, height);
		g.setColor(Color.gray);
		g.drawRect(0, 0, width-1, height-1);
		g.dispose();
	}
	public void drawGrid(Graphics2D g, int w, int h){
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, w, h);
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
		boolean off = false;
		for(y = 0; y<h; y += s/2){
			for(x = off?-s:0; x<=w; x += s){
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
	public ArrayList<Point> getTiles(){
		return tiles;
	}
	public int getLayers(){
		int minY = Integer.MAX_VALUE;
		for(Point p : tiles){
			minY = Math.min(minY, p.y);
		}
		return (int)Math.ceil(minY/(float)WraithEngine.projectBitSize)+1;
	}
}
