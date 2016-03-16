/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class MenuComponentList extends JPanel{
	private Menu menu;
	public Menu getMenu(){
		return menu;
	}
	public void setMenu(Menu menu){
		this.menu = menu;
		repaint();
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setColor(Color.lightGray);
		int width = getWidth();
		int height = getHeight();
		g.fillRect(0, 0, width, height);
		if(menu!=null){}
		g.dispose();
	}
}
