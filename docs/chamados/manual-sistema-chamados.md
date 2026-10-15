# Manual do Sistema de Chamados — Muralha Digital

> Este conteúdo é publicado no sistema em `pages/chamados/manual.jsp` (botão Manual das telas de chamados); mantenha os dois iguais.

Este manual explica, por perfil, como usar o Sistema de Chamados (central de suporte técnico) da Muralha Digital: abrir e acompanhar chamados, responder, avaliar o atendimento, consultar relatórios, exportar registros e, para o administrador, configurar o sistema.

Para a arquitetura e as regras técnicas, veja [`sistema-chamados-arquitetura.md`](sistema-chamados-arquitetura.md). Para o roteiro de testes, veja [`../testes/22-sistema-chamados.md`](../testes/22-sistema-chamados.md).

---

## Sumário

1. [Visão geral](#1-visão-geral)
2. [Como chegar às telas](#2-como-chegar-às-telas)
3. [Solicitante](#3-solicitante)
4. [Gestor do contrato](#4-gestor-do-contrato)
5. [Equipe técnica](#5-equipe-técnica)
6. [Administrador](#6-administrador)
7. [Prazos de atendimento (SLA)](#7-prazos-de-atendimento-sla)
8. [Resolução de problemas](#8-resolução-de-problemas)

---

## 1. Visão geral

O sistema registra cada pedido de suporte como um **chamado** com número de protocolo único (formato `CH-AAAA-NNNNNN`, por exemplo `CH-2026-000123`), prioridade, prazos e histórico completo.

### Perfis

O que você vê e pode fazer depende do perfil que o administrador atribuiu ao seu usuário. Um usuário pode ter mais de um perfil; nesse caso, recebe a soma das permissões.

| Perfil | Quem é | O que pode fazer |
|---|---|---|
| **Solicitante** | Usuário da contratante | Abrir chamados; ver, responder, avaliar, reabrir e exportar **apenas os próprios**; cancelar os próprios enquanto estiverem "Aberto" |
| **Gestor do contrato** | Contratante | Abrir chamados; avaliar, reabrir e cancelar **os chamados que abriu**; responder em **qualquer chamado aguardando retorno**; ver **todos** os chamados; relatórios e exportação geral |
| **Equipe técnica** | Contratada | Ver todos os chamados; assumir, iniciar, aguardar, resolver e reclassificar a prioridade; cancelar enquanto o chamado estiver aberto, em atendimento ou aguardando solicitante; relatórios |
| **Administrador** | Administração do sistema | Configuração (parâmetros de SLA, perfis de acesso, prioridades, categorias, SMTP); ver todos os chamados e relatórios em **somente leitura** |

O administrador, sozinho, não atende chamados: para isso precisa também do perfil de equipe técnica.

### Estados de um chamado

| Status | Significado |
|---|---|
| Aberto | Registrado, ainda sem atendimento |
| Em atendimento | A equipe técnica está tratando |
| Aguardando solicitante | A equipe pediu uma informação; o relógio de prazo de resolução fica **pausado** |
| Resolvido | A equipe informou a solução; o solicitante deve avaliar e fechar, ou reabrir |
| Fechado | Encerrado (avaliado pelo solicitante ou fechado automaticamente) |
| Cancelado | Encerrado sem atendimento |

Toda ação sobre um chamado exige uma **observação**, que vai para o histórico (com exceção de "Assumir", em que é opcional).

---

## 2. Como chegar às telas

1. Entre no sistema e abra a tela inicial da Muralha Digital.
2. Clique na categoria **Sistema de Chamados**.
3. Dentro dela aparecem os cartões a que o seu perfil dá acesso:
   - **Chamados**: lista, abertura e acompanhamento.
   - **Relatórios**: indicadores (gestor, equipe técnica e administrador).
   - **Configuração**: somente para o administrador.

Se um cartão não aparece, o seu usuário não tem o perfil necessário; peça ao administrador.

Dentro da tela **Chamados**, os botões "Relatórios" e "Configuração" no canto superior direito também levam às outras telas, quando o seu perfil permite.

---

## 3. Solicitante

### 3.1 Abrir um chamado

1. Na tela **Chamados**, clique em **Novo chamado**.
2. Escolha a **Categoria** e depois a **Subcategoria**.
3. Se a categoria exigir equipamento (por exemplo, "Equipamento"), aparece o campo **Equipamento**, que passa a ser obrigatório. Escolha o equipamento afetado.
4. Escolha a **Prioridade**. Abaixo do campo aparecem os prazos de atendimento e de resolução dessa prioridade. A prioridade que você escolhe é uma sugestão: a equipe técnica pode reclassificá-la com justificativa.
5. Preencha o **Título** (até 150 caracteres) e a **Descrição**.
6. Se quiser, anexe arquivos (**PDF, PNG ou JPG**, até 5 por envio; o tamanho máximo por arquivo é definido pelo administrador, 10 MB por padrão).
7. Clique em **Abrir chamado**.

Ao salvar, uma janela mostra o **número de protocolo**, a **prioridade** atribuída e os **prazos previstos** de atendimento e de resolução. Guarde o protocolo. Uma confirmação com as mesmas informações é enviada ao seu e-mail cadastrado (se o seu usuário não tiver e-mail, a confirmação na tela continua valendo, mas nenhum e-mail é enviado).

Se algum anexo não puder ser gravado, o chamado inteiro não é aberto e você vê a mensagem "Não foi possível gravar os anexos; tente novamente".

### 3.2 Acompanhar

A tela **Chamados** lista os seus chamados e se atualiza sozinha a cada 30 segundos (acima da tabela aparece "Atualizado às ..."; linhas alteradas ficam destacadas por instantes).

- Use os filtros (protocolo, status, prioridade, categoria, equipamento, período "De" e "Até") e o botão de busca; o botão com "X" limpa os filtros.
- Cada linha mostra protocolo, título, categoria, equipamento, prioridade, status, responsável, abertura e duas marcas de prazo (**SLA atend.** e **SLA resol.**; veja a [seção 7](#7-prazos-de-atendimento-sla)).
- A lista tem 50 chamados por página.
- Clique numa linha para abrir o **detalhe**.

No detalhe você vê:

- os dados do chamado (protocolo, prioridade, categoria, equipamento, solicitante, responsável, abertura);
- o quadro **Prazos (24x7)**, com a data prevista, a marca de prazo e uma contagem regressiva ("Restam 3h 20min" ou "Vencido há ..."). Quando o relógio está pausado, aparece "relógio pausado";
- a **Descrição**, os **Anexos** (clique para baixar) e o **Histórico** completo, com a mudança de status, o autor, a data e o texto de cada evento.

A tela de detalhe também se atualiza a cada 30 segundos.

### 3.3 Responder

Quando o status é **Aguardando solicitante**, a equipe está esperando uma informação sua. No detalhe, clique em **Responder**, escreva a observação (obrigatória), anexe arquivos se precisar (até 5) e confirme. O chamado volta para "Em atendimento" e o relógio de resolução volta a correr.

### 3.4 Avaliar e fechar, ou reabrir

Quando o status é **Resolvido**, há duas opções:

- **Avaliar e fechar**: escolha uma nota de 1 a 5 ★, escreva um comentário (opcional) e clique em **Enviar avaliação**. O chamado fica "Fechado".
- **Reabrir**: se a solução não resolveu, clique em **Reabrir** e escreva a justificativa. O chamado volta para "Em atendimento".

Se nenhuma das duas ações for feita em alguns dias (5 por padrão, ajustável pelo administrador), o sistema **fecha o chamado automaticamente** e o detalhe mostra "Fechado automaticamente — não avaliado".

### 3.5 Cancelar

Enquanto o chamado estiver **Aberto** (ainda sem atendimento), você pode clicar em **Cancelar** e informar o motivo.

### 3.6 Exportar

Na tela **Chamados**, os botões **CSV**, **XLS** e **PDF** exportam os chamados do conjunto filtrado (no seu caso, só os seus). Os arquivos CSV e XLS trazem os filtros, a data, o usuário, o total e uma impressão digital **SHA-256** do conteúdo; o PDF é montado no navegador.

---

## 4. Gestor do contrato

O gestor abre chamados como o solicitante e **avalia, reabre e cancela os chamados que abriu**, com estas diferenças:

- Na tela **Chamados**, vê **todos** os chamados, não só os próprios, e pode filtrar e exportar o conjunto geral.
- Pode **responder** a **qualquer chamado aguardando retorno**, mesmo que não tenha sido aberto por ele.
- Tem acesso à tela **Relatórios**.

### Relatórios

1. Abra **Relatórios** (cartão da categoria ou botão na tela Chamados).
2. Informe os filtros: **De**, **Até**, **Categoria**, **Equipamento** e **Prioridade**.
3. Em **Agrupar por**, escolha Mês, Semana, Dia, Categoria, Equipamento ou Prioridade.
4. Clique em **Gerar**.

A tela mostra três totais (Chamados, Resolvidos e Cancelados), um gráfico e a tabela **Indicadores**, com, para cada grupo:

| Coluna | Significado |
|---|---|
| Total / Resolvidos / Cancelados | Quantidade de chamados |
| % SLA atend. | Percentual com o prazo de atendimento cumprido |
| % SLA resol. | Percentual com o prazo de resolução cumprido |
| TMA (min) | Tempo médio até o primeiro atendimento |
| TMR (min) | Tempo médio de resolução, descontando as pausas |
| Nota média | Média das avaliações (1 a 5) |
| % avaliados | Percentual de chamados que receberam avaliação |

Os botões **CSV**, **XLS** e **PDF** exportam a tabela de indicadores.

---

## 5. Equipe técnica

A equipe técnica enxerga todos os chamados e atende conforme o status. No detalhe, os botões disponíveis mudam com o status:

| Ação | Quando aparece | O que faz |
|---|---|---|
| **Assumir** | Chamado aberto ou em atendimento | Define você como responsável (observação opcional) |
| **Iniciar atendimento** | Chamado aberto | Passa para "Em atendimento" e registra o primeiro atendimento |
| **Aguardar solicitante** | Em atendimento | Passa para "Aguardando solicitante"; descreva o que precisa do solicitante. O relógio de resolução pausa |
| **Resolver** | Em atendimento | Passa para "Resolvido"; descreva a solução |
| **Reclassificar prioridade** | Qualquer chamado não encerrado | Muda a prioridade (com justificativa), recalcula os prazos e avisa o solicitante por e-mail |
| **Cancelar** | Aberto, em atendimento ou aguardando solicitante | Encerra o chamado; a justificativa é obrigatória |

Cada ação abre uma janela para a observação, que vai ao histórico. Se a tela avisar "Chamado alterado por outro usuário; recarregue", alguém mexeu no chamado enquanto você o olhava: recarregue a página e refaça a ação.

A equipe também usa **Relatórios** (veja a seção 4) e os botões de exportação da lista. A Configuração do sistema é exclusiva do administrador.

---

## 6. Administrador

O administrador acessa a tela **Configuração** (cartão da categoria ou botão "Configuração" na tela Chamados). Ela tem cinco abas. Todas as alterações feitas aqui são registradas no log de auditoria do sistema.

### 6.1 Parâmetros de SLA

- **Risco de SLA (% do prazo consumido)**: de 1 a 99. Acima desse percentual o chamado passa a "Em risco". Padrão: 80.
- **Fechamento automático (dias)**: de 1 a 60. Dias após a resolução em que um chamado resolvido, sem avaliação nem reabertura, é fechado automaticamente. Padrão: 5.

Clique em **Salvar parâmetros**. Os prazos por prioridade ficam na aba "Prioridades e prazos"; as horas são corridas (24x7).

### 6.2 Perfis de acesso

A aba mostra três cartões: **Solicitantes**, **Gestores do contrato** e **Equipe técnica**, cada um com a lista dos usuários que têm o perfil.

- Para **incluir** um usuário: digite pelo menos 2 letras do nome ou do login no campo "Adicionar usuário", clique no resultado e confirme.
- Para **remover**: clique na lixeira ao lado do nome e confirme.

Os administradores aparecem numa lista de **somente leitura** no fim da aba; o grupo de administradores não é alterado por aqui.

Os mesmos grupos aparecem na tela de usuários do sistema legado; incluir um usuário por lá ou por aqui tem o mesmo efeito sobre o acesso. Porém, as alterações de grupo feitas pela tela legada de usuários **não aparecem no log de auditoria do sistema de chamados**; só ficam registradas as feitas nesta aba (Perfis de acesso).

### 6.3 Prioridades e prazos

Lista as prioridades com ordem, código, nome, prazo de atendimento e de resolução (em minutos) e situação. Use **Nova prioridade** ou o botão de edição da linha; no formulário informe código (maiúsculas), nome, ordem (1 = mais grave), os dois prazos em minutos e a cor do selo. Os valores iniciais sugeridos são:

| Prioridade | Atendimento | Resolução |
|---|---|---|
| Crítica | 60 min | 240 min |
| Alta | 120 min | 480 min |
| Média | 240 min | 1440 min |
| Baixa | 480 min | 4320 min |

Prioridades e categorias não são excluídas: quando deixam de ser usadas, são **desativadas** e não aparecem mais para novos chamados, mas os chamados antigos continuam intactos.

### 6.4 Categorias

Lista categorias e subcategorias (dois níveis). Use **Nova categoria** ou a edição da linha para definir o nome, a **Categoria pai** (vazia para categoria de primeiro nível) e se a categoria **exige equipamento** (a opção vale para a categoria de primeiro nível; as subcategorias herdam). Para retirar uma categoria de uso, desative-a.

### 6.5 E-mail (SMTP)

Define o servidor usado para enviar os e-mails dos chamados (confirmação de abertura e aviso de reclassificação). No alto da aba, um aviso informa qual servidor está em uso: o configurado aqui ou o do arquivo de configuração do servidor (`confGTW.xml`).

| Campo | Observação |
|---|---|
| **Servidor SMTP** | Nome do servidor ou endereço IPv4. Em branco = usa o servidor do `confGTW.xml` |
| **Porta** | De 1 a 65535 (padrão 587) |
| **Usuário** | Em branco = sem autenticação |
| **Senha** | Em branco = mantém a senha atual. Marque **Apagar a senha gravada** para removê-la. A senha nunca é exibida |
| **E-mail do remetente** | Endereço que aparece como remetente |
| **Usar STARTTLS** | Liga a criptografia STARTTLS (obrigatória nesse caso: se o servidor não oferecer, o envio falha). Desligado = sem criptografia. Só vale para o servidor configurado nesta tela |
| **Enviar e-mails dos chamados** | Liga ou desliga todo o envio |

São suportados apenas **STARTTLS** (normalmente porta 587) ou **sem criptografia**; a conexão SSL direta (porta 465) **não é suportada**. O interruptor STARTTLS só vale para o servidor configurado nesta tela: o servidor do `confGTW.xml` segue a sua própria configuração.

A senha é gravada **cifrada** e depende de uma chave guardada no servidor (variável de ambiente `GTW_CHAMADO_CHAVE_SMTP`). Ao trocar servidor, porta ou usuário, é preciso digitar a senha novamente (a senha gravada não é reaproveitada em outro servidor). Se a chave não estiver definida, a aba mostra um aviso e não é possível gravar uma senha nova; veja [`../requisitos/07-configuracao-e-segredos.md`](../requisitos/07-configuracao-e-segredos.md).

Depois de salvar, clique em **Enviar e-mail de teste**: o sistema envia uma mensagem, com a configuração gravada, para o e-mail cadastrado no seu usuário. Se houver falha, a mensagem do servidor é mostrada (sem a senha).

---

## 7. Prazos de atendimento (SLA)

Cada chamado tem dois prazos, definidos pela prioridade: **atendimento** (até o primeiro atendimento) e **resolução**. A contagem é em **horas corridas, 24 horas por dia, 7 dias por semana**.

- O relógio de resolução **pausa** enquanto o chamado está "Aguardando solicitante" e depois de "Resolvido"; o tempo pausado é somado ao prazo.
- Ao reclassificar a prioridade, os prazos são recalculados a partir da abertura.

As marcas de prazo na lista e no detalhe:

| Marca | Cor | Significado |
|---|---|---|
| No prazo | verde | Dentro do prazo |
| Em risco | amarelo | Consumiu o percentual de risco configurado ou mais |
| Vencido | vermelho | O prazo passou e ainda não foi cumprido |
| Cumprido | cinza | Foi cumprido dentro do prazo (definitivo) |
| Violado | vermelho | Foi cumprido, mas depois do prazo (definitivo) |

O sistema atualiza as marcas a cada 5 minutos.

---

## 8. Resolução de problemas

| Sintoma | O que fazer |
|---|---|
| Não vejo a categoria "Sistema de Chamados" ou um dos cartões | O seu usuário não tem o perfil necessário. Peça ao administrador para incluí-lo em Configuração > Perfis de acesso |
| O botão "Novo chamado" não aparece | Apenas solicitantes e gestores abrem chamados |
| Mensagem "Chamado alterado por outro usuário; recarregue" | Outra pessoa (ou o sistema, ao atualizar os prazos) alterou o chamado. Recarregue a página e repita a ação |
| "Não foi possível gravar os anexos; tente novamente" | O servidor não conseguiu gravar os arquivos. Tente de novo; se persistir, avise o administrador (diretório de anexos) |
| Anexo recusado | Só são aceitos PDF, PNG e JPG, dentro do tamanho máximo, e o conteúdo do arquivo precisa corresponder ao tipo |
| Não recebi o e-mail de confirmação | Confira se o seu usuário tem e-mail cadastrado e se o SMTP está configurado e ligado (administrador: aba E-mail (SMTP) e botão de teste) |
| Na aba SMTP aparece o aviso da variável `GTW_CHAMADO_CHAVE_SMTP` | A chave de cifragem não está definida no servidor. Veja a documentação de configuração e segredos |
| O gráfico dos relatórios não aparece | A tela carrega a biblioteca de gráficos da internet; verifique o acesso do computador e recarregue |
