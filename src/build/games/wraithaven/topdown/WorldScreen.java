/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.InputAdapter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class WorldScreen extends JPanel{
	private final SelectionCursor cursor = new SelectionCursor();
	private final ChipsetTileSelection selectedTile;
	private final BufferedImage selectionImage;
	private final BufferedImage newMapImage;
	private final MapEditor mapEditor;
	private int scrollX;
	private int scrollY;
	private int pixelSize = 32;
	private int mapSectionWidth = pixelSize*MapLayer.MAP_TILES_WIDTH;
	private int mapSectionHeight = pixelSize*MapLayer.MAP_TILES_HEIGHT;
	private Map loadedMap;
	public WorldScreen(ChipsetList chipsetList, MapEditor mapEditor){
		this.mapEditor = mapEditor;
		try{
			selectionImage = ImageIO.read(Algorithms.getAsset("Selection Box.png"));
			newMapImage = ImageIO.read(Algorithms.getAsset("New Map Box.png"));
		}catch(Exception exception){
			exception.printStackTrace();
			throw new RuntimeException();
		}
		selectedTile = chipsetList.getSelectedTile();
		InputAdapter inputAdapter = new InputAdapter(){
			private boolean dragging;
			private boolean drawing;
			private int scrollXStart;
			private int scrollYStart;
			private int mouseXStart;
			private int mouseYStart;
			private int clickCount;
			@Override
			public void keyPressed(KeyEvent event){
				int code = event.getKeyCode();
				switch(code){
					case KeyEvent.VK_S:
						if(event.isControlDown()){
							if(loadedMap!=null){
								loadedMap.save();
							}
						}
						break;
				}
			}
			public void mouseClicked(int x, int y, int button, boolean shift){
				if(loadedMap==null){
					return;
				}
				clickCount++;
				if(!cursor.isSeen()){
					return; // If the cursor isn't seen, it's probably off screen or something, and couldn't set tiles anyway.
				}
				if(cursor.isOverVoid()){
					if(clickCount>1){
						return;
					}
					// New map
					int mapX = (int)Math.floor(cursor.getX()/(float)MapLayer.MAP_TILES_WIDTH);
					int mapY = (int)Math.floor(cursor.getY()/(float)MapLayer.MAP_TILES_HEIGHT);
					// Check to make sure the map doesn't already exist, just in case.
					for(MapSection map : loadedMap.getMapSections()){
						if(map.getMapX()==mapX&&map.getMapY()==mapY){
							return; // Does exist, this is probably a repeated event or something.
						}
					}
					loadedMap.addMapSection(new MapSection(chipsetList, mapEditor.getToolbar(), loadedMap, mapX, mapY));
					mouseMoved(x, y);
					repaint();
				}else{
					if(button!=MouseEvent.BUTTON1&&button!=MouseEvent.BUTTON2) // If not left click, or middle click, do nothing.
					{
						return;
					}
					// Set Tile
					int mapX, mapY;
					for(MapSection map : loadedMap.getMapSections()){
						mapX = map.getMapX()*mapSectionWidth+scrollX;
						mapY = map.getMapY()*mapSectionHeight+scrollY;
						if(x>=mapX&&y>=mapY&&x<mapX+mapSectionWidth&&y<mapY+mapSectionHeight){
							if(button==MouseEvent.BUTTON2){ // Middle Click
								int mx = (x-mapX)/pixelSize;
								int my = (y-mapY)/pixelSize;
								Tile tile = map.getTile(mx, my, mapEditor.getToolbar().getEditingLayer());
								if(tile==null){
									selectedTile.reset();
									chipsetList.repaint();
									return;
								}
								selectedTile.select(tile.getChipset(), tile.getId(), tile.getId()%Chipset.PREVIEW_TILES_WIDTH,
									tile.getId()/Chipset.PREVIEW_TILES_WIDTH);
								for(ChipsetListComponent list : chipsetList.getChipsets()){
									if(list.getChipset()==selectedTile.getChipset()){
										// This is just to ensure that the desired chipset is actually selected.
										list.setExpanded(true);
										break;
									}
								}
								chipsetList.repaint();
							}else if(shift){
								final MapSection m = map;
								new SwingWorker<Boolean,Boolean>(){
									@Override
									protected Boolean doInBackground() throws Exception{
										int response = JOptionPane.showConfirmDialog(null,
											"Are you sure you want to delete this map section? This cannot be undone!", "Confirm Delete",
											JOptionPane.YES_NO_OPTION);
										return response==JOptionPane.YES_OPTION;
									}
									@Override
									protected void done(){
										try{
											if(get()){
												m.delete();
												loadedMap.removeMapSection(m);
												repaint();
											}
										}catch(Exception exception){
											exception.printStackTrace();
										}
									}
								}.execute();
							}else{
								if(selectedTile.isActive()){
									map.setTile((x-mapX)/pixelSize, (y-mapY)/pixelSize, mapEditor.getToolbar().getEditingLayer(),
										selectedTile.getChipset().getTile(selectedTile.getIndex()));
								}else{
									map.setTile((x-mapX)/pixelSize, (y-mapY)/pixelSize, mapEditor.getToolbar().getEditingLayer(), null);
								}
								updateNeedsSaving();
								repaint();
							}
							return;
						}
					}
				}
			}
			@Override
			public void mouseClicked(MouseEvent event){
				mouseClicked(event.getX(), event.getY(), event.getButton(), event.isShiftDown());
			}
			@Override
			public void mouseDragged(MouseEvent event){
				if(dragging){
					scrollX = event.getX()-mouseXStart+scrollXStart;
					scrollY = event.getY()-mouseYStart+scrollYStart;
					repaint();
				}else if(drawing){
					mouseClicked(event.getX(), event.getY(), 1, event.isShiftDown());
				}
				mouseMoved(event); // To update the cursor.
			}
			@Override
			public void mouseEntered(MouseEvent event){
				mouseMoved(event);
			}
			@Override
			public void mouseExited(MouseEvent event){
				dragging = false;
				drawing = false;
				cursor.hide();
				repaint();
			}
			public void mouseMoved(int x, int y){
				if(loadedMap==null){
					return;
				}
				cursor.moveTo((int)Math.floor((x-scrollX)/(float)pixelSize), (int)Math.floor((y-scrollY)/(float)pixelSize));
				boolean overVoid = true;
				int mapX, mapY;
				for(MapSection map : loadedMap.getMapSections()){
					mapX = map.getMapX()*mapSectionWidth+scrollX;
					mapY = map.getMapY()*mapSectionHeight+scrollY;
					if(x>=mapX&&y>=mapY&&x<mapX+mapSectionWidth&&y<mapY+mapSectionHeight){
						overVoid = false;
						break;
					}
				}
				cursor.setOverVoid(overVoid);
				repaint();
			}
			@Override
			public void mouseMoved(MouseEvent event){
				mouseMoved(event.getX(), event.getY());
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
				if(button==MouseEvent.BUTTON1){
					clickCount = 0;
					drawing = true;
				}else{
					drawing = false;
				}
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
				int change = -event.getWheelRotation();
				int pixelSizeBefore = pixelSize;
				pixelSize = Math.max(Math.min(pixelSize+change, 64), 8);
				mapSectionWidth = pixelSize*MapLayer.MAP_TILES_WIDTH;
				mapSectionHeight = pixelSize*MapLayer.MAP_TILES_HEIGHT;
				float per = pixelSize/(float)pixelSizeBefore;
				scrollX = -Math.round(event.getX()*(per-1f)+per*-scrollX);
				scrollY = -Math.round(event.getY()*(per-1f)+per*-scrollY);
				mouseMoved(event); // To update the cursor.
				repaint();
			}
		};
		addMouseListener(inputAdapter);
		addMouseMotionListener(inputAdapter);
		addMouseWheelListener(inputAdapter);
		addKeyListener(inputAdapter);
		setFocusable(true);
	}
	public void save(){
		if(loadedMap!=null){
			loadedMap.save();
			updateNeedsSaving();
		}
	}
	public void selectMap(Map map){
		if(loadedMap!=null){
			if(needsSaving()){
				int response = JOptionPane.showConfirmDialog(null, "Map not saved! Would you like to save before exiting?", "Confirm Save Map",
					JOptionPane.YES_NO_CANCEL_OPTION);
				if(response==JOptionPane.YES_OPTION){
					loadedMap.save();
				}else if(response!=JOptionPane.NO_OPTION){
					return;
				}
			}
			loadedMap.dispose();
		}
		loadedMap = map;
		map.loadMaps();
		repaint();
		updateNeedsSaving(); // Mostly, this is just to disable the star. This should never say 'unsaved'.
	}
	public void updateNeedsSaving(){
		boolean toSave = needsSaving();
		WraithEngine.INSTANCE.setTitle("WraithEngine "+(toSave?'*':"")+WraithEngine.projectName);
		if(mapEditor.getToolbar()!=null){
			mapEditor.getToolbar().setNeedsSaving(toSave);
		}
	}
	public boolean needsSaving(){
		if(loadedMap!=null){
			return loadedMap.needsSaving();
		}
		return false;
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		g.setColor(loadedMap==null?Color.darkGray:Color.gray);
		g.fillRect(0, 0, width, height);
		if(loadedMap!=null){
			int x, y;
			g.setColor(Color.blue);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for(MapSection map : loadedMap.getMapSections()){
				x = mapSectionWidth*map.getMapX()+scrollX;
				y = mapSectionHeight*map.getMapY()+scrollY;
				// Check is map is on the screen.
				if(x+mapSectionWidth>=0&&y+mapSectionHeight>=0&&x<width&&y<height){
					g.drawImage(map.getImage(), x, y, mapSectionWidth, mapSectionHeight, null);
					g.drawRect(x, y, mapSectionWidth, mapSectionHeight);
				}
			}
			if(cursor.isSeen()){
				if(cursor.isOverVoid()){
					g.drawImage(newMapImage, (int)Math.floor(cursor.getX()/(float)MapLayer.MAP_TILES_WIDTH)*mapSectionWidth+scrollX,
						(int)Math.floor(cursor.getY()/(float)MapLayer.MAP_TILES_HEIGHT)*mapSectionHeight+scrollY, mapSectionWidth, mapSectionHeight,
						null);
				}else{
					g.drawImage(selectionImage, cursor.getX()*pixelSize+scrollX, cursor.getY()*pixelSize+scrollY, pixelSize, pixelSize, null);
				}
			}
		}
		g.dispose();
	}
	public void redrawAllMapSections(){
		if(loadedMap==null){
			return;
		}
		for(MapSection map : loadedMap.getMapSections()){
			map.redraw();
		}
		repaint();
	}
}
