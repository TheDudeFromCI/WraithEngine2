/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractChipsetList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class ChipsetList extends AbstractChipsetList{
	public static final int PREVIEW_TILES_WIDTH = 4;
	private final ArrayList<Tile> tiles = new ArrayList(64);
	public ChipsetList(){
		updatePrefferedSize();
	}
	public void addTile(Tile tile){
		tiles.add(tile);
		updatePrefferedSize();
		repaint();
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		final int maxWidth = PREVIEW_TILES_WIDTH*ChipsetImporter.TILE_SIZE;
		int x = 0;
		int y = 0;
		for(Tile tile : tiles){
			g.drawImage(tile.getImage(), x, y, null);
			x += ChipsetImporter.TILE_SIZE;
			if(x==maxWidth){
				x = 0;
				y += ChipsetImporter.TILE_SIZE;
			}
		}
		g.dispose();
	}
	private void updatePrefferedSize(){
		setPreferredSize(
			new Dimension(PREVIEW_TILES_WIDTH*ChipsetImporter.TILE_SIZE, Math.max((int)Math.ceil(tiles.size()/(double)PREVIEW_TILES_WIDTH), 150)));
	}
}
