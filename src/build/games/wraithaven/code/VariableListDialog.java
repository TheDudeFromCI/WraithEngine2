/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import wraith.lib.code.Variable;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class VariableListDialog extends JPanel{
	private final ArrayList<Variable> variables;
	private final JList list;
	public VariableListDialog(ArrayList<? extends Variable> vars, Class<? extends Variable> varType){
		variables = new ArrayList(vars);
		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(320, 320));
		add(scroll);
		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()!=MouseEvent.BUTTON3){
					return;
				}
				JPopupMenu menu = new JPopupMenu();
				{
					// Build
					{
						// New
						JMenuItem item = new JMenuItem("New");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								String name = JOptionPane.showInputDialog(null, "Variable Name:", "Insert Name", JOptionPane.PLAIN_MESSAGE);
								if(name==null){
									return;
								}
								try{
									Variable v = varType.getConstructor(String.class).newInstance(Algorithms.randomUUID());
									v.setName(name);
									variables.add(v);
									updateModel();
								}catch(Exception exception){
									exception.printStackTrace();
									JOptionPane.showMessageDialog(null, "There has been an error attempting to create this variable.", "Error",
										JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						menu.add(item);
					}
					if(list.getSelectedIndex()!=-1){
						{
							// Edit
						}
						{
							// Delete
							JMenuItem item = new JMenuItem("Delete");
							item.addActionListener(new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent e){
									int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this variable?",
										"Confirm Delete", JOptionPane.YES_NO_OPTION);
									if(response!=JOptionPane.YES_OPTION){
										return;
									}
									variables.remove(list.getSelectedIndex());
									updateModel();
								}
							});
							menu.add(item);
						}
					}
				}
				menu.show(list, e.getX(), e.getY());
			}
		});
		updateModel();
	}
	private void updateModel(){
		list.setModel(new DefaultComboBoxModel(variables.toArray()));
	}
	public ArrayList<Variable> getVariables(){
		return variables;
	}
}
