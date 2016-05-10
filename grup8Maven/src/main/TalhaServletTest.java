import static org.junit.Assert.*;

import org.junit.Test;


public class TalhaServletTest {

	@Test
	public void testStringMatch(){
		String mountain1 = "Mount Everest";
		String mountain2 = "K2";
		String input = "Mount Everrddmsd";
		String result = "";
		int n1 = TalhaServlet.computeLevenshteinDistance(mountain1, input);
		int n2= TalhaServlet.computeLevenshteinDistance(mountain2, input);
		if(n1<n2){
			result = mountain1; 
		}else {
			result = mountain2;
		}
		assertEquals("Mount Everest", result);
		mountain1 = "Mount Everest";
		mountain2 = "K2";
		input = "K2sws";
		result = "";
		n1 = TalhaServlet.computeLevenshteinDistance(mountain1, input);
		n2= TalhaServlet.computeLevenshteinDistance(mountain2, input);
		if(n1<n2){
			result = mountain1; 
		}else {
			result = mountain2;
		}
		assertEquals("K2", result);
	}
	
	@Test
	public void testFindElevation() {
		double result = TalhaServlet.findElevation("123@12321");
		assertEquals(123, (int)result);
		
	}

	@Test
	public void testFindLabel() {
		String name = TalhaServlet.findLabel("Mount Everest@asdwq");
		assertEquals("Mount Everest", name);
		
	}

	@Test
	public void testFindCoordinates() {
		double[] result = TalhaServlet.findCoordinates("123.123213@http");
		assertEquals(123, (int)result[0]);
		
	}

	@Test
	public void testDistance() {
	
		double result = TalhaServlet.distance(0, 0, 0, 0);
		assertEquals(0, (int)result);
		
	}

}
