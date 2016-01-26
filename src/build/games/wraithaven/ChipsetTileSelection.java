package build.games.wraithaven;

public class ChipsetTileSelection {

    private Chipset chipset;
    private int index;
    private int x;
    private int y;

    public Chipset getChipset() {
        return chipset;
    }

    public int getIndex() {
        return index;
    }

    public int getSelectionX() {
        return x;
    }

    public int getSelectionY() {
        return y;
    }

    public boolean isActive() {
        return chipset != null;
    }

    public void reset() {
        chipset = null;
    }

    public void select(Chipset chipset, int index, int x, int y) {
        this.chipset = chipset;
        this.index = index;
        this.x = x;
        this.y = y;
    }
}
