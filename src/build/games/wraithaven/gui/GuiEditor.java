/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * @author thedudefromci
 */
public class GuiEditor{
	private static boolean ALIVE;
	public static void launch(){
		if(ALIVE){
			return;
		}
		ALIVE = true;
		JFrame frame = new JFrame();
		frame.setTitle("GUI Editor");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				ALIVE = false;
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addComponents(frame);
		frame.setVisible(true);
	}
	private static void addComponents(JFrame frame){
		frame.setLayout(new BorderLayout());
		MenuComponentList menuComponentList = new MenuComponentList();
		JScrollPane scrollPane = new JScrollPane(menuComponentList);
		MenuList menuList = new MenuList(menuComponentList);
		JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, menuList);
		leftPanel.setResizeWeight(0.7);
		JPanel centerPanel = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, centerPanel);
		splitPane.setResizeWeight(0.2);
		frame.add(splitPane, BorderLayout.CENTER);
	}
}
