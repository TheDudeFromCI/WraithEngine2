package build.games.wraithaven;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class WorldScreen extends JPanel {

    private final ArrayList<MapSection> mapSections = new ArrayList<MapSection>();
    private final SelectionCursor cursor = new SelectionCursor();
    private final ChipsetTileSelection selectedTile;
    private final BufferedImage selectionImage;
    private final BufferedImage newMapImage;
    private int scrollX;
    private int scrollY;
    private int pixelSize = 32;
    private int mapSectionWidth = pixelSize * MapLayer.Map_Tiles_Width;
    private int mapSectionHeight = pixelSize * MapLayer.Map_Tiles_Height;

    public WorldScreen(WorldBuilder worldBuilder) {
        try {
            selectionImage = ImageIO.read(Algorithms.getAsset("Selection Box.png"));
            newMapImage = ImageIO.read(Algorithms.getAsset("New Map Box.png"));
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException();
        }
        selectedTile = worldBuilder.getChipsetList().getSelectedTile();
        InputAdapter inputAdapter = new InputAdapter() {
            private boolean dragging;
            private boolean drawing;
            private int scrollXStart;
            private int scrollYStart;
            private int mouseXStart;
            private int mouseYStart;
            private boolean shift;

            @Override
            public void keyPressed(KeyEvent event) {
                int code = event.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_SHIFT:
                        shift = true;
                        break;
                    case KeyEvent.VK_S:
                        save();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shift = false;
                }
            }

            public void mouseClicked(int x, int y, int button) {
                if (!cursor.isSeen()) {
                    return; // If the cursor isn't seen, it's probably off screen or something, and couldn't set tiles anyway.
                }
                if (cursor.isOverVoid()) {
                    // New map
                    int mapX = (int) Math.floor(cursor.getX() / (float) MapLayer.Map_Tiles_Width);
                    int mapY = (int) Math.floor(cursor.getY() / (float) MapLayer.Map_Tiles_Height);
                    // Check to make sure the map doesn't already exist, just in case.
                    for (MapSection map : mapSections) {
                        if (map.getMapX() == mapX && map.getMapY() == mapY) {
                            return; // Does exist, this is probably a repeated event or something.
                        }
                    }
                    mapSections.add(new MapSection(worldBuilder.getChipsetList(), mapX, mapY));
                    mouseMoved(x, y);
                    repaint();
                } else {
                    if (button != MouseEvent.BUTTON1 && button != MouseEvent.BUTTON2) // If not left click, or middle click, do nothing.
                    {
                        return;
                    }
                    // Set Tile
                    int mapX, mapY;
                    for (MapSection map : mapSections) {
                        mapX = map.getMapX() * mapSectionWidth + scrollX;
                        mapY = map.getMapY() * mapSectionHeight + scrollY;
                        if (x >= mapX && y >= mapY && x < mapX + mapSectionWidth && y < mapY + mapSectionHeight) {
                            if (button == MouseEvent.BUTTON2) { // Middle Click
                                int mx = (x - mapX) / pixelSize;
                                int my = (y - mapY) / pixelSize;
                                Tile tile = map.getTile(mx, my, 0);
                                if (tile == null) {
                                    return;
                                }
                                selectedTile.select(tile.getChipset(), tile.getId(), tile.getId() % Chipset.Preview_Tiles_Width,
                                        tile.getId() / Chipset.Preview_Tiles_Width);
                                for (ChipsetListComponent list : worldBuilder.getChipsetList().getChipsets()) {
                                    if (list.getChipset() == selectedTile.getChipset()) {
                                        // This is just to ensure that the desired chipset is actually selected.
                                        list.setExpanded(true);
                                        break;
                                    }
                                }
                                worldBuilder.getChipsetList().repaint();
                            } else if (shift) {
                                shift = false;
                                final MapSection m = map;
                                new SwingWorker<Boolean, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground() throws Exception {
                                        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this map section?",
                                                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                                        return response == JOptionPane.YES_OPTION;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            if (get()) {
                                                mapSections.remove(m);
                                                repaint();
                                            }
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    }
                                }.execute();
                            } else if (selectedTile.isActive()) {
                                map.setTile((x - mapX) / pixelSize, (y - mapY) / pixelSize, 0,
                                        new Tile(selectedTile.getChipset(), selectedTile.getIndex()));
                                repaint();
                            }
                            return;
                        }
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                mouseClicked(event.getX(), event.getY(), event.getButton());
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (dragging) {
                    scrollX = event.getX() - mouseXStart + scrollXStart;
                    scrollY = event.getY() - mouseYStart + scrollYStart;
                    repaint();
                } else if (drawing) {
                    mouseClicked(event.getX(), event.getY(), 1);
                }
                mouseMoved(event); // To update the cursor.
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                mouseMoved(event);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                dragging = false;
                drawing = false;
                cursor.hide();
                repaint();
            }

            public void mouseMoved(int x, int y) {
                cursor.moveTo((int) Math.floor((x - scrollX) / (float) pixelSize), (int) Math.floor((y - scrollY) / (float) pixelSize));
                boolean overVoid = true;
                int mapX, mapY;
                for (MapSection map : mapSections) {
                    mapX = map.getMapX() * mapSectionWidth + scrollX;
                    mapY = map.getMapY() * mapSectionHeight + scrollY;
                    if (x >= mapX && y >= mapY && x < mapX + mapSectionWidth && y < mapY + mapSectionHeight) {
                        overVoid = false;
                        break;
                    }
                }
                cursor.setOverVoid(overVoid);
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                mouseMoved(event.getX(), event.getY());
            }

            @Override
            public void mousePressed(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON3) {
                    dragging = true;
                    scrollXStart = scrollX;
                    scrollYStart = scrollY;
                    mouseXStart = event.getX();
                    mouseYStart = event.getY();
                } else {
                    dragging = false;
                }
                drawing = event.getButton() == MouseEvent.BUTTON1;
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                dragging = false;
                drawing = false;
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                if (dragging || drawing) {
                    return;
                }
                int change = -event.getWheelRotation();
                int pixelSizeBefore = pixelSize;
                pixelSize = Math.max(Math.min(pixelSize + change, 64), 8);
                mapSectionWidth = pixelSize * MapLayer.Map_Tiles_Width;
                mapSectionHeight = pixelSize * MapLayer.Map_Tiles_Height;
                float per = pixelSize / (float) pixelSizeBefore;
                scrollX = -Math.round(event.getX() * (per - 1f) + per * -scrollX);
                scrollY = -Math.round(event.getY() * (per - 1f) + per * -scrollY);
                mouseMoved(event); // To update the cursor.
                repaint();
            }
        };
        addMouseListener(inputAdapter);
        addMouseMotionListener(inputAdapter);
        addMouseWheelListener(inputAdapter);
        addKeyListener(inputAdapter);
        setFocusable(true);
        load(worldBuilder.getChipsetList());
    }

    private void load(ChipsetList chipsetList) {
        File file = Algorithms.getFile("Worlds", "Sections.dat");
        if (!file.exists()) {
            return;
        }
        BinaryFile bin = new BinaryFile(file);
        bin.decompress(false);
        int size = bin.getInt();
        for (int i = 0; i < size; i++) {
            mapSections.add(new MapSection(chipsetList, bin.getInt(), bin.getInt()));
        }
    }

    public boolean needsSaving() {
        for (MapSection map : mapSections) {
            if (map.needsSaving()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        int width = getWidth();
        int height = getHeight();
        g.setColor(Color.gray);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.blue);
        int x, y;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (MapSection map : mapSections) {
            x = mapSectionWidth * map.getMapX() + scrollX;
            y = mapSectionHeight * map.getMapY() + scrollY;
            // Check is map is on the screen.
            if (x + mapSectionWidth >= 0 && y + mapSectionHeight >= 0 && x < width && y < height) {
                g.drawImage(map.getImage(), x, y, mapSectionWidth, mapSectionHeight, null);
                g.drawRect(x, y, mapSectionWidth, mapSectionHeight);
            }
        }
        if (cursor.isSeen()) {
            if (cursor.isOverVoid()) {
                g.drawImage(newMapImage, (int) Math.floor(cursor.getX() / (float) MapLayer.Map_Tiles_Width) * mapSectionWidth + scrollX,
                        (int) Math.floor(cursor.getY() / (float) MapLayer.Map_Tiles_Height) * mapSectionHeight + scrollY, mapSectionWidth, mapSectionHeight, null);
            } else {
                g.drawImage(selectionImage, cursor.getX() * pixelSize + scrollX, cursor.getY() * pixelSize + scrollY, pixelSize, pixelSize, null);
            }
        }
        g.dispose();
    }

    public void save() {
        BinaryFile bin = new BinaryFile(4 + mapSections.size() * 8);
        bin.addInt(mapSections.size());
        for (MapSection map : mapSections) {
            map.save();
            bin.addInt(map.getMapX());
            bin.addInt(map.getMapY());
        }
        bin.compress(false);
        bin.compile(Algorithms.getFile("Worlds", "Sections.dat"));
    }
}
