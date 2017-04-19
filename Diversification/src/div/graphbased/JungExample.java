package div.graphbased;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import utils.FileUtil;

public class JungExample
{

	public static final int coocc_count = 3;

	public static void main( String[] args )
	{
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
			
			String qryTermRemv = removeAllOccuranceOfFirst( cleaned_line, query );
		
//			cleaned_lines.add( cleaned_line );
			cleaned_lines.add( qryTermRemv );
		}

		//
		// // TODO Auto-generated method stub
		// // start();
		getCoOccStatistics( cleaned_lines );
	}

	public static String removeAllOccuranceOfFirst( String input , String query)
	{
		String[] words = input.split( " " );

		StringBuilder result = new StringBuilder();

		for ( int i = 0; i < words.length; i++ )
		{

			if ( query.equalsIgnoreCase( words[i] ) == false )
			{
				result.append( words[i] );
				result.append( " " );
			}
		}

		return result.toString();
	}

	private static void getCoOccStatistics( List<String> lines )
	{
		ResultSet1 rs1 = new ResultSet1();
		for ( String t : lines )
		{
			String[] splits = t.split( " " );
			rs1.updateHashtagToHashtagMap( new ArrayList<String>( new HashSet<String>( Arrays.asList( splits ) ) ) );
		}

		HashMap<String, HashMap<String, Integer>> coOccMap = rs1.getHashtagToHashtagMap();
		Set<Entry<String, HashMap<String, Integer>>> entrySet = coOccMap.entrySet();

		for ( Entry<String, HashMap<String, Integer>> entry : entrySet )
		{
			String key = entry.getKey();
			// System.out.print( key + " | " );
			HashMap<String, Integer> valuesMap = entry.getValue();
			Set<Entry<String, Integer>> entrySet2 = valuesMap.entrySet();
			for ( Entry<String, Integer> entry2 : entrySet2 )
			{
				String key2 = entry2.getKey();
				Integer value = entry2.getValue();
				// System.out.print( key2 + ":" + value + ", " );
			}
//			System.out.println();

		}

		// start(coOccMap);
		createGraph( rs1 );
	}

	private static void createGraph( ResultSet1 rs1 )
	{
		UndirectedGraph<RapidVertex, RapidEdge> g = rs1.generateWordWinClusters();

//		System.out.println( "The graph g = " + g.toString() );
		System.out.println( "No vertices = " + g.getVertexCount() );
		System.out.println( "No edges = " + g.getEdgeCount() );
		
		
		
		
		// No vertices = 2591 No edges = 17048
		Collection<RapidVertex> vertices = g.getVertices();
		List<RapidVertex> verticesToRemv = new ArrayList<>();
		
		for ( RapidVertex rapidVertex : vertices )
		{
			if ( g.getNeighborCount( rapidVertex ) == 0 ) //check if there are disconnected vertices
			{
				System.out.println( rapidVertex.getName() );
			}
			
//			if (rapidVertex.getName().equals( "#photography"))
//			{
//				int neighborCount = g.getNeighborCount( rapidVertex );
//				Collection<RapidVertex> neighbors = g.getNeighbors( rapidVertex );
//				System.out.println( neighbors.size() );
//				for ( RapidVertex rapidVertex2 : neighbors )
//				{
//					System.out.println( rapidVertex2.getName() );
//					System.out.println( g.getNeighborCount( rapidVertex2 ) );
//				}
//				
//				System.out.println( neighborCount );
//				System.out.println( g.outDegree( rapidVertex ) );
//				System.out.println( g.inDegree( rapidVertex ) );
//
//			}
			if ( g.getNeighborCount( rapidVertex ) == 1 ) 
			{
				verticesToRemv.add(rapidVertex);
			}

		}
		
//		//remove vertices with degree =1 
//		for ( RapidVertex rapidVertex : verticesToRemv )
//		{
//			g.removeVertex( rapidVertex );
//		}
		
		EdgeBetweennessClusterer<RapidVertex,RapidEdge> clusterer =	new EdgeBetweennessClusterer<RapidVertex,RapidEdge>(5);
//		WeakComponentClusterer<RapidVertex, RapidEdge> clusterer = new WeakComponentClusterer<>();
		Set<Set<RapidVertex>> transform = clusterer.transform( g );
		for ( Set<RapidVertex> vSet : transform )
		{
			System.out.println( "CLUSTER_i" );
			for ( RapidVertex rapidVertex : vSet )
			{
				System.out.print(rapidVertex.getName() + ", " );
			}
			System.out.println(  );
		}
		
		System.out.println( "No vertices = " + g.getVertexCount() );
		System.out.println( "No edges = " + g.getEdgeCount() );
		
		visualizeGraph( g );
		
	}
	
	private static void visualizeGraph(Graph g)
	{
		Layout<RapidVertex, RapidEdge> layout = new ISOMLayout<RapidVertex, RapidEdge>( g );
		layout.setSize( new Dimension( 500, 500 ) ); // sets the initial size of the space

		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		// BasicVisualizationServer<RapidVertex, RapidEdge> vv = new BasicVisualizationServer<RapidVertex, RapidEdge>( layout );
		VisualizationViewer<RapidVertex, RapidEdge> vv = new VisualizationViewer<>( layout );

		vv.setPreferredSize( new Dimension( 600, 600 ) ); // Sets the viewing area size
		vv.getRenderContext().setEdgeLabelTransformer( new Transformer<RapidEdge, String>()
		{

			@Override
			public String transform( RapidEdge arg0 )
			{
				// TODO Auto-generated method stub
				return String.valueOf( arg0.getWeight() );
			}
		} );
		vv.getRenderContext().setVertexLabelTransformer( new Transformer<RapidVertex, String>()
		{

			@Override
			public String transform( RapidVertex arg0 )
			{
				// TODO Auto-generated method stub
				return arg0.getName();
			}
		} );

		DefaultModalGraphMouse<RapidVertex, RapidEdge> gm = new DefaultModalGraphMouse<>();
		gm.setMode( Mode.PICKING );
		vv.setGraphMouse( gm );

		JFrame frame = new JFrame( "Simple Graph View" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add( vv );
		frame.pack();
		frame.setVisible( true );
	}

	
	//	private static void start( HashMap<String, HashMap<String, Integer>> coOccMap )
//	{
//		Graph<String, MyEdge> g = new SparseGraph<String, MyEdge>();
//
//		// Set<Entry<String, HashMap<String, Integer>>> entrySet = coOccMap.entrySet();
//		//
//		// for ( Entry<String, HashMap<String, Integer>> entry : entrySet )
//		// {
//		// String key = entry.getKey();
//		//// System.out.print( key + " | " );
//		// HashMap<String, Integer> valuesMap = entry.getValue();
//		// Set<Entry<String, Integer>> entrySet2 = valuesMap.entrySet();
//		// for ( Entry<String, Integer> entry2 : entrySet2 )
//		// {
//		// String key2 = entry2.getKey();
//		// Integer value = entry2.getValue();
//		//// System.out.print( key2 + ":" + value + ", " );
//		//
//		// g.addEdge( String.valueOf( value ), key, key2 );
//		// }
//		//// System.out.println();
//		//
//		// }
//		// Graph<V, E> where V is the type of the vertices
//		// and E is the type of the edges
//		// Add some vertices. From above we defined these to be type Integer.
//		// g.addVertex("shops");
//		// g.addVertex("food");
//		// g.addVertex("guide");
//		// g.addVertex( "food" );
//		// Add some edges. From above we defined these to be of type String
//		// Note that the default is for undirected edges.
//		g.addEdge( new MyEdge( "Edge-A" ), "shops", "food" ); // Note that Java 1.5 auto-boxes primitives
//		g.addEdge( new MyEdge( "Edge-B" ), "guide", "food" );
//		g.addEdge( new MyEdge( "Edge-C" ), "guide", "food" );
//
//		// Let's see what we have. Note the nice output from the
//		// SparseMultigraph<V,E> toString() method
//		System.out.println( "The graph g = " + g.toString() );
//		// Note that we can use the same nodes and edges in two different graphs.
//		// Graph<Integer, String> g2 = new SparseMultigraph<Integer, String>();
//		// g2.addVertex((Integer)1);
//		// g2.addVertex((Integer)2);
//		// g2.addVertex((Integer)3);
//		// g2.addEdge("Edge-A", 1,3);
//		// g2.addEdge("Edge-B", 2,3, EdgeType.DIRECTED);
//		// g2.addEdge("Edge-C", 3, 2, EdgeType.DIRECTED);
//		// g2.addEdge("Edge-P", 2,3); // A parallel edge
//		// System.out.println("The graph g2 = " + g2.toString());
//
//		// The Layout<V, E> is parameterized by the vertex and edge types
//		Layout<String, MyEdge> layout = new CircleLayout( g );
//		layout.setSize( new Dimension( 300, 300 ) ); // sets the initial size of the space
//		// The BasicVisualizationServer<V,E> is parameterized by the edge types
//		BasicVisualizationServer<String, MyEdge> vv = new BasicVisualizationServer<String, MyEdge>( layout );
//		vv.setPreferredSize( new Dimension( 350, 350 ) ); // Sets the viewing area size
//		vv.getRenderContext().setEdgeLabelTransformer( new Transformer<MyEdge, String>()
//		{
//
//			@Override
//			public String transform( MyEdge arg0 )
//			{
//				// TODO Auto-generated method stub
//				return arg0.getWeight();
//			}
//		} );
//		vv.getRenderContext().setVertexLabelTransformer( new Transformer<String, String>()
//		{
//
//			@Override
//			public String transform( String arg0 )
//			{
//				// TODO Auto-generated method stub
//				return arg0;
//			}
//		} );
//
//		JFrame frame = new JFrame( "Simple Graph View" );
//		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//		frame.getContentPane().add( vv );
//		frame.pack();
//		frame.setVisible( true );
//	}

}

class MyEdge
{
	private String weight;

	public MyEdge( String weight )
	{
		this.weight = weight;
	}

	public String getWeight()
	{
		return weight;
	}

	public void setWeight( String weight )
	{
		this.weight = weight;
	}
}
