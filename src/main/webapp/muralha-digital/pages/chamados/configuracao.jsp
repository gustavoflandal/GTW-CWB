<%-- src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" aria-current="page">Configuração</li>
  </ol></nav>
  <h4 class="mb-3"><i class="bi bi-gear me-2"></i>Configuração de Chamados</h4>

  <ul class="nav nav-tabs" role="tablist">
    <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#abaSla" type="button">Parâmetros de SLA</button></li>
    <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaPerfis" type="button">Perfis de acesso</button></li>
    <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaPri" type="button">Prioridades e prazos</button></li>
    <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaCat" type="button">Categorias</button></li>
    <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaSmtp" type="button">E-mail (SMTP)</button></li>
  </ul>
  <div class="tab-content border border-top-0 p-3">
    <div class="tab-pane fade show active" id="abaSla">
      <form id="formSla" class="row g-3" style="max-width:640px">
        <div class="col-12 col-md-6">
          <label class="form-label" for="slaRisco">Risco de SLA (% do prazo consumido)</label>
          <input type="number" class="form-control" id="slaRisco" min="1" max="99" required>
          <div class="form-text">Entre 1 e 99. Acima deste percentual o chamado fica "Em risco".</div>
        </div>
        <div class="col-12 col-md-6">
          <label class="form-label" for="slaDias">Fechamento automático (dias)</label>
          <input type="number" class="form-control" id="slaDias" min="1" max="60" required>
          <div class="form-text">Entre 1 e 60 dias após a resolução.</div>
        </div>
        <div class="col-12">
          <p class="small text-muted mb-2"><i class="bi bi-info-circle me-1"></i>Os prazos por prioridade ficam na aba "Prioridades e prazos". As horas são corridas (24x7).</p>
          <button type="submit" class="btn btn-primary btn-sm" id="btnSalvarSla"><i class="bi bi-check-circle me-1"></i>Salvar parâmetros</button>
        </div>
      </form>
    </div>
    <div class="tab-pane fade" id="abaPerfis">
      <div class="row g-3" id="cartoesPerfis"></div>
      <div class="mt-4">
        <h6><i class="bi bi-shield-lock me-1"></i>Administradores <span class="small text-muted">(somente leitura)</span></h6>
        <ul class="list-group list-group-flush small" id="listaAdmin"></ul>
      </div>
    </div>
    <div class="tab-pane fade" id="abaPri">
      <button class="btn btn-sm btn-primary mb-2" id="btnNovaPri"><i class="bi bi-plus-circle me-1"></i>Nova prioridade</button>
      <table class="table table-sm align-middle"><thead><tr><th>Ordem</th><th>Código</th><th>Nome</th>
        <th>Atendimento (min)</th><th>Resolução (min)</th><th>Ativa</th><th></th></tr></thead><tbody id="tblPri"></tbody></table>
    </div>
    <div class="tab-pane fade" id="abaCat">
      <button class="btn btn-sm btn-primary mb-2" id="btnNovaCat"><i class="bi bi-plus-circle me-1"></i>Nova categoria</button>
      <table class="table table-sm align-middle"><thead><tr><th>Categoria</th><th>Subcategoria</th>
        <th>Exige equipamento</th><th>Ativa</th><th></th></tr></thead><tbody id="tblCat"></tbody></table>
    </div>
    <div class="tab-pane fade" id="abaSmtp">
      <div class="alert alert-info py-2 small" id="smtpOrigem" role="status"></div>
      <div class="alert alert-warning py-2 small d-none" id="smtpSemChave" role="alert">
        <i class="bi bi-exclamation-triangle me-1"></i>A variável de ambiente <code>GTW_CHAMADO_CHAVE_SMTP</code> não está definida no servidor:
        não é possível gravar uma senha nova (nem usar a senha gravada). Consulte a documentação de configuração e segredos.
      </div>
      <form id="formSmtp" class="row g-3" style="max-width:760px" autocomplete="off">
        <div class="col-12 col-md-8">
          <label class="form-label" for="smtpHost">Servidor SMTP</label>
          <input type="text" class="form-control" id="smtpHost" maxlength="253" placeholder="smtp.exemplo.com.br">
          <div class="form-text">Deixe em branco para usar o servidor do <code>confGTW.xml</code>.</div>
        </div>
        <div class="col-12 col-md-4">
          <label class="form-label" for="smtpPorta">Porta</label>
          <input type="number" class="form-control" id="smtpPorta" min="1" max="65535">
        </div>
        <div class="col-12 col-md-6">
          <label class="form-label" for="smtpUsuario">Usuário</label>
          <input type="text" class="form-control" id="smtpUsuario" maxlength="100" autocomplete="off">
          <div class="form-text">Em branco = sem autenticação.</div>
        </div>
        <div class="col-12 col-md-6">
          <label class="form-label" for="smtpSenha">Senha</label>
          <input type="password" class="form-control" id="smtpSenha" maxlength="120" autocomplete="new-password">
          <div class="form-text" id="smtpSenhaAjuda">Deixe em branco para manter a senha atual.</div>
          <div class="form-check mt-1">
            <input class="form-check-input" type="checkbox" id="smtpLimparSenha">
            <label class="form-check-label small" for="smtpLimparSenha">Apagar a senha gravada</label>
          </div>
        </div>
        <div class="col-12 col-md-6">
          <label class="form-label" for="smtpRemetente">E-mail do remetente</label>
          <input type="email" class="form-control" id="smtpRemetente" maxlength="255" placeholder="chamados@exemplo.com.br">
        </div>
        <div class="col-12 col-md-6 d-flex flex-column justify-content-end">
          <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" role="switch" id="smtpStarttls">
            <label class="form-check-label" for="smtpStarttls">Usar STARTTLS</label>
          </div>
          <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" role="switch" id="smtpEmailAtivo">
            <label class="form-check-label" for="smtpEmailAtivo">Enviar e-mails dos chamados</label>
          </div>
        </div>
        <div class="col-12">
          <button type="submit" class="btn btn-primary btn-sm" id="btnSalvarSmtp"><i class="bi bi-check-circle me-1"></i>Salvar</button>
          <button type="button" class="btn btn-outline-secondary btn-sm" id="btnTestarSmtp"><i class="bi bi-envelope-check me-1"></i>Enviar e-mail de teste</button>
          <div class="form-text">O teste usa a configuração gravada e envia para o seu e-mail cadastrado.</div>
        </div>
      </form>
    </div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-configuracao.js"></script>
