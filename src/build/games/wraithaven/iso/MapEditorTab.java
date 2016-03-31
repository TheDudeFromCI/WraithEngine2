/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.code.SnipetPreviewer;
import build.games.wraithaven.core.ProjectList;
import build.games.wraithaven.core.WorldList;
import build.games.wraithaven.core.WraithEngine;
import build.games.wraithaven.core.gameprep.GameBuilder;
import build.games.wraithaven.core.window.BuilderTab;
import build.games.wraithaven.gui.GuiEditor;
import build.games.wraithaven.gui.Menu;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BackupUtils;

/**
 * @author thedudefromci
 */
public class MapEditorTab extends BuilderTab{
	private final ChipsetList chipsetList;
	private final MapEditor mapEditor;
	private final WorldList worldList;
	private final IsoMapStyle mapStyle;
	public MapEditorTab(IsoMapStyle mapStyle){
		super("Isometric Map Editor");
		this.mapStyle = mapStyle;
		chipsetList = new ChipsetList(this);
		mapEditor = new MapEditor(this);
		worldList = new WorldList(mapEditor);
		addComponents();
		updateEntityList();
	}
	private void addComponents(){
		setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, chipsetList, worldList);
		add(splitPane, BorderLayout.WEST);
		splitPane.setDividerLocation(400);
		add(mapEditor, BorderLayout.CENTER);
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
	public JFrame getFrame(){
		return mapStyle.getFrame();
	}
	@Override
	public void buildTabs(JMenuBar menuBar){
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
						GuiEditor.closeFrame();
						SnipetPreviewer.closeFrame();
						mapStyle.getFrame().dispose();
						WraithEngine.updateFolders(WraithEngine.getWorkspace(), WraithEngine.getAssetFolder());
						new ProjectList();
					}
				});
				mnFile.add(switchProject);
			}
			mnFile.addSeparator();
			{
				// Import New Tile
				JMenuItem mntmImportNewChipset = new JMenuItem("Import New Tile");
				mntmImportNewChipset.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent event){
						File file = Algorithms.userChooseFile("Import New Tile", "Import", "png");
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
						File file = Algorithms.userChooseFile("Import New Raw Tile", "Import", "png");
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
						int response = JOptionPane.showConfirmDialog(null, new RawTileImporter(image), "Confirm Import", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
						if(response!=JOptionPane.OK_OPTION){
							return;
						}
						TileCategory cat = chipsetList.getSelectedCategory();
						Tile tile = new Tile(Algorithms.randomUUID(), image, cat);
						cat.addTile(tile);
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
						File file = Algorithms.userChooseFile("Import New Entity", "Import", "png");
						if(file==null){
							return;
						}
						try{
							new EntityImporter(file, chipsetList).setVisible(true);
						}catch(Exception ex){
							ex.printStackTrace();
							JOptionPane.showMessageDialog(null, "There has been an error importing this entity.", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				mnFile.add(item);
			}
			{
				// Backups
				JMenu menu2 = new JMenu("Backups");
				{
					// Save
					JMenuItem item = new JMenuItem("Create");
					item.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							File folder = Algorithms.getFile();
							String date = Algorithms.getFormattedData();
							File zip = Algorithms.getRawFile(WraithEngine.getWorkspace(), "Backups", WraithEngine.projectUUID, date+".zip");
							try{
								BackupUtils.createBackup(folder, zip);
								JOptionPane.showMessageDialog(null, "Backup '"+date+".zip' created.");
							}catch(IOException ex){
								ex.printStackTrace();
								JOptionPane.showMessageDialog(null, "There has been an error attempting to create this backup.", "Error",
									JOptionPane.ERROR_MESSAGE);
								// Just to clean up any mess.
								if(zip.exists()){
									zip.delete();
								}
							}
						}
					});
					menu2.add(item);
				}
				{
					// Load
					JMenuItem item = new JMenuItem("Load");
					item.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							File backupDir = Algorithms.getRawFile(WraithEngine.getWorkspace(), "Backups", WraithEngine.projectUUID);
							File file = Algorithms.userChooseFile("Load Backup", "Load", "zip", backupDir);
							if(file==null){
								return;
							}
							int response = JOptionPane.showConfirmDialog(null,
								"Are you sure you want to load this backup? All current data WILL BE LOST!", "Confirm Load", JOptionPane.YES_NO_OPTION);
							if(response!=JOptionPane.YES_OPTION){
								return;
							}
							File folder = Algorithms.getFile();
							// Edit all files.
							try{
								BackupUtils.loadBackup(folder, file);
							}catch(IOException ex){
								ex.printStackTrace();
								JOptionPane.showMessageDialog(null, "There has been an error attempting to load this backup.", "Error",
									JOptionPane.ERROR_MESSAGE);
							}
							// Close current window, and reload.
							GuiEditor.closeFrame();
							SnipetPreviewer.closeFrame();
							mapStyle.getFrame().dispose();
							new IsoMapStyle().buildWindow();
						}
					});
					menu2.add(item);
				}
				mnFile.add(menu2);
			}
			mnFile.addSeparator();
			{
				// Exit
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
				// Backgrounds
				JMenu menu2 = new JMenu("Background");
				menu.add(menu2);
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
							File image = Algorithms.userChooseFile("Import Background", "Import", "png");
							if(image==null){
								return;
							}
							try{
								BufferedImage i = ImageIO.read(image);
								map.setBackgroundImage(i);
								getMapEditor().getPainter().repaint();
							}catch(Exception exception){
								exception.printStackTrace();
								JOptionPane.showMessageDialog(null, "There has been an error loading this image.", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					menu2.add(item);
				}
				{
					// Clear background.
					JMenuItem item = new JMenuItem("Clear Map Background");
					item.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							Map map = getMapEditor().getSelectedMap();
							if(map==null){
								JOptionPane.showMessageDialog(null, "You do not have a map selected!", "Warning", JOptionPane.WARNING_MESSAGE);
								return;
							}
							map.setBackgroundImage(null);
							getMapEditor().getPainter().repaint();
						}
					});
					menu2.add(item);
				}
			}
			{
				// Gui
				JMenuItem item = new JMenuItem("Menus");
				item.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						GuiEditor.launch();
					}
				});
				menu.add(item);
			}
			{
				// Code
				JMenuItem item = new JMenuItem("Code Snipets");
				item.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						SnipetPreviewer.launch();
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
							GameBuilder builder = new GameBuilder(mapStyle);
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
							Menu menu = GuiEditor.getSelectedMenu();
							GameBuilder builder = new GameBuilder(mapStyle);
							builder.compile();
							ArrayList<String> args = new ArrayList(4);
							args.add("-mapStyle:iso"); // Runtime Arg
							args.add("-mapPreview:"+map.getUUID()); // Runtime Arg
							if(menu!=null){
								args.add("menu:"+menu.getUUID()); // Run Specific Arg
							}
							String[] args2 = new String[args.size()];
							args.toArray(args2);
							builder.run(args2);
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
	public boolean confirmExit(){
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
	public void updateEntityList(){
		try{
			chipsetList.getEntityList().updateEntities();
		}catch(Exception exception){
			// Fails if not finished loading.
			// Not a problem.
		}
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
