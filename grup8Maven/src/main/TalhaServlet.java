
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.output.ByteArrayOutputStream;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
@WebServlet("/TalhaServlet")
public class TalhaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String jsonData = null;   
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TalhaServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method pulls saved data from the database and then shows the saved data.
	 * database userName : grup8
	 * database password : netnet456
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void listData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String result="";
		System.out.println("listData");
		//db select action
		Connection conn=null;
		String url = "jdbc:mysql://ec2-52-37-99-11.us-west-2.compute.amazonaws.com:3306/demodb";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection (url,"grup8","netnet456");
			Statement stmt = conn.createStatement();
			String sqlEntry="SELECT * FROM demodb.mountain;";
			java.sql.ResultSet res =  stmt.executeQuery(sqlEntry);
			result +="label***elevation***coord1***coord2\n";
			while(res.next()){
				String label = res.getString("label"); //elevation coord1 coord2
				double elevation = res.getDouble("elevation");
				double coord1 = res.getDouble("coord1");
				double coord2= res.getDouble("coord2");
				result+=label+"***"+elevation+"***"+coord1+"***"+coord2+"\n";
				//aoeu***aoeuaoe***oaeuoaeu***aoeu\n
			}
			
			//convert to format
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().write(0);
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			response.getWriter().write(0);
			return;
		}
		System.out.println(result);
		response.getWriter().write(result);
		
	}
	
	/**
	 * 
	 * This method finds the selected mountains.
	 * After that, sends the selected mountains to the table in the database.
	 * The data is sent in the format of label,elevation,coord1,coord2, where coord1 is latitude and coord2 is longtitude
	 * This method does not clear the database before sending the data.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void selectData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("selectData");
		String input = request.getParameter("input"); //TODO: must be a list
		String[] ids = input.split(" ");
		ArrayList<mountain> selectedMts = new ArrayList<mountain>();
		for (String idStr : ids) {
			int id = Integer.parseInt(idStr);
			selectedMts.add(sortedMountains.get(id));
		}
		Connection conn=null;
		String url = "jdbc:mysql://ec2-52-37-99-11.us-west-2.compute.amazonaws.com:3306/demodb";
		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection (url,"grup8","netnet456");
			System.out.println("Connected");
			Statement stmt = conn.createStatement();
			String sqlEntry="INSERT INTO demodb.mountain VALUES";

			for (mountain mt : selectedMts) {

				sqlEntry +="('"+ mt.label+ "', "+ mt.elevation+ ", "+ mt.coor1+ ", "+ mt.coor2+ "),";
			}
			sqlEntry=sqlEntry.substring(0, sqlEntry.length()-1);
			sqlEntry += ";";
			stmt.execute(sqlEntry);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().write(0);
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			response.getWriter().write(0);
			return;
		}
	}


	/**This method gets the data from wikidata query.
	 * According to the given input by user it makes a semantic search and creates a list
	 * sortedMountains to store mountains in a sorted matter.
	 * Sends the sorted data to jsp to create the table and display
	 * The data is extracted with jena library.
	 * The returned data is the mountains with elevation higher than 7000
	 * This method always pulls the same data since the amaount of the mountains is limited.
	 * Maxiumum hundred mountains are pulled from query-wikidata.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void queryData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		mountains =new ArrayList<mountain>();
		/**
		 * this ArrayList holds a pair, the first parameter is ...
		 */
		searchResult= new ArrayList<myPair>();

		if(mountains.size()<5){
			
		String s1 ="PREFIX wikibase: <http://wikiba.se/ontology#>\n"+
				"PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX bd: <http://www.bigdata.com/rdf#>"+
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
				"PREFIX wd: <http://www.wikidata.org/entity/>\n"+




					"#Mountains over 7000 elevation  \n"+
					"SELECT ?subjLabel ?label ?coord ?elev ?continentLabel \n"+ 
					"WHERE\n"+
					"{\n"+
					"?subj wdt:P2044 ?elev filter(?elev >7000) .	\n"+
					"?subj wdt:P625 ?coord .\n"+
					"?subj wdt:P30 ?continent .\n"+
					"SERVICE wikibase:label { bd:serviceParam wikibase:language \"en,zh\" . ?subj rdfs:label ?label }\n"+ 
					"}\n"+
					"LIMIT 100"	;


		System.out.println("Getting the data");
		Query query = QueryFactory.create(s1); 
		QueryExecution qExe =	 QueryExecutionFactory.sparqlService( "https://query.wikidata.org/sparql", query );
		ResultSet results = qExe.execSelect();
		System.out.println("got the data");

		double[] coords =new double[2];
		while (results.hasNext()) {
			QuerySolution binding = results.nextSolution();
			coords= findCoordinates(binding.get("?coord").toString());
			mountain currentMountain =new mountain(findLabel(binding.get("?label").toString()), findElevation(binding.get("?elev").toString()) , coords[0],coords[1] ) ;
			mountains.add(currentMountain);


		}
		}
		String inputTerm = request.getParameter("input");

		getSemanticResult(inputTerm);

		createSortedList();
		String resultData= "Label***Elevation***Coor1***Coor2\n";

		for(int i = 0 ; i  < sortedMountains.size();i++){
			resultData+=sortedMountains.get(i).label+"***"+sortedMountains.get(i).elevation+"***"+sortedMountains.get(i).coor1+"***"+sortedMountains.get(i).coor2+"\n";
		}

		response.getWriter().write(resultData);
	}
	/**
	 * This method returns the mountains that are selected
	 * @param filter
	 * @return
	 */
	private ArrayList<mountain> getSelectedMountains(String filter) {
		String[] idStrings = filter.split(" ");
		ArrayList<mountain> result = new ArrayList<mountain>();
		for (String idStr : idStrings) {
			
			result.add(mountains.get(Integer.parseInt(idStr)));
		}
		return result;
	}
	/**
	 * This method is used to clear data when the user hits delete this is invoked
	 * @param request
	 * @return
	 */
	public String clearData(HttpServletRequest request){
		String filter = request.getParameter("input");
		if (filter.equals("undefined")) {
			return "0";
		}
		ArrayList<mountain> checkedMountains = getSelectedMountains(filter);
		java.sql.Connection connection;
		String url = "jdbc:mysql://ec2-52-37-99-11.us-west-2.compute.amazonaws.com:3306/demodb";
		try {

			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection (url,"grup8","netnet456");
			java.sql.Statement stmt = connection.createStatement();
			for (mountain m : checkedMountains) {
				String sqlStmt = "DELETE FROM demodb.mountain WHERE label = \"" + m.label + "\";";
				stmt.executeUpdate(sqlStmt);
			}
			System.out.println("Deleted");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return "0";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return "0";
		}			
		return "1";
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestType = request.getParameter("type");
		System.out.println(requestType+"-----");
		if (requestType == null) {
			request.getRequestDispatcher("/WEB-INF/TalhaHome.jsp").forward(request, response);
		} else if (requestType.equals("queryData")) {

			queryData(request, response);

		}else if (requestType.equals("selectData")) {
			selectData(request, response);
		}else if (requestType.equalsIgnoreCase("deleteData")){
			response.getWriter().write(clearData(request));
		}
		else if (requestType.equals("listData")) {
			listData(request, response);
		} else {
			System.out.println("Unexpected request type: " + requestType);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);


	}
	/**
	 * This class is to store a pair
	 * The first element in the pair is the label of a mountain
	 * Second element - the amount of difference(The greater the difference, the less related the mountain is)
	 * @author Talha
	 *
	 */
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
	/**
	 * The class to store information about a mountain
	 * label is the name of the mountain
	 * 
	 * @author KNOCKOUT
	 *
	 */
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


	/**
	 * Minumum of three numbers
	 * @param a the first number
	 * @param b the second number
	 * @param c the third number	
	 * @return
	 */
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
	/**
	 * Uses regex to extract the elevation of a mountain
	 * @param s 
	 * @return
	 */
	public static double findElevation(String s){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(s);
		if(m.find()){
			return Double.parseDouble(m.group());
		}
		return 999999;

	}
	/**
	 * Finds the label-name of the mountain
	 * This method basically finds the string up to the char '@'
	 * @param s
	 * @return
	 */
	public static String findLabel(String s){
		Pattern p = Pattern.compile(".+(?=@)");
		Matcher m = p.matcher(s);
		if(m.find()){
			return m.group();
		}
		return "unknown";
	}
	/**
	 * Uses Regex to find the coordinates of the mountain.
	 * @param s
	 * @return 
	 */
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
	/**
	 * Distance between two mountains
	 * @param c1 mountain1 latitude
	 * @param c2 mountain1 longtitude
	 * @param c3 mountain2 latitude
	 * @param c4 mountain2 longtitude
	 * @return
	 */
	public static double distance(double c1,double c2,double c3,double c4){
		return Math.sqrt(Math.pow(c1-c3, 2)+Math.pow(c2-c4, 2))*50;
	}
	/**
	 * Prints the given mountain 
	 */
	public static String printMountain(mountain m){
		return (m.label+"    "+m.elevation+"   "+m.coor1+"     "+m.coor2);
	}
	/**
	 * Creates sortedList, sorted by the input given by user
	 */
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



	/**
	 * Checks whether the given name exists in the data
	 * @return
	 */
	public static boolean doesExist(String name){
		for(int i = 0 ; i < mountains.size();i++){
			if(mountains.get(i).label== name){
				return true;
			}
		}
		return false;
	}
	/***
	 * Makes semantic search with the given name.
	 * The closer mountains and the as high as mountains are placed to the top in the search.
	 * @param name
	 */
	public static void getSemanticResult(String name){

		//Tries to match the name to the closest name
		if(!doesExist(name)){
			int bestIndex= 0;//Holds the index for the closest String
			int minSteps = 99999;// Amount of steps to get the other string from the "name"
			for(int i = 0 ; i < mountains.size();i++){
				int currentSteps = computeLevenshteinDistance(name, mountains.get(i).label);
				if(currentSteps<minSteps){
					minSteps=currentSteps;
					bestIndex=i;
				}
			}
			name=mountains.get(bestIndex).label;
		}
		
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
			myPair currentPair = new myPair(current.label,currentDifference);
			searchResult.add(currentPair);

		}
		Collections.sort(searchResult);


	}

}





