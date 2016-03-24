/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.util.InputDialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class MenuList extends JPanel{
	private static final short FILE_VERSION = 0;
	private final ArrayList<Menu> menus = new ArrayList(16);
	private final JList list;
	private final MenuComponentList componentList;
	private final MenuEditor menuEditor;
	public MenuList(MenuComponentList componentList, MenuEditor menuEditor){
		this.componentList = componentList;
		this.menuEditor = menuEditor;
		load();
		setMinimumSize(new Dimension(100, 200));
		list = new JList();
		setLayout(new BorderLayout());
		add(list, BorderLayout.CENTER);
		list.setModel(new DefaultComboBoxModel(menus.toArray()));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				int button = e.getButton();
				if(button==MouseEvent.BUTTON1){
					if(e.getClickCount()==2){
						int index = list.locationToIndex(e.getPoint());
						if(index==-1){
							return;
						}
						selectMenu(menus.get(index));
					}
				}
				if(button==MouseEvent.BUTTON3){
					// Menu
					JPopupMenu menu = new JPopupMenu();
					{
						// New
						JMenuItem item = new JMenuItem("New Menu");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								NewMenuDialog dialog = new NewMenuDialog();
								InputDialog d = new InputDialog();
								d.setCancelButton(true);
								d.setOkButton(true);
								d.setData(dialog);
								d.setTitle("Create New Menu");
								d.setDefaultFocus(dialog.getDefaultFocus());
								d.show();
								int response = d.getResponse();
								if(response!=InputDialog.OK){
									return;
								}
								addMenu(dialog.build());
							}
						});
						menu.add(item);
					}
					if(list.getSelectedIndex()!=-1){
						{
							// Delete
							JMenuItem item = new JMenuItem("Delete Menu");
							item.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e){
									int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this menu?", "Confirm Delete",
										JOptionPane.OK_CANCEL_OPTION);
									if(response!=JOptionPane.OK_OPTION){
										return;
									}
									removeMenu((Menu)list.getSelectedValue());
								}
							});
							menu.add(item);
						}
						{
							// Edit
							JMenuItem item = new JMenuItem("Edit Menu");
							item.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e){
									NewMenuDialog dialog = new NewMenuDialog();
									InputDialog d = new InputDialog();
									d.setCancelButton(true);
									d.setOkButton(true);
									d.setData(dialog);
									d.setTitle("Edit Menu");
									d.setDefaultFocus(dialog.getDefaultFocus());
									d.show();
									int response = d.getResponse();
									if(response!=InputDialog.OK){
										return;
									}
									dialog.edit((Menu)list.getSelectedValue());
									save();
									repaint();
									componentList.repaint();
								}
							});
							menu.add(item);
						}
					}
					menu.show(list, e.getX(), e.getY());
				}
			}
		});
	}
	private void selectMenu(Menu menu){
		componentList.setMenu(menu);
		menuEditor.loadMenu(menu);
	}
	private void load(){
		File file = Algorithms.getFile("Menus.dat");
		if(!file.exists()){
			return;
		}
		try{
			BinaryFile bin = new BinaryFile(file);
			bin.decompress(true);
			short version = bin.getShort();
			switch(version){
				case 0:{
					int menuCount = bin.getInt();
					menus.ensureCapacity(menuCount);
					Menu menu;
					String uuid, name;
					for(int i = 0; i<menuCount; i++){
						uuid = bin.getString();
						name = bin.getString();
						menu = new Menu(uuid, name);
						menus.add(menu);
					}
					break;
				}
				default:
					throw new RuntimeException();
			}
		}catch(Exception exception){
			exception.printStackTrace();
			int response = JOptionPane.showConfirmDialog(null, "There has been an error attempting to load this file. Delete this file?", "Error",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if(response==JOptionPane.YES_OPTION){
				file.delete();
			}
			// And clean up.
			menus.clear();
		}
	}
	public void save(){
		BinaryFile bin = new BinaryFile(6);
		bin.addShort(FILE_VERSION);
		bin.addInt(menus.size());
		for(Menu menu : menus){
			bin.addStringAllocated(menu.getUUID());
			bin.addStringAllocated(menu.getName());
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("Menus.dat"));
	}
	public void addMenu(Menu menu){
		menus.add(menu);
		save();
		list.setModel(new DefaultComboBoxModel(menus.toArray()));
	}
	public void removeMenu(Menu menu){
		menus.remove(menu);
		save();
		if((Menu)list.getSelectedValue()==menu){
			componentList.setMenu(null);
		}
		list.setModel(new DefaultComboBoxModel(menus.toArray()));
		// Delete files.
		Algorithms.deleteFile(Algorithms.getFile("Menus", menu.getUUID()));
		Algorithms.deleteFile(Algorithms.getFile("Menus", menu.getUUID()+".dat"));
	}
}
