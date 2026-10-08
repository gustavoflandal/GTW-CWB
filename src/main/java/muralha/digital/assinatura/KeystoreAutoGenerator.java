/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 17/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Gerador automático de keystore para desenvolvimento usando keytool quando disponível.
 * Estratégia simples que funciona sem dependências externas.
 */
public class KeystoreAutoGenerator {
    
    /** Logger para registrar operações da classe */
    private static final Logger logger = Logger.getLogger(KeystoreAutoGenerator.class.getName());
    
    /**
     * Cria um keystore de desenvolvimento automaticamente se não existir.
     * Prioriza uso do keytool quando disponível, senão fornece instruções manuais.
     * 
     * @param keystorePath caminho onde o keystore será criado
     * @param senha senha para o keystore e chave privada
     * @param alias alias da chave dentro do keystore
     * @throws Exception se houver erro na criação ou keytool indisponível
     */
    public static void criarKeystoreDesenvolvimento(Path keystorePath, String senha, String alias) throws Exception {
        
        // Verifica se keystore já existe
        if (Files.exists(keystorePath)) {
            logger.info("Keystore já existe em: " + keystorePath);
            return;
        }
        
        // Cria diretório se necessário
        Files.createDirectories(keystorePath.getParent());
        
        // Tenta criar keystore usando keytool
        criarKeystoreBasico(keystorePath, senha, alias);
    }
    
    /**
     * Cria keystore básico usando keytool (comando nativo Java).
     * Se keytool não estiver disponível, gera exceção com instruções manuais.
     * 
     * @param keystorePath caminho do keystore a ser criado
     * @param senha senha para keystore e chave privada
     * @param alias alias da chave privada
     * @throws Exception se keytool falhar ou não estiver disponível
     */
    private static void criarKeystoreBasico(Path keystorePath, String senha, String alias) throws Exception {
        
        try {
            // Comando keytool para criar keystore PKCS12 com certificado auto-assinado
            ProcessBuilder pb = new ProcessBuilder(
                "keytool", 
                "-genkeypair",
                "-alias", alias,
                "-keyalg", "RSA", 
                "-keysize", "2048",
                "-sigalg", "SHA256withRSA",
                "-validity", "3650",
                "-storetype", "PKCS12",
                "-keystore", keystorePath.toString(),
                "-storepass", senha,
                "-keypass", senha,
                "-dname", "CN=Auto-Generated Certificate, OU=Development, O=GTW, L=Curitiba, ST=PR, C=BR"
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Keystore criado com sucesso: " + keystorePath);
            } else {
                throw new RuntimeException("Erro ao executar keytool. Código de saída: " + exitCode);
            }
            
        } catch (IOException | InterruptedException e) {
            // keytool não está disponível ou falhou, fornece instruções manuais
            String instrucoes = String.format(
                "\nKEYSTORE NÃO ENCONTRADO E KEYTOOL INDISPONÍVEL\n\n" +
                "Para resolver este problema, execute manualmente:\n\n" +
                "keytool -genkeypair -alias %s -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 3650 -storetype PKCS12 -keystore \"%s\" -storepass %s -keypass %s -dname \"CN=Auto-Generated Certificate, OU=Development, O=GTW, L=Curitiba, ST=PR, C=BR\"\n\n" +
                "Ou configure as variáveis de ambiente:\n" +
                "- SIGN_KS_PATH: caminho para seu keystore existente\n" +
                "- SIGN_KS_PASSWORD: senha do keystore\n" +
                "- SIGN_KS_ALIAS: alias da chave privada\n",
                alias, keystorePath, senha, senha);
            
            throw new RuntimeException(instrucoes, e);
        }
    }
}