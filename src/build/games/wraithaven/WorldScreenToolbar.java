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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author TheDudeFromCI
 */
public class WorldScreenToolbar extends JPanel {

    private static JButton createIcon(String asset, String pressed, String disabled) {
        try {
            JButton button = new JButton(new ImageIcon(Algorithms.getAsset(asset).getAbsolutePath()));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            if (pressed != null) {
                button.setPressedIcon(new ImageIcon(Algorithms.getAsset(pressed).getAbsolutePath()));
            }
            if (disabled != null) {
                button.setDisabledIcon(new ImageIcon(Algorithms.getAsset(disabled).getAbsolutePath()));
            }
            return button;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private final JButton saveButton;

    public WorldScreenToolbar(WorldBuilder worldBuilder) {
        setPreferredSize(new Dimension(32, 32));
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        {
            saveButton = createIcon("Save Icon.png", "Save Icon Down.png", "Save Icon Disabled.png");
            saveButton.setEnabled(worldBuilder.getWorldScreen().needsSaving());
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    worldBuilder.getWorldScreen().save();
                }
            });
            add(saveButton);
        }
    }

    public void setNeedsSaving(boolean needsSaving) {
        saveButton.setEnabled(needsSaving);
    }
};
