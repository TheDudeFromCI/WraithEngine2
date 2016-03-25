/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui;

import run.wraith.engine.opengl.renders.Model;
import run.wraith.engine.opengl.renders.ModelInstance;
import run.wraith.engine.opengl.renders.Texture;

/**
 * @author thedudefromci
 */
public class ImageModelInstance extends ModelInstance{
	private final Texture texture;
	public ImageModelInstance(Model model, Texture texture){
		super(model);
		this.texture = texture;
	}
	@Override
	public void render(){
		texture.bind();
		super.render();
		Texture.unbind();
	}
	public void dispose(){
		texture.dispose();
	}
}
