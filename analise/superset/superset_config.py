# =============================================================================
# superset_config.py — Configuração do Apache Superset 6.0 (Análises GTW-CWB)
# SUP-1 a SUP-12
#
# Todos os segredos vêm de variáveis de ambiente (.env).
# Não colocar valores sensíveis diretamente neste arquivo.
# =============================================================================

import os
import json
import sys
from datetime import timedelta

from flask import g

# Garante que /app esteja no path para importar gtw_security.py
if "/app" not in sys.path:
    sys.path.insert(0, "/app")

# ---------------------------------------------------------------------------
# Segurança
# ---------------------------------------------------------------------------
SECRET_KEY = os.environ["SUPERSET_SECRET_KEY"]

# ---------------------------------------------------------------------------
# Banco de metadados do Superset (PostgreSQL)
# ---------------------------------------------------------------------------
SQLALCHEMY_DATABASE_URI = os.environ["SUPERSET_DB_URI"]

# ---------------------------------------------------------------------------
# Redis — cache de dados e resultados de consulta
# ---------------------------------------------------------------------------
_REDIS_URL = os.environ.get("REDIS_URL", "redis://redis:6379/0")

DATA_CACHE_CONFIG = {
    "CACHE_TYPE": "RedisCache",
    "CACHE_DEFAULT_TIMEOUT": 60,  # 60 segundos
    "CACHE_KEY_PREFIX": "superset_dados_",
    "CACHE_REDIS_URL": _REDIS_URL,
}

FILTER_STATE_CACHE_CONFIG = {
    "CACHE_TYPE": "RedisCache",
    "CACHE_DEFAULT_TIMEOUT": 300,
    "CACHE_KEY_PREFIX": "superset_filtro_",
    "CACHE_REDIS_URL": _REDIS_URL,
}

EXPLORE_FORM_DATA_CACHE_CONFIG = {
    "CACHE_TYPE": "RedisCache",
    "CACHE_DEFAULT_TIMEOUT": 300,
    "CACHE_KEY_PREFIX": "superset_explorar_",
    "CACHE_REDIS_URL": _REDIS_URL,
}

RESULTS_BACKEND = None  # Usa o cache padrão do Redis para resultados do SQL Lab

# ---------------------------------------------------------------------------
# Cache de thumbnails (SUP-12)
# ---------------------------------------------------------------------------
THUMBNAIL_CACHE_CONFIG = {
    "CACHE_TYPE": "RedisCache",
    "CACHE_DEFAULT_TIMEOUT": 3600,
    "CACHE_KEY_PREFIX": "superset_thumb_",
    "CACHE_REDIS_URL": _REDIS_URL,
}

# ---------------------------------------------------------------------------
# Celery (alertas, relatórios agendados)
# ---------------------------------------------------------------------------
class CeleryConfig:
    broker_url = _REDIS_URL
    imports = ("superset.sql_lab", "superset.tasks.scheduler")
    result_backend = _REDIS_URL
    worker_prefetch_multiplier = 1
    task_acks_late = False
    beat_schedule = {
        "reports.scheduler": {
            "task": "reports.scheduler",
            "schedule": timedelta(minutes=1),
        },
        "reports.prune_log": {
            "task": "reports.prune_log",
            "schedule": timedelta(days=7),
        },
    }

CELERY_CONFIG = CeleryConfig

# ---------------------------------------------------------------------------
# Limites de consulta e SQL Lab
# ---------------------------------------------------------------------------
ROW_LIMIT = 10000
SQLLAB_TIMEOUT = 30  # segundos
SQL_MAX_ROW = 10000
SQLLAB_CTAS_NO_LIMIT = True
PREVENT_UNSAFE_DB_CONNECTIONS = True

# ---------------------------------------------------------------------------
# Desempenho — consultas assíncronas e pool de conexões (SUP-12)
# ---------------------------------------------------------------------------
SQLLAB_ASYNC_TIME_LIMIT_SEC = 120
SIP_15_ENABLED = True

SQLALCHEMY_ENGINE_OPTIONS = {
    "pool_size": 5,
    "max_overflow": 10,
    "pool_timeout": 30,
    "pool_recycle": 1800,
    "pool_pre_ping": True,
}

# Limitar carga de dashboards pesados
DASHBOARD_AUTO_REFRESH_INTERVALS = [
    [0, "Sem auto-refresh"],
    [60, "1 minuto"],
    [300, "5 minutos"],
    [900, "15 minutos"],
]

# Chart data — timeout e cache
SUPERSET_WEBSERVER_TIMEOUT = int(os.environ.get("SUPERSET_WEBSERVER_TIMEOUT", "60"))

# ---------------------------------------------------------------------------
# Autenticação — Remote User (SSO via cabeçalhos do nginx)
# ---------------------------------------------------------------------------
from flask_appbuilder.security.manager import AUTH_REMOTE_USER

AUTH_TYPE = AUTH_REMOTE_USER
AUTH_USER_REGISTRATION = True
AUTH_USER_REGISTRATION_ROLE = "Gamma"  # Papel padrão para novos usuários

# Gerenciador de segurança customizado que lê os cabeçalhos X-GTW-*
from gtw_security import GtwSecurityManager
CUSTOM_SECURITY_MANAGER = GtwSecurityManager

# ---------------------------------------------------------------------------
# Feature flags
# ---------------------------------------------------------------------------
FEATURE_FLAGS = {
    "DASHBOARD_RBAC": True,           # Permissão por painel
    "EMBEDDED_SUPERSET": True,        # Embedding de painéis no GTW
    "ALERT_REPORTS": True,            # Alertas e relatórios agendados
    "ENABLE_TEMPLATE_PROCESSING": True,  # Templates Jinja nos SQL
    "TAGGING_SYSTEM": True,           # Tags para organização (SUP-5)
    "ALERT_REPORT_TABS": True,        # Abas de alertas na interface (SUP-7)
}

# ---------------------------------------------------------------------------
# Row Level Security — Jinja para filtros dinâmicos (SUP-5)
# ---------------------------------------------------------------------------
JINJA_CONTEXT_ADDONS = {
    "current_username": lambda: g.user.username if hasattr(g, "user") and g.user else "anon",
}

# ---------------------------------------------------------------------------
# Formatos brasileiros (D3)
# ---------------------------------------------------------------------------
D3_FORMAT = {
    "decimal": ",",
    "thousands": ".",
    "grouping": [3],
    "currency": ["R$ ", ""],
}

D3_TIME_FORMAT = {
    "dateTime": "%A, %e de %B de %Y. %X",
    "date": "%d/%m/%Y",
    "time": "%H:%M:%S",
    "periods": ["AM", "PM"],
    "days": [
        "Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira",
        "Quinta-feira", "Sexta-feira", "Sábado",
    ],
    "shortDays": ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"],
    "months": [
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
    ],
    "shortMonths": [
        "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
        "Jul", "Ago", "Set", "Out", "Nov", "Dez",
    ],
}

# ---------------------------------------------------------------------------
# Identidade visual e idioma
# ---------------------------------------------------------------------------
APP_NAME = "Análises — Muralha Digital"

LANGUAGES = {
    "pt_BR": {"flag": "br", "name": "Português (Brasil)"},
    "en": {"flag": "us", "name": "English"},
}
BABEL_DEFAULT_LOCALE = "pt_BR"

# ---------------------------------------------------------------------------
# Guest token (embedding de painéis em telas do GTW)
# ---------------------------------------------------------------------------
GUEST_ROLE_NAME = "Gamma"
GUEST_TOKEN_JWT_SECRET = os.environ.get("SUPERSET_SECRET_KEY", SECRET_KEY)
GUEST_TOKEN_JWT_ALGO = "HS256"
GUEST_TOKEN_HEADER_NAME = "X-GuestToken"
GUEST_TOKEN_JWT_EXP_SECONDS = 3600  # 1 hora

# ---------------------------------------------------------------------------
# Auditoria e observabilidade (SUP-6)
# ---------------------------------------------------------------------------

# Log de ações — registra quem acessou qual dashboard/gráfico
FAB_LOG_LEVEL = os.environ.get("SUPERSET_LOG_LEVEL", "INFO")
LOG_FORMAT = "%(asctime)s [%(levelname)s] %(name)s — %(message)s"
LOG_LEVEL = FAB_LOG_LEVEL

# StatsD — métricas de performance (opcional, via .env)
_STATSD_HOST = os.environ.get("STATSD_HOST", "")
if _STATSD_HOST:
    from superset.stats_logger import StatsdStatsLogger
    STATS_LOGGER = StatsdStatsLogger(
        host=_STATSD_HOST,
        port=int(os.environ.get("STATSD_PORT", "8125")),
        prefix="superset",
    )

# Retenção do action log (dias)
SUPERSET_LOG_VIEW = True

# Consultas SQL — log de execução (o Superset exige uma função, não um booleano)
def QUERY_LOGGER(database, query, schema=None, client=None, security_manager=None, log_params=None):
    import logging as _logging
    usuario = getattr(getattr(g, "user", None), "username", "sistema")
    _logging.getLogger("superset.query_log").info(
        "consulta usuario=%s banco=%s schema=%s tamanho=%d",
        usuario, getattr(database, "database_name", "?"), schema, len(query or ""),
    )

# ---------------------------------------------------------------------------
# CSRF, reCAPTCHA e sessão
# ---------------------------------------------------------------------------
RECAPTCHA_PUBLIC_KEY = ""
RECAPTCHA_PRIVATE_KEY = ""
WTF_CSRF_ENABLED = True
WTF_CSRF_EXEMPT_LIST = [
    "superset.views.core.log",
    "superset.charts.api",
]
SESSION_COOKIE_HTTPONLY = True
SESSION_COOKIE_SAMESITE = "Lax"

# ---------------------------------------------------------------------------
# Proxy fix (nginx na frente)
# ---------------------------------------------------------------------------
ENABLE_PROXY_FIX = True
PROXY_FIX_CONFIG = {
    "x_for": 1,
    "x_proto": 1,
    "x_host": 1,
    "x_port": 1,
    "x_prefix": 1,
}

# ---------------------------------------------------------------------------
# SMTP — alertas e relatórios (valores do .env)
# ---------------------------------------------------------------------------
SMTP_HOST = os.environ.get("SMTP_HOST", "localhost")
SMTP_PORT = int(os.environ.get("SMTP_PORT", "587"))
SMTP_USER = os.environ.get("SMTP_USER", "")
SMTP_PASSWORD = os.environ.get("SMTP_PASSWORD", "")
SMTP_STARTTLS = os.environ.get("SMTP_STARTTLS", "true").lower() == "true"
SMTP_SSL = os.environ.get("SMTP_SSL", "false").lower() == "true"
SMTP_MAIL_FROM = os.environ.get("SMTP_MAIL_FROM", "analises@muralhadigital.local")

# ---------------------------------------------------------------------------
# Mapa — OpenStreetMap via deck.gl (SUP-8)
# ---------------------------------------------------------------------------
MAPBOX_API_KEY = ""  # Não usado; tiles via OpenStreetMap

DEFAULT_MAP_CENTER = [-12.9714, -38.5124]  # Salvador, BA
DEFAULT_MAP_ZOOM = 12

DECK_GL_DOT_SIZE = 300
HTML_SANITIZATION = True

# ---------------------------------------------------------------------------
# Embedding — CORS para iframes do GTW (SUP-9)
# ---------------------------------------------------------------------------
TALISMAN_ENABLED = False
HTTP_HEADERS = {
    "X-Frame-Options": "ALLOWALL",
}

CORS_OPTIONS = {
    "supports_credentials": True,
    "allow_headers": ["*"],
    "resources": ["/api/*", "/superset/guest_token/"],
    "origins": [
        os.environ.get("GTW_BASE_URL", "http://localhost:8080"),
    ],
}

GUEST_TOKEN_JWT_EXP_SECONDS = 3600  # 1 hora (redundante, mantido por clareza)
# =============================================================================
# Fim da configuração
# =============================================================================
