/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.topdown;

import build.games.wraithaven.core.MapStyle;
import build.games.wraithaven.core.ProjectList;
import build.games.wraithaven.core.WorldList;
import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.core.gameprep.SaveHandler;
import build.games.wraithaven.util.WrongImageSizeException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import wraith.lib.util.Algorithms;

/**
 * @author TheDudeFromCI
 */
public class TopDownMapStyle implements MapStyle{
	private final ChipsetList chipsetList;
	private final MapEditor mapEditor;
	private final WorldList worldList;
	private final JFrame frame;
	public TopDownMapStyle(){
		chipsetList = new ChipsetList(this);
		mapEditor = new MapEditor(this);
		worldList = new WorldList(mapEditor);
		frame = new JFrame();
	}
	public JFrame getFrame(){
		return frame;
	}
	public void openChipsetPreview(File file){
		try{
			new ChipsetPreview(chipsetList, new ChipsetImporter(file));
		}catch(WrongImageSizeException exception){
			// Nothing to worry about.
			// At this point, the window hasn't even attempted to build, so no resources wasted.
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	public ChipsetList getChipsetList(){
		return chipsetList;
	}
	public MapEditor getMapEditor(){
		return mapEditor;
	}
	public WorldList getWorldList(){
		return worldList;
	}
	@Override
	public void buildWindow(){
		frame.setTitle("World Builder");
		frame.setResizable(true);
		frame.setSize(800, 600);
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				if(confirmExit()){
					System.exit(0);
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(chipsetList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, worldList);
		frame.getContentPane().add(splitPane, BorderLayout.WEST);
		splitPane.setDividerSize(2);
		splitPane.setDividerLocation(400);
		frame.getContentPane().add(mapEditor, BorderLayout.CENTER);
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenuItem switchProject = new JMenuItem("Switch Project");
		switchProject.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				frame.dispose();
				WraithEngine.updateFolders(WraithEngine.getWorkspace(), WraithEngine.getAssetFolder());
				new ProjectList();
			}
		});
		mnFile.add(switchProject);
		JMenuItem mntmImportNewChipset = new JMenuItem("Import New Chipset");
		mntmImportNewChipset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				File file = Algorithms.userChooseImage("Import New Chipset", "Import");
				if(file==null){
					return;
				}
				openChipsetPreview(file);
			}
		});
		mnFile.add(mntmImportNewChipset);
		mnFile.addSeparator();
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(confirmExit()){
					System.exit(0);
				}
			}
		});
		mnFile.add(mntmExit);
		frame.setVisible(true);
	}
	private boolean confirmExit(){
		if(!mapEditor.needsSaving()){
			return true;
		}
		int response =
			JOptionPane.showConfirmDialog(null, "You have unsaved progress! Do you wish to save before exiting? All unsaved progress will be lost.",
				"Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION);
		if(response==JOptionPane.YES_OPTION){
			mapEditor.save();
			return true;
		}
		return response==JOptionPane.NO_OPTION;
	}
	@Override
	public SaveHandler getSaveHandler(){
		return new SaveHandler(){
			@Override
			public boolean needsSaving(){
				return mapEditor.needsSaving();
			}
			@Override
			public boolean requestSave(){
				int response = JOptionPane.showConfirmDialog(null, "You must save this project before you can run it. Save now?", "Confirm Save",
					JOptionPane.YES_NO_OPTION);
				if(response==JOptionPane.YES_OPTION){
					mapEditor.save();
					return true;
				}
				return false;
			}
		};
	}
}
