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
public class CursorSelection{
	private int screenX;
	private int screenY;
	private int tileX;
	private int tileY;
	private Tile selectedTile;
	private int selectedTileIndex;
	private int mapWidth;
	private int mapHeight;
	private boolean onEditor;
	public void setScreenLocation(int x, int y){
		screenX = x;
		screenY = y;
	}
	public void show(){
		onEditor = true;
	}
	public void hide(){
		onEditor = false;
	}
	public boolean isOnEditor(){
		return onEditor;
	}
	public int getScreenX(){
		return screenX;
	}
	public int getScreenY(){
		return screenY;
	}
	public Tile getSelectedTile(){
		return selectedTile;
	}
	public void setSelectedTile(Tile tile, int index){
		selectedTile = tile;
		selectedTileIndex = index;
	}
	public int getSelectedTileIndex(){
		return selectedTileIndex;
	}
	public boolean isActive(){
		return selectedTile!=null;
	}
	public int getTileX(){
		return tileX;
	}
	public int getTileY(){
		return tileY;
	}
	public void setMapSize(int width, int height){
		mapWidth = width;
		mapHeight = height;
	}
	public boolean isOverMap(){
		return tileX>=0&&tileY>=0&&tileX<mapWidth&&tileY<mapHeight;
	}
	public void setTileLocation(int x, int y){
		tileX = x;
		tileY = y;
	}
}
