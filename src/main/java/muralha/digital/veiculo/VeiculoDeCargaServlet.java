package muralha.digital.veiculo;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import muralha.digital._ini.Inicializacao;
import muralha.digital.acessos.UsuarioServlet;
import muralha.digital.util.Paginacao;
import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.util.Utils;

@WebServlet("/MuralhaDigital/VeiculoDeCarga")
public class VeiculoDeCargaServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(VeiculoDeCargaServlet.class);
    private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
    private static final boolean LIMITE_CONSULTA_ATIVO = Inicializacao.LimiteConsultaAtivo;
    private static final int LIMITE_SEGUNDOS_CONSULTA_MAPA = Inicializacao.LimiteConsultaVeiculosEmSegundos;
    private static final boolean EXIGIR_PLACA_COMPLETA = Inicializacao.ExigirPlacaCompleta;
    private static final int QTDE_MAX_CARACTER_ESPECIAL_PLACA = Inicializacao.QtdeMaxCaracterEspecialPlaca;
    private static final int TAMANHO_MINIMO_PLACA = Inicializacao.TamanhoMinimoPlaca;
    private static final String NOME_RELATORIO = "/muralha-digital/relatorios/RelatorioConsultaVeiculos.jasper";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Validando acesso do usuário
        if (!new Acesso(request, response, true).verificaAcesso(false)) {
            new Mensagem(response).showErroMuralha("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        }
    
        Integer idUsuario = null;
        final Acesso acessoUsuario = new Acesso(request, response, true);
        if (!acessoUsuario.verificaAcesso()) {
            new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        } else {
            idUsuario = acessoUsuario.getUsuario().getId();
        }
        
        try {
            String msg = null;
            String strAcao = request.getParameter("acao");
            
            if (strAcao == null || strAcao == "") {
                msg = "Ação não informada!";
                logger.error(msg);    
                respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
                return;
            }
            
            else if (strAcao.equals("consultaPorFiltrosTela"))
                ConsultaPorFiltrosTela(request, response, false, true);
            else if (strAcao.equals("obterListaCores"))
                ListarCores(response);
            else if (strAcao.equals("obterListaUf"))
                ListarUf(response);
            else if (strAcao.equals("obterListaTiposVeiculo"))
                ListarTiposVeiculo(response);
            else if (strAcao.equals("obterLocalidades"))
                ListarLocalidades(response);
            else if (strAcao.equals("consultaPorFiltrosTelaMapa"))
                ConsultaPorFiltrosTela(request, response, true, false);
            else if (strAcao.equals("obterVeiculoPorId"))
                ObterVeiculoPorId(request, response);
            else if (strAcao.equals("exportarConsulta"))
                ExportarConsultaPorFiltrosTela(request, response, false, false, idUsuario);
            else if (strAcao.equals("obterDadosUsuario"))
                ObterDadosUsuario(request, response);
            else if (strAcao.equals("obterListaMarcas"))
                ListarMarcas(response);
            else if (strAcao.equals("obterListaModelos"))
                ListarModelos(response);
            else if (strAcao.equals("exportarConsultaSelecionadosManual"))
                ExportarConsultaSelecionadosManual(request, response, idUsuario);
            else if (strAcao.equals("exportarConsultaSPU"))
                ExportarConsultaSPU(request, response, idUsuario);

        } catch (Exception e) {
            String msg = "Ocorreu um erro ao consultar veículos!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer idUsuario = null;
        // Validando acesso do usuário        
        final Acesso acessoUsuario = new Acesso(request, response, true);
        if (!acessoUsuario.verificaAcesso()) {
            new Mensagem(response).showErroMuralha("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        } else {
            idUsuario = acessoUsuario.getUsuario().getId();
        }

        try {
            String msg = null;
            String strAcao = request.getParameter("acao");
            
            if (strAcao == null || strAcao == "") {
                msg = "Ação não informada!";
                logger.error(msg);    
                respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
                return;
            }
            
            else if (strAcao.equals("exportarConsulta"))
                ExportarConsultaPorFiltrosTela(request, response, false, true, idUsuario);
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao processar requisição!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    private VeiculoDeCargaValidacao ValidarFiltros(HttpServletRequest request, HttpServletResponse response, boolean consultaMapa, boolean exportarConsulta) {
        VeiculoDeCargaValidacao veiculoValidacao = new VeiculoDeCargaValidacao();
        
        try {
            String strPlaca = request.getParameter("placa");
            String strEquipamento = request.getParameter("equipamento");
            String strFaixa = request.getParameter("pista");
            String strClassificacao = request.getParameter("classificacao");
            String strBuscarApenasVeiculoComImagem = request.getParameter("buscarApenasVeiculoComImagem");
            String strDataIni = request.getParameter("dataIni");
            String strDataFim = request.getParameter("dataFim");
            String strModoGrade = request.getParameter("modoGrade");
            String strRetornarImagens = request.getParameter("retornarImagens");
            String strCorVeiculo = request.getParameter("corVeiculo");
            String strAnoFabricacao = request.getParameter("anoFabricacao");
            String strAnoModelo = request.getParameter("anoModelo");
            String strRenavam = request.getParameter("renavam");
            String strChassi = request.getParameter("chassi");
            String strTipoVeiculo = request.getParameter("tipoVeiculo");
            String strRestricao = request.getParameter("restricao");
            String strMarca = request.getParameter("marca");
            String strModelo = request.getParameter("modelo");
            String strTipoPlaca = request.getParameter("tipoPlaca");
            String strFiltroPlaca = request.getParameter("filtroPlaca");
            String strIdLocalidade = request.getParameter("idLocalidade");
            String strMotivo = request.getParameter("motivo");
            String strVeiculosSelecionados = request.getParameter("veiculosSelecionados");
            String strApenasVeiculosComCarga = request.getParameter("chkSomenteCarga");
            String strComPesagem = request.getParameter("chkPesagem");
            String strDeveAplicarFiltrosImagem = request.getParameter("deveAplicarFiltroDeImagens");
            
            String sFormato = request.getParameter("formato");
            String strSomenteUltimaPassagem = request.getParameter("somenteUltimaPassagem");
            
            Paginacao paginacao = new Paginacao(request);
            
            strPlaca = strPlaca != null && (strPlaca.trim().equals("") || strPlaca.equals("0")) ? null : strPlaca.toUpperCase();
            boolean placaIncompleta = strPlaca != null && strPlaca.length() < 7;
            
            boolean placaPossuiCaracterEspecial = strPlaca != null ? Utils.PlacaPossuiCaracterEspecial(strPlaca) : false;
            String strPlacaAux = strPlaca != null && placaPossuiCaracterEspecial ? Utils.TratarCaracterEspecialPlaca(strPlaca) : strPlaca;

            boolean somenteUltimaPassagem = false;
            boolean apenasVeiculosComCarga = false;
            boolean pesagem = false;
            boolean deveAplicarFiltrosImagem = false;
            
            strEquipamento = strEquipamento != null && (strEquipamento.trim().equals("") || strEquipamento.equals("null")) ? null : strEquipamento;
            strFaixa = strFaixa != null && (strFaixa.trim().equals("") || strFaixa.equals("null")) ? null : strFaixa;
            strClassificacao = strClassificacao != null && (strClassificacao.trim().equals("") || strClassificacao.equals("null")) ? null : strClassificacao;
            
            if (strPlaca == null && ((strDataIni == null || strDataIni.trim() == "") && (strDataFim == null || strDataFim.trim() == ""))) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Pesquisa muito abrangente. Informar ao menos a placa, ou o período para consulta!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (consultaMapa && (strPlaca == null || placaIncompleta)) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem((consultaMapa && placaIncompleta ? "Favor informar a placa completa para visualização no mapa!" : "Placa não informada!"));
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (strPlaca != null && (strPlaca.length() < TAMANHO_MINIMO_PLACA)) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem(String.format("Placa informada inválida! Informar pelo menos %1d caracteres da placa para consulta!", TAMANHO_MINIMO_PLACA));
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (strPlacaAux != null && ((EXIGIR_PLACA_COMPLETA && strPlaca.length() < 7) || (!ExpValida.PLACA.validar(strPlacaAux) && !ExpValida.PLACA_MERCOSUL.validar(strPlacaAux) && !ExpValida.PLACA_PARCIAL.validar(strPlacaAux)))) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Placa informada inválida!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (strPlaca != null && !Utils.ValidarCaracterEspecialPlaca(strPlaca, QTDE_MAX_CARACTER_ESPECIAL_PLACA)) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem(String.format("Placa informada inválida! Quantidade limite de caracteres especiais (%1d) foi excedida!", QTDE_MAX_CARACTER_ESPECIAL_PLACA));
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if ((strPlaca == null) && ((strDataIni == null || strDataIni.trim() == "") || (strDataFim == null || strDataFim.trim() == ""))) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Favor informar datas de início e fim da pesquisa!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (((strDataIni == null || strDataIni.trim() == "") && (strDataFim != null && strDataFim.trim() != "")) || 
                    ((strDataFim == null || strDataFim.trim() == "") && (strDataIni != null && strDataIni.trim() != ""))) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Favor informar datas de início e fim da pesquisa!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (!consultaMapa && !exportarConsulta && !paginacao.OperacaoValida()) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Dados de paginação não informados corretamente!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (exportarConsulta && (sFormato == null || !Pattern.matches("(pdf)|(xls)", sFormato))) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Formato inválido!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            int filtroPlaca = 0; // 0 = Com ou sem leitura, 1 = Somente com leitura, 2 = Somente sem leitura

            if (strFiltroPlaca != null && !strFiltroPlaca.trim().equals("")) {
                filtroPlaca = Integer.parseInt(strFiltroPlaca);
            }

            int idLocalidade = -1;

            if (strIdLocalidade != null && !strIdLocalidade.trim().equals("")) {
                idLocalidade = Integer.parseInt(strIdLocalidade);
            }

            if (strSomenteUltimaPassagem != null && !strSomenteUltimaPassagem.trim().equals("")) {
                somenteUltimaPassagem = Boolean.parseBoolean(strSomenteUltimaPassagem);
            }

            if (strApenasVeiculosComCarga != null && !strApenasVeiculosComCarga.trim().equals("")) {
                apenasVeiculosComCarga = Boolean.parseBoolean(strApenasVeiculosComCarga);
            }

            if (strComPesagem != null && !strComPesagem.trim().equals("")) {
                pesagem = Boolean.parseBoolean(strComPesagem);
            }
            
            if (strDeveAplicarFiltrosImagem != null && !strDeveAplicarFiltrosImagem.trim().equals(""))
				deveAplicarFiltrosImagem = Boolean.parseBoolean(strDeveAplicarFiltrosImagem);
            
            Date dataIni = null, dataFim = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            boolean buscarApenasVeiculoComImagem = false, modoGrade = false, retornarImagens = false, dataInformada = false;
            var veiculosSelecionados = strVeiculosSelecionados != null && !strVeiculosSelecionados.trim().equals("") ? strVeiculosSelecionados : null;
            
            try {
                logger.debug("Filtros: Equipamento: " + strEquipamento + " | Pista: " + strFaixa + " | Com Imagem: " + strBuscarApenasVeiculoComImagem + " | DataIni: " + strDataIni + " | DataFim: " + strDataFim + " | Placa: " + strPlaca);
                
                if (strBuscarApenasVeiculoComImagem != null && !strBuscarApenasVeiculoComImagem.trim().equals(""))
                    buscarApenasVeiculoComImagem = Boolean.parseBoolean(strBuscarApenasVeiculoComImagem);
                
                if (strModoGrade != null && !strModoGrade.trim().equals(""))
                    modoGrade = Boolean.parseBoolean(strModoGrade);
                
                if (strRetornarImagens != null && !strRetornarImagens.trim().equals(""))
                    retornarImagens = Boolean.parseBoolean(strRetornarImagens);
                
                if (strDataIni != null && !strDataIni.trim().equals("")) {
                    strDataIni = strDataIni + ":00";
                    dataIni = sdf.parse(strDataIni);
                }
                
                if (strDataFim != null && !strDataFim.trim().equals("")) {
                    strDataFim = strDataFim + ":59";
                    dataFim = sdf.parse(strDataFim);
                }
                
                if (dataIni != null && dataFim != null)
                    dataInformada = true;
                
                if (strPlaca != null)
                    strPlaca = Utils.FormatarPlacaConsultaBD(strPlaca, strPlacaAux, TAMANHO_MINIMO_PLACA);
                
                if (strClassificacao != null)
                    strClassificacao = FormatarClassificacaoVeicConsultaBD(strClassificacao);
            } catch (Exception e) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("Erro ao preparar dados para consulta!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if ((dataIni != null && dataFim != null) && dataFim.before(dataIni)) {
                veiculoValidacao.setFiltroValido(false);
                veiculoValidacao.setMensagem("A data de início deve ser menor que a data fim!");
                logger.error(veiculoValidacao.getMensagem());
                return veiculoValidacao;
            }
            
            if (dataInformada && strPlaca == null && LIMITE_CONSULTA_ATIVO) {
                long diffInMillies = Math.abs(dataFim.getTime() - dataIni.getTime());
                long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS) - 59;
                
                if (diff > LIMITE_SEGUNDOS_CONSULTA_MAPA) {
                    veiculoValidacao.setFiltroValido(false);
                    veiculoValidacao.setMensagem("O período não deve ser maior que " + (LIMITE_SEGUNDOS_CONSULTA_MAPA / 60 / 60) + " horas!");
                    logger.error(veiculoValidacao.getMensagem());
                    return veiculoValidacao;
                }
            }
            

            veiculoValidacao.setPlaca(strPlaca);
            veiculoValidacao.setDataIni(dataIni);
            veiculoValidacao.setDataFim(dataFim);
            veiculoValidacao.setEquipamento(strEquipamento);
            veiculoValidacao.setFaixa(strFaixa);
            veiculoValidacao.setClassificacao(strClassificacao);
            veiculoValidacao.setBuscarApenasVeiculoComImagem(buscarApenasVeiculoComImagem);
            veiculoValidacao.setConsultaMapa(consultaMapa);
            veiculoValidacao.setModoGrade(modoGrade);
            veiculoValidacao.setRetornarImagens(retornarImagens);
            veiculoValidacao.setPaginacao(paginacao);
            veiculoValidacao.setCorVeiculo(strCorVeiculo);
            veiculoValidacao.setAnoFabricacao(strAnoFabricacao);
            veiculoValidacao.setAnoModelo(strAnoModelo);
            veiculoValidacao.setRenavam(strRenavam);
            veiculoValidacao.setChassi(strChassi);
            veiculoValidacao.setTipoVeiculo(strTipoVeiculo);
            veiculoValidacao.setRestricao(strRestricao);
            veiculoValidacao.setMarca(strMarca);
            veiculoValidacao.setModelo(strModelo);
            veiculoValidacao.setTipoPlaca(strTipoPlaca);
            veiculoValidacao.setFiltroPlaca(filtroPlaca);
            veiculoValidacao.setIdLocalidade(idLocalidade);
            veiculoValidacao.setVeiculosSelecionados(veiculosSelecionados);
            veiculoValidacao.setMotivo(strMotivo);
            veiculoValidacao.setSomenteUltimaPassagem(somenteUltimaPassagem);
            veiculoValidacao.setApenasVeiculosComCarga(apenasVeiculosComCarga);
            veiculoValidacao.setPesagem(pesagem);
            veiculoValidacao.setDeveAplicarFiltroImagem(deveAplicarFiltrosImagem);

            if (sFormato != null) {
                if ("pdf".compareToIgnoreCase(sFormato) == 0)
                    veiculoValidacao.setFormato(TipoMime.PDF);
                else if ("xls".compareToIgnoreCase(sFormato) == 0)
                    veiculoValidacao.setFormato(TipoMime.XLS);
            }

            veiculoValidacao.setFiltroValido(true);
            veiculoValidacao.setMensagem("Filtros validados com sucesso!");
            return veiculoValidacao;
            
        } catch (Exception e) {
            veiculoValidacao.setFiltroValido(false);
            veiculoValidacao.setMensagem("Ocorreu um erro ao consultar veiculos!");
            logger.error(veiculoValidacao.getMensagem(), e);
            return veiculoValidacao;
        }
    }
    
    private void ConsultaPorFiltrosTela(HttpServletRequest request, HttpServletResponse response, boolean consultaMapa, boolean deveAplicarFiltroDeImagens) {
        try {
            VeiculoDeCargaValidacao veiculoValidacao = ValidarFiltros(request, response, consultaMapa, false);
            
            if (!veiculoValidacao.isFiltroValido()) {
                respostaXML.EnviarRespostaRequisicaoXML(response, veiculoValidacao.isFiltroValido(), veiculoValidacao.getMensagem());
                return;
            }
                        
            ObterPassagensVeiculosDeCarga(
                veiculoValidacao.getPlaca(),
                veiculoValidacao.getDataIni(),
                veiculoValidacao.getDataFim(),
                veiculoValidacao.getEquipamento(),
                veiculoValidacao.getFaixa(),
                veiculoValidacao.getClassificacao(),
                veiculoValidacao.isBuscarApenasVeiculoComImagem(),
                veiculoValidacao.isConsultaMapa(),
                veiculoValidacao.isModoGrade(),
                veiculoValidacao.isRetornarImagens(),
                veiculoValidacao.getPaginacao(),
                veiculoValidacao.getCorVeiculo(),
                veiculoValidacao.getAnoFabricacao(),
                veiculoValidacao.getAnoModelo(),
                veiculoValidacao.getRenavam(),
                veiculoValidacao.getChassi(),
                veiculoValidacao.getTipoVeiculo(),
                veiculoValidacao.getRestricao(),
                veiculoValidacao.getMarca(),
                veiculoValidacao.getModelo(),
                veiculoValidacao.getTipoPlaca(),
                veiculoValidacao.getFiltroPlaca(),
                veiculoValidacao.getIdLocalidade(),
                veiculoValidacao.isSomenteUltimaPassagem(),
                veiculoValidacao.isExportarConsulta(),
                veiculoValidacao.isPesagem(),
                veiculoValidacao.isApenasVeiculosComCarga(),
                veiculoValidacao.isDeveAplicarFiltroImagem(),
                response
            );
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao consultar veiculos!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    private void ObterPassagensVeiculosDeCarga(
        String placa,
        Date dataIni,
        Date dataFim,
        String equipamento,
        String faixa,
        String classificacao,
        boolean buscarApenasVeiculoComImagem,
        boolean consultaMapa,
        boolean modoGrade,
        boolean retornarImagens,
        Paginacao paginacao,
        String corVeiculo,
        String anoFabricacao,
        String anoModelo,
        String renavam,
        String chassi,
        String tipoVeiculo,
        String restricao,
        String marca,
        String modelo,
        String tipoPlaca,
        int filtroPlaca,
        int idLocalidade,
        boolean somenteUltimaPassagem,
        boolean exportarConsulta,
        boolean comPesagem,
        boolean apenasVeiculosComCarga,
        boolean deveAplicarFiltroDeImagens,
        HttpServletResponse response
    ) {
        try {
            VeiculosDeCarga veiculos = VeiculosDeCarga.ObterVeiculosDeCargaPorFiltros(
                placa, dataIni, dataFim, equipamento, faixa, classificacao,
                buscarApenasVeiculoComImagem, consultaMapa, paginacao,
                retornarImagens, exportarConsulta, modoGrade,
                corVeiculo, anoFabricacao, anoModelo, renavam, chassi,
                tipoVeiculo, restricao, marca, modelo,
                tipoPlaca, filtroPlaca, idLocalidade, somenteUltimaPassagem, apenasVeiculosComCarga, comPesagem, deveAplicarFiltroDeImagens
            );

            EnviarRespostaXML(response, veiculos);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao consultar veiculos de carga!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }

    protected void ObterVeiculoPorId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String strId = request.getParameter("idVeiculo");
            long id;
            
            if (strId == null || strId.trim().equals("") || strId.trim().equals("0")) {
                String msg = "Identificador do veículo não informado!";
                logger.error(msg);    
                respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
                return;
            }
            
            try {
                id = Long.parseLong(strId);                
            } catch (Exception e) {
                String msg = "Erro ao preparar dados para consulta!";
                logger.error(msg);    
                respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
                return;
            }
            
            VeiculoDeCargaDetalhes veiculo = VeiculosDeCarga.ObterVeiculoDeCargaPorId(id);

            EnviarVeiculoPorIdXML(response, veiculo);
            
        } catch (Exception e) {
            logger.error("Erro ao ObterVeiculoPorId(): " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao consultar veículo!";
            logger.error(msg);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    private void ExportarConsultaPorFiltrosTela(HttpServletRequest request, HttpServletResponse response, boolean consultaMapa, boolean exportar, Integer idUsuario) {
        try {
            VeiculoDeCargaValidacao veiculoValidacao = ValidarFiltros(request, response, consultaMapa, true);
            
            if (!veiculoValidacao.isFiltroValido()) {
                String msg = veiculoValidacao.getMensagem() == null || veiculoValidacao.getMensagem().trim().equals("") ? 
                        "Ocorreu um erro ao gerar o relatório. Tente novamente ou contate o administrador do sistema!" :
                            veiculoValidacao.getMensagem();
                respostaXML.EnviarRespostaRequisicaoXML(response, veiculoValidacao.isFiltroValido(), msg);
                return;
            }
            
            if (exportar) {
                RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(NOME_RELATORIO));
                
                String nomeUsuario = UsuarioServlet.obterNomeUsuarioLogado(idUsuario);
                
                try {
                    relatorio.adicParametro("placa", veiculoValidacao.getPlaca());
                    relatorio.adicParametro("data_inicio", new java.sql.Timestamp(veiculoValidacao.getDataIni().getTime()));
                    relatorio.adicParametro("data_fim", new java.sql.Timestamp(veiculoValidacao.getDataFim().getTime()));
                    relatorio.adicParametro("equipamento", veiculoValidacao.getEquipamento());
                    relatorio.adicParametro("faixa", veiculoValidacao.getFaixa());
                    relatorio.adicParametro("classificacao", veiculoValidacao.getClassificacao());
                    relatorio.adicParametro("buscarApenasVeiculoComImagem", veiculoValidacao.isBuscarApenasVeiculoComImagem());
                    relatorio.adicParametro("consultaMapa", veiculoValidacao.isConsultaMapa());
                    relatorio.adicParametro("exportarConsulta", exportar);
                    relatorio.adicParametro("formato", veiculoValidacao.getFormato().getExtensao().replace(".", ""));
                    relatorio.adicParametro("motivo", veiculoValidacao.getMotivo());
                    relatorio.adicParametro("usuario", nomeUsuario);
                    relatorio.adicParametro("marca", veiculoValidacao.getMarca());
                    relatorio.adicParametro("modelo", veiculoValidacao.getModelo());
                    relatorio.adicParametro("idCor", veiculoValidacao.getCorVeiculo());
                    relatorio.adicParametro("anoFabricacao", veiculoValidacao.getAnoFabricacao());
                    relatorio.adicParametro("anoModelo", veiculoValidacao.getAnoModelo());
                    relatorio.adicParametro("renavam", veiculoValidacao.getRenavam());
                    relatorio.adicParametro("chassi", veiculoValidacao.getChassi());
                    relatorio.adicParametro("idLocalidade", veiculoValidacao.getIdLocalidade());
                    relatorio.adicParametro("restricao", veiculoValidacao.getRestricao());
                    relatorio.adicParametro("filtroPlaca", veiculoValidacao.getFiltroPlaca());
                    relatorio.adicParametro("tipoPlaca", veiculoValidacao.getTipoPlaca());
                    relatorio.adicParametro("tipoVeiculo", veiculoValidacao.getTipoVeiculo());
                    relatorio.adicParametro("apenasVeiculosCarga", veiculoValidacao.isApenasVeiculosComCarga());
                    relatorio.adicParametro("somenteUltimaPassagem", veiculoValidacao.isSomenteUltimaPassagem());

                    relatorio.preencheRelatorio();
                    response.setContentType(veiculoValidacao.getFormato() != null ? veiculoValidacao.getFormato().getTipo() : TipoMime.HTML.getTipo());
                    response.setHeader("Content-Disposition", "inline; filename=\"ConsultaVeiculosDeCarga" + veiculoValidacao.getFormato().getExtensao() + "\"");
                    
                    if (veiculoValidacao.getFormato().compareTo(TipoMime.PDF) == 0)
                        relatorio.exportReportToPdfStream(response.getOutputStream());
                    else
                        relatorio.exportReportToXlsStream(response.getOutputStream());
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    new ServletException("Erro ao gerar o relatório: " + e.getMessage());
                }
            } else {
                logger.debug(veiculoValidacao.getMensagem());    
                respostaXML.EnviarRespostaRequisicaoXML(response, veiculoValidacao.isFiltroValido(), veiculoValidacao.getMensagem());
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao exportar relatório!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    private void EnviarRespostaXML(HttpServletResponse response, VeiculosDeCarga veiculos) throws JAXBException, IOException {
        JAXBContext context;
        try {
            // Formando dados para envio
            context = JAXBContext.newInstance(VeiculosDeCarga.class);
            Marshaller marsHall = context.createMarshaller();
            marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marsHall.marshal(veiculos, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
            
            logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(veiculos.getListaVeiculos().size()));
            veiculos = null;
            
        } catch (Exception e) {
            logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao retornar o resultado da consulta de veiculos!";
            logger.error(msg);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    private void EnviarVeiculoPorIdXML(HttpServletResponse response, VeiculoDeCargaDetalhes veiculo) throws JAXBException, IOException {
        JAXBContext context;
        try {
            // Formando dados para envio
            context = JAXBContext.newInstance(VeiculoDeCargaDetalhes.class);
            Marshaller marsHall = context.createMarshaller();
            marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marsHall.marshal(veiculo, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
            
            veiculo = null;
            
        } catch (Exception e) {
            logger.error("Erro ao EnviarVeiculoPorIdXML(): " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao retornar o resultado da consulta do veículo!";
            logger.error(msg);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
    
    private String FormatarClassificacaoVeicConsultaBD(String listaClassificacoes) {
        String classificacoes = null;
        String[] listaClassificacoesAux = listaClassificacoes.split(",");
        
        if (listaClassificacoesAux.length > 0) {
            for (String classificacao : listaClassificacoesAux) {
                if (classificacoes == null)
                    classificacoes = "";
                else
                    classificacoes += ",";
                
                classificacoes += "'" + classificacao + "'";
            }
        }
        
        return classificacoes;
    }

    private void ListarCores(HttpServletResponse response) {
        try {
            List<JsonObject> cores = VeiculosDeCarga.ListaCores();

            JsonArray jsonArray = new JsonArray();
            for (JsonObject cor : cores) {
                jsonArray.add(cor);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            String msg = "Erro ao listar cores!";
            logger.error(msg, e);
            try {
                response.getWriter().write("{\"erro\": \"" + msg + "\"}");
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta de erro", ioException);
            }
        }
    }

    private void ListarUf(HttpServletResponse response) {
        try {
            List<JsonObject> ufs = VeiculosDeCarga.ListarUf();

            JsonArray jsonArray = new JsonArray();
            for (JsonObject uf : ufs) {
                jsonArray.add(uf);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            String msg = "Erro ao listar cores!";
            logger.error(msg, e);
            try {
                response.getWriter().write("{\"erro\": \"" + msg + "\"}");
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta de erro", ioException);
            }
        }
    }
    
    private void ListarTiposVeiculo(HttpServletResponse response) {
        try {
            List<JsonObject> tiposVeiculo = VeiculosDeCarga.ListarTiposVeiculo();

            JsonArray jsonArray = new JsonArray();
            for (JsonObject tipo : tiposVeiculo) {
                jsonArray.add(tipo);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            String msg = "Erro ao listar cores!";
            logger.error(msg, e);
            try {
                response.getWriter().write("{\"erro\": \"" + msg + "\"}");
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta de erro", ioException);
            }
        }
    }

    private void ListarLocalidades(HttpServletResponse response) {
        try {
            List<JsonObject> localidades = VeiculosDeCarga.ListarLocalidades();

            JsonArray jsonArray = new JsonArray();
            for (JsonObject tipo : localidades) {
                jsonArray.add(tipo);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            String msg = "Erro ao listar localidades!";
            logger.error(msg, e);
            try {
                response.getWriter().write("{\"erro\": \"" + msg + "\"}");
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta de erro", ioException);
            }
        }
    }

    private void ObterDadosUsuario(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Validando acesso do usuário
            final Acesso acessoUsuario = new Acesso(request, response, true);
            if (!acessoUsuario.verificaAcesso()) {
                new Mensagem(response).showErroMuralha("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
                return;
            }
            
            String nomeUsuario = acessoUsuario.getUsuario().getNome();
            int idUsuario = acessoUsuario.getUsuario().getId();
            
            // Criar XML de resposta
            String xmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resposta><nomeUsuario>" + 
                            (nomeUsuario != null ? nomeUsuario : "Usuário") + "</nomeUsuario>"+
                            "<idUsuario>" +
                            idUsuario +
                            "</idUsuario></resposta>";
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xmlResponse);
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter nome do usuário!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }

    private void ListarMarcas(HttpServletResponse response) {
        try {
            List<JsonObject> marcas = VeiculosDeCarga.ListarMarcas();

            JsonArray jsonArray = new JsonArray();
            for (JsonObject marca : marcas) {
                jsonArray.add(marca);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            String msg = "Erro ao listar marcas!";
            logger.error(msg, e);
            try {
                response.getWriter().write("{\"erro\": \"" + msg + "\"}");
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta de erro", ioException);
            }
        }
    }

    private void ListarModelos(HttpServletResponse response) {
        try {
            List<JsonObject> modelos = VeiculosDeCarga.ListarModelos();

            JsonArray jsonArray = new JsonArray();
            for (JsonObject modelo : modelos) {
                jsonArray.add(modelo);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            String msg = "Erro ao listar modelos!";
            logger.error(msg, e);
            try {
                response.getWriter().write("{\"erro\": \"" + msg + "\"}");
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta de erro", ioException);
            }
        }
    }

    private void ExportarConsultaSelecionadosManual(HttpServletRequest request, HttpServletResponse response, Integer idUsuario) {
        try {
            String formato = request.getParameter("formato");
            String motivo = request.getParameter("motivo");
            String dadosJson = request.getParameter("dados");
            
            if (dadosJson == null || dadosJson.trim().isEmpty()) {
                respostaXML.EnviarRespostaRequisicaoXML(response, false, "Nenhum dado selecionado para exportação!");
                return;
            }
            
            List<VeiculoDeCargaRelatorio> veiculosSelecionados = converterJsonParaRelatorio(dadosJson);
            
            RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath("/muralha-digital/relatorios/RelatorioConsultaVeiculosDeCargaManual.jasper"));
            
            String nomeUsuario = UsuarioServlet.obterNomeUsuarioLogado(idUsuario);
            
            relatorio.adicParametro("formato", formato);
            relatorio.adicParametro("motivo", motivo);
            relatorio.adicParametro("usuario", nomeUsuario);
            
            relatorio.adicDataSource(veiculosSelecionados);

            relatorio.preencheRelatorio();
            
            TipoMime tipoMime = "pdf".equalsIgnoreCase(formato) ? TipoMime.PDF : TipoMime.XLS;
            response.setContentType(tipoMime.getTipo());
            response.setHeader("Content-Disposition", "inline; filename=\"ConsultaVeiculosDeCarga." + formato + "\"");
            
            if (tipoMime.compareTo(TipoMime.PDF) == 0) {
                relatorio.exportReportToPdfStream(response.getOutputStream());
            } else {
                relatorio.exportReportToXlsStream(response.getOutputStream());
            }
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao exportar relatório!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }

    private List<VeiculoDeCargaRelatorio> converterJsonParaRelatorio(String dadosJson) {
        List<VeiculoDeCargaRelatorio> veiculos = new ArrayList<>();
        
        try {
            com.google.gson.JsonArray jsonArray = com.google.gson.JsonParser.parseString(dadosJson).getAsJsonArray();
            
            for (com.google.gson.JsonElement element : jsonArray) {
                com.google.gson.JsonObject jsonObj = element.getAsJsonObject();
                
                VeiculoDeCargaRelatorio veiculo = new VeiculoDeCargaRelatorio();
                veiculo.setPlaca(jsonObj.get("placa").getAsString());
                veiculo.setData(jsonObj.get("data").getAsString());
                veiculo.setPesagem("Sim");
                veiculo.setPbt(jsonObj.get("pbt").getAsString());
                veiculo.setPbtc(jsonObj.get("pbtc").getAsString());
                veiculo.setNumeroEixos(jsonObj.get("numeroEixos").getAsString());
                veiculo.setPesosEixos(jsonObj.get("pesosEixos").getAsString());
                veiculo.setDistanciasEixos(jsonObj.get("distanciasEixos").getAsString());
                veiculo.setEquipamento(jsonObj.get("equipamento").getAsString());
                veiculo.setClassificacao(jsonObj.get("classificacao").getAsString());
                
                veiculos.add(veiculo);
            }
        } catch (Exception e) {
            logger.error("Erro ao converter JSON para relatório: " + e.getMessage(), e);
        }
        
        return veiculos;
    }

    private void ExportarConsultaSPU(HttpServletRequest request, HttpServletResponse response, Integer idUsuario) {
        try {
            String formato = request.getParameter("formato");
            String motivo = request.getParameter("motivo");
            
            if (formato == null || !Pattern.matches("(pdf)|(xls)", formato.toLowerCase())) {
                respostaXML.EnviarRespostaRequisicaoXML(response, false, "Formato inválido!");
                return;
            }

            VeiculoDeCargaValidacao veiculoValidacao = ValidarFiltros(request, response, false, true);
            
            if (!veiculoValidacao.isFiltroValido()) {
                respostaXML.EnviarRespostaRequisicaoXML(response, false, veiculoValidacao.getMensagem());
                return;
            }

            RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath("/muralha-digital/relatorios/RelatorioConsultaVeiculosDeCargaSPU.jasper"));
            
            String nomeUsuario = UsuarioServlet.obterNomeUsuarioLogado(idUsuario);
            var pesagem = veiculoValidacao.isPesagem();
            
            relatorio.adicParametro("placa", veiculoValidacao.getPlaca());
            relatorio.adicParametro("data_inicio", veiculoValidacao.getDataIni() != null ? new java.sql.Timestamp(veiculoValidacao.getDataIni().getTime()) : null);
            relatorio.adicParametro("data_fim", veiculoValidacao.getDataFim() != null ? new java.sql.Timestamp(veiculoValidacao.getDataFim().getTime()) : null);
            relatorio.adicParametro("equipamento", veiculoValidacao.getEquipamento());
            relatorio.adicParametro("faixa", veiculoValidacao.getFaixa());
            relatorio.adicParametro("classificacao", veiculoValidacao.getClassificacao());
            relatorio.adicParametro("buscarApenasVeiculoComImagem", veiculoValidacao.isBuscarApenasVeiculoComImagem());
            relatorio.adicParametro("consultaMapa", false);
            relatorio.adicParametro("exportarConsulta", true);
            relatorio.adicParametro("formato", formato);
            relatorio.adicParametro("motivo", motivo);
            relatorio.adicParametro("usuario", nomeUsuario);
            relatorio.adicParametro("marca", veiculoValidacao.getMarca());
            relatorio.adicParametro("modelo", veiculoValidacao.getModelo());
            relatorio.adicParametro("idCor", veiculoValidacao.getCorVeiculo());
            relatorio.adicParametro("anoFabricacao", veiculoValidacao.getAnoFabricacao());
            relatorio.adicParametro("anoModelo", veiculoValidacao.getAnoModelo());
            relatorio.adicParametro("renavam", veiculoValidacao.getRenavam());
            relatorio.adicParametro("chassi", veiculoValidacao.getChassi());
            relatorio.adicParametro("idLocalidade", veiculoValidacao.getIdLocalidade());
            relatorio.adicParametro("restricao", veiculoValidacao.getRestricao());
            relatorio.adicParametro("filtroPlaca", veiculoValidacao.getFiltroPlaca());
            relatorio.adicParametro("tipoPlaca", veiculoValidacao.getTipoPlaca());
            relatorio.adicParametro("tipoVeiculo", veiculoValidacao.getTipoVeiculo());
            relatorio.adicParametro("apenasVeiculosCarga", veiculoValidacao.isApenasVeiculosComCarga());
            relatorio.adicParametro("somenteUltimaPassagem", veiculoValidacao.isSomenteUltimaPassagem());
            relatorio.adicParametro("comPesagem", pesagem);
            
            relatorio.preencheRelatorio();
            
            TipoMime tipoMime = "pdf".equalsIgnoreCase(formato) ? TipoMime.PDF : TipoMime.XLS;
            response.setContentType(tipoMime.getTipo());
            response.setHeader("Content-Disposition", "inline; filename=\"ConsultaVeiculosDeCarga." + formato + "\"");
            
            if (tipoMime.compareTo(TipoMime.PDF) == 0) {
                relatorio.exportReportToPdfStream(response.getOutputStream());
            } else {
                relatorio.exportReportToXlsStream(response.getOutputStream());
            }
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao exportar relatório!";
            logger.error(msg, e);    
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }
    }
}