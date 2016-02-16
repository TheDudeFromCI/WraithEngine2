/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataListener;

/**
 * @author thedudefromci
 */
public class CategoryComboBoxModel implements ComboBoxModel{
	private final ArrayList<ListDataListener> listeners = new ArrayList(1);
	private final TileCategoryList list;
	private final IsoMapStyle mapStyle;
	private TileCategory selected;
	private String lastName;
	public CategoryComboBoxModel(IsoMapStyle mapStyle){
		this.mapStyle = mapStyle;
		list = new TileCategoryList(mapStyle);
		if(list.getSize()==0){
			TileCategory category = new TileCategory(mapStyle, Algorithms.randomUUID());
			category.setName("Default");
			list.addCategory(category);
		}
		select(list.getCategoryAt(0));
	}
	@Override
	public void setSelectedItem(Object anItem){
		boolean n = false;
		if(anItem instanceof String){
			if(lastName!=null&&lastName.equals(anItem)){
				lastName = null;
				return;
			}
			lastName = (String)anItem;
			int response =
				JOptionPane.showConfirmDialog(null, "Do you want to create the '"+anItem+"' category?", "Confirm Create", JOptionPane.YES_NO_OPTION);
			if(response!=JOptionPane.YES_OPTION){
				return;
			}
			lastName = null;
			TileCategory cat = new TileCategory(mapStyle, Algorithms.randomUUID());
			cat.setName((String)anItem);
			list.addCategory(cat);
			anItem = cat;
			n = true;
		}
		if(selected!=anItem){
			select((TileCategory)anItem);
		}
		if(n){
			mapStyle.getChipsetList().updateCategoryList();
		}
	}
	@Override
	public Object getSelectedItem(){
		return selected;
	}
	@Override
	public int getSize(){
		return list.getSize();
	}
	@Override
	public Object getElementAt(int index){
		return list.getCategoryAt(index);
	}
	@Override
	public void addListDataListener(ListDataListener l){
		listeners.add(l);
	}
	@Override
	public void removeListDataListener(ListDataListener l){
		listeners.remove(l);
	}
	public TileCategory getSelected(){
		return selected;
	}
	public void deleteCategory(TileCategory cat){
		int index = list.getIndexOf(cat);
		list.removeCategory(cat);
		if(index>=list.getSize()){
			index = list.getSize()-1;
		}
		if(index==-1){
			TileCategory category = new TileCategory(mapStyle, Algorithms.randomUUID());
			String name = "Default";
			if(cat.getName().startsWith("Default")){
				if(cat.getName().equals(name)){
					name = "Default 2";
				}else{
					try{
						int id = Integer.valueOf(cat.getName().substring("Default ".length()));
						name = "Default "+(id+1);
					}catch(Exception exception){}
				}
			}
			category.setName(name);
			list.addCategory(category);
			select(category);
		}else{
			select(list.getCategoryAt(index));
		}
		mapStyle.getChipsetList().updateCategoryList();
	}
	private void select(TileCategory cat){
		selected = cat;
		mapStyle.updateTileList();
	}
	public TileCategory getCategory(String uuid){
		return list.getCategory(uuid);
	}
}
