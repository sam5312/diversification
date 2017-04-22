package div.graphbased;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import utils.FileUtil;

public class Main
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

		List<String> orig_lines = FileUtil.readLines( new File( "/media/fatcat/sameendra/twitter_personalization/tagbasedeval10nov/userwise3/PlanYourSafaris/#travel/toindex_tm/toindextweets.txt" ) );
		List<String> cleaned_lines = new ArrayList<String>();
		for ( String line : orig_lines )
		{
			String[] splits = line.split( "\\s+", 3 ); // other_00111 x wake early beat crowds #travel #earlybird
			String cleaned_line = splits[2]; // wake early beat crowds #travel #earlybird

			String qryTermRemv = CooccuranceGraph.removeTargetWords( cleaned_line, query );

			// cleaned_lines.add( cleaned_line );
			cleaned_lines.add( qryTermRemv );
		}

		 //
		 // // TODO Auto-generated method stub
		 // // start();
		 ResultSet1 rs1 = CooccuranceGraph.getCoOccStatistics( cleaned_lines );
		 UndirectedGraph<RapidVertex, RapidEdge> graph = CooccuranceGraph.createGraph( rs1 );
		 CooccuranceGraph.visualizeGraph( graph , "orig graph" );
		 Graph<RapidVertex, RapidEdge> maxSG = CooccuranceGraph.printSubGraphs( graph );
		 Graph<RapidVertex, RapidEdge> mst = CooccuranceGraph.getMaxSpanningTree( maxSG );
		 
		 CooccuranceGraph.visualizeGraph( mst , "MST");
		

	}

}
