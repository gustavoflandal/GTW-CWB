<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link rel="stylesheet" type="text/css" href="skin/minimalist.css"></link>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script type="text/javascript" src="flowplayer.min.js"></script>
<title>Video Player</title>
</head>
<body>

<div class="flowplayer" data-swf="flowplayer.swf" style="height: 610px; width: 457px" data-ratio="1.3334">
      <video width="610" height="457" controls="controls" autoplay loop>
         <source type="video/mp4" src="/servlet/VerVideo?id_veiculo=<%= request.getParameter("id_veiculo") %>">
      </video>
</div>

</body>
</html>