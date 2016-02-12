/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * @author TheDudeFromCI
 */
public class EntityImporter extends JPanel{
	private final String uuid;
	private final BufferedImage image;
	private final JSpinner width;
	private final JSpinner height;
	public EntityImporter(File file){
		try{
			image = ImageIO.read(file);
		}catch(Exception exception){
			throw new RuntimeException(exception.getMessage());
		}
		uuid = Algorithms.randomUUID();
		setLayout(new BorderLayout(5, 0));
		JPanel imagePreview = new JPanel(){
			private final Dimension size;
			{
				size = new Dimension(image.getWidth(), image.getHeight());
			}
			@Override
			public void paintComponent(Graphics g1){
				Graphics2D g = (Graphics2D)g1;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawImage(image, 0, 0, null);
				g.setColor(Color.gray);
				g.drawRect(0, 0, image.getWidth()-1, image.getHeight()-1);
				g.dispose();
			}
			@Override
			public Dimension getPreferredSize(){
				return size;
			}
		};
		{
			// Side bar information.
			JPanel sideBar = new JPanel();
			sideBar.setLayout(new VerticalFlowLayout(0, 5));
			{
				// Width
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JLabel label = new JLabel("Width (X)");
				panel.add(label, BorderLayout.WEST);
				width = new JSpinner();
				width.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				panel.add(width, BorderLayout.CENTER);
				sideBar.add(panel);
			}
			{
				// Height
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JLabel label = new JLabel("Height (Y)");
				panel.add(label, BorderLayout.WEST);
				height = new JSpinner();
				height.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
				panel.add(height, BorderLayout.CENTER);
				sideBar.add(panel);
			}
			add(sideBar, BorderLayout.EAST);
		}
		add(imagePreview, BorderLayout.CENTER);
	}
	public EntityType build(){
		return new EntityType(uuid);
	}
	public BufferedImage getEntityImage(){
		return image;
	}
	public int getWidth(){
		return (int)width.getValue();
	}
	public int getHeight(){
		return (int)height.getValue();
	}
}
