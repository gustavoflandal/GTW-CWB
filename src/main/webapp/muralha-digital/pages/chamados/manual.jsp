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
    .manual-container table { font-size: 0.88rem; }
    .manual-container th { background-color: #f0f6fc; white-space: nowrap; }
    .manual-container code { font-size: 0.85em; color: #0d6efd; background: #f8f9fa; padding: 0.15em 0.35em; border-radius: 3px; }
    .manual-container blockquote { border-left: 4px solid #0d75bf; background: #f0f6fc; padding: 0.75rem 1rem; margin: 1rem 0; border-radius: 0 6px 6px 0; }
    .manual-container blockquote p { margin-bottom: 0; }
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
                <li class="breadcrumb-item"><a href="/muralha-digital/pages/chamados/index.jsp"><i class="bi bi-headset"></i> Chamados</a></li>
                <li class="breadcrumb-item active">Manual</li>
            </ol>
        </nav>
        <button class="btn btn-outline-secondary btn-sm" onclick="window.print()">
            <i class="bi bi-printer"></i> Imprimir / PDF
        </button>
    </div>

    <div class="manual-container">

        <!-- ============================================================ -->
        <h1><i class="bi bi-book me-2" style="color:#0d75bf"></i>Manual do Sistema de Chamados</h1>
        <p class="text-muted mb-4">Guia completo para abrir, acompanhar e atender chamados de suporte técnico no sistema Muralha Digital.</p>

        <!-- ============================================================ -->
        <h2 id="visao-geral">1. Visão Geral</h2>

        <p>O <strong>Sistema de Chamados</strong> é a central de suporte técnico da Muralha Digital. Cada pedido de suporte vira um <strong>chamado</strong> com número de protocolo único, prioridade, prazos e histórico completo. O ciclo de um chamado segue este fluxo:</p>

        <div class="flow-diagram">
            <div class="flow-box" style="background:#e9ecef;color:#495057;">
                <i class="bi bi-plus-circle d-block" style="font-size:1.5rem"></i>
                Abertura
                <small class="d-block fw-normal" style="font-size:0.75rem">solicitante registra o pedido</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#cfe2ff;color:#084298;">
                <i class="bi bi-tools d-block" style="font-size:1.5rem"></i>
                Atendimento
                <small class="d-block fw-normal" style="font-size:0.75rem">equipe técnica trata</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#fff3cd;color:#664d03;">
                <i class="bi bi-hourglass-split d-block" style="font-size:1.5rem"></i>
                Aguardando solicitante
                <small class="d-block fw-normal" style="font-size:0.75rem">se faltar informação</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#d1e7dd;color:#0f5132;">
                <i class="bi bi-check2-circle d-block" style="font-size:1.5rem"></i>
                Resolução
                <small class="d-block fw-normal" style="font-size:0.75rem">solução informada</small>
            </div>
            <div class="flow-arrow"><i class="bi bi-arrow-right"></i></div>
            <div class="flow-box" style="background:#f8d7da;color:#842029;">
                <i class="bi bi-star d-block" style="font-size:1.5rem"></i>
                Avaliação
                <small class="d-block fw-normal" style="font-size:0.75rem">fechar ou reabrir</small>
            </div>
        </div>

        <table class="table table-bordered table-sm">
            <thead><tr><th>Conceito</th><th>O que é</th><th>Quem</th></tr></thead>
            <tbody>
                <tr><td><strong>Chamado</strong></td><td>Pedido de suporte registrado no sistema, com histórico completo de tudo o que acontece com ele</td><td>Solicitante ou Gestor do contrato abre</td></tr>
                <tr><td><strong>Protocolo</strong></td><td>Número único do chamado, no formato <code>CH-AAAA-NNNNNN</code> (por exemplo, <code>CH-2026-000123</code>)</td><td>Gerado pelo sistema</td></tr>
                <tr><td><strong>Prioridade</strong></td><td>Grau de urgência (Crítica, Alta, Média, Baixa). A escolhida na abertura é uma sugestão: a equipe técnica pode reclassificá-la com justificativa</td><td>Solicitante sugere; Equipe técnica reclassifica</td></tr>
                <tr><td><strong>SLA</strong></td><td>Prazos de atendimento e de resolução definidos pela prioridade, contados em horas corridas (24x7)</td><td>Administrador configura</td></tr>
                <tr><td><strong>Categoria</strong></td><td>Classificação do assunto do chamado, em dois níveis (categoria e subcategoria); algumas exigem informar o equipamento</td><td>Administrador cadastra</td></tr>
                <tr><td><strong>Avaliação</strong></td><td>Nota de 1 a 5 e comentário sobre o atendimento, dados ao fechar o chamado resolvido</td><td>Solicitante ou Gestor do contrato</td></tr>
            </tbody>
        </table>

        <p><strong>Estados de um chamado:</strong></p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Status</th><th>Significado</th></tr></thead>
            <tbody>
                <tr><td>Aberto</td><td>Registrado, ainda sem atendimento</td></tr>
                <tr><td>Em atendimento</td><td>A equipe técnica está tratando</td></tr>
                <tr><td>Aguardando solicitante</td><td>A equipe pediu uma informação; o relógio de prazo de resolução fica <strong>pausado</strong></td></tr>
                <tr><td>Resolvido</td><td>A equipe informou a solução; o solicitante deve avaliar e fechar, ou reabrir</td></tr>
                <tr><td>Fechado</td><td>Encerrado (avaliado pelo solicitante ou fechado automaticamente)</td></tr>
                <tr><td>Cancelado</td><td>Encerrado sem atendimento</td></tr>
            </tbody>
        </table>

        <p>Toda ação sobre um chamado exige uma <strong>observação</strong>, que vai para o histórico (com exceção de "Assumir", em que é opcional).</p>

        <!-- ============================================================ -->
        <h2 id="acesso">2. Acesso ao Sistema</h2>

        <h3>2.1 Perfis necessários</h3>
        <p>O que você vê e pode fazer depende do perfil que o administrador atribuiu ao seu usuário. Um usuário pode ter mais de um perfil; nesse caso, recebe a soma das permissões.</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Perfil</th><th>Quem é</th><th>Permissão</th></tr></thead>
            <tbody>
                <tr><td><strong>Chamados - Solicitante</strong></td><td>Usuário da contratante</td><td>Abrir chamados; ver, responder, avaliar, reabrir e exportar <strong>apenas os próprios</strong>; cancelar os próprios enquanto estiverem "Aberto"</td></tr>
                <tr><td><strong>Chamados - Gestor do contrato</strong></td><td>Contratante</td><td>Abrir chamados; avaliar, reabrir e cancelar <strong>os chamados que abriu</strong>; responder em <strong>qualquer chamado aguardando retorno</strong>; ver <strong>todos</strong> os chamados; relatórios e exportação geral</td></tr>
                <tr><td><strong>Chamados - Equipe tecnica</strong></td><td>Contratada</td><td>Ver todos os chamados; assumir, iniciar, aguardar, resolver e reclassificar a prioridade; cancelar enquanto o chamado estiver aberto, em atendimento ou aguardando solicitante; relatórios</td></tr>
                <tr><td><strong>Administradores</strong></td><td>Administração do sistema</td><td>Configuração (parâmetros de SLA, perfis de acesso, prioridades, categorias, SMTP); ver todos os chamados e relatórios em <strong>somente leitura</strong></td></tr>
            </tbody>
        </table>

        <blockquote>Se você não vê os menus ou cartões descritos abaixo, solicite ao administrador que adicione seu usuário ao grupo apropriado. O administrador, sozinho, não atende chamados: para isso precisa também do perfil de equipe técnica.</blockquote>

        <h3>2.2 Navegação pelo menu</h3>
        <p>Entre no sistema, abra a tela inicial da Muralha Digital e clique na categoria <strong>Sistema de Chamados</strong>. Dentro dela aparecem os cartões a que o seu perfil dá acesso:</p>
        <ul>
            <li><strong>Chamados</strong> — lista, abertura e acompanhamento</li>
            <li><strong>Relatórios</strong> — indicadores (gestor, equipe técnica e administrador)</li>
            <li><strong>Configuração</strong> — somente para o administrador</li>
        </ul>
        <p>Dentro da tela <strong>Chamados</strong>, os botões "Relatórios" e "Configuração" no canto superior direito também levam às outras telas, quando o seu perfil permite. Os botões <strong>Manual</strong>, presentes nas telas do sistema, abrem este guia.</p>

        <!-- ============================================================ -->
        <h2 id="abrir">3. Abrir um Chamado <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Solicitante / Gestor</span></h2>

        <blockquote><strong>Quem faz:</strong> o solicitante e o gestor do contrato. A equipe técnica e o administrador não abrem chamados.</blockquote>

        <h3>3.1 Preencher e enviar</h3>
        <div class="step-item"><span class="step-number">1</span><p>Na tela <strong>Chamados</strong>, clique em <strong>Novo chamado</strong>.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Escolha a <strong>Categoria</strong> e depois a <strong>Subcategoria</strong>.</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Se a categoria exigir equipamento (por exemplo, "Equipamento"), aparece o campo <strong>Equipamento</strong>, que passa a ser obrigatório. Escolha o equipamento afetado.</p></div>
        <div class="step-item"><span class="step-number">4</span><p>Escolha a <strong>Prioridade</strong>. Abaixo do campo aparecem os prazos de atendimento e de resolução dessa prioridade. A prioridade que você escolhe é uma sugestão: a equipe técnica pode reclassificá-la com justificativa.</p></div>
        <div class="step-item"><span class="step-number">5</span><p>Preencha o <strong>Título</strong> (até 150 caracteres) e a <strong>Descrição</strong>.</p></div>
        <div class="step-item"><span class="step-number">6</span><p>Se quiser, anexe arquivos (<strong>PDF, PNG ou JPG</strong>, até 5 por envio; o tamanho máximo por arquivo é definido pelo administrador, 10 MB por padrão).</p></div>
        <div class="step-item"><span class="step-number">7</span><p>Clique em <strong>Abrir chamado</strong>.</p></div>

        <h3>3.2 O que acontece ao salvar</h3>
        <p>Uma janela mostra o <strong>número de protocolo</strong>, a <strong>prioridade</strong> atribuída e os <strong>prazos previstos</strong> de atendimento e de resolução. Guarde o protocolo. Uma confirmação com as mesmas informações é enviada ao seu e-mail cadastrado (se o seu usuário não tiver e-mail, a confirmação na tela continua valendo, mas nenhum e-mail é enviado).</p>
        <p>Se algum anexo não puder ser gravado, o chamado inteiro não é aberto e você vê a mensagem "Não foi possível gravar os anexos; tente novamente".</p>

        <!-- ============================================================ -->
        <h2 id="acompanhar">4. Acompanhar e Responder <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Solicitante / Gestor</span></h2>

        <blockquote><strong>Quem faz:</strong> o solicitante vê apenas os próprios chamados. O gestor do contrato vê <strong>todos</strong> os chamados, pode filtrar e exportar o conjunto geral e pode <strong>responder</strong> a <strong>qualquer chamado aguardando retorno</strong>, mesmo que não tenha sido aberto por ele.</blockquote>

        <h3>4.1 Acompanhar</h3>
        <p>A tela <strong>Chamados</strong> lista os chamados e se atualiza sozinha a cada 30 segundos (acima da tabela aparece "Atualizado às ..."; linhas alteradas ficam destacadas por instantes).</p>
        <div class="step-item"><span class="step-number">1</span><p>Use os filtros (protocolo, status, prioridade, categoria, equipamento, período "De" e "Até") e o botão de busca; o botão com "X" limpa os filtros.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Cada linha mostra protocolo, título, categoria, equipamento, prioridade, status, responsável, abertura e duas marcas de prazo (<strong>SLA atend.</strong> e <strong>SLA resol.</strong>; veja a <a href="#sla">seção 9</a>). A lista tem 50 chamados por página.</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Clique numa linha para abrir o <strong>detalhe</strong>.</p></div>

        <p>No detalhe você vê:</p>
        <ul>
            <li>os dados do chamado (protocolo, prioridade, categoria, equipamento, solicitante, responsável, abertura);</li>
            <li>o quadro <strong>Prazos (24x7)</strong>, com a data prevista, a marca de prazo e uma contagem regressiva ("Restam 3h 20min" ou "Vencido há ..."). Quando o relógio está pausado, aparece "relógio pausado";</li>
            <li>a <strong>Descrição</strong>, os <strong>Anexos</strong> (clique para baixar) e o <strong>Histórico</strong> completo, com a mudança de status, o autor, a data e o texto de cada evento.</li>
        </ul>
        <p>A tela de detalhe também se atualiza a cada 30 segundos.</p>

        <h3>4.2 Responder</h3>
        <p>Quando o status é <strong>Aguardando solicitante</strong>, a equipe está esperando uma informação sua.</p>
        <div class="step-item"><span class="step-number">1</span><p>No detalhe, clique em <strong>Responder</strong>.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Escreva a observação (obrigatória) e anexe arquivos se precisar (até 5).</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Confirme. O chamado volta para "Em atendimento" e o relógio de resolução volta a correr.</p></div>

        <h3>4.3 Cancelar</h3>
        <p>Enquanto o chamado estiver <strong>Aberto</strong> (ainda sem atendimento), você pode clicar em <strong>Cancelar</strong> e informar o motivo. O solicitante cancela apenas os próprios chamados; o gestor, os que abriu.</p>

        <!-- ============================================================ -->
        <h2 id="avaliar">5. Avaliar o Atendimento <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Solicitante / Gestor</span></h2>

        <blockquote><strong>Quem faz:</strong> quem abriu o chamado. Quando o status é <strong>Resolvido</strong>, há duas opções: avaliar e fechar, ou reabrir.</blockquote>

        <h3>5.1 Avaliar e fechar</h3>
        <div class="step-item"><span class="step-number">1</span><p>No detalhe do chamado resolvido, escolha uma nota de 1 a 5 ★.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Escreva um comentário (opcional).</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Clique em <strong>Enviar avaliação</strong>. O chamado fica "Fechado".</p></div>

        <h3>5.2 Reabrir</h3>
        <p>Se a solução não resolveu, clique em <strong>Reabrir</strong> e escreva a justificativa. O chamado volta para "Em atendimento".</p>

        <h3>5.3 Fechamento automático</h3>
        <p>Se nenhuma das duas ações for feita em alguns dias (5 por padrão, ajustável pelo administrador), o sistema <strong>fecha o chamado automaticamente</strong> e o detalhe mostra "Fechado automaticamente — não avaliado".</p>

        <!-- ============================================================ -->
        <h2 id="equipe">6. Atender Chamados <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Equipe técnica</span></h2>

        <blockquote><strong>Quem faz:</strong> a equipe técnica, que enxerga todos os chamados e atende conforme o status. Para atender, o administrador também precisa ter o perfil de equipe técnica.</blockquote>

        <p>No detalhe, os botões disponíveis mudam com o status:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Ação</th><th>Quando aparece</th><th>O que faz</th></tr></thead>
            <tbody>
                <tr><td><strong>Assumir</strong></td><td>Chamado aberto ou em atendimento</td><td>Define você como responsável (observação opcional)</td></tr>
                <tr><td><strong>Iniciar atendimento</strong></td><td>Chamado aberto</td><td>Passa para "Em atendimento" e registra o primeiro atendimento</td></tr>
                <tr><td><strong>Aguardar solicitante</strong></td><td>Em atendimento</td><td>Passa para "Aguardando solicitante"; descreva o que precisa do solicitante. O relógio de resolução pausa</td></tr>
                <tr><td><strong>Resolver</strong></td><td>Em atendimento</td><td>Passa para "Resolvido"; descreva a solução</td></tr>
                <tr><td><strong>Reclassificar prioridade</strong></td><td>Qualquer chamado não encerrado</td><td>Muda a prioridade (com justificativa), recalcula os prazos e avisa o solicitante por e-mail</td></tr>
                <tr><td><strong>Cancelar</strong></td><td>Aberto, em atendimento ou aguardando solicitante</td><td>Encerra o chamado; a justificativa é obrigatória</td></tr>
            </tbody>
        </table>

        <div class="step-item"><span class="step-number">1</span><p>Abra o chamado na lista e clique na ação desejada.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Na janela que abre, escreva a observação, que vai para o histórico.</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Confirme. Se a tela avisar "Chamado alterado por outro usuário; recarregue", alguém mexeu no chamado enquanto você o olhava: recarregue a página e refaça a ação.</p></div>

        <p>A equipe também usa <strong>Relatórios</strong> (veja a <a href="#relatorios">seção 7</a>) e os botões de exportação da lista. A Configuração do sistema é exclusiva do administrador.</p>

        <!-- ============================================================ -->
        <h2 id="relatorios">7. Relatórios e Exportação <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Todos os perfis</span></h2>

        <blockquote><strong>Quem faz:</strong> a exportação da lista está disponível a todos os perfis (o solicitante exporta só os próprios chamados). A tela <strong>Relatórios</strong> é do gestor do contrato, da equipe técnica e do administrador.</blockquote>

        <h3>7.1 Exportar a lista de chamados</h3>
        <p>Na tela <strong>Chamados</strong>, os botões <strong>CSV</strong>, <strong>XLS</strong> e <strong>PDF</strong> exportam os chamados do conjunto filtrado. Os arquivos CSV e XLS trazem os filtros, a data, o usuário, o total e uma impressão digital <strong>SHA-256</strong> do conteúdo; o PDF é montado no navegador.</p>

        <h3>7.2 Gerar relatórios</h3>
        <div class="step-item"><span class="step-number">1</span><p>Abra <strong>Relatórios</strong> (cartão da categoria ou botão na tela Chamados).</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Informe os filtros: <strong>De</strong>, <strong>Até</strong>, <strong>Categoria</strong>, <strong>Equipamento</strong> e <strong>Prioridade</strong>.</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Em <strong>Agrupar por</strong>, escolha Mês, Semana, Dia, Categoria, Equipamento ou Prioridade.</p></div>
        <div class="step-item"><span class="step-number">4</span><p>Clique em <strong>Gerar</strong>.</p></div>

        <p>A tela mostra três totais (Chamados, Resolvidos e Cancelados), um gráfico e a tabela <strong>Indicadores</strong>, com, para cada grupo:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Coluna</th><th>Significado</th></tr></thead>
            <tbody>
                <tr><td>Total / Resolvidos / Cancelados</td><td>Quantidade de chamados</td></tr>
                <tr><td>% SLA atend.</td><td>Percentual com o prazo de atendimento cumprido</td></tr>
                <tr><td>% SLA resol.</td><td>Percentual com o prazo de resolução cumprido</td></tr>
                <tr><td>TMA (min)</td><td>Tempo médio até o primeiro atendimento</td></tr>
                <tr><td>TMR (min)</td><td>Tempo médio de resolução, descontando as pausas</td></tr>
                <tr><td>Nota média</td><td>Média das avaliações (1 a 5)</td></tr>
                <tr><td>% avaliados</td><td>Percentual de chamados que receberam avaliação</td></tr>
            </tbody>
        </table>
        <p>Os botões <strong>CSV</strong>, <strong>XLS</strong> e <strong>PDF</strong> da tela exportam a tabela de indicadores.</p>

        <!-- ============================================================ -->
        <h2 id="configuracao">8. Configuração <span class="badge bg-secondary fw-normal" style="font-size:0.7rem">Admin</span></h2>

        <blockquote><strong>Quem faz:</strong> apenas o administrador, na tela <strong>Configuração</strong> (cartão da categoria ou botão "Configuração" na tela Chamados). Ela tem cinco abas. Todas as alterações feitas aqui são registradas no log de auditoria do sistema.</blockquote>

        <h3 id="adm-sla">8.1 Parâmetros de SLA</h3>
        <ul>
            <li><strong>Risco de SLA (% do prazo consumido)</strong>: de 1 a 99. Acima desse percentual o chamado passa a "Em risco". Padrão: 80.</li>
            <li><strong>Fechamento automático (dias)</strong>: de 1 a 60. Dias após a resolução em que um chamado resolvido, sem avaliação nem reabertura, é fechado automaticamente. Padrão: 5.</li>
        </ul>
        <p>Clique em <strong>Salvar parâmetros</strong>. Os prazos por prioridade ficam na aba "Prioridades e prazos"; as horas são corridas (24x7).</p>

        <h3 id="adm-perfis">8.2 Perfis de acesso</h3>
        <p>A aba mostra três cartões: <strong>Solicitantes</strong>, <strong>Gestores do contrato</strong> e <strong>Equipe técnica</strong>, cada um com a lista dos usuários que têm o perfil.</p>
        <div class="step-item"><span class="step-number">1</span><p>Para <strong>incluir</strong> um usuário: digite pelo menos 2 letras do nome ou do login no campo "Adicionar usuário", clique no resultado e confirme.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Para <strong>remover</strong>: clique na lixeira ao lado do nome e confirme.</p></div>
        <p>Os administradores aparecem numa lista de <strong>somente leitura</strong> no fim da aba; o grupo de administradores não é alterado por aqui.</p>
        <blockquote>Os mesmos grupos aparecem na tela de usuários do sistema legado; incluir um usuário por lá ou por aqui tem o mesmo efeito sobre o acesso. Porém, as alterações de grupo feitas pela tela legada de usuários <strong>não aparecem no log de auditoria do sistema de chamados</strong>; só ficam registradas as feitas nesta aba (Perfis de acesso).</blockquote>

        <h3 id="adm-prioridades">8.3 Prioridades e prazos</h3>
        <p>Lista as prioridades com ordem, código, nome, prazo de atendimento e de resolução (em minutos) e situação. Use <strong>Nova prioridade</strong> ou o botão de edição da linha; no formulário informe código (maiúsculas), nome, ordem (1 = mais grave), os dois prazos em minutos e a cor do selo. Os valores iniciais sugeridos são:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Prioridade</th><th>Atendimento</th><th>Resolução</th></tr></thead>
            <tbody>
                <tr><td>Crítica</td><td>60 min</td><td>240 min</td></tr>
                <tr><td>Alta</td><td>120 min</td><td>480 min</td></tr>
                <tr><td>Média</td><td>240 min</td><td>1440 min</td></tr>
                <tr><td>Baixa</td><td>480 min</td><td>4320 min</td></tr>
            </tbody>
        </table>
        <p>Prioridades e categorias não são excluídas: quando deixam de ser usadas, são <strong>desativadas</strong> e não aparecem mais para novos chamados, mas os chamados antigos continuam intactos.</p>

        <h3 id="adm-categorias">8.4 Categorias</h3>
        <p>Lista categorias e subcategorias (dois níveis). Use <strong>Nova categoria</strong> ou a edição da linha para definir o nome, a <strong>Categoria pai</strong> (vazia para categoria de primeiro nível) e se a categoria <strong>exige equipamento</strong> (a opção vale para a categoria de primeiro nível; as subcategorias herdam). Para retirar uma categoria de uso, desative-a.</p>

        <h3 id="adm-smtp">8.5 E-mail (SMTP)</h3>
        <p>Define o servidor usado para enviar os e-mails dos chamados (confirmação de abertura e aviso de reclassificação). No alto da aba, um aviso informa qual servidor está em uso: o configurado aqui ou o da configuração geral do servidor do sistema.</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Campo</th><th>Observação</th></tr></thead>
            <tbody>
                <tr><td><strong>Servidor SMTP</strong></td><td>Nome do servidor ou endereço IPv4. Em branco = usa o servidor da configuração geral do sistema</td></tr>
                <tr><td><strong>Porta</strong></td><td>De 1 a 65535 (padrão 587)</td></tr>
                <tr><td><strong>Usuário</strong></td><td>Em branco = sem autenticação</td></tr>
                <tr><td><strong>Senha</strong></td><td>Em branco = mantém a senha atual. Marque <strong>Apagar a senha gravada</strong> para removê-la. A senha nunca é exibida</td></tr>
                <tr><td><strong>E-mail do remetente</strong></td><td>Endereço que aparece como remetente</td></tr>
                <tr><td><strong>Usar STARTTLS</strong></td><td>Liga a criptografia STARTTLS (obrigatória nesse caso: se o servidor não oferecer, o envio falha). Desligado = sem criptografia. Só vale para o servidor configurado nesta tela</td></tr>
                <tr><td><strong>Enviar e-mails dos chamados</strong></td><td>Liga ou desliga todo o envio</td></tr>
            </tbody>
        </table>

        <blockquote>São suportados apenas <strong>STARTTLS</strong> (normalmente porta 587) ou <strong>sem criptografia</strong>; a conexão SSL direta (porta 465) <strong>não é suportada</strong>. O interruptor STARTTLS só vale para o servidor configurado nesta tela: o servidor da configuração geral segue a sua própria configuração.</blockquote>

        <p>A senha é gravada <strong>cifrada</strong> e depende de uma chave de cifragem guardada no servidor, configurada pela equipe de infraestrutura. Ao trocar servidor, porta ou usuário, é preciso digitar a senha novamente (a senha gravada não é reaproveitada em outro servidor). Se a chave não estiver definida, a aba mostra um aviso e não é possível gravar uma senha nova.</p>

        <div class="step-item"><span class="step-number">1</span><p>Preencha os campos e salve.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Clique em <strong>Enviar e-mail de teste</strong>: o sistema envia uma mensagem, com a configuração gravada, para o e-mail cadastrado no seu usuário. Se houver falha, a mensagem do servidor é mostrada (sem a senha).</p></div>

        <!-- ============================================================ -->
        <h2 id="sla">9. Prazos de Atendimento (SLA)</h2>

        <p>Cada chamado tem dois prazos, definidos pela prioridade: <strong>atendimento</strong> (até o primeiro atendimento) e <strong>resolução</strong>. A contagem é em <strong>horas corridas, 24 horas por dia, 7 dias por semana</strong>.</p>
        <ul>
            <li>O relógio de resolução <strong>pausa</strong> enquanto o chamado está "Aguardando solicitante" e depois de "Resolvido"; o tempo pausado é somado ao prazo.</li>
            <li>Ao reclassificar a prioridade, os prazos são recalculados a partir da abertura.</li>
        </ul>

        <p>As marcas de prazo na lista e no detalhe:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Marca</th><th>Cor</th><th>Significado</th></tr></thead>
            <tbody>
                <tr><td>No prazo</td><td>verde</td><td>Dentro do prazo</td></tr>
                <tr><td>Em risco</td><td>amarelo</td><td>Consumiu o percentual de risco configurado ou mais</td></tr>
                <tr><td>Vencido</td><td>vermelho</td><td>O prazo passou e ainda não foi cumprido</td></tr>
                <tr><td>Cumprido</td><td>cinza</td><td>Foi cumprido dentro do prazo (definitivo)</td></tr>
                <tr><td>Violado</td><td>vermelho</td><td>Foi cumprido, mas depois do prazo (definitivo)</td></tr>
            </tbody>
        </table>
        <p>O sistema atualiza as marcas a cada 5 minutos.</p>

        <!-- ============================================================ -->
        <h2 id="problemas">10. Resolução de Problemas</h2>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Não vejo a categoria "Sistema de Chamados" ou um dos cartões</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solução:</strong> O seu usuário não tem o perfil necessário. Peça ao administrador para incluí-lo em Configuração &gt; Perfis de acesso.</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>O botão "Novo chamado" não aparece</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solução:</strong> Apenas solicitantes e gestores abrem chamados.</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>"Chamado alterado por outro usuário; recarregue"</strong></div>
            <div class="card-body">
                <p class="mb-1"><strong>Causa:</strong> Outra pessoa (ou o sistema, ao atualizar os prazos) alterou o chamado.</p>
                <p class="mb-0"><strong>Solução:</strong> Recarregue a página e repita a ação.</p>
            </div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>"Não foi possível gravar os anexos; tente novamente"</strong></div>
            <div class="card-body">
                <p class="mb-1"><strong>Causa:</strong> O servidor não conseguiu gravar os arquivos.</p>
                <p class="mb-0"><strong>Solução:</strong> Tente de novo; se persistir, avise o administrador (diretório de anexos).</p>
            </div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Anexo recusado</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solução:</strong> Só são aceitos PDF, PNG e JPG, dentro do tamanho máximo, e o conteúdo do arquivo precisa corresponder ao tipo.</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Não recebi o e-mail de confirmação</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solução:</strong> Confira se o seu usuário tem e-mail cadastrado e se o SMTP está configurado e ligado (administrador: aba E-mail (SMTP) e botão de teste).</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>Na aba SMTP aparece o aviso sobre a chave de cifragem</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solução:</strong> A chave de cifragem não está definida no servidor. Peça à equipe de infraestrutura para configurá-la.</p></div>
        </div>

        <div class="card mb-3 border-warning">
            <div class="card-header bg-warning bg-opacity-10"><strong>O gráfico dos relatórios não aparece</strong></div>
            <div class="card-body"><p class="mb-0"><strong>Solução:</strong> A tela carrega a biblioteca de gráficos da internet; verifique o acesso do computador e recarregue.</p></div>
        </div>

        <!-- ============================================================ -->
        <h2 id="glossario">11. Glossário</h2>

        <table class="table table-bordered table-sm">
            <thead><tr><th style="width:160px">Termo</th><th>Definição</th></tr></thead>
            <tbody>
                <tr><td><strong>Avaliação</strong></td><td>Nota de 1 a 5 e comentário opcional dados ao fechar um chamado resolvido</td></tr>
                <tr><td><strong>Categoria</strong></td><td>Classificação do assunto do chamado, com subcategoria; pode exigir equipamento</td></tr>
                <tr><td><strong>Chamado</strong></td><td>Pedido de suporte técnico registrado no sistema, com histórico completo</td></tr>
                <tr><td><strong>Fechamento automático</strong></td><td>Fechamento feito pelo sistema quando um chamado resolvido não é avaliado nem reaberto no prazo configurado</td></tr>
                <tr><td><strong>Prioridade</strong></td><td>Grau de urgência do chamado (Crítica, Alta, Média, Baixa); define os prazos de SLA</td></tr>
                <tr><td><strong>Protocolo</strong></td><td>Número único do chamado, no formato CH-AAAA-NNNNNN</td></tr>
                <tr><td><strong>Reclassificar</strong></td><td>Mudar a prioridade de um chamado, com justificativa, recalculando os prazos</td></tr>
                <tr><td><strong>SLA</strong></td><td>Acordo de nível de serviço — prazos de atendimento e de resolução, em horas corridas (24x7)</td></tr>
                <tr><td><strong>SMTP</strong></td><td>Servidor usado para enviar os e-mails do sistema</td></tr>
                <tr><td><strong>STARTTLS</strong></td><td>Criptografia da conexão com o servidor de e-mail, ligada na tela de Configuração</td></tr>
                <tr><td><strong>TMA</strong></td><td>Tempo médio de atendimento — até o primeiro atendimento</td></tr>
                <tr><td><strong>TMR</strong></td><td>Tempo médio de resolução, descontando as pausas</td></tr>
            </tbody>
        </table>

        <hr class="my-4">
        <p class="text-muted text-center" style="font-size:0.8rem">
            <i class="bi bi-info-circle me-1"></i>
            Manual do Sistema de Chamados — Muralha Digital
        </p>

    </div>
</div>
