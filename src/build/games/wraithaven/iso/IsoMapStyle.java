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
import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.core.gameprep.GameBuilder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import wraith.lib.util.Algorithms;

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
		updateEntityList();
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
								WraithEngine.updateFolders(WraithEngine.getWorkspace(), WraithEngine.getAssetFolder());
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
						// Import New Raw Tile
						JMenuItem mntmImportNewChipset = new JMenuItem("Import New Raw Tile");
						mntmImportNewChipset.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent event){
								File file = Algorithms.userChooseImage("Import New Raw Tile", "Import");
								if(file==null){
									return;
								}
								BufferedImage image;
								try{
									image = ImageIO.read(file);
									int width = image.getWidth();
									int height = image.getHeight();
									if(width!=height||width!=WraithEngine.projectBitSize){
										image = Algorithms.smoothResize(image, WraithEngine.projectBitSize);
									}
								}catch(Exception exception){
									exception.printStackTrace();
									return;
								}
								TileCategory cat = chipsetList.getSelectedCategory();
								Tile tile = new Tile(Algorithms.randomUUID(), image, cat);
								cat.addTile(tile);
								JOptionPane.showMessageDialog(null, "Tile imported.");
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
								try{
									new EntityImporter(file, chipsetList).setVisible(true);
								}catch(Exception ex){
									ex.printStackTrace();
									JOptionPane.showMessageDialog(null, "There has been an error importing this entity.", "Error",
										JOptionPane.ERROR_MESSAGE);
								}
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
				{
					// Edit
					JMenu menu = new JMenu("Edit");
					menuBar.add(menu);
					{
						// Set background.
						JMenuItem item = new JMenuItem("Set Map Background");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								Map map = getMapEditor().getSelectedMap();
								if(map==null){
									JOptionPane.showMessageDialog(null, "You do not have a map selected!", "Warning", JOptionPane.WARNING_MESSAGE);
									return;
								}
								File image = Algorithms.userChooseImage("Import Background", "Import");
								if(image==null){
									return;
								}
								try{
									BufferedImage i = ImageIO.read(image);
									map.setBackgroundImage(i);
									getMapEditor().getPainter().repaint();
									JOptionPane.showMessageDialog(null, "Background imported.", "Imported", JOptionPane.PLAIN_MESSAGE);
								}catch(Exception exception){
									exception.printStackTrace();
									JOptionPane.showMessageDialog(null, "There has been an error loading this image.", "Error",
										JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						menu.add(item);
					}
				}
				{
					// Run
					JMenu menu = new JMenu("Run");
					menuBar.add(menu);
					{
						// Run Full Game
						JMenuItem item = new JMenuItem("Run Full Game");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								try{
									GameBuilder builder = new GameBuilder();
									builder.compile();
									builder.run("-mapStyle:iso");
								}catch(IOException ex){
									ex.printStackTrace();
									JOptionPane.showMessageDialog(null, "There has been an error trying to launch this game.", "Warning",
										JOptionPane.WARNING_MESSAGE);
								}
							}
						});
						menu.add(item);
					}
					{
						// Preview Map
						JMenuItem item = new JMenuItem("Preview Map");
						item.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){
								try{
									Map map = mapEditor.getSelectedMap();
									if(map==null){
										JOptionPane.showMessageDialog(null, "You must select a map first, to preview it!", "Warning",
											JOptionPane.WARNING_MESSAGE);
										return;
									}
									GameBuilder builder = new GameBuilder();
									builder.compile();
									builder.run("-mapStyle:iso", "-mapPreview:"+map.getUUID());
								}catch(IOException ex){
									ex.printStackTrace();
									JOptionPane.showMessageDialog(null, "There has been an error trying to launch this game.", "Error",
										JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						menu.add(item);
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
	public void updateEntityList(){
		try{
			chipsetList.getEntityList().updateEntities();
		}catch(Exception exception){
			// Fails if not finished loading.
			// Not a problem.
		}
	}
}
