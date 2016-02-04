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
public class Entity{
	private final int x;
	private final int z;
	private final EntityType entityType;
	public Entity(EntityType entityType, int x, int z){
		this.entityType = entityType;
		this.x = x;
		this.z = z;
	}
	public int getX(){
		return x;
	}
	public int getZ(){
		return z;
	}
	public EntityType getEntityType(){
		return entityType;
	}
}
