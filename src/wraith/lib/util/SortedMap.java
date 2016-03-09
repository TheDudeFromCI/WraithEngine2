/*
 * Copyright (C) 2016 thedudefromci This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package wraith.lib.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author thedudefromci
 * @param <K>
 *            - The key
 * @param <V>
 *            - The value
 */
public class SortedMap<K,V> implements Iterable<K>{
	private int size;
	private Object[] k;
	private Object[] v;
	public SortedMap(int initalCapacity){
		k = new Object[initalCapacity];
		v = new Object[initalCapacity];
	}
	public V get(K key){
		if(key==null){
			return null;
		}
		for(int i = 0; i<size; i++){
			if(k[i]==key){
				return (V)v[i];
			}
		}
		return null;
	}
	public void put(K key, V value){
		if(key==null){
			return;
		}
		// Check to see if the key already exists.
		for(int i = 0; i<size; i++){
			if(k[i]==key){
				// It already does exist. Let's set the value, and return.
				v[i] = value;
				return;
			}
		}
		// It doesn't exist. Let's add it.
		if(size==k.length){
			k = Arrays.copyOf(k, k.length+1);
			v = Arrays.copyOf(v, v.length+1);
		}
		k[size] = key;
		v[size] = value;
		size++;
	}
	public void remove(K key){
		if(key==null){
			return;
		}
		for(int i = 0; i<size; i++){
			if(k[i]==key){
				// Move everything after, down 1.
				removeIndex(i);
				return;
			}
		}
	}
	public void removeIndex(int index){
		size--;
		for(int a = index; a<size; a++){
			k[a] = k[a+1];
			v[a] = v[a+1];
		}
	}
	/**
	 * Removes all keys with the requested value.
	 *
	 * @param value
	 *            - The value to remove.
	 */
	public void removeValue(V value){
		main:while(true){
			// Keep iterating over the last, as the value may be contained for several keys.
			// Loop until value no longer found.
			for(int i = 0; i<size; i++){
				if(v[i]==value){
					removeIndex(i);
					continue main;
				}
			}
			// No more values found. Return.
			return;
		}
	}
	/**
	 * Sorts the map by keys.
	 *
	 * @param sorter
	 *            - This comparator rule used to sort the key list. If null, all keys are assumed to extend Comparable.
	 */
	public void sort(Comparator<K> sorter){
		Object swapK, swapV;
		int c;
		boolean sorted;
		for(int n = 0; n<size; n++){
			sorted = true;
			for(int m = 0; m<(size-1)-n; m++){
				if(sorter==null){
					c = ((Comparable)k[m]).compareTo(k[m+1]);
				}else{
					c = sorter.compare((K)k[m], (K)k[m+1]);
				}
				if(c>0){
					sorted = false;
					swapK = k[m];
					k[m] = k[m+1];
					k[m+1] = swapK;
					swapV = v[m];
					v[m] = v[m+1];
					v[m+1] = swapV;
				}
				if(sorted){
					// If no values changed during this iteration, the list is already sorted.
					return;
				}
			}
		}
	}
	public void quickClear(){
		size = 0;
	}
	public void fullClear(){
		for(int i = 0; i<k.length; i++){
			k[i] = null;
			v[i] = null;
		}
	}
	@Override
	public Iterator<K> iterator(){
		return new Iterator<K>(){
			private int position;
			@Override
			public boolean hasNext(){
				return position<size;
			}
			@Override
			public K next(){
				return (K)k[position++];
			}
		};
	}
	public int getSize(){
		return size;
	}
}
