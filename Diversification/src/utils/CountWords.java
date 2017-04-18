package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CountWords
{
	/**
	 * returns the most freq words in the text. Used as queries
	 * 
	 * @param lines
	 * @param topn
	 * @return
	 */
	public static String getMostFreqWords( List<String> lines, int topn )
	{
		Map<String, Integer> wordsMap = new HashMap<String, Integer>();
		for ( String line : lines )
		{
			String[] words = line.split( "\\s+" );
			for ( int i = 0; i < words.length; i++ )
			{
				String s = words[i];

				if ( wordsMap.containsKey( s ) )
				{
					Integer count = wordsMap.get( s ) + 1;
					wordsMap.put( s, count );
				}
				else
					wordsMap.put( s, 1 );

			}
		}

		Object[] a = wordsMap.entrySet().toArray();

		Arrays.sort( a, new Comparator<Object>()
		{
			public int compare( Object o1, Object o2 )
			{
				return ( ( Map.Entry<String, Integer> ) o2 ).getValue().compareTo( ( ( Map.Entry<String, Integer> ) o1 ).getValue() );
			}
		} );

		String topWords = "";
		for ( int i = 0; i < topn; i++ )
		{
			Map.Entry<String, Integer> e = ( Entry<String, Integer> ) a[i];
			topWords += e.getKey() + " ";
			// System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
			// + ((Map.Entry<String, Integer>) e).getValue());
		}
		return topWords;
	}

	public static String getMostFreqHashTags( List<String> lines, int topn )
	{
		Map<String, Integer> wordsMap = new HashMap<String, Integer>();
		for ( String line : lines )
		{
			String[] words = line.split( "\\s+" );
			for ( int i = 0; i < words.length; i++ )
			{
				String s = words[i];

				if ( s.startsWith( "#" ) )
				{
					if ( wordsMap.containsKey( s ) )
					{
						Integer count = wordsMap.get( s ) + 1;
						wordsMap.put( s, count );
					}
					else
					{
						wordsMap.put( s, 1 );
					}
				}

			}
		}

		Object[] a = wordsMap.entrySet().toArray();

		Arrays.sort( a, new Comparator<Object>()
		{
			public int compare( Object o1, Object o2 )
			{
				return ( ( Map.Entry<String, Integer> ) o2 ).getValue().compareTo( ( ( Map.Entry<String, Integer> ) o1 ).getValue() );
			}
		} );

		if (a.length <= 3)
		{
			return "";
		}
		//to avoid array index outof bounds exception from below
		if (a.length < topn)
		{
			topn = a.length;
		} 
		
		String topWords = "";
		for ( int i = 0; i < topn; i++ )
		{
			Map.Entry<String, Integer> e = ( Entry<String, Integer> ) a[i];
			topWords += e.getKey() + " ";
			// System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
			// + ((Map.Entry<String, Integer>) e).getValue());
		}
		return topWords;
	}
}
