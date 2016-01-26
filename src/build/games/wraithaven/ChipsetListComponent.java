package build.games.wraithaven;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ChipsetListComponent{
	private final Chipset chipset;
	private boolean expanded;
	private BufferedImage image;
	public ChipsetListComponent(Chipset chipset){
		this.chipset = chipset;
	}
	public Chipset getChipset(){
		return chipset;
	}
	public BufferedImage getImage(){
		return image;
	}
	public String getName(){
		return chipset.getName();
	}
	public boolean isExpanded(){
		return expanded;
	}
	public void setExpanded(boolean expanded){
		this.expanded = expanded;
		if(expanded){
			try{
				image = ImageIO.read(Algorithms.getFile("Chipsets", chipset.getUUID(), "preview.png"));
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}else
			image = null; // To dispose unused resources and save memory.
	}
}
