/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.NewMapDialog;
import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author TheDudeFromCI
 */
public class WorldList extends JPanel{
	private class MapStructure implements TreeModel{
		private final String root = "Map List";
		@Override
		public Object getRoot(){
			return root;
		}
		@Override
		public Object getChild(Object parent, int index){
			if(parent==root){
				return mainMaps.get(index);
			}else{
				return ((Map)parent).getChild(index);
			}
		}
		@Override
		public int getChildCount(Object parent){
			if(parent==root){
				return mainMaps.size();
			}else{
				return ((Map)parent).getChildCount();
			}
		}
		@Override
		public boolean isLeaf(Object node){
			if(node==root){
				return false;
			}else{
				return ((Map)node).getChildCount()==0;
			}
		}
		@Override
		public void valueForPathChanged(TreePath path, Object newValue){}
		@Override
		public int getIndexOfChild(Object parent, Object child){
			if(parent==root){
				return mainMaps.indexOf(child);
			}else{
				return ((Map)parent).getIndexOf((Map)child);
			}
		}
		@Override
		public void addTreeModelListener(TreeModelListener l){}
		@Override
		public void removeTreeModelListener(TreeModelListener l){}
	}
	private final JTree tree;
	private final ArrayList<Map> mainMaps = new ArrayList(64);
	private final WraithEngine worldBuilder;
	private final MapStructure model;
	public WorldList(WraithEngine wraithEngine){
		this.worldBuilder = wraithEngine;
		setLayout(new BorderLayout());
		load(wraithEngine);
		tree = new JTree();
		model = new MapStructure();
		tree.setModel(model);
		tree.setRootVisible(true);
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		MouseAdapter ml = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if(selRow!=-1){
					int button = e.getButton();
					if(button==MouseEvent.BUTTON1){
						// If left click.
						if(e.getClickCount()==2){
							Object selected = selPath.getLastPathComponent();
							tree.setSelectionPath(selPath);
							if(selected instanceof Map){
								((MapEditor)wraithEngine.getMapEditor()).getWorldScreen().selectMap((Map)selected);
							}
						}
					}else if(button==MouseEvent.BUTTON3){
						// If right click.
						Object selected = selPath.getLastPathComponent();
						tree.setSelectionPath(selPath);
						if(selected instanceof Map){
							showContextMenu((Map)selected, e.getX(), e.getY());
						}else{
							showContextMenu(null, e.getX(), e.getY());
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);
		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}
	private void showContextMenu(Map selectedMap, int x, int y){
		// Show map properties.
		// If selectedMap is null, then global properties.
		JPopupMenu menu = new JPopupMenu();
		{
			// New child map.
			JMenuItem newMap = new JMenuItem(selectedMap==null?"New Map":"New Child Map");
			newMap.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					new NewMapDialog(worldBuilder, selectedMap);
				}
			});
			menu.add(newMap);
		}
		if(selectedMap==null){
			// TODO
		}else{
			// TODO
		}
		menu.show(tree, x, y);
	}
	public void updateTreeModel(){
		tree.setModel(null);
		tree.setModel(model);
	}
	private void load(WraithEngine worldBuilder){
		File file = Algorithms.getFile("Worlds", "List.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int count = bin.getInt();
		for(int i = 0; i<count; i++){
			mainMaps.add(new Map(worldBuilder, bin.getString()));
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(mainMaps.size());
		for(Map map : mainMaps){
			byte[] bytes = map.getUUID().getBytes();
			bin.allocateBytes(bytes.length+4);
			bin.addInt(bytes.length);
			bin.addBytes(bytes, 0, bytes.length);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", "List.dat"));
	}
	public void addMap(Map map){
		mainMaps.add(map);
		save();
	}
	public void removeMap(Map map){
		mainMaps.remove(map);
		save();
	}
}
