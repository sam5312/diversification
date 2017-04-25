package div.graphbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class ResultSet1 {

	private HashMap<String,HashMap<String,Integer>> hashtagToHashtagMap = new HashMap<>();

	public HashMap<String, HashMap<String, Integer>> getHashtagToHashtagMap()
	{
		return hashtagToHashtagMap;
	}


	public void setHashtagToHashtagMap( HashMap<String, HashMap<String, Integer>> hashtagToHashtagMap )
	{
		this.hashtagToHashtagMap = hashtagToHashtagMap;
	}


	public void updateHashtagToHashtagMap(ArrayList<String> tokens){
		
		for (int x = 0;  x < tokens.size() - 1; x++) {
			
			for (int y = x+1;  y < tokens.size(); y++) {
				
				String dest_word = tokens.get(x);
				String source_word = tokens.get(y);
				String key;
				String value;
				if(source_word.compareTo(dest_word) <=0) {
					key = source_word;
					value = dest_word;
				} else {
					key = dest_word;
					value = source_word;
				}
				if(hashtagToHashtagMap.containsKey(key)){
					Integer previousCount = hashtagToHashtagMap.get(key).get(value);
					if(previousCount == null){
						previousCount = 0;
					}
					hashtagToHashtagMap.get(key).put(value,previousCount+1);
				} else {
					HashMap<String, Integer> map = new HashMap<>();
					map.put(value, 1);
					hashtagToHashtagMap.put(key, map);
				}

			}
		}
	}


	public UndirectedGraph<RapidVertex, RapidEdge> generateWordWinClusters( ) {
		UndirectedGraph<RapidVertex, RapidEdge> graph = new UndirectedSparseGraph<RapidVertex, RapidEdge>();
		HashMap<String, RapidVertex> verticesMap = new HashMap<>();
		for (Map.Entry<String, HashMap<String, Integer>> entry : hashtagToHashtagMap.entrySet()) {
			RapidVertex source;
			if (verticesMap.containsKey(entry.getKey())) {
				source = verticesMap.get(entry.getKey());
			} else {
				source = new RapidVertex(entry.getKey());
				verticesMap.put(entry.getKey(), source);
			}
			for (Map.Entry<String, Integer> edgeInfo : entry.getValue().entrySet()) {
				RapidVertex dest;
				if (verticesMap.containsKey(edgeInfo.getKey())) {
					dest = verticesMap.get(edgeInfo.getKey());
				} else {
					dest = new RapidVertex(edgeInfo.getKey());
					verticesMap.put(edgeInfo.getKey(), dest);
				}
				if ( edgeInfo.getValue() > CooccuranceGraph.coocc_count) //only words with more than 3 co occurence
				{
				RapidEdge edge = new RapidEdge("", source, dest);
				edge.setWeight(edgeInfo.getValue());
				graph.addEdge(edge, source, dest);
				}
			}
		}
		return graph;
	}



}


class RapidEdge {
	private String name;
	public int weight = 1;
	private RapidVertex sourceVertex;
	private RapidVertex destinationVertex;

	public RapidEdge(String n, RapidVertex source, RapidVertex dest) {
		name = n;
		sourceVertex = source;
		destinationVertex = dest;
	}

	public String toString() {
		if (weight > 1) {
			return name + " (" + weight + ")";
		}
		return name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public RapidVertex getSourceVertex() {
		return sourceVertex;
	}

	public void setSourceVertex(RapidVertex sourceVertex) {
		this.sourceVertex = sourceVertex;
	}

	public RapidVertex getDestinationVertex() {
		return destinationVertex;
	}

	public void setDestinationVertex(RapidVertex destinationVertex) {
		this.destinationVertex = destinationVertex;
	}


}
 class RapidVertex {

	private String name;
	private String label;
	public int state; //1 == core, 2==boarder
	public Set<String> clusterIDs = new HashSet<String>();

	public RapidVertex(String n) {
		name = n;
	}

	public String toString() {
		return name;
	}



	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

