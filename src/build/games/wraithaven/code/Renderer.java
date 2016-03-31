/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class Renderer extends JPanel{
	private Snipet snipet;
	public Renderer(){
		setLayout(new BorderLayout());
		setBackground(Color.lightGray);
	}
	public void loadSnipet(Snipet snipet){
		removeAll();
		if(this.snipet!=null){
			this.snipet.save();
			this.snipet.dispose();
		}
		this.snipet = snipet;
		if(this.snipet!=null){
			this.snipet.load();
			LanguageLoader lan = this.snipet.getLanguage();
			if(lan!=null){
				JPanel panel = lan.getRenderComponent();
				if(panel!=null){
					add(panel, BorderLayout.CENTER);
				}
			}
		}
		revalidate();
		repaint();
	}
}
