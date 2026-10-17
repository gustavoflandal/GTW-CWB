<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<style>
    .manual-container {
        max-width: 960px;
        margin: 0 auto;
        padding: 1.5rem;
        font-size: 0.95rem;
        line-height: 1.7;
    }
    .manual-container h1 { font-size: 1.8rem; font-weight: 700; margin-bottom: 0.5rem; }
    .manual-container h2 {
        font-size: 1.35rem; font-weight: 700; margin-top: 2.5rem; margin-bottom: 1rem;
        padding-bottom: 0.4rem; border-bottom: 2px solid #0d75bf;
    }
    .manual-container h3 { font-size: 1.1rem; font-weight: 600; margin-top: 1.8rem; margin-bottom: 0.7rem; }
    .manual-container h4 { font-size: 1rem; font-weight: 600; margin-top: 1.2rem; }
    .manual-container table { font-size: 0.88rem; }
    .manual-container th { background-color: #f0f6fc; white-space: nowrap; }
    .manual-container code { font-size: 0.85em; color: #0d6efd; background: #f8f9fa; padding: 0.15em 0.35em; border-radius: 3px; }
    .manual-container pre { background: #1e1e1e; color: #d4d4d4; padding: 1rem; border-radius: 6px; overflow-x: auto; font-size: 0.82rem; }
    .manual-container pre code { color: inherit; background: none; padding: 0; }
    .manual-container blockquote { border-left: 4px solid #0d75bf; background: #f0f6fc; padding: 0.75rem 1rem; margin: 1rem 0; border-radius: 0 6px 6px 0; }
    .manual-container blockquote p { margin-bottom: 0; }
    .manual-container .badge-dim { background: #0d6efd; }
    .manual-container .badge-med { background: #198754; }
    .manual-container .badge-tmp { background: #f59e0b; color: #000; }
    .manual-container .badge-geo { background: #6f42c1; }
    .manual-container .layout-diagram {
        background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px;
        padding: 1rem; font-family: monospace; font-size: 0.82rem; white-space: pre; overflow-x: auto;
    }
    .manual-toc { position: sticky; top: 80px; }
    .manual-toc a { font-size: 0.82rem; color: #495057; text-decoration: none; display: block; padding: 0.2rem 0; }
    .manual-toc a:hover { color: #0d75bf; }
    .manual-toc a.fw-bold { color: #212529; }
    .step-number {
        display: inline-flex; align-items: center; justify-content: center;
        width: 28px; height: 28px; border-radius: 50%;
        background: #0d75bf; color: #fff; font-weight: 700; font-size: 0.85rem;
        margin-right: 0.5rem; flex-shrink: 0;
    }
    .step-item { display: flex; align-items: flex-start; margin-bottom: 0.75rem; }
    .step-item p { margin-bottom: 0; }
    .flow-diagram {
        display: flex; align-items: center; justify-content: center; gap: 0.5rem;
        flex-wrap: wrap; margin: 1.5rem 0;
    }
    .flow-box {
        padding: 0.6rem 1rem; border-radius: 8px; text-align: center;
        font-weight: 600; font-size: 0.9rem; min-width: 120px;
    }
    .flow-arrow { font-size: 1.3rem; color: #adb5bd; }
    @media print {
        .no-print { display: none !important; }
        .manual-container { max-width: 100%; }
    }
</style>

<div class="container-fluid py-3">
    <div class="d-flex justify-content-between align-items-center mb-3 no-print">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-0" style="font-size:0.85rem">
                <li class="breadcrumb-item"><a href="/muralha-digital/pages/analises/paineis/lista.jsp"><i class="bi bi-grid-1x2"></i> Paineis</a></li>
                <li class="breadcrumb-item active">Manual</li>
            </ol>
        </nav>
        <button class="btn btn-outline-secondary btn-sm" onclick="window.print()">
            <i class="bi bi-printer"></i> Imprimir / PDF
        </button>
    </div>

    <div class="manual-container">

        <!-- ============================================================ -->
        <h1><i class="bi bi-book me-2" style="color:#0d75bf"></i>Manual do Dashboard Builder</h1>
        <p class="text-muted mb-4">Guia completo para criacao de dashboards e KPIs no sistema Muralha Digital.</p>

        <!-- ============================================================ -->
        <h2 id="visao-geral">1. Visao Geral</h2>

        <p>O <strong>Dashboard Builder</strong> permite criar paineis personalizados com graficos, tabelas e indicadores (KPIs) sem escrever codigo ou SQL. O processo segue este fluxo:</p>

        <div class="flow-diagram">
            <div class="flow-box" style="background:#e9ecef;color:#495057;">
                <i class="bi bi-journal-code d-block" style="font-size:1.5rem"></i>
                Catalogo de Dados
                <small class="d-block fw-normal" style="font-size:0.75rem">tabelas do banco</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#cfe2ff;color:#084298;">
                <i class="bi bi-database d-block" style="font-size:1.5rem"></i>
                Dataset
                <small class="d-block fw-normal" style="font-size:0.75rem">modelo de dados</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#d1e7dd;color:#0f5132;">
                <i class="bi bi-grid-1x2 d-block" style="font-size:1.5rem"></i>
                Painel
                <small class="d-block fw-normal" style="font-size:0.75rem">tela do dashboard</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#fff3cd;color:#664d03;">
                <i class="bi bi-bar-chart d-block" style="font-size:1.5rem"></i>
                Widgets
                <small class="d-block fw-normal" style="font-size:0.75rem">graficos e KPIs</small>
            </div>
        </div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Conceito</th><th>O que e</th><th>Quem cria</th></tr></thead>
            <tbody>
                <tr><td><strong>Catalogo de Dados</strong></td><td>Registro das tabelas e colunas disponiveis no banco de dados</td><td>Administrador</td></tr>
                <tr><td><strong>Dataset</strong></td><td>Selecao de tabelas, colunas e relacionamentos que formam uma fonte de dados</td><td>Operador ou Admin</td></tr>
                <tr><td><strong>Painel</strong></td><td>Uma tela (dashboard) onde voce organiza os widgets</td><td>Operador</td></tr>
                <tr><td><strong>Widget</strong></td><td>Componente visual: grafico de barras, linha, pizza, KPI, tabela, etc.</td><td>Operador</td></tr>
            </tbody>
        </table>

        <!-- ============================================================ -->
        <h2 id="acesso">2. Acesso ao Sistema</h2>

        <h3>2.1 Permissoes necessarias</h3>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Grupo</th><th>Permissao</th></tr></thead>
            <tbody>
                <tr><td><strong>11</strong> (Administrador)</td><td>Acesso total: catalogo, datasets, paineis e widgets</td></tr>
                <tr><td><strong>41</strong> (Analista)</td><td>Criar e editar datasets, paineis e widgets</td></tr>
                <tr><td><strong>42</strong> (Visualizador)</td><td>Visualizar paineis compartilhados</td></tr>
            </tbody>
        </table>

        <blockquote>Se voce nao ve os menus abaixo, solicite ao administrador que adicione seu usuario ao grupo apropriado.</blockquote>

        <h3>2.2 Navegacao pelo menu</h3>
        <p>Apos o login, localize no menu lateral:</p>
        <ul>
            <li><strong>Paineis de Analise</strong> — galeria de paineis (dashboards)</li>
            <li><strong>Catalogo de Datasets</strong> — lista de datasets criados</li>
            <li><strong>Cadastro e Configuracao &gt; Catalogo de Dados</strong> — gerenciamento de tabelas (admin)</li>
        </ul>

        <!-- ============================================================ -->
        <h2 id="catalogo">3. Etapa 1 — Catalogo de Dados <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Admin</span></h2>

        <blockquote><strong>Quem faz:</strong> apenas o administrador. Se o catalogo ja estiver configurado, pule para a <a href="#dataset">Etapa 2</a>.</blockquote>

        <h3 id="cat-tabela">3.1 Cadastrar uma Tabela</h3>
        <div class="step-item"><span class="step-number">1</span><p>No menu lateral, clique em <strong>Cadastro e Configuracao</strong> &gt; <strong>Catalogo de Dados</strong></p></div>
        <div class="step-item"><span class="step-number">2</span><p>Na aba <strong>Tabelas</strong>, clique no botao <strong>Nova Tabela</strong> (canto superior direito)</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Preencha o formulario no painel lateral:</p></div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Campo</th><th>O que preencher</th><th>Exemplo</th></tr></thead>
            <tbody>
                <tr><td><strong>Nome Fisico</strong></td><td>Nome real da tabela no banco de dados</td><td><code>veiculo_tempo_real</code></td></tr>
                <tr><td><strong>Schema</strong></td><td>Esquema do banco (selecione na lista)</td><td><code>muralha</code></td></tr>
                <tr><td><strong>Rotulo</strong></td><td>Nome amigavel que aparecera nas telas</td><td><code>Passagens de Veiculos</code></td></tr>
                <tr><td><strong>Descricao</strong></td><td>Texto livre explicando o conteudo (opcional)</td><td><code>Registros de passagem</code></td></tr>
                <tr><td><strong>Chave Primaria</strong></td><td>Coluna que identifica cada registro</td><td><code>id</code></td></tr>
                <tr><td><strong>Sensivel</strong></td><td>Marque se a tabela contem dados pessoais</td><td><em>(deixe desmarcado)</em></td></tr>
            </tbody>
        </table>

        <div class="step-item"><span class="step-number">4</span><p>Clique em <strong>Salvar</strong>. A tabela aparece na lista com selo colorido: <span class="badge" style="background:#6c757d">dbo</span> <span class="badge" style="background:#0d75bf">muralha</span> <span class="badge" style="background:#198754">dash</span></p></div>

        <h3 id="cat-coluna">3.2 Cadastrar Colunas</h3>
        <div class="step-item"><span class="step-number">1</span><p>Clique na aba <strong>Colunas</strong></p></div>
        <div class="step-item"><span class="step-number">2</span><p>Selecione a tabela desejada na lista suspensa</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Clique em <strong>Nova Coluna</strong> e preencha:</p></div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Campo</th><th>O que preencher</th><th>Exemplo</th></tr></thead>
            <tbody>
                <tr><td><strong>Nome Fisico</strong></td><td>Nome real da coluna no banco</td><td><code>velocidade</code></td></tr>
                <tr><td><strong>Rotulo</strong></td><td>Nome amigavel</td><td><code>Velocidade</code></td></tr>
                <tr><td><strong>Tipo</strong></td><td>Tipo do dado</td><td><code>numerico</code></td></tr>
                <tr><td><strong>Papel Sugerido</strong></td><td>Como a coluna sera usada em graficos</td><td><code>Medida</code></td></tr>
                <tr><td><strong>Coluna sensivel</strong></td><td>Marque se contem dados pessoais</td><td><em>(se aplicavel)</em></td></tr>
            </tbody>
        </table>

        <p><strong>Tipos de dado:</strong></p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Tipo</th><th>Quando usar</th></tr></thead>
            <tbody>
                <tr><td><code>texto</code></td><td>Nomes, descricoes, categorias</td></tr>
                <tr><td><code>numerico</code></td><td>Valores decimais (velocidade, percentual)</td></tr>
                <tr><td><code>inteiro</code></td><td>Contadores, quantidades, IDs</td></tr>
                <tr><td><code>data</code></td><td>Somente data (sem horario)</td></tr>
                <tr><td><code>data_hora</code></td><td>Data com horario</td></tr>
                <tr><td><code>booleano</code></td><td>Verdadeiro/Falso</td></tr>
            </tbody>
        </table>

        <p><strong>Papeis sugeridos:</strong></p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Papel</th><th>Significado</th><th>Cor</th></tr></thead>
            <tbody>
                <tr><td><strong>Dimensao</strong></td><td>Categoriza os dados (eixo X, agrupamento)</td><td><span class="badge badge-dim">Azul</span></td></tr>
                <tr><td><strong>Medida</strong></td><td>Valor numerico a ser medido (eixo Y)</td><td><span class="badge badge-med">Verde</span></td></tr>
                <tr><td><strong>Tempo</strong></td><td>Coluna de data/hora para eixo temporal</td><td><span class="badge badge-tmp">Ambar</span></td></tr>
                <tr><td><strong>Geo</strong></td><td>Coordenada geografica (latitude/longitude)</td><td><span class="badge badge-geo">Roxo</span></td></tr>
            </tbody>
        </table>

        <div class="step-item"><span class="step-number">4</span><p>Clique em <strong>Salvar</strong>. Repita para todas as colunas da tabela.</p></div>

        <h3 id="cat-relacao">3.3 Cadastrar Relacoes entre Tabelas</h3>
        <div class="step-item"><span class="step-number">1</span><p>Clique na aba <strong>Relacoes</strong> e depois em <strong>Nova Relacao</strong></p></div>
        <div class="step-item"><span class="step-number">2</span><p>Preencha:</p></div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Campo</th><th>O que preencher</th><th>Exemplo</th></tr></thead>
            <tbody>
                <tr><td><strong>Tabela Origem</strong></td><td>Tabela com a chave estrangeira</td><td><code>veiculo_tempo_real</code></td></tr>
                <tr><td><strong>Tabela Destino</strong></td><td>Tabela referenciada</td><td><code>equipamento</code></td></tr>
                <tr><td><strong>Coluna Origem</strong></td><td>Coluna na tabela origem</td><td><code>id_equipamento</code></td></tr>
                <tr><td><strong>Coluna Destino</strong></td><td>Coluna na tabela destino</td><td><code>id</code></td></tr>
                <tr><td><strong>Cardinalidade</strong></td><td>Tipo de relacionamento</td><td><code>N:1 (muitos para um)</code></td></tr>
            </tbody>
        </table>

        <div class="step-item"><span class="step-number">3</span><p>Clique em <strong>Salvar</strong>. A relacao aparece na lista mostrando <code>colunaOrigem = colunaDestino</code>.</p></div>

        <!-- ============================================================ -->
        <h2 id="dataset">4. Etapa 2 — Criar um Dataset</h2>

        <p>O Dataset define quais tabelas, colunas e filtros alimentarao seus graficos e KPIs.</p>

        <h3>4.1 Acessar a lista de Datasets</h3>
        <div class="step-item"><span class="step-number">1</span><p>No menu lateral, clique em <strong>Catalogo de Datasets</strong></p></div>
        <div class="step-item"><span class="step-number">2</span><p>Clique no botao <strong>Novo Dataset</strong> (canto superior direito)</p></div>

        <h3>4.2 Passo 1 — Tabela Base</h3>
        <p>O editor abre no modo wizard (assistente de 6 passos):</p>
        <div class="d-flex gap-2 flex-wrap mb-3" style="font-size:0.82rem">
            <span class="badge bg-primary">1. Tabela Base</span>
            <span class="badge bg-secondary">2. Relacoes</span>
            <span class="badge bg-secondary">3. Campos</span>
            <span class="badge bg-secondary">4. Calculados</span>
            <span class="badge bg-secondary">5. Preview</span>
            <span class="badge bg-secondary">6. Publicar</span>
        </div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Campo</th><th>O que preencher</th><th>Exemplo</th></tr></thead>
            <tbody>
                <tr><td><strong>Nome do dataset</strong></td><td>Nome que identifica esta fonte de dados</td><td><code>Passagens por Dia</code></td></tr>
                <tr><td><strong>Descricao</strong></td><td>Texto explicativo (opcional)</td><td><code>Total de passagens diarias</code></td></tr>
                <tr><td><strong>Tabela base</strong></td><td>Tabela principal (selecione na lista)</td><td><code>veiculo_tempo_real</code></td></tr>
                <tr><td><strong>Visibilidade</strong></td><td>Quem pode usar este dataset</td><td><code>Publico</code> ou <code>Privado</code></td></tr>
            </tbody>
        </table>
        <p>Clique em <strong>Proximo &rarr;</strong></p>

        <h3>4.3 Passo 2 — Relacoes</h3>
        <p>O sistema mostra as tabelas relacionadas a sua tabela base.</p>
        <div class="step-item"><span class="step-number">1</span><p><strong>Clique no cartao</strong> de cada tabela para inclui-la:</p></div>
        <ul>
            <li>Cartao com <strong>borda verde e icone &#10003;</strong> = incluida</li>
            <li>Cartao com <strong>borda cinza e icone +</strong> = nao incluida</li>
        </ul>
        <blockquote>Se nao houver relacoes, volte ao Catalogo de Dados e cadastre-as.</blockquote>
        <p>Clique em <strong>Proximo &rarr;</strong></p>

        <h3>4.4 Passo 3 — Campos</h3>
        <p>Selecione quais colunas farao parte do dataset.</p>
        <div class="step-item"><span class="step-number">1</span><p>Para cada coluna desejada, <strong>marque o checkbox</strong> a esquerda</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Selecione o <strong>Tipo</strong>:</p></div>
        <ul>
            <li><strong>Dimensao</strong> — para agrupar dados (nomes, categorias, locais)</li>
            <li><strong>Medida</strong> — para valores numericos (contagens, somas, medias)</li>
            <li><strong>Tempo</strong> — para colunas de data/hora</li>
        </ul>
        <div class="step-item"><span class="step-number">3</span><p>Se escolher <strong>Medida</strong>, selecione a <strong>Agregacao</strong>:</p></div>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Agregacao</th><th>O que faz</th></tr></thead>
            <tbody>
                <tr><td><code>SUM</code></td><td>Soma dos valores</td></tr>
                <tr><td><code>AVG</code></td><td>Media dos valores</td></tr>
                <tr><td><code>COUNT</code></td><td>Contagem de registros</td></tr>
                <tr><td><code>COUNT DISTINCT</code></td><td>Contagem de valores unicos</td></tr>
                <tr><td><code>MIN</code> / <code>MAX</code></td><td>Menor / maior valor</td></tr>
            </tbody>
        </table>

        <blockquote>
            <strong>Exemplo pratico:</strong> Para "Total de passagens por equipamento":<br>
            &bull; Marque <code>nome_equipamento</code> como <strong>Dimensao</strong><br>
            &bull; Marque <code>id</code> como <strong>Medida</strong> com agregacao <strong>COUNT</strong>
        </blockquote>
        <p>Clique em <strong>Proximo &rarr;</strong></p>

        <h3>4.5 Passo 4 — Campos Calculados (Opcional)</h3>
        <p>Permite criar colunas derivadas com expressoes SQL simples. Se nao precisar, clique em <strong>Proximo</strong>.</p>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Campo</th><th>O que preencher</th><th>Exemplo</th></tr></thead>
            <tbody>
                <tr><td><strong>Alias</strong></td><td>Identificador interno (sem espacos)</td><td><code>faixa_velocidade</code></td></tr>
                <tr><td><strong>Rotulo</strong></td><td>Nome exibido nos graficos</td><td><code>Faixa de Velocidade</code></td></tr>
                <tr><td><strong>Tipo</strong></td><td>Dimensao ou Medida</td><td><code>Dimensao</code></td></tr>
                <tr><td><strong>Expressao</strong></td><td>Formula SQL</td><td><code>CASE WHEN velocidade &gt; 60 THEN 'alta' ELSE 'normal' END</code></td></tr>
            </tbody>
        </table>
        <p>A validacao em tempo real indica: <span class="text-success">&#10003; verde = valida</span> | <span class="text-danger">&#10007; vermelho = erro</span></p>

        <h3>4.6 Passo 5 — Preview</h3>
        <div class="step-item"><span class="step-number">1</span><p>Clique em <strong>Carregar preview</strong> para ver ate 50 linhas de dados</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Verifique se as colunas e valores estao corretos</p></div>
        <blockquote>Se aparecer <em>"Campo '...' nao existe no dataset"</em> ou outra mensagem de validacao, corrija o campo indicado e salve novamente. Veja <a href="#problemas">Resolucao de Problemas</a>.</blockquote>

        <h3>4.7 Passo 6 — Publicar</h3>
        <div class="step-item"><span class="step-number">1</span><p>A tela mostra um resumo do dataset</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Clique em <strong>Publicar Dataset</strong> (botao verde) para tornar o dataset disponivel para widgets</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Ou clique em <strong>Salvar Rascunho</strong> para finalizar depois</p></div>

        <!-- ============================================================ -->
        <h2 id="painel">5. Etapa 3 — Criar um Painel</h2>

        <h3>5.1 Acessar a galeria</h3>
        <div class="step-item"><span class="step-number">1</span><p>No menu lateral, clique em <strong>Paineis de Analise</strong></p></div>
        <p>A tela mostra tres abas: <strong>Meus Paineis</strong>, <strong>Compartilhados</strong> e <strong>Todos</strong>.</p>

        <h3>5.2 Criar um novo Painel</h3>
        <div class="step-item"><span class="step-number">1</span><p>Clique em <strong>Novo Painel</strong> (canto superior direito)</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Digite um nome descritivo (ex.: <code>Operacao Diaria - Zona Norte</code>)</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Voce e redirecionado ao <strong>Editor do Painel</strong> — uma tela em branco pronta para widgets</p></div>

        <h3>5.3 Estrutura do Editor</h3>
        <div class="layout-diagram">&#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9516;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9488;
&#9474;              &#9474;  [Nome]    [Refresh] [+Widget] [&#9881;] [Salvar] &#9474;
&#9474;  Lista de    &#9474;                                               &#9474;
&#9474;  Paineis     &#9474;           Area do Dashboard                   &#9474;
&#9474;  (lateral)   &#9474;         (arraste e redimensione               &#9474;
&#9474;              &#9474;          os widgets aqui)                     &#9474;
&#9492;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9524;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9496;</div>

        <!-- ============================================================ -->
        <h2 id="widgets">6. Etapa 4 — Adicionar Widgets</h2>

        <h3>6.1 Abrir o Editor de Widget</h3>
        <div class="step-item"><span class="step-number">1</span><p>No editor do painel, clique em <strong>+ Widget</strong> na barra superior</p></div>

        <h3>6.2 Selecionar Dataset e Tipo</h3>
        <div class="step-item"><span class="step-number">1</span><p>Em <strong>Dataset</strong>, selecione o dataset publicado</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Em <strong>Titulo</strong>, de um nome ao widget</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Em <strong>Tipo de Visualizacao</strong>, selecione:</p></div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Tipo</th><th>Icone</th><th>Melhor para</th></tr></thead>
            <tbody>
                <tr><td><strong>Barras</strong></td><td><i class="bi bi-bar-chart"></i></td><td>Comparar quantidades entre categorias</td></tr>
                <tr><td><strong>Linhas</strong></td><td><i class="bi bi-graph-up"></i></td><td>Tendencias ao longo do tempo</td></tr>
                <tr><td><strong>Area</strong></td><td><i class="bi bi-graph-up-arrow"></i></td><td>Tendencias com enfase no volume</td></tr>
                <tr><td><strong>Pizza</strong></td><td><i class="bi bi-pie-chart"></i></td><td>Proporcoes (maximo 7-8 fatias)</td></tr>
                <tr><td><strong>Rosca</strong></td><td><i class="bi bi-bullseye"></i></td><td>Igual a pizza, com espaco central</td></tr>
                <tr><td><strong>Dispersao</strong></td><td><i class="bi bi-dot"></i></td><td>Correlacao entre duas medidas</td></tr>
                <tr><td><strong>Combinado</strong></td><td><i class="bi bi-bar-chart-line"></i></td><td>Barras + linhas no mesmo grafico</td></tr>
                <tr><td><strong>Funil</strong></td><td><i class="bi bi-funnel"></i></td><td>Processos com etapas sequenciais</td></tr>
                <tr><td><strong>Gauge</strong></td><td><i class="bi bi-speedometer2"></i></td><td>Indicador tipo velocimetro</td></tr>
                <tr><td><strong>Mapa de Calor</strong></td><td><i class="bi bi-grid-3x3"></i></td><td>Intensidade em grade</td></tr>
                <tr><td><strong>KPI</strong></td><td><i class="bi bi-123"></i></td><td>Card com numero grande e variacao</td></tr>
                <tr><td><strong>Tabela</strong></td><td><i class="bi bi-table"></i></td><td>Dados tabulares com ordenacao</td></tr>
                <tr><td><strong>Tabela Dinamica</strong></td><td><i class="bi bi-grid-3x3-gap"></i></td><td>Pivot table (linhas x colunas x valores)</td></tr>
            </tbody>
        </table>

        <h3>6.3 Mapear Campos nos Eixos</h3>
        <p>Apos selecionar o dataset, arraste os campos disponiveis para as zonas de eixo:</p>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Zona</th><th>O que colocar</th><th>Efeito</th></tr></thead>
            <tbody>
                <tr><td><strong>Eixo X / Dimensoes</strong></td><td>Campos de agrupamento</td><td>Define categorias no eixo horizontal</td></tr>
                <tr><td><strong>Eixo Y / Medidas</strong></td><td>Campos numericos</td><td>Define valores no eixo vertical</td></tr>
                <tr><td><strong>Cores / Series</strong></td><td>Campo de agrupamento (opcional)</td><td>Divide dados em series coloridas</td></tr>
            </tbody>
        </table>

        <blockquote>
            <strong>Exemplo — Grafico de barras "Passagens por Equipamento":</strong><br>
            &bull; Arraste <code>equipamento</code> para <strong>Eixo X</strong><br>
            &bull; Arraste <code>total_passagens</code> para <strong>Eixo Y</strong><br>
            <br>
            <strong>Exemplo — Linhas "Passagens por Dia e Equipamento":</strong><br>
            &bull; Arraste <code>data</code> para <strong>Eixo X</strong><br>
            &bull; Arraste <code>total_passagens</code> para <strong>Eixo Y</strong><br>
            &bull; Arraste <code>equipamento</code> para <strong>Cores / Series</strong><br>
            &bull; Em <strong>Dimensão Temporal</strong>, escolha a <strong>Granularidade</strong> <em>Dia</em>
        </blockquote>

        <p>Um campo de data arrastado para <strong>Eixo X</strong> passa a ser a <strong>dimensão temporal</strong> do widget:
        os valores são agrupados pela <strong>Granularidade</strong> escolhida (dia, semana, mês, trimestre ou ano).
        Com <strong>Sem agrupamento</strong>, a data serve apenas para filtrar o <strong>Período</strong>; com
        <strong>Todo o período</strong>, nenhum recorte de data é aplicado. KPI, gauge, pizza, rosca e funil
        usam a data só como filtro de período, para que cada categoria apareça uma única vez.</p>

        <p>Ao passar o mouse sobre um gráfico, o <strong>tooltip</strong> mostra o nome amigável do campo, o período
        (ex.: <em>out/2026</em>) e o valor no formato brasileiro (ex.: <em>12.345,6</em>).</p>

        <p>O <strong>preview automatico</strong> atualiza a cada alteracao (apos meio segundo).</p>

        <h3>6.4 Filtros (Opcional)</h3>
        <div class="step-item"><span class="step-number">1</span><p>Na secao <strong>Filtros</strong>, clique em <strong>+ Condicao</strong></p></div>
        <div class="step-item"><span class="step-number">2</span><p>Selecione <strong>Campo</strong>, <strong>Operador</strong> e digite o <strong>Valor</strong></p></div>
        <div class="step-item"><span class="step-number">3</span><p>As condições são combinadas com <strong>E</strong>: o widget mostra apenas os registros que atendem a todas elas</p></div>

        <h3>6.5 Opcoes Visuais</h3>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Opcao</th><th>O que faz</th></tr></thead>
            <tbody>
                <tr><td><strong>Paleta</strong></td><td>Esquema de cores: Padrao, Azul, Verde</td></tr>
                <tr><td><strong>Orientacao</strong></td><td>Vertical ou Horizontal</td></tr>
                <tr><td><strong>Rotulos</strong></td><td>Mostrar valores sobre barras/fatias</td></tr>
                <tr><td><strong>Empilhado</strong></td><td>Empilhar barras em vez de lado a lado</td></tr>
                <tr><td><strong>Limite (Top N)</strong></td><td>Quantos registros mostrar (padrao: 20)</td></tr>
            </tbody>
        </table>

        <h3>6.6 Salvar o Widget</h3>
        <div class="step-item"><span class="step-number">1</span><p>Verifique o preview a direita</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Clique em <strong>Salvar e Voltar</strong> para retornar ao painel</p></div>

        <!-- ============================================================ -->
        <h2 id="kpi">7. Etapa 5 — Montar um KPI</h2>

        <p>Um KPI e um card com numero grande, ideal para metricas principais.</p>

        <h3>7.1 Criar um Widget KPI</h3>
        <div class="step-item"><span class="step-number">1</span><p>No painel, clique em <strong>+ Widget</strong></p></div>
        <div class="step-item"><span class="step-number">2</span><p>Selecione o <strong>Dataset</strong></p></div>
        <div class="step-item"><span class="step-number">3</span><p>Em <strong>Tipo</strong>, selecione <strong>KPI</strong> (<i class="bi bi-123"></i>)</p></div>
        <div class="step-item"><span class="step-number">4</span><p>Arraste <strong>um campo numerico</strong> para <strong>Eixo Y / Medidas</strong></p></div>
        <div class="step-item"><span class="step-number">5</span><p>Clique em <strong>Salvar e Voltar</strong></p></div>

        <h3>7.2 Exemplo — 4 KPIs para painel operacional</h3>
        <table class="table table-bordered table-sm">
            <thead><tr><th>KPI</th><th>Campo (Medida)</th><th>Agregacao</th><th>Filtro sugerido</th></tr></thead>
            <tbody>
                <tr><td><strong>Total de Passagens</strong></td><td><code>id</code></td><td>COUNT</td><td>Periodo: Hoje</td></tr>
                <tr><td><strong>Velocidade Media</strong></td><td><code>velocidade</code></td><td>AVG</td><td>Periodo: Hoje</td></tr>
                <tr><td><strong>Equipamentos Ativos</strong></td><td><code>id_equipamento</code></td><td>COUNT DISTINCT</td><td>Periodo: Hoje</td></tr>
                <tr><td><strong>Infracoes Detectadas</strong></td><td><code>id</code></td><td>COUNT</td><td><code>tipo = 'infracao'</code></td></tr>
            </tbody>
        </table>

        <h3>7.3 Layout sugerido</h3>
        <div class="row g-2 mb-3">
            <div class="col-3"><div class="card text-center p-3 border-primary"><h3 class="mb-0 text-primary">12.458</h3><small class="text-muted">Passagens</small></div></div>
            <div class="col-3"><div class="card text-center p-3 border-success"><h3 class="mb-0 text-success">47 km/h</h3><small class="text-muted">Vel. Media</small></div></div>
            <div class="col-3"><div class="card text-center p-3 border-info"><h3 class="mb-0 text-info">32</h3><small class="text-muted">Equip. Ativos</small></div></div>
            <div class="col-3"><div class="card text-center p-3 border-danger"><h3 class="mb-0 text-danger">156</h3><small class="text-muted">Infracoes</small></div></div>
        </div>
        <p class="text-muted" style="font-size:0.82rem">Os KPIs ficam melhores como cards pequenos na primeira linha do painel, seguidos de graficos maiores abaixo.</p>

        <!-- ============================================================ -->
        <h2 id="organizar">8. Etapa 6 — Organizar e Personalizar</h2>

        <h3>8.1 Mover e redimensionar</h3>
        <ul>
            <li><strong>Mover</strong>: clique e segure o titulo do widget, arraste para a posicao</li>
            <li><strong>Redimensionar</strong>: arraste a borda ou canto inferior do widget</li>
            <li>A grade tem <strong>12 colunas</strong> de largura</li>
            <li>O layout e <strong>salvo automaticamente</strong> 1,5 segundo apos qualquer alteracao</li>
        </ul>

        <h3>8.2 Menu do widget (&#8942;)</h3>
        <p>Clique no icone de tres pontos (<strong>&#8942;</strong>) no canto superior direito de qualquer widget:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Opcao</th><th>O que faz</th></tr></thead>
            <tbody>
                <tr><td><strong>Editar</strong></td><td>Abre o editor de widget para modificar</td></tr>
                <tr><td><strong>Ver como tabela</strong></td><td>Alterna entre grafico e tabela de dados</td></tr>
                <tr><td><strong>Atualizar</strong></td><td>Recarrega os dados deste widget</td></tr>
                <tr><td><strong>Exportar CSV / XLSX</strong></td><td>Baixa os dados como arquivo</td></tr>
                <tr><td><strong>Remover</strong></td><td>Exclui o widget (com confirmacao)</td></tr>
            </tbody>
        </table>

        <h3>8.3 Atualizacao automatica</h3>
        <p>Na barra superior, use a lista de intervalo de refresh:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Opcao</th><th>Comportamento</th></tr></thead>
            <tbody>
                <tr><td>Sem refresh</td><td>Atualiza somente manualmente</td></tr>
                <tr><td>1 min / 5 min / 15 min</td><td>Atualiza todos os widgets automaticamente</td></tr>
            </tbody>
        </table>
        <p>O indicador de cor mostra quanto tempo se passou desde a ultima atualizacao: <span class="text-success">&#9679; verde</span> &lt;5min, <span class="text-warning">&#9679; amarelo</span> 5-15min, <span class="text-danger">&#9679; vermelho</span> &gt;15min.</p>

        <!-- ============================================================ -->
        <h2 id="compartilhar">9. Etapa 7 — Compartilhar e Exportar</h2>

        <p>Use o menu <strong>&#9881;</strong> (engrenagem) na barra superior do painel:</p>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Acao</th><th>Como funciona</th></tr></thead>
            <tbody>
                <tr><td><strong>Compartilhar</strong></td><td>Informe o ID do usuario e o nivel de acesso (<code>ver</code> ou <code>editar</code>)</td></tr>
                <tr><td><strong>Duplicar</strong></td><td>Cria uma copia do painel com todos os widgets</td></tr>
                <tr><td><strong>Criar versao</strong></td><td>Salva um snapshot do estado atual</td></tr>
                <tr><td><strong>Historico</strong></td><td>Lista versoes salvas com opcao de <strong>Restaurar</strong></td></tr>
                <tr><td><strong>Exportar todos (CSV)</strong></td><td>Baixa dados de todos os widgets como CSV</td></tr>
                <tr><td><strong>Imprimir / PDF</strong></td><td>Abre a tela de impressao do navegador (selecione "Salvar como PDF")</td></tr>
            </tbody>
        </table>

        <!-- ============================================================ -->
        <h2 id="problemas">10. Resolucao de Problemas</h2>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>"A consulta excedeu 30 s"</strong></div>
            <div class="card-body">
                <p class="mb-1"><strong>Causa:</strong> A consulta percorre dados demais (por exemplo, todas as passagens sem periodo).</p>
                <p class="mb-0"><strong>Solucao:</strong> Defina um periodo no widget ou nos filtros do painel, ou reduza a quantidade de campos.</p>
            </div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>"A medida ... seria multiplicada pela relacao 1:N"</strong></div>
            <div class="card-body">
                <p class="mb-0"><strong>Causa:</strong> Somar ou contar uma tabela "um" junto com campos de uma tabela "muitos" repetiria cada valor.
                <strong>Solucao:</strong> use COUNT_DISTINCT, MIN ou MAX, ou crie um dataset com a tabela "muitos" como base.</p>
            </div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>"Nenhuma tabela encontrada"</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solucao:</strong> Peca ao administrador para cadastrar tabelas no Catalogo de Dados (<a href="#catalogo">Etapa 1</a>).</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Widget mostra "Erro ao carregar dados"</strong></div>
            <div class="card-body">
                <ul class="mb-0">
                    <li>O dataset nao esta publicado (status "Rascunho") — publique-o</li>
                    <li>Um campo do widget foi removido do dataset — a mensagem informa qual</li>
                    <li>A tabela no banco nao existe ou mudou de nome</li>
                </ul>
            </div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Nao consigo ver os menus</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solucao:</strong> Peca ao administrador para adicionar seu usuario ao grupo 41 (Analista) ou 11 (Administrador).</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Widgets nao atualizam automaticamente</strong></div>
            <div class="card-body"><p class="mb-0">Verifique se o intervalo de refresh esta configurado na barra superior do painel (padrao: "Sem refresh").</p></div>
        </div>

        <!-- ============================================================ -->
        <h2 id="glossario">11. Glossario</h2>

        <table class="table table-bordered table-sm">
            <thead><tr><th style="width:160px">Termo</th><th>Definicao</th></tr></thead>
            <tbody>
                <tr><td><strong>Agregacao</strong></td><td>Operacao matematica aplicada a uma medida: SUM, AVG, COUNT, MIN, MAX</td></tr>
                <tr><td><strong>Catalogo de Dados</strong></td><td>Registro das tabelas e colunas do banco disponiveis para datasets</td></tr>
                <tr><td><strong>Dashboard</strong></td><td>Sinonimo de Painel — tela com graficos, tabelas e KPIs</td></tr>
                <tr><td><strong>Dataset</strong></td><td>Modelo de dados que define quais tabelas e colunas alimentam os widgets</td></tr>
                <tr><td><strong>Dimensao</strong></td><td>Campo usado para agrupar ou categorizar dados (eixo X)</td></tr>
                <tr><td><strong>Drill-down</strong></td><td>Em gráficos com o tempo no eixo, clicar em um período (ex.: um mês) mostra esse período no nível seguinte (semanas, dias...); o caminho aparece acima do gráfico para voltar</td></tr>
                <tr><td><strong>Filtro cruzado</strong></td><td>Clicar em uma categoria (barra, fatia) filtra os demais widgets do mesmo dataset; o selo amarelo no título remove o filtro</td></tr>
                <tr><td><strong>KPI</strong></td><td>Key Performance Indicator — indicador numerico destacado</td></tr>
                <tr><td><strong>Medida</strong></td><td>Campo numerico que sera somado, contado ou calculado (eixo Y)</td></tr>
                <tr><td><strong>Painel</strong></td><td>Tela do dashboard que contem um ou mais widgets em grade</td></tr>
                <tr><td><strong>Widget</strong></td><td>Componente visual: grafico, tabela, KPI, mapa, etc.</td></tr>
            </tbody>
        </table>

        <hr class="my-4">
        <p class="text-muted text-center" style="font-size:0.8rem">
            <i class="bi bi-info-circle me-1"></i>
            Manual do Dashboard Builder — Muralha Digital
        </p>

    </div>
</div>
