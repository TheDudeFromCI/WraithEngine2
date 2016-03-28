/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code;

import build.games.wraithaven.code.languages.WraithScript;
import java.io.File;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Snipet{
	private static final short FILE_VERSION = 0;
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
		System.out.printf("Loaded script '%s'\n", uuid);
		File file = Algorithms.getFile("Scripts", uuid+".dat");
		if(!file.exists()){
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(true);
		short version = bin.getShort();
		switch(version){
			case 0:{
				name = bin.getString();
				int languageId = bin.getInt();
				switch(languageId){
					case -1:
						// No script.
						break;
					case 0:
						language = new WraithScript();
						break;
					default:
						throw new RuntimeException("Unknown language! '"+languageId+"'");
				}
				if(language!=null){
					language.load(bin, version);
				}
				break;
			}
			default:
				throw new RuntimeException("Unknown file version! '"+version+"'");
		}
	}
	public void save(){
		BinaryFile bin = new BinaryFile(2+4);
		bin.addShort(FILE_VERSION);
		bin.addStringAllocated(name);
		bin.addInt(language==null?-1:language.getId());
		if(language!=null){
			language.save(bin);
		}
		bin.compress(true);
		bin.compile(Algorithms.getFile("Scripts", uuid+".dat"));
	}
	public void dispose(){
		language = null;
	}
	public LanguageLoader getLanguage(){
		return language;
	}
	public void setLanguage(LanguageLoader language){
		this.language = language;
	}
}
