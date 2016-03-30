/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso.preview;

import run.wraith.engine.core.RunProtocol;
import run.wraith.engine.gui.Gui;
import run.wraith.engine.mapstyles.iso.Map;
import run.wraith.engine.mapstyles.iso.MapRenderer;
import run.wraith.engine.opengl.loop.InputHandler;
import run.wraith.engine.opengl.loop.RenderLoop;
import wraith.lib.util.SortedMap;

/**
 * @author thedudefromci
 */
public class MapPreviewProtocol implements RunProtocol{
	private final String mapId;
	private final SortedMap<String,String> args;
	private MapRenderer renderer;
	private MapPreviewInputHandler inputHandler;
	public MapPreviewProtocol(String mapId, SortedMap<String,String> args){
		this.mapId = mapId;
		this.args = args;
	}
	@Override
	public RenderLoop getRenderLoop(){
		return renderer;
	}
	@Override
	public InputHandler getInputHandler(){
		return inputHandler;
	}
	@Override
	public void initalize(){
		renderer = new MapRenderer();
		inputHandler = new MapPreviewInputHandler();
	}
	@Override
	public void preLoop(){
		renderer.initalize();
		Map map = new Map(mapId);
		renderer.setMap(map);
		Gui gui = null;
		{
			// Load menu, if available.
			String menu = getArg("menu", null);
			if(menu!=null){
				gui = new Gui();
				gui.loadMenu(menu);
				renderer.setGui(gui);
			}
		}
		inputHandler.load(map, gui);
	}
	@Override
	public void dispose(){
		renderer.dispose();
	}
	public String getArg(String key, String def){
		String val = args.get(key);
		if(val==null){
			return def;
		}
		return val;
	}
}
