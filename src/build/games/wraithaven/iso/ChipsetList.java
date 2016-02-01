/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.AbstractChipsetList;
import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

/**
 * @author TheDudeFromCI
 */
public class ChipsetList extends AbstractChipsetList{
	public static final int PREVIEW_TILES_WIDTH = 6;
	public static final int PREVIEW_TILE_SCALE = 48;
	private final ArrayList<Tile> tiles = new ArrayList(64);
	public ChipsetList(){
		load();
		updatePrefferedSize();
	}
	private void load(){
		File file = Algorithms.getFile("Chipsets", "List.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int tileCount = bin.getInt();
		for(int i = 0; i<tileCount; i++){
			tiles.add(new Tile(bin.getString()));
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(tiles.size());
		for(Tile tile : tiles){
			bin.addStringAllocated(tile.getUUID());
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Chipsets", "List.dat"));
	}
	public void addTile(Tile tile){
		tiles.add(tile);
		updatePrefferedSize();
		repaint();
		save();
	}
	public Tile getTile(String uuid){
		for(Tile tile : tiles){
			if(tile.getUUID().equals(uuid)){
				return tile;
			}
		}
		return null;
	}
	public ArrayList<Tile> getAllTiles(){
		return tiles;
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		final int maxWidth = PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE;
		int x = 0;
		int y = 0;
		for(Tile tile : tiles){
			g.drawImage(tile.getPreviewImage(), x, y, null);
			x += PREVIEW_TILE_SCALE;
			if(x==maxWidth){
				x = 0;
				y += PREVIEW_TILE_SCALE;
			}
		}
		g.dispose();
	}
	private void updatePrefferedSize(){
		setPreferredSize(
			new Dimension(PREVIEW_TILES_WIDTH*PREVIEW_TILE_SCALE, Math.max((int)Math.ceil(tiles.size()/(double)PREVIEW_TILES_WIDTH), 150)));
	}
}
