

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;


/**
 * Servlet implementation class BizimServlet
 */
public class BizimServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String lasQuery =
    		"PREFIX wd: <http://www.wikidata.org/entity/> "
    		+	"PREFIX wdt: <http://www.wikidata.org/prop/direct/> "
    		+	"PREFIX wikibase: <http://wikiba.se/ontology#> "
    		+	"PREFIX p: <http://www.wikidata.org/prop/> "
    		+	"PREFIX ps: <http://www.wikidata.org/prop/statement/> "
    		+	"PREFIX pq: <http://www.wikidata.org/prop/qualifier/> "
    		+	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
    		+	"PREFIX bd: <http://www.bigdata.com/rdf#> "

    		+	"SELECT ?link ?linkLabel ?ageOfDeath ?ordinal "
    		+	"WHERE "
    		+	"{ "
    		+	"?link wdt:P31 wd:Q5 "
    		+	"; p:P39 ?guidPositionHeldPope . "
    		+	"	OPTIONAL { ?link wdt:P569 ?dateOfBirth } "
    		+	"	OPTIONAL { ?link p:P39 ?pope. "
    		+	"               ?pope pq:P1545 ?ordinal. "
    		+	             "} "
    		+	"	OPTIONAL { ?link wdt:P570 ?dateOfDeath } "
    		+	"	?guidPositionHeldPope ps:P39 wd:Q19546; "
    		+	"    pq:P580 ?startTime . "

    		+	"	BIND(YEAR(?dateOfDeath)-YEAR(?dateOfBirth) as ?ageOfDeath ) "
    		+	"	SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" } "
    		+	"} "
    		+	"ORDER BY DESC(?startTime) ";
    private String searchText ; 
    private ArrayList<Pope_Z> rawPopes;		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BizimServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		rawPopes = new ArrayList<Pope_Z>();
		response.setContentType("text/html");
		
		Query curQuery = QueryFactory.create(lasQuery); 
	    QueryExecution qExec = QueryExecutionFactory.sparqlService( "https://query.wikidata.org/sparql", curQuery);
	    ResultSet resultingList = qExec.execSelect();
	    
	    while( resultingList.hasNext() ){
	    	QuerySolution currentRow = resultingList.nextSolution();
	    	int aod=0,ord=0;
	  		String name = "N/A";
	  	
	  		if( currentRow.get("?ageOfDeath") != null )  
	  			aod = currentRow.get("?ageOfDeath").asLiteral().getInt();
	  		
	  		if( currentRow.get("?ordinal") != null )  
	  			ord = currentRow.get("?ordinal").asLiteral().getInt();
	  		
	  		if( currentRow.get("?linkLabel") != null)
	  			name = currentRow.get("?linkLabel").asLiteral().getString();
	  		
	  		Pope_Z imaNoPope = new Pope_Z(ord, aod , name);
	  		rawPopes.add(imaNoPope);
	    }
	    
		PrintWriter out = response.getWriter();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		out.println("<html>");
		out.println("<title>MAIN PAGE</title>");
		out.println("<form method='post' action='BizimServlet'>"
				+ "<input type='text' name='searchQuery'>"
				+ "<input type='submit' name='submitButton' value='Search'>");
		out.println("</form>");
		//String q = BERK ;
		
		
		out.println("</html>") ;
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String searchText = request.getParameter("searchQuery");
		System.out.println(searchText);
		System.out.println(isInDeList(searchText));
		if(isInDeList(searchText))
			rawPopes = processedPopes(getPapa(searchText));
		//display rawPopes.
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<html>");
		out.println("<title>MAIN PAGE</title>");
		
		out.println("<table style=\"width:100%\">");
		
		out.println("<tr>");
		out.println("<th>Popal Name</th>");
	    out.println("<th>Age of Death</th>"); 
	    out.println("<th>Ordinal</th>");
	    out.println("</tr>");
		
	    for(int i=0 ; i<rawPopes.size() ; ++i){
				Pope_Z curPope = rawPopes.get(i);
				out.println("<tr>");
				out.println("<td>"+curPope.getName()+"</td>");
				int age=curPope.getAgeOfDeath();
				if( curPope.getOrdinal() >=265 )	out.println("<td> ALIVE </td>");
				else if(age<=0)	out.println("<td> UNKNOWN </td>");
				else out.println("<td>"+curPope.getAgeOfDeath()+"</td>");
				out.println("<td>"+curPope.getOrdinal()+"</td>");
				out.println("</tr>");
			}
		out.println("</table>");
		
		out.println("</html>") ;
		
	}
	private boolean isInDeList(String searchText){
		for(Pope_Z i:rawPopes){
			if(i.getName().equals(searchText) )
				return true;
		}
		return false;
	}
	private Pope_Z getPapa(String papaName){
		for(Pope_Z i:rawPopes){
			if(i.getName().equals(papaName) )
				return new Pope_Z(i.getOrdinal(),i.getAgeOfDeath(),i.getName());
		}
		return null;
	}
	private int papaDist(Pope_Z q, Pope_Z p){
		int rawDist = 0 ;
		if( !(q.getName().equals(p.getName())) )	rawDist += 1000;
		rawDist += 3 * Math.abs( ( q.getOrdinal()-p.getOrdinal() ) );
		rawDist += Math.abs( q.getAgeOfDeath() - p.getAgeOfDeath() ); 
		return rawDist;
	}
	private ArrayList<Pope_Z> processedPopes(Pope_Z currentPapa){
		ArrayList<Pope_Z> proPapas = new ArrayList<Pope_Z>();
		if(currentPapa == null){
			System.out.println("PAPYOK");
			return new ArrayList<Pope_Z>();
		}
		while(rawPopes.size()>0){
			int minIndex=-1,minDist = 10000;
			for(int i=0 ; i<rawPopes.size(); ++i){
				int curDist = papaDist(currentPapa, rawPopes.get(i));
				if( curDist < minDist ){
					minIndex = i ;
					minDist = curDist;
				}
			}
			proPapas.add( rawPopes.get(minIndex) );
			rawPopes.remove(minIndex);
		}
		return proPapas;
	}
}
