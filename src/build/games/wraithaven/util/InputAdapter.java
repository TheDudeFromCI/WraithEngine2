/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputAdapter implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{
	@Override
	public void keyPressed(KeyEvent event){}
	@Override
	public void keyReleased(KeyEvent event){}
	@Override
	public void keyTyped(KeyEvent event){}
	@Override
	public void mouseClicked(MouseEvent event){}
	@Override
	public void mouseDragged(MouseEvent event){}
	@Override
	public void mouseEntered(MouseEvent event){}
	@Override
	public void mouseExited(MouseEvent event){}
	@Override
	public void mouseMoved(MouseEvent event){}
	@Override
	public void mousePressed(MouseEvent event){}
	@Override
	public void mouseReleased(MouseEvent event){}
	@Override
	public void mouseWheelMoved(MouseWheelEvent event){}
}
