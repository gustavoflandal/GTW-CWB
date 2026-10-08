/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 17/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Classe responsável pelo carregamento de configurações de assinatura digital.
 *
 * Carrega configurações através de variáveis de ambiente ou system properties,
 * mantendo compatibilidade com o sistema legado sem criar novos arquivos de configuração.
 */
public class AssinaturaConfig {
    
    /** Algoritmo de assinatura digital utilizado (padrão: SHA256withRSA) */
    public final String algoritmo;
    
    /** Caminho para o arquivo keystore contendo o certificado digital */
    public final Path keystorePath;
    
    /** Senha para acesso ao keystore */
    public final String keystoreSenha;
    
    /** Alias do certificado dentro do keystore (padrão: app-signing) */
    public final String keystoreAlias;
    
    /** Diretório de armazenamento para arquivos de assinatura */
    public final Path storageDir;

    /**
     * Construtor privado para criar instância de configuração.
     * 
     * @param algoritmo algoritmo de assinatura digital a ser utilizado
     * @param keystorePath caminho para o arquivo keystore
     * @param keystoreSenha senha de acesso ao keystore
     * @param keystoreAlias alias do certificado dentro do keystore
     * @param storageDir diretório de armazenamento dos arquivos assinados
     */
    private AssinaturaConfig(String algoritmo, Path keystorePath, String keystoreSenha, String keystoreAlias, Path storageDir) {
        this.algoritmo = algoritmo;
        this.keystorePath = keystorePath;
        this.keystoreSenha = keystoreSenha;
        this.keystoreAlias = keystoreAlias;
        this.storageDir = storageDir;
    }

    /**
     * Carrega configuração de assinatura digital através de propriedades do sistema ou variáveis de ambiente.
     * 
     * Se keystore não estiver configurado, utiliza keystore automático para desenvolvimento.
     * Propriedades de sistema têm precedência sobre variáveis de ambiente.
     * 
     * @return instância configurada de AssinaturaConfig
     */
    public static AssinaturaConfig carregar() {
        String alg = getCfg("assinatura.algorithm", "SIGN_ALGORITHM", "SHA256withRSA");
        String ks = getCfg("assinatura.keystore.path", "SIGN_KS_PATH", null);
        String pw = getCfg("assinatura.keystore.password", "SIGN_KS_PASSWORD", null);
        String al = getCfg("assinatura.keystore.alias", "SIGN_KS_ALIAS", "app-signing");
        String dir = getCfg("assinatura.storage.dir", "SIGN_STORAGE_DIR", "target/assinaturas");

        if (ks == null || pw == null) {
            ks = "src/main/java/muralha/digital/assinatura/keystore/muralha-keystore.p12";
            pw = "MuralhaDigital#2025$Secure!KeyStore@Consilux";
            al = "app-signing";
        }

        Path ksPath = Paths.get(ks);
        Path storage = Paths.get(dir);
        try { 
            Files.createDirectories(storage);
            Files.createDirectories(ksPath.getParent());
        } catch (Exception ignore) {}
        
        return new AssinaturaConfig(alg, ksPath, pw, al, storage);
    }

    /**
     * Obtém valor de configuração com ordem de precedência: propriedade do sistema → variável de ambiente → padrão.
     * 
     * @param sysProp nome da propriedade do sistema
     * @param env nome da variável de ambiente
     * @param def valor padrão caso nenhum esteja definido
     * @return valor de configuração encontrado ou padrão
     */
    private static String getCfg(String sysProp, String env, String def) {
        String v = System.getProperty(sysProp);
        if (v == null || v.trim().isEmpty()) v = System.getenv(env);
        if (v == null || v.trim().isEmpty()) v = def;
        return v;
    }
}