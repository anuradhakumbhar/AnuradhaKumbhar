import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class BaggageSystem {
	
	private Map<String, Node> mapCovSystemNodes = new HashMap<String, Node>();
	private Map<String, List<Connection>> mapNodesAndConnection = new HashMap<String, List<Connection>>();
	private List<DepartureDetails> listDeparture = new ArrayList<DepartureDetails>();
	private List<BagsDetails> listBags = new ArrayList<BagsDetails>();
	private String section = null;
    
	//Build input context using command line user inputs
	private void buildInputContext(String line){
    	if(line.startsWith("#conveyor_system")){
    		section = "conveyor_system";
    	}
    	else if(line.startsWith("#departure_list")){
    		section = "departure_list";
    	}
    	else if(line.startsWith("#bags_list")){
    		section = "bags_list";
    	}else{
    		if(section.equals("conveyor_system")){
    			String[] input = line.split(" ");
    			if(input.length < 3){
    				System.out.println("Number of arguments required for Conveyor system should be mininum 3.");
    				System.exit(0);
    			}else{
    				try{
    					createNodesAndConnections(input[0], input[1], Integer.parseInt(input[2].trim()));
    				}catch(NumberFormatException e){
    					System.out.println("Invalid Distance value :"+input[2] + ":"+e.getMessage());
    					System.exit(0);
    				}
    			}        			
    		}else if(section.equals("departure_list")){
    			String[] input = line.split(" ");
    			if(input.length < 4){
    				System.out.println("Number of arguments required for Departure Details should be mininum 4");
    				System.exit(0);
    			}else{
    				DepartureDetails dep = new DepartureDetails(input[0], input[1], input[2], input[3]);
    				listDeparture.add(dep);
    			}
    		}else if(section.equals("bags_list")){
    			String[] input = line.split(" ");
    			if(input.length <3){
    				System.out.println("Number of arguments required for Bags Details should be mininum 3.");
    				System.exit(0);
    			}else{
    				BagsDetails bag = new BagsDetails (input[0], input[1], input[2]);
    				listBags.add(bag);
    			}
    		}
    	}
	}

	
	private void createNodesAndConnections(String node1, String node2, int nWeight){
		if(!mapCovSystemNodes.containsKey(node1)){
			mapCovSystemNodes.put(node1, new Node(node1));
		}
		
		if(!mapCovSystemNodes.containsKey(node2)){
			mapCovSystemNodes.put(node2, new Node(node2));
		}
		
		List<Connection> listEdges = mapNodesAndConnection.get(node1);
		if(listEdges == null){
			listEdges = new ArrayList<Connection>();
		}
		listEdges.add(new Connection(mapCovSystemNodes.get(node2), nWeight));
		mapNodesAndConnection.put(node1, listEdges);
		
		
		List<Connection> listEdges1 = mapNodesAndConnection.get(node2);
		if(listEdges1 == null){
			listEdges1 = new ArrayList<Connection>();
		}
		listEdges1.add(new Connection(mapCovSystemNodes.get(node1), nWeight));
		mapNodesAndConnection.put(node2, listEdges1);
		
	}
	
	//Calculate shortest travel path for each bag
	private void calculateShortestTrvelPathAndTravelTime(){
		
		//set connections to each node before proceed.
		for(String strName : mapNodesAndConnection.keySet()){
			Node v = mapCovSystemNodes.get(strName);
			List<Connection> list = mapNodesAndConnection.get(strName);
			
			v.adjacencies = list.toArray(new Connection[list.size()]);
			mapCovSystemNodes.put(strName, v);
		}
		mapNodesAndConnection.clear();
		mapNodesAndConnection = null;
		
		//iterate over bags list
		for(BagsDetails bags : listBags){
			System.out.println("..............................................................");
			System.out.println("bag no : Entry Gate : Flight ID="+bags.getBagNumber()+":"+bags.getEntrypoint()+":"+bags.getFlightID());
			
			TravelRouteDetails objTravelRouteDetails = new TravelRouteDetails();
			objTravelRouteDetails.setBagNo(bags.getBagNumber());

			String strFinalDestNode = null;
			if(bags.getFlightID().equals("ARRIVAL")){
				strFinalDestNode = "BaggageClaim";
			}else{
				for(DepartureDetails deps : listDeparture){
					if(deps.getFlightID().equals(bags.getFlightID())){
						strFinalDestNode = deps.getFlightgGate();
						break;
					}
				}
			}
			
			//check if flight ID not found in .departure details
			if(strFinalDestNode == null || strFinalDestNode.trim().length() == 0){
				System.out.println("Flight ID Specified in Bags Details not Found in Departure list...");
				continue;
			}
			
			calculateRouteNodesAndDistance(bags.getEntrypoint(), strFinalDestNode, objTravelRouteDetails);
			System.out.println("Final output for Bag="+objTravelRouteDetails.getBagNo()+ " "+objTravelRouteDetails.getRouteNodes()+" "+objTravelRouteDetails.getTotalTravelTime());
		}
	}
	
	
	private void calculateRouteNodesAndDistance(String entryPoint, String strFinalDestNode, TravelRouteDetails objTravelRouteDetails ){
		System.out.println("Entry Point:Destination=="+entryPoint+"::"+strFinalDestNode);
		
		//reset min distance before proceed.
		for(Node v : mapCovSystemNodes.values()){
			v.minDistance = Double.POSITIVE_INFINITY;
		}
		
		calculateShortestPathForNode(mapCovSystemNodes.get(entryPoint));
        List<String> path = getShortestPathTo(mapCovSystemNodes.get(strFinalDestNode), mapCovSystemNodes.get(entryPoint));
        
        objTravelRouteDetails.setRouteNode(path);
        objTravelRouteDetails.setTraveltime(mapCovSystemNodes.get(strFinalDestNode).minDistance);
	}
	
	
	public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in)); 
        String line; 

        //read each line of input and create context .
        BaggageSystem objBaggageSys = new BaggageSystem();
        while ((line = stdin.readLine()) != null && line.length()!= 0) {
        	objBaggageSys.buildInputContext(line);
        }
        
        //calculate shortest travel path for each bag
        objBaggageSys.calculateShortestTrvelPathAndTravelTime();
    }
  
	
    private class TravelRouteDetails{
    	String Bag_Number = null;
    	List<String> listRoutNodes = null;
    	double nTotalTravelTime = 0;
    	
    	private void setBagNo(String bagNo){
    		Bag_Number = bagNo;
    	}
    	
    	private void setRouteNode(List<String> RouteNodes){
    		listRoutNodes = RouteNodes;
    	}
    	
    	private void setTraveltime(double travelTime){
    		nTotalTravelTime = travelTime;
    	}
    	
    	
    	private String getBagNo(){
    		return Bag_Number;
    	}
    	
    	private List<String> getRouteNodes(){
    		return listRoutNodes;
    	}
    	
    	private double getTotalTravelTime(){
    		return nTotalTravelTime;
    	}
    }

    private class DepartureDetails{
    	//<flight_id> <flight_gate> <destination> <flight_time>
    	String flight_id = null;
    	String flight_gate = null;
    	String destination = null;
    	String flight_time = null;
    	
    	public DepartureDetails(String flight_id, String flight_gate, String destination, String flight_time){
    		this.flight_id = flight_id;
    		this.flight_gate = flight_gate;
    		this.destination = destination;
    		this.flight_time = flight_time;
    	}
    	
    	public String getFlightID(){
    		return flight_id;
    	}
    	
    	public String getFlightgGate(){
    		return flight_gate;
    	}
    	
    	public String getFlightTime(){
    		return flight_time;
    	}
    	
    	public String getFlightDestination(){
    		return destination;
    	}
    }

    private class BagsDetails{
    	//<bag_number> <entry_point> <flight_id>
    	String bag_number = null;
    	String entry_point = null;
    	String flight_id	= null;
    	
    	public BagsDetails(String bag_number, String entry_point, String flight_id){
    		this.bag_number = bag_number;
    		this.entry_point = entry_point;
    		this.flight_id = flight_id;
    	}
    	
    	public String getFlightID(){
    		return flight_id;
    	}
    	
    	public String getBagNumber(){
    		return bag_number;
    	}
    	
    	public String getEntrypoint(){
    		return entry_point;
    	}
    }

    
    private static void calculateShortestPathForNode(Node source)
    {
    	source.minDistance = 0.;
    	PriorityQueue<Node> nodesQueue = new PriorityQueue<Node>();
    	nodesQueue.add(source);

    	while (!nodesQueue.isEmpty()) {
    		Node src = nodesQueue.poll();
    		// Visit each edge for node
    		for (Connection e : src.adjacencies)
    		{
    			//System.out.println("edge="+e.target.name+":"+e.weight);
    			Node dest = e.target;
    			double weight = e.weight;
    			double distanceTillNow = src.minDistance + weight;
    			//System.out.println("distanceThroughU="+distanceThroughU+":::"+dest.minDistance);

    			if (distanceTillNow < dest.minDistance) {
    				nodesQueue.remove(dest);
    				dest.minDistance = distanceTillNow ;
    				dest.previous = src;
    				nodesQueue.add(dest);
    			}
    		}
    	}
    }

    
    private static List<String> getShortestPathTo(Node target, Node src)
    {
    	List<String> path = new ArrayList<String>(20);
        for (Node vertex = target; vertex != null; vertex = vertex.previous){
        	path.add(vertex.name);
        	if(vertex.name.equals(src.name)){
        		break;
        	}
        }

        Collections.reverse(path);
        return path;
    }
}


class Node implements Comparable<Node>
{
    public final String name;
    public Connection[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Node previous;
    public Node(String argName) { name = argName; }
    
    public Node(Node node) {
    	this.name = node.name;
    	this.adjacencies = node.adjacencies;
    	
    }
    
    public String toString() { return name; }
    public int compareTo(Node other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

}


class Connection
{
    public final Node target;
    public final double weight;
    public Connection(Node argTarget, double argWeight)
    { target = argTarget; weight = argWeight; }
    
    public String toString() { 
    	return target.name+":"+weight; 
    }
    
}


/*
inputs used for testing

#conveyor_system
Concourse_A_Ticketing A5 5
A5 BaggageClaim 5
A5 A10 4
A5 A1 6
A1 A2 1
A2 A3 1
A3 A4 1
A10 A9 1
A9 A8 1
A8 A7 1
A7 A6 1
#departure_list
UA10 A1 MIA 08:00
UA11 A1 LAX 09:00
UA12 A1 JFK 09:45
UA13 A2 JFK 08:30
UA14 A2 JFK 09:45
UA15 A2 JFK 10:00
UA16 A3 JFK 09:00
UA17 A4 MHT 09:15
UA18 A5 LAX 10:15
#bags_list
0001 Concourse_A_Ticketing UA12
0002 A5 UA17
0003 A2 UA10
0004 A8 UA18
0005 A7 ARRIVAL
0006 A7 INVALID_FLIGHT_ID
0006 A2 UA13


output
----------------------------------------------------
..............................................................
bag no : Entry Gate : Flight ID=0001:Concourse_A_Ticketing:UA12
Entry Point:Destination==Concourse_A_Ticketing::A1
Final output for Bag=0001 [Concourse_A_Ticketing, A5, A1] 11.0
..............................................................
bag no : Entry Gate : Flight ID=0002:A5:UA17
Entry Point:Destination==A5::A4
Final output for Bag=0002 [A5, A1, A2, A3, A4] 9.0
..............................................................
bag no : Entry Gate : Flight ID=0003:A2:UA10
Entry Point:Destination==A2::A1
Final output for Bag=0003 [A2, A1] 1.0
..............................................................
bag no : Entry Gate : Flight ID=0004:A8:UA18
Entry Point:Destination==A8::A5
Final output for Bag=0004 [A8, A9, A10, A5] 6.0
..............................................................
bag no : Entry Gate : Flight ID=0005:A7:ARRIVAL
Entry Point:Destination==A7::BaggageClaim
Final output for Bag=0005 [A7, A8, A9, A10, A5, BaggageClaim] 12.0
..............................................................
bag no : Entry Gate : Flight ID=0006:A7:INVALID_FLIGHT_ID
Flight ID Specified in Bags Details not Found in Departure list...
..............................................................
bag no : Entry Gate : Flight ID=0006:A2:UA13
Entry Point:Destination==A2::A2
Final output for Bag=0006 [A2] 0.0

/*
