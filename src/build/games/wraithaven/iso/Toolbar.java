/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.tools.Tool;
import build.games.wraithaven.util.Algorithms;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * @author TheDudeFromCI
 */
public class Toolbar extends JPanel{
	private static AbstractButton createIcon(String asset, String pressed, String disabled, boolean tool){
		try{
			Image img = ImageIO.read(Algorithms.getAsset(asset));
			if(tool){
				img = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
			}
			AbstractButton button;
			if(tool){
				button = new JToggleButton();
			}else{
				button = new JButton();
			}
			button.setIcon(new ImageIcon(img));
			if(!tool){
				button.setBorder(BorderFactory.createEmptyBorder());
				button.setContentAreaFilled(false);
			}
			button.setFocusPainted(false);
			if(pressed!=null){
				img = ImageIO.read(Algorithms.getAsset(pressed));
				if(tool){
					img = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
				}
				button.setPressedIcon(new ImageIcon(img));
			}else{
				BufferedImage buf = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = buf.createGraphics();
				g.drawImage(img, 0, 1, null);
				g.dispose();
				button.setPressedIcon(new ImageIcon(buf));
			}
			if(disabled!=null){
				img = ImageIO.read(Algorithms.getAsset(disabled));
				if(tool){
					img = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
				}
				button.setDisabledIcon(new ImageIcon(img));
			}
			return button;
		}catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}
	private final JButton saveButton;
	private final MapEditorPainter painter;
	public Toolbar(MapEditor mapEditor){
		painter = mapEditor.getPainter();
		setPreferredSize(new Dimension(32, 32));
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		{
			// Save Button
			saveButton = (JButton)createIcon("Save Icon.png", "Save Icon Down.png", "Save Icon Disabled.png", false);
			saveButton.setEnabled(false);
			saveButton.setToolTipText("Click to save.");
			saveButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					mapEditor.save();
				}
			});
			add(saveButton);
		}
		{
			// Tools
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			panel.setBorder(BorderFactory.createLoweredBevelBorder());
			ButtonGroup buttonGroup = new ButtonGroup();
			{
				// Basic
				JToggleButton button = createTool("Basic Tool.png", Tool.BASIC);
				buttonGroup.add(button);
				panel.add(button);
				button.setSelected(true);
			}
			{
				// Fill
				JToggleButton button = createTool("Paint Bucket.png", Tool.FILL);
				buttonGroup.add(button);
				panel.add(button);
			}
			{
				// Rectangle
				JToggleButton button = createTool("Rectangle.png", Tool.RECTANGLE);
				buttonGroup.add(button);
				panel.add(button);
			}
			{
				// Circle
				JToggleButton button = createTool("Circle.png", Tool.CIRCLE);
				buttonGroup.add(button);
				panel.add(button);
			}
			add(panel);
		}
	}
	public void setNeedsSaving(boolean needsSaving){
		saveButton.setEnabled(needsSaving);
	}
	private JToggleButton createTool(String asset, Tool tool){
		AbstractButton button = createIcon(asset, null, null, true);
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){}
		});
		return (JToggleButton)button;
	}
};
