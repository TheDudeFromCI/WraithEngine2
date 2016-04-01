/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code;

/**
 * @author thedudefromci
 */
public class FunctionUtils{
	public static String generateHtml(String original, int in){
		String indent;
		boolean enabled;
		if(in>=0){
			StringBuilder sb = new StringBuilder(in);
			for(int i = 0; i<in; i++){
				sb.append(' ');
			}
			indent = sb.toString();
			enabled = true;
		}else{
			indent = "";
			enabled = false;
		}
		StringBuilder sb = new StringBuilder(16);
		sb.append("<html>");
		sb.append("<pre>");
		sb.append(coloredString(original, enabled?"black":"gray", enabled));
		sb.append("</pre>");
		sb.append("</html>");
		return sb.toString();
	}
	private static String coloredString(String text, String color, boolean renderColor){
		String all = "";
		if(renderColor){
			String s = "";
			int mode = 0;
			for(char c : text.toCharArray()){
				switch(mode){
					case 0:{
						switch(c){
							case '"':
								all += coloredString(s, "black", false);
								s = "";
								mode = 2;
								break;
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								all += coloredString(s, "black", false);
								s = "";
								s += c;
								mode = 3;
								break;
							case '$':
								all += coloredString(s, "black", false);
								s = "";
								s += c;
								mode = 1;
								break;
							default:
								s += c;
						}
						break;
					}
					case 1:{
						if(Character.isAlphabetic(c)||Character.isDigit(c)){
							s += c;
						}else{
							all += coloredString(s, "purple", false);
							s = "";
							s += c;
							mode = 0;
						}
						break;
					}
					case 2:{
						switch(c){
							case '"':
								s += c;
								all += coloredString(s, "blue", false);
								s = "";
								mode = 0;
								break;
							default:
								s += c;
						}
						break;
					}
					case 3:{
						switch(c){
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								s += c;
								break;
							default:
								mode = 0;
								all += coloredString(s, "orange", false);
								s = "";
								s += c;
								break;
						}
						break;
					}
				}
			}
			if(!s.isEmpty()){
				all += coloredString(s, "black", false);
			}
		}else{
			all = text;
		}
		return "<font face=\"Courier\" size=\"3\" color="+color+">"+all+"</font>";
	}
}
