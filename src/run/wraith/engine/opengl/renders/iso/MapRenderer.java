/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders.iso;

import java.io.File;
import org.lwjgl.opengl.GL11;
import run.wraith.engine.mapstyles.iso.Map;
import run.wraith.engine.opengl.loop.RenderLoop;
import run.wraith.engine.opengl.renders.Camera;
import run.wraith.engine.opengl.renders.Model;
import run.wraith.engine.opengl.renders.ShaderProgram;
import run.wraith.engine.opengl.renders.Texture;
import run.wraith.engine.opengl.renders.Universe;
import run.wraith.engine.opengl.renders.UniverseFlags;
import run.wraith.engine.opengl.renders.VAO;
import run.wraith.engine.opengl.utils.PrimitiveGenerator;
import run.wraith.engine.opengl.utils.VertexBuildData;

/**
 * @author thedudefromci
 */
public class MapRenderer implements RenderLoop{
	private Universe universe;
	private Map map;
	private Texture texture;
	private ShaderProgram shader;
	public void setMap(Map map){
		this.map = map;
	}
	public void initalize(){
		GL11.glClearColor(0, 0, 0, 1);
		UniverseFlags.initalize();
		universe = new Universe();
		{
			// TEST
			VertexBuildData buildData = PrimitiveGenerator.generateBox(0.5f, 0.5f, 0.5f, PrimitiveGenerator.ALL);
			VAO vao = PrimitiveGenerator.convertToVAO(buildData);
			Model model = new Model(vao);
			universe.addModel(model);
			Camera camera = new Camera();
			camera.setPerspective(70, 4/3f, 0.1f, 100.0f);
			camera.moveTo(0, 1, 3);
			camera.lookAt(0, 0, 0);
			universe.setCamera(camera);
			File vertexShader = new File("/home/thedudefromci/Documents/Vertex.txt");
			File fragmentShader = new File("/home/thedudefromci/Documents/Fragment.txt");
			shader = new ShaderProgram(vertexShader, null, fragmentShader);
			shader.loadUniforms("projectionMatrix", "viewMatrix", "modelMatrix");
			universe.setShader(shader, 0, 1, 2);
			universe.getFlags().setDepthTest(true);
			universe.getFlags().setTexture2D(true);
			universe.getFlags().setCullFace(false);
			universe.getFlags().setWireframe(false);
			File textureFile = new File("/home/thedudefromci/Documents/Texture.png");
			texture = new Texture(textureFile, true);
			texture.bind();
		}
	}
	@Override
	public void render(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		texture.bind();
		universe.render();
		Texture.unbind();
	}
	public void dispose(){
		universe.dispose();
		texture.dispose();
		shader.dispose();
	}
	@Override
	public void update(double delta, double time){}
}
