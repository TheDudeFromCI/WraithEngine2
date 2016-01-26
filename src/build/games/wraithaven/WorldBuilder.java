package build.games.wraithaven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
        final String workspaceName = "Spellgate Data";
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
        WorldScreen worldScreen = new WorldScreen(this);
        getContentPane().add(worldScreen, BorderLayout.CENTER);
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
                System.exit(0);
            }
        });
        mnFile.add(mntmExit);
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
