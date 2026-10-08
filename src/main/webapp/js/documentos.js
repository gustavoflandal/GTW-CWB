function anexarDoc(classificador, identificador, funcRetorno) {
    var w = window.open("/documentos/AnexarDoc?classificador="+classificador+"&identificador="+identificador,"Anexo de Documentos","width=500, height=250");
    w.aoFechar = function(){funcRetorno();};
}
function listaDoc(classificador, identificador, funcRetorno) {
    var w = window.open("/documentos/ListarDoc?classificador="+classificador+"&identificador="+identificador,"Anexo de Documentos","width=500, height=250");
    w.aoFechar = function(){funcRetorno();};
}