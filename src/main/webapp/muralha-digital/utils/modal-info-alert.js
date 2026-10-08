(function($){
    $.fn.extend({
        bs_alert: function(message, title){
            var cls='alert-danger';
            var html='<div id="myAlert" class="alert '+cls+' alert-dismissible fade show" role="alert">' +
						'<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2" viewBox="0 0 16 16" role="img" aria-label="Danger:">' +
							'<path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>' +
						'</svg>' +
						'<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';
            if(typeof title!=='undefined' &&  title!==''){
                html+='<h4>'+title+'</h4>';
            }
            html+='<span>'+message+'</span></div>';
            $(this).html(html);
        },
        bs_warning: function(message, title){
            var cls='alert-warning';
            var html='<div id="myAlert" class="alert '+cls+' alert-dismissible fade show" role="alert">' +
            			'<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2" viewBox="0 0 16 16" role="img" aria-label="Warning:">' +
    						'<path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>' +
  						'</svg>'+
            			'<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';
            if(typeof title!=='undefined' &&  title!==''){
                html+='<h4>'+title+'</h4>';
            }
            html+='<span>'+message+'</span></div>';
            $(this).html(html);
        },
        bs_info: function(message, title){
            var cls='alert-info';
            var html='<div id="myAlert" class="alert '+cls+' alert-dismissible fade show" role="alert">' +
						'<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-info-fill flex-shrink-0 me-2" viewBox="0 0 16 16" role="img" aria-label="Info:">' +
							'<path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>' +
						'</svg>'+
						'<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';
            if(typeof title!=='undefined' &&  title!==''){
                html+='<h4>'+title+'</h4>';
            }
            html+='<span>'+message+'</span></div>';
            $(this).html(html);
        },
        bs_success: function(message, title){
            var cls='alert-success';
            var html='<div id="myAlert" class="alert '+cls+' alert-dismissible fade show" role="alert">' +
						'<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-check-circle-fill flex-shrink-0 me-2" viewBox="0 0 16 16" role="img" aria-label="Success:">' +
							'<path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>' +
						'</svg>'+
						'<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';
            if(typeof title!=='undefined' &&  title!==''){
                html+='<h4>'+title+'</h4>';
            }
            html+='<span>'+message+'</span></div>';
            $(this).html(html);
        }
    });
})(jQuery);

function fechaAlerta()		{ $("#myAlert").alert("close"); 			}	

function AlertCsx(info) 	{ $('#error_container').bs_alert(info);		}

function WarningCsx(info) 	{ $('#error_container').bs_warning(info);	}

function InfoCsx(info)		{ $('#error_container').bs_info(info);		}

function SuccessCsx(info)		{ $('#error_container').bs_success(info);		}
