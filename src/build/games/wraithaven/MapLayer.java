package build.games.wraithaven;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MapLayer {

    public static final int Map_Tiles_Width = 20;
    public static final int Map_Tiles_Height = 15;
    private final BufferedImage image;
    private final Tile[] tiles;
    private final int layer;
    private int tileCount;

    public MapLayer(int layer) {
        this.layer = layer;
        image = new BufferedImage(Chipset.Bit_Size * Map_Tiles_Width, Chipset.Bit_Size * Map_Tiles_Height, BufferedImage.TYPE_INT_ARGB);
        tiles = new Tile[Map_Tiles_Width * Map_Tiles_Height];
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getLayer() {
        return layer;
    }

    public Tile getTile(int x, int y) {
        return tiles[y * Map_Tiles_Width + x];
    }

    public boolean isEmpty() {
        return tileCount == 0;
    }

    public void load(BinaryFile bin, ChipsetList chipsetList) {
        tileCount = bin.getInt();
        for (int i = 0; i < tiles.length; i++) {
            if (bin.getBoolean()) {
                try {
                    String uuid = bin.getString();
                    int id = bin.getInt();
                    tiles[i] = chipsetList.getChipset(uuid).getTile(id);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    tiles[i] = null;
                }
            } else {
                tiles[i] = null;
            }
            System.out.println(tiles[i]);
        }
        redraw();
    }

    private void redraw() {
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
        int x, y;
        int index;
        for (x = 0; x < Map_Tiles_Width; x++) {
            for (y = 0; y < Map_Tiles_Height; y++) {
                index = y * Map_Tiles_Width + x;
                if (tiles[index] == null) {
                    continue;
                }
                g.drawImage(tiles[index].getImage(), x * Chipset.Bit_Size, y * Chipset.Bit_Size, null);
            }
        }
        g.dispose();
    }

    public void save(BinaryFile bin) {
        bin.allocateBytes(4);
        bin.addInt(tileCount);
        for (int i = 0; i < tiles.length; i++) {
            bin.allocateBytes(1);
            if (tiles[i] == null) {
                bin.addBoolean(false);
            } else {
                bin.addBoolean(true);
                byte[] bytes = tiles[i].getChipset().getUUID().getBytes();
                bin.allocateBytes(bytes.length + 8);
                bin.addInt(bytes.length);
                bin.addBytes(bytes, 0, bytes.length);
                bin.addInt(tiles[i].getId());
            }
        }
    }

    public void setTile(int x, int y, Tile tile) {
        int index = y * Map_Tiles_Width + x;
        if (tiles[index] != tile && (tiles[index] == null || tile == null)) {
            if (tile == null) {
                tileCount--;
            } else {
                tileCount++;
            }
        }
        tiles[index] = tile;
        redraw();
    }
}
