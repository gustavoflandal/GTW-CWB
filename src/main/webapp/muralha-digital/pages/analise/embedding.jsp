<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<style>
  .analise-container {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 80px);
    padding: 0;
  }
  .analise-toolbar {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: #f8f9fa;
    border-bottom: 1px solid #dee2e6;
    flex-shrink: 0;
  }
  .analise-toolbar label {
    font-weight: 500;
    font-size: .875rem;
    margin: 0;
  }
  .analise-toolbar select {
    min-width: 280px;
  }
  .analise-frame-wrapper {
    flex: 1;
    position: relative;
    overflow: hidden;
  }
  .analise-frame-wrapper iframe {
    width: 100%;
    height: 100%;
    border: none;
  }
  .analise-loading {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    z-index: 10;
  }
  .analise-btn-abrir {
    margin-left: auto;
  }
</style>

<div class="analise-container">
  <div class="analise-toolbar">
    <label for="selectDashboard">
      <i class="bi bi-bar-chart-line"></i> Painel:
    </label>
    <select id="selectDashboard" class="form-select form-select-sm">
      <option value="">Selecione um painel...</option>
      <optgroup label="Demonstração (dados reais)">
        <option value="demo-fluxo-veicular">Fluxo Veicular</option>
        <option value="demo-infracoes">Fiscalização e Infrações</option>
        <option value="demo-trafego-historico">Tráfego Histórico</option>
        <option value="demo-mobilidade-waze">Mobilidade Urbana (Waze)</option>
        <option value="demo-alertas-veiculos">Alertas de Veículos de Interesse</option>
      </optgroup>
      <optgroup label="Painéis operacionais">
      <option value="kpis-operacionais">KPIs Operacionais</option>
      <option value="assertividade-dupla-analise">Assertividade</option>
      <option value="sla-latencia">SLA de Latência</option>
      <option value="disponibilidade-equipamentos">Disponibilidade de Equipamentos</option>
      <option value="preprocessamento-imagens">Pré-processamento</option>
      <option value="alertas">Alertas</option>
      <option value="infracoes">Infrações</option>
      <option value="passagens-veiculares">Passagens Veiculares</option>
      <option value="mapa-equipamentos-infracoes">Mapa de Equipamentos</option>
      </optgroup>
    </select>
    <a id="btnAbrirSuperset" href="#" target="_blank"
       class="btn btn-outline-primary btn-sm analise-btn-abrir" style="display:none;">
      <i class="bi bi-box-arrow-up-right"></i> Abrir no Superset
    </a>
  </div>
  <div class="analise-frame-wrapper">
    <div id="loadingAnalise" class="analise-loading" style="display:none;">
      <div class="text-center">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-2 text-muted">Carregando painel...</p>
      </div>
    </div>
    <iframe id="frameAnalise" src="about:blank"></iframe>
    <div id="placeholderAnalise" class="analise-loading">
      <div class="text-center text-muted">
        <i class="bi bi-bar-chart-line" style="font-size:3rem;"></i>
        <p class="mt-2">Selecione um painel para visualizar</p>
      </div>
    </div>
  </div>
</div>

<script>
  var supersetBaseUrl = '${not empty supersetBaseUrl ? supersetBaseUrl : "http://localhost:8088"}';
</script>
<script src="${pageContext.request.contextPath}/muralha-digital/pages/analise/assets/js/embedding.js"></script>
