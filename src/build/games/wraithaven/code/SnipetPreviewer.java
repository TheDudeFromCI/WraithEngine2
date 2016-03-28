/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author thedudefromci
 */
public class SnipetPreviewer{
	private static JFrame frame;
	public static void launch(){
		if(frame!=null){
			return;
		}
		frame = new JFrame();
		frame.setTitle("Snipet Previewer");
		frame.setSize(700, 525);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				frame = null;
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addComponents();
		frame.setVisible(true);
	}
	public static void closeFrame(){
		if(frame!=null){
			frame.dispose();
			frame = null;
		}
	}
	private static void addComponents(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		Renderer renderer = new Renderer();
		SnipetList list = new SnipetList(renderer);
		panel.add(list, BorderLayout.WEST);
		panel.add(renderer, BorderLayout.CENTER);
		frame.add(panel);
	}
}
