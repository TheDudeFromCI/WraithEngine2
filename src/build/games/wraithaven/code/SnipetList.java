/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.util.InputDialog;
import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class SnipetList extends JPanel{
	private static final short FILE_VERSION = 0;
	private final ArrayList<Snipet> snipets = new ArrayList(16);
	private final JList list;
	private final Renderer renderer;
	private Snipet selected;
	public SnipetList(Renderer renderer){
		this.renderer = renderer;
		load();
		list = new JList();
		updateListModel();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e){
				setSelected((Snipet)list.getSelectedValue());
			}
		});
		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()!=MouseEvent.BUTTON3){
					return;
				}
				JPopupMenu menu = new JPopupMenu();
				{
					// Load menu
					{
						// New
						JMenuItem item = new JMenuItem("New Script");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								InputDialog dialog = new InputDialog();
								NewScriptDialog newScriptDialog = new NewScriptDialog();
								dialog.setData(newScriptDialog);
								dialog.setCancelButton(true);
								dialog.setOkButton(true);
								dialog.setTitle("New Script");
								dialog.setDefaultFocus(null);
								dialog.show();
								if(dialog.getResponse()!=InputDialog.OK){
									return;
								}
								Snipet script = newScriptDialog.build();
								snipets.add(script);
								updateListModel();
								save();
								repaint();
							}
						});
						menu.add(item);
					}
					if(selected!=null){
						{
							// Delete
							JMenuItem item = new JMenuItem("Delete");
							item.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e){
									int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this script?", "Confirm Delete",
										JOptionPane.YES_NO_OPTION);
									if(response!=JOptionPane.YES_OPTION){
										return;
									}
									snipets.remove(selected);
									updateListModel();
									save();
									repaint();
								}
							});
							menu.add(item);
						}
					}
				}
				menu.show(list, e.getX(), e.getY());
			}
		});
		JScrollPane scrollPane = new JScrollPane(list);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}
	private void updateListModel(){
		list.setModel(new DefaultComboBoxModel(snipets.toArray()));
	}
	private void setSelected(Snipet snipet){
		if(selected==snipet){
			// Don't double up on load events!
			return;
		}
		selected = snipet;
		renderer.loadSnipet(snipet);
	}
	private void load(){
		File file = Algorithms.getFile("scripts.dat");
		if(!file.exists()){
			return;
		}
		try{
			BinaryFile bin = new BinaryFile(file);
			bin.decompress(true);
			short version = bin.getShort();
			switch(version){
				case 0:{
					int count = bin.getInt();
					snipets.ensureCapacity(count);
					for(int i = 0; i<count; i++){
						Snipet s = new Snipet(bin.getString());
						s.setName(bin.getString());
						snipets.add(s);
					}
					break;
				}
				default:
					throw new RuntimeException("Unknown file version! '"+version+"'");
			}
		}catch(Exception exception){
			exception.printStackTrace();
			int response = JOptionPane.showConfirmDialog(null, "There has been a error attempting to load this file. Would you like to delete it?",
				"Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
			if(response==JOptionPane.YES_OPTION){
				file.delete();
			}
			snipets.clear();
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(2+4);
		bin.addShort(FILE_VERSION);
		bin.addInt(snipets.size());
		for(Snipet s : snipets){
			bin.addStringAllocated(s.getUuid());
			bin.addStringAllocated(s.getName());
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("scripts.dat"));
	}
}
