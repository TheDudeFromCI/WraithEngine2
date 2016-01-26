package build.games.wraithaven;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MapLayer {

    public static final int MAP_TILES_WIDTH = 20;
    public static final int MAP_TILES_HEIGHT = 15;
    private final BufferedImage image;
    private final Tile[] tiles;
    private final int layer;
    private int tileCount;

    public MapLayer(int layer) {
        this.layer = layer;
        image = new BufferedImage(Chipset.BIT_SIZE * MAP_TILES_WIDTH, Chipset.BIT_SIZE * MAP_TILES_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        tiles = new Tile[MAP_TILES_WIDTH * MAP_TILES_HEIGHT];
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getLayer() {
        return layer;
    }

    public Tile getTile(int x, int y) {
        return tiles[y * MAP_TILES_WIDTH + x];
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
        }
        redraw();
    }

    private void redraw() {
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
        int x, y;
        int index;
        for (x = 0; x < MAP_TILES_WIDTH; x++) {
            for (y = 0; y < MAP_TILES_HEIGHT; y++) {
                index = y * MAP_TILES_WIDTH + x;
                if (tiles[index] == null) {
                    continue;
                }
                g.drawImage(tiles[index].getImage(), x * Chipset.BIT_SIZE, y * Chipset.BIT_SIZE, null);
            }
        }
        g.dispose();
    }

    public void save(BinaryFile bin) {
        bin.allocateBytes(4);
        bin.addInt(tileCount);
        for (Tile tile : tiles) {
            bin.allocateBytes(1);
            if (tile == null) {
                bin.addBoolean(false);
            } else {
                bin.addBoolean(true);
                byte[] bytes = tile.getChipset().getUUID().getBytes();
                bin.allocateBytes(bytes.length + 8);
                bin.addInt(bytes.length);
                bin.addBytes(bytes, 0, bytes.length);
                bin.addInt(tile.getId());
            }
        }
    }

    public void setTile(int x, int y, Tile tile) {
        int index = y * MAP_TILES_WIDTH + x;
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
