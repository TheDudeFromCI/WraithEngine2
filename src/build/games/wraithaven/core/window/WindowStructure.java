/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author thedudefromci
 */
public class WindowStructure{
	private final JFrame frame;
	public WindowStructure(WindowStructureBuilder builder){
		frame = new JFrame();
		frame.setTitle("WraithEngine");
		frame.setResizable(true);
		frame.setSize(800, 600);
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				if(builder.confirmExit()){
					System.exit(0);
				}
			}
		});
		frame.setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		frame.add(tabs, BorderLayout.CENTER);
		JMenuBar menuBar = new JMenuBar();
		frame.add(menuBar, BorderLayout.NORTH);
		for(BuilderTab com : builder.getTabs()){
			tabs.addTab(com.getTabName(), com);
		}
		if(builder.getTabs().length!=0){
			builder.getTabs()[0].buildTabs(menuBar);
		}
		tabs.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				menuBar.removeAll();
				builder.getTabs()[tabs.getSelectedIndex()].buildTabs(menuBar);
				menuBar.revalidate();
				menuBar.repaint();
			}
		});
	}
	public void show(){
		frame.setVisible(true);
	}
	public JFrame getFrame(){
		return frame;
	}
}
