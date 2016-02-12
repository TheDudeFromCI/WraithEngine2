/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

/**
 * @author TheDudeFromCI
 */
public class EntityType{
	private final String uuid;
	private final int width;
	private final int height;
	private final float offX;
	private final float offY;
	public EntityType(String uuid, int width, int height){
		this.uuid = uuid;
		this.width = width;
		this.height = height;
		offX = width/-2f+0.5f;
		offY = -1f;
	}
	public String getUUID(){
		return uuid;
	}
	public float getOffsetX(){
		return offX;
	}
	public float getOffsetY(){
		return offY;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
}
