/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.core;

import build.games.wraithaven.iso.IsoMapStyle;
import build.games.wraithaven.topdown.TopDownMapStyle;

/**
 * @author TheDudeFromCI
 */
public class MapStyleFactory{
	public static MapStyle loadMapStyle(int id){
		switch(id){
			case 0:
				return new TopDownMapStyle();
			case 1:
				return new IsoMapStyle();
			default:
				throw new RuntimeException();
		}
	}
	public static int getId(MapStyle mapStyle){
		if(mapStyle instanceof TopDownMapStyle){
			return 0;
		}
		if(mapStyle instanceof IsoMapStyle){
			return 1;
		}
		throw new RuntimeException();
	}
}
