/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/**
 * @author thedudefromci
 */
public class Universe{
	private final FloatBuffer buffer;
	private final ArrayList<ModelInstance> models = new ArrayList(16);
	private final UniverseFlags flags;
	private ShaderProgram shader;
	private Camera camera;
	private int projectionLocation;
	private int viewLocation;
	private int modelLocation;
	public Universe(){
		buffer = BufferUtils.createFloatBuffer(16);
		flags = new UniverseFlags();
	}
	public void addModel(ModelInstance model){
		models.add(model);
	}
	public void dispose(){
		for(ModelInstance model : models){
			model.getModel().dispose();
		}
		models.clear();
		shader = null;
		camera = null;
	}
	public UniverseFlags getFlags(){
		return flags;
	}
	public void removeModel(Model model){
		models.remove(model);
	}
	public void render(){
		if(shader==null){
			return;
		}
		if(models.isEmpty()){
			return;
		}
		if(camera==null){
			return;
		}
		shader.bind();
		flags.bind();
		camera.dumpToShader(viewLocation, projectionLocation);
		for(ModelInstance model : models){
			model.getPosition().get(buffer);
			GL20.glUniformMatrix4fv(modelLocation, false, buffer);
			model.render();
		}
		flags.unbind();
		shader.unbind();
	}
	public void setCamera(Camera camera){
		this.camera = camera;
	}
	public void setShader(ShaderProgram shader, int projectionLocation, int viewLocation, int modelLocation){
		this.shader = shader;
		this.projectionLocation = shader.getUniformLocations()[projectionLocation];
		this.viewLocation = shader.getUniformLocations()[viewLocation];
		this.modelLocation = shader.getUniformLocations()[modelLocation];
	}
	public void update(double delta, double time){
		// TODO Add better support for adding/removing models during an update.
		for(ModelInstance model : models){
			model.update(delta, time);
		}
	}
	public void sortModels(Comparator<ModelInstance> compare){
		models.sort(compare);
	}
}
