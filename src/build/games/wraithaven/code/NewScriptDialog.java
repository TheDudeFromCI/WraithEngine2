/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import wraith.lib.code.WraithScript;
import build.games.wraithaven.util.VerticalFlowLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class NewScriptDialog extends JPanel{
	private final JTextField text;
	private final JComboBox language;
	public NewScriptDialog(){
		setLayout(new VerticalFlowLayout(0, 5));
		text = new JTextField();
		text.setColumns(20);
		language = new JComboBox();
		language.setModel(new DefaultComboBoxModel(new String[]{
			"WraithScript"
		}));
		add(text);
		add(language);
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
		s.save();
		return s;
	}
}
