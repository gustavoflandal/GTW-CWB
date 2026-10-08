<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Video Player</title>
</head>
<body>

<object id="mediaplayer" classid="clsid:22d6f312-b0f6-11d0-94ab-0080c74c7e95" codebase="http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#version=5,1,52,701" standby="loading microsoft windows media player components..." type="application/x-oleobject" width="640" height="480">
<param name="filename" value="/servlet/VerVideo?id_veiculo=<%= request.getParameter("id_veiculo") %>"/>
<param name="animationatstart" value="true"/>
<param name="transparentatstart" value="true"/>
<param name="autostart" value="true"/>
<param name="showcontrols" value="false"/>
<param name="ShowStatusBar" value="false"/>
<param name="windowlessvideo" value="true"/>
<param name="playCount" value="1000" />
<embed src="/servlet/VerVideo?id_veiculo=<%= request.getParameter("id_veiculo") %>" autostart="true" showcontrols="false" loop="true" showstatusbar="0" bgcolor="white" width="320" height="310"/>
</object>

</body>
</html>