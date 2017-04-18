package div.graphbased;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;

public class JungExample
{

	public static void main( String[] args )
	{
		String t1 = "travel gear checklist #travelblogger #fashionblogger #travel #fashionblogger #blogger";
		String t2 = "wake early beat crowds #travel #earlybird ";// "sameendra colombo";
		String t3 = "highest-rated travel products amazon #travel #products gear";
		

		List<String> l = new ArrayList<>();
		l.add( t1 );
		l.add( t2 );
		l.add( t3 );

		// TODO Auto-generated method stub
		// start();
		createCoocuGraph( l );
	}

	private static void createCoocuGraph( List<String> l )
	{
		ResultSet1 rs1 = new ResultSet1();
		for ( String t : l )
		{
			String[] splits = t.split( " " );
			rs1.updateHashtagToHashtagMap( new ArrayList<String>( new HashSet<String>( Arrays.asList( splits ) ) ) );
		}

		HashMap<String, HashMap<String, Integer>> coOccMap = rs1.getHashtagToHashtagMap();
		Set<Entry<String, HashMap<String, Integer>>> entrySet = coOccMap.entrySet();

		for ( Entry<String, HashMap<String, Integer>> entry : entrySet )
		{
			String key = entry.getKey();
			System.out.print( key + " | " );
			HashMap<String, Integer> valuesMap = entry.getValue();
			Set<Entry<String, Integer>> entrySet2 = valuesMap.entrySet();
			for ( Entry<String, Integer> entry2 : entrySet2 )
			{
				String key2 = entry2.getKey();
				Integer value = entry2.getValue();
				System.out.print( key2 + ":" + value + ", " );
			}
			System.out.println();

		}

//		start(coOccMap);
		createGraph(rs1);
	}

	private static void createGraph( ResultSet1 rs1 )
	{
		UndirectedGraph<RapidVertex,RapidEdge> g = rs1.generateWordWinClusters();
		System.out.println( "The graph g = " + g.toString() );
		
		Layout<RapidVertex, RapidEdge> layout = new CircleLayout( g );
		layout.setSize( new Dimension( 500, 500 ) ); // sets the initial size of the space
	
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
//		BasicVisualizationServer<RapidVertex, RapidEdge> vv = new BasicVisualizationServer<RapidVertex, RapidEdge>( layout );
		VisualizationViewer<RapidVertex, RapidEdge> vv = new VisualizationViewer<>( layout );
		
		vv.setPreferredSize( new Dimension( 600, 600 ) ); // Sets the viewing area size
		vv.getRenderContext().setEdgeLabelTransformer(new Transformer<RapidEdge, String>()
		{

			@Override
			public String transform( RapidEdge arg0 )
			{
				// TODO Auto-generated method stub
				return String.valueOf( arg0.getWeight());
			}
		});
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
	

	private static void start( HashMap<String, HashMap<String, Integer>> coOccMap )
	{
		Graph<String, MyEdge> g = new SparseGraph<String, MyEdge>();

//		Set<Entry<String, HashMap<String, Integer>>> entrySet = coOccMap.entrySet();
//
//		for ( Entry<String, HashMap<String, Integer>> entry : entrySet )
//		{
//			String key = entry.getKey();
////			System.out.print( key + " | " );
//			HashMap<String, Integer> valuesMap = entry.getValue();
//			Set<Entry<String, Integer>> entrySet2 = valuesMap.entrySet();
//			for ( Entry<String, Integer> entry2 : entrySet2 )
//			{
//				String key2 = entry2.getKey();
//				Integer value = entry2.getValue();
////				System.out.print( key2 + ":" + value + ", " );
//				
//				g.addEdge( String.valueOf( value ), key, key2 );
//			}
////			System.out.println();
//
//		}
		// Graph<V, E> where V is the type of the vertices
		// and E is the type of the edges
		// Add some vertices. From above we defined these to be type Integer.
		// g.addVertex("shops");
		// g.addVertex("food");
		// g.addVertex("guide");
		// g.addVertex( "food" );
		// Add some edges. From above we defined these to be of type String
		// Note that the default is for undirected edges.
		g.addEdge( new MyEdge( "Edge-A"), "shops", "food" ); // Note that Java 1.5 auto-boxes primitives
		g.addEdge( new MyEdge( "Edge-B"), "guide", "food" );
		g.addEdge( new MyEdge( "Edge-C"), "guide", "food" );

		// Let's see what we have. Note the nice output from the
		// SparseMultigraph<V,E> toString() method
		System.out.println( "The graph g = " + g.toString() );
		// Note that we can use the same nodes and edges in two different graphs.
		// Graph<Integer, String> g2 = new SparseMultigraph<Integer, String>();
		// g2.addVertex((Integer)1);
		// g2.addVertex((Integer)2);
		// g2.addVertex((Integer)3);
		// g2.addEdge("Edge-A", 1,3);
		// g2.addEdge("Edge-B", 2,3, EdgeType.DIRECTED);
		// g2.addEdge("Edge-C", 3, 2, EdgeType.DIRECTED);
		// g2.addEdge("Edge-P", 2,3); // A parallel edge
		// System.out.println("The graph g2 = " + g2.toString());

		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<String, MyEdge> layout = new CircleLayout( g );
		layout.setSize( new Dimension( 300, 300 ) ); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<String, MyEdge> vv = new BasicVisualizationServer<String, MyEdge>( layout );
		vv.setPreferredSize( new Dimension( 350, 350 ) ); // Sets the viewing area size
		vv.getRenderContext().setEdgeLabelTransformer(new Transformer<MyEdge, String>()
		{
			
			@Override
			public String transform( MyEdge arg0 )
			{
				// TODO Auto-generated method stub
				return arg0.getWeight();
			}
		});
		vv.getRenderContext().setVertexLabelTransformer( new Transformer<String, String>()
		{
			
			@Override
			public String transform( String arg0 )
			{
				// TODO Auto-generated method stub
				return arg0;
			}
		} );
		
		
		JFrame frame = new JFrame( "Simple Graph View" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add( vv );
		frame.pack();
		frame.setVisible( true );
	}

}

class MyEdge
{
	private String weight;

	public MyEdge(String weight)
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
