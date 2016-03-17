/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.util;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * @author thedudefromci
 */
public class InputDialog{
	public static final int EXIT = 0;
	public static final int OK = 1;
	public static final int CANCEL = 2;
	public static final int YES = 3;
	public static final int NO = 4;
	private boolean ok;
	private boolean cancel;
	private boolean yes;
	private boolean no;
	private JComponent data;
	private JComponent focus;
	private String title;
	private int response;
	public void setDefaultFocus(JComponent focus){
		this.focus = focus;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public void setData(JComponent data){
		this.data = data;
	}
	public void setOkButton(boolean show){
		ok = show;
	}
	public void setCancelButton(boolean show){
		cancel = show;
	}
	public void setYesButton(boolean show){
		yes = show;
	}
	public void setNoButton(boolean show){
		no = show;
	}
	public void show(){
		Object[] options;
		{
			// Generate options
			// Count
			int d = 0;
			if(ok){
				d++;
			}
			if(cancel){
				d++;
			}
			if(yes){
				d++;
			}
			if(no){
				d++;
			}
			options = new Object[d];
			// Set
			int c = 0;
			if(ok){
				options[c++] = "Ok";
			}
			if(cancel){
				options[c++] = "Cancel";
			}
			if(yes){
				options[c++] = "Yes";
			}
			if(no){
				options[c++] = "No";
			}
		}
		int r = JOptionPane.showOptionDialog(null, data, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, focus);
		{
			if(r==-1){
				response = EXIT;
				return;
			}
			// Assign response
			String n = (String)options[r];
			switch(n){
				case "Ok":
					response = OK;
					break;
				case "Cancel":
					response = CANCEL;
					break;
				case "Yes":
					response = YES;
					break;
				case "NO":
					response = NO;
					break;
				default:
					response = EXIT;
					break;
			}
		}
	}
	public int getResponse(){
		return response;
	}
}
