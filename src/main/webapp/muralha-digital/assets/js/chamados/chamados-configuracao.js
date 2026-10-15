// src/main/webapp/muralha-digital/assets/js/chamados/chamados-configuracao.js
(function () {
  var esc = Chamados.esc;
  var dados = { prioridades: [], categorias: [] };

  async function carregar() {
    var d = await Chamados.get('ChamadoConfig', { acao: 'listar' });
    if (!d.ok) return;
    dados = d;
    document.getElementById('tblPri').innerHTML = d.prioridades.map(function (p) {
      return '<tr><td>' + p.ordem + '</td><td><code>' + esc(p.codigo) + '</code></td><td>' + Chamados.badgePrioridade(p.nome, p.cor) +
        '</td><td>' + p.minutosAtendimento + '</td><td>' + p.minutosResolucao + '</td><td>' + (p.ativo ? 'Sim' : 'Não') +
        '</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-editar-pri="' + p.id + '"><i class="bi bi-pencil"></i></button> ' +
        '<button class="btn btn-sm btn-outline-secondary" data-ativo="prioridade:' + p.id + '">' + (p.ativo ? 'Desativar' : 'Ativar') + '</button></td></tr>';
    }).join('');
    document.getElementById('tblCat').innerHTML = d.categorias.map(function (c) {
      return '<tr><td>' + esc(c.idPai ? c.nomePai : c.nome) + '</td><td>' + esc(c.idPai ? c.nome : '—') + '</td><td>' +
        (c.exigeEquipamento ? 'Sim' : 'Não') + '</td><td>' + (c.ativo ? 'Sim' : 'Não') +
        '</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-editar-cat="' + c.id + '"><i class="bi bi-pencil"></i></button> ' +
        '<button class="btn btn-sm btn-outline-secondary" data-ativo="categoria:' + c.id + '">' + (c.ativo ? 'Desativar' : 'Ativar') + '</button></td></tr>';
    }).join('');
  }

  // ---- Parâmetros de SLA ----
  async function carregarParametros() {
    var d = await Chamados.get('ChamadoConfig', { acao: 'parametros' });
    if (!d.ok) return;
    document.getElementById('slaRisco').value = d.riscoPercentual;
    document.getElementById('slaDias').value = d.fechamentoAutoDias;
  }

  async function salvarParametros(e) {
    e.preventDefault();
    var btn = document.getElementById('btnSalvarSla');
    btn.disabled = true;
    try {
      var d = await Chamados.post('ChamadoConfig', new URLSearchParams({
        acao: 'salvarParametros',
        riscoPercentual: document.getElementById('slaRisco').value,
        fechamentoAutoDias: document.getElementById('slaDias').value
      }));
      if (d.ok) Swal.fire({ icon: 'success', title: 'Parâmetros salvos', timer: 1200, showConfirmButton: false });
    } finally {
      btn.disabled = false;
    }
  }

  // ---- E-mail (SMTP) ----
  function el(id) { return document.getElementById(id); }

  async function carregarSmtp() {
    var d = await Chamados.get('ChamadoConfig', { acao: 'smtp' });
    if (!d.ok) return;
    el('smtpHost').value = d.host || '';
    el('smtpPorta').value = d.porta || '';
    el('smtpUsuario').value = d.usuario || '';
    el('smtpRemetente').value = d.remetente || '';
    el('smtpStarttls').checked = !!d.starttls;
    el('smtpEmailAtivo').checked = !!d.emailAtivo;
    el('smtpSenha').value = '';
    el('smtpLimparSenha').checked = false;
    el('smtpLimparSenha').disabled = !d.senhaDefinida;
    el('smtpSenhaAjuda').textContent = d.senhaDefinida
      ? 'Há uma senha gravada (cifrada). Deixe em branco para manter a senha atual.'
      : 'Nenhuma senha gravada. Deixe em branco para manter sem senha.';
    el('smtpSemChave').classList.toggle('d-none', !!d.chaveDisponivel);
    el('smtpOrigem').innerHTML = '<i class="bi bi-info-circle me-1"></i>Em uso: ' + (d.origemAtual === 'CHAMADOS'
      ? '<strong>configuração dos Chamados</strong> (servidor ' + esc(d.host) + ').'
      : '<strong>servidor do confGTW.xml</strong> (o campo "Servidor SMTP" está em branco).');
  }

  async function salvarSmtp(e) {
    e.preventDefault();
    var btn = el('btnSalvarSmtp');
    btn.disabled = true;
    try {
      var d = await Chamados.post('ChamadoConfig', new URLSearchParams({
        acao: 'salvarSmtp',
        host: el('smtpHost').value.trim(),
        porta: el('smtpPorta').value,
        usuario: el('smtpUsuario').value.trim(),
        senha: el('smtpSenha').value,
        limparSenha: el('smtpLimparSenha').checked,
        remetente: el('smtpRemetente').value.trim(),
        starttls: el('smtpStarttls').checked,
        emailAtivo: el('smtpEmailAtivo').checked
      }));
      if (d.ok) {
        Swal.fire({ icon: 'success', title: 'Configuração de e-mail salva', timer: 1200, showConfirmButton: false });
        carregarSmtp();
      }
    } finally {
      btn.disabled = false;
    }
  }

  async function testarSmtp() {
    var btn = el('btnTestarSmtp');
    btn.disabled = true;
    try {
      var d = await Chamados.post('ChamadoConfig', new URLSearchParams({ acao: 'testarSmtp' }));
      if (d.ok) Swal.fire({ icon: 'success', title: 'E-mail de teste enviado', text: 'Verifique a caixa de entrada de ' + d.destinatario + '.' });
    } finally {
      btn.disabled = false;
    }
  }

  // ---- Perfis de acesso ----
  var PERFIS = [
    { codigo: 'SOLICITANTE', titulo: 'Solicitantes', icone: 'bi-person' },
    { codigo: 'GESTOR', titulo: 'Gestores do contrato', icone: 'bi-person-badge' },
    { codigo: 'EQUIPE', titulo: 'Equipe técnica', icone: 'bi-tools' }
  ];
  var membros = [];
  var timers = {};

  function montarCartoes() {
    document.getElementById('cartoesPerfis').innerHTML = PERFIS.map(function (p) {
      return '<div class="col-12 col-lg-4"><div class="card h-100"><div class="card-header"><i class="bi ' + p.icone + ' me-1"></i>' + esc(p.titulo) + '</div>' +
        '<div class="card-body"><label class="form-label small" for="busca' + p.codigo + '">Adicionar usuário</label>' +
        '<div class="position-relative mb-3"><input type="search" class="form-control form-control-sm" id="busca' + p.codigo +
        '" data-perfil="' + p.codigo + '" autocomplete="off" placeholder="Digite ao menos 2 letras do nome ou login">' +
        '<div class="list-group position-absolute w-100 shadow-sm d-none" style="z-index:1050;max-height:220px;overflow:auto" id="res' + p.codigo + '"></div></div>' +
        '<ul class="list-group list-group-flush" id="lista' + p.codigo + '"></ul></div></div></div>';
    }).join('');
  }

  function renderPerfis() {
    PERFIS.forEach(function (p) {
      var itens = membros.filter(function (m) { return m.perfil === p.codigo; });
      document.getElementById('lista' + p.codigo).innerHTML = itens.length ? itens.map(function (m) {
        return '<li class="list-group-item d-flex justify-content-between align-items-center px-0"><span>' + esc(m.nome) +
          ' <span class="small text-muted">(' + esc(m.login) + ')</span>' + (m.ativo === false ? ' <span class="badge text-bg-secondary">Inativo</span>' : '') +
          '</span><button type="button" class="btn btn-sm btn-outline-danger" data-remover="' + esc(p.codigo) + ':' + esc(m.idUsuario) +
          '" aria-label="Remover ' + esc(m.nome) + '"><i class="bi bi-trash"></i></button></li>';
      }).join('') : '<li class="list-group-item px-0 text-muted small">Nenhum usuário neste perfil.</li>';
    });
    var admins = membros.filter(function (m) { return m.perfil === 'ADMIN'; });
    document.getElementById('listaAdmin').innerHTML = admins.length ? admins.map(function (m) {
      return '<li class="list-group-item px-0">' + esc(m.nome) + ' <span class="text-muted">(' + esc(m.login) + ')</span></li>';
    }).join('') : '<li class="list-group-item px-0 text-muted">Nenhum administrador.</li>';
  }

  async function carregarPerfis() {
    var d = await Chamados.get('ChamadoConfig', { acao: 'perfis' });
    if (!d.ok) return;
    membros = d.membros;
    renderPerfis();
  }

  function fecharResultados(perfil) {
    var box = document.getElementById('res' + perfil);
    box.classList.add('d-none');
    box.innerHTML = '';
  }

  async function buscar(perfil, termo) {
    var box = document.getElementById('res' + perfil);
    if (termo.length < 2) { fecharResultados(perfil); return; }
    var d = await Chamados.get('ChamadoConfig', { acao: 'buscarUsuarios', termo: termo });
    if (!d.ok) return;
    if (document.getElementById('busca' + perfil).value.trim() !== termo) return; // resposta obsoleta
    var jaTem = membros.filter(function (m) { return m.perfil === perfil; }).map(function (m) { return String(m.idUsuario); });
    var lista = d.usuarios.filter(function (u) { return jaTem.indexOf(String(u.idUsuario)) < 0; });
    box.innerHTML = lista.length ? lista.map(function (u) {
      return '<button type="button" class="list-group-item list-group-item-action small" data-adicionar="' + esc(perfil) + ':' + esc(u.idUsuario) +
        '" data-nome="' + esc(u.nome) + '">' + esc(u.nome) + ' <span class="text-muted">(' + esc(u.login) + ')</span></button>';
    }).join('') : '<div class="list-group-item small text-muted">Nenhum usuário encontrado.</div>';
    box.classList.remove('d-none');
  }

  function rotuloPerfil(codigo) {
    return PERFIS.filter(function (p) { return p.codigo === codigo; })[0].titulo;
  }

  function nomeDoMembro(perfil, id) {
    var m = membros.filter(function (x) { return x.perfil === perfil && String(x.idUsuario) === String(id); })[0];
    return m ? m.nome : 'o usuário';
  }

  async function alterarMembro(acao, perfil, idUsuario, nome) {
    var adicionar = acao === 'adicionarMembro';
    var c = await Swal.fire({
      title: adicionar ? 'Adicionar usuário?' : 'Remover usuário?',
      text: (adicionar ? 'Incluir ' : 'Remover ') + nome + (adicionar ? ' em ' : ' de ') + rotuloPerfil(perfil) + '?',
      icon: 'question', showCancelButton: true, confirmButtonText: adicionar ? 'Adicionar' : 'Remover', cancelButtonText: 'Cancelar'
    });
    if (!c.isConfirmed) return;
    var d = await Chamados.post('ChamadoConfig', new URLSearchParams({ acao: acao, perfil: perfil, idUsuario: idUsuario }));
    if (!d.ok) return;
    Swal.fire({ icon: 'success', title: adicionar ? 'Usuário adicionado' : 'Usuário removido', timer: 1200, showConfirmButton: false });
    if (adicionar) {
      document.getElementById('busca' + perfil).value = '';
      fecharResultados(perfil);
    }
    carregarPerfis();
  }

  async function enviar(params) {
    var fd = new URLSearchParams(params);
    var r = await fetch((window.CTX || '') + '/MuralhaDigital/ChamadoConfig', { method: 'POST', body: fd });
    var d = await r.json().catch(function () { return { ok: false, erro: 'Falha de comunicação.' }; });
    if (!d.ok) { Swal.fire({ title: 'Atenção', text: d.erro, icon: 'warning' }); return; }
    Swal.fire({ icon: 'success', title: 'Salvo', timer: 1200, showConfirmButton: false });
    carregar();
  }

  function campo(id, rotulo, valor, tipo) {
    return '<div class="mb-2 text-start"><label class="form-label small" for="' + id + '">' + rotulo + '</label><input id="' + id +
      '" type="' + (tipo || 'text') + '" class="form-control form-control-sm" value="' + esc(valor == null ? '' : valor) + '"></div>';
  }

  async function editarPrioridade(p) {
    p = p || { id: 0, codigo: '', nome: '', ordem: dados.prioridades.length + 1, minutosAtendimento: 60, minutosResolucao: 240, cor: '#6c757d' };
    var r = await Swal.fire({
      title: p.id ? 'Editar prioridade' : 'Nova prioridade',
      html: campo('sCodigo', 'Código (MAIÚSCULAS)', p.codigo) + campo('sNome', 'Nome', p.nome) + campo('sOrdem', 'Ordem', p.ordem, 'number') +
            campo('sAt', 'Prazo de atendimento (minutos)', p.minutosAtendimento, 'number') +
            campo('sRes', 'Prazo de resolução (minutos)', p.minutosResolucao, 'number') + campo('sCor', 'Cor', p.cor, 'color'),
      showCancelButton: true, confirmButtonText: 'Salvar',
      preConfirm: function () {
        return { acao: 'salvarPrioridade', id: p.id, codigo: document.getElementById('sCodigo').value.trim().toUpperCase(),
          nome: document.getElementById('sNome').value, ordem: document.getElementById('sOrdem').value,
          minutosAtendimento: document.getElementById('sAt').value, minutosResolucao: document.getElementById('sRes').value,
          cor: document.getElementById('sCor').value };
      }
    });
    if (r.isConfirmed) enviar(r.value);
  }

  async function editarCategoria(c) {
    c = c || { id: 0, nome: '', idPai: null, exigeEquipamento: false };
    var pais = dados.categorias.filter(function (x) { return !x.idPai && x.id !== c.id; });
    var r = await Swal.fire({
      title: c.id ? 'Editar categoria' : 'Nova categoria',
      html: campo('sNomeCat', 'Nome', c.nome) +
        '<div class="mb-2 text-start"><label class="form-label small" for="sPai">Categoria pai</label><select id="sPai" class="form-select form-select-sm">' +
        '<option value="0">(nenhuma — categoria de primeiro nível)</option>' +
        pais.map(function (x) { return '<option value="' + x.id + '"' + (x.id === c.idPai ? ' selected' : '') + '>' + esc(x.nome) + '</option>'; }).join('') +
        '</select></div><div class="form-check text-start"><input class="form-check-input" type="checkbox" id="sExige"' +
        (c.exigeEquipamento && !c.idPai ? ' checked' : '') + '><label class="form-check-label small" for="sExige">Exige equipamento (só categoria de primeiro nível; subcategorias herdam)</label></div>',
      showCancelButton: true, confirmButtonText: 'Salvar',
      preConfirm: function () {
        return { acao: 'salvarCategoria', id: c.id, nome: document.getElementById('sNomeCat').value,
          idPai: document.getElementById('sPai').value, exigeEquipamento: document.getElementById('sExige').checked };
      }
    });
    if (r.isConfirmed) enviar(r.value);
  }

  document.addEventListener('click', function (e) {
    var b = e.target.closest('button');
    if (!b) return;
    if (b.dataset.editarPri) editarPrioridade(dados.prioridades.find(function (p) { return String(p.id) === b.dataset.editarPri; }));
    if (b.dataset.editarCat) editarCategoria(dados.categorias.find(function (c) { return String(c.id) === b.dataset.editarCat; }));
    if (b.dataset.remover) {
      var r = b.dataset.remover.split(':');
      alterarMembro('removerMembro', r[0], r[1], nomeDoMembro(r[0], r[1]));
    }
    if (b.dataset.adicionar) {
      var a = b.dataset.adicionar.split(':');
      alterarMembro('adicionarMembro', a[0], a[1], b.dataset.nome);
    }
    if (b.dataset.ativo) {
      var partes = b.dataset.ativo.split(':');
      enviar({ acao: 'alternarAtivo', tipo: partes[0], id: partes[1] });
    }
  });
  document.getElementById('btnNovaPri').addEventListener('click', function () { editarPrioridade(null); });
  document.getElementById('btnNovaCat').addEventListener('click', function () { editarCategoria(null); });
  document.getElementById('formSla').addEventListener('submit', salvarParametros);
  el('formSmtp').addEventListener('submit', salvarSmtp);
  el('btnTestarSmtp').addEventListener('click', testarSmtp);
  montarCartoes();
  document.getElementById('cartoesPerfis').addEventListener('input', function (e) {
    var perfil = e.target.dataset.perfil;
    if (!perfil) return;
    clearTimeout(timers[perfil]);
    var termo = e.target.value.trim();
    timers[perfil] = setTimeout(function () { buscar(perfil, termo); }, 300);
  });
  document.addEventListener('click', function (e) {
    if (!e.target.closest('[data-perfil], [data-adicionar]')) PERFIS.forEach(function (p) { fecharResultados(p.codigo); });
  });
  carregarParametros();
  carregarSmtp();
  carregarPerfis();
  carregar();
})();
