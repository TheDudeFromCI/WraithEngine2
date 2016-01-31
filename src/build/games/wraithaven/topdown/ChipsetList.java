/*
 * Copyright (C) 2016 TheDudeFromCI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.util.BinaryFile;
import build.games.wraithaven.util.Algorithms;
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

    private static final int TITLE_BAR_HEIGHT = 30;
    private static final Color TITLE_BAR_COLOR = new Color(220, 220, 220);
    private static final Color TITLE_BAR_COLOR_2 = new Color(235, 235, 235);
    private static final Color TITLE_BAR_COLOR_3 = new Color(210, 210, 210);
    private static final Font TITLE_BAR_FONT = new Font("Tahoma", Font.BOLD | Font.ITALIC, 20);
    private final ArrayList<ChipsetListComponent> chipsets = new ArrayList(8);
    private final ChipsetTileSelection selection = new ChipsetTileSelection();
    private BufferedImage selectionBox;

    public ChipsetList() {
        try {
            selectionBox = ImageIO.read(Algorithms.getAsset("Selection Box.png"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        setMinimumSize(new Dimension(Chipset.TILE_OUT_SIZE * Chipset.PREVIEW_TILES_WIDTH, 300));
        updateSize();
        load();
        addMouseListener(new MouseAdapter() {
            private void checkForTileSelection(int x, int y) {
                int height = 0;
                for (ChipsetListComponent c : chipsets) {
                    height += TITLE_BAR_HEIGHT;
                    if (c.isExpanded()) {
                        if (y >= height && y < height + c.getImage().getHeight()) {
                            int selX = x / Chipset.TILE_OUT_SIZE;
                            int selY = (y - height) / Chipset.TILE_OUT_SIZE;
                            selection.select(c.getChipset(), selY * Chipset.PREVIEW_TILES_WIDTH + selX, selX, selY);
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
                    if (y >= height && y < height + TITLE_BAR_HEIGHT) {
                        return c;
                    }
                    height += TITLE_BAR_HEIGHT;
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
        g.setFont(TITLE_BAR_FONT);
        FontMetrics fm = g.getFontMetrics();
        int verticalOffset = 0;
        int selectionVerticalOffset = 0;
        for (ChipsetListComponent chip : chipsets) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, verticalOffset, TITLE_BAR_COLOR, 0, TITLE_BAR_HEIGHT / 2 + verticalOffset, TITLE_BAR_COLOR_2, true));
            g.fillRect(0, verticalOffset, width, TITLE_BAR_HEIGHT);
            g.setColor(TITLE_BAR_COLOR_3);
            g.drawLine(0, verticalOffset, width, verticalOffset);
            g.drawLine(0, verticalOffset + TITLE_BAR_HEIGHT - 1, width, verticalOffset + TITLE_BAR_HEIGHT - 1);
            g.setColor(new Color(0, 0, 0));
            g.drawString(chip.getName(), (width - fm.stringWidth(chip.getName())) / 2, (TITLE_BAR_HEIGHT - fm.getHeight()) / 2 + fm.getAscent() + verticalOffset);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            verticalOffset += TITLE_BAR_HEIGHT;
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
            g.drawImage(selectionBox, selection.getSelectionX() * Chipset.TILE_OUT_SIZE - size,
                    selection.getSelectionY() * Chipset.TILE_OUT_SIZE + selectionVerticalOffset - size, Chipset.TILE_OUT_SIZE + size * 2,
                    Chipset.TILE_OUT_SIZE + size * 2, null);
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
            height += TITLE_BAR_HEIGHT;
            if (c.isExpanded()) {
                height += c.getImage().getHeight();
            }
        }
        setPreferredSize(new Dimension(Chipset.TILE_OUT_SIZE * Chipset.PREVIEW_TILES_WIDTH, Math.max(height, 300)));
        revalidate();
        repaint();
    }
}
