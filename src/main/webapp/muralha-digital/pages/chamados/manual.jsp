<%-- src/main/webapp/muralha-digital/pages/chamados/manual.jsp
     Conteudo estatico: espelho de docs/chamados/manual-sistema-chamados.md (mantenha os dois iguais). --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

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
    .manual-container th { background-color: #f0f6fc; }
    .manual-container code { font-size: 0.85em; color: #0d6efd; background: #f8f9fa; padding: 0.15em 0.35em; border-radius: 3px; }
    .manual-container blockquote { border-left: 4px solid #0d75bf; background: #f0f6fc; padding: 0.75rem 1rem; margin: 1rem 0; border-radius: 0 6px 6px 0; }
    .manual-container blockquote p { margin-bottom: 0; }
    .manual-toc { position: sticky; top: 80px; }
    .manual-toc a { font-size: 0.82rem; color: #495057; text-decoration: none; display: block; padding: 0.2rem 0; }
    .manual-toc a:hover { color: #0d75bf; }
    .manual-container h2, .manual-container h3 { scroll-margin-top: 70px; }
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

    <div class="row">
        <div class="col-lg-3 d-none d-lg-block no-print">
            <nav class="manual-toc" aria-label="Sumário do manual">
                <div class="fw-bold small text-uppercase text-muted mb-2">Sumário</div>
                <a href="#visao-geral">1. Visão geral</a>
                <a href="#telas">2. Como chegar às telas</a>
                <a href="#solicitante">3. Solicitante</a>
                <a href="#gestor">4. Gestor do contrato</a>
                <a href="#equipe">5. Equipe técnica</a>
                <a href="#administrador">6. Administrador</a>
                <a href="#sla">7. Prazos de atendimento (SLA)</a>
                <a href="#problemas">8. Resolução de problemas</a>
            </nav>
        </div>

        <div class="col-lg-9">
            <div class="manual-container">

                <h1><i class="bi bi-book me-2" style="color:#0d75bf"></i>Manual do Sistema de Chamados — Muralha Digital</h1>
                <p class="text-muted mb-4">Este manual explica, por perfil, como usar o Sistema de Chamados (central de suporte técnico) da Muralha Digital: abrir e acompanhar chamados, responder, avaliar o atendimento, consultar relatórios, exportar registros e, para o administrador, configurar o sistema.</p>
                <p>Para a arquitetura e as regras técnicas, veja <code>docs/chamados/sistema-chamados-arquitetura.md</code>. Para o roteiro de testes, veja <code>docs/testes/22-sistema-chamados.md</code>.</p>

                <!-- ============================================================ -->
                <h2 id="visao-geral">1. Visão geral</h2>

                <p>O sistema registra cada pedido de suporte como um <strong>chamado</strong> com número de protocolo único (formato <code>CH-AAAA-NNNNNN</code>, por exemplo <code>CH-2026-000123</code>), prioridade, prazos e histórico completo.</p>

                <h3>Perfis</h3>
                <p>O que você vê e pode fazer depende do perfil que o administrador atribuiu ao seu usuário. Um usuário pode ter mais de um perfil; nesse caso, recebe a soma das permissões.</p>

                <table class="table table-bordered table-sm">
                    <thead><tr><th>Perfil</th><th>Quem é</th><th>O que pode fazer</th></tr></thead>
                    <tbody>
                        <tr><td><strong>Solicitante</strong></td><td>Usuário da contratante</td><td>Abrir chamados; ver, responder, avaliar, reabrir e exportar <strong>apenas os próprios</strong>; cancelar os próprios enquanto estiverem "Aberto"</td></tr>
                        <tr><td><strong>Gestor do contrato</strong></td><td>Contratante</td><td>Abrir chamados; avaliar, reabrir e cancelar <strong>os chamados que abriu</strong>; responder em <strong>qualquer chamado aguardando retorno</strong>; ver <strong>todos</strong> os chamados; relatórios e exportação geral</td></tr>
                        <tr><td><strong>Equipe técnica</strong></td><td>Contratada</td><td>Ver todos os chamados; assumir, iniciar, aguardar, resolver e reclassificar a prioridade; cancelar enquanto o chamado estiver aberto, em atendimento ou aguardando solicitante; relatórios</td></tr>
                        <tr><td><strong>Administrador</strong></td><td>Administração do sistema</td><td>Configuração (parâmetros de SLA, perfis de acesso, prioridades, categorias, SMTP); ver todos os chamados e relatórios em <strong>somente leitura</strong></td></tr>
                    </tbody>
                </table>

                <p>O administrador, sozinho, não atende chamados: para isso precisa também do perfil de equipe técnica.</p>

                <h3>Estados de um chamado</h3>
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
                <h2 id="telas">2. Como chegar às telas</h2>

                <ol>
                    <li>Entre no sistema e abra a tela inicial da Muralha Digital.</li>
                    <li>Clique na categoria <strong>Sistema de Chamados</strong>.</li>
                    <li>Dentro dela aparecem os cartões a que o seu perfil dá acesso:
                        <ul>
                            <li><strong>Chamados</strong>: lista, abertura e acompanhamento.</li>
                            <li><strong>Relatórios</strong>: indicadores (gestor, equipe técnica e administrador).</li>
                            <li><strong>Configuração</strong>: somente para o administrador.</li>
                        </ul>
                    </li>
                </ol>

                <p>Se um cartão não aparece, o seu usuário não tem o perfil necessário; peça ao administrador.</p>
                <p>Dentro da tela <strong>Chamados</strong>, os botões "Relatórios" e "Configuração" no canto superior direito também levam às outras telas, quando o seu perfil permite.</p>

                <!-- ============================================================ -->
                <h2 id="solicitante">3. Solicitante</h2>

                <h3 id="abrir">3.1 Abrir um chamado</h3>
                <ol>
                    <li>Na tela <strong>Chamados</strong>, clique em <strong>Novo chamado</strong>.</li>
                    <li>Escolha a <strong>Categoria</strong> e depois a <strong>Subcategoria</strong>.</li>
                    <li>Se a categoria exigir equipamento (por exemplo, "Equipamento"), aparece o campo <strong>Equipamento</strong>, que passa a ser obrigatório. Escolha o equipamento afetado.</li>
                    <li>Escolha a <strong>Prioridade</strong>. Abaixo do campo aparecem os prazos de atendimento e de resolução dessa prioridade. A prioridade que você escolhe é uma sugestão: a equipe técnica pode reclassificá-la com justificativa.</li>
                    <li>Preencha o <strong>Título</strong> (até 150 caracteres) e a <strong>Descrição</strong>.</li>
                    <li>Se quiser, anexe arquivos (<strong>PDF, PNG ou JPG</strong>, até 5 por envio; o tamanho máximo por arquivo é definido pelo administrador, 10 MB por padrão).</li>
                    <li>Clique em <strong>Abrir chamado</strong>.</li>
                </ol>

                <p>Ao salvar, uma janela mostra o <strong>número de protocolo</strong>, a <strong>prioridade</strong> atribuída e os <strong>prazos previstos</strong> de atendimento e de resolução. Guarde o protocolo. Uma confirmação com as mesmas informações é enviada ao seu e-mail cadastrado (se o seu usuário não tiver e-mail, a confirmação na tela continua valendo, mas nenhum e-mail é enviado).</p>

                <p>Se algum anexo não puder ser gravado, o chamado inteiro não é aberto e você vê a mensagem "Não foi possível gravar os anexos; tente novamente".</p>

                <h3 id="acompanhar">3.2 Acompanhar</h3>
                <p>A tela <strong>Chamados</strong> lista os seus chamados e se atualiza sozinha a cada 30 segundos (acima da tabela aparece "Atualizado às ..."; linhas alteradas ficam destacadas por instantes).</p>
                <ul>
                    <li>Use os filtros (protocolo, status, prioridade, categoria, equipamento, período "De" e "Até") e o botão de busca; o botão com "X" limpa os filtros.</li>
                    <li>Cada linha mostra protocolo, título, categoria, equipamento, prioridade, status, responsável, abertura e duas marcas de prazo (<strong>SLA atend.</strong> e <strong>SLA resol.</strong>; veja a <a href="#sla">seção 7</a>).</li>
                    <li>A lista tem 50 chamados por página.</li>
                    <li>Clique numa linha para abrir o <strong>detalhe</strong>.</li>
                </ul>

                <p>No detalhe você vê:</p>
                <ul>
                    <li>os dados do chamado (protocolo, prioridade, categoria, equipamento, solicitante, responsável, abertura);</li>
                    <li>o quadro <strong>Prazos (24x7)</strong>, com a data prevista, a marca de prazo e uma contagem regressiva ("Restam 3h 20min" ou "Vencido há ..."). Quando o relógio está pausado, aparece "relógio pausado";</li>
                    <li>a <strong>Descrição</strong>, os <strong>Anexos</strong> (clique para baixar) e o <strong>Histórico</strong> completo, com a mudança de status, o autor, a data e o texto de cada evento.</li>
                </ul>
                <p>A tela de detalhe também se atualiza a cada 30 segundos.</p>

                <h3 id="responder">3.3 Responder</h3>
                <p>Quando o status é <strong>Aguardando solicitante</strong>, a equipe está esperando uma informação sua. No detalhe, clique em <strong>Responder</strong>, escreva a observação (obrigatória), anexe arquivos se precisar (até 5) e confirme. O chamado volta para "Em atendimento" e o relógio de resolução volta a correr.</p>

                <h3 id="avaliar">3.4 Avaliar e fechar, ou reabrir</h3>
                <p>Quando o status é <strong>Resolvido</strong>, há duas opções:</p>
                <ul>
                    <li><strong>Avaliar e fechar</strong>: escolha uma nota de 1 a 5 ★, escreva um comentário (opcional) e clique em <strong>Enviar avaliação</strong>. O chamado fica "Fechado".</li>
                    <li><strong>Reabrir</strong>: se a solução não resolveu, clique em <strong>Reabrir</strong> e escreva a justificativa. O chamado volta para "Em atendimento".</li>
                </ul>
                <p>Se nenhuma das duas ações for feita em alguns dias (5 por padrão, ajustável pelo administrador), o sistema <strong>fecha o chamado automaticamente</strong> e o detalhe mostra "Fechado automaticamente — não avaliado".</p>

                <h3 id="cancelar">3.5 Cancelar</h3>
                <p>Enquanto o chamado estiver <strong>Aberto</strong> (ainda sem atendimento), você pode clicar em <strong>Cancelar</strong> e informar o motivo.</p>

                <h3 id="exportar">3.6 Exportar</h3>
                <p>Na tela <strong>Chamados</strong>, os botões <strong>CSV</strong>, <strong>XLS</strong> e <strong>PDF</strong> exportam os chamados do conjunto filtrado (no seu caso, só os seus). Os arquivos CSV e XLS trazem os filtros, a data, o usuário, o total e uma impressão digital <strong>SHA-256</strong> do conteúdo; o PDF é montado no navegador.</p>

                <!-- ============================================================ -->
                <h2 id="gestor">4. Gestor do contrato</h2>

                <p>O gestor abre chamados como o solicitante e <strong>avalia, reabre e cancela os chamados que abriu</strong>, com estas diferenças:</p>
                <ul>
                    <li>Na tela <strong>Chamados</strong>, vê <strong>todos</strong> os chamados, não só os próprios, e pode filtrar e exportar o conjunto geral.</li>
                    <li>Pode <strong>responder</strong> a <strong>qualquer chamado aguardando retorno</strong>, mesmo que não tenha sido aberto por ele.</li>
                    <li>Tem acesso à tela <strong>Relatórios</strong>.</li>
                </ul>

                <h3 id="relatorios">Relatórios</h3>
                <ol>
                    <li>Abra <strong>Relatórios</strong> (cartão da categoria ou botão na tela Chamados).</li>
                    <li>Informe os filtros: <strong>De</strong>, <strong>Até</strong>, <strong>Categoria</strong>, <strong>Equipamento</strong> e <strong>Prioridade</strong>.</li>
                    <li>Em <strong>Agrupar por</strong>, escolha Mês, Semana, Dia, Categoria, Equipamento ou Prioridade.</li>
                    <li>Clique em <strong>Gerar</strong>.</li>
                </ol>

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
                <p>Os botões <strong>CSV</strong>, <strong>XLS</strong> e <strong>PDF</strong> exportam a tabela de indicadores.</p>

                <!-- ============================================================ -->
                <h2 id="equipe">5. Equipe técnica</h2>

                <p>A equipe técnica enxerga todos os chamados e atende conforme o status. No detalhe, os botões disponíveis mudam com o status:</p>
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

                <p>Cada ação abre uma janela para a observação, que vai ao histórico. Se a tela avisar "Chamado alterado por outro usuário; recarregue", alguém mexeu no chamado enquanto você o olhava: recarregue a página e refaça a ação.</p>
                <p>A equipe também usa <strong>Relatórios</strong> (veja a <a href="#gestor">seção 4</a>) e os botões de exportação da lista. A Configuração do sistema é exclusiva do administrador.</p>

                <!-- ============================================================ -->
                <h2 id="administrador">6. Administrador</h2>

                <p>O administrador acessa a tela <strong>Configuração</strong> (cartão da categoria ou botão "Configuração" na tela Chamados). Ela tem cinco abas. Todas as alterações feitas aqui são registradas no log de auditoria do sistema.</p>

                <h3 id="adm-sla">6.1 Parâmetros de SLA</h3>
                <ul>
                    <li><strong>Risco de SLA (% do prazo consumido)</strong>: de 1 a 99. Acima desse percentual o chamado passa a "Em risco". Padrão: 80.</li>
                    <li><strong>Fechamento automático (dias)</strong>: de 1 a 60. Dias após a resolução em que um chamado resolvido, sem avaliação nem reabertura, é fechado automaticamente. Padrão: 5.</li>
                </ul>
                <p>Clique em <strong>Salvar parâmetros</strong>. Os prazos por prioridade ficam na aba "Prioridades e prazos"; as horas são corridas (24x7).</p>

                <h3 id="adm-perfis">6.2 Perfis de acesso</h3>
                <p>A aba mostra três cartões: <strong>Solicitantes</strong>, <strong>Gestores do contrato</strong> e <strong>Equipe técnica</strong>, cada um com a lista dos usuários que têm o perfil.</p>
                <ul>
                    <li>Para <strong>incluir</strong> um usuário: digite pelo menos 2 letras do nome ou do login no campo "Adicionar usuário", clique no resultado e confirme.</li>
                    <li>Para <strong>remover</strong>: clique na lixeira ao lado do nome e confirme.</li>
                </ul>
                <p>Os administradores aparecem numa lista de <strong>somente leitura</strong> no fim da aba; o grupo de administradores não é alterado por aqui.</p>
                <p>Os mesmos grupos aparecem na tela de usuários do sistema legado; incluir um usuário por lá ou por aqui tem o mesmo efeito sobre o acesso. Porém, as alterações de grupo feitas pela tela legada de usuários <strong>não aparecem no log de auditoria do sistema de chamados</strong>; só ficam registradas as feitas nesta aba (Perfis de acesso).</p>

                <h3 id="adm-prioridades">6.3 Prioridades e prazos</h3>
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

                <h3 id="adm-categorias">6.4 Categorias</h3>
                <p>Lista categorias e subcategorias (dois níveis). Use <strong>Nova categoria</strong> ou a edição da linha para definir o nome, a <strong>Categoria pai</strong> (vazia para categoria de primeiro nível) e se a categoria <strong>exige equipamento</strong> (a opção vale para a categoria de primeiro nível; as subcategorias herdam). Para retirar uma categoria de uso, desative-a.</p>

                <h3 id="adm-smtp">6.5 E-mail (SMTP)</h3>
                <p>Define o servidor usado para enviar os e-mails dos chamados (confirmação de abertura e aviso de reclassificação). No alto da aba, um aviso informa qual servidor está em uso: o configurado aqui ou o do arquivo de configuração do servidor (<code>confGTW.xml</code>).</p>
                <table class="table table-bordered table-sm">
                    <thead><tr><th>Campo</th><th>Observação</th></tr></thead>
                    <tbody>
                        <tr><td><strong>Servidor SMTP</strong></td><td>Nome do servidor ou endereço IPv4. Em branco = usa o servidor do <code>confGTW.xml</code></td></tr>
                        <tr><td><strong>Porta</strong></td><td>De 1 a 65535 (padrão 587)</td></tr>
                        <tr><td><strong>Usuário</strong></td><td>Em branco = sem autenticação</td></tr>
                        <tr><td><strong>Senha</strong></td><td>Em branco = mantém a senha atual. Marque <strong>Apagar a senha gravada</strong> para removê-la. A senha nunca é exibida</td></tr>
                        <tr><td><strong>E-mail do remetente</strong></td><td>Endereço que aparece como remetente</td></tr>
                        <tr><td><strong>Usar STARTTLS</strong></td><td>Liga a criptografia STARTTLS (obrigatória nesse caso: se o servidor não oferecer, o envio falha). Desligado = sem criptografia. Só vale para o servidor configurado nesta tela</td></tr>
                        <tr><td><strong>Enviar e-mails dos chamados</strong></td><td>Liga ou desliga todo o envio</td></tr>
                    </tbody>
                </table>

                <p>São suportados apenas <strong>STARTTLS</strong> (normalmente porta 587) ou <strong>sem criptografia</strong>; a conexão SSL direta (porta 465) <strong>não é suportada</strong>. O interruptor STARTTLS só vale para o servidor configurado nesta tela: o servidor do <code>confGTW.xml</code> segue a sua própria configuração.</p>

                <p>A senha é gravada <strong>cifrada</strong> e depende de uma chave guardada no servidor (variável de ambiente <code>GTW_CHAMADO_CHAVE_SMTP</code>). Ao trocar servidor, porta ou usuário, é preciso digitar a senha novamente (a senha gravada não é reaproveitada em outro servidor). Se a chave não estiver definida, a aba mostra um aviso e não é possível gravar uma senha nova; veja <code>docs/requisitos/07-configuracao-e-segredos.md</code>.</p>

                <p>Depois de salvar, clique em <strong>Enviar e-mail de teste</strong>: o sistema envia uma mensagem, com a configuração gravada, para o e-mail cadastrado no seu usuário. Se houver falha, a mensagem do servidor é mostrada (sem a senha).</p>

                <!-- ============================================================ -->
                <h2 id="sla">7. Prazos de atendimento (SLA)</h2>

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
                <h2 id="problemas">8. Resolução de problemas</h2>

                <table class="table table-bordered table-sm">
                    <thead><tr><th>Sintoma</th><th>O que fazer</th></tr></thead>
                    <tbody>
                        <tr><td>Não vejo a categoria "Sistema de Chamados" ou um dos cartões</td><td>O seu usuário não tem o perfil necessário. Peça ao administrador para incluí-lo em Configuração &gt; Perfis de acesso</td></tr>
                        <tr><td>O botão "Novo chamado" não aparece</td><td>Apenas solicitantes e gestores abrem chamados</td></tr>
                        <tr><td>Mensagem "Chamado alterado por outro usuário; recarregue"</td><td>Outra pessoa (ou o sistema, ao atualizar os prazos) alterou o chamado. Recarregue a página e repita a ação</td></tr>
                        <tr><td>"Não foi possível gravar os anexos; tente novamente"</td><td>O servidor não conseguiu gravar os arquivos. Tente de novo; se persistir, avise o administrador (diretório de anexos)</td></tr>
                        <tr><td>Anexo recusado</td><td>Só são aceitos PDF, PNG e JPG, dentro do tamanho máximo, e o conteúdo do arquivo precisa corresponder ao tipo</td></tr>
                        <tr><td>Não recebi o e-mail de confirmação</td><td>Confira se o seu usuário tem e-mail cadastrado e se o SMTP está configurado e ligado (administrador: aba E-mail (SMTP) e botão de teste)</td></tr>
                        <tr><td>Na aba SMTP aparece o aviso da variável <code>GTW_CHAMADO_CHAVE_SMTP</code></td><td>A chave de cifragem não está definida no servidor. Veja a documentação de configuração e segredos</td></tr>
                        <tr><td>O gráfico dos relatórios não aparece</td><td>A tela carrega a biblioteca de gráficos da internet; verifique o acesso do computador e recarregue</td></tr>
                    </tbody>
                </table>

                <hr class="my-4">
                <p class="text-muted text-center" style="font-size:0.8rem">
                    <i class="bi bi-info-circle me-1"></i>
                    Manual do Sistema de Chamados — Muralha Digital
                </p>

            </div>
        </div>
    </div>
</div>
