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
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    private final JSpinner editingLayer;
    private final JCheckBox hideOtherLayers;
    private int currentLayer;
    private boolean hideLayers;

    public WorldScreenToolbar(WorldBuilder worldBuilder) {
        setPreferredSize(new Dimension(32, 32));
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        {
            saveButton = createIcon("Save Icon.png", "Save Icon Down.png", "Save Icon Disabled.png");
            saveButton.setEnabled(false);
            saveButton.setToolTipText("Click to save.");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    worldBuilder.getWorldScreen().save();
                }
            });
            add(saveButton);
        }
        {
            editingLayer = new JSpinner(new SpinnerModel() {
                private final ArrayList<ChangeListener> changeListeners = new ArrayList(1);
                private int value;

                @Override
                public Object getValue() {
                    return "Layer " + value;
                }

                @Override
                public void setValue(Object value) {
                    this.value = Integer.valueOf(((String) value).substring(6));
                    currentLayer = this.value;
                    for (ChangeListener listener : changeListeners) {
                        listener.stateChanged(new ChangeEvent(this));
                    }
                }

                @Override
                public Object getNextValue() {
                    return "Layer " + (value + 1);
                }

                @Override
                public Object getPreviousValue() {
                    return "Layer " + (value - 1);
                }

                @Override
                public void addChangeListener(ChangeListener l) {
                    changeListeners.add(l);
                }

                @Override
                public void removeChangeListener(ChangeListener l) {
                    changeListeners.remove(l);
                }
            });
            editingLayer.setToolTipText("Change what layer you are currently editing.");
            editingLayer.setPreferredSize(new Dimension(96, 32));
            add(editingLayer);
        }
        {
            hideOtherLayers = new JCheckBox("Hide Other Layers");
            hideOtherLayers.setToolTipText("If checked, only the current layer will be shown.");
            hideOtherLayers.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hideLayers = hideOtherLayers.isSelected();
                    worldBuilder.getWorldScreen().redrawAllMapSections();
                }
            });
            add(hideOtherLayers);
        }
    }

    public void setNeedsSaving(boolean needsSaving) {
        saveButton.setEnabled(needsSaving);
    }

    public int getEditingLayer() {
        return currentLayer;
    }

    public boolean hideOtherLayers() {
        return hideLayers;
    }

};
