<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Video Player</title>
</head>
<body>

<video width="610" height="457" controls="controls" autoplay loop>
	<source src="/servlet/VerVideo?id_veiculo=<%= request.getParameter("id_veiculo") %>&video_sel=<%= request.getParameter("video_sel") %>" type="video/mp4" />
</video>

<br />

</body>
</html>