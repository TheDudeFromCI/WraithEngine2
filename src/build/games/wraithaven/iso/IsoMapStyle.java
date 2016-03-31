/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.core.MapStyle;
import build.games.wraithaven.core.gameprep.SaveHandler;
import build.games.wraithaven.core.window.BuilderTab;
import build.games.wraithaven.core.window.WindowStructure;
import build.games.wraithaven.core.window.WindowStructureBuilder;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author TheDudeFromCI
 */
public class IsoMapStyle implements MapStyle{
	private final WindowStructure windowStructure;
	private final MapEditorTab mapEditorTab;
	public IsoMapStyle(){
		mapEditorTab = new MapEditorTab(this);
		BuilderTab[] tabs = new BuilderTab[]{
			mapEditorTab
		};
		WindowStructureBuilder builder = new WindowStructureBuilder(){
			@Override
			public BuilderTab[] getTabs(){
				return tabs;
			}
			@Override
			public boolean confirmExit(){
				return mapEditorTab.confirmExit();
			}
		};
		windowStructure = new WindowStructure(builder);
	}
	public JFrame getFrame(){
		return windowStructure.getFrame();
	}
	@Override
	public void buildWindow(){
		windowStructure.show();
	}
	@Override
	public SaveHandler getSaveHandler(){
		return new SaveHandler(){
			@Override
			public boolean needsSaving(){
				return mapEditorTab.getMapEditor().needsSaving();
			}
			@Override
			public boolean requestSave(){
				int response = JOptionPane.showConfirmDialog(null, "You must save this project before you can run it. Save now?", "Confirm Save",
					JOptionPane.YES_NO_OPTION);
				if(response==JOptionPane.YES_OPTION){
					mapEditorTab.getMapEditor().save();
					return true;
				}
				return false;
			}
		};
	}
}
