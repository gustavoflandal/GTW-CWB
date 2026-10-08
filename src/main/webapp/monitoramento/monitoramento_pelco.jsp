<%@ include file="/includes/cabecalho_gmw.jsp" %>
<%@page import="com.consilux.model.Processo"%>
<%@page import="java.util.Collection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@page import="com.consilux.model.TipoRemessa"%>

<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<script type="text/javascript">
	var iCount = 0;
	var lock = false;
	
	function Start()
	{
		window.setInterval('UpdateImage()', 120);
	}
	
	function UpdateImage()
	{
		if(!lock)
		{
			lock = true;
			
			var divVideo = document.getElementById('divVideo');
			var img = document.getElementById('imgVideo');
			var div = document.getElementById('divCount');
			var vlQualidade = document.getElementById('txtQualidade').value;
			var urlImg = "http://10.0.1.101:31001/@ImgPelcoD;GetImage;"+vlQualidade;
			var divVideoWidth = divVideo.style.width.replace('px','');
			var bodyWidth = document.body.offsetWidth;
			
			var randomnumber = Math.random();
		
			img.src = urlImg+";"+randomnumber;
	
			iCount++;
			
			
			divVideo.style.left = ((bodyWidth / 2) - (divVideoWidth / 2))+'px';
		}
	}
	
	function SendMessage(sMsg)
	{
		$.ajax({url: 'http://10.0.1.101:31001/'+sMsg, context: document.body });
	}
	
	function MouseDown(event)
	{
		var imgVideo = document.getElementById("imgVideo");
		var divVideo = document.getElementById('divVideo');
		
		PanDir = 'R';
		TiltDir = 'D';
		
		xPos = event.offsetX?(event.offsetX):event.pageX-imgVideo.offsetLeft;
		yPos = event.offsetY?(event.offsetY):event.pageY-imgVideo.offsetTop;
		
		xPos -= (imgVideo.width / 2);
		yPos -= (imgVideo.height / 2);
		
		xPos -= divVideo.style.left.replace('px','');
		yPos -= divVideo.style.top.replace('px','');
		
		if(xPos < 0)
		{
			xPos = (xPos * -1);
			PanDir = 'L';
		}
	
		if(yPos < 0)
		{
			yPos = (yPos * -1);
			TiltDir = 'U';
		}
	
		xPos = Math.round(xPos / 2.5);
		yPos = Math.round(yPos / 1.875);
		
		var div = document.getElementById('divCount');
		
		SendMessage('@PelcoD;PanTilt;'+PanDir+';'+xPos+';'+TiltDir+';'+yPos);
	}
	
	function MouseUp(event)
	{
		SendMessage('@PelcoD;Stop');
	}
	
	function Zoom(s)
	{
		SendMessage('@PelcoD;Zoom;'+s);
	}
	
	function Focus(s)
	{
		SendMessage('@PelcoD;Focus;'+s);
	}
</script>
	<div id="divVideo" 
		style="position:absolute; 
			   top:200px; 
			   left:100px;
			   background-image: url('/images/player_gmw.png');
			   background-position: bottom left;
			   background-repeat: no-repeat; 
			   height: 265px;
			   width: 560px">
		<img id="imgVideo"
			 style = "float: left; width: 320px; height: 240px" 
			 src = "http://10.0.1.101:31001/@ImgPelcoD;GetImage;10" 
			 onLoad="lock = false;"
			 onMouseDown="MouseDown(event)" 
			 onMouseUp="MouseUp(event)">
		
		<div style="position:absolute; top: 243px; left: 0px; color: #E2E2E2">
			&nbsp;Qualidade&nbsp;<input id="txtQualidade" type="text" value="10" style="width:50px"></input>
		</div>
		
		<div style="float: left;
				position: relative;
				top: 100px;
				left: 30px; 
				background-image: url('/images/controles_gmw.png'); 
				width: 200px; 
				height: 135px">
		<div style="position: relative;
					top: 10px;
					left: 53px;
					width: 38px;
					height: 28px;"
					onMouseDown="SendMessage('@PelcoD;Focus;F')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -18px;
					left: 100px;
					width: 38px;
					height: 28px;"
					onMouseDown="SendMessage('@PelcoD;Tilt;U;64')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -46px;
					left: 148px;
					width: 38px;
					height: 28px;"
					onMouseDown="SendMessage('@PelcoD;Focus;N')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -31px;
					left: 54px;
					width: 38px;
					height: 28px;"
					onMouseDown="SendMessage('@PelcoD;Pan;L;64')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -59px;
					left: 148px;
					width: 38px;
					height: 28px;"
					onMouseDown="SendMessage('@PelcoD;Pan;R;64')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -44px;
					left: 53px;
					width: 38px;
					height: 28px;"
					onMouseDown="Zoom('W')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -72px;
					left: 100px;
					width: 38px;
					height: 28px;"
					onMouseDown="SendMessage('@PelcoD;Tilt;D;64')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		
		<div style="position: relative;
					top: -100px;
					left: 148px;
					width: 38px;
					height: 28px;"
					onMouseDown="Zoom('T')"
					onMouseUp="SendMessage('@PelcoD;Stop;')">
		</div>
		</div>
	</div>

	<script>Start();</script>
<%@ include file="/includes/rodape.jsp" %>