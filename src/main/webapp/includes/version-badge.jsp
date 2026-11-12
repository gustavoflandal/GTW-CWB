<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.jar.*" %>
<%--
  Selo da VERSAO COMPILADA em execucao, fixo no canto inferior direito.
  Resolve a versao por varias fontes, em ordem: (1) MANIFEST do WAR (producao),
  (2) nome da pasta versionada do deploy gtw-<ver>-r<sha>, (3) commit do .git
  (desenvolvimento, servido de src/main/webapp). Blindado: nunca quebra a pagina.
  REGRA: a tela principal deve sempre incluir este arquivo (ver CLAUDE.md).
--%>
<%!
    private String lerArquivo(File f) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String ln;
        try { while ((ln = r.readLine()) != null) sb.append(ln).append('\n'); }
        finally { r.close(); }
        return sb.toString();
    }

    private String resolverVersao(javax.servlet.ServletContext ctx) {
        String versao = "3.0";
        String build = null;
        // 1) MANIFEST do WAR (producao)
        try {
            InputStream is = ctx.getResourceAsStream("/META-INF/MANIFEST.MF");
            if (is != null) {
                Manifest mf = new Manifest(is);
                Attributes a = mf.getMainAttributes();
                String v = a.getValue("Version");
                String b = a.getValue("Implementation-Build");
                is.close();
                if (v != null && v.trim().length() > 0 && !v.contains("devel")) versao = v.trim();
                if (b != null && b.trim().length() > 0 && !b.contains("devel")) build = b.trim();
            }
        } catch (Exception e) { /* ignora */ }
        // 2) pasta versionada do deploy: .../gtw-<ver>-r<sha>/ROOT
        if (build == null) {
            try {
                File ver = new File(ctx.getRealPath("/")).getParentFile();
                if (ver != null) {
                    String nome = ver.getName();
                    int ri = nome.indexOf("-r");
                    if (nome.startsWith("gtw-") && ri > 4) {
                        versao = nome.substring(4, ri);
                        build  = nome.substring(ri + 2);
                    }
                }
            } catch (Exception e) { /* ignora */ }
        }
        // 3) commit do .git (desenvolvimento)
        if (build == null) {
            try {
                File dir = new File(ctx.getRealPath("/"));
                File git = null;
                for (int i = 0; i < 8 && dir != null; i++) {
                    File g = new File(dir, ".git");
                    if (g.exists()) { git = g; break; }
                    dir = dir.getParentFile();
                }
                if (git != null) {
                    File gitDir = git;
                    if (git.isFile()) {
                        String l = lerArquivo(git).trim();
                        if (l.startsWith("gitdir:")) gitDir = new File(l.substring(7).trim());
                    }
                    String head = lerArquivo(new File(gitDir, "HEAD")).trim();
                    String sha = null;
                    if (head.startsWith("ref:")) {
                        String ref = head.substring(4).trim();
                        File refFile = new File(gitDir, ref);
                        if (refFile.exists()) {
                            sha = lerArquivo(refFile).trim();
                        } else {
                            File pr = new File(gitDir, "packed-refs");
                            if (pr.exists()) {
                                for (String line : lerArquivo(pr).split("\n")) {
                                    line = line.trim();
                                    if (line.endsWith(" " + ref) && line.indexOf(' ') > 0) { sha = line.substring(0, line.indexOf(' ')); break; }
                                }
                            }
                        }
                    } else {
                        sha = head;
                    }
                    if (sha != null && sha.length() >= 7) build = sha.substring(0, 8);
                }
            } catch (Exception e) { /* ignora */ }
        }
        return build != null ? ("v" + versao + " · build " + build) : ("v" + versao);
    }
%>
<%
    String __verLabel;
    try { __verLabel = resolverVersao(application); } catch (Exception e) { __verLabel = "v3.0"; }
%>
<div id="appVersionBadge" title="Versao compilada em execucao"
     style="position:fixed;right:10px;bottom:8px;z-index:3000;font:11px/1 sans-serif;color:#6c757d;background:rgba(255,255,255,.88);border:1px solid #dee2e6;padding:3px 9px;border-radius:12px;box-shadow:0 1px 3px rgba(0,0,0,.15);">
  <%= __verLabel %>
</div>
