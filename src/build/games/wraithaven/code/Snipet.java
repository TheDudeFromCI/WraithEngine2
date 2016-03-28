/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

/**
 * @author thedudefromci
 */
public class Snipet{
	private final String uuid;
	private String name;
	private LanguageLoader language;
	public Snipet(String uuid){
		this.uuid = uuid;
	}
	public String getUuid(){
		return uuid;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	@Override
	public String toString(){
		return name==null?"menu:"+uuid:name;
	}
	public void load(){
		// TODO
	}
	public void save(){
		// TODO
	}
	public void dispose(){
		// TODO
	}
	public LanguageLoader getLanguage(){
		return language;
	}
	public void setLanguage(LanguageLoader language){
		this.language = language;
	}
}
