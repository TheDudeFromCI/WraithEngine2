/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.gui;

/**
 * @author thedudefromci
 */
public class Menu{
	private Theme theme;
	private String name;
	public Theme getTheme(){
		return theme;
	}
	public void setTheme(Theme theme){
		this.theme = theme;
	}
	public void dispose(){
		// This may not even be needed, but just in case.
		// TODO
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return name;
	}
}
