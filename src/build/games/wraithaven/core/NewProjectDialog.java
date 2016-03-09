/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * @author TheDudeFromCI
 */
public class NewProjectDialog extends JPanel{
	private static class MapStyleOption{
		private final String name;
		private final int id;
		private MapStyleOption(String name, int id){
			this.name = name;
			this.id = id;
		}
		@Override
		public String toString(){
			return name;
		}
	}
	private final JTextField projectName;
	private final JComboBox<MapStyleOption> mapStyle;
	private final JSpinner bitSize;
	public NewProjectDialog(){
		VerticalFlowLayout verticalFlowLayout = new VerticalFlowLayout();
		verticalFlowLayout.setVGap(5);
		setLayout(verticalFlowLayout);
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(5, 0));
			JLabel label = new JLabel("Project Name:");
			panel.add(label, BorderLayout.WEST);
			projectName = new JTextField();
			projectName.setText("Untitled Project");
			projectName.setPreferredSize(new Dimension(130, 20));
			panel.add(projectName, BorderLayout.CENTER);
			add(panel);
		}
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(5, 0));
			JLabel label = new JLabel("Map Style:");
			panel.add(label, BorderLayout.WEST);
			mapStyle = new JComboBox<>();
			mapStyle.setModel(new DefaultComboBoxModel<>(new MapStyleOption[]{
				new MapStyleOption("Top Down", 0), new MapStyleOption("Isometric", 1)
			}));
			mapStyle.setSelectedIndex(0);
			panel.add(mapStyle, BorderLayout.CENTER);
			add(panel);
		}
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(5, 0));
			JLabel label = new JLabel("Bit Size:");
			panel.add(label, BorderLayout.WEST);
			bitSize = new JSpinner();
			bitSize.setModel(new SpinnerNumberModel(16, 8, 128, 8));
			panel.add(bitSize, BorderLayout.CENTER);
			add(panel);
		}
	}
	public String getProjectName(){
		return projectName.getText();
	}
	public int getMapStyle(){
		return ((MapStyleOption)mapStyle.getSelectedItem()).id;
	}
	public int getBitSize(){
		return (int)bitSize.getValue();
	}
	public JComponent getDefaultFocus(){
		return projectName;
	}
}
