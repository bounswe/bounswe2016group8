<%@page contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*,webapp.Data"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
<title>Home Page</title>
</head>

<body>

	<form method="POST" action="MainServlet">
		Search Word: <input type="text" name="search"
			value="<%=request.getAttribute("searchWord")%>" /> <input
			type="submit" value="Search" />
	</form>
	<form method="POST" action="SaveServlet">
		<input type="submit" value="List Saved">
	</form>


	<form action="MainServlet" method="POST">
		<table border="1" style="width: 100%">
			<input type="submit" value="Save Selected">
			<tr>
				<td>Select</td>
				<td>City</td>
				<td>Year</td>
				<td>Population</td>
			</tr>
			<%
				@SuppressWarnings("unchecked")
				ArrayList<Data> cityData = (ArrayList<Data>) request.getAttribute("cityData");
				if (cityData.size() != 0) {
					for (int i = 0; i < cityData.size(); i++) {
			%>
			<tr>
				<td><input type="checkbox" name="cities"
					value="<%=cityData.get(i).getID()%>"></td>
				<td><%=cityData.get(i).getName()%></td>
				<td><%=cityData.get(i).getYear()%></td>
				<td><%=cityData.get(i).getPopulation()%></td>
			</tr>
			<%
				}
				}
			%>
		</table>
	</form>

</body>
</html>