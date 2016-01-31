/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

/**
 * @author TheDudeFromCI
 */
public class ProjectConstraints{
	private final String name;
	private final String uuid;
	private final int type;
	public ProjectConstraints(String name, String uuid, int type){
		this.name = name;
		this.uuid = uuid;
		this.type = type;
	}
	public String getName(){
		return name;
	}
	public String getUUID(){
		return uuid;
	}
	public int getType(){
		return type;
	}
	@Override
	public String toString(){
		return name;
	}
}
