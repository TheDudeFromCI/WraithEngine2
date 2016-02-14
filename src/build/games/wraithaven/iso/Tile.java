/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.Algorithms;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @author TheDudeFromCI
 */
public class Tile{
	private static BufferedImage scaleImage(BufferedImage image){
		BufferedImage buf = image;
		int s = WraithEngine.projectBitSize;
		do{
			if(s>ChipsetListPainter.PREVIEW_TILE_SCALE){
				s /= 2;
				if(s<ChipsetListPainter.PREVIEW_TILE_SCALE){
					s = ChipsetListPainter.PREVIEW_TILE_SCALE;
				}
			}else{
				s *= 2;
				if(s>ChipsetListPainter.PREVIEW_TILE_SCALE){
					s = ChipsetListPainter.PREVIEW_TILE_SCALE;
				}
			}
			BufferedImage out = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = out.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(buf, 0, 0, s, s, null);
			g.dispose();
			buf = out;
		}while(s!=ChipsetListPainter.PREVIEW_TILE_SCALE);
		return buf;
	}
	private final String uuid;
	private BufferedImage previewImage;
	public Tile(String uuid){
		this.uuid = uuid;
	}
	public Tile(String uuid, BufferedImage image){
		this.uuid = uuid;
		previewImage = scaleImage(image);
		try{
			ImageIO.write(image, "png", Algorithms.getFile("Chipsets", "Fulls", uuid+".png"));
			ImageIO.write(previewImage, "png", Algorithms.getFile("Chipsets", "Previews", uuid+".png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	public BufferedImage getPreviewImage(){
		if(previewImage==null){
			try{
				previewImage = ImageIO.read(Algorithms.getFile("Chipsets", "Previews", uuid+".png"));
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
		return previewImage;
	}
	public String getUUID(){
		return uuid;
	}
}
