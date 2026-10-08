function GerarArquivoDownload(urlPesquisa, parametros)
{
	console.log("GerarArquivoDownload");
	console.log("urlPesquisa: " + urlPesquisa);
	console.log("parametros: " + JSON.stringify(parametros));

	var form = $('<form></form>');

    form.attr("method", "POST");
	form.attr("action", urlPesquisa);

    $.each(parametros, function(key, value) {
        var field = $('<input></input>');

        field.attr("type", "hidden");
        field.attr("name", key);
        field.attr("value", value);

        form.append(field);
    });

    // The form needs to be a part of the document in
    // order for us to be able to submit it.
    $(document.body).append(form);
    form.attr('target', '_blank').submit();
//	$(document.body).remove(form);
}
