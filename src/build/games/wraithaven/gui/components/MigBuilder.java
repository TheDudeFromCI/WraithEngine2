/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class MigBuilder extends JPanel{
	private static final int BORDER_SPACE = 10;
	private static final float COLUMN_DARKNESS = 0.2f;
	private int[] rows;
	private int[] cols;
	public MigBuilder(int[] rows, int[] cols){
		this.rows = rows;
		this.cols = cols;
		setPreferredSize(new Dimension(320, 240));
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		int sizeWidth = width-BORDER_SPACE*2;
		int sizeHeight = height-BORDER_SPACE*2;
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		g.setColor(Color.white);
		g.fillRect(BORDER_SPACE, BORDER_SPACE, sizeWidth, sizeHeight);
		g.setColor(Color.black);
		{
			// Draw rows and cols.
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, COLUMN_DARKNESS));
			drawRowsAndCols(g, true, sizeWidth, sizeHeight);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			drawRowsAndCols(g, false, sizeWidth, sizeHeight);
		}
		g.dispose();
	}
	private void drawRowsAndCols(Graphics2D g, boolean fill, int sizeWidth, int sizeHeight){
		int extraSpaceCols = countExtraSpace(cols, sizeWidth);
		int extraSpaceRows = countExtraSpace(rows, sizeHeight);
		int x = BORDER_SPACE;
		int y = BORDER_SPACE;
		int w, h;
		for(int i = 0; i<cols.length; i++){
			w = cols[i]==0?extraSpaceCols:cols[i];
			if(fill){
				g.setColor(i%2==0?Color.white:Color.black);
				g.fillRect(x, BORDER_SPACE, w, sizeHeight);
			}else{
				g.setColor(Color.black);
				g.drawRect(x, BORDER_SPACE, w, sizeHeight);
			}
			x += w;
		}
		for(int i = 0; i<rows.length; i++){
			h = rows[i]==0?extraSpaceRows:rows[i];
			if(fill){
				g.setColor(i%2==0?Color.white:Color.black);
				g.fillRect(BORDER_SPACE, y, sizeWidth, h);
			}else{
				g.drawRect(BORDER_SPACE, y, sizeWidth, h);
			}
			y += h;
		}
	}
	private int countExtraSpace(int[] x, int total){
		int y = 0;
		int a = 0;
		for(int z : x){
			if(z==0){
				a++;
				continue;
			}
			y += z;
		}
		return (total-y)/a;
	}
	public void setSizes(int[] cols, int[] rows){
		this.cols = cols;
		this.rows = rows;
		repaint();
	}
}
