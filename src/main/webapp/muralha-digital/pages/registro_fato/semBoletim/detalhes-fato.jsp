<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Detalhes do Fato</title>
    <link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
    <script src="/muralha-digital/pages/registro_fato/semBoletim/js/detalhes.js"></script>   
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="p-4">
    <h2>Fato #${fato.id}</h2>

    <h4>Dados Gerais</h4>
    <ul>
        <li>Data: ${fato.data}</li>
        <li>Privado: ${fato.privado}</li>
        <li>Tipo: ${fato.tipoRegistro}</li>
        <li>Permite Atendimento: ${fato.permiteAtendimento}</li>
        <li>Observações: ${fato.observacoes}</li>
    </ul>

    <h4>Endereço</h4>
    <ul>
        <li>Rua: ${fato.endereco.logradouro}</li>
        <li>Número: ${fato.endereco.numero}</li>
        <li>Bairro: ${fato.endereco.bairro}</li>
        <li>Município: ${fato.endereco.nomeMunicipio}</li>
        <li>Referência: ${fato.endereco.referencia}</li>
    </ul>

    <h4>Veículos</h4>
    <c:forEach var="v" items="${fato.veiculos}">
        <div class="border rounded p-2 mb-2">
            <strong>${v.tipoVeiculo}</strong><br/>
            Placa: ${v.placa} - Cor: ${v.cor} - Fabricante: ${v.fabricante} - Modelo: ${v.modelo}
        </div>
    </c:forEach>

    <h4>Envolvidos</h4>
    <c:forEach var="e" items="${fato.envolvidos}">
        <div class="border rounded p-2 mb-2">
            Nome: ${e.nome} - CPF: ${e.cpf} - DDD: ${e.ddd} - Telefone: ${e.telefone}
            <br/>Sexo: ${e.sexo} - Data Nasc.: ${e.dataNascimento}
            <br/>Condutor: ${e.condutor} - Uso Cinto: ${e.usoCinto}
        </div>
    </c:forEach>

    <h4>Armas</h4>
    <c:forEach var="a" items="${fato.armas}">
        <div class="border rounded p-2 mb-2">
            Tipo: ${a.tipoArma} - Fabricante: ${a.fabricante} - Calibre: ${a.calibre} - Quantidade: ${a.qtdMunicao}
        </div>
    </c:forEach>
</body>
</html>