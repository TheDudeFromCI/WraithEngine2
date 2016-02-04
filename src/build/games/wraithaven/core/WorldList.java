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
import build.games.wraithaven.util.InputAdapter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
				return ((MapInterface)parent).getChild(index);
			}
		}
		@Override
		public int getChildCount(Object parent){
			if(parent==root){
				return mainMaps.size();
			}else{
				return ((MapInterface)parent).getChildCount();
			}
		}
		@Override
		public boolean isLeaf(Object node){
			if(node==root){
				return false;
			}else{
				return ((MapInterface)node).getChildCount()==0;
			}
		}
		@Override
		public void valueForPathChanged(TreePath path, Object newValue){}
		@Override
		public int getIndexOfChild(Object parent, Object child){
			if(parent==root){
				return mainMaps.indexOf(child);
			}else{
				return ((MapInterface)parent).getIndexOf((MapInterface)child);
			}
		}
		@Override
		public void addTreeModelListener(TreeModelListener l){}
		@Override
		public void removeTreeModelListener(TreeModelListener l){}
	}
	private final JTree tree;
	private final ArrayList<MapInterface> mainMaps = new ArrayList(64);
	private final MapStructure model;
	public WorldList(){
		setLayout(new BorderLayout());
		load();
		tree = new JTree();
		model = new MapStructure();
		tree.setRootVisible(true);
		tree.setDragEnabled(true);
		tree.setToggleClickCount(0);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		InputAdapter ml = new InputAdapter(){
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
							if(selected instanceof MapInterface){
								WraithEngine.INSTANCE.getMapStyle().selectMap((MapInterface)selected);
							}
						}
					}else if(button==MouseEvent.BUTTON3){
						// If right click.
						Object selected = selPath.getLastPathComponent();
						tree.setSelectionPath(selPath);
						if(selected instanceof MapInterface){
							showContextMenu((MapInterface)selected, e.getX(), e.getY());
						}else{
							showContextMenu(null, e.getX(), e.getY());
						}
					}
				}
			}
			@Override
			public void keyPressed(KeyEvent event){
				if(event.getKeyCode()==KeyEvent.VK_DELETE){
					TreePath path = tree.getSelectionPath();
					if(path==null){
						return;
					}
					Object selected = tree.getSelectionPath().getLastPathComponent();
					if(selected==null||!(selected instanceof MapInterface)){
						return;
					}
					deleteMap((MapInterface)selected);
				}
			}
		};
		tree.addMouseListener(ml);
		tree.addKeyListener(ml);
		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
		updateTreeModel();
	}
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
		for(int i = startingIndex; i<rowCount; ++i){
			tree.expandRow(i);
		}
		if(tree.getRowCount()!=rowCount){
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}
	private void showContextMenu(MapInterface selectedMap, int x, int y){
		// Show map properties.
		// If selectedMap is null, then global properties.
		JPopupMenu menu = new JPopupMenu();
		{
			// New child map.
			JMenuItem newMap = new JMenuItem(selectedMap==null?"New Map":"New Child Map");
			newMap.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					new NewMapDialog(selectedMap);
				}
			});
			menu.add(newMap);
		}
		if(selectedMap==null){
			// TODO
		}else{
			{
				// Delete
				JMenuItem item = new JMenuItem("Delete");
				item.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						deleteMap(selectedMap);
					}
				});
				menu.add(item);
			}
		}
		menu.show(tree, x, y);
	}
	public void updateTreeModel(){
		tree.setModel(null);
		tree.setModel(model);
		expandAllNodes(tree, 0, tree.getRowCount());
	}
	private void deleteMap(MapInterface map){
		int response = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete this map? All child maps will also be deleted.",
			"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if(response!=JOptionPane.YES_OPTION){
			return;
		}
		MapInterface parent = map.getParent();
		if(parent==null){
			mainMaps.remove(map);
			save();
		}else{
			parent.removeChild(map);
		}
		map.delete();
		updateTreeModel();
		if(WraithEngine.INSTANCE.getMapStyle().getSelectedMap()==map){
			WraithEngine.INSTANCE.getMapStyle().selectMap(null);
		}
	}
	private void load(){
		File file = Algorithms.getFile("Worlds", "List.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int count = bin.getInt();
		for(int i = 0; i<count; i++){
			mainMaps.add(WraithEngine.INSTANCE.getMapStyle().loadMap(bin.getString()));
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(mainMaps.size());
		for(MapInterface map : mainMaps){
			byte[] bytes = map.getUUID().getBytes();
			bin.allocateBytes(bytes.length+4);
			bin.addInt(bytes.length);
			bin.addBytes(bytes, 0, bytes.length);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", "List.dat"));
	}
	public void addMap(MapInterface map){
		mainMaps.add(map);
		save();
	}
	public void removeMap(MapInterface map){
		mainMaps.remove(map);
		save();
	}
	public MapInterface getMap(String uuid){
		for(MapInterface map : mainMaps){
			MapInterface map2 = getMap(map, uuid);
			if(map2!=null){
				return map2;
			}
		}
		return null;
	}
	private MapInterface getMap(MapInterface map, String uuid){
		if(map.getUUID().equals(uuid)){
			return map;
		}
		for(int i = 0; i<map.getChildCount(); i++){
			MapInterface map2 = getMap(map.getChild(i), uuid);
			if(map2!=null){
				return map2;
			}
		}
		return null;
	}
}
