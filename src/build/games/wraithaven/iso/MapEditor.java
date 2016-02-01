/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractMapEditor;
import build.games.wraithaven.util.InputAdapter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author TheDudeFromCI
 */
public class MapEditor extends AbstractMapEditor{
	private final MapImageStorage imageStorage;
	private Map map;
	private int scrollX;
	private int scrollY;
	private int tileSize = ChipsetImporter.TILE_SIZE;
	private int tileWidth = tileSize/2;
	private int tileHeight = tileSize/4;
	public MapEditor(){
		imageStorage = new MapImageStorage();
		InputAdapter ml = new InputAdapter(){
			private boolean dragging;
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
			}
			@Override
			public void mouseExited(MouseEvent event){
				dragging = false;
				repaint();
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
			}
			@Override
			public void mouseReleased(MouseEvent event){
				dragging = false;
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent event){
				if(dragging){
					return;
				}
				int change = -event.getWheelRotation()*4;
				int pixelSizeBefore = tileSize;
				tileSize = Math.max(Math.min(tileSize+change, ChipsetImporter.TILE_SIZE*4), ChipsetImporter.TILE_SIZE/4);
				tileWidth = tileSize/2;
				tileHeight = tileSize/4;
				float per = tileSize/(float)pixelSizeBefore;
				scrollX = -Math.round(event.getX()*(per-1f)+per*-scrollX);
				scrollY = -Math.round(event.getY()*(per-1f)+per*-scrollY);
				repaint();
			}
		};
		addMouseListener(ml);
		addMouseMotionListener(ml);
		addMouseWheelListener(ml);
		addKeyListener(ml);
		setFocusable(true);
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
			this.map.dispose();
			imageStorage.clear();
		}
		this.map = map;
		if(map!=null){ // In case we are given a 'null' map.
			map.load();
		}
		repaint();
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(map==null?Color.darkGray:Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(map!=null){
			Tile[] tiles = map.getAllTiles();
			int w = map.getWidth();
			int h = map.getHeight();
			int a, b, maxB, i, x, y;
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
					g.drawImage(imageStorage.getImage(tiles[i]), (x-y)*tileWidth+scrollX, (x+y)*tileHeight+scrollY, tileSize, tileSize, null);
				}
			}
		}
		g.dispose();
	}
}
