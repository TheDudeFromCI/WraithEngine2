/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * @author TheDudeFromCI
 */
public class ChipsetList extends JPanel{
	private final ChipsetListPainter painter;
	private final EntityList entityList;
	public ChipsetList(){
		painter = new ChipsetListPainter();
		entityList = new EntityList();
		JTabbedPane tabbedPane = new JTabbedPane();
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		{
			// Tabs
			tabbedPane.addTab("Tiles", new JScrollPane(painter));
			tabbedPane.addTab("Entities", new JScrollPane(entityList));
		}
	}
	public Tile getTile(String uuid){
		return painter.getTile(uuid);
	}
	public CursorSelection getCursorSelection(){
		return painter.getCursorSelection();
	}
	public int getIndexOfTile(Tile tile){
		return painter.getIndexOfTile(tile);
	}
	public void addTile(Tile tile){
		painter.addTile(tile);
	}
	public EntityList getEntityList(){
		return entityList;
	}
}
