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
package build.games.wraithaven;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ChipsetImporter {

    private final String uuid;
    private final String name;
    private final File file;
    private BufferedImage previewImage;
    private BufferedImage[] tileImages;
    private Tile[] tiles;

    public ChipsetImporter(File file) {
        this.file = file;
        uuid = UUID.randomUUID().toString();
        name = file.getName().substring(0, file.getName().length() - 4);
        unwrap();
    }

    public Chipset asChipset() {
        return new Chipset(uuid, tiles, name);
    }

    public String getName() {
        return name;
    }

    public BufferedImage getPreviewImage() {
        return previewImage;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public String getUUID() {
        return uuid;
    }

    public void saveImages() {
        for (int i = 0; i < tileImages.length; i++) {
            if (tileImages[i] == null) {
                continue;
            }
            try {
                ImageIO.write(tileImages[i], "png", Algorithms.getFile("Chipsets", uuid, i + ".png"));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        try {
            ImageIO.write(previewImage, "png", Algorithms.getFile("Chipsets", uuid, "preview.png"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void unwrap() {
        try {
            BufferedImage image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();
            if (width % Chipset.BIT_SIZE != 0 || height % Chipset.BIT_SIZE != 0) {
                JOptionPane.showMessageDialog(null, "This image is in an unknown size, and could not be parsed.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                throw new WrongImageSizeException();
            }
            width /= Chipset.BIT_SIZE;
            height /= Chipset.BIT_SIZE;
            tiles = new Tile[width * height];
            tileImages = new BufferedImage[tiles.length];
            int x, y, index;
            previewImage = new BufferedImage(Chipset.PREVIEW_TILES_WIDTH * Chipset.TILE_OUT_SIZE,
                    (int) Math.ceil(tiles.length / (float) Chipset.PREVIEW_TILES_WIDTH) * Chipset.TILE_OUT_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = previewImage.createGraphics();
            g.setColor(Color.black);
            g.fillRect(0, 0, previewImage.getWidth(), previewImage.getHeight());
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
                    index = y * Chipset.PREVIEW_TILES_WIDTH + x % Chipset.PREVIEW_TILES_WIDTH + x / Chipset.PREVIEW_TILES_WIDTH * Chipset.PREVIEW_TILES_WIDTH * height;
                    if (index >= tiles.length) {
                        continue;
                    }
                    tiles[index] = new Tile(null, index);
                    tileImages[index] = image.getSubimage(x * Chipset.BIT_SIZE, y * Chipset.BIT_SIZE, Chipset.BIT_SIZE, Chipset.BIT_SIZE);
                    g.drawImage(tileImages[index], index % Chipset.PREVIEW_TILES_WIDTH * Chipset.TILE_OUT_SIZE,
                            index / Chipset.PREVIEW_TILES_WIDTH * Chipset.TILE_OUT_SIZE, Chipset.TILE_OUT_SIZE, Chipset.TILE_OUT_SIZE, null);
                    g.setColor(Color.white);
                }
            }
            g.dispose();
        } catch (WrongImageSizeException exception) {
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, "There has been an error loading this image.", "Warning", JOptionPane.WARNING_MESSAGE);
            throw new RuntimeException();
        }
    }
}
