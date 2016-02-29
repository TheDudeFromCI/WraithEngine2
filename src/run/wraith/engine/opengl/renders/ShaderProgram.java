/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.opengl.renders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

/**
 * @author thedudefromci
 */
public class ShaderProgram{
	private static String readFile(File file){
		try{
			StringBuilder sb;
			try(BufferedReader in = new BufferedReader(new FileReader(file))){
				sb = new StringBuilder(128);
				String s;
				while((s = in.readLine())!=null){
					sb.append(s);
					sb.append('\n');
				}
			}
			return sb.toString();
		}catch(Exception exception){
			exception.printStackTrace();
			throw new RuntimeException("Could not read shader file!");
		}
	}
	private final int program;
	private int[] uniforms;
	private int[] attributes;
	public ShaderProgram(File vertexShader, File geometryShader, File fragmentShader){
		this(ShaderProgram.readFile(vertexShader), geometryShader==null?null:ShaderProgram.readFile(geometryShader),
			ShaderProgram.readFile(fragmentShader));
	}
	public ShaderProgram(String folder, String name){
		this(new File(folder, name+".vert"), null, new File(folder, name+".frag"));
	}
	public ShaderProgram(String vertexShader, String geometryShader, String fragmentShader){
		// Vertex Shader
		int vs = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vs, vertexShader);
		GL20.glCompileShader(vs);
		int vsStatus = GL20.glGetShaderi(vs, GL20.GL_COMPILE_STATUS);
		if(vsStatus!=GL11.GL_TRUE){
			throw new RuntimeException(GL20.glGetShaderInfoLog(vs, 1000));
		}
		// Geometry Shader
		int gs;
		if(geometryShader!=null){
			gs = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
			GL20.glShaderSource(gs, geometryShader);
			GL20.glCompileShader(gs);
			int gsStatus = GL20.glGetShaderi(gs, GL20.GL_COMPILE_STATUS);
			if(gsStatus!=GL11.GL_TRUE){
				throw new RuntimeException(GL20.glGetShaderInfoLog(gs, 1000));
			}
		}else{
			gs = -1;
		}
		// Fragment Shader
		int fs = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fs, fragmentShader);
		GL20.glCompileShader(fs);
		int fsStatus = GL20.glGetShaderi(fs, GL20.GL_COMPILE_STATUS);
		if(fsStatus!=GL11.GL_TRUE){
			throw new RuntimeException(GL20.glGetShaderInfoLog(fs, 1000));
		}
		// Compile Program
		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vs);
		GL20.glAttachShader(program, fs);
		if(geometryShader!=null){
			GL20.glAttachShader(program, gs);
		}
		GL20.glLinkProgram(program);
		GL20.glValidateProgram(program);
		int pStatus = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
		if(pStatus!=GL11.GL_TRUE){
			throw new RuntimeException(GL20.glGetProgramInfoLog(program, 1000));
		}
		GL20.glDetachShader(program, vs);
		if(gs!=-1){
			GL20.glDetachShader(program, gs);
		}
		GL20.glDetachShader(program, fs);
		GL20.glDeleteShader(vs);
		if(gs!=-1){
			GL20.glDeleteShader(gs);
		}
		GL20.glDeleteShader(fs);
	}
	public void bind(){
		GL20.glUseProgram(program);
		if(attributes!=null){
			for(int i : attributes){
				GL20.glEnableVertexAttribArray(i);
			}
		}
	}
	public void dispose(){
		GL20.glDeleteProgram(program);
	}
	public int getAttributeLocation(int index){
		return attributes[index];
	}
	public int getId(){
		return program;
	}
	public int[] getUniformLocations(){
		return uniforms;
	}
	public void loadAttributes(String... att){
		GL20.glUseProgram(program);
		attributes = new int[att.length];
		int i = 0;
		for(String s : att){
			attributes[i] = GL20.glGetAttribLocation(program, s);
			i++;
		}
		GL20.glUseProgram(0);
	}
	public void loadUniforms(String... uni){
		uniforms = new int[uni.length];
		int i = 0;
		for(String s : uni){
			uniforms[i] = GL20.glGetUniformLocation(program, s);
			i++;
		}
	}
	public void setUniform1f(int index, float v1){
		GL20.glUniform1f(uniforms[index], v1);
	}
	public void setUniform1I(int index, int value){
		GL20.glUniform1i(uniforms[index], value);
	}
	public void setUniform2f(int index, float v1, float v2){
		GL20.glUniform2f(uniforms[index], v1, v2);
	}
	public void setUniform3f(int index, float v1, float v2, float v3){
		GL20.glUniform3f(uniforms[index], v1, v2, v3);
	}
	public void setUniform4f(int index, float v1, float v2, float v3, float v4){
		GL20.glUniform4f(uniforms[index], v1, v2, v3, v4);
	}
	public void setUniformMat4(int index, FloatBuffer mat){
		GL20.glUniformMatrix4fv(uniforms[index], false, mat);
	}
	public void unbind(){
		GL20.glUseProgram(0);
		if(attributes!=null){
			for(int i : attributes){
				GL20.glDisableVertexAttribArray(i);
			}
		}
	}
}
