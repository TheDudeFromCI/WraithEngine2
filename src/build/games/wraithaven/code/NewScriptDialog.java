/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.util.VerticalFlowLayout;
import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wraith.lib.code.WraithScript;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class NewScriptDialog extends JPanel{
	private final JTextField text;
	private final JComboBox language;
	private final JTextField des;
	public NewScriptDialog(){
		setLayout(new VerticalFlowLayout(5, VerticalFlowLayout.FILL_SPACE));
		{
			// Name
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(5, 0));
			JLabel label = new JLabel("Name:");
			panel.add(label, BorderLayout.WEST);
			text = new JTextField();
			text.setColumns(20);
			panel.add(text, BorderLayout.CENTER);
			add(panel);
		}
		{
			// Description
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout(5, 0));
			JLabel label = new JLabel("Description:");
			panel.add(label, BorderLayout.WEST);
			des = new JTextField();
			des.setColumns(20);
			panel.add(des, BorderLayout.CENTER);
			add(panel);
		}
		{
			// Language
			language = new JComboBox();
			language.setModel(new DefaultComboBoxModel(new String[]{
				"WraithScript"
			}));
			add(language);
		}
	}
	private LanguageLoader getLanguage(Snipet snipet){
		String lan = (String)language.getSelectedItem();
		switch(lan){
			case "WraithScript":
				return new WraithScript(snipet);
			default:
				throw new RuntimeException("Unknown language! '"+lan+"'");
		}
	}
	public Snipet build(){
		Snipet s = new Snipet(Algorithms.randomUUID());
		s.setLanguage(getLanguage(s));
		s.setName(text.getText());
		s.setDescription(des.getText());
		s.save();
		return s;
	}
}
