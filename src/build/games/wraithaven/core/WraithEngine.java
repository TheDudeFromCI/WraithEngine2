/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

import build.games.wraithaven.util.Algorithms;
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
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class WraithEngine extends JFrame{
	public static String workspaceFolder;
	public static String outputFolder;
	public static String assetFolder;
	public static String projectName;
	public static WraithEngine INSTANCE;
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception exception){
			exception.printStackTrace();
		}
		final String workspaceName = "WraithEngine";
		if(args.length>0){
			workspaceFolder = args[0]+File.separatorChar+workspaceName;
		}else{
			workspaceFolder = System.getProperty("user.dir")+File.separatorChar+workspaceName;
		}
		outputFolder = workspaceFolder;
		assetFolder = System.getProperty("user.dir")+File.separatorChar+"Assets";
		new ProjectList();
	}
	private final MapStyle mapStyle;
	private AbstractChipsetList chipsetList;
	private AbstractMapEditor mapEditor;
	private WorldList worldList;
	public WraithEngine(MapStyle mapStyle){
		INSTANCE = this;
		this.mapStyle = mapStyle;
		init();
		addComponents();
		setVisible(true);
	}
	public WorldList getWorldList(){
		return worldList;
	}
	public void addComponents(){
		chipsetList = mapStyle.getChipsetList();
		worldList = new WorldList();
		mapEditor = mapStyle.getMapEditor();
		JScrollPane scrollPane = new JScrollPane(chipsetList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, worldList);
		getContentPane().add(splitPane, BorderLayout.WEST);
		splitPane.setDividerSize(2);
		splitPane.setDividerLocation(400);
		getContentPane().add(mapEditor, BorderLayout.CENTER);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenuItem switchProject = new JMenuItem("Switch Project");
		switchProject.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				dispose();
				outputFolder = workspaceFolder;
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
				mapStyle.openChipsetPreview(file);
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
	public MapStyle getMapStyle(){
		return mapStyle;
	}
	private void init(){
		setTitle("World Builder");
		setResizable(true);
		setSize(800, 600);
		setMinimumSize(new Dimension(640, 480));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				if(confirmExit()){
					System.exit(0);
				}
			}
		});
	}
}
