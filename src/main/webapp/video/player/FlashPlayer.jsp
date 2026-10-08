<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Video Player</title>
<script src="flowplayer-3.2.13.min.js"></script>
</head>
<body>

<a href="/servlet/VerVideo?id_veiculo=<%= request.getParameter("id_veiculo") %>"
   style="display:block;width:610px;height:457px;"
   id="player">
</a>
<script language="JavaScript">
  flowplayer("player", "flowplayer-3.2.18.swf", {
	  clip: {
		  autoPlay: true,
		  onBeforeFinish: function() {
	             return false;
	       }
	  }
  });
</script>

</body>
</html>