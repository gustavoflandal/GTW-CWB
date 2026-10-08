	function popup(evt,html) {
		popdown();
		var posX = evt.pageX ? evt.pageX : evt.clientX+document.documentElement.scrollLeft;
		var posY = evt.pageY ? evt.pageY : evt.clientY+document.documentElement.scrollTop;
		var div = document.createElement('div');
		
		div.id = "popup_menu";
		div.innerHTML = html;
		div.className = "div_popup";

		div.style.left = posX+"px";
		div.style.top = posY+"px";
		div.style.visibility = "hidden";

		document.body.appendChild(div); 
	}
	function popdown() {
		var div = document.getElementById("popup_menu");
		if (div) {
			if (div.style.visibility == "visible")
				document.body.removeChild(div); 
			else
				div.style.visibility = "visible"; //mostra o div pq o popdown eh sempre chamado imediatamente apos o popup.
		}
	}

	function addEvent (whichObject,eventType,functionName) { 
		if(whichObject.attachEvent) { 
		    whichObject['e'+eventType+functionName] = functionName; 
		    whichObject[eventType+functionName] = function(){whichObject['e'+eventType+functionName]( window.event );} 
		    whichObject.attachEvent( 'on'+eventType, whichObject[eventType+functionName] ); 
		}
		else 
	    	whichObject.addEventListener(eventType,functionName,false); 	    
	} 

	addEvent(document.documentElement,"click",function() {popdown()});
	