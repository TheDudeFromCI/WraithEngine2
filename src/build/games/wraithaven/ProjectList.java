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
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class ProjectList extends JFrame {

    private final String[] projects;

    public ProjectList() {
        projects = loadProjects();
        init();
        addComponents();
        setVisible(true);
    }

    private void addComponents() {
        JList<String> list = new JList<String>();
        list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        list.setFont(new Font("Tahoma", Font.PLAIN, 15));
        list.setBackground(Color.LIGHT_GRAY);
        list.setModel(new AbstractListModel<String>() {
            public String getElementAt(int index) {
                return projects[index];
            }

            public int getSize() {
                return projects.length;
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
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                btnLoadProject.setEnabled(list.getSelectedIndex() != -1);
            }
        });
        btnLoadProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProject(list.getSelectedValue());
                dispose();
            }
        });
        JButton btnCreateNewProject = new JButton("Create New Project");
        panel.add(btnCreateNewProject);
        btnCreateNewProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Please enter project name:");
                if (name == null) {
                    return;
                }
                BinaryFile bin = new BinaryFile(4);
                bin.addInt(projects.length + 1);
                for (String s : projects) {
                    byte[] bytes = s.getBytes();
                    bin.allocateBytes(bytes.length + 4);
                    bin.addInt(bytes.length);
                    bin.addBytes(bytes, 0, bytes.length);
                }
                byte[] bytes = name.getBytes();
                bin.allocateBytes(bytes.length + 4);
                bin.addInt(bytes.length);
                bin.addBytes(bytes, 0, bytes.length);
                bin.compress(false);
                bin.compile(Algorithms.getFile("Projects.dat"));
                dispose();
                loadProject(name);
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
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
                System.exit(0);
            }
        });
    }

    private void init() {
        setTitle("Choose Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(336, 435);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void loadProject(String project) {
        WorldBuilder.outputFolder += File.separatorChar + project;
        new WorldBuilder();
    }

    private String[] loadProjects() {
        File file = Algorithms.getFile("Projects.dat");
        if (!file.exists()) {
            return new String[0];
        }
        BinaryFile bin = new BinaryFile(file);
        bin.decompress(false);
        String[] list = new String[bin.getInt()];
        for (int i = 0; i < list.length; i++) {
            list[i] = bin.getString();
        }
        return list;
    }
}
