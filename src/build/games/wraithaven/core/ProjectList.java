/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class ProjectList extends JFrame{
	private ProjectConstraints[] projects;
	public ProjectList(){
		projects = loadProjects();
		init();
		addComponents();
		setVisible(true);
	}
	private void addComponents(){
		JList<ProjectConstraints> list = new JList();
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		list.setFont(new Font("Tahoma", Font.PLAIN, 15));
		list.setBackground(Color.LIGHT_GRAY);
		list.setModel(new AbstractListModel<ProjectConstraints>(){
			@Override
			public ProjectConstraints getElementAt(int index){
				return projects[index];
			}
			@Override
			public int getSize(){
				return projects.length;
			}
		});
		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent evt){
				JList list = (JList)evt.getSource();
				if(evt.getClickCount()==2){
					int index = list.locationToIndex(evt.getPoint());
					loadProject(projects[index]);
					dispose();
				}
			}
		});
		list.setSelectedIndex(-1);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getContentPane().add(list, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		JButton btnLoadProject = new JButton("Load Project");
		btnLoadProject.setEnabled(false);
		panel.add(btnLoadProject);
		btnLoadProject.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				loadProject(list.getSelectedValue());
				dispose();
			}
		});
		JButton deleteProject = new JButton("Delete Project");
		deleteProject.setEnabled(false);
		deleteProject.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this project? This CANNOT be undone!!!",
					"Confirm Delete", JOptionPane.YES_NO_OPTION);
				if(response==JOptionPane.YES_OPTION){
					ProjectConstraints toDelete = list.getSelectedValue();
					File file = Algorithms.getFile(toDelete.getUUID());
					Algorithms.deleteFile(file);
					ProjectConstraints[] newProjectList = new ProjectConstraints[projects.length-1];
					int j = 0;
					for(ProjectConstraints project : projects){
						if(project==toDelete){
							continue;
						}
						newProjectList[j++] = project;
					}
					projects = newProjectList;
					BinaryFile bin = new BinaryFile(4+newProjectList.length*8);
					bin.addInt(newProjectList.length);
					for(ProjectConstraints s : newProjectList){
						bin.addStringAllocated(s.getName());
						bin.addStringAllocated(s.getUUID());
						bin.addInt(s.getType());
						bin.addInt(s.getBitSize());
					}
					bin.compress(false);
					bin.compile(Algorithms.getFile("Projects.dat"));
					list.setModel(new AbstractListModel<ProjectConstraints>(){
						@Override
						public ProjectConstraints getElementAt(int index){
							return projects[index];
						}
						@Override
						public int getSize(){
							return projects.length;
						}
					});
					list.setSelectedIndex(-1);
				}
			}
		});
		panel.add(deleteProject);
		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent event){
				btnLoadProject.setEnabled(list.getSelectedIndex()!=-1);
				deleteProject.setEnabled(list.getSelectedIndex()!=-1);
			}
		});
		JButton btnCreateNewProject = new JButton("Create New Project");
		panel.add(btnCreateNewProject);
		btnCreateNewProject.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				NewProjectDialog dialog = new NewProjectDialog();
				int response = JOptionPane.showConfirmDialog(null, dialog, "New Project", JOptionPane.OK_CANCEL_OPTION);
				if(response!=JOptionPane.OK_OPTION){
					return;
				}
				String name = dialog.getProjectName();
				String uuid = Algorithms.randomUUID();
				int type = dialog.getMapStyle();
				int bitSize = dialog.getBitSize();
				BinaryFile bin = new BinaryFile(4+projects.length*8+8);
				bin.addInt(projects.length+1);
				for(ProjectConstraints s : projects){
					bin.addStringAllocated(s.getName());
					bin.addStringAllocated(s.getUUID());
					bin.addInt(s.getType());
					bin.addInt(s.getBitSize());
				}
				bin.addStringAllocated(name);
				bin.addStringAllocated(uuid);
				bin.addInt(type);
				bin.addInt(bitSize);
				bin.compress(false);
				bin.compile(Algorithms.getFile("Projects.dat"));
				dispose();
				loadProject(new ProjectConstraints(name, uuid, type, bitSize));
			}
		});
		JButton btnCancel = new JButton("Cancel");
		panel.add(btnCancel);
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.WEST);
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.EAST);
		JPanel panel_3 = new JPanel();
		getContentPane().add(panel_3, BorderLayout.NORTH);
		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				dispose();
				System.exit(0);
			}
		});
	}
	private void init(){
		setTitle("Choose Project");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 450);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	private void loadProject(ProjectConstraints pc){
		WraithEngine.outputFolder += File.separatorChar+pc.getUUID();
		WraithEngine.projectName = pc.getName();
		WraithEngine.projectBitSize = pc.getBitSize();
		WraithEngine.INSTANCE = new WraithEngine(MapStyleFactory.loadMapStyle(pc.getType()));
	}
	private ProjectConstraints[] loadProjects(){
		File file = Algorithms.getFile("Projects.dat");
		if(!file.exists()){
			return new ProjectConstraints[0];
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		ProjectConstraints[] list = new ProjectConstraints[bin.getInt()];
		for(int i = 0; i<list.length; i++){
			String name = bin.getString();
			String uuid = bin.getString();
			int type = bin.getInt();
			int bitSize = bin.getInt();
			list[i] = new ProjectConstraints(name, uuid, type, bitSize);
		}
		return list;
	}
}
