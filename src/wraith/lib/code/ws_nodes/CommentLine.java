/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.code.ws_nodes;

import build.games.wraithaven.gui.MenuComponentDialog;
import build.games.wraithaven.util.VerticalFlowLayout;
import javax.swing.JComponent;
import javax.swing.JTextField;
import wraith.lib.code.WSNode;
import wraith.lib.util.BinaryFile;

/**
 * @author thedudefromci
 */
public class CommentLine implements WSNode{
	private static final int ID = 0;
	private String comment = "";
	@Override
	public void save(BinaryFile bin){
		bin.addStringAllocated(comment);
	}
	@Override
	public void load(BinaryFile bin, short version){
		comment = bin.getString();
	}
	@Override
	public int getId(){
		return ID;
	}
	public String getComment(){
		return comment;
	}
	public void setComment(String comment){
		this.comment = comment;
	}
	@Override
	public MenuComponentDialog getCreationDialog(){
		return new MenuComponentDialog(){
			private final JTextField text;
			{
				setLayout(new VerticalFlowLayout(5));
				text = new JTextField();
				text.setColumns(20);
				text.setText(comment);
				add(text);
			}
			@Override
			public void build(Object component){
				CommentLine c = (CommentLine)component;
				c.comment = text.getText();
			}
			@Override
			public JComponent getDefaultFocus(){
				return text;
			}
		};
	}
	@Override
	public void run(){}
	@Override
	public String getHtml(int in){
		String indent;
		if(in>=0){
			StringBuilder sb = new StringBuilder(in);
			for(int i = 0; i<in; i++){
				sb.append(' ');
			}
			indent = sb.toString();
		}else{
			indent = "";
		}
		return "<html><pre><font face=\"Courier\" size=\"3\" color=green>"+indent+"# "+comment+"</font></pre></html>";
	}
}
