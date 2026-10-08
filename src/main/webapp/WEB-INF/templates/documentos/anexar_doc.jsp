<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<c:set var="classificador" value='<%=request.getParameter("classificador")%>'/>
<c:set var="identificador" value='<%=request.getParameter("identificador")%>'/>
<link rel="stylesheet" href="/css/jquery.fileupload-ui.css">
</head>
<body>
<script type="text/javascript">
	function selecionarArquivo() {
		var file_arquivo = document.getElementById("file_arquivo");
		var txt_arquivo = document.getElementById("txt_arquivo");
		
		file_arquivo.onchange = function() {txt_arquivo.value = file_arquivo.value;};
		file_arquivo.click();
		
	}
</script>
<br />
<br />
<br />
<form id="file_upload" action="" method="POST" enctype="multipart/form-data">
	<table class="tabela_branca" width="100%">
		<tr>
			<td align="center">
				<table class="tabela_branca"> 
					<tr>
						<td class="label_campo" colspan="2">Enviar arquivo...</td>
				    </tr>
					<tr>
						<td class="valor_campo" width="300px">
						    <div id="drop_zone">
						        <input id="file_arquivo" style="display: none;" type="file" name="file">
						    	<input type="text" id="txt_arquivo" name="txt_arquivo"/>
						    </div>
					    </td>
						<td class="valor_campo">
					        <button onclick="selecionarArquivo(); return false;">Selecionar Arquivo</button>
				        </td>
				    </tr>
					<tr>
						<td class="box_botoes" colspan="2">
    						<button>Enviar</button>
   						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
    <table id="files"></table>
    <input type="hidden" name="classificador" value="${classificador}">
    <input type="hidden" name="identificador" value="${identificador}">
</form>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/jquery.fileupload.js"></script>
<script type="text/javascript" src="/js/jquery.fileupload-ui.js"></script>
<script>

/*global $ */
$(function () {
    var initFileUpload = function () {
        $('#file_upload').fileUploadUI({
            namespace: 'file_upload',
            fileInputFilter: '#arquivo',
            dropZone: $('#drop_zone'),
            uploadTable: $('#files'),
            downloadTable: $('#files'),
            buildUploadRow: function (files, index) {
                return $('<tr><td>' + files[index].name + '<\/td>' +
                        '<td class="file_upload_progress"><div><\/div><\/td>' +
                        '<td class="file_upload_cancel">' +
                        '<button class="ui-state-default ui-corner-all" title="Cancel">' +
                        '<span class="ui-icon ui-icon-cancel">Cancel<\/span>' +
                        '<\/button><\/td><\/tr>');
            },
            buildDownloadRow: function (file) {
                return $('<tr><td>' + file.name + '<\/td><\/tr>');
            }
        });
    };
});
</script> 
<%@ include file="/includes/rodape.jsp" %>
