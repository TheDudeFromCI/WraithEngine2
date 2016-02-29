/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.core;

import run.wraith.engine.opengl.loop.InputHandler;
import run.wraith.engine.opengl.loop.RenderLoop;

/**
 * @author thedudefromci
 */
public interface RunProtocol{
	public void initalize();
	public RenderLoop getRenderLoop();
	public InputHandler getInputHandler();
	public void preLoop();
	public void dispose();
}
