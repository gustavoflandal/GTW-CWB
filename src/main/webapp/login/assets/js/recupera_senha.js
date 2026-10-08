$(document).ready(function () {
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');

  if (!token) {
    Swal.fire({
      icon: 'error',
      title: 'Token não informado',
      allowOutsideClick: false
    }).then(() => window.location.href = '/muralha-digital/login.jsp');
    return;
  }

  // Valida o token via backend
  $.post('/MuralhaDigital/Notificacao', {
    acao: 'validartoken',
    token: token
  }).done(function (res) {
    try {
      const json = typeof res === 'string' ? JSON.parse(res) : res;

      if (json.valido) {
        renderizarFormulario(json.idUsuario);
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Token inválido ou expirado',
          allowOutsideClick: false
        }).then(() => window.location.href = '/muralha-digital/login.jsp');
      }
    } catch (e) {
      console.error("Erro ao interpretar resposta:", e);
      Swal.fire('Erro', 'Resposta inesperada do servidor.', 'error');
    }
  }).fail(function () {
    Swal.fire('Erro', 'Erro ao validar o token.', 'error');
  });
});

function renderizarFormulario(idUsuario) {
  $('#conteudo-recuperacao').html(`
    <h4 class="mb-3 text-center">Insira a nova senha</h4>
    <p class="text-muted text-center mb-4">Digite a sua nova senha e confirme</p>

    <input type="hidden" id="idUsuario" value="${idUsuario}">

    <div class="mb-3">
      <label for="novaSenha" class="form-label">Nova Senha</label>
      <input type="password" class="form-control" id="novaSenha" placeholder="Digite sua nova senha">
    </div>

    <div class="mb-3">
      <label for="confirmarSenha" class="form-label">Confirme a nova senha</label>
      <input type="password" class="form-control" id="confirmarSenha" placeholder="Confirme sua nova senha">
    </div>

    <div class="d-grid">
      <button id="enviar" class="btn btn-primary">Enviar</button>
    </div>
  `);

  $('#enviar').on('click', function () {
    const senha = $('#novaSenha').val().trim();
    const confirmar = $('#confirmarSenha').val().trim();
    const idUsuario = $('#idUsuario').val();

    if (!senha || !confirmar) {
      Swal.fire('Atenção', 'Preencha todos os campos!', 'warning');
      return;
    }

    if (senha !== confirmar) {
      Swal.fire('Erro', 'As senhas não coincidem!', 'error');
      return;
    }

    // Desativa botão para evitar múltiplos envios
    $('#enviar').prop('disabled', true);

    $.post('/MuralhaDigital/Notificacao', {
      acao: 'atualizarsenha',
      idUsuario: idUsuario,
      novaSenha: senha
    }).done(function (res) {
      $('#enviar').prop('disabled', false);

      const json = typeof res === 'string' ? JSON.parse(res) : res;

      if (json.sucesso) {
        Swal.fire('Sucesso!', 'Senha redefinida com sucesso!', 'success')
          .then(() => window.location.href = '/login/login.jsp');
      } else {
        Swal.fire('Erro', json.mensagem || 'Erro ao redefinir a senha.', 'error');
      }
    }).fail(function () {
      $('#enviar').prop('disabled', false);
      Swal.fire('Erro', 'Erro ao atualizar a senha.', 'error');
    });
  });
}
