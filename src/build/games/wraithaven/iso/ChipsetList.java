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
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
	private final JButton trashCategoryIcon;
	private final JTabbedPane tabbedPane;
	private final JComboBox categoryComboBox;
	private final CategoryComboBoxModel categoryComboBoxModel;
	public ChipsetList(IsoMapStyle mapStyle){
		categoryComboBoxModel = new CategoryComboBoxModel(mapStyle);
		entityLayers = new EntityLayers(mapStyle);
		painter = new ChipsetListPainter(mapStyle);
		entityList = new EntityList(mapStyle);
		tabbedPane = new JTabbedPane();
		setLayout(new BorderLayout());
		{
			// Select tile category drop down.
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			categoryComboBox = new JComboBox();
			categoryComboBox.setModel(categoryComboBoxModel);
			categoryComboBox.setEditable(true);
			trashCategoryIcon = makeIcon("Trash Can.png", "Trash Can Over.png", "Trash Can Down.png", "Trash Can Disabled.png");
			trashCategoryIcon.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					TileCategory sel = categoryComboBoxModel.getSelected();
					int response = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete the '"+sel.getName()+"' category?",
						"Confirm Delete", JOptionPane.YES_NO_OPTION);
					if(response!=JOptionPane.YES_OPTION){
						return;
					}
					categoryComboBoxModel.deleteCategory(sel);
				}
			});
			panel.add(trashCategoryIcon, BorderLayout.EAST);
			panel.add(categoryComboBox, BorderLayout.CENTER);
			add(panel, BorderLayout.NORTH);
		}
		add(tabbedPane, BorderLayout.CENTER);
		{
			// Tabs
			tabbedPane.addTab("Tiles", new JScrollPane(painter));
			{
				// Entities Tab
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
					trashLayerIcon.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this layer?", "Confirm Delete",
								JOptionPane.YES_NO_OPTION);
							if(response!=JOptionPane.YES_OPTION){
								return;
							}
							Layer layer = entityLayers.getSelectedLayer();
							entityLayers.removeLayer(layer);
							if(entityLayers.getLayerCount()==0){
								// We don't want the player to not have any layers.
								// That may cause issues.
								entityLayers.addLayer(new Layer("Layer 1"));
							}
							Map map = mapStyle.getMapEditor().getPainter().getMap();
							map.deleteLayer(layer);
							mapStyle.getMapEditor().getPainter().repaint();
						}
					});
				}
				panel.add(bot, BorderLayout.SOUTH);
				panel.add(new JScrollPane(entityLayers), BorderLayout.CENTER);
				JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(entityList), panel);
				tabbedPane.addTab("Entities", split);
			}
		}
	}
	public void updateCategoryList(){
		categoryComboBox.setModel(new DefaultComboBoxModel(new String[0]));
		categoryComboBox.setModel(categoryComboBoxModel);
	}
	public boolean isTileMode(){
		return tabbedPane.getSelectedIndex()==0;
	}
	public void updateLayerIcons(){
		addLayerIcon.setEnabled(entityLayers.isLoaded());
		trashLayerIcon.setEnabled(entityLayers.isLoaded());
	}
	public Tile getTile(String cat, String tile){
		return categoryComboBoxModel.getCategory(cat).getTile(tile);
	}
	public int getIndexOfTile(Tile tile){
		return tile.getCategory().getIndexOf(tile);
	}
	public int getIndexOfEntity(EntityType entity){
		return entity.getCategory().getIndexOf(entity);
	}
	public CursorSelection getCursorSelection(){
		return painter.getCursorSelection();
	}
	public EntityList getEntityList(){
		return entityList;
	}
	public EntityLayers getEntityLayers(){
		return entityLayers;
	}
	public TileCategory getSelectedCategory(){
		return categoryComboBoxModel.getSelected();
	}
	public ChipsetListPainter getPainter(){
		return painter;
	}
	public EntityType getEntity(String cat, String entity){
		return categoryComboBoxModel.getCategory(cat).getEntity(entity);
	}
}
