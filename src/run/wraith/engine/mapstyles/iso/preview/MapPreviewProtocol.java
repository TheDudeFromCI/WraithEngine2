/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso.preview;

import run.wraith.engine.core.RunProtocol;
import run.wraith.engine.mapstyles.iso.Map;
import run.wraith.engine.mapstyles.iso.MapRenderer;
import run.wraith.engine.opengl.loop.InputHandler;
import run.wraith.engine.opengl.loop.RenderLoop;

/**
 * @author thedudefromci
 */
public class MapPreviewProtocol implements RunProtocol{
	private final String mapId;
	private MapRenderer renderer;
	private MapPreviewInputHandler inputHandler;
	public MapPreviewProtocol(String mapId){
		this.mapId = mapId;
	}
	@Override
	public void initalize(){
		renderer = new MapRenderer();
		inputHandler = new MapPreviewInputHandler();
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
	public void preLoop(){
		renderer.initalize();
		Map map = new Map(mapId);
		renderer.setMap(map);
		inputHandler.loadMap(map);
	}
	@Override
	public void dispose(){
		renderer.dispose();
	}
}
