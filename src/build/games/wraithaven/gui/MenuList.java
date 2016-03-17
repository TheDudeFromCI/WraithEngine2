/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class MenuList extends JPanel{
	private static final short FILE_VERSION = 0;
	private final ArrayList<Menu> menus = new ArrayList(16);
	public MenuList(){
		load();
		setMinimumSize(new Dimension(100, 200));
		JList list = new JList();
		setLayout(new BorderLayout());
		add(list, BorderLayout.CENTER);
		list.setModel(new DefaultComboBoxModel(new String[]{}));
	}
	private void load(){
		File file = Algorithms.getFile("Menus.dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(true);
		short version = bin.getShort();
		switch(version){
			case 0:
				break;
			default:
				throw new RuntimeException();
		}
	}
	private void save(){
		BinaryFile bin = new BinaryFile(2);
		bin.addShort(FILE_VERSION);
		bin.compress(true);
		bin.compile(Algorithms.getFile("Menus.dat"));
	}
}
