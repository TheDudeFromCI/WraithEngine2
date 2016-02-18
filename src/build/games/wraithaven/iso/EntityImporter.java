/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityImporter extends JFrame{
	private final BufferedImage image;
	private final EntityImporterImagePainter painter;
	private final EntityImporterGrid grid;
	private final ChipsetList chipsetList;
	private boolean isBelow;
	public EntityImporter(File file, ChipsetList chipsetList) throws Exception{
		this.chipsetList = chipsetList;
		image = Algorithms.trimTransparency(ImageIO.read(file));
		grid = new EntityImporterGrid();
		painter = new EntityImporterImagePainter(image, grid);
		setLayout(new BorderLayout());
		JButton ok;
		{
			// Add confirm and cancel buttons.
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			ok = new JButton("Ok");
			ok.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					build();
				}
			});
			ok.setEnabled(false);
			panel.add(ok);
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
			panel.add(cancel);
			add(panel, BorderLayout.SOUTH);
		}
		{
			// Build components.
			JPanel main = new JPanel();
			main.setLayout(new BorderLayout());
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
			JCheckBox below = new JCheckBox("Is Below");
			below.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					isBelow = below.isSelected();
				}
			});
			panel.add(below);
			JButton confirmButton = new JButton("Confirm");
			confirmButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					ok.setEnabled(true);
					painter.setConfirmed();
					main.remove(panel);
					grid.build(painter.getIdealWidth(), painter.getIdealHeight());
					main.add(grid, BorderLayout.EAST);
					main.revalidate();
					pack();
					repaint();
				}
			});
			panel.add(confirmButton);
			main.add(panel, BorderLayout.SOUTH);
			main.add(painter, BorderLayout.CENTER);
			add(main, BorderLayout.CENTER);
		}
		setTitle("Entity Import");
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
	}
	public boolean isBelow(){
		return isBelow;
	}
	private void build(){
		TileCategory cat = chipsetList.getSelectedCategory();
		EntityType[] entities = build(cat);
		BufferedImage[] images = getImages();
		for(int i = 0; i<entities.length; i++){
			cat.addEntityType(entities[i], images[i]);
		}
		chipsetList.getEntityList().repaint();
	}
	private EntityType[] build(TileCategory cat){
		// TODO
		return null;
	}
	private BufferedImage[] getImages(){
		// TODO
		return null;
	}
}
