/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 17/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Locale;

/**
 * Classe responsável pela assinatura digital de imagens JPEG.
 *
 * Implementa funcionalidades de assinatura digital embutida em imagens,
 * identificação de origem do sistema GTW-MURALHA, validação de integridade
 * e extração de metadados de assinatura.
 */
public class AssinaturaImagemService {
    /** Configuração de assinatura carregada */
    private final AssinaturaConfig cfg;
    /** Chave privada para assinatura digital */
    private volatile PrivateKey privateKey;
    /** Chave pública para verificação de assinatura */
    private volatile PublicKey publicKey;
    
    /** Cache de instâncias MessageDigest para SHA-256 */
    private static final ThreadLocal<MessageDigest> SHA256_CACHE = 
        ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar MessageDigest SHA-256", e);
            }
        });
    
    /** Cache de instâncias Signature */
    private ThreadLocal<Signature> signatureCache;

    /**
     * Construtor do serviço de assinatura de imagens.
     * 
     * @param cfg configuração de assinatura digital
     */
    public AssinaturaImagemService(AssinaturaConfig cfg) {
        this.cfg = cfg;
        this.signatureCache = ThreadLocal.withInitial(() -> {
            try {
                return Signature.getInstance(cfg.algoritmo);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar instância Signature", e);
            }
        });
    }

    /**
     * Carrega as chaves privada e pública do keystore se ainda não foram carregadas.
     * Cria keystore automaticamente se não existir.
     * 
     * @throws Exception se houver erro ao carregar o keystore ou chaves
     */
    synchronized void carregarChavesSeNecessario() throws Exception {
        if (privateKey != null && publicKey != null) return;
        
        if (cfg.keystorePath == null || cfg.keystoreSenha == null) {
            throw new IllegalStateException("Keystore ou senha não configurados");
        }

        if (!Files.exists(cfg.keystorePath)) {
            System.out.println("Keystore não encontrado, criando automaticamente para desenvolvimento: " + cfg.keystorePath);
            KeystoreAutoGenerator.criarKeystoreDesenvolvimento(cfg.keystorePath, cfg.keystoreSenha, cfg.keystoreAlias);
        }

        KeyStore ks = KeyStore.getInstance(cfg.keystorePath.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".p12") ? "PKCS12" : "JKS");
        try (java.io.InputStream in = Files.newInputStream(cfg.keystorePath)) {
            ks.load(in, cfg.keystoreSenha.toCharArray());
        }
        
        KeyStore.Entry entry = ks.getEntry(cfg.keystoreAlias, new KeyStore.PasswordProtection(cfg.keystoreSenha.toCharArray()));
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new IllegalStateException("Alias não é PrivateKeyEntry: " + cfg.keystoreAlias);
        }
        
        KeyStore.PrivateKeyEntry pke = (KeyStore.PrivateKeyEntry) entry;
        
        PrivateKey newPrivateKey = pke.getPrivateKey();
        Certificate cert = pke.getCertificate();
        PublicKey newPublicKey = cert.getPublicKey();
        
        this.privateKey = newPrivateKey;
        this.publicKey = newPublicKey;
    }

    /**
     * Assina dados utilizando a chave privada carregada.
     * 
     * @param dados dados a serem assinados
     * @return assinatura digital dos dados
     * @throws Exception se houver erro na assinatura
     */
    public byte[] assinar(byte[] dados) throws Exception {
        carregarChavesSeNecessario();
        
        Signature sig = signatureCache.get();
        sig.initSign(privateKey);
        sig.update(dados);
        return sig.sign();
    }

    /**
     * Verifica a assinatura digital dos dados utilizando a chave pública.
     * 
     * @param dados dados originais
     * @param assinatura assinatura digital a ser verificada
     * @return true se a assinatura for válida, false caso contrário
     * @throws Exception se houver erro na verificação
     */
    public boolean verificar(byte[] dados, byte[] assinatura) throws Exception {
        carregarChavesSeNecessario();
        
        Signature sig = signatureCache.get();
        sig.initVerify(publicKey);
        sig.update(dados);
        return sig.verify(assinatura);
    }

    /**
     * Calcula o hash SHA-256 dos dados e retorna em formato hexadecimal.
     * 
     * @param dados dados para cálculo do hash
     * @return hash SHA-256 em formato hexadecimal lowercase
     * @throws Exception se houver erro no cálculo do hash
     */
    public static String sha256Hex(byte[] dados) throws Exception {
        MessageDigest md = SHA256_CACHE.get();
        md.reset();
        byte[] h = md.digest(dados);
        
        StringBuilder sb = new StringBuilder(h.length * 2);
        for (byte b : h) {
            sb.append(String.format(Locale.ROOT, "%02x", b));
        }
        return sb.toString();
    }

    /**
     * Assina imagem digitalmente com metadados protegidos embutidos sem salvar em arquivo.
     *
     * @param dadosImagem       Dados binários da imagem original
     * @param tipoSolicitante   Tipo do solicitante (MANUAL ou SISTEMA)
     * @return                  Array de bytes da imagem assinada
     * @throws Exception        Caso ocorra erro durante assinatura
     */
    public byte[] assinarImagem(byte[] dadosImagem, TipoSolicitanteAssinatura tipoSolicitante) throws Exception {
        return assinarImagemComMetadados(dadosImagem, tipoSolicitante, null, null);
    }
    
    /**
     * Assina imagem com metadados estruturados (coordenadas, extras) protegidos digitalmente.
     * 
     * @param dadosImagem dados binários da imagem original
     * @param tipoSolicitante tipo do solicitante (MANUAL ou SISTEMA)
     * @param coordenadas coordenadas geográficas no formato "latitude,longitude" (opcional)
     * @param metadadosExtras metadados adicionais customizáveis (opcional)
     * @return dados da imagem com assinatura digital e metadados embutidos
     * @throws Exception se houver erro na assinatura
     */
    public byte[] assinarImagemComMetadados(byte[] dadosImagem, TipoSolicitanteAssinatura tipoSolicitante, 
                                        String coordenadas, String metadadosExtras) throws Exception {
        DadosAssinatura dados = new DadosAssinatura(dadosImagem, null, tipoSolicitante, cfg.keystoreAlias);
        
        if (coordenadas != null && !coordenadas.trim().isEmpty()) {
            dados.comCoordenadas(coordenadas);
        }
        
        if (metadadosExtras != null && !metadadosExtras.trim().isEmpty()) {
            dados.comMetadados(metadadosExtras);
        }
        
        byte[] bytesParaAssinar = dados.gerarBytesParaAssinatura();
        byte[] assinatura = assinar(bytesParaAssinar);
        
        String assinaturaB64 = Base64.getEncoder().encodeToString(assinatura);
        String comentario = dados.gerarComentarioJPEG(assinaturaB64);
        
        return embutirComentarioJPEG(dadosImagem, comentario);
    }

    /**
     * Embute um comentário em uma imagem JPEG inserindo segmento COM após o cabeçalho.
     * 
     * @param dadosImagem dados da imagem JPEG original
     * @param comentario comentário a ser embutido
     * @return dados da imagem com comentário embutido
     * @throws Exception se a imagem não for JPEG válida ou houver erro no processamento
     */
    private byte[] embutirComentarioJPEG(byte[] dadosImagem, String comentario) throws Exception {
        if (dadosImagem.length < 2 || dadosImagem[0] != (byte)0xFF || dadosImagem[1] != (byte)0xD8) {
            throw new IllegalArgumentException("Não é uma imagem JPEG válida");
        }
        
        byte[] comentarioBytes = comentario.getBytes(StandardCharsets.UTF_8);
        
        ByteArrayOutputStream resultado = new ByteArrayOutputStream(dadosImagem.length + comentarioBytes.length + 10);
        
        resultado.write(0xFF);
        resultado.write(0xD8);
        
        resultado.write(0xFF);
        resultado.write(0xFE);
        
        int tamanho = comentarioBytes.length + 2;
        resultado.write((tamanho >> 8) & 0xFF);
        resultado.write(tamanho & 0xFF);
        
        resultado.write(comentarioBytes);
        
        resultado.write(dadosImagem, 2, dadosImagem.length - 2);
        
        return resultado.toByteArray();
    }

    /**
     * Verifica se uma imagem possui assinatura digital embutida (válida ou inválida).
     * 
     * @param dadosImagem dados da imagem JPEG
     * @return true se a imagem possui assinatura embutida, false caso contrário
     * @throws Exception se houver erro ao processar a imagem
     */
    public boolean verificarImagemComAssinatura(byte[] dadosImagem) throws Exception {
        String comentario = extrairComentarioJPEG(dadosImagem);
        return comentario != null && comentario.startsWith("DIGITAL_SIGNATURE:");
    }

    /**
     * Verifica se uma imagem possui assinatura digital válida.
     * 
     * @param dadosImagem dados da imagem JPEG
     * @return true se a assinatura for válida, false caso contrário
     * @throws Exception se houver erro ao processar a imagem ou verificar assinatura
     */
    public boolean verificarAssinaturaValida(byte[] dadosImagem) throws Exception {
        String comentario = extrairComentarioJPEG(dadosImagem);
        if (comentario == null || !comentario.startsWith("DIGITAL_SIGNATURE:")) {
            return false;
        }
        
        String[] partes = comentario.split("\\|");
        if (partes.length == 0) return false;
        
        String assinaturaB64 = partes[0].substring("DIGITAL_SIGNATURE:".length());
        byte[] assinatura = Base64.getDecoder().decode(assinaturaB64);
        byte[] dadosOriginais = removerComentarioJPEG(dadosImagem);
        
        try {
            String tipoSolicitante = extrairTipoSolicitante(comentario);
            String origem = extrairOrigem(comentario);
            String aliasCertificado = extrairAliasCertificado(comentario);
            String dataAssinatura = extrairDataAssinatura(comentario);
            
            if (tipoSolicitante != null && origem != null && aliasCertificado != null && dataAssinatura != null) {
                TipoSolicitanteAssinatura tipoEnum = TipoSolicitanteAssinatura.valueOf(tipoSolicitante);
                DadosAssinatura dadosReconstruidos = new DadosAssinatura(dadosOriginais, origem, tipoEnum, aliasCertificado, dataAssinatura);
                
                String coordenadas = extrairCoordenadas(comentario);
                String metadadosExtras = extrairMetadadosExtras(comentario);
                
                if (coordenadas != null) {
                    dadosReconstruidos.comCoordenadas(coordenadas);
                }
                
                if (metadadosExtras != null) {
                    dadosReconstruidos.comMetadados(metadadosExtras);
                }
                
                byte[] bytesParaVerificar = dadosReconstruidos.gerarBytesParaAssinatura();
                return verificar(bytesParaVerificar, assinatura);
            } else {
                return verificar(dadosOriginais, assinatura);
            }
        } catch (Exception e) {
            return verificar(dadosOriginais, assinatura);
        }
    }
    
    /**
     * Extrai campo específico do comentário JPEG usando prefixo.
     * Consolida lógica de múltiplos métodos similares em um método reutilizável.
     * 
     * @param comentario comentário JPEG contendo metadados estruturados
     * @param prefixo prefixo do campo a ser extraído (ex: "COORDENADAS", "ORIGEM")
     * @return valor do campo encontrado ou null se não existir
     */
    private String extrairCampo(String comentario, String prefixo) {
        if (comentario == null) return null;
        
        String[] partes = comentario.split("\\|");
        String busca = prefixo + ":";
        
        for (String parte : partes) {
            if (parte.startsWith(busca)) {
                return parte.substring(busca.length());
            }
        }
        return null;
    }
    
    /**
     * Extrai coordenadas geográficas dos metadados da assinatura.
     * @param comentario comentário JPEG com metadados
     * @return coordenadas no formato "latitude,longitude" ou null
     */
    private String extrairCoordenadas(String comentario) {
        return extrairCampo(comentario, "COORDENADAS");
    }
    
    /**
     * Extrai metadados extras customizados da assinatura.
     * @param comentario comentário JPEG com metadados  
     * @return metadados extras ou null
     */
    private String extrairMetadadosExtras(String comentario) {
        return extrairCampo(comentario, "EXTRAS");
    }
    
    /**
     * Extrai sistema de origem da assinatura.
     * @param comentario comentário JPEG com metadados
     * @return identificador do sistema de origem ou null
     */
    private String extrairOrigem(String comentario) {
        return extrairCampo(comentario, "ORIGEM");
    }
    
    /**
     * Extrai alias do certificado usado na assinatura.
     * @param comentario comentário JPEG com metadados
     * @return alias do certificado ou null  
     */
    private String extrairAliasCertificado(String comentario) {
        return extrairCampo(comentario, "CERTIFICADO");
    }
    
    /**
     * Extrai timestamp da assinatura digital.
     * @param comentario comentário JPEG com metadados
     * @return timestamp ISO da assinatura ou null
     */
    private String extrairDataAssinatura(String comentario) {
        return extrairCampo(comentario, "TIMESTAMP");
    }
    
    /**
     * Extrai tipo de solicitante da assinatura.
     * @param comentario comentário JPEG com metadados
     * @return tipo do solicitante ou "MANUAL" como padrão
     */
    private String extrairTipoSolicitante(String comentario) {
        String resultado = extrairCampo(comentario, "SOLICITANTE");
        return resultado != null ? resultado : "MANUAL";
    }

    /**
     * Extrai comentário COM de uma imagem JPEG.
     * 
     * @param dados dados da imagem JPEG
     * @return comentário extraído ou null se não houver
     */
    private String extrairComentarioJPEG(byte[] dados) {
        if (dados.length < 4) return null;
        
        int pos = 2; 
        
        while (pos < dados.length - 1) {
            if (dados[pos] != (byte)0xFF) break;
            
            int marcador = dados[pos + 1] & 0xFF;
            pos += 2;
            
            if (marcador == 0xFE) {
                if (pos + 1 >= dados.length) break;
                
                int tamanho = ((dados[pos] & 0xFF) << 8) | (dados[pos + 1] & 0xFF);
                pos += 2;
                
                if (pos + tamanho - 2 > dados.length) break;
                
                byte[] comentarioBytes = new byte[tamanho - 2];
                System.arraycopy(dados, pos, comentarioBytes, 0, tamanho - 2);
                
                return new String(comentarioBytes, StandardCharsets.UTF_8);
            } else if (marcador == 0xD9) {
                break;
            } else {
                if (pos + 1 >= dados.length) break;
                int tamanho = ((dados[pos] & 0xFF) << 8) | (dados[pos + 1] & 0xFF);
                pos += tamanho;
            }
        }
        
        return null;
    }

    /**
     * Remove comentário COM de uma imagem JPEG para obter dados originais sem assinatura.
     * 
     * @param dados dados da imagem JPEG com comentário
     * @return dados da imagem sem comentário
     * @throws Exception se houver erro ao processar a imagem
     */
    private byte[] removerComentarioJPEG(byte[] dados) throws Exception {
        if (dados.length < 2) return dados;
        
        ByteArrayOutputStream resultado = new ByteArrayOutputStream(dados.length - 100);
        
        resultado.write(dados[0]);
        resultado.write(dados[1]);
        
        int pos = 2;
        while (pos < dados.length - 1) {
            if (dados[pos] != (byte)0xFF) {
                resultado.write(dados, pos, dados.length - pos);
                break;
            }
            
            int marcador = dados[pos + 1] & 0xFF;
            
            if (marcador == 0xFE) {
                pos += 2;
                if (pos + 1 >= dados.length) break;
                int tamanho = ((dados[pos] & 0xFF) << 8) | (dados[pos + 1] & 0xFF);
                pos += tamanho;
            } else {
                resultado.write(dados[pos]);
                resultado.write(dados[pos + 1]);
                pos += 2;
                
                if (marcador == 0xD9) break;
                
                if (pos + 1 >= dados.length) break;
                int tamanho = ((dados[pos] & 0xFF) << 8) | (dados[pos + 1] & 0xFF);
                
                resultado.write(dados, pos, tamanho);
                pos += tamanho;
            }
        }
        
        return resultado.toByteArray();
    }

    /**
     * Obtém o hash SHA-256 da imagem original (sem assinatura embutida).
     * Para imagens com assinatura, remove os comentários antes de calcular o hash.
     * Para imagens sem assinatura, calcula o hash diretamente.
     * 
     * @param dadosImagem dados da imagem JPEG
     * @return hash SHA-256 em formato hexadecimal da imagem original
     * @throws Exception se houver erro ao processar a imagem ou calcular hash
     */
    public String obterHashImagemOriginal(byte[] dadosImagem) throws Exception {
        if (verificarImagemComAssinatura(dadosImagem)) {
            byte[] dadosOriginais = removerComentarioJPEG(dadosImagem);
            return sha256Hex(dadosOriginais);
        } else {
            return sha256Hex(dadosImagem);
        }
    }

    /**
     * Extrai o tipo de solicitante dos metadados de uma imagem assinada.
     * @param dadosImagem - Bytes da imagem assinada
     * @return Tipo do solicitante extraído dos metadados
     * @throws Exception se a imagem não possuir assinatura ou metadados válidos
     */
    public TipoSolicitanteAssinatura obterTipoSolicitante(byte[] dadosImagem) throws Exception {
        String comentario = extrairComentarioJPEG(dadosImagem);
        if (comentario == null || !comentario.startsWith("DIGITAL_SIGNATURE:")) {
            return TipoSolicitanteAssinatura.NULL; 
        }
        
        String[] partes = comentario.split("\\|");
        for (String parte : partes) {
            if (parte.startsWith("SOLICITANTE:")) {
                String tipoSolicitanteStr = parte.substring(12);
                return TipoSolicitanteAssinatura.valueOf(tipoSolicitanteStr);
            }
        }

        return TipoSolicitanteAssinatura.NULL;
    }
}