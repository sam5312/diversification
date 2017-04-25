package div.graphbased;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ClusterTweets
{

	public static void start( Set<Set<RapidVertex>> weakComponents, List<String> orig_lines )
	{
		System.out.println( "total no of clusters = " + weakComponents.size() );

		Map<Integer, List<String>> tweetClustersMap = new HashMap<Integer, List<String>>();

		for ( String tweet : orig_lines ) // for each tweet
		{
			String[] splits = tweet.split( "\\s+", 3 ); // other_00111 x wake early beat crowds #travel #earlybird
			String cleaned_line = splits[2]; // wake early beat crowds #travel #earlybird
			Map<String, Integer> tweetMap = getTermFrequencyMapForDoc( cleaned_line.split( " " ) );
			
			double maxScore = -1;
			int clusterId = -1;

			int i = 0;
			for ( Set<RapidVertex> vSet : weakComponents ) // for each graph component
			{
				Map<String, Integer> graphCompMap = getTermFrequencyMapForAGraphComp( vSet );

				double simScore = calcSim( tweetMap, graphCompMap );
				if ( simScore != 0)
				{
					if ( simScore > maxScore)
					{
						maxScore = simScore;
						clusterId = i;
					}
				}
				else
				{
//					System.out.println( "sim score is zero" );
				}
				i++;
			}
			
			addThisTweetToACluster(tweet,clusterId,tweetClustersMap);
		}
		
		Set<Entry<Integer,List<String>>> entrySet = tweetClustersMap.entrySet();
		for ( Entry<Integer, List<String>> entry : entrySet )
		{
			Integer clusterId = entry.getKey();
			List<String> tweets = entry.getValue();
			System.out.println( clusterId + ", " + tweets.size() );
		}
	}

	private static void addThisTweetToACluster( String tweet, int clusterId, Map<Integer, List<String>> tweetClustersMap )
	{
		if ( tweetClustersMap.get( clusterId ) == null)
		{
			List<String> l = new LinkedList<>();
			l.add( tweet );
			tweetClustersMap.put( clusterId, l );
		} else
		{
			List<String> tweetsList = tweetClustersMap.get( clusterId );
			tweetsList.add( tweet );
			tweetClustersMap.put( clusterId, tweetsList );
		}
	}

	public static Map<String, Integer> getTermFrequencyMapForDoc( String[] terms )
	{
		Map<String, Integer> termFrequencyMap = new HashMap<>();
		for ( String term : terms )
		{
			Integer n = termFrequencyMap.get( term );
			n = ( n == null ) ? 1 : ++n;
			termFrequencyMap.put( term, n );
		}
		return termFrequencyMap;
	}

	public static Map<String, Integer> getTermFrequencyMapForAGraphComp( Set<RapidVertex> vSet )
	{
		Map<String, Integer> termFrequencyMap = new HashMap<>();
		for ( RapidVertex rapidVertex : vSet )
		{
			Integer n = termFrequencyMap.get( rapidVertex.getName() );
			n = ( n == null ) ? 1 : ++n;
			termFrequencyMap.put( rapidVertex.getName(), n );
		}
		return termFrequencyMap;
	}

	public static double calcSim( Map<String, Integer> a, Map<String, Integer> b )
	{
		// Get unique words from both sequences
		HashSet<String> intersection = new HashSet<>( a.keySet() );
		intersection.retainAll( b.keySet() );

		double dotProduct = 0, magnitudeA = 0, magnitudeB = 0;

		// Calculate dot product
		for ( String item : intersection )
		{
			dotProduct += a.get( item ) * b.get( item );
		}

		// Calculate magnitude a
		for ( String k : a.keySet() )
		{
			magnitudeA += Math.pow( a.get( k ), 2 );
		}

		// Calculate magnitude b
		for ( String k : b.keySet() )
		{
			magnitudeB += Math.pow( b.get( k ), 2 );
		}

		// return cosine similarity
		return dotProduct / Math.sqrt( magnitudeA * magnitudeB );
	}

}
