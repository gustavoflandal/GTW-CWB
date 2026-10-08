package muralha.digital.monitorado;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/VeiculoAuxiliar")
public class VeiculoAuxiliarServlet extends HttpServlet implements Servlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger =
            Logger.getLogger(VeiculoAuxiliarServlet.class);

    private static RespostaRequisicaoXML respostaXML =
            new RespostaRequisicaoXML();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Validando acesso do usuário
        if (!new Acesso(request, response, true).verificaAcesso(false)) {
            new Mensagem(response).showErro(
                    "Usuário não autenticado!",
                    "/login/abertura-sistemas.jsp"
            );
            return;
        }

        try {

            String strAcao = request.getParameter("acao");

            if (strAcao == null || strAcao.isEmpty()) {
                String msg = "Ação não informada!";

                logger.error(msg);

                respostaXML.EnviarRespostaRequisicaoXML(
                        response,
                        false,
                        msg
                );

                return;
            }

            switch (strAcao) {

            case "buscarCores":
                ObterCores(request, response);
                break;

            case "buscarMarcas":
                ObterMarcas(request, response);
                break;

            case "buscarModelos":
                ObterModelos(request, response);
                break;

            case "buscarClassesVeiculo":
                ObterClassesVeiculo(request, response);
                break;

            default:
                String msg = "Ação inválida!";
                logger.error(msg);
                respostaXML.EnviarRespostaRequisicaoXML(
                        response,
                        false,
                        msg
                );
                break;
            }

        }
        catch (Exception e) {

            logger.error(
                    "Erro no processo doGet() de requisição de veículos: "
                    + e.getMessage(),
                    e
            );

            respostaXML.EnviarRespostaRequisicaoXML(
                    response,
                    false,
                    "Ocorreu um erro ao processar a requisição."
            );
        }
    }


    public void ObterCores(
            HttpServletRequest request,
            HttpServletResponse response)
            throws JAXBException, IOException,
            ConexaoException, SQLException {

        Cores listaCores = new Cores();

        listaCores.setListaCores(
                new ArrayList<CorEntidade>()
        );

        List<CorEntidade> resultListaCores =
                Cores.ObterListaCores();

        listaCores.setListaCores(resultListaCores);

        EnviarRespostaXML(
                response,
                listaCores,
                Cores.class
        );
    }


    public void ObterMarcas(
            HttpServletRequest request,
            HttpServletResponse response)
            throws JAXBException, IOException,
            ConexaoException, SQLException {

        Marcas listaMarcas = new Marcas();

        listaMarcas.setListaMarcas(
                new ArrayList<MarcaEntidade>()
        );

        List<MarcaEntidade> resultListaMarcas =
                Marcas.ObterListaMarcas();

        listaMarcas.setListaMarcas(resultListaMarcas);

        EnviarRespostaXML(
                response,
                listaMarcas,
                Marcas.class
        );
    }


    public void ObterModelos(
            HttpServletRequest request,
            HttpServletResponse response)
            throws JAXBException, IOException,
            ConexaoException, SQLException {

        Modelos listaModelos = new Modelos();

        listaModelos.setListaModelos(
                new ArrayList<ModeloEntidade>()
        );

        List<ModeloEntidade> resultListaModelos =
                Modelos.ObterListaModelos();

        listaModelos.setListaModelos(resultListaModelos);

        EnviarRespostaXML(
                response,
                listaModelos,
                Modelos.class
        );
    }
    
    public void ObterClassesVeiculo(
            HttpServletRequest request,
            HttpServletResponse response)
            throws JAXBException, IOException,
            ConexaoException, SQLException {

        ClassesVeiculo listaClassesVeiculo =
                new ClassesVeiculo();

        listaClassesVeiculo.setListaClassesVeiculo(
                new ArrayList<ClasseVeiculoEntidade>()
        );

        List<ClasseVeiculoEntidade> resultListaClassesVeiculo =
                ClassesVeiculo.ObterListaClassesVeiculo();

        listaClassesVeiculo.setListaClassesVeiculo(
                resultListaClassesVeiculo
        );

        EnviarRespostaXML(
                response,
                listaClassesVeiculo,
                ClassesVeiculo.class
        );
    }

    private void EnviarRespostaXML(
            HttpServletResponse response,
            Object objeto,
            Class<?> classe)
            throws JAXBException, IOException {

        try {

            JAXBContext context =
                    JAXBContext.newInstance(classe);

            Marshaller marsHall =
                    context.createMarshaller();

            marsHall.setProperty(
                    Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE
            );

            StringWriter sw =
                    new StringWriter();

            marsHall.marshal(objeto, sw);

            String xml =
                    sw.toString();

            sw.close();

            response.setHeader(
                    "Content-Type",
                    "text/xml"
            );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().write(xml);
            response.getWriter().flush();

        }
        catch (Exception e) {

            logger.error(
                    "Erro ao EnviarRespostaXML(): "
                    + e.getMessage(),
                    e
            );

            String msg =
                    "Ocorreu um erro ao retornar os dados do veículo!";

            respostaXML.EnviarRespostaRequisicaoXML(
                    response,
                    false,
                    msg
            );
        }
    }
}