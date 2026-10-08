window.jsPDF = window.jspdf.jsPDF;

/**
	Este script depende das seguinte bibliotecas:
	* jspdf.umd.min.js
	* jspdf.plugin.autotable.min.js
	* xlsx.core.min.js
 */


function GerarRelatorioPDF(tabela, filename, listaIgnoreColumns)
{
	var exportArray = toArray(tabela, listaIgnoreColumns);
	
	var doc = new jsPDF('p', 'pt');
	
  	doc.autoTable({
						head: exportArray.header,
						body: exportArray.data,
//						theme: 'grid',
//					    alternateRowStyles: {
//					      	fillColor: [240, 240, 240],
//					    },
//					    startY: 200
					});
  	doc.save(filename);
}


function GerarRelatorioXLS(tabela, filename, listaIgnoreColumns)
{
	var wb = XLSX.utils.table_to_book(document.getElementById(tabela), {sheet:"Relatorio"});
	var arr = toArray(tabela, listaIgnoreColumns);
	
	var wa = XLSX.utils.aoa_to_sheet(
			arr.total
		);
		
	wb.Sheets["Relatorio"] = wa;
	
	XLSX.writeFile(wb, filename, {bookType: 'biff8'});
}


function GerarRelatorioXLSX(tabela, filename, listaIgnoreColumns)
{
	var wb = XLSX.utils.table_to_book(document.getElementById(tabela), {sheet:"Relatorio"});
	var arr = toArray(tabela, listaIgnoreColumns);
	
	var wa = XLSX.utils.aoa_to_sheet(
			arr.total
		);
		
	wb.Sheets["Relatorio"] = wa;
	
	XLSX.writeFile(wb, filename);
}


function toArray(tabela, ignoreColumns)
{
	var el = $('#' + tabela);
	var totalArray = [];
    var headerArray = [];
    el.find('thead').find('tr').each(function() {
        var arrayTd = [];

        $(this).find('th').each(function(index,data) {  // .not(ignoreColumns)
            if ($(this).css('display') != 'none' && ignoreColumns.indexOf(index) == -1){
                arrayTd.push(parseString($(this)));
            }
        });
        headerArray.push(arrayTd);
        totalArray.push(arrayTd);

    });

    var bodyArray = [];
    el.find('tbody').find('tr').each(function() {
        var arrayTd = [];

        $(this).find('td').each(function(index,data) {
            if ($(this).css('display') != 'none' && ignoreColumns.indexOf(index) == -1){
                arrayTd.push(parseString($(this)));
            }
        });
        bodyArray.push(arrayTd);
        totalArray.push(arrayTd);

    });

    return {header:headerArray,data:bodyArray, total:totalArray};
}


function parseString(data)
{
    return data.text().trim();
}

