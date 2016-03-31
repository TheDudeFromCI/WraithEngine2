/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.core.window.BuilderTab;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
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
public class GuiEditor extends BuilderTab{
	private final MenuComponentLocationPanel componentInfo;
	private final MenuEditor editor;
	private final MenuComponentList menuComponentList;
	private final MenuList menuList;
	public GuiEditor(){
		super("Gui Editor");
		componentInfo = new MenuComponentLocationPanel();
		editor = new MenuEditor(componentInfo);
		menuComponentList = new MenuComponentList(editor, componentInfo);
		menuList = new MenuList(menuComponentList, editor);
		addComponents();
	}
	private void addComponents(){
		setLayout(new BorderLayout());
		componentInfo.setMenuEditor(editor);
		editor.setMenuComponentList(menuComponentList);
		JScrollPane scrollPane = new JScrollPane(menuComponentList);
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
		add(splitPane, BorderLayout.CENTER);
	}
	public Menu getSelectedMenu(){
		return menuList.getSelectedMenu();
	}
	@Override
	public void buildTabs(JMenuBar menuBar){
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
	}
}
