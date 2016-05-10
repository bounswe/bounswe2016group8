<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,webapp.Data"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Saved Results</title>
</head>
<body>
	<form method="GET" action="MainServlet">
		<input type="submit" value="Return">
	</form>


	<form action="MainServlet" method="POST">
		<table border="1" style="width: 100%">

			<tr>
				<td>City</td>
				<td>Year</td>
				<td>Population</td>
			</tr>
			<%
            @SuppressWarnings("unchecked") 
            ArrayList<Data> savedData = (ArrayList<Data>)request.getAttribute("savedData");
			if(savedData.size() != 0){
	 			for (int i=0; i<savedData.size(); i++) { %>
				<tr>
					<td><%= savedData.get(i).getName() %></td>
					<td><%= savedData.get(i).getYear() %></td>
					<td><%= savedData.get(i).getPopulation() %></td>
				</tr>
				<%
	    		}
	 		}%>
		</table>
	</form>

</body>
</html>