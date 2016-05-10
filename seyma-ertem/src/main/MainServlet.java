
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import java.sql.*;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.*;
import webapp.Data;



/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/")
public class MainServlet extends HttpServlet {

	
	
	private static final long serialVersionUID = 1L;
	ArrayList<Data> data;	
	ArrayList<Data> filteredData;
	Connection conn = null;
	Statement stmt = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		

		data =new ArrayList<Data>();
		String queryString ="PREFIX wd: <http://www.wikidata.org/entity/>\n" +
				"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
				"PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
				"PREFIX p: <http://www.wikidata.org/prop/>\n" +
				"PREFIX ps: <http://www.wikidata.org/prop/statement/>\n" +
				"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>\n" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
				"PREFIX bd: <http://www.bigdata.com/rdf#>\n" +

					"#Population in Europe after 1960\n" +
					"SELECT  ?objectLabel    (YEAR(?date) as ?year) \n" +
					"?population     (?objectLabel as ?Location)\n" +
					"WHERE\n" +
					"{\n" +
					"?object	wdt:P31 wd:Q185441 \n" +
					";	p:P1082 ?populationStatement .\n" +
					"?populationStatement    ps:P1082 ?population\n" +
					";	pq:P585 ?date .\n" +
					"SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\" }  \n" +              
					"FILTER (YEAR(?date) >= 1960)\n" +
					"} \n" +
					"ORDER BY ?objectLabel ?year\n";




		Query query = QueryFactory.create(queryString); 
		QueryExecution qExec = QueryExecutionFactory.sparqlService( "https://query.wikidata.org/sparql", query );

		ResultSet results = qExec.execSelect();

		int count = 0;
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			Data currentData =new Data(count,findString(sol.get("?objectLabel").toString()), findInteger(sol.get("?year").toString()) , findInteger(sol.get("?population").toString()) ) ;
			data.add(currentData);
			count++;

		}

		request.setAttribute("searchWord", "");
		request.setAttribute("cityData", data);
		request.getRequestDispatcher("/index2.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		filteredData = new ArrayList<Data>();

		try{
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			    conn = DriverManager.getConnection("jdbc:mysql://ec2-52-37-99-11.us-west-2.compute.amazonaws.com:3306/demodb","grup8", "netnet456");
				stmt = conn.createStatement();
				String[] results = request.getParameterValues("cities");
				for (int i = 0; i < results.length; i++) {
					int index = Integer.parseInt(results[i]);
					String sql = "INSERT INTO saved " +
			                   "VALUES ("+data.get(index).getID()+ ", '"+data.get(index).getName() +"', '"+ data.get(index).getYear()+"', "+data.get(index).getPopulation() +")";
					stmt.executeUpdate(sql);
					
				}
				
			} catch (SQLException ex) {
			    // handle any errors
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			} catch (InstantiationException e) {
				System.out.println("InstantiationException: " + e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.out.println("IllegalAccessException: " + e.getMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNotFoundException: " + e.getMessage());
				e.printStackTrace();
			}

			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				e.printStackTrace();
			}
			/*
			if(!savedData.isEmpty()){
				request.setAttribute("savedData", savedData);
				request.getRequestDispatcher("/savedResults.jsp").forward(request, response);
			}else
				
				*/
			this.doGet(request, response);

		}catch(NullPointerException e){
			String searchWord =  request.getParameter("search");
			if(searchWord != ""){
				editList(searchWord);
				request.setAttribute("searchWord", searchWord);
				request.setAttribute("cityData", filteredData);
				request.getRequestDispatcher("/index2.jsp").forward(request, response);
			}
			else{
				this.doGet(request, response);
			}
		}


	}


	private void editList(String searchWord) {
		try{
			int number = Integer.parseInt(searchWord);
			int year = -1,population = -1;
			if(number > 1900 && number < 2020)
				year = number;
			else
				population = number;

			if(year != -1){
				for(Data currentData: data){
					int diff = Math.abs(currentData.getYear() - year);
					currentData.yearDiff = diff;
					filteredData.add(currentData);					
				}
				Collections.sort(filteredData,new Comparator<Data>() {
					public int compare(Data d1, Data d2) {
						return d1.yearDiff - d2.yearDiff;
					}
				});
			}else if(population != -1){
				for(Data currentData: data){
					int diff = Math.abs(currentData.getPopulation() - population);
					currentData.popuDiff = diff;
					filteredData.add(currentData);					
				}
				Collections.sort(filteredData,new Comparator<Data>() {
					public int compare(Data d1, Data d2) {
						return d1.popuDiff - d2.popuDiff;
					}
				});
			}

		}catch(NumberFormatException e){
			int min = 99999;
			int bestIndex=0;
			int currentPopu = 0;
			for(Data currentData: data){
				int current = computeLevenshteinDistance(currentData.getName(), searchWord);
				if(current < min){
					min = current;
					searchWord=currentData.getName();
					currentPopu = currentData.getPopulation();
				}
			}
			int sum = 0;
			int count = 0;
			for(Data currentData: data){
				if(currentData.getName().equalsIgnoreCase(searchWord)){
					sum += currentData.getPopulation();
					count++;
				}				
			}
			int mean = sum/count;
			for(Data currentData: data){
				int diff = Math.abs(currentData.getPopulation() - mean);
				if(currentData.getName().equalsIgnoreCase(searchWord))
					currentData.popuDiff = 0;
				else
					currentData.popuDiff = diff;
				filteredData.add(currentData);					
			}
			Collections.sort(filteredData,new Comparator<Data>() {
				public int compare(Data d1, Data d2) {
					return d1.popuDiff - d2.popuDiff;
				}
			});
			
		}
		

	}
	private static int minimum(int a, int b, int c) {                            
		return Math.min(Math.min(a, b), c);                                      
	}                                                                            
	/**
	 * Calculates how many steps it will take to get the second string from the first one.
	 *  The less return value the more the strings are related to each other                                                                       
	 */
	public static int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {      
		int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];        

		for (int i = 0; i <= lhs.length(); i++)                                 
			distance[i][0] = i;                                                  
		for (int j = 1; j <= rhs.length(); j++)                                 
			distance[0][j] = j;                                                  

		for (int i = 1; i <= lhs.length(); i++)                                 
			for (int j = 1; j <= rhs.length(); j++)                             
				distance[i][j] = minimum(                                        
						distance[i - 1][j] + 1,                                  
						distance[i][j - 1] + 1,                                  
						distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

		return distance[lhs.length()][rhs.length()];                           
	}


	public static int findInteger(String s){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(s);
		if(m.find()){
			return Integer.parseInt(m.group());
		}
		return -1;

	}
	
	public static String findString(String s){
		int l = s.indexOf("@");
		s = s.substring(0,l);
		return s;
	}



}
