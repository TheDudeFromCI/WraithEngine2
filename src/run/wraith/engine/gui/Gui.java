/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.gui;

import java.io.File;
import java.util.ArrayList;
import run.wraith.engine.opengl.renders.Camera;
import run.wraith.engine.opengl.renders.Model;
import run.wraith.engine.opengl.renders.ShaderProgram;
import run.wraith.engine.opengl.renders.Universe;
import run.wraith.engine.opengl.renders.VAO;
import run.wraith.engine.opengl.utils.PrimitiveGenerator;
import run.wraith.engine.opengl.utils.PrimitiveGenerator.PrimitiveFlags;
import run.wraith.engine.opengl.utils.VertexBuildData;
import wraith.lib.util.Algorithms;

/**
 * @author thedudefromci
 */
public class Gui{
	private final ArrayList<Menu> activeMenus = new ArrayList(4);
	private final Universe universe;
	private final Camera camera;
	private final ShaderProgram shader;
	private final Model model;
	private int width;
	private int height;
	public Gui(){
		width = 800;
		height = 600;
		camera = new Camera();
		camera.setOrthographic(0, width, height, 0, -1, 1);
		universe = new Universe();
		universe.setCamera(camera);
		File vertexShader = Algorithms.getAsset("Vertex.txt");
		File fragmentShader = Algorithms.getAsset("Fragment.txt");
		shader = new ShaderProgram(vertexShader, null, fragmentShader);
		shader.loadUniforms("projection", "view", "model", "diffuse");
		shader.bind();
		shader.setUniform1I(3, 0);
		shader.unbind();
		universe.setShader(shader, 0, 1, 2);
		universe.getFlags().setTexture2D(true);
		universe.getFlags().setBlending(true);
		{
			// Generate model base.
			PrimitiveFlags flags = new PrimitiveFlags(true, true);
			VertexBuildData build = PrimitiveGenerator.generateSquare(1, 1, flags);
			VAO vao = PrimitiveGenerator.convertToVAO(build, flags);
			model = new Model(vao);
		}
	}
	public void dispose(){
		for(Menu menu : activeMenus){
			menu.dispose();
		}
		activeMenus.clear();
		universe.dispose();
		shader.dispose();
		model.dispose();
	}
	public void render(){
		universe.render();
	}
	public void loadMenu(String uuid){
		Menu menu = new Menu(this, uuid);
		activeMenus.add(menu);
		menu.addAllComponents(universe);
	}
	public void unloadMenu(String uuid){
		Menu menu = null;
		for(Menu m : activeMenus){
			if(m.getUUID().equals(uuid)){
				menu = m;
				break;
			}
		}
		if(menu==null){
			return;
		}
		activeMenus.remove(menu);
		menu.removeAllComponents(universe);
	}
	public Model getModel(){
		return model;
	}
	public void updateAllMenuLayouts(){
		for(Menu m : activeMenus){
			m.updateComponentLayouts();
		}
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public void updateWindowSize(int width, int height){
		this.width = width;
		this.height = height;
		camera.setOrthographic(0, width, height, 0, -1, 1);
		updateAllMenuLayouts();
	}
}
