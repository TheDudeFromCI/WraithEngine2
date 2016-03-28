/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class GuiEditor{
	private static JFrame frame;
	private static MenuList menuList;
	public static void launch(){
		if(frame!=null){
			return;
		}
		frame = new JFrame();
		frame.setTitle("GUI Editor");
		frame.setSize(700, 525);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				frame = null;
				menuList = null;
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addComponents();
		frame.setVisible(true);
	}
	public static void closeFrame(){
		if(frame!=null){
			frame.dispose();
			frame = null;
			menuList = null;
		}
	}
	public static Menu getSelectedMenu(){
		if(frame==null){
			return null;
		}
		return menuList.getSelectedMenu();
	}
	private static void addComponents(){
		frame.setLayout(new BorderLayout());
		MenuComponentLocationPanel componentInfo = new MenuComponentLocationPanel();
		MenuEditor editor = new MenuEditor(componentInfo);
		componentInfo.setMenuEditor(editor);
		MenuComponentList menuComponentList = new MenuComponentList(editor, componentInfo);
		editor.setMenuComponentList(menuComponentList);
		JScrollPane scrollPane = new JScrollPane(menuComponentList);
		menuList = new MenuList(menuComponentList, editor);
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(componentInfo, BorderLayout.SOUTH);
		upperPanel.add(scrollPane, BorderLayout.CENTER);
		JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, upperPanel, menuList);
		leftPanel.setResizeWeight(0.8);
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(editor, BorderLayout.CENTER);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, centerPanel);
		splitPane.setResizeWeight(0.2);
		frame.add(splitPane, BorderLayout.CENTER);
		{
			JMenuBar menuBar = new JMenuBar();
			{
				// Edit
				JMenu menu = new JMenu("Edit");
				{
					// Background
					JMenu menu2 = new JMenu("Temp Background");
					{
						// Set Background
						JMenuItem item = new JMenuItem("Set Background");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								File file = Algorithms.userChooseFile("Import Image", "Import", "png");
								if(file==null){
									return;
								}
								try{
									BufferedImage image = ImageIO.read(file);
									editor.setTempBackground(image);
								}catch(Exception exception){
									exception.printStackTrace();
									JOptionPane.showMessageDialog(null, "There has been an error trying to load this image.", "Error",
										JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						menu2.add(item);
					}
					{
						// Clear Background
						JMenuItem item = new JMenuItem("Clear Background");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								editor.setTempBackground(null);
							}
						});
						menu2.add(item);
					}
					menu.add(menu2);
				}
				menuBar.add(menu);
			}
			frame.add(menuBar, BorderLayout.NORTH);
		}
	}
}
