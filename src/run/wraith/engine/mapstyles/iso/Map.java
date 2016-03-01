/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso;

import java.io.File;
import java.util.Comparator;
import run.wraith.engine.opengl.renders.Camera;
import run.wraith.engine.opengl.renders.ModelInstance;
import run.wraith.engine.opengl.renders.ShaderProgram;
import run.wraith.engine.opengl.renders.Universe;
import run.wraith.engine.opengl.utils.RenderIndex;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Map{
	private final int width;
	private final int height;
	private final TileInstance[] tiles;
	private final String uuid;
	private final String name;
	private final Universe universe;
	private final Camera camera;
	private final ShaderProgram shader;
	public Map(String uuid){
		this.uuid = uuid;
		try{
			{
				// Initalize scene.
				universe = new Universe();
				camera = new Camera();
				camera.setOrthographic(0, 15, 15, 0, -1, 1);
				universe.setCamera(camera);
				File vertexShader = new File("/home/thedudefromci/Documents/Vertex.txt");
				File fragmentShader = new File("/home/thedudefromci/Documents/Fragment.txt");
				shader = new ShaderProgram(vertexShader, null, fragmentShader);
				shader.loadUniforms("projectionMatrix", "viewMatrix", "modelMatrix");
				universe.setShader(shader, 0, 1, 2);
				universe.getFlags().setTexture2D(true);
				universe.getFlags().setBlending(true);
			}
			{
				// Load properties.
				File file = Algorithms.getFile("Worlds", uuid+".dat");
				if(!file.exists()){
					// Some noob has deleted his project files. *sigh*
					System.err.println(file.getAbsolutePath());
					throw new RuntimeException("Map file not found!");
				}
				BinaryFile bin = new BinaryFile(file);
				bin.decompress(false);
				short version = bin.getShort();
				switch(version){
					case 0:
						try{
							width = bin.getInt();
							height = bin.getInt();
							name = bin.getString();
							tiles = new TileInstance[width*height];
						}catch(Exception exception){
							exception.printStackTrace();
							throw new RuntimeException("Error loading map file!");
						}
						break;
					default:
						throw new RuntimeException("Error loading map file!");
				}
			}
			{
				// Load Tiles
				File file = Algorithms.getFile("Worlds", "Tiles", uuid+".dat");
				if(!file.exists()){
					// Some noob has deleted his project files. *sigh*
					System.err.println(file.getAbsolutePath());
					throw new RuntimeException("Map file not found!");
				}
				BinaryFile bin = new BinaryFile(file);
				bin.decompress(true);
				short version = bin.getShort();
				switch(version){
					case 0:
						try{
							Tile[] references = new Tile[bin.getInt()];
							String layer, cat, id;
							int index, y, entityCount, i, j;
							int u, v, r;
							float s, t;
							TileModelInstance mod;
							EntityModelInstance mod2;
							Entity entity;
							EntityList entityList = new EntityList();
							for(i = 0; i<references.length; i++){
								cat = bin.getString();
								id = bin.getString();
								references[i] = new Tile(cat, id);
							}
							for(i = 0; i<tiles.length; i++){
								index = bin.getInt();
								if(index==-1){
									bin.skip(8);
									continue;
								}
								y = bin.getInt();
								u = i%width; // X
								v = i/width; // Y
								s = (u-v)*0.5f;
								t = (u+v)*0.25f-y*0.125f;
								r = Math.max(u, v);
								tiles[i] = new TileInstance(references[index], y);
								mod = new TileModelInstance(references[index].getModel());
								mod.getPosition().translate(s, t, 0);
								mod.setRenderIndex(r);
								universe.addModel(mod);
								entityCount = bin.getInt();
								for(j = 0; j<entityCount; j++){
									layer = bin.getString();
									cat = bin.getString();
									id = bin.getString();
									entity = entityList.getEntity(cat, id, layer);
									mod2 = new EntityModelInstance(entity.getModel());
									mod2.getPosition().translate(s, t+(entity.getHeight()>=0?(1-entity.getHeight()):0), 0);
									mod2.setRenderIndex(r+0.5);
									universe.addModel(mod2);
								}
							}
							universe.sortModels(new Comparator<ModelInstance>(){
								@Override
								public int compare(ModelInstance o1, ModelInstance o2){
									RenderIndex a = (RenderIndex)o1;
									RenderIndex b = (RenderIndex)o2;
									return Double.compare(a.getRenderIndex(), b.getRenderIndex());
								}
							});
						}catch(Exception exception){
							exception.printStackTrace();
							throw new RuntimeException("Error loading map file!");
						}
						break;
					default:
						throw new RuntimeException("Error loading map file!");
				}
			}
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
			throw exception; // This last line will never be called. Just shut up the compiler.
		}
	}
	public void render(){
		universe.render();
	}
	public void update(double delta, double time){
		universe.update(delta, time);
	}
	public void dispose(){
		universe.dispose();
	}
}
