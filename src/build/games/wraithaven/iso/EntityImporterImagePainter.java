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
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityImporterImagePainter extends JPanel{
	private static int nextMultiple(int x){
		return (int)(Math.ceil(x/(double)WraithEngine.projectBitSize)*WraithEngine.projectBitSize);
	}
	private final BufferedImage image;
	private final EntityImporterGrid grid;
	private final int width;
	private final int height;
	private final InputAdapter drag;
	private final InputAdapter tileSelect;
	private boolean confirmed;
	private EntityImporterGrid tileGrid;
	private int posX;
	private int posY;
	public EntityImporterImagePainter(BufferedImage image, EntityImporterGrid grid){
		this.image = image;
		this.grid = grid;
		width = nextMultiple(image.getWidth());
		height = nextMultiple(image.getHeight());
		System.out.println("Scaled to "+width+" x "+height);
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
				posX = (event.getX()-downX)+posStartX;
				posY = (event.getY()-downY)+posStartY;
				repaint();
			}
		};
		addMouseListener(drag);
		addMouseMotionListener(drag);
		tileSelect = new InputAdapter(){
			private boolean showCursor;
			@Override
			public void mouseMoved(MouseEvent event){
				cursor(event.getX(), event.getY());
			}
			@Override
			public void mouseExited(MouseEvent event){
				showCursor = false;
				repaint();
				grid.repaint();
			}
			@Override
			public void mouseEntered(MouseEvent event){
				showCursor = true;
				cursor(event.getX(), event.getY());
			}
			@Override
			public void mousePressed(MouseEvent event){
				int x = event.getX();
				int y = event.getY();
				int tileX = (int)Math.floor((x/(float)(WraithEngine.projectBitSize/2)+(y/(float)(WraithEngine.projectBitSize/4)))/2);
				int tileY = (int)Math.floor((y/(float)(WraithEngine.projectBitSize/4)-(x/(float)(WraithEngine.projectBitSize/2)))/2);
				int cursorX = (tileX-tileY)*(WraithEngine.projectBitSize/2);
				int cursorY = (tileX+tileY)*(WraithEngine.projectBitSize/4);
				grid.cursorClick(cursorX, cursorY);
				repaint();
			}
			private void cursor(int x, int y){
				int tileX = (int)Math.floor((x/(float)(WraithEngine.projectBitSize/2)+(y/(float)(WraithEngine.projectBitSize/4)))/2);
				int tileY = (int)Math.floor((y/(float)(WraithEngine.projectBitSize/4)-(x/(float)(WraithEngine.projectBitSize/2)))/2);
				int cursorX = (tileX-tileY)*(WraithEngine.projectBitSize/2);
				int cursorY = (tileX+tileY)*(WraithEngine.projectBitSize/4);
				tileGrid.updateCursor(showCursor, cursorX, cursorY);
				repaint();
			}
		};
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		grid.drawGrid(g, width, height);
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
	public void setConfirmed(EntityImporterGrid grid){
		this.tileGrid = grid;
		confirmed = true;
		removeMouseListener(drag);
		removeMouseMotionListener(drag);
		addMouseListener(tileSelect);
		addMouseMotionListener(tileSelect);
		grid.addMouseListener(tileSelect);
		grid.addMouseMotionListener(tileSelect);
	}
}
