/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * @author TheDudeFromCI
 */
public class ChipsetList extends JPanel{
	private static JButton makeIcon(String asset, String over, String down, String disabled){
		JButton button = new JButton();
		button.setIcon(new ImageIcon(Algorithms.getAsset(asset).getAbsolutePath()));
		button.setRolloverIcon(new ImageIcon(Algorithms.getAsset(over).getAbsolutePath()));
		button.setPressedIcon(new ImageIcon(Algorithms.getAsset(down).getAbsolutePath()));
		button.setDisabledIcon(new ImageIcon(Algorithms.getAsset(disabled).getAbsolutePath()));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		return button;
	}
	private final ChipsetListPainter painter;
	private final EntityList entityList;
	private final EntityLayers entityLayers;
	private final JButton addLayerIcon;
	private final JButton trashLayerIcon;
	public ChipsetList(){
		entityLayers = new EntityLayers(this);
		painter = new ChipsetListPainter();
		entityList = new EntityList(painter.getCursorSelection());
		JTabbedPane tabbedPane = new JTabbedPane();
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		{
			// Tabs
			tabbedPane.addTab("Tiles", new JScrollPane(painter));
			tabbedPane.addTab("Entities", new JScrollPane(entityList));
			{
				// Layer Tab
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JPanel bot = new JPanel();
				bot.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
				{
					addLayerIcon = makeIcon("Plus Symbol.png", "Plus Symbol Over.png", "Plus Symbol Down.png", "Plus Symbol Disabled.png");
					addLayerIcon.setEnabled(false);
					trashLayerIcon = makeIcon("Trash Can.png", "Trash Can Over.png", "Trash Can Down.png", "Trash Can Disabled.png");
					trashLayerIcon.setEnabled(false);
					bot.add(addLayerIcon);
					bot.add(trashLayerIcon);
					addLayerIcon.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							entityLayers.addLayer(new Layer("Layer "+(entityLayers.getLayerCount()+1)));
						}
					});
				}
				panel.add(bot, BorderLayout.SOUTH);
				panel.add(new JScrollPane(entityLayers), BorderLayout.CENTER);
				tabbedPane.addTab("Layers", panel);
			}
		}
	}
	public void updateLayerIcons(){
		addLayerIcon.setEnabled(entityLayers.isLoaded());
		trashLayerIcon.setEnabled(entityLayers.isLoaded());
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
