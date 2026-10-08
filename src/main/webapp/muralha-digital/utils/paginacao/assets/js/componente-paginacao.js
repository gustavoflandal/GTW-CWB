var PAGINACAO_TOTAL_REGISTROS = 0;
var PAGINACAO_ITENS_POR_PAGINA = 5;
var PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE = 5;
var PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE;
var PAGINACAO_OFFSET = 0;
var PAGINACAO_PAGINA_ATUAL = 1;
var PAGINACAO_TOTAL_PAGINAS = 1;
var PAGINACAO_ITENS_POR_PAGINA_OPCOES = uniq = [...new Set([PAGINACAO_ITENS_POR_PAGINA,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,50,100].sort((a,b)=>a-b))];

function CarregarPametrosPaginacao()
{
	PAGINACAO_TOTAL_REGISTROS = 0;
	PAGINACAO_ITENS_POR_PAGINA = 5;
	PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE = 5;
	PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE;
	PAGINACAO_OFFSET = 0;
	PAGINACAO_PAGINA_ATUAL = 1;
	PAGINACAO_TOTAL_PAGINAS = 1;

//	console.log("CarregarPametrosPaginacao");
//	console.log("PAGINACAO_TOTAL_REGISTROS: " + PAGINACAO_TOTAL_REGISTROS);
//	console.log("PAGINACAO_TOTAL_PAGINAS: " + PAGINACAO_TOTAL_PAGINAS);
//	console.log("PAGINACAO_PAGINA_ATUAL: " + PAGINACAO_PAGINA_ATUAL);
//	console.log("PAGINACAO_TOTAL_PAGINAS_VISIVEIS: " + PAGINACAO_TOTAL_PAGINAS_VISIVEIS);
}


function CriarComponentePaginacao()
{
	$('#componente_paginacao').empty();
	$('#componente_paginacao').removeData("twbs-pagination");
	$('#componente_paginacao').unbind("page");
		
	if (PAGINACAO_TOTAL_REGISTROS > 0)
	{
		PAGINACAO_TOTAL_PAGINAS = Math.ceil(PAGINACAO_TOTAL_REGISTROS / PAGINACAO_ITENS_POR_PAGINA);
		PAGINACAO_PAGINA_ATUAL = PAGINACAO_PAGINA_ATUAL > PAGINACAO_TOTAL_PAGINAS ? PAGINACAO_TOTAL_PAGINAS : PAGINACAO_PAGINA_ATUAL;
		PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS < PAGINACAO_TOTAL_PAGINAS_VISIVEIS ? PAGINACAO_TOTAL_PAGINAS : PAGINACAO_TOTAL_PAGINAS_VISIVEIS;
	
		$('#componente_paginacao').twbsPagination({
			startPage: PAGINACAO_PAGINA_ATUAL,
			totalPages: PAGINACAO_TOTAL_PAGINAS,
            visiblePages: PAGINACAO_TOTAL_PAGINAS_VISIVEIS,
            first: "&larr;",
            prev: "&laquo;",
            next: "&raquo;",
            last: "&rarr;",
            pageVariable: '{{page}}',
			totalPagesVariable: '{{total_pages}}',
        }).on('page', function (event, page) {
            PAGINACAO_OFFSET = PAGINACAO_ITENS_POR_PAGINA * (page-1);
            PAGINACAO_PAGINA_ATUAL = page;
            AtualizarItensPaginacao();
        });
    }
}

function ReiniciarComponentePaginacao()
{
	PAGINACAO_TOTAL_REGISTROS = 0;
	CriarComponentePaginacao();
}

function ReiniciaPaginaAtualComponentePaginacao()
{
	PAGINACAO_PAGINA_ATUAL = 1;
	PAGINACAO_OFFSET = PAGINACAO_ITENS_POR_PAGINA * (PAGINACAO_PAGINA_ATUAL-1);
}

function AtualizarParametrosComponentePaginacao(event)
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
		
	var totalRegistros = $xml.find('paginacao totalRegistros').text();
	
	if (totalRegistros != "")
		PAGINACAO_TOTAL_REGISTROS = parseInt(totalRegistros);
	else
		ReiniciarComponentePaginacao();
		
	PAGINACAO_TOTAL_PAGINAS = Math.ceil(PAGINACAO_TOTAL_REGISTROS / PAGINACAO_ITENS_POR_PAGINA);
	PAGINACAO_PAGINA_ATUAL = PAGINACAO_PAGINA_ATUAL > PAGINACAO_TOTAL_PAGINAS ? PAGINACAO_TOTAL_PAGINAS : PAGINACAO_PAGINA_ATUAL;
	PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS > PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE ? PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE : PAGINACAO_TOTAL_PAGINAS;
}

function AlterarRegistrosPorPagina()
{
	var registrosPorPagina = document.getElementById("registrosPorPagina");
	
	if (registrosPorPagina)
	{
		var quantidade = (registrosPorPagina.value != "" ? parseInt(registrosPorPagina.value) : 0);
		if (quantidade > 0) PAGINACAO_ITENS_POR_PAGINA = quantidade;
	}
}