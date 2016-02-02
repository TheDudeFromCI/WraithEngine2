/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractMapEditor;
import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.InputAdapter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JOptionPane;

/**
 * @author TheDudeFromCI
 */
public class MapEditor extends AbstractMapEditor{
	private final MapImageStorage imageStorage;
	private final CursorSelection cursorSelection;
	private Map map;
	private int tileSize;
	private int tileWidth;
	private int tileHeight;
	private int scrollX;
	private int scrollY;
	private Polygon selectionHexagon;
	public MapEditor(CursorSelection cursorSelection){
		tileSize = WraithEngine.projectBitSize;
		tileWidth = tileSize/2;
		tileHeight = tileSize/4;
		this.cursorSelection = cursorSelection;
		imageStorage = new MapImageStorage();
		InputAdapter ml = new InputAdapter(){
			private boolean dragging;
			private boolean drawing;
			private int scrollXStart;
			private int scrollYStart;
			private int mouseXStart;
			private int mouseYStart;
			@Override
			public void mouseDragged(MouseEvent event){
				if(dragging){
					scrollX = event.getX()-mouseXStart+scrollXStart;
					scrollY = event.getY()-mouseYStart+scrollYStart;
					repaint();
				}
				if(drawing){
					mouseClicked(event.getX(), event.getY(), 1);
				}
				mouseMoved(event);
			}
			@Override
			public void mouseExited(MouseEvent event){
				dragging = false;
				drawing = false;
				cursorSelection.hide();
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent event){
				cursorSelection.show();
				mouseMoved(event);
				repaint();
			}
			@Override
			public void mouseMoved(MouseEvent event){
				int x = event.getX()-scrollX;
				int y = event.getY()-scrollY;
				int tileX = (int)Math.floor((x/(float)tileWidth+y/(float)tileHeight)/2);
				int tileY = (int)Math.floor((y/(float)tileHeight-(x/(float)tileWidth))/2);
				cursorSelection.setScreenLocation((tileX-tileY)*tileWidth+scrollX, (tileX+tileY)*tileHeight+scrollY);
				cursorSelection.setTileLocation(tileX, tileY);
				repaint();
			}
			@Override
			public void mouseClicked(MouseEvent event){
				mouseClicked(event.getX(), event.getY(), event.getButton());
			}
			public void mouseClicked(int x, int y, int button){
				if(map==null){
					return;
				}
				if(button==MouseEvent.BUTTON1){
					if(cursorSelection.isOverMap()){
						map.setTile(cursorSelection.getTileX(), cursorSelection.getTileY(), cursorSelection.getSelectedTile());
						updateNeedsSaving();
						repaint();
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent event){
				int button = event.getButton();
				if(button==MouseEvent.BUTTON3){
					dragging = true;
					scrollXStart = scrollX;
					scrollYStart = scrollY;
					mouseXStart = event.getX();
					mouseYStart = event.getY();
				}else{
					dragging = false;
				}
				drawing = button==MouseEvent.BUTTON1;
			}
			@Override
			public void mouseReleased(MouseEvent event){
				dragging = false;
				drawing = false;
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent event){
				if(dragging||drawing){
					return;
				}
				int change = -event.getWheelRotation()*4;
				int pixelSizeBefore = tileSize;
				tileSize = Math.max(Math.min(tileSize+change, WraithEngine.projectBitSize*4), WraithEngine.projectBitSize/4);
				tileWidth = tileSize/2;
				tileHeight = tileSize/4;
				generateSelectionHexagon();
				float per = tileSize/(float)pixelSizeBefore;
				scrollX = -Math.round(event.getX()*(per-1f)+per*-scrollX);
				scrollY = -Math.round(event.getY()*(per-1f)+per*-scrollY);
				mouseMoved(event);
				repaint();
			}
		};
		addMouseListener(ml);
		addMouseMotionListener(ml);
		addMouseWheelListener(ml);
		addKeyListener(ml);
		setFocusable(true);
		generateSelectionHexagon();
	}
	private void updateNeedsSaving(){
		boolean needsSaving = needsSaving();
		WraithEngine.INSTANCE.setTitle("WraithEngine "+(needsSaving?'*':"")+WraithEngine.projectName);
	}
	private void generateSelectionHexagon(){
		int[] x = new int[4];
		int[] y = new int[4];
		int r = tileSize/2;
		int f = tileSize/4;
		x[0] = 0;
		y[0] = 0;
		x[1] = r;
		y[1] = f;
		x[2] = 0;
		y[2] = r;
		x[3] = -r;
		y[3] = f;
		selectionHexagon = new Polygon(x, y, 4);
	}
	@Override
	public boolean needsSaving(){
		if(map==null){
			return false;
		}
		return map.needsSaving();
	}
	@Override
	public void save(){
		if(map!=null){
			map.save();
			imageStorage.clear(); // To keep memory down, drop all images on save. Just in case some of the loaded tiles are no longer being used.
		}
	}
	public void selectMap(Map map){
		if(this.map!=null){
			if(needsSaving()){
				int response = JOptionPane.showConfirmDialog(null, "Map not saved! Would you like to save before exiting?", "Confirm Save Map",
					JOptionPane.YES_NO_CANCEL_OPTION);
				if(response==JOptionPane.YES_OPTION){
					this.map.save();
				}else if(response!=JOptionPane.NO_OPTION){
					return;
				}
			}
			this.map.dispose();
			imageStorage.clear();
		}
		this.map = map;
		if(map!=null){ // In case we are given a 'null' map.
			cursorSelection.setMapSize(map.getWidth(), map.getHeight());
			map.load();
		}else{
			cursorSelection.setMapSize(0, 0);
		}
		updateNeedsSaving();
		repaint();
	}
	private boolean isOnScreen(int x, int y, int w, int h){
		return x<w&&x+tileSize>=0&&y<h&&y+tileSize>=0;
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setColor(map==null?Color.darkGray:Color.lightGray);
		int width = getWidth();
		int height = getHeight();
		g.fillRect(0, 0, width, height);
		if(map!=null){
			Tile[] tiles = map.getAllTiles();
			int w = map.getWidth();
			int h = map.getHeight();
			int a, b, i, x, y, u, v;
			int maxA = w+h-1;
			for(a = 0; a<maxA; a++){
				for(b = 0; b<=a; b++){
					x = b;
					y = a-b;
					if(x>=w||y>=h){
						continue;
					}
					i = y*w+x;
					if(tiles[i]==null){
						continue;
					}
					u = (x-y)*tileWidth+scrollX-tileWidth;
					v = (x+y)*tileHeight+scrollY;
					if(isOnScreen(u, v, width, height)){
						g.drawImage(imageStorage.getImage(tiles[i]), u, v, tileSize, tileSize, null);
					}
				}
			}
			if(cursorSelection.isOnEditor()){
				g.setStroke(new BasicStroke(2));
				g.translate(cursorSelection.getScreenX(), cursorSelection.getScreenY());
				g.setColor(cursorSelection.isOverMap()?Color.white:Color.red);
				g.drawPolygon(selectionHexagon);
			}
		}
		g.dispose();
	}
}
