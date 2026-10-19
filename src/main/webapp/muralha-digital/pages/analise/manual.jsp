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
                <li class="breadcrumb-item"><a href="/MuralhaDigital/SsoAnalise?acao=abrir"><i class="bi bi-bar-chart-line"></i> Análises</a></li>
                <li class="breadcrumb-item active">Manual</li>
            </ol>
        </nav>
        <button class="btn btn-outline-secondary btn-sm" onclick="window.print()">
            <i class="bi bi-printer"></i> Imprimir / PDF
        </button>
    </div>

    <div class="manual-container">

        <h1><i class="bi bi-book me-2" style="color:#0d75bf"></i>Manual da Plataforma de Análises</h1>
        <p class="text-muted mb-4">Guia do usuário para consultar painéis, aplicar filtros, exportar dados e receber alertas e relatórios.</p>

        <h2 id="acesso">1. Acesso</h2>
        <p>A plataforma de Análises é aberta pelo card <strong>Análises</strong> do sistema Muralha Digital. O login é automático: basta estar logado no sistema.</p>

        <h3>Perfis de acesso</h3>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Perfil</th><th>O que pode fazer</th></tr></thead>
            <tbody>
                <tr><td><strong>Operador</strong></td><td>Visualizar painéis publicados e aplicar filtros</td></tr>
                <tr><td><strong>Gestor</strong></td><td>Criar gráficos e explorar dados, sem consulta SQL livre</td></tr>
                <tr><td><strong>Auditor</strong></td><td>Visualizar painéis e consultar o registro de ações</td></tr>
                <tr><td><strong>Administrador</strong></td><td>Acesso total: consulta SQL livre, gerenciar usuários, configurar alertas</td></tr>
            </tbody>
        </table>

        <h2 id="paineis">2. Painéis disponíveis</h2>
        <p>Os painéis consultam os dados do sistema. O nome e as coordenadas de cada equipamento são os da versão do cadastro vigente na data do registro. Os resultados ficam em cache de 10 minutos a 1 hora, conforme o painel, e as cargas de dados são atualizadas periodicamente.</p>

        <h3>Painéis operacionais</h3>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Painel</th><th>Conteúdo</th></tr></thead>
            <tbody>
                <tr><td><strong>KPIs Operacionais</strong></td><td>Passagens, infrações, alertas, disponibilidade, SLA de latência e assertividade, com evolução mensal.</td></tr>
                <tr><td><strong>Assertividade — Dupla Análise</strong></td><td>Assertividade (pré-aprovadas ÷ analisadas), analisadas, desempates, reprovadas e evolução diária.</td></tr>
                <tr><td><strong>SLA de Latência</strong></td><td>Percentual de janelas de 5 minutos dentro do limite, latência P95, violações e limite do SLA.</td></tr>
                <tr><td><strong>Disponibilidade de Equipamentos</strong></td><td>Horas com captura ÷ 24, incluindo os dias sem captura; evolução mensal, equipamentos com menor disponibilidade e ranking.</td></tr>
                <tr><td><strong>Pré-processamento de Imagens</strong></td><td>Recebidas, disponibilizadas, aproveitamento, SLA de 72h, tempo médio até a entrada, motivos de descarte e produtividade por operador.</td></tr>
                <tr><td><strong>Alertas</strong></td><td>Total, críticos, pendentes, percentual tratado, evolução por tipo, status e principais responsáveis.</td></tr>
                <tr><td><strong>Infrações</strong></td><td>Total, excesso médio, equipamentos, participação de velocidade, evolução por categoria, enquadramentos e faixas.</td></tr>
                <tr><td><strong>Passagens Veiculares</strong></td><td>Passagens por hora do dia, classificação, faixa × hora, velocidade média, percentual Mercosul e ranking de equipamentos.</td></tr>
                <tr><td><strong>Mapa — Equipamentos, Áreas e Infrações</strong></td><td>Equipamentos cadastrados, polígonos das áreas monitoradas, passagens e infrações por equipamento.</td></tr>
            </tbody>
        </table>

        <h3>Painéis de demonstração</h3>
        <p>Fluxo Veicular, Fiscalização e Infrações, Tráfego Histórico, Mobilidade Urbana (Waze, com o filtro de cidade iniciando em Salvador) e Alertas de Veículos de Interesse.</p>

        <h2 id="filtros">3. Usando filtros</h2>
        <p>Os filtros ficam na barra lateral à esquerda de cada painel:</p>
        <ol>
            <li><strong>Período</strong>: sem filtro por padrão (todo o histórico); escolha um intervalo para restringir.</li>
            <li><strong>Equipamento, Tipo, Status, Operador etc.</strong>: filtros específicos de cada painel.</li>
            <li><strong>Filtro cruzado</strong>: clicar em uma barra ou fatia de um gráfico filtra os demais gráficos do painel automaticamente.</li>
        </ol>

        <h3>Como aplicar um filtro</h3>
        <div class="step-item"><span class="step-number">1</span><p>Clique no ícone de filtro na barra superior do painel.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Selecione o período ou o valor desejado.</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Clique em <strong>Aplicar filtros</strong>.</p></div>
        <div class="step-item"><span class="step-number">4</span><p>Para limpar, clique em <strong>Limpar todos</strong>.</p></div>

        <h2 id="exportar">4. Exportando dados</h2>

        <h3>Exportar um gráfico como imagem</h3>
        <div class="step-item"><span class="step-number">1</span><p>Passe o mouse sobre o gráfico.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Clique no menu <strong>⋯</strong> (três pontos) no canto superior direito.</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Selecione <strong>Download &gt; Download as Image</strong>.</p></div>

        <h3>Exportar dados como CSV</h3>
        <div class="step-item"><span class="step-number">1</span><p>Passe o mouse sobre o gráfico.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Clique no menu <strong>⋯</strong> (três pontos).</p></div>
        <div class="step-item"><span class="step-number">3</span><p>Selecione <strong>Download &gt; Export to CSV</strong>.</p></div>

        <h3>Exportar o painel completo</h3>
        <div class="step-item"><span class="step-number">1</span><p>No menu superior do painel, clique em <strong>⋯</strong>.</p></div>
        <div class="step-item"><span class="step-number">2</span><p>Selecione <strong>Download as Image</strong> para obter uma imagem de todo o painel.</p></div>

        <h2 id="alertas">5. Alertas automáticos</h2>
        <p>O sistema envia alertas por e-mail quando:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Alerta</th><th>Condição</th><th>Frequência</th></tr></thead>
            <tbody>
                <tr><td>Disponibilidade baixa</td><td>Média &lt; 95% nas últimas 24h</td><td>A cada 15 minutos</td></tr>
                <tr><td>SLA violado</td><td>Cumprimento &lt; 95% nas últimas 24h</td><td>A cada 15 minutos</td></tr>
                <tr><td>Assertividade baixa</td><td>Média &lt; 80% nas últimas 24h</td><td>A cada 30 minutos</td></tr>
                <tr><td>Alertas críticos</td><td>Pendentes há mais de 2 horas</td><td>A cada 10 minutos</td></tr>
            </tbody>
        </table>

        <h2 id="relatorios">6. Relatórios agendados</h2>
        <p>Os relatórios são enviados automaticamente por e-mail:</p>
        <table class="table table-bordered table-sm">
            <thead><tr><th>Relatório</th><th>Frequência</th><th>Horário</th></tr></thead>
            <tbody>
                <tr><td>Resumo Diário — KPIs</td><td>Diário</td><td>07:00</td></tr>
                <tr><td>Semanal — Infrações</td><td>Segunda-feira</td><td>08:00</td></tr>
                <tr><td>Semanal — SLA</td><td>Segunda-feira</td><td>08:30</td></tr>
            </tbody>
        </table>

        <h2 id="embedding">7. Painéis dentro do sistema</h2>
        <p>Os painéis são exibidos dentro do próprio sistema, sem sair dele. Acesse <strong>Tela Inicial → Análise de Dados → Análises</strong> e escolha o painel na lista <strong>Painel</strong> (grupos <em>Demonstração</em> e <em>Painéis operacionais</em>).</p>
        <p>O botão <strong>Manual</strong>, na barra da tela, abre este manual em nova aba.</p>
        <p>O botão <strong>Abrir no Superset</strong> abre o painel em nova aba, na plataforma completa (com filtros avançados e, para administradores, consulta SQL livre). Ele só aparece para o administrador do sistema ou para quem recebeu o direito <em>Analises - Abrir no Superset</em>.</p>
        <blockquote><p>Se o botão <strong>Abrir no Superset</strong> não aparece para você, peça ao administrador o direito <em>Analises - Abrir no Superset</em>.</p></blockquote>

        <h2 id="duvidas">8. Dúvidas frequentes</h2>

        <p><strong>O painel está mostrando "Sem dados".</strong><br>
        Confira se o filtro de período ou um filtro cruzado não excluiu todos os registros (limpe os filtros na barra lateral). Se persistir, procure o administrador para verificar se a carga de dados está em execução.</p>

        <p><strong>Não consigo ver um painel específico.</strong><br>
        Seu perfil pode não ter acesso. Solicite ao administrador do sistema.</p>

        <p><strong>Os dados estão desatualizados.</strong><br>
        Os resultados ficam em cache de 10 minutos a 1 hora, conforme o painel, e a carga de dados é atualizada periodicamente. Aguarde alguns minutos e recarregue o painel.</p>

        <p><strong>Como criar um novo gráfico?</strong><br>
        Apenas os perfis Gestor e Administrador podem criar gráficos, na plataforma completa (botão <strong>Abrir no Superset</strong>), pelo menu <strong>+ &gt; Chart</strong>.</p>

        <p><strong>O mapa não está carregando.</strong><br>
        Os mapas usam imagens do OpenStreetMap. Verifique sua conexão com a internet.</p>

        <hr class="my-4">
        <p class="text-muted text-center" style="font-size:0.8rem">
            <i class="bi bi-info-circle me-1"></i>
            Manual da Plataforma de Análises — Muralha Digital
        </p>

    </div>
</div>
