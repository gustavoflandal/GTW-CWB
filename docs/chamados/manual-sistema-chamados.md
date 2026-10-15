# Manual do Sistema de Chamados

> Este conteúdo é publicado no sistema em `pages/chamados/manual.jsp` (botão Manual das telas de chamados); mantenha os dois iguais.

Guia completo para abrir, acompanhar e atender chamados de suporte técnico no sistema Muralha Digital.

---

## 1. Visão Geral

O **Sistema de Chamados** é a central de suporte técnico da Muralha Digital. Cada pedido de suporte vira um **chamado** com número de protocolo único, prioridade, prazos e histórico completo. O ciclo de um chamado segue este fluxo:

**Abertura** (solicitante registra o pedido) → **Atendimento** (equipe técnica trata) → **Aguardando solicitante** (se faltar informação) → **Resolução** (solução informada) → **Avaliação** (fechar ou reabrir)

| Conceito | O que é | Quem |
|---|---|---|
| **Chamado** | Pedido de suporte registrado no sistema, com histórico completo de tudo o que acontece com ele | Solicitante ou Gestor do contrato abre |
| **Protocolo** | Número único do chamado, no formato `CH-AAAA-NNNNNN` (por exemplo, `CH-2026-000123`) | Gerado pelo sistema |
| **Prioridade** | Grau de urgência (Crítica, Alta, Média, Baixa). A escolhida na abertura é uma sugestão: a equipe técnica pode reclassificá-la com justificativa | Solicitante sugere; Equipe técnica reclassifica |
| **SLA** | Prazos de atendimento e de resolução definidos pela prioridade, contados em horas corridas (24x7) | Administrador configura |
| **Categoria** | Classificação do assunto do chamado, em dois níveis (categoria e subcategoria); algumas exigem informar o equipamento | Administrador cadastra |
| **Avaliação** | Nota de 1 a 5 e comentário sobre o atendimento, dados ao fechar o chamado resolvido | Solicitante ou Gestor do contrato |

**Estados de um chamado:**

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

## 2. Acesso ao Sistema

### 2.1 Perfis necessários

O que você vê e pode fazer depende do perfil que o administrador atribuiu ao seu usuário. Um usuário pode ter mais de um perfil; nesse caso, recebe a soma das permissões.

| Perfil | Quem é | Permissão |
|---|---|---|
| **Chamados - Solicitante** | Usuário da contratante | Abrir chamados; ver, responder, avaliar, reabrir e exportar **apenas os próprios**; cancelar os próprios enquanto estiverem "Aberto" |
| **Chamados - Gestor do contrato** | Contratante | Abrir chamados; avaliar, reabrir e cancelar **os chamados que abriu**; responder em **qualquer chamado aguardando retorno**; ver **todos** os chamados; relatórios e exportação geral |
| **Chamados - Equipe tecnica** | Contratada | Ver todos os chamados; assumir, iniciar, aguardar, resolver e reclassificar a prioridade; cancelar enquanto o chamado estiver aberto, em atendimento ou aguardando solicitante; relatórios |
| **Administradores** | Administração do sistema | Configuração (parâmetros de SLA, perfis de acesso, prioridades, categorias, SMTP); ver todos os chamados e relatórios em **somente leitura** |

> Se você não vê os menus ou cartões descritos abaixo, solicite ao administrador que adicione seu usuário ao grupo apropriado. O administrador, sozinho, não atende chamados: para isso precisa também do perfil de equipe técnica.

### 2.2 Navegação pelo menu

Entre no sistema, abra a tela inicial da Muralha Digital e clique na categoria **Sistema de Chamados**. Dentro dela aparecem os cartões a que o seu perfil dá acesso:

- **Chamados** — lista, abertura e acompanhamento
- **Relatórios** — indicadores (gestor, equipe técnica e administrador)
- **Configuração** — somente para o administrador

Dentro da tela **Chamados**, os botões "Relatórios" e "Configuração" no canto superior direito também levam às outras telas, quando o seu perfil permite. Os botões **Manual**, presentes nas telas do sistema, abrem este guia.

---

## 3. Abrir um Chamado `Solicitante / Gestor`

> **Quem faz:** o solicitante e o gestor do contrato. A equipe técnica e o administrador não abrem chamados.

### 3.1 Preencher e enviar

1. Na tela **Chamados**, clique em **Novo chamado**.
2. Escolha a **Categoria** e depois a **Subcategoria**.
3. Se a categoria exigir equipamento (por exemplo, "Equipamento"), aparece o campo **Equipamento**, que passa a ser obrigatório. Escolha o equipamento afetado.
4. Escolha a **Prioridade**. Abaixo do campo aparecem os prazos de atendimento e de resolução dessa prioridade. A prioridade que você escolhe é uma sugestão: a equipe técnica pode reclassificá-la com justificativa.
5. Preencha o **Título** (até 150 caracteres) e a **Descrição**.
6. Se quiser, anexe arquivos (**PDF, PNG ou JPG**, até 5 por envio; o tamanho máximo por arquivo é definido pelo administrador, 10 MB por padrão).
7. Clique em **Abrir chamado**.

### 3.2 O que acontece ao salvar

Uma janela mostra o **número de protocolo**, a **prioridade** atribuída e os **prazos previstos** de atendimento e de resolução. Guarde o protocolo. Uma confirmação com as mesmas informações é enviada ao seu e-mail cadastrado (se o seu usuário não tiver e-mail, a confirmação na tela continua valendo, mas nenhum e-mail é enviado).

Se algum anexo não puder ser gravado, o chamado inteiro não é aberto e você vê a mensagem "Não foi possível gravar os anexos; tente novamente".

---

## 4. Acompanhar e Responder `Solicitante / Gestor`

> **Quem faz:** o solicitante vê apenas os próprios chamados. O gestor do contrato vê **todos** os chamados, pode filtrar e exportar o conjunto geral e pode **responder** a **qualquer chamado aguardando retorno**, mesmo que não tenha sido aberto por ele.

### 4.1 Acompanhar

A tela **Chamados** lista os chamados e se atualiza sozinha a cada 30 segundos (acima da tabela aparece "Atualizado às ..."; linhas alteradas ficam destacadas por instantes).

1. Use os filtros (protocolo, status, prioridade, categoria, equipamento, período "De" e "Até") e o botão de busca; o botão com "X" limpa os filtros.
2. Cada linha mostra protocolo, título, categoria, equipamento, prioridade, status, responsável, abertura e duas marcas de prazo (**SLA atend.** e **SLA resol.**; veja a [seção 9](#9-prazos-de-atendimento-sla)). A lista tem 50 chamados por página.
3. Clique numa linha para abrir o **detalhe**.

No detalhe você vê:

- os dados do chamado (protocolo, prioridade, categoria, equipamento, solicitante, responsável, abertura);
- o quadro **Prazos (24x7)**, com a data prevista, a marca de prazo e uma contagem regressiva ("Restam 3h 20min" ou "Vencido há ..."). Quando o relógio está pausado, aparece "relógio pausado";
- a **Descrição**, os **Anexos** (clique para baixar) e o **Histórico** completo, com a mudança de status, o autor, a data e o texto de cada evento.

A tela de detalhe também se atualiza a cada 30 segundos.

### 4.2 Responder

Quando o status é **Aguardando solicitante**, a equipe está esperando uma informação sua.

1. No detalhe, clique em **Responder**.
2. Escreva a observação (obrigatória) e anexe arquivos se precisar (até 5).
3. Confirme. O chamado volta para "Em atendimento" e o relógio de resolução volta a correr.

### 4.3 Cancelar

Enquanto o chamado estiver **Aberto** (ainda sem atendimento), você pode clicar em **Cancelar** e informar o motivo. O solicitante cancela apenas os próprios chamados; o gestor, os que abriu.

---

## 5. Avaliar o Atendimento `Solicitante / Gestor`

> **Quem faz:** quem abriu o chamado. Quando o status é **Resolvido**, há duas opções: avaliar e fechar, ou reabrir.

### 5.1 Avaliar e fechar

1. No detalhe do chamado resolvido, escolha uma nota de 1 a 5 ★.
2. Escreva um comentário (opcional).
3. Clique em **Enviar avaliação**. O chamado fica "Fechado".

### 5.2 Reabrir

Se a solução não resolveu, clique em **Reabrir** e escreva a justificativa. O chamado volta para "Em atendimento".

### 5.3 Fechamento automático

Se nenhuma das duas ações for feita em alguns dias (5 por padrão, ajustável pelo administrador), o sistema **fecha o chamado automaticamente** e o detalhe mostra "Fechado automaticamente — não avaliado".

---

## 6. Atender Chamados `Equipe técnica`

> **Quem faz:** a equipe técnica, que enxerga todos os chamados e atende conforme o status. Para atender, o administrador também precisa ter o perfil de equipe técnica.

No detalhe, os botões disponíveis mudam com o status:

| Ação | Quando aparece | O que faz |
|---|---|---|
| **Assumir** | Chamado aberto ou em atendimento | Define você como responsável (observação opcional) |
| **Iniciar atendimento** | Chamado aberto | Passa para "Em atendimento" e registra o primeiro atendimento |
| **Aguardar solicitante** | Em atendimento | Passa para "Aguardando solicitante"; descreva o que precisa do solicitante. O relógio de resolução pausa |
| **Resolver** | Em atendimento | Passa para "Resolvido"; descreva a solução |
| **Reclassificar prioridade** | Qualquer chamado não encerrado | Muda a prioridade (com justificativa), recalcula os prazos e avisa o solicitante por e-mail |
| **Cancelar** | Aberto, em atendimento ou aguardando solicitante | Encerra o chamado; a justificativa é obrigatória |

1. Abra o chamado na lista e clique na ação desejada.
2. Na janela que abre, escreva a observação, que vai para o histórico.
3. Confirme. Se a tela avisar "Chamado alterado por outro usuário; recarregue", alguém mexeu no chamado enquanto você o olhava: recarregue a página e refaça a ação.

A equipe também usa **Relatórios** (veja a [seção 7](#7-relatórios-e-exportação)) e os botões de exportação da lista. A Configuração do sistema é exclusiva do administrador.

---

## 7. Relatórios e Exportação `Todos os perfis`

> **Quem faz:** a exportação da lista está disponível a todos os perfis (o solicitante exporta só os próprios chamados). A tela **Relatórios** é do gestor do contrato, da equipe técnica e do administrador.

### 7.1 Exportar a lista de chamados

Na tela **Chamados**, os botões **CSV**, **XLS** e **PDF** exportam os chamados do conjunto filtrado. Os arquivos CSV e XLS trazem os filtros, a data, o usuário, o total e uma impressão digital **SHA-256** do conteúdo; o PDF é montado no navegador.

### 7.2 Gerar relatórios

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

Os botões **CSV**, **XLS** e **PDF** da tela exportam a tabela de indicadores.

---

## 8. Configuração `Admin`

> **Quem faz:** apenas o administrador, na tela **Configuração** (cartão da categoria ou botão "Configuração" na tela Chamados). Ela tem cinco abas. Todas as alterações feitas aqui são registradas no log de auditoria do sistema.

### 8.1 Parâmetros de SLA

- **Risco de SLA (% do prazo consumido)**: de 1 a 99. Acima desse percentual o chamado passa a "Em risco". Padrão: 80.
- **Fechamento automático (dias)**: de 1 a 60. Dias após a resolução em que um chamado resolvido, sem avaliação nem reabertura, é fechado automaticamente. Padrão: 5.

Clique em **Salvar parâmetros**. Os prazos por prioridade ficam na aba "Prioridades e prazos"; as horas são corridas (24x7).

### 8.2 Perfis de acesso

A aba mostra três cartões: **Solicitantes**, **Gestores do contrato** e **Equipe técnica**, cada um com a lista dos usuários que têm o perfil.

1. Para **incluir** um usuário: digite pelo menos 2 letras do nome ou do login no campo "Adicionar usuário", clique no resultado e confirme.
2. Para **remover**: clique na lixeira ao lado do nome e confirme.

O grupo de administradores não aparece nem é alterado por aqui; ele é mantido na tela de usuários do sistema.

> Os mesmos grupos aparecem na tela de usuários do sistema legado; incluir um usuário por lá ou por aqui tem o mesmo efeito sobre o acesso. Porém, as alterações de grupo feitas pela tela legada de usuários **não aparecem no log de auditoria do sistema de chamados**; só ficam registradas as feitas nesta aba (Perfis de acesso).

### 8.3 Prioridades e prazos

Lista as prioridades com ordem, código, nome, prazo de atendimento e de resolução (em minutos) e situação. Use **Nova prioridade** ou o botão de edição da linha; no formulário informe código (maiúsculas), nome, ordem (1 = mais grave), os dois prazos em minutos e a cor do selo. Os valores iniciais sugeridos são:

| Prioridade | Atendimento | Resolução |
|---|---|---|
| Crítica | 60 min | 240 min |
| Alta | 120 min | 480 min |
| Média | 240 min | 1440 min |
| Baixa | 480 min | 4320 min |

Prioridades e categorias não são excluídas: quando deixam de ser usadas, são **desativadas** e não aparecem mais para novos chamados, mas os chamados antigos continuam intactos.

### 8.4 Categorias

Lista categorias e subcategorias (dois níveis). Use **Nova categoria** ou a edição da linha para definir o nome, a **Categoria pai** (vazia para categoria de primeiro nível) e se a categoria **exige equipamento** (a opção vale para a categoria de primeiro nível; as subcategorias herdam). Para retirar uma categoria de uso, desative-a.

### 8.5 E-mail (SMTP)

Define o servidor usado para enviar os e-mails dos chamados (confirmação de abertura e aviso de reclassificação). No alto da aba, um aviso informa qual servidor está em uso: o configurado aqui ou o da configuração geral do servidor do sistema.

| Campo | Observação |
|---|---|
| **Servidor SMTP** | Nome do servidor ou endereço IPv4. Em branco = usa o servidor da configuração geral do sistema |
| **Porta** | De 1 a 65535 (padrão 587) |
| **Usuário** | Em branco = sem autenticação |
| **Senha** | Em branco = mantém a senha atual. Marque **Apagar a senha gravada** para removê-la. A senha nunca é exibida |
| **E-mail do remetente** | Endereço que aparece como remetente |
| **Usar STARTTLS** | Liga a criptografia STARTTLS (obrigatória nesse caso: se o servidor não oferecer, o envio falha). Desligado = sem criptografia. Só vale para o servidor configurado nesta tela |
| **Enviar e-mails dos chamados** | Liga ou desliga todo o envio |

> São suportados apenas **STARTTLS** (normalmente porta 587) ou **sem criptografia**; a conexão SSL direta (porta 465) **não é suportada**. O interruptor STARTTLS só vale para o servidor configurado nesta tela: o servidor da configuração geral segue a sua própria configuração.

A senha é gravada **cifrada** e depende de uma chave de cifragem guardada no servidor, configurada pela equipe de infraestrutura. Ao trocar servidor, porta ou usuário, é preciso digitar a senha novamente (a senha gravada não é reaproveitada em outro servidor). Se a chave não estiver definida, a aba mostra um aviso e não é possível gravar uma senha nova.

1. Preencha os campos e salve.
2. Clique em **Enviar e-mail de teste**: o sistema envia uma mensagem, com a configuração gravada, para o e-mail cadastrado no seu usuário. Se houver falha, a mensagem do servidor é mostrada (sem a senha).

---

## 9. Prazos de Atendimento (SLA)

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

## 10. Resolução de Problemas

**Não vejo a categoria "Sistema de Chamados" ou um dos cartões**
Solução: o seu usuário não tem o perfil necessário. Peça ao administrador para incluí-lo em Configuração > Perfis de acesso.

**O botão "Novo chamado" não aparece**
Solução: apenas solicitantes e gestores abrem chamados.

**"Chamado alterado por outro usuário; recarregue"**
Causa: outra pessoa (ou o sistema, ao atualizar os prazos) alterou o chamado. Solução: recarregue a página e repita a ação.

**"Não foi possível gravar os anexos; tente novamente"**
Causa: o servidor não conseguiu gravar os arquivos. Solução: tente de novo; se persistir, avise o administrador (diretório de anexos).

**Anexo recusado**
Solução: só são aceitos PDF, PNG e JPG, dentro do tamanho máximo, e o conteúdo do arquivo precisa corresponder ao tipo.

**Não recebi o e-mail de confirmação**
Solução: confira se o seu usuário tem e-mail cadastrado e se o SMTP está configurado e ligado (administrador: aba E-mail (SMTP) e botão de teste).

**Na aba SMTP aparece o aviso sobre a chave de cifragem**
Solução: a chave de cifragem não está definida no servidor. Peça à equipe de infraestrutura para configurá-la.

**O gráfico dos relatórios não aparece**
Solução: a tela carrega a biblioteca de gráficos da internet; verifique o acesso do computador e recarregue.

---

## 11. Glossário

| Termo | Definição |
|---|---|
| **Avaliação** | Nota de 1 a 5 e comentário opcional dados ao fechar um chamado resolvido |
| **Categoria** | Classificação do assunto do chamado, com subcategoria; pode exigir equipamento |
| **Chamado** | Pedido de suporte técnico registrado no sistema, com histórico completo |
| **Fechamento automático** | Fechamento feito pelo sistema quando um chamado resolvido não é avaliado nem reaberto no prazo configurado |
| **Prioridade** | Grau de urgência do chamado (Crítica, Alta, Média, Baixa); define os prazos de SLA |
| **Protocolo** | Número único do chamado, no formato CH-AAAA-NNNNNN |
| **Reclassificar** | Mudar a prioridade de um chamado, com justificativa, recalculando os prazos |
| **SLA** | Acordo de nível de serviço — prazos de atendimento e de resolução, em horas corridas (24x7) |
| **SMTP** | Servidor usado para enviar os e-mails do sistema |
| **STARTTLS** | Criptografia da conexão com o servidor de e-mail, ligada na tela de Configuração |
| **TMA** | Tempo médio de atendimento — até o primeiro atendimento |
| **TMR** | Tempo médio de resolução, descontando as pausas |

---

*Manual do Sistema de Chamados — Muralha Digital*
