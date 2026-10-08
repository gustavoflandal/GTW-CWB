<%@page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8" session="false"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.model.Acesso"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>GTW</title>
<link rel="stylesheet" href="/css/gtw.css" media="screen" type="text/css">
</head>
<body onunload="if (window.aoFechar) aoFechar();" onload="if (window.aoAbrir) aoAbrir();">
