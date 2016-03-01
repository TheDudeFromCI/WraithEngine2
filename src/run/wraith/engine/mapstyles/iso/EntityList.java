/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package run.wraith.engine.mapstyles.iso;

import java.util.HashMap;

/**
 * @author thedudefromci
 */
public class EntityList{
	private static class EntityId{
		private final String cat;
		private final String uuid;
		private EntityId(String cat, String uuid){
			this.cat = cat;
			this.uuid = uuid;
		}
	}
	private final HashMap<EntityId,Entity> entities = new HashMap(16);
	public Entity getEntity(String cat, String id, String layer){
		for(EntityId e : entities.keySet()){
			if(e.cat.equals(cat)&&e.uuid.equals(layer)){
				return entities.get(e);
			}
		}
		EntityId e = new EntityId(cat, id);
		Entity entity = new Entity(cat, id, layer);
		entities.put(e, entity);
		return entity;
	}
}
