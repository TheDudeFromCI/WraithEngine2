/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class RawTileImporter extends JPanel{
	private final BufferedImage tile;
	public RawTileImporter(BufferedImage tile){
		this.tile = tile;
		setPreferredSize(new Dimension(tile.getWidth()*2, tile.getHeight()*2));
	}
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(tile, 0, 0, tile.getWidth()*2, tile.getHeight()*2, null);
		g.dispose();
	}
}
