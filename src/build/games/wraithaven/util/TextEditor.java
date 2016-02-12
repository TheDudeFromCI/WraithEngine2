/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * @author TheDudeFromCI
 */
public class TextEditor{
	private final StringBuilder sb;
	private final InputAdapter inputAdapter;
	private Component component;
	private Runnable onEnterScript;
	private int cursor;
	private boolean cursorShown = true;
	public TextEditor(String base){
		sb = new StringBuilder(base);
		cursor = base.length();
		inputAdapter = new InputAdapter(){
			@Override
			public void keyTyped(KeyEvent event){
				char c = event.getKeyChar();
				// Make sure we know what they are typing, instead of allowing random unicode characters.
				if(Character.isAlphabetic(c)||Character.isDigit(c)||c==' '||c=='\''||c==','){
					sb.insert(cursor, c);
					cursor++;
					repaint();
				}
			}
			@Override
			public void keyPressed(KeyEvent event){
				int code = event.getKeyCode();
				switch(code){
					case KeyEvent.VK_BACK_SPACE:
						if(cursor>0){
							cursor--;
							sb.deleteCharAt(cursor);
							repaint();
						}
						break;
					case KeyEvent.VK_ENTER:
						end();
						break;
					case KeyEvent.VK_DELETE:
						if(cursor<sb.length()-1){
							sb.deleteCharAt(cursor);
							repaint();
						}
						break;
					case KeyEvent.VK_LEFT:
						if(cursor>0){
							cursor--;
							repaint();
						}
						break;
					case KeyEvent.VK_RIGHT:
						if(cursor<sb.length()-1){
							cursor++;
							repaint();
						}
						break;
				}
			}
		};
	}
	public void end(){
		if(onEnterScript!=null){
			onEnterScript.run();
		}
	}
	private void repaint(){
		if(component!=null){
			component.repaint();
		}
	}
	public void setRepaintComponent(Component component){
		this.component = component;
	}
	public InputAdapter getInputAdapter(){
		return inputAdapter;
	}
	public void setOnEnterScript(Runnable run){
		onEnterScript = run;
	}
	public boolean isCursorShown(){
		return cursorShown;
	}
	public void setCursorShown(boolean shown){
		cursorShown = shown;
		repaint();
	}
	public void draw(Graphics g, int x, int y){
		g.drawString(sb.toString(), x, y);
		if(!cursorShown){
			return;
		}
		FontMetrics fm = g.getFontMetrics();
		x += fm.stringWidth(sb.substring(0, cursor))+fm.stringWidth("|")/2;
		g.drawString("|", x, y);
	}
	public String getText(){
		return sb.toString();
	}
}
