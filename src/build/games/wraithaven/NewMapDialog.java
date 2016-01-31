/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * @author TheDudeFromCI
 */
public class NewMapDialog extends JFrame{
	public NewMapDialog(WorldBuilder worldBuilder, Map parentMap){
		setTitle("Create New Map");
		setResizable(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		GridBagConstraints c = new GridBagConstraints();
		JTextField mapName;
		{
			{
				JLabel label = new JLabel("Map Name:");
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.weightx = 0.5;
				c.weighty = 0.5;
				add(label, c);
			}
			{
				mapName = new JTextField();
				mapName.setText("Untitled Map");
				mapName.setPreferredSize(new Dimension(130, 20));
				c.gridx = 1;
				c.gridy = 0;
				c.gridwidth = 2;
				c.gridheight = 1;
				c.weightx = 0.5;
				c.weighty = 0.5;
				add(mapName, c);
			}
			{
				JLabel label = new JLabel("Map Size:");
				c.gridx = 0;
				c.gridy = 1;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.weightx = 0.5;
				c.weighty = 0.5;
				add(label, c);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JLabel label = new JLabel("Width");
				label.setHorizontalAlignment(JLabel.CENTER);
				panel.add(label, BorderLayout.NORTH);
				JSpinner spinner = new JSpinner();
				spinner.setModel(new SpinnerNumberModel(20, 1, Integer.MAX_VALUE, 1));
				panel.add(spinner, BorderLayout.CENTER);
				c.gridx = 1;
				c.gridy = 1;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.weightx = 0.25;
				c.weighty = 0.5;
				add(panel, c);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JLabel label = new JLabel("Height");
				label.setHorizontalAlignment(JLabel.CENTER);
				panel.add(label, BorderLayout.NORTH);
				JSpinner spinner = new JSpinner();
				spinner.setModel(new SpinnerNumberModel(15, 1, Integer.MAX_VALUE, 1));
				panel.add(spinner, BorderLayout.CENTER);
				c.gridx = 2;
				c.gridy = 1;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.weightx = 0.25;
				c.weighty = 0.5;
				add(panel, c);
			}
			{
				JPanel panel = new JPanel();
				FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT, 5, 0);
				panel.setLayout(flowLayout);
				JButton ok = new JButton("Ok");
				JButton cancel = new JButton("Cancel");
				panel.add(ok);
				panel.add(cancel);
				c.gridx = 2;
				c.gridy = 2;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.weightx = 0.5;
				c.weighty = 0.5;
				add(panel, c);
				ok.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						dispose();
						Map map = new Map(worldBuilder, Algorithms.randomUUID(), mapName.getText());
						if(parentMap==null){
							worldBuilder.getWorldList().addMap(map);
						}else{
							parentMap.addChild(map);
						}
						worldBuilder.getWorldList().updateTreeModel();
					}
				});
				cancel.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						dispose();
					}
				});
			}
		}
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
