# =============================================================================
# superset_config.py — Configuração do Apache Superset 6.0 (Análises GTW-CWB)
#
# Todos os segredos vêm de variáveis de ambiente (.env).
# Não colocar valores sensíveis diretamente neste arquivo.
# =============================================================================

import os
import json
import sys
from datetime import timedelta

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
PREVENT_UNSAFE_DB_CONNECTIONS = True

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
# CSRF e sessão
# ---------------------------------------------------------------------------
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
# Mapa — OpenStreetMap via deck.gl (sem Mapbox)
# ---------------------------------------------------------------------------
MAPBOX_API_KEY = ""  # Não usado; tiles via OpenStreetMap
# =============================================================================
# Fim da configuração
# =============================================================================
