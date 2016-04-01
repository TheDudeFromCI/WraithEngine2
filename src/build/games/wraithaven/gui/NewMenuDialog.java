/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class NewMenuDialog extends JPanel{
	private final JTextField nameInput;
	public NewMenuDialog(){
		setLayout(new VerticalFlowLayout(5));
		{
			// Name
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JLabel label = new JLabel("Name: ");
			panel.add(label, BorderLayout.WEST);
			nameInput = new JTextField();
			nameInput.setColumns(20);
			panel.add(nameInput, BorderLayout.CENTER);
			add(panel);
		}
	}
	public Menu build(){
		Menu menu = new Menu(Algorithms.randomUUID(), nameInput.getText());
		menu.save();
		return menu;
	}
	public void edit(Menu menu){
		menu.setName(nameInput.getText());
		menu.save();
	}
	public JComponent getDefaultFocus(){
		return nameInput;
	}
}
