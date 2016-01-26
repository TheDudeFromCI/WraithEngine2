package build.games.wraithaven;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class MapSection {

    private final ArrayList<MapLayer> layers = new ArrayList<MapLayer>();
    private final BufferedImage image;
    private final int mapX;
    private final int mapY;
    private boolean needsSaving;

    public MapSection(ChipsetList chipsetList, int mapX, int mapY) {
        this.mapX = mapX;
        this.mapY = mapY;
        image = new BufferedImage(Chipset.Bit_Size * MapLayer.Map_Tiles_Width, Chipset.Bit_Size * MapLayer.Map_Tiles_Height, BufferedImage.TYPE_INT_ARGB);
        needsSaving = true;
        load(chipsetList);
        redraw();
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public Tile getTile(int x, int y, int z) {
        for (MapLayer layer : layers) {
            if (layer.getLayer() == z) {
                return layer.getTile(x, y);
            }
        }
        return null;
    }

    private void load(ChipsetList chipsetList) {
        File file = Algorithms.getFile("Worlds", mapX + "," + mapY + ".dat");
        if (!file.exists()) {
            return;
        }
        BinaryFile bin = new BinaryFile(file);
        bin.decompress(false);
        int layerCount = bin.getInt();
        for (int i = 0; i < layerCount; i++) {
            MapLayer l = new MapLayer(bin.getInt());
            l.load(bin, chipsetList);
            layers.add(l);
        }
        needsSaving = false;
    }

    public boolean needsSaving() {
        return needsSaving;
    }

    public void redraw() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        for (MapLayer layer : layers) {
            g.drawImage(layer.getImage(), 0, 0, null);
        }
        g.dispose();
    }

    public void save() {
        if (!needsSaving) {
            return;
        }
        needsSaving = false;
        BinaryFile bin = new BinaryFile(4 + layers.size() * 4);
        bin.addInt(layers.size());
        for (MapLayer layer : layers) {
            bin.addInt(layer.getLayer());
            layer.save(bin);
        }
        bin.compress(false);
        bin.compile(Algorithms.getFile("Worlds", mapX + "," + mapY + ".dat"));
    }

    public void setTile(int x, int y, int z, Tile tile) {
        needsSaving = true;
        MapLayer layer = null;
        for (MapLayer l : layers) {
            if (l.getLayer() == z) {
                layer = l;
                break;
            }
        }
        if (layer == null && tile == null) {
            return;
        }
        if (layer == null) {
            layer = new MapLayer(z);
            layers.add(layer);
        }
        layer.setTile(x, y, tile);
        if (layer.isEmpty()) {
            layers.remove(layer);
        }
        redraw();
    }
}
