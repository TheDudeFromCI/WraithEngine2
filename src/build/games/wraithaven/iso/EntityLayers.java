/*
 * Copyright (C) 2016 TheDudeFromCI This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package build.games.wraithaven.iso;

import build.games.wraithaven.util.Algorithms;
import build.games.wraithaven.util.BinaryFile;
import build.games.wraithaven.util.InputAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author TheDudeFromCI
 */
public class EntityLayers extends JPanel{
	private static final int LAYER_WIDTH = 256;
	private static final int LAYER_HEIGHT = 32;
	private static final int EYE_ICON_SIZE = 32;
	private static final int EYE_ICON_IMAGE_SIZE = 24;
	private static final int EYE_POS = (EYE_ICON_SIZE-EYE_ICON_IMAGE_SIZE)/2;
	private final ArrayList<Layer> layers = new ArrayList(8);
	private final Font font = new Font("Tahoma", Font.PLAIN, 15);
	private final ChipsetList chipsetList;
	private BufferedImage eyeOpen;
	private BufferedImage eyeClosed;
	private String uuid;
	private Layer selectedLayer;
	public EntityLayers(ChipsetList chipsetList){
		this.chipsetList = chipsetList;
		try{
			eyeOpen = ImageIO.read(Algorithms.getAsset("Eye Open.png"));
			eyeClosed = ImageIO.read(Algorithms.getAsset("Eye Closed.png"));
		}catch(Exception exception){
			exception.printStackTrace();
		}
		updatePreferedSize();
		InputAdapter ia = new InputAdapter(){
			@Override
			public void mouseClicked(MouseEvent event){
				int x = event.getX();
				int y = event.getY();
				int h = 0;
				for(Layer layer : layers){
					if(x>=EYE_POS&&x<EYE_POS+EYE_ICON_IMAGE_SIZE&&y>=h+EYE_POS&&y<h+EYE_POS+EYE_ICON_IMAGE_SIZE){
						layer.setVisible(!layer.isVisible());
						save();
						repaint();
						return;
					}else if(y>=h&&y<h+LAYER_HEIGHT){
						selectedLayer = layer;
						repaint();
						return;
					}
					h += LAYER_HEIGHT;
				}
			}
		};
		addMouseListener(ia);
	}
	public void loadMap(String uuid){
		this.uuid = uuid;
		chipsetList.updateLayerIcons();
		layers.clear();
		if(uuid==null){
			selectedLayer = null;
			repaint();
			return;
		}
		File file = Algorithms.getFile("Worlds", "Layers", uuid+".dat");
		if(!file.exists()){
			selectedLayer = new Layer("Layer 1");
			layers.add(selectedLayer);
			save();
			updatePreferedSize();
			repaint();
			return;
		}
		BinaryFile bin = new BinaryFile(file);
		bin.decompress(false);
		int layerCount = bin.getInt();
		for(int i = 0; i<layerCount; i++){
			layers.add(new Layer(bin));
		}
		if(layers.isEmpty()){
			selectedLayer = null;
		}else{
			selectedLayer = layers.get(0);
		}
		updatePreferedSize();
		repaint();
	}
	public int getLayerCount(){
		return layers.size();
	}
	public boolean isLoaded(){
		return uuid!=null;
	}
	private boolean needsSaving(){
		for(Layer layer : layers){
			if(layer.needsSaving()){
				return true;
			}
		}
		return false;
	}
	public void addLayer(Layer layer){
		layers.add(layer);
		if(selectedLayer==null){
			selectedLayer = layer;
		}
		save(true);
		updatePreferedSize();
		repaint();
	}
	public void removeLayer(Layer layer){
		layers.remove(layer);
		if(selectedLayer==layer){
			if(layers.isEmpty()){
				selectedLayer = null;
			}else{
				selectedLayer = layers.get(0);
			}
		}
		save(true);
		updatePreferedSize();
		save();
	}
	public void save(){
		save(false);
	}
	private void save(boolean force){
		if(!force&&!needsSaving()){
			return;
		}
		if(uuid==null){
			return;
		}
		BinaryFile bin = new BinaryFile(4);
		bin.addInt(layers.size());
		for(Layer layer : layers){
			layer.save(bin);
		}
		bin.compress(false);
		bin.compile(Algorithms.getFile("Worlds", "Layers", uuid+".dat"));
	}
	private void updatePreferedSize(){
		setPreferredSize(new Dimension(LAYER_WIDTH, Math.max(layers.size()*LAYER_HEIGHT, 10)));
	}
	@Override
	public void paintComponent(Graphics g1){
		Graphics2D g = (Graphics2D)g1;
		g.setColor(Color.lightGray);
		int width = getWidth();
		int height = getHeight();
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r;
		int y = 0;
		for(Layer layer : layers){
			g.setColor(layer==selectedLayer?Color.blue:Color.white);
			g.fillRect(0, y, width, LAYER_HEIGHT);
			g.setColor(Color.black);
			g.drawRect(0, y, width, LAYER_HEIGHT);
			r = fm.getStringBounds(layer.getName(), g);
			g.drawString(layer.getName(), EYE_ICON_SIZE+5, (LAYER_HEIGHT-(float)r.getHeight())/2f+fm.getAscent()+y);
			g.drawImage(layer.isVisible()?eyeOpen:eyeClosed, EYE_POS, y+EYE_POS, null);
			g.drawRect(EYE_POS, EYE_POS+y, EYE_ICON_IMAGE_SIZE, EYE_ICON_IMAGE_SIZE);
			y += LAYER_HEIGHT;
		}
		g.dispose();
	}
	public Layer getSelectedLayer(){
		return selectedLayer;
	}
	public Layer getType(String uuid){
		for(Layer layer : layers){
			if(layer.getUUID().equals(uuid)){
				return layer;
			}
		}
		return null;
	}
}
