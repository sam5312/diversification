/**
 * 
 */
package div.graphbased;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import utils.FileUtil;

/**
 * @author sam 26 Apr 2017 11:21:41 am
 */
public class RunMSTExperiments
{
//	public static final String rootdir = "/media/fatcat/sameendra/twitter_personalization/tagbasedeval10nov/userwise3/";
	public static final String rootdir = "/media/fatcat/sameendra/twitter_personalization/tagbasedeval10nov/userwise4_50per/";

	private  static int pk1 = 20; //20; //precision at k1
	private  static int pk2 = 50; // 50; 
	
	private final static int  TIMES_USERS_TWEETS = 100;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main( String[] args ) throws IOException
	{
		start();
	}

	private static void start() throws IOException
	{
		File[] folders = new File( rootdir ).listFiles();

		for ( File userDir : folders ) // for each user
		{
			float methodavg_pat20 = 0f;
			float methodavg_pat50 = 0f;
			float methodavg_avgPrec = 0f;
			float methodavg_rr = 0f;
			
			String userName = userDir.getName();
			File[] querytermsfolders = userDir.listFiles();

			float noOfQuries = querytermsfolders.length;

			System.out.println( userDir.getPath() ); // print the name of the list pair

			for ( File qryDir : querytermsfolders ) // for each query "#beauty", "#fashion" etc
			{
				String query = qryDir.getName().trim();

				System.out.println( query ); // print the query
				
				List<String> userTweets = FileUtil.readLines( new File(qryDir.getPath() + "/userprofiles/" + userName + "_bottom_diff.dat")) ;
				Map<String, Integer> tfMapUser = getTermFrequencyMapForUser( userTweets );

//				List<String> linesToindex = FileUtil.readLines( new File(qryDir.getPath() + "/toindex_tm/toindextweets.txt" )) ;
				
				//when not using the output from gensim where each tweet is a single file
				List<String> linesToindex = new ArrayList<>();

				File toindexDir = new File(qryDir.getPath() + "/toindex/");
				List<File> tweetsOftheSameUser = getTweetsOftheSameUser( toindexDir.toPath(), userName );
				int noOfTweetsFromTheSameUsr = tweetsOftheSameUser.size();

				
				for ( File doc : tweetsOftheSameUser )
				{
					List<String> lines = FileUtil.readLines( doc );
					String tweet = lines.get( 0 ); 
					linesToindex.add( doc.getName()	+"\tx\t"+ tweet );
				}
				
				List<File> otherTweets = getOtherTweets( toindexDir.toPath(), TIMES_USERS_TWEETS*noOfTweetsFromTheSameUsr - noOfTweetsFromTheSameUsr );
				
				for ( File doc : otherTweets )
				{
					List<String> lines = FileUtil.readLines( doc );
					String tweet = lines.get( 0 ); 
					linesToindex.add( doc.getName()	+"\tx\t"+tweet );
				}
				
				System.out.println( "total docs in the index = " + linesToindex.size() );
				Map<Integer, List<Tweet>> clusteredTweetsToindex = RunClusterTweets.start( query, linesToindex, userName );
				
				double maxScore = -1;
				int userAssignCluster = -1;
				//assign the user to the most similar cluster
				for ( Entry<Integer, List<Tweet>> cluster : clusteredTweetsToindex.entrySet() )
				{
					Integer clusterId = cluster.getKey();
					List<Tweet> tweetsInThisCluster = cluster.getValue();
					
					Map<String, Integer> tfMapCluster = getTermFrequencyMapForAGraphComp( tweetsInThisCluster );
					
					double simScore = ClusterTweets.cosineSim( tfMapUser, tfMapCluster );
					if ( simScore != 0)
					{
						if ( simScore > maxScore)
						{
							maxScore = simScore;
							userAssignCluster = clusterId;
						}
					}
					else
					{
						System.out.println( "user-cluster cosine score is zero" );
					}
				}
				
				//tweets from cluster which is most sim tweets to user
				List<Tweet> tweetsFromUserAssignedCluster = clusteredTweetsToindex.get( userAssignCluster ); 
//				for ( Tweet tweet : tweetsFromUserAssignedCluster )
//				{
//					System.out.println( tweet.getTweetId() + " " + tweet.getTweetTxt() );
//				}
				
				//other tweets from other clusters
				List<Tweet> tweetsFromOtherClusters = getTweetsFromOtherClusters(clusteredTweetsToindex, userAssignCluster);
				//append other tweets to the end of most sim tweets
				tweetsFromUserAssignedCluster.addAll( tweetsFromOtherClusters );
				
				
				float p_20 = calcPrecAtK( userName, pk1, tweetsFromUserAssignedCluster );
				float p_50 = calcPrecAtK( userName, pk2, tweetsFromUserAssignedCluster );
				float ap = calcAvgPrec( userName, tweetsFromUserAssignedCluster );
				float rr = calcRR( userName, tweetsFromUserAssignedCluster );
				
				methodavg_pat20 += p_20;
				methodavg_pat50 += p_50;
				methodavg_avgPrec += ap;
				methodavg_rr += rr;
				
				System.out.print( "MST" + '\t' );
				System.out.print( ( float ) p_20 + "\t" );
				System.out.print( ( float ) p_50 + "\t" );
				System.out.print( ( float ) ap + "\t" );
				System.out.print( ( float ) rr + "\t" );	
				System.out.println();
			}
			
			System.out.println( "AVG_MST\t" + methodavg_pat20 / noOfQuries + "\t" + methodavg_pat50 / noOfQuries + "\t" + 
					methodavg_avgPrec / noOfQuries + "\t" + methodavg_rr / noOfQuries );

		}

	}
	
	private static List<File> getTweetsOftheSameUser(Path path, String userName) throws IOException
	{
		List<File> tweetsSameUser = new ArrayList<File>();
		
		if ( Files.isDirectory( path ) )
		{
			File dir = path.toFile();
			
			for ( File file : dir.listFiles() )
			{
				if(file.getName().contains( userName))
				{
					tweetsSameUser.add( file );
				}
			}
		}
		return tweetsSameUser;
	}
	
	private static List<File> getOtherTweets( Path path, final int otherTweetsToIndex ) throws IOException
	{
		List<File> othertweets = new ArrayList<File>();

		if ( Files.isDirectory( path ) )
		{
			File dir = path.toFile();
			int count = 0;

			for ( File tweet : dir.listFiles() )
			{
				// only index non user's tweets (other_xxx.txt)
				if ( tweet.getName().startsWith( "other_" ) )
				{

					if ( count < otherTweetsToIndex )
					{
						othertweets.add( tweet );
						count++;
					}
					else // break if the desired no of tweets hv been indexed
					{
						break;
					}
				}
			}
		}
		return othertweets;
	}
	
	private static float calcRR( String qryClass, List<Tweet> tweetsFromUserAssignedCluster )
	{
		float val = 0.0f;
		int i = 0;
		
		for ( Tweet	 t : tweetsFromUserAssignedCluster )
		{
			String docName = t.getTweetId();
			boolean ispos = isPos( docName, qryClass );
				
			if ( ispos  ) // non rel item
			{
				val = ( float ) 1 / ( i + 1 );
				break;
			}
			
			i++;
		}

		return val;
	}

	private static float calcAvgPrec( String qryClass, List<Tweet> tweetsFromUserAssignedCluster )
	{
		int relDocsCount = 0;
		float cum_pAt_i = 0.0f; // cumulative p@i

		int i = 0;
		
		for ( Tweet	 t : tweetsFromUserAssignedCluster )
		{
			String docName = t.getTweetId();
			boolean isPos = isPos( docName, qryClass );


			if ( isPos )
			{
				relDocsCount++;
				float pAt_i = ( float ) relDocsCount / ( i + 1 );
				cum_pAt_i += pAt_i;
			}

			i++;
		}

		float avgPrec = cum_pAt_i / relDocsCount;

		return avgPrec;
	}

	private static float calcPrecAtK( String qryClass, int K, List<Tweet> tweetsFromUserAssignedCluster )
	{
		int reldocs = 0;
		
		int count = 0;
		for ( Tweet	 t : tweetsFromUserAssignedCluster )
		{
			String docName = t.getTweetId();
			boolean ispos = isPos( docName, qryClass );

			if ( ispos )
				reldocs++;
			
			if ( ++count == K)
			{
				break;
			}
		}

		return ( float ) reldocs / K;
	}
	
	private static boolean isPos( String docName, String queryclass )
	{
		String docclass = docName.split( "_" )[0];
		// selecting the right aspect of the query
		if ( queryclass.equals( docclass ) )
			return true;
		else
			return false;
	}

	private static List<Tweet> getTweetsFromOtherClusters( Map<Integer, List<Tweet>> clusteredTweetsToindex, int userAssignCluster )
	{
		List<Tweet> tweetsFromOtherClusters = new ArrayList<>();
		for ( Entry<Integer, List<Tweet>> cluster : clusteredTweetsToindex.entrySet() )
		{
			int clusterId = cluster.getKey();
			if ( clusterId !=  userAssignCluster)
			{
				List<Tweet> tweetsInThisCluster = cluster.getValue();
				tweetsFromOtherClusters.addAll( tweetsInThisCluster );
			}
		}
		return tweetsFromOtherClusters;
	}

	private static Map<String, Integer> getTermFrequencyMapForUser( List<String> userTweets )
	{
		Map<String, Integer> termFrequencyMap = new HashMap<>();

		for ( String tweet : userTweets )
		{
			String[] terms = tweet.split(" " );
			for ( String term : terms )
			{
				Integer n = termFrequencyMap.get( term );
				n = ( n == null ) ? 1 : ++n;
				termFrequencyMap.put( term, n );
			}
		}
		return termFrequencyMap;
	}
	private static Map<String, Integer> getTermFrequencyMapForAGraphComp( List<Tweet> GraphCompTweets )
	{
		Map<String, Integer> termFrequencyMap = new HashMap<>();

		for ( Tweet tweet : GraphCompTweets )
		{
			String[] terms = tweet.getTweetTxt().split(" " );
			for ( String term : terms )
			{
				Integer n = termFrequencyMap.get( term );
				n = ( n == null ) ? 1 : ++n;
				termFrequencyMap.put( term, n );
			}
		}
		return termFrequencyMap;
	}

}
