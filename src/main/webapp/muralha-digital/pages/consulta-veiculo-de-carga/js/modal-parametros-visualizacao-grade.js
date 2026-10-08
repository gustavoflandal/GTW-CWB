function abrirModalParametros() {
    const modalElement = document.getElementById('modalParametrosGrade');
    if(modalElement){
        const modal = new bootstrap.Modal(modalElement, {
            backdrop: 'static',
            keyboard: false
        });
        modal.show();
    }
}

function fecharModalParametros() {
    const modalElement = document.getElementById('modalParametrosGrade');
    if(modalElement){
        const modal = bootstrap.Modal.getInstance(modalElement); // pega instância existente
        if(modal){
            modal.hide();
        }
    }
}

function cancelarModoGrade(){
	
	//O valor é tratado dentro da função modo_grade
	// Caso true na consulta.js se torna false e é feito os tratamentos necessários;
	
	resetOpcoesPaginacao();
	
	IS_MODO_GRADE_IMAGENS = true;
	IMAGEM_ORIGINAL = false;
	modo_grade();
	
	fecharModalParametros();
}

function confirmarModoGrade(){
			
	//O valor é tratado dentro da função modo_grade
	// Caso false na consulta.js se torna true e é feito os tratamentos necessários;
	
	IS_MODO_GRADE_IMAGENS = false;
	PAGINACAO_ITENS_POR_PAGINA = parseInt(document.getElementById('qtdImagens').value);
	
	PAGINACAO_ITENS_POR_PAGINA_OPCOES = uniq = [...new Set([PAGINACAO_ITENS_POR_PAGINA].sort((a,b)=>a-b))];
	
	const imagemOriginal = parseInt(document.getElementById('imgOriginal').value);
	IMAGEM_ORIGINAL = imagemOriginal === 0 ? false : true;
	
	modo_grade();
}

function resetOpcoesPaginacao(){
	PAGINACAO_ITENS_POR_PAGINA_OPCOES = uniq = [...new Set([4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20].sort((a,b)=>a-b))];
}