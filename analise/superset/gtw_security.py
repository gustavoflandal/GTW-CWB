# =============================================================================
# gtw_security.py — Gerenciador de segurança customizado para SSO com o GTW
#
# Lê os cabeçalhos X-GTW-Usuario, X-GTW-UserId e X-GTW-Grupos definidos
# pelo nginx (via auth_request ao endpoint SsoAnalise do GTW).
# Cria ou atualiza o usuário no Superset e sincroniza os papéis conforme
# o mapeamento GTW_GRUPOS_PAPEIS do .env.
#
# Nenhuma escrita no banco do GTW — somente leitura.
# =============================================================================

import json
import logging
import os

from flask import redirect, request, session, g
from flask_appbuilder.security.manager import AUTH_REMOTE_USER
from superset.security import SupersetSecurityManager

logger = logging.getLogger(__name__)

# Cabeçalhos definidos pelo nginx (sobrescritos; cliente não pode forjar)
_HEADER_USUARIO = "X-GTW-Usuario"
_HEADER_USER_ID = "X-GTW-UserId"
_HEADER_GRUPOS = "X-GTW-Grupos"


def _carregar_mapeamento_grupos() -> dict:
    """
    Carrega o mapeamento grupo GTW → papel Superset do ambiente.
    Formato JSON: {"id_grupo": "NomePapel", ...}
    Retorna dicionário vazio se não configurado.
    """
    raw = os.environ.get("GTW_GRUPOS_PAPEIS", "{}")
    try:
        return json.loads(raw)
    except json.JSONDecodeError:
        logger.error("GTW_GRUPOS_PAPEIS contém JSON inválido: %s", raw)
        return {}


class GtwSecurityManager(SupersetSecurityManager):
    """
    Gerenciador de segurança que autentica usuários via cabeçalhos do GTW,
    definidos pelo nginx após validação da sessão (auth_request).

    Fluxo:
      1. nginx recebe a requisição e faz auth_request ao SsoAnalise do GTW
      2. SsoAnalise valida a sessão e retorna cabeçalhos X-GTW-*
      3. nginx sobrescreve quaisquer cabeçalhos X-GTW-* do cliente
      4. Este manager lê os cabeçalhos e cria/atualiza o usuário no Superset
      5. Papéis são sincronizados a cada requisição conforme GTW_GRUPOS_PAPEIS
    """

    def __init__(self, appbuilder):
        super().__init__(appbuilder)
        self._mapeamento_grupos = _carregar_mapeamento_grupos()
        logger.info(
            "GtwSecurityManager inicializado com %d mapeamentos de grupo",
            len(self._mapeamento_grupos),
        )

    # -------------------------------------------------------------------------
    # Hook executado antes de cada requisição (Superset 6)
    # -------------------------------------------------------------------------
    def before_request(self):
        """
        Intercepta cada requisição para identificar o usuário via cabeçalhos
        do GTW. Se não houver cabeçalho válido nem sessão ativa, redireciona
        ao login do GTW.
        """
        # Rotas que não precisam de autenticação
        caminho = request.path
        rotas_livres = ("/health", "/static/", "/favicon.ico", "/nginx-health")
        if any(caminho.startswith(r) for r in rotas_livres):
            return None

        nome_usuario = request.headers.get(_HEADER_USUARIO)
        id_usuario = request.headers.get(_HEADER_USER_ID)

        if nome_usuario and id_usuario:
            # Cabeçalhos presentes — autenticar/sincronizar
            self._sincronizar_usuario(nome_usuario, id_usuario)
            return None

        # Sem cabeçalhos — verificar se já tem sessão Flask válida
        usuario = getattr(g, "user", None)
        if usuario and getattr(usuario, "is_authenticated", False):
            return None

        # Nenhuma sessão válida — redirecionar ao login do GTW
        url_base_gtw = os.environ.get("GTW_BASE_URL", "http://localhost:8080")
        url_login = (
            f"{url_base_gtw}/muralha-digital/pages/login/login.jsp"
            f"?retorno=/analise/"
        )
        logger.info(
            "Sem sessão GTW para %s — redirecionando ao login", caminho
        )
        return redirect(url_login)

    # -------------------------------------------------------------------------
    # Criação / atualização do usuário Superset
    # -------------------------------------------------------------------------
    def _sincronizar_usuario(self, nome_usuario: str, id_usuario: str):
        """
        Busca ou cria o usuário no Superset e sincroniza seus papéis
        com base nos grupos do GTW.
        """
        usuario = self.find_user(username=nome_usuario)

        if usuario is None:
            # Criar novo usuário no Superset
            papel_padrao = self.find_role("Gamma")
            usuario = self.add_user(
                username=nome_usuario,
                first_name=nome_usuario,
                last_name="",
                email=f"{nome_usuario}@gtw.local",
                role=papel_padrao,
            )
            if usuario:
                logger.info("Usuário criado no Superset: %s", nome_usuario)
            else:
                logger.error(
                    "Falha ao criar usuário no Superset: %s", nome_usuario
                )
                return

        # Sincronizar papéis a partir dos grupos do GTW
        grupos_raw = request.headers.get(_HEADER_GRUPOS, "")
        self._sincronizar_papeis(usuario, grupos_raw)

        # Definir na sessão Flask e no contexto da requisição
        from flask_login import login_user
        login_user(usuario)
        g.user = usuario

    def _sincronizar_papeis(self, usuario, grupos_csv: str):
        """
        Recebe a lista de IDs de grupo do GTW (separados por vírgula) e
        atribui os papéis correspondentes no Superset conforme o mapeamento
        em GTW_GRUPOS_PAPEIS.

        Exemplo: grupos_csv="11,42" → papéis ["Admin", "Gamma"]
        """
        if not grupos_csv:
            logger.warning(
                "Usuário %s sem grupos GTW — mantendo papéis atuais",
                usuario.username,
            )
            return

        ids_grupos = [g.strip() for g in grupos_csv.split(",") if g.strip()]
        papeis_novos = set()

        for id_grupo in ids_grupos:
            nome_papel = self._mapeamento_grupos.get(id_grupo)
            if nome_papel:
                papel = self.find_role(nome_papel)
                if papel:
                    papeis_novos.add(papel)
                else:
                    logger.warning(
                        "Papel '%s' (grupo %s) não encontrado no Superset",
                        nome_papel,
                        id_grupo,
                    )

        if papeis_novos:
            # Sempre incluir o papel Public para acesso básico
            papel_public = self.find_role("Public")
            if papel_public:
                papeis_novos.add(papel_public)

            usuario.roles = list(papeis_novos)
            self.update_user(usuario)
            logger.debug(
                "Papéis atualizados para %s: %s",
                usuario.username,
                [p.name for p in papeis_novos],
            )
        else:
            logger.warning(
                "Nenhum papel mapeado para os grupos de %s: %s",
                usuario.username,
                ids_grupos,
            )


# =============================================================================
# Fim do módulo
# =============================================================================
