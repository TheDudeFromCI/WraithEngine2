/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code;

import build.games.wraithaven.code.LanguageLoader;
import build.games.wraithaven.code.Snipet;
import build.games.wraithaven.code.languages.NodeLineLogic;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import run.wraith.engine.code.CodeLanguage;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class WraithScript implements LanguageLoader, CodeLanguage{
	private static final int ID = 0;
	private final WraithScriptLogic logic;
	private final Snipet snipet;
	public WraithScript(Snipet snipet){
		this.snipet = snipet;
		logic = new WraithScriptLogic();
	}
	@Override
	public JPanel getRenderComponent(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		NodeLineLogic nodeLineLogic = new NodeLineLogic(snipet, logic);
		JScrollPane scrollPane = new JScrollPane(nodeLineLogic);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	@Override
	public void save(BinaryFile bin){
		logic.save(bin);
	}
	@Override
	public void load(BinaryFile bin, short version){
		logic.load(bin, version);
	}
	@Override
	public int getId(){
		return ID;
	}
	@Override
	public void run(){
		logic.run();
	}
}
