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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
	public MenuList(){
		load();
		setMinimumSize(new Dimension(100, 200));
		list = new JList();
		setLayout(new BorderLayout());
		add(list, BorderLayout.CENTER);
		list.setModel(new DefaultComboBoxModel(menus.toArray()));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		add(panel, BorderLayout.SOUTH);
		JButton newButton = new JButton("New");
		JButton deleteButton = new JButton("Delete");
		newButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				NewMenuDialog dialog = new NewMenuDialog();
				InputDialog d = new InputDialog();
				d.setCancelButton(true);
				d.setOkButton(true);
				d.setData(dialog);
				d.setTitle("Create New Menu");
				d.show();
				int response = d.getResponse();
				if(response!=InputDialog.OK){
					return;
				}
				addMenu(dialog.build());
			}
		});
		deleteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(list.getSelectedIndex()==-1){
					return;
				}
				int response =
					JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this menu?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
				if(response!=JOptionPane.OK_OPTION){
					return;
				}
				removeMenu((Menu)list.getSelectedValue());
			}
		});
		panel.add(newButton);
		panel.add(deleteButton);
	}
	private void load(){
		File file = Algorithms.getFile("Menus.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(true);
		short version = bin.getShort();
		switch(version){
			case 0:
				break;
			default:
				throw new RuntimeException();
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(2);
		bin.addShort(FILE_VERSION);
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
		list.setModel(new DefaultComboBoxModel(menus.toArray()));
	}
}
