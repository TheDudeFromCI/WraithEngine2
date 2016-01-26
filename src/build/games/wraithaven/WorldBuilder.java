/* 
 * Copyright (C) 2016 TheDudeFromCI
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class WorldBuilder extends JFrame {

    public static String workspaceFolder;
    public static String outputFolder;
    public static String assetFolder;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        final String workspaceName = "WraithEngine";
        if (args.length > 0) {
            workspaceFolder = args[0] + File.separatorChar + workspaceName;
        } else {
            workspaceFolder = System.getProperty("user.dir") + File.separatorChar + workspaceName;
        }
        outputFolder = workspaceFolder;
        assetFolder = System.getProperty("user.dir") + File.separatorChar + "Assets";
        new ProjectList();
    }
    private ChipsetList chipsetList;
    private WorldScreen worldScreen;

    public WorldBuilder() {
        init();
        addComponents();
        setVisible(true);
    }

    public void addComponents() {
        chipsetList = new ChipsetList();
        JScrollPane scrollPane = new JScrollPane(chipsetList);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane, BorderLayout.WEST);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        worldScreen = new WorldScreen(this);
        WorldScreenToolbar worldScreenToolbar = new WorldScreenToolbar();
        panel.add(worldScreenToolbar, BorderLayout.NORTH);
        panel.add(worldScreen, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        JMenuItem switchProject = new JMenuItem("Switch Project");
        switchProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                outputFolder = workspaceFolder;
                new ProjectList();
            }
        });
        mnFile.add(switchProject);
        JMenuItem mntmImportNewChipset = new JMenuItem("Import New Chipset");
        mntmImportNewChipset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() || file.getName().endsWith(".png");
                    }

                    @Override
                    public String getDescription() {
                        return "*.PNG Files";
                    }
                });
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setDialogTitle("Import New Chipset");
                fileChooser.setAcceptAllFileFilterUsed(true);
                fileChooser.showDialog(null, "Import");
                File file = fileChooser.getSelectedFile();
                if (file == null) {
                    return;
                }
                try {
                    new ChipsetPreview(WorldBuilder.this, new ChipsetImporter(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mnFile.add(mntmImportNewChipset);
        mnFile.addSeparator();
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (confirmExit()) {
                    System.exit(0);
                }
            }
        });
        mnFile.add(mntmExit);
    }

    private boolean confirmExit() {
        if (!worldScreen.needsSaving()) {
            return true;
        }
        int response = JOptionPane.showConfirmDialog(null, "You have unsaved progress! Are you sure you want to exit? All unsaved progress will be lost.", "Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        return response == JOptionPane.YES_OPTION;
    }

    public ChipsetList getChipsetList() {
        return chipsetList;
    }

    private void init() {
        setTitle("World Builder");
        setResizable(true);
        setSize(800, 600);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmExit()) {
                    System.exit(0);
                }
            }

        });
    }
}
