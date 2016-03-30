/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import build.games.wraithaven.util.VerticalFlowLayout;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class AttachScriptsDialog extends JPanel{
	public AttachScriptsDialog(){
		setLayout(new VerticalFlowLayout(0, 5));
		JLabel label = new JLabel("Note to self; do this later.");
		add(label);
	}
	public JComponent getFocus(){
		return null;
	}
	public void compile(MenuComponent component){
		ArrayList<String> scripts = component.getScripts();
		if(scripts.isEmpty()){
			scripts.add("821760e9113d03328e041972");
		}
	}
}
