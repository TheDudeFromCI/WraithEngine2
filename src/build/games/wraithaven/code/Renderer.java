/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class Renderer extends JPanel{
	private Snipet snipet;
	@Override
	public void paintComponent(Graphics g){
		if(snipet==null||snipet.getLanguage()==null){
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, getWidth(), getHeight());
		}else{
			snipet.getLanguage().draw((Graphics2D)g, getWidth(), getHeight(), snipet);
		}
		g.dispose();
	}
	public void loadSnipet(Snipet snipet){
		if(this.snipet!=null){
			this.snipet.save();
			this.snipet.dispose();
		}
		this.snipet = snipet;
		if(this.snipet!=null){
			this.snipet.load();
		}
		repaint();
	}
}
