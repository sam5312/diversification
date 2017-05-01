package div.graphbased;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ClusterTweets
{

	public static Map<Integer, List<Tweet>> assignTweetsToClusters( Set<Set<RapidVertex>> weakComponents, List<String> orig_lines )
	{
		System.out.println( "total no of clusters = " + weakComponents.size() );

		Map<Integer, List<Tweet>> tweetClustersMap = new HashMap<Integer, List<Tweet>>();

		for ( String tweet : orig_lines ) // for each tweet
		{
			String[] splits = tweet.split( "\\s+", 3 ); // other_00111 x wake early beat crowds #travel #earlybird
			String cleaned_line = splits[2]; // wake early beat crowds #travel #earlybird
			
			Tweet t = new Tweet( splits[0], cleaned_line );
//			Map<String, Integer> tweetMap = getTermFrequencyMapForDoc( cleaned_line.split( " " ) );
			
			double maxScore = -1;
			int clusterId = -1;

			int i = 0;
			for ( Set<RapidVertex> vSet : weakComponents ) // for each graph component
			{
				//similarity using cosine
//				Map<String, Integer> graphCompMap = getTermFrequencyMapForAGraphComp( vSet );
//				double simScore = cosineSim( tweetMap, graphCompMap );
				
				//similarity based on the intersection of words in the tweet and the words in the cluster(vSet)
				double simScore = tweetAndClusterIntersec( cleaned_line.split( " " ), vSet );
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
			
			addThisTweetToACluster(t,clusterId,tweetClustersMap);
		}
		
		return tweetClustersMap;
	}

	private static int tweetAndClusterIntersec(String[] terms, Set<RapidVertex> vSet)
	{
		Set<String> senseCluster = new HashSet<>();
		for ( RapidVertex vertex : vSet )
		{
			senseCluster.add( vertex.getName() );
		}
		
		senseCluster.retainAll( new HashSet<String>(Arrays.asList( terms )) );
		
		return senseCluster.size();
		
	}
	
	private static void addThisTweetToACluster( Tweet t, int clusterId, Map<Integer, List<Tweet>> tweetClustersMap )
	{
		if ( tweetClustersMap.get( clusterId ) == null)
		{
			List<Tweet> l = new LinkedList<>();
			l.add( t );
			tweetClustersMap.put( clusterId, l );
		} else
		{
			List<Tweet> tweetsList = tweetClustersMap.get( clusterId );
			tweetsList.add( t );
			tweetClustersMap.put( clusterId, tweetsList );
		}
	}

	private static Map<String, Integer> getTermFrequencyMapForDoc( String[] terms )
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

	private static Map<String, Integer> getTermFrequencyMapForAGraphComp( Set<RapidVertex> vSet )
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

	public static double cosineSim( Map<String, Integer> a, Map<String, Integer> b )
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

class Tweet
{
	String tweetTxt;
	String tweetId;
	
	public Tweet(String id, String txt)
	{
		this.tweetId = id;
		this.tweetTxt = txt;
	}
	public String getTweetTxt()
	{
		return tweetTxt;
	}
	public void setTweetTxt( String tweetTxt )
	{
		this.tweetTxt = tweetTxt;
	}
	public String getTweetId()
	{
		return tweetId;
	}
	public void setTweetId( String tweetId )
	{
		this.tweetId = tweetId;
	}

}
