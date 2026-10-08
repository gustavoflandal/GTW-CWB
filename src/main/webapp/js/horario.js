// Fernando Oliveira da Silva
// Utilizando algumas funções do calendario.js written	by Tan Ling Wee	on 2 Dec 2001.
// 08/05/2006
var imgDirHorario = "/images/"
var	ie=document.all
var	dom=document.getElementById
var	bHorarioCarregado=false
var	agora =	new	Date()
var	horaNow	 = agora.getHours()
var	minutoNow = agora.getMinutes()
var divHorario = null;
var bShowHorario = false;
var nInicioHora = 8;
var nInicioMinuto = 12;

var	objHorario, objHora, objMinuto, horaSel, minutoSel, horaMontado, minutoMontado, HorarioIntervalID1, HorarioIntervalID2, HorarioTimeoutID1, HorarioTimeoutID2, txtValor, ctlNow;

/* hides <select> and <applet> objects (for IE only) */
function hideElement( elmID, overDiv ) {
	if( ie ) {
		for( i = 0; i < document.all.tags( elmID ).length; i++ ) {
			obj = document.all.tags( elmID )[i];
			if( !obj || !obj.offsetParent ) {
				continue;
			}
		
			// Find the element's offsetTop and offsetLeft relative to the BODY tag.
			objLeft   = obj.offsetLeft;
			objTop    = obj.offsetTop;
			objParent = obj.offsetParent;
			
			while( objParent.tagName.toUpperCase() != "BODY" ) {
				objLeft  += objParent.offsetLeft;
				objTop   += objParent.offsetTop;
				objParent = objParent.offsetParent;
			}
		
			objHeight = obj.offsetHeight;
			objWidth = obj.offsetWidth;
		
			if(( overDiv.offsetLeft + overDiv.offsetWidth ) <= objLeft );
			else if(( overDiv.offsetTop + overDiv.offsetHeight ) <= objTop );
/* CHANGE by Charlie Roche for nested TDs*/
			else if( overDiv.offsetTop >= ( objTop + objHeight + obj.height ));
/* END CHANGE */
			else if( overDiv.offsetLeft >= ( objLeft + objWidth ));
			else {
				obj.style.visibility = "hidden";
			}
		}
	}
}
/*
* unhides <select> and <applet> objects (for IE only)
*/
function showElement( elmID ) {
	if( ie ) {
		for( i = 0; i < document.all.tags( elmID ).length; i++ ) {
			obj = document.all.tags( elmID )[i];
			
			if( !obj || !obj.offsetParent ) {
				continue;
			}
		
			obj.style.visibility = "";
		}
	}
}

function swapImage2(srcImg, destImg){
	if (ie)	{ document.getElementById(srcImg).setAttribute("src",imgDirHorario + destImg) }
}

function initHorario()	{
	objHorario=document.getElementById("Horario").style;
	escondeHorario();

	objHora=document.getElementById("selectHora").style;
	objMinuto=document.getElementById("selectMinuto").style;

	horaMondado=false;
	minutoMontado=false;

	sHTML1 = "";

	sHTML1+="<span id='spanHora' class='title-control-normal-style' onmouseover='swapImage2(\"changeHora\",\"calendario/seta_baixo.png\");this.className=\"title-control-select-style\";window.status=\"Selecione a hora\"' onmouseout='swapImage2(\"changeHora\",\"calendario/seta_baixo.png\");this.className=\"title-control-normal-style\";window.status=\"\"' onclick='popUpHora()'></span>&nbsp;"
	sHTML1+="<span id='spanMinuto'  class='title-control-normal-style' onmouseover='swapImage2(\"changeMinuto\",\"calendario/seta_baixo.png\");this.className=\"title-control-select-style\";window.status=\"Selecione o minuto\"' onmouseout='swapImage2(\"changeMinuto\",\"calendario/seta_baixo.png\");this.className=\"title-control-normal-style\";window.status=\"\"'	onclick='popUpMinuto()'></span>&nbsp;"
	
	document.getElementById("captionHorario").innerHTML  =	sHTML1

	bHorarioCarregado=true
}

function padZero(num) {
	return (num	< 10)? '0' + num : num ;
}

function escondeHorario()	{
	objHorario.visibility="hidden"
	if (objHora != null){objHora.visibility="hidden"}
	if (objMinuto != null){objMinuto.visibility="hidden"}

 	showElement( 'SELECT' );
	showElement( 'APPLET' );
}

function montaStrHora(h,m) {
	return padZero(h)+":"+padZero(m);
}

function fechaHorario() {
	escondeHorario();
	if (txtValor)
		txtValor.value = montaStrHora(horaSel,minutoSel)
}


function criaDIVHorario() {
	document.write ("<div onclick='bShowHorario=true' id='Horario' class='div-style'><table width='100' class='table-style'><tr class='title-background-style' ><td><table width='100%'><tr><td class='title-style'><B><span id='captionHorario'></span></B></td></tr></table></td></tr></table></div><div id='selectHora' class='div-style'></div><div id='selectMinuto' class='div-style'></div>");
}
function montaHora() {
	popDownMinuto()
	sHTML =	""
	if (!horaMontado) {

		sHTML =	"<tr><td align='center'	onmouseover='this.className=\"dropdown-select-style\"'  onmouseout='clearInterval(HorarioIntervalID1);this.className=\"dropdown-normal-style\"'  onmousedown='clearInterval(HorarioIntervalID1);HorarioIntervalID1=setInterval(\"decHora()\",30)'  onmouseup='clearInterval(HorarioIntervalID1)'>-</td></tr>"
		j =	0
		nInicioHora = horaSel-3
		if (nInicioHora > 17)
			nInicioHora = 17;
		else if (nInicioHora < 0)
			nInicioHora = 0;
		for	(i=(nInicioHora); i<=(nInicioHora+6); i++) {
			sName =	i;
			if (i==horaSel){
				sName =	"<B>" +	sName +	"</B>"
			}

			sHTML += "<tr><td id='h" + j + "' onmouseover='this.className=\"dropdown-select-style\"' onmouseout='this.className=\"dropdown-normal-style\"' onclick='setHora("+j+");event.cancelBubble=true'>&nbsp;" + sName + "&nbsp;</td></tr>"
			j ++;
		}

		sHTML += "<tr><td align='center' onmouseover='this.className=\"dropdown-select-style\"'  onmouseout='clearInterval(HorarioIntervalID2);this.className=\"dropdown-normal-style\"'  onmousedown='clearInterval(HorarioIntervalID2);HorarioIntervalID2=setInterval(\"incHora()\",30)'  onmouseup='clearInterval(HorarioIntervalID2)'>+</td></tr>"

		document.getElementById("selectHora").innerHTML	= "<table width=44 class='dropdown-style' onmouseover='clearTimeout(HorarioTimeoutID2)' onmouseout='clearTimeout(HorarioTimeoutID2);HorarioTimeoutID2=setTimeout(\"popDownHora()\",100)' cellspacing=0>"	+ sHTML	+ "</table>"

		horaMontado	= true
	}
}
function popUpHora() {
	montaHora()
	leftOffset = parseInt(objHorario.left) + document.getElementById("spanHora").offsetLeft
	if (ie)	{
		leftOffset += 6
	}
	objHora.left =	leftOffset+"px";
	objHora.top = (parseInt(objHorario.top)+26)+"px";
	objHora.visibility = (dom||ie)? "visible"	: "show";
	
	hideElement( 'SELECT', document.getElementById("selectHora") );
	hideElement( 'APPLET', document.getElementById("selectHora") );
}
function popDownHora()	{
	objHora.visibility= "hidden"
}

function incHora() {
	for	(i=0; i<7; i++){

		if (nInicioHora > 16)
			return;

		newHora	= (i+nInicioHora)+1;

		if (newHora==horaSel) {
			txtHora = "&nbsp;<B>"	+ newHora +	"</B>&nbsp;"
		}
		else {
			txtHora = "&nbsp;" + newHora + "&nbsp;"
		}
		document.getElementById("h"+i).innerHTML = txtHora;
	}
	nInicioHora ++;
	bShowHorario=true
}

function decHora() {
	for	(i=0; i<7; i++){

		if (nInicioHora < 1)
			return;

		newHora	= (i+nInicioHora)-1

		if (newHora==horaSel) {
			txtHora =	"&nbsp;<B>"	+ newHora +	"</B>&nbsp;"
		}
		else {
			txtHora =	"&nbsp;" + newHora + "&nbsp;"
		}
		document.getElementById("h"+i).innerHTML = txtHora;
	}
	nInicioHora --;
	bShowHorario=true
}

function setHora(nHora) {
	horaSel=parseInt(nHora+nInicioHora);
	horaMontado=false;
	iniciaHorario();
	popDownHora();
}

function montaMinuto() {
	popDownHora()
	sHTML =	""
	if (!minutoMontado) {

		sHTML =	"<tr><td align='center'	onmouseover='this.className=\"dropdown-select-style\"'  onmouseout='clearInterval(HorarioIntervalID1);this.className=\"dropdown-normal-style\"'  onmousedown='clearInterval(HorarioIntervalID1);HorarioIntervalID1=setInterval(\"decMinuto()\",30)'  onmouseup='clearInterval(HorarioIntervalID1)'>-</td></tr>"
		j =	0
		nInicioMinuto = minutoSel-3
		if (nInicioMinuto > 53)
			nInicioMinuto = 53;
		else if (nInicioMinuto < 0)
			nInicioMinuto = 0;
		for	(i=(nInicioMinuto); i<=(nInicioMinuto+6); i++) {
			sName =	i;
			if (i==minutoSel){
				sName =	"<B>" +	sName +	"</B>"
			}

			sHTML += "<tr><td id='m" + j + "' onmouseover='this.className=\"dropdown-select-style\"' onmouseout='this.className=\"dropdown-normal-style\"' onclick='setMinuto("+j+");event.cancelBubble=true'>&nbsp;" + sName + "&nbsp;</td></tr>"
			j ++;
		}

		sHTML += "<tr><td align='center' onmouseover='this.className=\"dropdown-select-style\"'  onmouseout='clearInterval(HorarioIntervalID2);this.className=\"dropdown-normal-style\"'  onmousedown='clearInterval(HorarioIntervalID2);HorarioIntervalID2=setInterval(\"incMinuto()\",30)'  onmouseup='clearInterval(HorarioIntervalID2)'>+</td></tr>"

		document.getElementById("selectMinuto").innerHTML	= "<table width=44 class='dropdown-style' onmouseover='clearTimeout(HorarioTimeoutID2)' onmouseout='clearTimeout(HorarioTimeoutID2);HorarioTimeoutID2=setTimeout(\"popDownMinuto()\",100)' cellspacing=0>"	+ sHTML	+ "</table>"

		minutoMontado	= true
	}
}

function popUpMinuto() {
	montaMinuto();
	leftOffset = parseInt(objHorario.left) + document.getElementById("spanMinuto").offsetLeft
	if (ie)	{
		leftOffset += 6
	}
	objMinuto.left = leftOffset+"px";
	objMinuto.top = (parseInt(objHorario.top)+26)+"px";
	objMinuto.visibility = (dom||ie)? "visible"	: "show";
	
	hideElement( 'SELECT', document.getElementById("selectMinuto") );
	hideElement( 'APPLET', document.getElementById("selectMinuto") );
}
function popDownMinuto()	{
	objMinuto.visibility= "hidden";
}

function incMinuto() {
	for	(i=0; i<7; i++){

		if (nInicioMinuto > 52)
			return;

		newMinuto = (i+nInicioMinuto)+1;

		if (newMinuto==minutoSel) {
			txtMinuto = "&nbsp;<B>"	+ newMinuto +	"</B>&nbsp;"
		}
		else {
			txtMinuto = "&nbsp;" + newMinuto + "&nbsp;"
		}
		document.getElementById("m"+i).innerHTML = txtMinuto;
	}
	nInicioMinuto ++;
	bShowHorario=true
}

function decMinuto() {
	for	(i=0; i<7; i++){

		if (nInicioMinuto < 1)
			return;

		newMinuto = (i+nInicioMinuto)-1

		if (newMinuto==minutoSel) {
			txtMinuto =	"&nbsp;<B>"	+ newMinuto + "</B>&nbsp;"
		}
		else {
			txtMinuto =	"&nbsp;" + newMinuto + "&nbsp;"
		}
		document.getElementById("m"+i).innerHTML = txtMinuto;
	}
	nInicioMinuto --;
	bShowHorario=true
}

function setMinuto(nMinuto) {
	minutoSel=parseInt(nMinuto+nInicioMinuto);
	minutoMontado=false;
	iniciaHorario();
	popDownMinuto();
}

function iniciaHorario() {
		document.getElementById("spanHora").innerHTML = "&nbsp;" +padZero(horaSel)+ "&nbsp;<IMG id='changeHora' SRC='"+imgDirHorario+"calendario/seta_baixo.png' WIDTH='12' HEIGHT='10' BORDER=0>"
		document.getElementById("spanMinuto").innerHTML = "&nbsp;" +padZero(minutoSel)+ "&nbsp;<IMG id='changeMinuto' SRC='"+imgDirHorario+"calendario/seta_baixo.png' WIDTH='12' HEIGHT='10' BORDER=0>"
}
function mostraHorario(ctl,ctl2,format) {
	var	leftpos=0
	var	toppos=0

	if (bHorarioCarregado) {
		if ( objHorario.visibility ==	"hidden" ) {
			txtValor	= ctl2

			tokensChanged =	0

			aTime =	ctl2.value.split(":");
			aFormat = format.split(":");

			for	(i=0;i<3;i++) {
				if (aFormat[i]=="hh") {
					horaSel = parseInt(aTime[i], 10)
					tokensChanged ++
				}
				else if	(aFormat[i]=="mm") {
					minutoSel =	parseInt(aTime[i], 10)
					tokensChanged ++
				}
			}

			if ((tokensChanged!=2)||isNaN(horaSel)||isNaN(minutoSel)) {
				horaSel = horaNow
				minutoSel =	minutoNow
			}

			ohoraSelected=horaSel
			ominutoSelected=minutoSel

			aTag = ctl
			do {
				aTag = aTag.offsetParent;
				leftpos	+= aTag.offsetLeft;
				toppos += aTag.offsetTop;
			} while(aTag.tagName!="BODY");

			objHorario.left =	(fixedX==-1 ? ctl.offsetLeft	+ leftpos :	fixedX)+"px";
			objHorario.top = (fixedY==-1 ?	ctl.offsetTop +	toppos + ctl.offsetHeight +	2 :	fixedY)+"px";
			iniciaHorario();
			objHorario.visibility=(dom||ie)? "visible" : "show"
			
			hideElement( 'SELECT', document.getElementById("Horario") );
			hideElement( 'APPLET', document.getElementById("Horario") );			

			bShowHorario = true;
		}
	}
	else {
		mostraHorario(ctl,ctl2,format)
	}
}
criaDIVHorario();
initHorario();
document.onkeypress = function hideHorario1 () { 
	if (typeof event != 'undefined' && event.keyCode==27) {
		fechaHorario();
	}
}
document.onclick = function hideHorario2 () { 		
	if (!bShowHorario && objHorario.visibility != "hidden") {
		fechaHorario();
	}
	bShowHorario = false
}
