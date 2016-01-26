package build.games.wraithaven;

import java.io.File;

public class Algorithms{
	public static File getAsset(String name){
		File file = new File(WorldBuilder.assetFolder+File.separatorChar+name);
		if(!file.exists()){
			if(file.getName().contains("."))
				file.getParentFile().mkdirs();
			else
				file.mkdirs();
		}
		return file;
	}
	public static File getFile(String... path){
		if(path.length==0)
			throw new RuntimeException();
		StringBuilder sb = new StringBuilder();
		sb.append(WorldBuilder.outputFolder);
		for(String s : path){
			sb.append(File.separatorChar);
			sb.append(s);
		}
		File file = new File(sb.toString());
		if(!file.exists()){
			if(file.getName().contains("."))
				file.getParentFile().mkdirs();
			else
				file.mkdirs();
		}
		return file;
	}
	public static int groupLocation(int x, int w){
		return x>=0?x/w*w:(x-(w-1))/w*w;
	}
}
