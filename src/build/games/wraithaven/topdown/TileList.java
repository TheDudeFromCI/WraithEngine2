/* 
 * Copyright (C) 2016 TheDudeFromCI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.topdown.Chipset;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TileList extends JPanel {

    private BufferedImage buf;

    public TileList(BufferedImage buf) {
        if (buf == null) {
            setPreferredSize(new Dimension(Chipset.TILE_OUT_SIZE * Chipset.PREVIEW_TILES_WIDTH, 10));
            return;
        }
        this.buf = buf;
        setPreferredSize(new Dimension(buf.getWidth(), buf.getHeight()));
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(buf, 0, 0, null);
        g.dispose();
    }

    public void setPreviewImage(BufferedImage image) {
        buf = image;
        setPreferredSize(new Dimension(buf.getWidth(), buf.getHeight()));
        repaint();
    }
}
