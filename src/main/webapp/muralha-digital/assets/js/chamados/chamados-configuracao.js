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
    if (b.dataset.ativo) {
      var partes = b.dataset.ativo.split(':');
      enviar({ acao: 'alternarAtivo', tipo: partes[0], id: partes[1] });
    }
  });
  document.getElementById('btnNovaPri').addEventListener('click', function () { editarPrioridade(null); });
  document.getElementById('btnNovaCat').addEventListener('click', function () { editarCategoria(null); });
  carregar();
})();
