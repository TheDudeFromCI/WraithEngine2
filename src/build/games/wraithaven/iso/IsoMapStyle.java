/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.MapStyle;
import build.games.wraithaven.core.ProjectList;
import build.games.wraithaven.core.WorldList;
import static build.games.wraithaven.core.WraithEngine.outputFolder;
import static build.games.wraithaven.core.WraithEngine.workspaceFolder;
import build.games.wraithaven.util.Algorithms;
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
import javax.swing.JSplitPane;

/**
 * @author TheDudeFromCI
 */
public class IsoMapStyle implements MapStyle{
	private final ChipsetList chipsetList;
	private final MapEditor mapEditor;
	private final WorldList worldList;
	private final JFrame frame;
	public IsoMapStyle(){
		chipsetList = new ChipsetList(this);
		mapEditor = new MapEditor(this);
		worldList = new WorldList(mapEditor);
		frame = new JFrame();
	}
	public JFrame getFrame(){
		return frame;
	}
	public MapEditor getMapEditor(){
		return mapEditor;
	}
	public ChipsetList getChipsetList(){
		return chipsetList;
	}
	public WorldList getWorldList(){
		return worldList;
	}
	@Override
	public void buildWindow(){
		{
			// Initalize
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
		}
		{
			// Add components
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, chipsetList, worldList);
			frame.getContentPane().add(splitPane, BorderLayout.WEST);
			splitPane.setDividerSize(2);
			splitPane.setDividerLocation(400);
			frame.getContentPane().add(mapEditor, BorderLayout.CENTER);
			{
				// Menu
				JMenuBar menuBar = new JMenuBar();
				frame.setJMenuBar(menuBar);
				{
					// File
					JMenu mnFile = new JMenu("File");
					menuBar.add(mnFile);
					{
						// Switch Project
						JMenuItem switchProject = new JMenuItem("Switch Project");
						switchProject.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								frame.dispose();
								outputFolder = workspaceFolder;
								new ProjectList();
							}
						});
						mnFile.add(switchProject);
					}
					{
						// Import New Tile
						JMenuItem mntmImportNewChipset = new JMenuItem("Import New Tile");
						mntmImportNewChipset.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent event){
								File file = Algorithms.userChooseImage("Import New Tile", "Import");
								if(file==null){
									return;
								}
								new ChipsetImporter(chipsetList, file);
							}
						});
						mnFile.add(mntmImportNewChipset);
					}
					{
						// Import New Entity
						JMenuItem item = new JMenuItem("Import New Entity");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								File file = Algorithms.userChooseImage("Import New Entity", "Import");
								if(file==null){
									return;
								}
								EntityImporter importer;
								try{
									importer = new EntityImporter(file);
								}catch(WrongImageSizeException exception){
									JOptionPane.showMessageDialog(null, exception.getMessage(), "Error, Wrong Image Size", JOptionPane.ERROR_MESSAGE);
									return;
								}
								int response = JOptionPane.showConfirmDialog(null, importer, "Import New Entity", JOptionPane.OK_CANCEL_OPTION);
								if(response!=JOptionPane.OK_OPTION){
									return;
								}
								chipsetList.getEntityList().addEntityType(importer.build(), importer.getEntityImage());
							}
						});
						mnFile.add(item);
					}
					{
						// Exit
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
					}
				}
			}
		}
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
	public void updateTileList(){
		try{
			chipsetList.getPainter().updateTiles();
		}catch(Exception exception){
			// Fails if not finished loading.
			// Not a problem.
		}
	}
}
