package div.graphbased;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import utils.FileUtil;

public class CooccuranceGraph
{

	public static final int coocc_count = 3;
	
	public static final int mst_min_degree = 3;

	public static String removeTargetWords( String input, String target )
	{
		String[] words = input.split( " " );

		StringBuilder result = new StringBuilder();

		for ( int i = 0; i < words.length; i++ )
		{

			if ( !target.equalsIgnoreCase( words[i] )  )
			{
				result.append( words[i] );
				result.append( " " );
			}
		}

		return result.toString();
	}

	public static ResultSet1 getCoOccStatistics( List<String> lines )
	{
		ResultSet1 rs1 = new ResultSet1();
		for ( String t : lines )
		{
			String[] splits = t.split( " " );
			rs1.updateHashtagToHashtagMap( new ArrayList<String>( new HashSet<String>( Arrays.asList( splits ) ) ) );
		}

		HashMap<String, HashMap<String, Integer>> coOccMap = rs1.getHashtagToHashtagMap();
		Set<Entry<String, HashMap<String, Integer>>> entrySet = coOccMap.entrySet();

//		for ( Entry<String, HashMap<String, Integer>> entry : entrySet )
//		{
//			String key = entry.getKey();
//			// System.out.print( key + " | " );
//			HashMap<String, Integer> valuesMap = entry.getValue();
//			Set<Entry<String, Integer>> entrySet2 = valuesMap.entrySet();
//			for ( Entry<String, Integer> entry2 : entrySet2 )
//			{
//				String key2 = entry2.getKey();
//				Integer value = entry2.getValue();
//				// System.out.print( key2 + ":" + value + ", " );
//			}
//			// System.out.println();
//
//		}
		return rs1;
		// start(coOccMap);
	}

	public static UndirectedGraph<RapidVertex, RapidEdge> createGraph( ResultSet1 rs1 )
	{
		UndirectedGraph<RapidVertex, RapidEdge> g = rs1.generateWordWinClusters();

//		System.out.println( "The graph g = " + g.toString() );
		System.out.println( "No vertices = " + g.getVertexCount() );
		System.out.println( "No edges = " + g.getEdgeCount() );

		Collection<RapidVertex> vertices = g.getVertices();
		List<RapidVertex> verticesToRemv = new ArrayList<>();

//		for ( RapidVertex rapidVertex : vertices )
//		{
//			if ( g.getNeighborCount( rapidVertex ) == 0 ) // check if there are disconnected vertices
//			{
//				System.out.println( rapidVertex.getName() );
//			}

			// if (rapidVertex.getName().equals( "#photography"))
			// {
			// int neighborCount = g.getNeighborCount( rapidVertex );
			// Collection<RapidVertex> neighbors = g.getNeighbors( rapidVertex );
			// System.out.println( neighbors.size() );
			// for ( RapidVertex rapidVertex2 : neighbors )
			// {
			// System.out.println( rapidVertex2.getName() );
			// System.out.println( g.getNeighborCount( rapidVertex2 ) );
			// }
			//
			// System.out.println( neighborCount );
			// System.out.println( g.outDegree( rapidVertex ) );
			// System.out.println( g.inDegree( rapidVertex ) );
			//
			// }
//			if ( g.getNeighborCount( rapidVertex ) == 1 )
//			{
//				verticesToRemv.add( rapidVertex );
//			}
//
//		}

		// //remove vertices with degree =1
		// for ( RapidVertex rapidVertex : verticesToRemv )
		// {
		// g.removeVertex( rapidVertex );
		// }
		return g;
	}
	
	
	
	
	/**
	 * prints the subgraphs of the given graph and returns the maximum subgraph. uses the weakcomponentclusterer for finding subgraphs.
	 * @param g
	 * @param subgraphsRemvMaxsubgraph 
	 * @return 
	 */
	public static Graph<RapidVertex, RapidEdge> printSubGraphs(Graph<RapidVertex,RapidEdge> g, Set<Set<RapidVertex>> subgraphsRemvMaxsubgraph)
	{
//		EdgeBetweennessClusterer<RapidVertex, RapidEdge> clusterer = new EdgeBetweennessClusterer<RapidVertex, RapidEdge>( 5 );
		WeakComponentClusterer<RapidVertex, RapidEdge> clusterer = new WeakComponentClusterer<>();
		int i = 0;
		Set<Set<RapidVertex>> transform = clusterer.transform( g );
		subgraphsRemvMaxsubgraph.addAll( transform ); //add all sets of vertices first
	
		int maxClusterSize  = -1;
		Set<RapidVertex> maxClust = null; //contains the largest cluster
		
		for ( Set<RapidVertex> vSet : transform )
		{  
			if (vSet.size() > maxClusterSize)
			{
				maxClust = vSet;
				maxClusterSize = vSet.size();
			}
	
			System.out.println( "CLUSTER_" + i + ", " + vSet.size() );
			for ( RapidVertex rapidVertex : vSet )
			{
				System.out.print( rapidVertex.getName() + ", " );
			}
			System.out.println();
			i++;
		}
		
		subgraphsRemvMaxsubgraph.remove( maxClust );//remove the max subgraph

		
		Graph<RapidVertex, RapidEdge> maxSubGraph = FilterUtils.createInducedSubgraph( maxClust, g );
		return maxSubGraph;
	}
	
	/**
	 * returns the subgraphs of a given graph
	 * @param g
	 * @param subgraphsRemvMaxsubgraph 
	 * @return 
	 */
	public static Set<Set<RapidVertex>> getWeakComponents(Graph<RapidVertex,RapidEdge> g)
	{
//		EdgeBetweennessClusterer<RapidVertex, RapidEdge> clusterer = new EdgeBetweennessClusterer<RapidVertex, RapidEdge>( 5 );
		WeakComponentClusterer<RapidVertex, RapidEdge> clusterer = new WeakComponentClusterer<>();
		int i = 0;
		Set<Set<RapidVertex>> transform = clusterer.transform( g );
	
		return transform;
	}
	
	/**
	 * Finds the maximum spanning tree and apply the algorithm as in the paper which removes minium weight edges
	 * @param g
	 * @return
	 */
	public static Graph<RapidVertex, RapidEdge> getMaxSpanningTree(Graph<RapidVertex,RapidEdge> g)
	{
		PrimMinimumSpanningTree<RapidVertex, RapidEdge> prim = new PrimMinimumSpanningTree<>( 
				UndirectedSparseGraph.<RapidVertex, RapidEdge> getFactory(), new Transformer<RapidEdge, Double>()
		{

			@Override
			public Double transform( RapidEdge arg0 )
			{
				// TODO Auto-generated method stub
				return -Double.valueOf( arg0.getWeight() );
			}
		} );
		Graph<RapidVertex, RapidEdge> MST = prim.transform( g );
	
//		System.out.println( "MST" + MST.toString() );
		System.out.println( "MST, No vertices = " + MST.getVertexCount() );
		System.out.println( "MST, No edges = " + MST.getEdgeCount() );
	
		
		Set<RapidEdge> mstEdgesSet = new HashSet<RapidEdge>(MST.getEdges());
		List<RapidEdge> mstEdgesList = new ArrayList<>();
		mstEdgesList.addAll( mstEdgesSet );
		
//		System.out.println( printEdges( mstEdgesList ) );
	
		//sort edges by their weight
		Collections.sort( mstEdgesList, new Comparator<RapidEdge>()
		{

			@Override
			public int compare( RapidEdge o1, RapidEdge o2 )
			{
				if (o1.getWeight() > o2.getWeight()) return 1;
				if (o1.getWeight() < o2.getWeight()) return -1;
				return 0;
			}
		} );
		for ( RapidEdge rapidEdge : mstEdgesList )
		{
			Pair<RapidVertex> endpoints = MST.getEndpoints( rapidEdge );
			RapidVertex first = endpoints.getFirst();
			RapidVertex second = endpoints.getSecond();
			int degreeFirst = MST.getNeighborCount( first );
			int degreeSecond = MST.getNeighborCount( second );
			if ( degreeFirst >= mst_min_degree && degreeSecond >= mst_min_degree)
			{
				MST.removeEdge( rapidEdge );
			}
		}
		
//		System.out.println( printEdges( mstEdgesList ) );
		return MST;
	}
	
	public static String printEdges(List<RapidEdge> edges) {
    	StringBuffer sb = new StringBuffer("Edges:");
    	
    	for(RapidEdge e : edges) {
    		sb.append(e.getWeight() + ",");
    	}
        return sb.toString();
    }

	public static void visualizeGraph( Graph g , String name)
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

		JFrame frame = new JFrame( name );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add( vv );
		frame.pack();
		frame.setVisible( true );
	}

//	private static void start()
//	{
//		Graph<String, MyEdge> g = new UndirectedSparseGraph<String, MyEdge>();
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
//		g.addEdge( new MyEdge( "10" ), "A", "C" ); // Note that Java 1.5 auto-boxes primitives
//		g.addEdge( new MyEdge( "7" ), "A", "D" );
//		g.addEdge( new MyEdge( "9" ), "C", "D" );
//		g.addEdge( new MyEdge( "32" ), "D", "B" );
//		g.addEdge( new MyEdge( "23" ), "F", "E" );
//
//		// Let's see what we have. Note the nice output from the
//		// SparseMultigraph<V,E> toString() method
//		System.out.println( "The graph g = " + g.toString() );
//		PrimMinimumSpanningTree<String, MyEdge> prim = new PrimMinimumSpanningTree<>( UndirectedSparseGraph.<String, MyEdge> getFactory(), new Transformer<MyEdge, Double>()
//		{
//
//			@Override
//			public Double transform( MyEdge arg0 )
//			{
//				// TODO Auto-generated method stub
//				return Double.valueOf( arg0.getWeight() );
//			}
//		} );
//		Graph<String, MyEdge> MST = prim.transform( g );
//		System.out.println( MST.toString() );
//
//		// PrimMinimumSpanningTree<String, MyEdge> prim = new PrimMinimumSpanningTree<>( UndirectedSparseGraph.<String,MyEdge>getFactory() );
//		// Graph<String, MyEdge> MST = prim.transform( g );
//		// System.out.println( MST.toString() );
//		//
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
//		Layout<String, MyEdge> layout = new ISOMLayout( MST );
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
