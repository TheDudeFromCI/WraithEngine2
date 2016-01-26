package build.games.wraithaven;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ChipsetImporter{
	private final String uuid;
	private final String name;
	private final File file;
	private BufferedImage previewImage;
	private BufferedImage[] tileImages;
	private Tile[] tiles;
	public ChipsetImporter(File file){
		this.file = file;
		uuid = UUID.randomUUID().toString();
		name = file.getName().substring(0, file.getName().length()-4);
	}
	public Chipset asChipset(){
		return new Chipset(uuid, tiles, name);
	}
	public String getName(){
		return name;
	}
	public BufferedImage getPreviewImage(){
		return previewImage;
	}
	public Tile[] getTiles(){
		return tiles;
	}
	public String getUUID(){
		return uuid;
	}
	public void saveImages(){
		for(int i = 0; i<tileImages.length; i++){
			if(tileImages[i]==null)
				continue;
			try{
				ImageIO.write(tileImages[i], "png", Algorithms.getFile("Chipsets", uuid, i+".png"));
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
		try{
			ImageIO.write(previewImage, "png", Algorithms.getFile("Chipsets", uuid, "preview.png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	public void unwrap(){
		try{
			BufferedImage image = ImageIO.read(file);
			int width = image.getWidth();
			int height = image.getHeight();
			if(width%Chipset.Bit_Size!=0||height%Chipset.Bit_Size!=0){
				JOptionPane.showMessageDialog(null, "This image is in an unknown size, and could not be parsed.", "Warning",
					JOptionPane.WARNING_MESSAGE);
				throw new RuntimeException();
			}
			width /= Chipset.Bit_Size;
			height /= Chipset.Bit_Size;
			tiles = new Tile[width*height];
			tileImages = new BufferedImage[tiles.length];
			int x, y, index;
			previewImage = new BufferedImage(Chipset.Preview_Tiles_Width*Chipset.Tile_Out_Size,
				(int)Math.ceil(tiles.length/(float)Chipset.Preview_Tiles_Width)*Chipset.Tile_Out_Size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = previewImage.createGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, previewImage.getWidth(), previewImage.getHeight());
			for(x = 0; x<width; x++){
				for(y = 0; y<height; y++){
					index =
						y*Chipset.Preview_Tiles_Width+x%Chipset.Preview_Tiles_Width+x/Chipset.Preview_Tiles_Width*Chipset.Preview_Tiles_Width*height;
					if(index>=tiles.length)
						continue;
					tiles[index] = new Tile(null, index);
					tileImages[index] = image.getSubimage(x*Chipset.Bit_Size, y*Chipset.Bit_Size, Chipset.Bit_Size, Chipset.Bit_Size);
					g.drawImage(tileImages[index], index%Chipset.Preview_Tiles_Width*Chipset.Tile_Out_Size,
						index/Chipset.Preview_Tiles_Width*Chipset.Tile_Out_Size, Chipset.Tile_Out_Size, Chipset.Tile_Out_Size, null);
					g.setColor(Color.white);
				}
			}
			g.dispose();
		}catch(Exception exception){
			exception.printStackTrace();
			JOptionPane.showMessageDialog(null, "There has been an error loading this image.", "Warning", JOptionPane.WARNING_MESSAGE);
			throw new RuntimeException();
		}
	}
}
