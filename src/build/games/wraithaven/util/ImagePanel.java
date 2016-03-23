/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class ImagePanel extends JPanel{
	private final Dimension lastSize = new Dimension();
	private final Rectangle rec = new Rectangle();
	private BufferedImage image;
	private boolean fill;
	public ImagePanel(BufferedImage image){
		this.image = image;
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}
	public ImagePanel(BufferedImage image, int width, int height){
		this.image = image;
		setPreferredSize(new Dimension(width, height));
	}
	public void setImage(BufferedImage image){
		this.image = image;
		updateRectangle(true);
		repaint();
	}
	public void setImageSize(int width, int height){
		setPreferredSize(new Dimension(width, height));
		getParent().revalidate();
		repaint();
	}
	public void setFill(boolean fill){
		this.fill = fill;
		updateRectangle(true);
		repaint();
	}
	public boolean isFill(){
		return fill;
	}
	private void updateRectangle(boolean force){
		if(image==null){
			return;
		}
		int width = getWidth();
		int height = getHeight();
		if(!force){
			if(width==lastSize.width&&height==lastSize.height){
				return;
			}
		}
		rec.width = image.getWidth();
		rec.height = image.getHeight();
		lastSize.width = width;
		lastSize.height = height;
		if(image.getWidth()>width){
			rec.width = width;
			rec.height = (rec.width*image.getHeight())/image.getWidth();
		}
		if(rec.height>height){
			rec.height = height;
			rec.width = (rec.height*image.getWidth())/image.getHeight();
		}
		rec.x = (width-rec.width)/2;
		rec.y = (height-rec.height)/2;
	}
	@Override
	public void paintComponent(Graphics g1){
		updateRectangle(false);
		Graphics2D g = (Graphics2D)g1;
		int width = getWidth();
		int height = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		if(image!=null){
			// Turn on some quality, to account for loss with image scaling.
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(image, rec.x, rec.y, rec.width, rec.height, null);
		}
		g.dispose();
	}
}
