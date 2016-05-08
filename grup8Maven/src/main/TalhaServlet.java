

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import java.io.File;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.jena.Jena;
import org.w3c.dom.Element;


/**
 * Servlet implementation class TalhaServlet
 */
@WebServlet("/")
public class TalhaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TalhaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	


		
		mountains =new ArrayList<mountain>();
		searchResult= new ArrayList<myPair>();
		String s1 ="PREFIX wikibase: <http://wikiba.se/ontology#>\n"+
				"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX bd: <http://www.bigdata.com/rdf#>"+
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
				"PREFIX wd: <http://www.wikidata.org/entity/>\n"+
				
					
					
				
					"#Mountains over 7000 elevation  \n"+
					"SELECT ?subj ?label ?coord ?elev ?continent ?globe \n"+ 
					"WHERE\n"+
					"{\n"+
						"?subj wdt:P2044 ?elev filter(?elev >7000) .	\n"+
						"?subj wdt:P625 ?coord .\n"+
						"?subj wdt:P30 ?continent .\n"+
						//"?subj wikibase:geoGlobe ?globe FILTER ( ?globe = wd:Q2 )\n"+
						"SERVICE wikibase:label { bd:serviceParam wikibase:language \"en,zh\" . ?subj rdfs:label ?label }\n"+ 
					"}\n"+
					"LIMIT 100"	;
		
		
		
		  
	      Query query = QueryFactory.create(s1); 
	      QueryExecution qExe = QueryExecutionFactory.sparqlService( "https://query.wikidata.org/sparql", query );
	     
	      ResultSet results = qExe.execSelect();
	     double[] coords =new double[2];
	      while (results.hasNext()) {
	  		QuerySolution binding = results.nextSolution();
	  		
	  		coords= findCoordinates(binding.get("?coord").toString());
	  		mountain currentMountain =new mountain(findLabel(binding.get("?label").toString()), findElevation(binding.get("?elev").toString()) , coords[0],coords[1] ) ;
	  		mountains.add(currentMountain);
	  		
	  		
	  	}
	      /*
	      for(int i = 0 ; i  < mountains.size(); i++){
	    	  System.out.println(mountains.get(i).label+" "+mountains.get(i).elevation + " " + mountains.get(i).coor1+" "+mountains.get(i).coor2);
	    	  
	      }
	      */
	      getSemanticResult("K2");
	      
	     // printResult();
	      createSortedList();
	      
	      
	   
	      
	      PrintWriter out = response.getWriter();
	      
	      out.append("Search: <input type=\"text\" name=\"Search\" value=\"write a mountain\"><br>");
	     out.append("<table border=\"1\" style=\"width:100%\">");
	     
	      out.append("<tr><td>Click</td><td>LABEL</td><td>ELEVATION</td><td>LATITUDE</td><td>LONGTITUDE</td></tr>");
	      for(int i = 0; i < sortedMountains.size();i++){
	    	  out.append("<tr><td><input type=\"checkbox\" name=\"label\" value=\""+i+"\"></td><td>"+sortedMountains.get(i).label+"</td><td>"+sortedMountains.get(i).elevation+"</td><td>"+sortedMountains.get(i).coor1+"</td><td>"+sortedMountains.get(i).coor2+"</td></tr>");
	    	  		
	    	  //response.getWriter().append("<p>" + sortedMountains.get(i).label+"    "+sortedMountains.get(i).elevation+"   "+sortedMountains.get(i).coor1+"     "+sortedMountains.get(i).coor2+"</p>");
	      }
	      
	      out.append("</table>");
	      out.append("<input type=\"submit\" value=\"Save\">");
	    
	      
		
	}




	
	private static class myPair implements Comparable<myPair>{
		double difference;
		String label;
		public myPair(String label,double difference) {
			this.difference=difference;
			this.label=label;
			
			// TODO Auto-generated constructor stub
		}
		public int compareTo(myPair pair) {
			
			 
			
			//ascending order
			return (int)this.difference - (int)pair.difference;
			
			
			
		}
	}
	
	public static class mountain{
		String label;
		double elevation,coor1,coor2;
		public mountain(String label,double elevation,double coor1,double coor2 ) {
			this.label =label;
			this.elevation =elevation;
			this.coor1=coor1;
			this.coor2=coor2;
			
			
			// TODO Auto-generated constructor stub
		}
	}
public static ArrayList<mountain> mountains;	
public static ArrayList<myPair> searchResult;
public static ArrayList<mountain> sortedMountains; 
	
public static double findElevation(String s){
	Pattern p = Pattern.compile("\\d+");
	Matcher m = p.matcher(s);
	if(m.find()){
		return Double.parseDouble(m.group());
	}
	return 999999;
	
}
public static String findLabel(String s){
	Pattern p = Pattern.compile(".+(?=@)");
	Matcher m = p.matcher(s);
	if(m.find()){
		return m.group();
	}
	return "unknown";
}
public static double[] findCoordinates(String s){
	Pattern p = Pattern.compile("[\\d|\\.]+");
	Matcher m =p.matcher(s	);
	double[] coords = new double[2];
	int counter = 0;
	while(m.find() && counter <2){
		coords[counter]=Double.parseDouble(m.group());
		counter++;
	}
	
	return coords;
}

public static double distance(double c1,double c2,double c3,double c4){
	return Math.sqrt(Math.pow(c1-c3, 2)+Math.pow(c2-c4, 2))*50;
}
public static void printMountain(int index){
	System.out.println(mountains.get(index).label+"    "+mountains.get(index).elevation+"   "+mountains.get(index).coor1+"     "+mountains.get(index).coor2);
}

public static void createSortedList(){
	sortedMountains = new ArrayList<TalhaServlet.mountain>();
	for(int i = 0 ; i < searchResult.size();i++){
		for(int j = 0 ; j <mountains.size();j++){
			if(mountains.get(j).label.equals(searchResult.get(i).label)){
				sortedMountains.add(mountains.get(j));
				break;
			}
		}
	}
}
public static void printResult(){
	for(int i = 0 ; i < searchResult.size();i++){
		for(int j = 0 ; j <mountains.size();j++){
			if(mountains.get(j).label.equals(searchResult.get(i).label)){
				
				printMountain(j);
				break;
			}
		}
	}
}
public static boolean canMatch(String name){
	
	
	return false;
}

public static void getSemanticResult(String name){
	String lable = name;
	double elevation=0;
	double coor1=0;
	double coor2=0;
	
	//find the information of the given name
	for(int i = 0 ; i <mountains.size(); i++){
		if(mountains.get(i).label.equalsIgnoreCase(name)){
			elevation=mountains.get(i).elevation;
			coor1=mountains.get(i).coor1;
			coor2=mountains.get(i).coor2;
			break;
		}
	}
	for(int i = 0 ; i < mountains.size();i++){
		mountain current = mountains.get(i);
		double currentDifference = Math.abs(current.elevation-elevation)+distance(coor1, coor2, current.coor1,current.coor2 );
		//System.out.println(current.label +"  "+ currentDifference);
		myPair currentPair = new myPair(current.label,currentDifference);
		searchResult.add(currentPair);
		
		
		
	}
	Collections.sort(searchResult);
	
	
}

public static void main(String [] args){
	
	
	mountains =new ArrayList<mountain>();
	searchResult= new ArrayList<myPair>();
	String s1 ="PREFIX wikibase: <http://wikiba.se/ontology#>\n"+
			"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"+
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
			"PREFIX bd: <http://www.bigdata.com/rdf#>"+
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
			"PREFIX wd: <http://www.wikidata.org/entity/>\n"+
			
				
				
			
				"#Mountains over 7000 elevation  \n"+
				"SELECT ?subj ?label ?coord ?elev ?continent ?globe \n"+ 
				"WHERE\n"+
				"{\n"+
					"?subj wdt:P2044 ?elev filter(?elev >7000) .	\n"+
					"?subj wdt:P625 ?coord .\n"+
					"?subj wdt:P30 ?continent .\n"+
					//"?subj wikibase:geoGlobe ?globe FILTER ( ?globe = wd:Q2 )\n"+
					"SERVICE wikibase:label { bd:serviceParam wikibase:language \"en,zh\" . ?subj rdfs:label ?label }\n"+ 
				"}\n"+
				"LIMIT 100"	;
	
	
	
	  
      Query query = QueryFactory.create(s1); 
      QueryExecution qExe = QueryExecutionFactory.sparqlService( "https://query.wikidata.org/sparql", query );
     
      ResultSet results = qExe.execSelect();
     double[] coords =new double[2];
      while (results.hasNext()) {
  		QuerySolution binding = results.nextSolution();
  		
  		coords= findCoordinates(binding.get("?coord").toString());
  		mountain currentMountain =new mountain(findLabel(binding.get("?label").toString()), findElevation(binding.get("?elev").toString()) , coords[0],coords[1] ) ;
  		mountains.add(currentMountain);
  		
  		
  	}
      /*
      for(int i = 0 ; i  < mountains.size(); i++){
    	  System.out.println(mountains.get(i).label+" "+mountains.get(i).elevation + " " + mountains.get(i).coor1+" "+mountains.get(i).coor2);
    	  
      }
      */
      getSemanticResult("Khan Tengri");
      
      //printResult();
      createSortedList();
      
    
      
	
}
}

				

