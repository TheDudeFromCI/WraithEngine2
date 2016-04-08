/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.code;

import java.io.File;
import wraith.lib.code.WraithScript;
import wraith.lib.util.Algorithms;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class Snipet{
	private final CodeLanguage lan;
	public Snipet(String uuid){
		CodeLanguage tempLan = null;
		try{
			File file = Algorithms.getFile("Scripts", uuid+".dat");
			BinaryFile bin = new BinaryFile(file);
			bin.decompress(true);
			short version = bin.getShort();
			switch(version){
				case 0:
					bin.getString(); // Name
					bin.getString(); // Description
					bin.getInt(); // Event Type
					int lanId = bin.getInt();
					switch(lanId){
						case -1:{ // Empty Script. Do nothing.
							tempLan = null;
							break;
						}
						case 0:{ // WrathScript
							tempLan = new WraithScript(null);
							break;
						}
						default:
							throw new RuntimeException("Unknown language! '"+lanId+"'");
					}
					if(tempLan!=null){
						tempLan.load(bin, version);
						tempLan.initalizeRuntime();
					}
					break;
				default:
					throw new RuntimeException("Unknown file version! '"+version+"'");
			}
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
		}
		lan = tempLan;
	}
	public void run(){
		if(lan!=null){
			lan.run();
		}
	}
}
