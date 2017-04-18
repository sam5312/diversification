package utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MapUtils
{
	/**
	 * @param key entity you want to keep track of
	 * @param val optional, maybe a description of the key?
	 * @param map
	 */
	public static void IncrementThisUnitInTheMap(String key,  Map<String,C1> map,String val)
	{
		if(map.containsKey( key ))
		{
			C1 c1 = map.get( key );
			c1.freq++;
			map.put( key, c1 );
		}
		else {
			C1 c = new C1();
			c.freq = 1;
			c.word = val; 
			map.put( key, c );
		}
	}
	
	
	/**
	 * sorts based on value. basically used to sort when the value is numeric
	 * @param map
	 * @return
	 */
	public static Object[] sortMap(Map<String,C1> map)
	{
		Object[] array = map.entrySet().toArray();
		   
		Arrays.sort(array, new Comparator() {
	        public int compare(Object o1, Object o2) {
	            return ((Map.Entry<String, C1>) o2).getValue().freq.compareTo( ((Map.Entry<String, C1>) o1).getValue().freq);
	        }
	    });
		
		return array;
	}
	
	public static class C1{
		public Integer freq; 
		public String word;	
	}
	
	/**
	 * returns a new instance of a map
	 * @return
	 */
	public static Map<String, C1> getNewMap()
	{
		return new HashMap<String, MapUtils.C1>();
	}

}

