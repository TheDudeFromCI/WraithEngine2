package build.games.wraithaven;

public class Chipset {

    public static final int BIT_SIZE = 16;
    public static final int PREVIEW_TILES_WIDTH = 8;
    public static final int TILE_OUT_SIZE = 32;
    private final Tile[] tiles;
    private final String uuid;
    private String name;

    public Chipset(BinaryFile bin) {
        uuid = bin.getString();
        name = bin.getString();
        tiles = new Tile[bin.getInt()];
        for (int i = 0; i < tiles.length; i++) {
            if (bin.getBoolean()) {
                tiles[i] = new Tile(this, i);
                tiles[i].load(bin);
            }
        }
    }

    public Chipset(String uuid, Tile[] tiles, String name) {
        this.uuid = uuid;
        this.name = name;
        this.tiles = new Tile[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                this.tiles[i] = new Tile(this, i);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Tile getTile(int index) {
        return tiles[index];
    }

    public String getUUID() {
        return uuid;
    }

    public void save(BinaryFile bin) {
        byte[] uuidBytes = uuid.getBytes();
        byte[] nameBytes = name.getBytes();
        bin.allocateBytes(uuidBytes.length + nameBytes.length + 8 + 4);
        bin.addInt(uuidBytes.length);
        bin.addBytes(uuidBytes, 0, uuidBytes.length);
        bin.addInt(nameBytes.length);
        bin.addBytes(nameBytes, 0, nameBytes.length);
        bin.addInt(tiles.length);
        for (Tile tile : tiles) {
            bin.allocateBytes(1);
            if (tile == null) {
                bin.addBoolean(false);
            } else {
                bin.addBoolean(true);
                tile.save(bin);
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
