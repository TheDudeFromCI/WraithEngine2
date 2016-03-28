/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.code.languages;

import build.games.wraithaven.code.LanguageLoader;
import build.games.wraithaven.code.Snipet;
import java.awt.Color;
import java.awt.Graphics2D;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class WraithScript implements LanguageLoader{
	private static final int ID = 0;
	@Override
	public void draw(Graphics2D g, int width, int height, Snipet script){
		g.setColor(Color.red);
		g.fillRect(0, 0, width, height);
	}
	@Override
	public void save(BinaryFile bin){}
	@Override
	public void load(BinaryFile bin, short version){}
	@Override
	public int getId(){
		return ID;
	}
}
