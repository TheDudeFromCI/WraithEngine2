/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code;

import java.awt.FlowLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author thedudefromci
 */
public class TrueFalseComponent extends JPanel{
	private final JRadioButton t;
	public TrueFalseComponent(){
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		t = new JRadioButton("True");
		add(t);
		JRadioButton f = new JRadioButton("False");
		f.setSelected(true);
		add(f);
		ButtonGroup group = new ButtonGroup();
		group.add(t);
		group.add(f);
	}
	public boolean getState(){
		return t.isSelected();
	}
}
