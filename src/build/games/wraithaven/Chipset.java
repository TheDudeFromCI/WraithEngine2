package build.games.wraithaven;

public class Chipset{
	public static final int Bit_Size = 16;
	public static final int Preview_Tiles_Width = 8;
	public static final int Tile_Out_Size = 32;
	private final Tile[] tiles;
	private final String uuid;
	private String name;
	public Chipset(BinaryFile bin){
		uuid = bin.getString();
		name = bin.getString();
		tiles = new Tile[bin.getInt()];
	}
	public Chipset(String uuid, Tile[] tiles, String name){
		this.uuid = uuid;
		this.tiles = tiles;
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public Tile getTile(int index){
		return tiles[index];
	}
	public String getUUID(){
		return uuid;
	}
	public void save(BinaryFile bin){
		byte[] uuidBytes = uuid.getBytes();
		byte[] nameBytes = name.getBytes();
		bin.allocateBytes(uuidBytes.length+nameBytes.length+8+4);
		bin.addInt(uuidBytes.length);
		bin.addBytes(uuidBytes, 0, uuidBytes.length);
		bin.addInt(nameBytes.length);
		bin.addBytes(nameBytes, 0, nameBytes.length);
		bin.addInt(tiles.length);
		// TODO Save tiles.
	}
	public void setName(String name){
		this.name = name;
	}
}
