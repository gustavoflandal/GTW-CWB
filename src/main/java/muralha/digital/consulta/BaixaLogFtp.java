package muralha.digital.consulta;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

@WebServlet("/MuralhaDigital/Ftp")
public class BaixaLogFtp extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BaixaLogFtp.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dataFiltro = request.getParameter("dataIniValor"); // Ex: 2025-10-15
        String idEquip = request.getParameter("equip_sel");       // Ex: 202
        String pastaData = dataFiltro.replace("-", "");           // Ex: 20251013

        logger.info("Recebendo parâmetros: data=" + pastaData + ", idEquip=" + idEquip);

        if (dataFiltro == null || idEquip == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros obrigatórios ausentes.");
            return;
        }

        // 🔹 Caminho base local (ajuste conforme o seu ambiente)
        String caminhoBase = "E:\\Velsis\\IPATINGA\\logs\\" + idEquip + "\\" + pastaData; //Caminho do servido
       // String caminhoBase = "C:\\Users\\luiz.sai\\Desktop\\teste\\" + idEquip + "\\" + pastaData; // caminho para teste
        File pasta = new File(caminhoBase);

        logger.info("Buscando arquivos em: " + pasta.getAbsolutePath());

        if (!pasta.exists() || !pasta.isDirectory()) {
            logger.warn("Pasta não encontrada: " + caminhoBase);
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Pasta não encontrada: " + caminhoBase);
            return;
        }

        File[] arquivos = pasta.listFiles();
        if (arquivos == null || arquivos.length == 0) {
            logger.warn("Nenhum arquivo encontrado na pasta: " + caminhoBase);
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Nenhum arquivo encontrado na pasta " + caminhoBase);
            return;
        }

        // 🔹 Configura resposta HTTP como ZIP
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"Logs_" + idEquip + "_" + pastaData + ".zip\"");

        boolean encontrouArquivo = false;

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) {
                    logger.info("Adicionando arquivo ao ZIP: " + arquivo.getName());
                    try (FileInputStream fis = new FileInputStream(arquivo)) {
                        zos.putNextEntry(new ZipEntry(arquivo.getName()));
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            zos.write(buffer);
                        }
                        zos.closeEntry();
                        encontrouArquivo = true;
                    } catch (Exception e) {
                        logger.error("Erro ao adicionar arquivo " + arquivo.getName() + " ao ZIP", e);
                    }
                }
            }
        }

        if (!encontrouArquivo) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Nenhum arquivo válido encontrado na pasta.");
        }

        logger.info("ZIP gerado com sucesso para: " + pastaData);
    }
}
