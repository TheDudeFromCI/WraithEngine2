/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.core.tools.Tool;
import build.games.wraithaven.util.InputAdapter;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class MapEditorPainter extends JPanel{
	private final MapImageStorage imageStorage;
	private final CursorSelection cursorSelection;
	private final Toolbar toolbar;
	private final MapEditor mapEditor;
	private final IsoMapStyle mapStyle;
	private Map map;
	private int tileSize;
	private int tileWidth;
	private int tileHeight;
	private int scrollX;
	private int scrollY;
	private Polygon selectionSquare;
	private Polygon mapBorder;
	private Polygon isoCubeBorder;
	private Tool tool;
	public MapEditorPainter(IsoMapStyle mapStyle, Toolbar toolbar, MapEditor mapEditor){
		this.mapStyle = mapStyle;
		this.toolbar = toolbar;
		this.mapEditor = mapEditor;
		setTool(Tool.BASIC);
		tileSize = WraithEngine.projectBitSize;
		tileWidth = tileSize/2;
		tileHeight = tileSize/4;
		this.cursorSelection = mapStyle.getChipsetList().getCursorSelection();
		imageStorage = new MapImageStorage();
		InputAdapter ml = new InputAdapter(){
			private boolean dragging;
			private boolean drawing;
			private int scrollXStart;
			private int scrollYStart;
			private int mouseXStart;
			private int mouseYStart;
			private int drawStartTileX;
			private int drawStartTileY;
			private int drawBoundsX;
			private int drawBoundsY;
			private int drawBoundsX2;
			private int drawBoundsY2;
			@Override
			public void mouseDragged(MouseEvent event){
				if(dragging){
					scrollX = event.getX()-mouseXStart+scrollXStart;
					scrollY = event.getY()-mouseYStart+scrollYStart;
					repaint();
				}
				if(drawing){
					mouseClicked(event.getX(), event.getY(), 1, event.isShiftDown());
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
				if(map==null){
					return;
				}
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
				mouseClicked(event.getX(), event.getY(), event.getButton(), event.isShiftDown());
			}
			public void mouseClicked(int x, int y, int button, boolean shift){
				if(map==null){
					return;
				}
				if(button==MouseEvent.BUTTON1){
					if(cursorSelection.isOverMap()){
						if(cursorSelection.isTileMode()){
							if(!tool.isDragBased()){
								switch(tool){
									case BASIC:
										map.setTile(cursorSelection.getTileX(), cursorSelection.getTileY(), cursorSelection.getSelectedTile());
										updateNeedsSaving();
										repaint();
										break;
									case FILL:
										IsoMapFillable fillable = new IsoMapFillable(map);
										fillable.fill(cursorSelection.getTileX(), cursorSelection.getTileY(), cursorSelection.getSelectedTile());
										updateNeedsSaving();
										repaint();
										break;
									default:
										throw new RuntimeException();
								}
							}
						}else if(cursorSelection.isEntityMode()){
							TileInstance tile = map.getTile(cursorSelection.getTileX(), cursorSelection.getTileY());
							if(tile!=null){
								tile.setEntity(cursorSelection.getSelectedEntity());
								map.setNeedsSaving();
								updateNeedsSaving();
								repaint();
							}
						}
					}
				}else if(button==MouseEvent.BUTTON2){
					if(cursorSelection.isOverMap()){
						TileInstance tile = map.getTile(cursorSelection.getTileX(), cursorSelection.getTileY());
						if(shift){
							cursorSelection.setSelectedEntity(tile.getEntity(),
								mapStyle.getChipsetList().getEntityList().getAllTypes().indexOf(tile.getEntity()));
							mapStyle.getChipsetList().repaint();
						}else{
							cursorSelection.setSelectedTile(tile==null?null:tile.getTile(),
								tile==null?-1:mapStyle.getChipsetList().getIndexOfTile(tile.getTile()));
							mapStyle.getChipsetList().repaint();
						}
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent event){
				if(map==null){
					return;
				}
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
				drawing = false;
				if(button==MouseEvent.BUTTON1){
					if(tool.isDragBased()){
						if(cursorSelection.isOverMap()){
							drawStartTileX = cursorSelection.getTileX();
							drawStartTileY = cursorSelection.getTileY();
							drawing = true;
							drawBoundsX = 0;
							drawBoundsY = 0;
							drawBoundsX2 = 0;
							drawBoundsY2 = 0;
						}
					}else{
						drawing = true;
					}
				}
			}
			@Override
			public void mouseReleased(MouseEvent event){
				if(map!=null){
					if(drawing&&tool.isDragBased()&&cursorSelection.isOverMap()){
						IsoMapFillable fillable = new IsoMapFillable(map);
						switch(tool){
							case RECTANGLE:
								fillable.rectangle(drawStartTileX, drawStartTileY, cursorSelection.getTileX(), cursorSelection.getTileY(),
									cursorSelection.getSelectedTile());
								break;
							case CIRCLE:
								fillable.circle(drawStartTileX, drawStartTileY, cursorSelection.getTileX(), cursorSelection.getTileY(),
									cursorSelection.getSelectedTile());
								break;
							default:
								throw new RuntimeException();
						}
						updateNeedsSaving();
						repaint();
					}
				}
				dragging = false;
				drawing = false;
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent event){
				if(map==null){
					return;
				}
				if(dragging||drawing){
					return;
				}
				if(event.isShiftDown()){
					if(cursorSelection.isOverMap()){
						TileInstance tile = map.getTile(cursorSelection.getTileX(), cursorSelection.getTileY());
						if(tile!=null){
							tile.setHeight(tile.getHeight()-event.getWheelRotation());
							map.setNeedsSaving();
							updateNeedsSaving();
							repaint();
						}
					}
				}else{
					int change = -event.getWheelRotation()*4;
					int pixelSizeBefore = tileSize;
					tileSize = Math.max(Math.min(tileSize+change, WraithEngine.projectBitSize*4), WraithEngine.projectBitSize/4);
					tileWidth = tileSize/2;
					tileHeight = tileSize/4;
					generateSelectionSquare();
					generateMapBorder();
					generateIsoCubeBorder();
					float per = tileSize/(float)pixelSizeBefore;
					scrollX = -Math.round(event.getX()*(per-1f)+per*-scrollX);
					scrollY = -Math.round(event.getY()*(per-1f)+per*-scrollY);
					mouseMoved(event);
					repaint();
				}
			}
		};
		addMouseListener(ml);
		addMouseMotionListener(ml);
		addMouseWheelListener(ml);
		addKeyListener(ml);
		setFocusable(true);
		generateSelectionSquare();
		generateIsoCubeBorder();
	}
	public void setTool(Tool tool){
		this.tool = tool;
		setCursor(tool.getCursor());
	}
	public void updateNeedsSaving(){
		boolean needsSaving = mapEditor.needsSaving();
		mapStyle.getFrame().setTitle("WraithEngine "+(needsSaving?'*':"")+WraithEngine.projectName);
		toolbar.setNeedsSaving(needsSaving);
	}
	private void generateIsoCubeBorder(){
		int[] x = new int[6];
		int[] y = new int[6];
		int r = tileSize/2;
		int f = tileSize/4;
		x[0] = 0;
		y[0] = -r;
		x[1] = r;
		y[1] = -f;
		x[2] = r;
		y[2] = f;
		x[3] = 0;
		y[3] = r;
		x[4] = -r;
		y[4] = f;
		x[5] = -r;
		y[5] = -f;
		isoCubeBorder = new Polygon(x, y, 6);
	}
	public Map getMap(){
		return map;
	}
	private void generateSelectionSquare(){
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
		selectionSquare = new Polygon(x, y, 4);
	}
	private void generateMapBorder(){
		if(map==null){
			return;
		}
		int[] x = new int[4];
		int[] y = new int[4];
		int w = map.getWidth();
		int h = map.getHeight();
		x[0] = 0;
		y[0] = 0;
		x[1] = w*tileWidth;
		y[1] = w*tileHeight;
		x[2] = (w-h)*tileWidth;
		y[2] = (w+h)*tileHeight;
		x[3] = -h*tileWidth;
		y[3] = h*tileHeight;
		mapBorder = new Polygon(x, y, 4);
	}
	public void selectMap(Map map){
		if(this.map!=null){
			if(mapEditor.needsSaving()){
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
			generateMapBorder();
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
			TileInstance[] tiles = map.getAllTiles();
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
					v = (x+y)*tileHeight+scrollY-tiles[i].getHeight()*tileSize/8;
					if(isOnScreen(u, v, width, height)){
						g.drawImage(imageStorage.getImage(tiles[i].getTile()), u, v, tileSize, tileSize, null);
						if(x==cursorSelection.getTileX()&&y==cursorSelection.getTileY()){
							Composite com = g.getComposite();
							g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
							g.setColor(Color.white);
							g.translate(u+tileWidth, v+tileWidth);
							g.fillPolygon(isoCubeBorder);
							g.translate(-u-tileWidth, -v-tileWidth);
							g.setComposite(com);
						}
						if(tiles[i].getEntity()!=null){
							g.drawImage(imageStorage.getImage(tiles[i].getEntity()), u+(int)(tiles[i].getEntity().getOffsetX()*tileSize),
								v+(int)(tiles[i].getEntity().getOffsetY()*tileSize), tileSize, tileSize*2, null);
						}
					}
				}
			}
			g.setColor(Color.blue);
			g.translate(scrollX, scrollY);
			g.drawPolygon(mapBorder);
			g.translate(-scrollX, -scrollY);
			if(cursorSelection.isOnEditor()){
				g.setStroke(new BasicStroke(2));
				g.setColor(cursorSelection.isOverMap()?Color.white:Color.red);
				g.translate(cursorSelection.getScreenX(), cursorSelection.getScreenY());
				g.drawPolygon(selectionSquare);
				g.translate(-cursorSelection.getScreenX(), -cursorSelection.getScreenY());
			}
		}
		g.dispose();
	}
	public MapImageStorage getMapImageStorage(){
		return imageStorage;
	}
}
