package build.games.wraithaven;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ChipsetList extends JPanel {

    private static final int TitleBarHeight = 30;
    private static final Color TitleBarColor1 = new Color(220, 220, 220);
    private static final Color TitleBarColor2 = new Color(235, 235, 235);
    private static final Color TitleBarColor3 = new Color(210, 210, 210);
    private static final Font TitleBarFont = new Font("Tahoma", Font.BOLD | Font.ITALIC, 20);
    private final ArrayList<ChipsetListComponent> chipsets = new ArrayList<ChipsetListComponent>();
    private final ChipsetTileSelection selection = new ChipsetTileSelection();
    private BufferedImage selectionBox;

    public ChipsetList() {
        try {
            selectionBox = ImageIO.read(Algorithms.getAsset("Selection Box.png"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        updateSize();
        load();
        addMouseListener(new MouseAdapter() {
            private void checkForTileSelection(int x, int y) {
                int height = 0;
                for (ChipsetListComponent c : chipsets) {
                    height += TitleBarHeight;
                    if (c.isExpanded()) {
                        if (y >= height && y < height + c.getImage().getHeight()) {
                            int selX = x / Chipset.Tile_Out_Size;
                            int selY = (y - height) / Chipset.Tile_Out_Size;
                            selection.select(c.getChipset(), selY * Chipset.Preview_Tiles_Width + selX, selX, selY);
                            repaint();
                            return;
                        }
                        height += c.getImage().getHeight();
                    }
                }
                selection.reset();
            }

            private ChipsetListComponent getComponentTitleAt(int y) {
                int height = 0;
                for (ChipsetListComponent c : chipsets) {
                    if (y >= height && y < height + TitleBarHeight) {
                        return c;
                    }
                    height += TitleBarHeight;
                    if (c.isExpanded()) {
                        height += c.getImage().getHeight();
                    }
                }
                return null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                ChipsetListComponent c = getComponentTitleAt(e.getY());
                if (c == null) {
                    checkForTileSelection(e.getX(), e.getY());
                    return;
                }
                c.setExpanded(!c.isExpanded());
                if (selection.getChipset() == c.getChipset()) {
                    selection.reset();
                }
                updateSize();
                repaint();
            }
        });
    }

    public void addChipset(Chipset chipset) {
        chipsets.add(new ChipsetListComponent(chipset));
        updateSize();
        repaint();
        save();
    }

    public Chipset getChipset(String uuid) {
        for (ChipsetListComponent chipset : chipsets) {
            if (chipset.getChipset().getUUID().equals(uuid)) {
                return chipset.getChipset();
            }
        }
        return null;
    }

    public ArrayList<ChipsetListComponent> getChipsets() {
        return chipsets;
    }

    public ChipsetTileSelection getSelectedTile() {
        return selection;
    }

    private void load() {
        File file = Algorithms.getFile("Chipsets", "List.dat");
        if (!file.exists()) {
            return;
        }
        BinaryFile bin = new BinaryFile(file);
        bin.decompress(true);
        int listSize = bin.getInt();
        for (int i = 0; i < listSize; i++) {
            chipsets.add(new ChipsetListComponent(new Chipset(bin)));
        }
    }

    @Override
    public void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        int width = getWidth();
        int height = getHeight();
        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);
        g.setFont(TitleBarFont);
        FontMetrics fm = g.getFontMetrics();
        int verticalOffset = 0;
        int selectionVerticalOffset = 0;
        for (ChipsetListComponent chip : chipsets) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, verticalOffset, TitleBarColor1, 0, TitleBarHeight / 2 + verticalOffset, TitleBarColor2, true));
            g.fillRect(0, verticalOffset, width, TitleBarHeight);
            g.setColor(TitleBarColor3);
            g.drawLine(0, verticalOffset, width, verticalOffset);
            g.drawLine(0, verticalOffset + TitleBarHeight - 1, width, verticalOffset + TitleBarHeight - 1);
            g.setColor(new Color(0, 0, 0));
            g.drawString(chip.getName(), (width - fm.stringWidth(chip.getName())) / 2, (TitleBarHeight - fm.getHeight()) / 2 + fm.getAscent() + verticalOffset);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            verticalOffset += TitleBarHeight;
            if (chip.getChipset() == selection.getChipset()) {
                selectionVerticalOffset = verticalOffset;
            }
            if (chip.isExpanded()) {
                g.drawImage(chip.getImage(), 0, verticalOffset, null);
                verticalOffset += chip.getImage().getHeight();
            }
        }
        if (selection.isActive()) {
            final int size = 4;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(selectionBox, selection.getSelectionX() * Chipset.Tile_Out_Size - size,
                    selection.getSelectionY() * Chipset.Tile_Out_Size + selectionVerticalOffset - size, Chipset.Tile_Out_Size + size * 2,
                    Chipset.Tile_Out_Size + size * 2, null);
        }
        g.dispose();
    }

    private void save() {
        BinaryFile bin = new BinaryFile(4);
        bin.addInt(chipsets.size());
        for (ChipsetListComponent chip : chipsets) {
            chip.getChipset().save(bin);
        }
        bin.compress(true);
        bin.compile(Algorithms.getFile("Chipsets", "List.dat"));
    }

    private void updateSize() {
        int height = 0;
        for (ChipsetListComponent c : chipsets) {
            height += TitleBarHeight;
            if (c.isExpanded()) {
                height += c.getImage().getHeight();
            }
        }
        setPreferredSize(new Dimension(Chipset.Tile_Out_Size * Chipset.Preview_Tiles_Width, Math.max(height, 10)));
        revalidate();
        repaint();
    }
}
