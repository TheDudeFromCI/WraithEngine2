package build.games.wraithaven;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Tile {

    private final Chipset chipset;
    private final int id;
    private boolean passable;
    private BufferedImage image;

    public Tile(Chipset chipset, int id) {
        this.chipset = chipset;
        this.id = id;
        passable = true;
    }

    public void disposeImage() {
        // To avoid memory build up, the image should be disposed every now and then.
        // Keeping it in memory will speed up repaints, but will cause memory to heap if enough tiles are placed.
        image = null;
    }

    public Chipset getChipset() {
        return chipset;
    }

    public int getId() {
        return id;
    }

    public BufferedImage getImage() {
        if (image == null) {
            try {
                image = ImageIO.read(Algorithms.getFile("Chipsets", chipset.getUUID(), id + ".png"));
            } catch (Exception exception) {
                // If not found, then it's probably empty, black tile.
            }
        }
        return image;
    }

    public void save(BinaryFile bin) {
        bin.allocateBytes(1);
        bin.addBoolean(passable);
    }

    public void load(BinaryFile bin) {
        passable = bin.getBoolean();
    }

    public boolean isPassable() {
        return passable;
    }
}
