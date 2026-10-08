document.addEventListener("DOMContentLoaded", () => {
  $("#enviar").on("click", function () {
    const apelido = $('#usuario').val();

    $.ajax({
      url: "/MuralhaDigital/Notificacao",
      type: "POST",
      data: {
        acao: "buscarusuario",
        apelido: apelido
      },
      dataType: "json",
      success: async function (data) {
	    let sucessoEmail = false;
    	let sucessoSms = false;
        if (data) {
          const telefoneFormatado = data.telefone ? '<b>Telefone:</b> ' + mascararTelefone(data.telefone) : "";
          const emailFormatado = data.email ? '<b>Email:</b> ' + mascararEmail(data.email) : "";

          if (!telefoneFormatado && !emailFormatado) {
            Swal.fire("Usuário não possui telefone ou email cadastrado, favor contatar o supervisor.");
            return;
          }

          if (data.email) {
            sucessoEmail = await enviarEmail(data.email, data.idUsuario);
          }
          if (data.telefone) {
            sucessoSms = await enviarSms(data.telefone, data.idUsuario);
          }
      
			Swal.fire({
			  title: "Resultado do envio",
			  html: `
			    ${telefoneFormatado} ${sucessoSms ? "(SMS enviado)" : "(Falha no SMS)"}<br>
			    ${emailFormatado} ${sucessoEmail ? "(Email enviado)" : "(Falha no Email)"}
			  `,
			  icon: "info"
			});
        } else {
          Swal.fire("Usuário encontrado, mas dados incompletos.");
        }
      },
      error: function (xhr) {
        if (xhr.status === 404) {
          Swal.fire("Usuário não encontrado.");
        } else {
          Swal.fire("Erro ao buscar usuário.");
        }
      }
    });
  });
});

function mascararTelefone(telefone) {
  const digitos = telefone.replace(/\D/g, '');
  if (digitos.length < 10) return 'Telefone inválido';

  const ddd = digitos.slice(0, 2);
  const final = digitos.slice(-4);
  return `(${ddd}) *****-${final}`;
}

function mascararEmail(email) {
  const [usuario, dominio] = email.split('@');
  if (!usuario || !dominio) return 'Email inválido';

  const inicio = usuario.slice(0, 2);
  const fim = usuario.slice(-2);
  const mascarado = `${inicio}*****${fim}@${dominio}`;
  return mascarado;
}

function enviarEmail(email, idUsuario) {
  return $.ajax({
    url: "/MuralhaDigital/Notificacao",
    type: "POST",
    data: {
      acao: "enviarEmail",
      destinatario: email,
      idUsuario: idUsuario
    },
    dataType: "json"
  }).then(function (data) {
    return data && data.success;
  }).catch(function () {
    return false;
  });
}

function enviarSms(telefone, idUsuario) {
  return $.ajax({
    url: "/MuralhaDigital/Notificacao",
    type: "POST",
    data: {
      acao: "enviarsms",
      telefone: telefone,
      idUsuario: idUsuario
    },
    dataType: "json"
  }).then(function (data) {
    return data && data.success;
  }).catch(function () {
    return false;
  });
}