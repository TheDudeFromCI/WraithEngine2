package build.games.wraithaven;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TileList extends JPanel{
	private BufferedImage buf;
	public TileList(BufferedImage buf){
		if(buf==null){
			setPreferredSize(new Dimension(Chipset.Tile_Out_Size*Chipset.Preview_Tiles_Width, 10));
			return;
		}
		this.buf = buf;
		setPreferredSize(new Dimension(buf.getWidth(), buf.getHeight()));
	}
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(buf, 0, 0, null);
		g.dispose();
	}
	public void setPreviewImage(BufferedImage image){
		buf = image;
		setPreferredSize(new Dimension(buf.getWidth(), buf.getHeight()));
		repaint();
	}
}
