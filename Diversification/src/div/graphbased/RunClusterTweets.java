package div.graphbased;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import utils.FileUtil;

public class RunClusterTweets
{

	public static void main( String[] args )
	{
//		start();
		// String t1 = "travel gear checklist #travelblogger #fashionblogger #travel #fashionblogger #blogger";
		// String t2 = "wake early beat crowds #travel #earlybird ";
		// String t3 = "highest-rated travel products amazon #travel #products gear";
		// String t4 = "sameendra colombo travel";
		// String t5 = "colombo travel";
		//
		// List<String> l = new ArrayList<>();
		// l.add( t1 );
		// l.add( t2 );
		// l.add( t3 );
		// l.add( t4 );
		// l.add( t5 );

		String query = "#travel";
//		String query = "#android";

		List<String> orig_lines = FileUtil.readLines( new File( "/media/fatcat/sameendra/twitter_personalization/"
				+ "tagbasedeval10nov/userwise3/PlanYourSafaris/#travel/toindex_tm/toindextweets.txt" ) );
		
//		List<String> orig_lines = FileUtil.readLines( new File( "/media/fatcat/sameendra/twitter_personalization/"
//				+ "tagbasedeval10nov/userwise3/AndroidDev/#android/toindex_tm/toindextweets.txt" ) );
//		
		
		start(query, orig_lines);

	}
	
	public static Map<Integer, List<Tweet>> start(String query, List<String> orig_lines)
	{
		List<String> cleaned_lines = new ArrayList<String>();
		for ( String line : orig_lines )
		{
			String[] splits = line.split( "\\s+", 3 ); // other_00111 x wake early beat crowds #travel #earlybird
			String cleaned_line = splits[2]; // wake early beat crowds #travel #earlybird

			String qryTermRemv = CooccuranceGraph.removeTargetWords( cleaned_line, query );

			// cleaned_lines.add( cleaned_line );
			cleaned_lines.add( qryTermRemv );
		}

		 ResultSet1 rs1 = CooccuranceGraph.getCoOccStatistics( cleaned_lines );
		 UndirectedGraph<RapidVertex, RapidEdge> graph = CooccuranceGraph.createGraph( rs1 );
		// CooccuranceGraph.visualizeGraph( graph , "orig graph" );
		 
		 Set<Set<RapidVertex>> subgraphsRemvMaxsubgraph = new HashSet<Set<RapidVertex>>(); //contains subgraphs but the maxsubgraph is removed
		 Graph<RapidVertex, RapidEdge> maxSG = CooccuranceGraph.printSubGraphs( graph, subgraphsRemvMaxsubgraph ); //get the maximum subgraph
		 Graph<RapidVertex, RapidEdge> mst = CooccuranceGraph.getMaxSpanningTree( maxSG ); //find the MSTs for the maximum subgraph

		 Set<Set<RapidVertex>> weakComponents = CooccuranceGraph.getWeakComponents( mst );
		 weakComponents.addAll( subgraphsRemvMaxsubgraph );

		 //all the clusters 
//		int i = 0;
//		for ( Set<RapidVertex> vSet : weakComponents )
//		{
//			System.out.println( "CLUSTER_" + i + ", " + vSet.size() );
//			for ( RapidVertex rapidVertex : vSet )
//			{
//				System.out.print( rapidVertex.getName() + ", " );
//			}
//			System.out.println();
//			i++;
//		}
		 
	//	 CooccuranceGraph.visualizeGraph( mst , "MST");
		 Map<Integer, List<Tweet>> clusteredTweets = ClusterTweets.assignTweetsToClusters( weakComponents, orig_lines );
			//print clusters
//			Set<Entry<Integer, List<Tweet>>> entrySet = clusteredTweets.entrySet();
//			for ( Entry<Integer, List<Tweet>> entry : entrySet )
//			{
//				Integer clusterId = entry.getKey();
//				List<Tweet> tweets = entry.getValue();
//				System.out.println( clusterId + ", " + tweets.size()  +", " + tweets.get( 0 ).tweetId + ", " + tweets.get( 0 ).getTweetTxt());
//			}
		 return clusteredTweets;
	}

}
