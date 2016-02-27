/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import org.lwjgl.opengl.GL11;

/**
 * @author thedudefromci
 */
public class UniverseFlags{
	public static void initalize(){
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	private boolean depthTest;
	private boolean cullFace;
	private boolean texture2D;
	private boolean blending;
	private boolean wireframe;
	private boolean isBound = false;
	public void bind(){
		isBound = true;
		if(depthTest){
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		if(cullFace){
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		if(texture2D){
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		if(blending){
			GL11.glEnable(GL11.GL_BLEND);
		}
		if(wireframe){
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}
	}
	public boolean isBlending(){
		return blending;
	}
	public boolean isBound(){
		return isBound;
	}
	public boolean isCullFace(){
		return cullFace;
	}
	public boolean isDepthTesting(){
		return depthTest;
	}
	public boolean isTexture2D(){
		return texture2D;
	}
	public boolean isWireframe(){
		return wireframe;
	}
	public void setBlending(boolean blending){
		if(isBound){
			throw new RuntimeException();
		}
		this.blending = blending;
	}
	public void setCullFace(boolean cullFace){
		if(isBound){
			throw new RuntimeException();
		}
		this.cullFace = cullFace;
	}
	public void setDepthTest(boolean depthTest){
		if(isBound){
			throw new RuntimeException();
		}
		this.depthTest = depthTest;
	}
	public void setTexture2D(boolean texture2D){
		if(isBound){
			throw new RuntimeException();
		}
		this.texture2D = texture2D;
	}
	public void setWireframe(boolean wireframe){
		if(isBound){
			throw new RuntimeException();
		}
		this.wireframe = wireframe;
	}
	public void unbind(){
		if(depthTest){
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
		if(cullFace){
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
		if(texture2D){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		if(blending){
			GL11.glDisable(GL11.GL_BLEND);
		}
		if(wireframe){
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		isBound = false;
	}
}
