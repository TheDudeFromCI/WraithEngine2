/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.core.window.BuilderTab;
import java.awt.BorderLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * @author thedudefromci
 */
public class SnipetPreviewer extends BuilderTab{
	private final Renderer renderer;
	private final SnipetList list;
	public SnipetPreviewer(){
		super("Script Editor");
		renderer = new Renderer();
		list = new SnipetList(renderer);
		addComponents();
	}
	private void addComponents(){
		setLayout(new BorderLayout());
		JScrollPane scroll1 = new JScrollPane(list);
		JScrollPane scroll2 = new JScrollPane(renderer);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scroll1, scroll2);
		splitPane.setResizeWeight(0.2);
		add(splitPane, BorderLayout.CENTER);
	}
	@Override
	public void buildTabs(JMenuBar menuBar){
		// We don't have any menu bar commands yet.
		menuBar.add(new JMenu("Place Holder"));
	}
}
