package build.games.wraithaven;

public class SelectionCursor {

    private boolean seen;
    private int x;
    private int y;
    private boolean overVoid;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void hide() {
        seen = false;
    }

    public boolean isOverVoid() {
        return overVoid;
    }

    public boolean isSeen() {
        return seen;
    }

    public void moveTo(int x, int y) {
        seen = true;
        this.x = x;
        this.y = y;
    }

    public void setOverVoid(boolean overVoid) {
        this.overVoid = overVoid;
    }
}
