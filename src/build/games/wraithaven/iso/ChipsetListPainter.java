/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.InputAdapter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class ChipsetListPainter extends JPanel{
	private static Polygon generateCursor(){
		int[] x = new int[4];
		int[] y = new int[4];
		x[0] = 0;
		y[0] = 0;
		x[1] = PREVIEW_TILE_SCALE;
		y[1] = 0;
		x[2] = PREVIEW_TILE_SCALE;
		y[2] = PREVIEW_TILE_SCALE;
		x[3] = 0;
		y[3] = PREVIEW_TILE_SCALE;
		return new Polygon(x, y, 4);
	}
	private static final int PREVIEW_TILES_WIDTH = 8;
	private static final int PREVIEW_TILE_SCALE = 32;
	private final CursorSelection cursorSelection;
	private final Polygon cursor;
	private final MapEditorTab mapStyle;
	public ChipsetListPainter(MapEditorTab mapStyle){
		this.mapStyle = mapStyle;
		cursorSelection = new CursorSelection();
		cursor = generateCursor();
		updatePrefferedSize();
		InputAdapter ia = new InputAdapter(){
			@Override
			public void mouseClicked(MouseEvent event){
				int x = event.getX()/PREVIEW_TILE_SCALE;
				int y = event.getY()/PREVIEW_TILE_SCALE;
				int index = y*PREVIEW_TILES_WIDTH+x-1;
				ArrayList<Tile> tiles = mapStyle.getChipsetList().getSelectedCategory().getTiles();
				if(index<0||index>=tiles.size()){
					cursorSelection.setSelectedTile(null, -1);
					repaint();
					return;
				}
				cursorSelection.setSelectedTile(tiles.get(index), index);
				repaint();
			}
		};
		addMouseListener(ia);
	}
	public CursorSelection getCursorSelection(){
		return cursorSelection;
	}
	public void updateTiles(){
		updatePrefferedSize();
		cursorSelection.setSelectedTile(null, -1);
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		final int maxWidth = PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE;
		int x = PREVIEW_TILE_SCALE;
		int y = 0;
		ArrayList<Tile> tiles = mapStyle.getChipsetList().getSelectedCategory().getTiles();
		for(Tile tile : tiles){
			g.drawImage(mapStyle.getMapEditor().getImageStorage().getImage(tile), x, y, PREVIEW_TILE_SCALE, PREVIEW_TILE_SCALE, null);
			x += PREVIEW_TILE_SCALE;
			if(x==maxWidth){
				x = 0;
				y += PREVIEW_TILE_SCALE;
			}
		}
		g.setStroke(new BasicStroke(3));
		int index = cursorSelection.isTileActive()?cursorSelection.getSelectedTileIndex()+1:0;
		g.translate(index%PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE, index/PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE);
		g.setColor(Color.white);
		g.drawPolygon(cursor);
		g.dispose();
	}
	private void updatePrefferedSize(){
		try{
			ArrayList<Tile> tiles = mapStyle.getChipsetList().getSelectedCategory().getTiles();
			setPreferredSize(
				new Dimension(PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE, Math.max((int)Math.ceil((tiles.size()+1)/(double)PREVIEW_TILES_WIDTH), 150)));
		}catch(Exception exception){
			setPreferredSize(new Dimension(PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE, 150));
		}
	}
}
