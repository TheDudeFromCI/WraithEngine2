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
	private int posX;
	private int posY;
	private boolean confirmed;
	public EntityImporterImagePainter(BufferedImage image, EntityImporterGrid grid){
		this.image = image;
		this.grid = grid;
		width = nextMultiple(image.getWidth());
		height = nextMultiple(image.getHeight());
		System.out.println("Scaled to "+width+" x "+height);
		setPreferredSize(new Dimension(width, height));
		InputAdapter ia = new InputAdapter(){
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
				if(!confirmed){
					posX = (event.getX()-downX)+posStartX;
					posY = (event.getY()-downY)+posStartY;
					repaint();
				}
			}
		};
		addMouseListener(ia);
		addMouseMotionListener(ia);
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		grid.drawGrid(g, width, height);
		g.drawImage(image, posX, posY, null);
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
	}
}
