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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
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
	public EntityImporterGrid(){
		isoSquare = generateIsoSquare();
		InputAdapter ia = new InputAdapter(){
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
		};
		addMouseListener(ia);
		addMouseMotionListener(ia);
	}
	public void cursor(int x, int y){
		int tileX = (int)Math.floor((x/(float)(WraithEngine.projectBitSize/2)+(y/(float)(WraithEngine.projectBitSize/4)))/2);
		int tileY = (int)Math.floor((y/(float)(WraithEngine.projectBitSize/4)-(x/(float)(WraithEngine.projectBitSize/2)))/2);
		cursorX = (tileX-tileY)*(WraithEngine.projectBitSize/2);
		cursorY = (tileX+tileY)*(WraithEngine.projectBitSize/4);
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
		g.setColor(Color.gray);
		int s = WraithEngine.projectBitSize;
		int x, y;
		if(showCursor){
			g.translate(cursorX, cursorY);
			g.setColor(Color.pink);
			g.fillPolygon(isoSquare);
			g.setColor(Color.gray);
			g.translate(-cursorX, -cursorY);
		}
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
}
