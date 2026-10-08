# Catálogo de objetos programáveis

O código-fonte completo de cada objeto está em [codigo-sql/](codigo-sql/). Para cada objeto: parâmetros e objetos referenciados (tabelas/views/outros) extraídos de `sys.sql_expression_dependencies`. Tipos entre colchetes: U=tabela, V=view, P=procedure, FN/IF/TF=função, TR=trigger.

## Views — 123

### dbo.agenda_camera_vigente
- Arquivo: `codigo-sql/views/dbo.agenda_camera_vigente.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.agenda_camera [U]

### dbo.cad_isento_pesquisa
- Arquivo: `codigo-sql/views/dbo.cad_isento_pesquisa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_isento [U], dbo.cad_isento_ant [U]

### dbo.cad_isento_vigente
- Arquivo: `codigo-sql/views/dbo.cad_isento_vigente.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_arquivos_importados [U], dbo.cad_isento [U], dbo.cad_isento_arquivo [U]

### dbo.cad_veiculo_aux
- Arquivo: `codigo-sql/views/dbo.cad_veiculo_aux.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_veiculo_aux_tab [U]

### dbo.cadastro_veiculo
- Arquivo: `codigo-sql/views/dbo.cadastro_veiculo.sql` · criado 2025-06-02 · alterado 2025-09-04
- Parâmetros/retorno: —
- Referencia: dbo.cad_categoria [U], dbo.cad_cor [U], dbo.cad_especie [U], dbo.cad_especie_processo [U], dbo.cad_localidade [U], dbo.cad_marca [U], dbo.cad_marca_cet [U], dbo.cad_marca_cet_processo [U], dbo.cad_situacao [U], dbo.cad_tipo [U], dbo.cad_tipo_cet [U], dbo.cad_uf_processo [U], dbo.cad_veiculo [U], dbo.cad_veiculo_info_aux [U]

### dbo.configuracao_equipamento_pendente_importacao
- Arquivo: `codigo-sql/views/dbo.configuracao_equipamento_pendente_importacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_importacao [U], dbo.local [U], dbo.local_vigente [V]

### dbo.configuracao_equipamento_pendente_importacao_novo
- Arquivo: `codigo-sql/views/dbo.configuracao_equipamento_pendente_importacao_novo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.configuracao_equipamento_data_modificacao [U], dbo.configuracao_equipamento_importacao [U], dbo.veiculo_importacao [U]

### dbo.configuracao_equipamento_regra_infracao_temp
- Arquivo: `codigo-sql/views/dbo.configuracao_equipamento_regra_infracao_temp.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento_regra_infracao [U], dbo.local_pista_vigente_temp [V]

### dbo.descarga_cad_veiculo
- Arquivo: `codigo-sql/views/dbo.descarga_cad_veiculo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_especie [U], dbo.cad_especie_processo [U], dbo.cad_marca [U], dbo.cad_marca_cet [U], dbo.cad_marca_cet_processo [U], dbo.cad_veiculo [U], dbo.infracao [U], dbo.veiculo_descarga [U]

### dbo.descarga_classe_veiculo
- Arquivo: `codigo-sql/views/dbo.descarga_classe_veiculo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.classe_veiculo [U], dbo.veiculo [U], dbo.veiculo_descarga [U]

### dbo.descarga_descarga
- Arquivo: `codigo-sql/views/dbo.descarga_descarga.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.chave_valor [U], dbo.descarga [U]

### dbo.descarga_enquadramento
- Arquivo: `codigo-sql/views/dbo.descarga_enquadramento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.enquadramento [U], dbo.infracao [U], dbo.veiculo_descarga [U]

### dbo.descarga_imagem
- Arquivo: `codigo-sql/views/dbo.descarga_imagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.imagem [U], dbo.imagem_info [U], dbo.local [U], dbo.tipo_imagem [U], dbo.veiculo [U], dbo.veiculo_descarga [U], dbo.veiculo_imagem [U]

### dbo.descarga_inconsistencia
- Arquivo: `codigo-sql/views/dbo.descarga_inconsistencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.veiculo_descarga [U]

### dbo.descarga_infracao
- Arquivo: `codigo-sql/views/dbo.descarga_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.veiculo_descarga [U]

### dbo.descarga_obliteracao
- Arquivo: `codigo-sql/views/dbo.descarga_obliteracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_obliteracao [U], dbo.veiculo_descarga [U]

### dbo.descarga_pista
- Arquivo: `codigo-sql/views/dbo.descarga_pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.local [U], dbo.veiculo [U], dbo.veiculo_descarga [U]

### dbo.descarga_usuario
- Arquivo: `codigo-sql/views/dbo.descarga_usuario.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.sis_usuario [U], dbo.veiculo_descarga [U]

### dbo.descarga_veiculo
- Arquivo: `codigo-sql/views/dbo.descarga_veiculo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local [U], dbo.veiculo [U], dbo.veiculo_descarga [U], dbo.veiculo_imagem [U]

### dbo.equipamento_regras
- Arquivo: `codigo-sql/views/dbo.equipamento_regras.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.local [U]

### dbo.eventos_csx_CAV
- Arquivo: `codigo-sql/views/dbo.eventos_csx_CAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_categoria_x_evento [U], dbo.fcn_getEventosCsxDescProprietarioCAV [IF]

### dbo.eventos_csx_pesquisa
- Arquivo: `codigo-sql/views/dbo.eventos_csx_pesquisa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_categoria [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_nivel [U], dbo.eventos_csx_desc_prioridade [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.eventos_csx_pesquisa_CAV
- Arquivo: `codigo-sql/views/dbo.eventos_csx_pesquisa_CAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_CAV [V], dbo.eventos_csx_desc_categoria [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_nivel [U], dbo.eventos_csx_desc_prioridade [U], dbo.eventos_csx_legacy [U], dbo.fcn_getEventosCsxDescCategoriaCAV [IF], dbo.fcn_getEventosCsxDescEventoCAV [IF], dbo.fcn_getEventosCsxDescNivelCAV [IF], dbo.fcn_getEventosCsxDescPrioridadeCAV [IF], dbo.fcn_getEventosCsxDescProprietarioCAV [IF]

### dbo.gerencia_contrato_painel
- Arquivo: `codigo-sql/views/dbo.gerencia_contrato_painel.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.gerencia_contrato_alerta [U], dbo.gerencia_contrato_cad_alerta [U], dbo.local_vigente [V]

### dbo.grupo_hierarquia
- Arquivo: `codigo-sql/views/dbo.grupo_hierarquia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.sis_grupo [U]

### dbo.infracao_amostra
- Arquivo: `codigo-sql/views/dbo.infracao_amostra.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_parametros_adicionais [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.local [U], dbo.veiculo [U]

### dbo.infracao_completa
- Arquivo: `codigo-sql/views/dbo.infracao_completa.sql` · criado 2025-06-02 · alterado 2025-10-21
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_processo_usuario [V], dbo.infracao_remessa [U], dbo.local [U], dbo.movimento_importacao [U], dbo.processo [U], dbo.remessa [U], dbo.sis_usuario [U], dbo.veiculo [U], dbo.veiculo_pesagem [U]

### dbo.infracao_entre_faixa
- Arquivo: `codigo-sql/views/dbo.infracao_entre_faixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.veiculo [U]

### dbo.infracao_processo_finalizada
- Arquivo: `codigo-sql/views/dbo.infracao_processo_finalizada.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.processo [U]

### dbo.infracao_processo_usuario
- Arquivo: `codigo-sql/views/dbo.infracao_processo_usuario.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao_processo [U]

### dbo.infracao_sumarizado
- Arquivo: `codigo-sql/views/dbo.infracao_sumarizado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.veiculo [U]

### dbo.local_mediavelocidade_totaltrafego
- Arquivo: `codigo-sql/views/dbo.local_mediavelocidade_totaltrafego.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.veiculo [U]

### dbo.local_parametro_adicional_data_search
- Arquivo: `codigo-sql/views/dbo.local_parametro_adicional_data_search.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_parametros_adicionais [U], dbo.local_vigente [V]

### dbo.local_pista_vigente
- Arquivo: `codigo-sql/views/dbo.local_pista_vigente.sql` · criado 2025-06-02 · alterado 2025-09-25
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.local [U], dbo.local_municipio_regiao [U], dbo.sis_usuario [U]

### dbo.local_pista_vigente_temp
- Arquivo: `codigo-sql/views/dbo.local_pista_vigente_temp.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.local [U], dbo.sis_usuario [U]

### dbo.local_regra_infracao
- Arquivo: `codigo-sql/views/dbo.local_regra_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.local [U]

### dbo.local_regra_infracao_vigente
- Arquivo: `codigo-sql/views/dbo.local_regra_infracao_vigente.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_regra_infracao [V], dbo.local_vigente [V]

### dbo.local_status
- Arquivo: `codigo-sql/views/dbo.local_status.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_status_conexao [V], dbo.local_status_div [V], dbo.local_status_energia [V], dbo.local_vigente [V], dbo.MAX_DATE [FN]

### dbo.local_status_conexao
- Arquivo: `codigo-sql/views/dbo.local_status_conexao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.status_conexao [U]

### dbo.local_status_div
- Arquivo: `codigo-sql/views/dbo.local_status_div.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.status_DIV [U]

### dbo.local_status_energia
- Arquivo: `codigo-sql/views/dbo.local_status_energia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.status_energia [U]

### dbo.local_status_tempo_real
- Arquivo: `codigo-sql/views/dbo.local_status_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.local_status [V], dbo.local_vigente [V], dbo.MAX_DATE [FN], dbo.status_tempo_real [U], dbo.TEMPO_DECORRIDO [FN], dbo.veiculo_monitorado [U]

### dbo.local_vigente
- Arquivo: `codigo-sql/views/dbo.local_vigente.sql` · criado 2025-06-02 · alterado 2025-07-07
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.local [U], dbo.local_municipio_regiao [U], dbo.sis_usuario [U]

### dbo.local_vigente_antigo
- Arquivo: `codigo-sql/views/dbo.local_vigente_antigo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.local [U], dbo.local_vigente [V], dbo.sis_usuario [U]

### dbo.painel_contrato_vigente_se
- Arquivo: `codigo-sql/views/dbo.painel_contrato_vigente_se.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.painel_contrato [U], dbo.painel_contrato_alerta [U]

### dbo.painel_contrato_vigente_se_teste
- Arquivo: `codigo-sql/views/dbo.painel_contrato_vigente_se_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.painel_contrato [U], dbo.painel_contrato_alerta [U]

### dbo.painel_imagens_defeituosas
- Arquivo: `codigo-sql/views/dbo.painel_imagens_defeituosas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.painel_local_desconectado
- Arquivo: `codigo-sql/views/dbo.painel_local_desconectado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_status_conexao [V]

### dbo.painel_ultima_camera_carregada
- Arquivo: `codigo-sql/views/dbo.painel_ultima_camera_carregada.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.painel_ultima_infracao
- Arquivo: `codigo-sql/views/dbo.painel_ultima_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_importacao [U], dbo.local_vigente [V], dbo.veiculo_importacao [U]

### dbo.painel_ultimo_arquivo
- Arquivo: `codigo-sql/views/dbo.painel_ultimo_arquivo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U]

### dbo.pmesp_movimento
- Arquivo: `codigo-sql/views/dbo.pmesp_movimento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.pmesp_movimento_2016 [U], dbo.pmesp_movimento_2017 [U]

### dbo.relatorio_amostras
- Arquivo: `codigo-sql/views/dbo.relatorio_amostras.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.relatorio_aproveitamento_semanal
- Arquivo: `codigo-sql/views/dbo.relatorio_aproveitamento_semanal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.processo [U]

### dbo.Relatorio_DataInfracao_DataProcesso_FinalPlaca
- Arquivo: `codigo-sql/views/dbo.Relatorio_DataInfracao_DataProcesso_FinalPlaca.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_processo [U]

### dbo.RelatorioMediaVelocidadeHora
- Arquivo: `codigo-sql/views/dbo.RelatorioMediaVelocidadeHora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.Relatorios_MediaVelocidade_Hora [U]

### dbo.RelatoriosPTPNT
- Arquivo: `codigo-sql/views/dbo.RelatoriosPTPNT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.LOCAL [U], dbo.Relatorios_PT_PNT [U]

### dbo.RelatorioVolumeHora
- Arquivo: `codigo-sql/views/dbo.RelatorioVolumeHora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.Relatorios_Volume_Hora_PMG [U]

### dbo.RelatorioVolumeHoraPistaPMG
- Arquivo: `codigo-sql/views/dbo.RelatorioVolumeHoraPistaPMG.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.Relatorios_Volume_Hora_PMG [U]

### dbo.RelatorioVolumeInfracaoValidacao
- Arquivo: `codigo-sql/views/dbo.RelatorioVolumeInfracaoValidacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_regra_infracao [U], dbo.local_vigente [V], dbo.Relatorios_Trafego_Pista [U]

### dbo.RelatorioVolumeInfracaoValidacaoPista
- Arquivo: `codigo-sql/views/dbo.RelatorioVolumeInfracaoValidacaoPista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_regra_infracao [U], dbo.local_vigente [V], dbo.Relatorios_Trafego_Pista [U]

### dbo.sis_documento_classificador
- Arquivo: `codigo-sql/views/dbo.sis_documento_classificador.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: —

### dbo.sis_usuario_eventos
- Arquivo: `codigo-sql/views/dbo.sis_usuario_eventos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_usuarios [U]

### dbo.status_processo
- Arquivo: `codigo-sql/views/dbo.status_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: —

### dbo.ultimo_iccid
- Arquivo: `codigo-sql/views/dbo.ultimo_iccid.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.ultimo_MAC
- Arquivo: `codigo-sql/views/dbo.ultimo_MAC.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.v_cad_veiculo_proprietario
- Arquivo: `codigo-sql/views/dbo.v_cad_veiculo_proprietario.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_veiculo_proprietario [U]

### dbo.v_enquadramentos_ativos
- Arquivo: `codigo-sql/views/dbo.v_enquadramentos_ativos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento_regra_infracao [U], dbo.local [U]

### dbo.v_enquadramentos_manutencao
- Arquivo: `codigo-sql/views/dbo.v_enquadramentos_manutencao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.enquadramento_regra_infracao [U]

### dbo.v_equipamento_cameras
- Arquivo: `codigo-sql/views/dbo.v_equipamento_cameras.sql` · criado 2025-06-02 · alterado 2025-09-01
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_camera [U], dbo.configuracao_equipamento_pista [U], dbo.local_pista_vigente [V]

### dbo.v_equipamento_cameras_todas
- Arquivo: `codigo-sql/views/dbo.v_equipamento_cameras_todas.sql` · criado 2025-09-04 · alterado 2025-09-04
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_camera [U], dbo.configuracao_equipamento_pista [U], dbo.local_pista_vigente [V]

### dbo.v_infracao_enquadramento_inconsistencia
- Arquivo: `codigo-sql/views/dbo.v_infracao_enquadramento_inconsistencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.enquadramento [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.veiculo [U]

### dbo.v_locais_pmesp
- Arquivo: `codigo-sql/views/dbo.v_locais_pmesp.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_parametros_adicionais [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V]

### dbo.v_local_pista_vigente
- Arquivo: `codigo-sql/views/dbo.v_local_pista_vigente.sql` · criado 2025-06-02 · alterado 2025-09-25
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.local [U], dbo.sis_usuario [U]

### dbo.v_local_tipo_id
- Arquivo: `codigo-sql/views/dbo.v_local_tipo_id.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V]

### dbo.v_local_tipo_pista
- Arquivo: `codigo-sql/views/dbo.v_local_tipo_pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.v_local_tipo_id [V]

### dbo.v_manutencao_cav
- Arquivo: `codigo-sql/views/dbo.v_manutencao_cav.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.equipamento_estatico [U], dbo.local_vigente [V], dbo.manutencao [U], dbo.v_enquadramentos_ativos [V]

### dbo.v_movimentos_pendentes
- Arquivo: `codigo-sql/views/dbo.v_movimentos_pendentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.remessa_amostragem [U], dbo.remessa_iteracao [U], dbo.sis_usuario [U]

### dbo.v_municipios_equipamentos
- Arquivo: `codigo-sql/views/dbo.v_municipios_equipamentos.sql` · criado 2025-06-02 · alterado 2025-10-23
- Parâmetros/retorno: —
- Referencia: dbo.cad_localidade [U], dbo.InitCap [FN], dbo.local_vigente [V], muralha.fcn_LocalidadeContrato [IF]

### dbo.v_pmesp_estado
- Arquivo: `codigo-sql/views/dbo.v_pmesp_estado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.pmesp_atraso [U], dbo.pmesp_desconectado [U], dbo.pmesp_evento_conexao [U], dbo.pmesp_perda [U]

### dbo.v_ppv_patio_vagas_ptz
- Arquivo: `codigo-sql/views/dbo.v_ppv_patio_vagas_ptz.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ppv_patio_vagas_ptz [U]

### dbo.v_ppv_qfv
- Arquivo: `codigo-sql/views/dbo.v_ppv_qfv.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ppv_qfv_distancias [U], dbo.ppv_qfv_grupo [U], dbo.ppv_qfv_grupo_config [U], dbo.ppv_qfv_tipos [U]

### dbo.v_tmp_equipamentos_hv_incorreto
- Arquivo: `codigo-sql/views/dbo.v_tmp_equipamentos_hv_incorreto.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.v_tmp_imagens_exportar_v2
- Arquivo: `codigo-sql/views/dbo.v_tmp_imagens_exportar_v2.sql` · criado 2025-06-02 · alterado 2025-10-14
- Parâmetros/retorno: —
- Referencia: dbo.imagem [U], dbo.temp_imagens_exportar_dados [U]

### dbo.v_veiculo_completo_gtw
- Arquivo: `codigo-sql/views/dbo.v_veiculo_completo_gtw.sql` · criado 2025-10-12 · alterado 2025-10-14
- Parâmetros/retorno: —
- Referencia: dbo.cadastro_veiculo [V], dbo.classe_veiculo [U], dbo.local_pista_vigente [V], dbo.v_veiculo_pesagem [V], dbo.v_veiculo_pesagem_distancia_eixos [V], dbo.v_veiculo_pesagem_eixo [V], dbo.veiculo_imagem [U], dbo.veiculo_pesquisa [V]

### dbo.v_veiculo_pesagem
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem.sql` · criado 2025-09-30 · alterado 2025-10-15
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_pesagem [U], dbo.veiculo_pesquisa [V]

### dbo.v_veiculo_pesagem_bkp
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem_bkp.sql` · criado 2025-10-19 · alterado 2025-10-19
- Parâmetros/retorno: —
- Referencia: dbo.bkp_veiculo_pesagem [U], dbo.veiculo_pesquisa [V]

### dbo.v_veiculo_pesagem_distancia_eixos
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem_distancia_eixos.sql` · criado 2025-09-30 · alterado 2025-09-30
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_pesagem [U], dbo.veiculo_pesagem_eixo [U]

### dbo.v_veiculo_pesagem_distancia_eixos_bkp
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem_distancia_eixos_bkp.sql` · criado 2025-10-19 · alterado 2025-10-19
- Parâmetros/retorno: —
- Referencia: dbo.bkp_veiculo_pesagem [U], dbo.bkp_veiculo_pesagem_eixo [U]

### dbo.v_veiculo_pesagem_eixo
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem_eixo.sql` · criado 2025-09-30 · alterado 2025-10-12
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_pesagem [U], dbo.veiculo_pesagem_eixo [U]

### dbo.v_veiculo_pesagem_eixo_bkp
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem_eixo_bkp.sql` · criado 2025-10-19 · alterado 2025-10-19
- Parâmetros/retorno: —
- Referencia: dbo.bkp_veiculo_pesagem [U], dbo.bkp_veiculo_pesagem_eixo [U]

### dbo.v_veiculo_pesagem_temp
- Arquivo: `codigo-sql/views/dbo.v_veiculo_pesagem_temp.sql` · criado 2025-10-15 · alterado 2025-10-15
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_pesagem [U], dbo.veiculo_pesquisa [V]

### dbo.v_veiculo_sumarizado_faixa_velocidade
- Arquivo: `codigo-sql/views/dbo.v_veiculo_sumarizado_faixa_velocidade.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_pesquisa [V]

### dbo.v_veiculo_sumarizado_faixa_velocidade_temp
- Arquivo: `codigo-sql/views/dbo.v_veiculo_sumarizado_faixa_velocidade_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_pesquisa_temp [V]

### dbo.v_veiculo_sumarizado_relatorio
- Arquivo: `codigo-sql/views/dbo.v_veiculo_sumarizado_relatorio.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.veiculo_pesquisa_sumariza [V]

### dbo.v_veiculo_sumarizado_relatorio_temp
- Arquivo: `codigo-sql/views/dbo.v_veiculo_sumarizado_relatorio_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.veiculo_pesquisa_sumariza_temp [V]

### dbo.veiculo_flag
- Arquivo: `codigo-sql/views/dbo.veiculo_flag.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_flag_01 [U], dbo.veiculo_flag_02 [U]

### dbo.veiculo_imagens
- Arquivo: `codigo-sql/views/dbo.veiculo_imagens.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.imagem_info [U], dbo.infracao [U], dbo.veiculo [U], dbo.veiculo_imagem [U]

### dbo.veiculo_pesquisa
- Arquivo: `codigo-sql/views/dbo.veiculo_pesquisa.sql` · criado 2025-06-02 · alterado 2026-10-08
- Parâmetros/retorno: —
- Referencia: dbo.veiculo [U], dbo.veiculo_estatistica [U], dbo.veiculo_pesagem_eixo [U]

### dbo.veiculo_pesquisa_classe
- Arquivo: `codigo-sql/views/dbo.veiculo_pesquisa_classe.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.veiculo [U], dbo.veiculo_estatistica [U]

### dbo.veiculo_pesquisa_sumariza
- Arquivo: `codigo-sql/views/dbo.veiculo_pesquisa_sumariza.sql` · criado 2025-06-02 · alterado 2025-10-21
- Parâmetros/retorno: —
- Referencia: dbo.veiculo [U], dbo.veiculo_estatistica [U]

### dbo.veiculo_pesquisa_sumariza_temp
- Arquivo: `codigo-sql/views/dbo.veiculo_pesquisa_sumariza_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.veiculo [U], dbo.veiculo_estatistica [U]

### dbo.veiculo_pesquisa_temp
- Arquivo: `codigo-sql/views/dbo.veiculo_pesquisa_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.veiculo [U], dbo.veiculo_estatistica [U]

### dbo.vw_relatorio_eventos_equipamentos
- Arquivo: `codigo-sql/views/dbo.vw_relatorio_eventos_equipamentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V]

### dbo.vw_relatorio_eventos_reinicializacao
- Arquivo: `codigo-sql/views/dbo.vw_relatorio_eventos_reinicializacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V]

### dbo.vw_relatorio_qtde_arquivos
- Arquivo: `codigo-sql/views/dbo.vw_relatorio_qtde_arquivos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V]

### dbo.vw_relatorio_qtde_img_teste
- Arquivo: `codigo-sql/views/dbo.vw_relatorio_qtde_img_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.local_vigente [V]

### dbo.vw_relatorio_qtde_infracao_completo
- Arquivo: `codigo-sql/views/dbo.vw_relatorio_qtde_infracao_completo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.classe_veiculo [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento [U], dbo.enquadramento_regra_infracao [U], dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.vw_relatorio_versao_captura
- Arquivo: `codigo-sql/views/dbo.vw_relatorio_versao_captura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V]

### dbo.vw_sis_usuario_eventos_CAV
- Arquivo: `codigo-sql/views/dbo.vw_sis_usuario_eventos_CAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_CAV [V]

### muralha.v_config_status_tipo_alerta
- Arquivo: `codigo-sql/views/muralha.v_config_status_tipo_alerta.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: muralha.tipo_alerta_ocorrencia [U]

### muralha.v_config_vigente_monitoramento_ao_vivo
- Arquivo: `codigo-sql/views/muralha.v_config_vigente_monitoramento_ao_vivo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.sis_usuario [U], muralha.config_monitoramento_ao_vivo [U], muralha.monitoramento_ao_vivo_grupo_exibicao [U]

### muralha.v_enquadramentos_dashboard
- Arquivo: `codigo-sql/views/muralha.v_enquadramentos_dashboard.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.enquadramento_regra_infracao [U]

### muralha.v_grupo_alertas
- Arquivo: `codigo-sql/views/muralha.v_grupo_alertas.sql` · criado 2025-06-25 · alterado 2025-07-03
- Parâmetros/retorno: —
- Referencia: dbo.sis_grupo [U]

### muralha.v_grupo_supervisionado
- Arquivo: `codigo-sql/views/muralha.v_grupo_supervisionado.sql` · criado 2025-06-23 · alterado 2025-11-25
- Parâmetros/retorno: —
- Referencia: dbo.sis_grupo [U]

### muralha.v_porte_veiculo_ref
- Arquivo: `codigo-sql/views/muralha.v_porte_veiculo_ref.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.classe_veiculo [U]

### muralha.v_status_alerta_vinculado
- Arquivo: `codigo-sql/views/muralha.v_status_alerta_vinculado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: muralha.status_alerta [U]

### muralha.v_status_ocorrencia_finalizacao
- Arquivo: `codigo-sql/views/muralha.v_status_ocorrencia_finalizacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: muralha.status_ocorrencia [U]

### muralha.v_veiculo_tempo_real
- Arquivo: `codigo-sql/views/muralha.v_veiculo_tempo_real.sql` · criado 2025-06-02 · alterado 2025-09-12
- Parâmetros/retorno: —
- Referencia: dbo.cadastro_veiculo [V], dbo.classe_veiculo [U], dbo.local_pista_vigente [V], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### muralha.v_veiculo_tempo_real_teste
- Arquivo: `codigo-sql/views/muralha.v_veiculo_tempo_real_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### muralha.veiculos_transporte_clandestino_importar
- Arquivo: `codigo-sql/views/muralha.veiculos_transporte_clandestino_importar.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: —

### muralha.vw_registro_fato_origem
- Arquivo: `codigo-sql/views/muralha.vw_registro_fato_origem.sql` · criado 2025-09-29 · alterado 2025-09-29
- Parâmetros/retorno: —
- Referencia: muralha.fato [U], muralha.registro_fato [U], muralha.registro_fato_historico [U]

### muralha.vw_resumo_correlacionamento_placas
- Arquivo: `codigo-sql/views/muralha.vw_resumo_correlacionamento_placas.sql` · criado 2026-04-28 · alterado 2026-05-05
- Parâmetros/retorno: —
- Referencia: muralha.analise_correlacionamento_placas_registro_fato, muralha.correlacionamento_placas

## Stored Procedures — 388

### dbo.grava_indicadores_tempo_real
- Arquivo: `codigo-sql/procedures/dbo.grava_indicadores_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @recebido int, @enviado int, @id int
- Referencia: dbo.indicadores_estatisticas_tempo_real [U]

### dbo.obter_indicadores_tempo_real
- Arquivo: `codigo-sql/procedures/dbo.obter_indicadores_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id bigint OUT
- Referencia: dbo.indicadores_estatisticas_tempo_real [U]

### dbo.ObterPassagensPorPlaca
- Arquivo: `codigo-sql/procedures/dbo.ObterPassagensPorPlaca.sql` · criado 2025-07-22 · alterado 2025-07-22
- Parâmetros/retorno: @Placa varchar, @DataInicio datetime, @DataFim datetime, @IdLocal int
- Referencia: dbo.local [U], muralha.veiculo_tempo_real [U]

### dbo.pmesp_atualiza_evento_conexao
- Arquivo: `codigo-sql/procedures/dbo.pmesp_atualiza_evento_conexao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_evento_conexao int, @id_local int, @data_desconexao datetime, @movimentos_recebidos int, @movimentos_transmitidos int, @movimentos_invalidos int, @data_ultimo_movimento datetime
- Referencia: dbo.pmesp_evento_conexao [U]

### dbo.pmesp_atualiza_tabela_auxiliar_medicao
- Arquivo: `codigo-sql/procedures/dbo.pmesp_atualiza_tabela_auxiliar_medicao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.pmesp_movimento [V], dbo.pmesp_movimentos_atraso_dia [U]

### dbo.pmesp_insere_evento_conexao
- Arquivo: `codigo-sql/procedures/dbo.pmesp_insere_evento_conexao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_conexao datetime, @endereco_ip varchar
- Referencia: dbo.pmesp_evento_conexao [U]

### dbo.pmesp_movimento_erro_inserir
- Arquivo: `codigo-sql/procedures/dbo.pmesp_movimento_erro_inserir.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_movimento bigint, @id_equipamento int, @id_evento_conexao int, @codigo int, @placa_retorno char, @mensagem varchar
- Referencia: dbo.pmesp_movimento_erro [U]

### dbo.PMESP_ObterTempoOffline
- Arquivo: `codigo-sql/procedures/dbo.PMESP_ObterTempoOffline.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int
- Referencia: dbo.fcn_ObterDatasMinutosPeriodo [IF], dbo.pmesp_evento_conexao [U]

### dbo.prd_getEstatisticaTodosCompleto
- Arquivo: `codigo-sql/procedures/dbo.prd_getEstatisticaTodosCompleto.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.classe_veiculo [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.fcn_ObterVariavelFluxo [FN], dbo.local_pista_vigente [V], dbo.local_vigente [V], dbo.status_traffic [U], dbo.v_local_tipo_id [V], dbo.v_local_tipo_pista [V], muralha.veiculo_tempo_real [U]

### dbo.sp_alterdiagram
- Arquivo: `codigo-sql/procedures/dbo.sp_alterdiagram.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: @diagramname sysname, @owner_id int, @version int, @definition varbinary
- Referencia: dbo.sysdiagrams [U]

### dbo.sp_creatediagram
- Arquivo: `codigo-sql/procedures/dbo.sp_creatediagram.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: @diagramname sysname, @owner_id int, @version int, @definition varbinary
- Referencia: dbo.sysdiagrams [U]

### dbo.sp_dicionario_dados_dependencias
- Arquivo: `codigo-sql/procedures/dbo.sp_dicionario_dados_dependencias.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dbname varchar
- Referencia: dbo.dependencias, dbo.spt_values, dbo.tabela

### dbo.sp_dropdiagram
- Arquivo: `codigo-sql/procedures/dbo.sp_dropdiagram.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: @diagramname sysname, @owner_id int
- Referencia: dbo.sysdiagrams [U]

### dbo.sp_helpdiagramdefinition
- Arquivo: `codigo-sql/procedures/dbo.sp_helpdiagramdefinition.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: @diagramname sysname, @owner_id int
- Referencia: dbo.sysdiagrams [U]

### dbo.sp_helpdiagrams
- Arquivo: `codigo-sql/procedures/dbo.sp_helpdiagrams.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: @diagramname sysname, @owner_id int
- Referencia: dbo.sysdiagrams [U]

### dbo.sp_RelatorioPermanenciaArea
- Arquivo: `codigo-sql/procedures/dbo.sp_RelatorioPermanenciaArea.sql` · criado 2025-10-06 · alterado 2025-10-06
- Parâmetros/retorno: @area_monitorada varchar, @placa varchar, @data_ini date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### dbo.sp_renamediagram
- Arquivo: `codigo-sql/procedures/dbo.sp_renamediagram.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: @diagramname sysname, @owner_id int, @new_diagramname sysname
- Referencia: dbo.sysdiagrams [U]

### dbo.sp_upgraddiagrams
- Arquivo: `codigo-sql/procedures/dbo.sp_upgraddiagrams.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: —
- Referencia: dbo.dtproperties, dbo.sysdiagrams [U]

### dbo.spu_adic_amostra_manual
- Arquivo: `codigo-sql/procedures/dbo.spu_adic_amostra_manual.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data date, @id_local int, @id_pista int, @metrologica bit, @id_veiculo bigint, @aplicavel bit, @id_usuario int
- Referencia: dbo.amostra_imagem_manual [U], dbo.enquadramento [U], dbo.fcn_pontua_infracao [FN], dbo.infracao [U], dbo.infracao_rejeita_amostra [U], dbo.spu_replica_erro

### dbo.spu_adic_documento
- Arquivo: `codigo-sql/procedures/dbo.spu_adic_documento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @identificador_externo char, @nome_arquivo varchar, @id_classificador int, @id_usuario_criacao int, @md5_documento char, @conteudo varbinary
- Referencia: dbo.sis_documento [U], dbo.sis_documento_conteudo [U]

### dbo.spu_adic_imagem_ar
- Arquivo: `codigo-sql/procedures/dbo.spu_adic_imagem_ar.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @tipo_ar char, @imagem image, @id_status int, @data_retorno date, @observacao varchar
- Referencia: dbo.controle_ar [U], dbo.imagem_ar [U]

### dbo.spu_adic_imagem_ar_nai
- Arquivo: `codigo-sql/procedures/dbo.spu_adic_imagem_ar_nai.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @tipo_ar char, @imagem image, @id_status int, @data_retorno date, @observacao varchar
- Referencia: dbo.controle_ar [U], dbo.imagem_ar [U]

### dbo.spu_adic_imagem_ar_nip
- Arquivo: `codigo-sql/procedures/dbo.spu_adic_imagem_ar_nip.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @tipo_ar char, @imagem image, @id_status int, @data_retorno date, @observacao varchar
- Referencia: dbo.controle_ar [U], dbo.imagem_ar [U]

### dbo.spu_adic_periodo_medicao
- Arquivo: `codigo-sql/procedures/dbo.spu_adic_periodo_medicao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_medicao int, @data_inicio datetime, @data_final datetime, @complementar bit
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.processo_medicao [U], dbo.processo_medicao_veiculo [U], dbo.veiculo [U]

### dbo.spu_agendar_processamento
- Arquivo: `codigo-sql/procedures/dbo.spu_agendar_processamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @id_remessa int, @id_enquadramento int, @consistencia bit, @espera bit, @periodo_ini datetime, @periodo_fim datetime, @id_inconsistencia int
- Referencia: dbo.agendamento_processamento [U], dbo.fcn_InfracaoDisponivelUsuario [IF], dbo.spu_replica_erro

### dbo.spu_ajusta_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_ajusta_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_infracao_processo int
- Referencia: dbo.enquadramento [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_obliteracao [U], dbo.infracao_processo [U], dbo.infracao_processo_obliteracao [U], dbo.spu_replica_erro, dbo.tipo_imagem [U], dbo.veiculo_imagem [U]

### dbo.spu_ajusta_janela
- Arquivo: `codigo-sql/procedures/dbo.spu_ajusta_janela.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @tamanho_janela int, @apenas_processadas bit
- Referencia: dbo.infracao_janela [U], dbo.processo [U], dbo.spu_replica_erro, dbo.spu_status_infracao

### dbo.spu_altera_modo_operacao_ptz
- Arquivo: `codigo-sql/procedures/dbo.spu_altera_modo_operacao_ptz.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_modo_operacao tinyint, @id_usuario int
- Referencia: dbo.ptz_configuracao_operacao [U], dbo.spu_replica_erro

### dbo.spu_atualiza_configuracao_equipamento_data_modificacao
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_configuracao_equipamento_data_modificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_data_modificacao [U], dbo.local [U]

### dbo.spu_atualiza_configuracoes_equipamentos
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_configuracoes_equipamentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento, dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao, dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_agd, dbo.configuracao_equipamento_agd [U], dbo.configuracao_equipamento_agd_pista, dbo.configuracao_equipamento_agd_pista [U], dbo.configuracao_equipamento_agenda_camera, dbo.configuracao_equipamento_agenda_camera [U], dbo.configuracao_equipamento_camera, dbo.configuracao_equipamento_camera [U], dbo.configuracao_equipamento_captura_imagem, dbo.configuracao_equipamento_captura_imagem [U], dbo.configuracao_equipamento_captura_veiculo, dbo.configuracao_equipamento_captura_veiculo [U], dbo.configuracao_equipamento_controlador, dbo.configuracao_equipamento_controlador [U], dbo.configuracao_equipamento_controlador_canais, dbo.configuracao_equipamento_controlador_canais [U], dbo.configuracao_equipamento_controlador_canaisv2, dbo.configuracao_equipamento_controlador_canaisv2 [U], dbo.configuracao_equipamento_controlador_pesagem, dbo.configuracao_equipamento_controlador_pesagem [U], dbo.configuracao_equipamento_controlador_pesagem_canais, dbo.configuracao_equipamento_controlador_pesagem_canais [U], dbo.configuracao_equipamento_dimensoes_ml, dbo.configuracao_equipamento_dimensoes_ml [U], dbo.configuracao_equipamento_div, dbo.configuracao_equipamento_div [U], dbo.configuracao_equipamento_geral_divs, dbo.configuracao_equipamento_geral_divs [U], dbo.configuracao_equipamento_horario, dbo.configuracao_equipamento_horario [U], dbo.configuracao_equipamento_laco_virtual_ml, dbo.configuracao_equipamento_laco_virtual_ml [U], dbo.configuracao_equipamento_nivel_video, dbo.configuracao_equipamento_nivel_video [U], dbo.configuracao_equipamento_painel, dbo.configuracao_equipamento_painel [U], dbo.configuracao_equipamento_painel_geral, dbo.configuracao_equipamento_painel_geral [U], dbo.configuracao_equipamento_parametros_adicionais, dbo.configuracao_equipamento_parametros_adicionais [U], dbo.configuracao_equipamento_pesagem, dbo.configuracao_equipamento_pesagem [U], dbo.configuracao_equipamento_pista, dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_pista_pesagem, dbo.configuracao_equipamento_pista_pesagem [U], dbo.configuracao_equipamento_pmv, dbo.configuracao_equipamento_pmv [U], dbo.configuracao_equipamento_pmv_circunstancias, dbo.configuracao_equipamento_pmv_circunstancias [U], dbo.configuracao_equipamento_regra_infracao, dbo.configuracao_equipamento_regra_infracao [U], dbo.configuracao_equipamento_relevante, dbo.configuracao_equipamento_relevante [U], dbo.configuracao_equipamento_resolucao_imagem, dbo.configuracao_equipamento_resolucao_imagem [U], dbo.configuracao_equipamento_rodizio, dbo.configuracao_equipamento_rodizio [U], dbo.configuracao_equipamento_rodovia, dbo.configuracao_equipamento_rodovia [U], dbo.configuracao_equipamento_sensor_piezo, dbo.configuracao_equipamento_sensor_piezo [U], dbo.configuracao_equipamento_servidor, dbo.configuracao_equipamento_servidor [U], dbo.local, dbo.local [U], dbo.spu_replica_erro

### dbo.spu_atualiza_local_status_tempo_real
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_local_status_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int, @versao nchar, @tempo_executando bigint, @status_copia nchar, @ultima_deteccao datetime
- Referencia: dbo.status_tempo_real [U]

### dbo.spu_atualiza_log_finaliza_resumo
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_log_finaliza_resumo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.log_finaliza [U], dbo.log_finaliza_detalhe [U], dbo.log_finaliza_resumo [U]

### dbo.spu_atualiza_painel_MAC
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_painel_MAC.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.painel_contrato [U]

### dbo.spu_atualiza_painel_principal
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_painel_principal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_isento_arquivo [U], dbo.fcn_getRelatorioErrosExportaAutomatico [IF], dbo.fcn_getRelatorioIsentos [IF], dbo.fcn_IndicadoresProcessamentoAgrupado [IF], dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimentos_erro [U], dbo.painel_principal [U], dbo.remessa [U]

### dbo.spu_atualiza_processo_medicao
- Arquivo: `codigo-sql/procedures/dbo.spu_atualiza_processo_medicao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_medicao int
- Referencia: dbo.processo_medicao [U], dbo.sis_documento [U], dbo.sis_documento_classificador [V]

### dbo.spu_atualizar_cad_isento_antigo
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_cad_isento_antigo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_isento [U], dbo.cad_isento_ant [U], dbo.infracao [U]

### dbo.spu_atualizar_inconsistencias_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_inconsistencias_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento [U], dbo.enquadramento_inconsistencia [U], dbo.enquadramento_regra_infracao [U], dbo.inconsistencia [U], dbo.inconsistencia_processo_enquadramento [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.local_vigente [V], dbo.processo [U], dbo.processo_inconsistencia [U], dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo

### dbo.spu_atualizar_integracao_sequencia
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_integracao_sequencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.integracao_sequencia_imagem_gct [U]

### dbo.spu_atualizar_painel_contrato
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_painel_contrato.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.painel_contrato [U], dbo.painel_local_desconectado [V], dbo.painel_ultima_infracao [V], dbo.painel_ultimo_arquivo [V]

### dbo.spu_atualizar_painel_contrato_old
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_painel_contrato_old.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_getAlertaEvento [IF], dbo.fcn_getRelatorioPrioridadeManutencao [IF], dbo.fcn_LocalEventosConexao [TF], dbo.fcn_LocalImagensDefeituosa [IF], dbo.fcn_maxDataHoraEventoPorProprietario [IF], dbo.local_parametro_adicional_data_search [V], dbo.local_status_div [V], dbo.local_vigente [V], dbo.painel_contrato [U], dbo.painel_contrato_alerta [U], dbo.painel_local_desconectado [V], dbo.painel_ultima_camera_carregada [V], dbo.painel_ultima_infracao [V], dbo.painel_ultimo_arquivo [V], dbo.ultimo_iccid [V], dbo.ultimo_mac [V]

### dbo.spu_atualizar_veiculo_sumarizado
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDataInicioSumariza [FN], dbo.spu_replica_erro, dbo.spu_sumariza_veiculos, dbo.veiculo_sumarizado [U]

### dbo.spu_atualizar_veiculo_sumarizado_dia_atual
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_dia_atual.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, dbo.spu_sumariza_veiculos, dbo.veiculo_sumarizado [U]

### dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade.sql` · criado 2025-06-02 · alterado 2025-07-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDataInicioSumariza [FN], dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_faixa_velocidade, dbo.veiculo_sumarizado_faixa_velocidade [U]

### dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade_dia_atual
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade_dia_atual.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_faixa_velocidade, dbo.veiculo_sumarizado_faixa_velocidade [U]

### dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_faixa_velocidade_temp, dbo.veiculo_sumarizado_faixa_velocidade [U]

### dbo.spu_atualizar_veiculo_sumarizado_relatorio
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_relatorio.sql` · criado 2025-06-02 · alterado 2025-07-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDataInicioSumariza [FN], dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_relatorio, dbo.veiculo_sumarizado_relatorio [U]

### dbo.spu_atualizar_veiculo_sumarizado_relatorio_dia_atual
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_relatorio_dia_atual.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_relatorio, dbo.veiculo_sumarizado_relatorio [U]

### dbo.spu_atualizar_veiculo_sumarizado_relatorio_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_relatorio_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_relatorio_temp, dbo.veiculo_sumarizado_relatorio [U]

### dbo.spu_atualizar_veiculo_sumarizado_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_atualizar_veiculo_sumarizado_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, dbo.spu_sumariza_veiculos_temp, dbo.veiculo_sumarizado [U]

### dbo.spu_busca_amostras_periodo
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_amostras_periodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFinal date
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.Date_Table [TF], dbo.fcn_Locais_Vigentes_Na_Data [IF], dbo.infracao [U], dbo.infracao_imagem [U], dbo.local_vigente [V], dbo.veiculo_imagem [U]

### dbo.spu_busca_amostras_periodo_local_pista
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_amostras_periodo_local_pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFinal date, @idLocal int, @idPista int, @metrologica bit
- Referencia: dbo.fcn_lista_amostras_periodo_local_pista [IF]

### dbo.spu_busca_infracao_processamento
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_infracao_processamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @id_enquadramento int, @consistencia bit, @espera bit, @periodo_ini datetime, @periodo_fim datetime, @id_infracao_atual int, @acao tinyint
- Referencia: dbo.fcn_InfracaoDisponivelUsuario [IF], dbo.infracao [U], dbo.infracao_janela [U], dbo.spu_ajusta_janela, dbo.spu_replica_erro

### dbo.spu_busca_infracao_processamento_validacao
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_infracao_processamento_validacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @id_remessa int, @id_enquadramento int, @consistencia bit, @espera bit, @periodo_ini datetime, @periodo_fim datetime, @id_infracao_atual int, @acao tinyint, @amostra bit
- Referencia: dbo.fcn_InfracaoDisponivelUsuario [IF], dbo.infracao [U], dbo.infracao_janela [U], dbo.spu_ajusta_janela, dbo.spu_replica_erro

### dbo.spu_busca_multiplas_infracao_contestacao
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_multiplas_infracao_contestacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @id_remessa int, @id_enquadramento int, @consistencia bit, @espera bit, @periodo_ini datetime, @periodo_fim datetime, @qtde_infracao int, @amostra bit
- Referencia: dbo.fcn_InfracaoDisponivelContestacao [IF], dbo.infracao [U], dbo.infracao_janela [U], dbo.spu_ajusta_janela

### dbo.spu_busca_multiplas_infracao_contestacao_teste
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_multiplas_infracao_contestacao_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @id_remessa int, @id_enquadramento int, @consistencia bit, @espera bit, @periodo_ini datetime, @periodo_fim datetime, @qtde_infracao int, @amostra bit
- Referencia: dbo.fcn_InfracaoDisponivelContestacao [IF], dbo.infracao [U], dbo.infracao_janela [U], dbo.spu_ajusta_janela

### dbo.spu_busca_multiplas_infracao_processamento
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_multiplas_infracao_processamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @id_processo int, @id_remessa int, @id_enquadramento int, @consistencia bit, @espera bit, @periodo_ini datetime, @periodo_fim datetime, @qtde_infracao int, @amostra bit
- Referencia: dbo.fcn_InfracaoDisponivelUsuario [IF], dbo.infracao [U], dbo.infracao_janela [U], dbo.spu_ajusta_janela

### dbo.spu_busca_pistas_amostra_imagem
- Arquivo: `codigo-sql/procedures/dbo.spu_busca_pistas_amostra_imagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ListaPistasAmostra [IF]

### dbo.spu_cad_veiculo_proprietario_adicionar
- Arquivo: `codigo-sql/procedures/dbo.spu_cad_veiculo_proprietario_adicionar.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @placa char, @id_marca int, @marca varchar, @id_tipo int, @tipo varchar, @proprietario varchar, @observacao varchar, @imagem image
- Referencia: dbo.cad_veiculo_proprietario [U]

### dbo.spu_conclui_infracao_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_conclui_infracao_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao_processo int, @tempo_proc int
- Referencia: dbo.infracao_processo [U]

### dbo.spu_controle_cadastro_equipamentos
- Arquivo: `codigo-sql/procedures/dbo.spu_controle_cadastro_equipamentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.local [U], dbo.local_vigente [V]

### dbo.spu_cria_exportacao_imagem
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_exportacao_imagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.exportacao_imagem [U], dbo.exportacao_imagem_imagem [U], dbo.infracao_imagem [U], dbo.infracao_remessa [U], dbo.veiculo_imagem [U]

### dbo.spu_cria_exportacao_trafego
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_exportacao_trafego.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_trafego date
- Referencia: dbo.exportacao_trafego [U], dbo.exportacao_trafego_arquivo [U], dbo.veiculo_estatistica [U]

### dbo.spu_cria_Relatorios_Diario_MediaVelocidade_Hora
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Diario_MediaVelocidade_Hora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.local_vigente [V], dbo.Relatorios_Gerados [U], dbo.Relatorios_MediaVelocidade_Hora [U], dbo.veiculo [U]

### dbo.spu_cria_Relatorios_Diario_Trafego_Pista
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Diario_Trafego_Pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.inconsistencia [U], dbo.infracao_completa [V], dbo.local_vigente [V], dbo.Relatorios_Gerados [U], dbo.Relatorios_Trafego_Pista [U], dbo.veiculo [U]

### dbo.spu_cria_Relatorios_Diario_Volume_Hora_PMG
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Diario_Volume_Hora_PMG.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.classe_veiculo [U], dbo.configuracao_equipamento_captura_veiculo [U], dbo.configuracao_equipamento_pista [U], dbo.local [U], dbo.local_vigente [V], dbo.Relatorios_Gerados [U], dbo.Relatorios_Volume_Hora_PMG [U], dbo.Veiculo [U]

### dbo.spu_cria_Relatorios_Periodo_MediaVelocidade_Hora
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Periodo_MediaVelocidade_Hora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicio datetime, @DataFinal datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.Relatorios_Gerados [U], dbo.Relatorios_MediaVelocidade_Hora [U]

### dbo.spu_cria_Relatorios_Periodo_Trafego_Pista
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Periodo_Trafego_Pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicio datetime, @DataFinal datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.Relatorios_Gerados [U], dbo.Relatorios_Trafego_Pista [U]

### dbo.spu_cria_Relatorios_Periodo_Volume_Hora_PMG
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Periodo_Volume_Hora_PMG.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicio datetime, @DataFinal datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.Relatorios_Gerados [U], dbo.Relatorios_Volume_Hora_PMG [U]

### dbo.spu_cria_Relatorios_Trafego_Pista
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Trafego_Pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicio datetime, @DataFinal datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.spu_cria_Relatorios_Diario_Trafego_Pista, dbo.spu_cria_Relatorios_Periodo_Trafego_Pista

### dbo.spu_cria_Relatorios_Volume_Hora_PMG
- Arquivo: `codigo-sql/procedures/dbo.spu_cria_Relatorios_Volume_Hora_PMG.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicio datetime, @DataFinal datetime, @HoraInicio datetime, @HoraFinal datetime, @Usuario char
- Referencia: dbo.spu_cria_Relatorios_Diario_Volume_Hora_PMG, dbo.spu_cria_Relatorios_Periodo_Volume_Hora_PMG

### dbo.spu_criar_amostras_imagens
- Arquivo: `codigo-sql/procedures/dbo.spu_criar_amostras_imagens.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.amostra_imagem [U], dbo.fcn_pontua_infracao [FN], dbo.infracao_amostra [V], dbo.local_regra_infracao [V]

### dbo.spu_criar_inibicao_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_criar_inibicao_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @descricao varchar, @pista int, @serie_equipamento int, @id_enquadramento int, @id_classe char, @data_inicio date, @data_fim date, @horario_inicio time, @horario_fim time, @id_usuario int, @id_inconsistencia int
- Referencia: dbo.cad_inibicao_infracao [U], dbo.spu_replica_erro

### dbo.spu_criar_processo_medicao
- Arquivo: `codigo-sql/procedures/dbo.spu_criar_processo_medicao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @mes smallint, @ano smallint
- Referencia: dbo.processo_medicao [U]

### dbo.spu_desativa_alerta_inexistente
- Arquivo: `codigo-sql/procedures/dbo.spu_desativa_alerta_inexistente.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @info_adic varchar
- Referencia: dbo.fcn_getRelatorioPrioridadeManutencao [IF], dbo.painel_contrato_alerta [U]

### dbo.spu_desativar_inibicao_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_desativar_inibicao_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_inibicao_infracao int, @id_usuario int
- Referencia: dbo.cad_inibicao_infracao [U], dbo.filtro [U], dbo.spu_replica_erro

### dbo.spu_DicionarioDados
- Arquivo: `codigo-sql/procedures/dbo.spu_DicionarioDados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: —

### dbo.spu_digitacao_automatica
- Arquivo: `codigo-sql/procedures/dbo.spu_digitacao_automatica.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime
- Referencia: dbo.cad_especie_processo [U], dbo.cad_marca_cet_processo [U], dbo.cad_veiculo [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.spu_processa_infracao_direto_digitacao, dbo.spu_status_infracao, dbo.veiculo [U]

### dbo.spu_digitacao_confirmada
- Arquivo: `codigo-sql/procedures/dbo.spu_digitacao_confirmada.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.cad_isento [U], dbo.cad_veiculo [U], dbo.infracao [U], dbo.veiculo [U]

### dbo.spu_EnviaEmailAlertaArquivosCadastrosSemNovos
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaArquivosCadastrosSemNovos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_arquivos_importados [U], dbo.configuracao_alerta_cad_arquivos_importados [U], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaCapturaTravado
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaCapturaTravado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaCapturaTravadoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaExcessoEventos
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaExcessoEventos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaExcessoEventosHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaFalhaCarregamentoAgenda
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaFalhaCarregamentoAgenda.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaFalhaCarregamentoAgendaHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaLocaisConexaoInstavel
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaLocaisConexaoInstavel.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaLocaisConexaoInstavelHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaLocaisOffline
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaLocaisOffline.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaLocaisOfflineHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaNaoEnviaEventos
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaNaoEnviaEventos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaNaoEnviaEventosHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAlertaSemVeiculosValidos
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAlertaSemVeiculosValidos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAproveitamentoImagens
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAproveitamentoImagens.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAproveitamentoImagens [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAproveitamentoImagensAgrupado
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAproveitamentoImagensAgrupado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAproveitamentoImagensAgrupado [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailAtualizacaoSoftware
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailAtualizacaoSoftware.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailBDEventosCorrompido
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailBDEventosCorrompido.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailCapturaIniciado
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailCapturaIniciado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailDefasagemRelogio
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailDefasagemRelogio.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailErroAtualizacaoSoftware
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailErroAtualizacaoSoftware.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailErroDIV
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailErroDIV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailErroLeituraArqLote
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailErroLeituraArqLote.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailErroPainelControlador
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailErroPainelControlador.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailErroRespostaPooling
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailErroRespostaPooling.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailErrosProcessamento
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailErrosProcessamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getErrosProcessamentoHTML [FN], dbo.fcn_getNomeContrato [FN], dbo.infracao_processo_finalizada [V], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailFalhaArquivosImportados
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailFalhaArquivosImportados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.falha_arquivos_importados [U], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.local_vigente [V], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailFalhaSequenciaImagem
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailFalhaSequenciaImagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_pesquisa [V], dbo.falha_sequencia_imagem [U], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.local_vigente [V], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailHistoricoAfericao
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailHistoricoAfericao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getHistoricoAfericaoHTML [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailHistoricoManutencao
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailHistoricoManutencao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getHistoricoManutencaoHTML [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailImagemDefeituosa
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailImagemDefeituosa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailImportacaoAtrasada
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailImportacaoAtrasada.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.local_vigente [V], dbo.sp_send_dbmail, dbo.veiculo_pesquisa [V]

### dbo.spu_EnviaEmailManutencao
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailManutencao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaManutencaoHTML, dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailParamMetroDivergentes
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailParamMetroDivergentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailParamMetroNaoEncontrados
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailParamMetroNaoEncontrados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailProximasAfericoes
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailProximasAfericoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.fcn_getProximasAfericoesHTML [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailRelatorioDeIsencoes
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailRelatorioDeIsencoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.fcn_getRelatorioIsencoesHTML [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailSemEnergiaComercial
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailSemEnergiaComercial.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getAlertaEventoHTML [FN], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail

### dbo.spu_EnviaEmailVeiculoInvalido
- Arquivo: `codigo-sql/procedures/dbo.spu_EnviaEmailVeiculoInvalido.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.local_vigente [V], dbo.sp_send_dbmail, dbo.veiculo_pesquisa [V]

### dbo.spu_espera_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_espera_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int
- Referencia: dbo.infracao [U], dbo.infracao_processo [U], dbo.spu_status_infracao

### dbo.spu_estado_processamento
- Arquivo: `codigo-sql/procedures/dbo.spu_estado_processamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @data_ini datetime, @data_fim datetime
- Referencia: dbo.infracao [U], dbo.processo [U]

### dbo.spu_estatistica_tempo_real
- Arquivo: `codigo-sql/procedures/dbo.spu_estatistica_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local smallint, @id_pista tinyint, @placa char, @data datetime, @velocidade smallint, @classificacao char
- Referencia: —

### dbo.spu_excluir_remessa
- Arquivo: `codigo-sql/procedures/dbo.spu_excluir_remessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remesssa int
- Referencia: dbo.infracao_remessa [U], dbo.infracao_remessa_excluida [U], dbo.remessa [U], dbo.remessa_excluida [U], dbo.spu_replica_erro

### dbo.spu_executarReindex
- Arquivo: `codigo-sql/procedures/dbo.spu_executarReindex.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.reindex [U]

### dbo.spu_execute_index_defrag_all_indexes
- Arquivo: `codigo-sql/procedures/dbo.spu_execute_index_defrag_all_indexes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @TableNameLike varchar
- Referencia: —

### dbo.spu_exporta_amostras_periodo
- Arquivo: `codigo-sql/procedures/dbo.spu_exporta_amostras_periodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFinal date
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.imagem [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_remessa [U], dbo.local [U], dbo.tipo_imagem [U], dbo.veiculo_imagem [U]

### dbo.spu_exportar_imagens_comprovacao
- Arquivo: `codigo-sql/procedures/dbo.spu_exportar_imagens_comprovacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_medicao int, @complementar bit
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.imagem [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_remessa [U], dbo.local [U], dbo.processo_medicao_veiculo [U]

### dbo.spu_finaliza
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.spu_atualizar_veiculo_sumarizado_dia_atual, dbo.spu_atualizar_veiculo_sumarizado_faixa_velocidade_dia_atual, dbo.spu_atualizar_veiculo_sumarizado_relatorio_dia_atual, dbo.spu_finaliza_importacao_estatisticas, dbo.spu_finaliza_importacao_imagens, dbo.spu_finaliza_importacao_sequencia_local, dbo.spu_finaliza_importacao_sequencia_local_novo

### dbo.spu_finaliza_cav
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_cav.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_finaliza_importacao_movimento_lote, dbo.spu_finaliza_movimento_rejeitado, dbo.spu_finaliza_remessas_validadas

### dbo.spu_finaliza_importacao_estatisticas
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_estatisticas.sql` · criado 2025-06-02 · alterado 2025-10-17
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.bkp_veiculo_pesagem [U], dbo.bkp_veiculo_pesagem_eixo [U], dbo.fcn_FormataNumero [FN], dbo.imagem_importacao [U], dbo.perfil_importacao [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.veiculo [U], dbo.veiculo_estatistica [U], dbo.veiculo_importacao [U], dbo.veiculo_pesagem [U], dbo.veiculo_pesagem_controle [U], dbo.veiculo_pesagem_eixo [U], dbo.veiculo_pesagem_eixo_importacao [U], dbo.veiculo_pesagem_importacao [U], dbo.video_importacao [U]

### dbo.spu_finaliza_importacao_estatisticas_1
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_estatisticas_1.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_FormataNumero [FN], dbo.imagem_importacao [U], dbo.local_vigente [V], dbo.perfil_importacao [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.tmp_nome_arquivos_importar, dbo.veiculo_estatistica [U], dbo.veiculo_importacao [U], dbo.video_importacao [U]

### dbo.spu_finaliza_importacao_estatisticas_ant
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_estatisticas_ant.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.fcn_FormataNumero [FN], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.veiculo_estatistica [U], dbo.veiculo_importacao_ant, dbo.vi

### dbo.spu_finaliza_importacao_estatisticas_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_estatisticas_temp.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.fcn_FormataNumero [FN], dbo.imagem_importacao [U], dbo.perfil_importacao [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.veiculo_estatistica [U], dbo.veiculo_importacao [U], dbo.video_importacao [U]

### dbo.spu_finaliza_importacao_imagens
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_imagens.sql` · criado 2025-06-02 · alterado 2025-10-17
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.bkp_veiculo_pesagem [U], dbo.bkp_veiculo_pesagem_eixo [U], dbo.equipamento_estatico [U], dbo.fcn_FormataNumero [FN], dbo.imagem [U], dbo.imagem_importacao [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_importacao [U], dbo.local_vigente [V], dbo.perfil [U], dbo.perfil_importacao [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.tipo_imagem [U], dbo.tipo_video [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_importacao [U], dbo.veiculo_importacao_estatico [U], dbo.veiculo_pesagem [U], dbo.veiculo_pesagem_controle [U], dbo.veiculo_pesagem_eixo [U], dbo.veiculo_pesagem_eixo_importacao [U], dbo.veiculo_pesagem_importacao [U], dbo.veiculo_video [U], dbo.video [U], dbo.video_importacao [U], dbo.video_info [U]

### dbo.spu_finaliza_importacao_imagens_1
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_imagens_1.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.equipamento_estatico [U], dbo.fcn_FormataNumero [FN], dbo.imagem [U], dbo.imagem_importacao [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_importacao [U], dbo.local_vigente [V], dbo.perfil [U], dbo.perfil_importacao [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.tipo_imagem [U], dbo.tipo_video [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_importacao [U], dbo.veiculo_importacao_estatico [U], dbo.veiculo_video [U], dbo.video [U], dbo.video_importacao [U], dbo.video_info [U]

### dbo.spu_finaliza_importacao_imagens_velocidade_pan
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_imagens_velocidade_pan.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.enquadramento [U], dbo.imagem [U], dbo.imagem_importacao [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_importacao [U], dbo.tipo_imagem [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_importacao [U], dbo.veiculo_importacao_estatico [U]

### dbo.spu_finaliza_importacao_movimento_lote
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_movimento_lote.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_veiculo [U], dbo.chave_valor [U], dbo.enquadramento_regra_infracao [U], dbo.imagem [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimento_arquivo [U], dbo.movimento_importacao [U], dbo.movimento_tarja [U], dbo.movimento_tipo_arquivo [U], dbo.remessa [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_importacao [U], dbo.veiculo_video [U], dbo.video [U], dbo.video_info [U]

### dbo.spu_finaliza_importacao_sequencia_local
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_sequencia_local.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pendente_importacao_novo [V], dbo.fcn_FormataNumero [FN], dbo.local_vigente [V], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.veiculo_importacao [U]

### dbo.spu_finaliza_importacao_sequencia_local_novo
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_sequencia_local_novo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pendente_importacao_novo [V], dbo.spu_atualiza_configuracao_equipamento_data_modificacao, dbo.spu_log_atualiza_processo, dbo.spu_log_atualiza_processo_total, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.veiculo_importacao [U]

### dbo.spu_finaliza_importacao_sequencia_local_temp_lv
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_sequencia_local_temp_lv.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.spu_log_atualiza_processo, dbo.spu_log_atualiza_processo_total, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.veiculo_importacao [U]

### dbo.spu_finaliza_importacao_targets
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_importacao_targets.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.veiculo [U], dbo.veiculo_target [U]

### dbo.spu_finaliza_movimento_rejeitado
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_movimento_rejeitado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.enquadramento_regra_infracao [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimento_arquivo [U], dbo.movimento_importacao [U], dbo.remessa [U], dbo.spu_reposiciona_infracao_processo

### dbo.spu_finaliza_remessas_validadas
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_remessas_validadas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.remessa [U]

### dbo.spu_finaliza_remessas_validadas_cai
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_remessas_validadas_cai.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.lote_reprovado [U], dbo.movimento_arquivo [U], dbo.remessa [U]

### dbo.spu_finaliza_trata_data_futura
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_trata_data_futura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_FormataNumero [FN], dbo.veiculo_importacao [U]

### dbo.spu_finaliza_trata_inconsistentes
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_trata_inconsistentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_finaliza_trata_data_futura, dbo.spu_finaliza_trata_registro_duplicado, dbo.spu_finaliza_trata_velocidade_media

### dbo.spu_finaliza_trata_panoramica_excesso
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_trata_panoramica_excesso.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_FormataNumero [FN], dbo.imagem_importacao [U], dbo.imagem_info [U], dbo.tipo_imagem [U]

### dbo.spu_finaliza_trata_registro_duplicado
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_trata_registro_duplicado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_FormataNumero [FN], dbo.imagem_importacao [U], dbo.infracao_importacao [U], dbo.veiculo_importacao [U]

### dbo.spu_finaliza_trata_velocidade_media
- Arquivo: `codigo-sql/procedures/dbo.spu_finaliza_trata_velocidade_media.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.veiculo_importacao [U]

### dbo.spu_gera_agendamento_remessa
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_agendamento_remessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_remessa datetime, @data_inicial datetime, @data_final datetime, @infracoes_por_lote int, @id_processo_remessa int, @id_usuario int
- Referencia: dbo.gera_remessa_automatico [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.spu_qtde_aproximada_lotes, dbo.spu_replica_erro

### dbo.spu_gera_descarga
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_descarga.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @diaInicio date, @diaFim date, @idUsuario int
- Referencia: dbo.descarga [U], dbo.infracao [U], dbo.local_vigente [V], dbo.spu_replica_erro, dbo.veiculo [U], dbo.veiculo_descarga [U], dbo.veiculo_imagem [U]

### dbo.spu_gera_remessa
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_remessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_enquadramento int, @id_processo_remessa int, @codigo_externo int, @data_inicio datetime, @data_fim datetime, @tipo char, @data_remessa date, @residual bit, @infracoes int, @id_usuario int, @id_inconsistencia int, @id_remessa_automatico int
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U]

### dbo.spu_gera_remessa_faixa
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_remessa_faixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_remessa int, @data_inicio datetime, @data_fim datetime, @data_remessa date, @residual bit, @infracoes int, @id_usuario int, @id_inconsistencia int, @id_remessa_automatico int
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.local [U], dbo.remessa [U]

### dbo.spu_gera_remessa_urbs
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_remessa_urbs.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_remessa int, @codigo_externo int, @data_inicio datetime, @data_fim datetime, @tipo varchar, @data_remessa date, @residual bit
- Referencia: dbo.chave_valor [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U]

### dbo.spu_gera_remessa_velocidade_100
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_remessa_velocidade_100.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_enquadramento int, @id_processo_remessa int, @codigo_externo int, @data_inicio datetime, @data_fim datetime, @tipo char, @data_remessa date, @residual bit, @infracoes int, @id_usuario int, @id_inconsistencia int, @id_remessa_automatico int
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.veiculo [U]

### dbo.spu_gera_solicitacao_auditoria
- Arquivo: `codigo-sql/procedures/dbo.spu_gera_solicitacao_auditoria.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_imagens date, @id_processo int, @id_usuario int
- Referencia: dbo.infracao [U], dbo.solicitacao_auditoria [U], dbo.solicitacao_auditoria_infracao [U], dbo.spu_replica_erro

### dbo.spu_gerencia_contrato_atualiza_datas
- Arquivo: `codigo-sql/procedures/dbo.spu_gerencia_contrato_atualiza_datas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_maxDataHoraEventoPorProprietario [IF], dbo.gerencia_contrato_alerta [U], dbo.local_parametro_adicional_data_search [V], dbo.local_vigente [V], dbo.painel_local_desconectado [V], dbo.painel_ultima_camera_carregada [V], dbo.painel_ultima_infracao [V], dbo.painel_ultimo_arquivo [V]

### dbo.spu_gerencia_contrato_atualiza_imagens_defeituosas
- Arquivo: `codigo-sql/procedures/dbo.spu_gerencia_contrato_atualiza_imagens_defeituosas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_LocalImagensDefeituosa [IF], dbo.gerencia_contrato_alerta [U], dbo.local_vigente [V]

### dbo.spu_gerencia_contrato_atualiza_status_config_equip
- Arquivo: `codigo-sql/procedures/dbo.spu_gerencia_contrato_atualiza_status_config_equip.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_maxDataHoraEventoPorProprietario [IF], dbo.gerencia_contrato_alerta [U], dbo.local_vigente [V]

### dbo.spu_getHTML_AlertaCapturaTravado
- Arquivo: `codigo-sql/procedures/dbo.spu_getHTML_AlertaCapturaTravado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @tableHTML nvarchar OUT
- Referencia: dbo.AlertaCapturaTravado

### dbo.spu_getModoOperacaoPPV
- Arquivo: `codigo-sql/procedures/dbo.spu_getModoOperacaoPPV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ppv_ocorrencias [U], dbo.ppv_tp_ocorrencia [U]

### dbo.spu_getPorcentagemOcupacaoVia
- Arquivo: `codigo-sql/procedures/dbo.spu_getPorcentagemOcupacaoVia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: dbo.local_pista_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.spu_getProximoProcesso
- Arquivo: `codigo-sql/procedures/dbo.spu_getProximoProcesso.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_processo_atual int, @id_inconsistencia int
- Referencia: dbo.processo [U], dbo.processo_ligacao [U], dbo.spu_replica_erro

### dbo.spu_getRelatorio3MedicaoFluxoVeicular
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorio3MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.local_vigente [V]

### dbo.spu_getRelatorio3MedicaoFluxoVeicularPista
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorio3MedicaoFluxoVeicularPista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.local_pista_vigente [V]

### dbo.spu_getRelatorioFluxo15MinPorClassificacao
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorioFluxo15MinPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.data_hora_15min [U], dbo.local_pista_vigente [V], dbo.veiculo_pesquisa_classe [V]

### dbo.spu_getRelatorioFluxoDiarioPorClassificacao
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorioFluxoDiarioPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.local_pista_vigente [V], dbo.veiculo_pesquisa_classe [V]

### dbo.spu_getRelatorioFluxoHoraPorClassificacao
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorioFluxoHoraPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.local_pista_vigente [V], dbo.veiculo_pesquisa_classe [V]

### dbo.spu_getRelatorioFluxoMensalPorClassificacao
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorioFluxoMensalPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.local_pista_vigente [V], dbo.veiculo_pesquisa_classe [V]

### dbo.spu_getRelatorioProcessamentoAproveitamento
- Arquivo: `codigo-sql/procedures/dbo.spu_getRelatorioProcessamentoAproveitamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_inicial datetime, @data_final datetime
- Referencia: dbo.inconsistencia [U]

### dbo.spu_grava_log
- Arquivo: `codigo-sql/procedures/dbo.spu_grava_log.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @resumo varchar, @tipo char, @id_usuario int, @detalhe varchar, @data datetime
- Referencia: dbo.sis_log [U], dbo.sis_log_detalhe [U]

### dbo.spu_grava_log_local
- Arquivo: `codigo-sql/procedures/dbo.spu_grava_log_local.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @tipo char, @usuario char, @detalhe varchar, @data datetime
- Referencia: dbo.sis_usuario [U], dbo.spu_grava_log

### dbo.spu_imagem_ajuste
- Arquivo: `codigo-sql/procedures/dbo.spu_imagem_ajuste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @idImagem int, @brilho int, @contraste float
- Referencia: dbo.imagem_ajuste [U]

### dbo.spu_IncrementaRevisaoRemessa
- Arquivo: `codigo-sql/procedures/dbo.spu_IncrementaRevisaoRemessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.remessa [U]

### dbo.spu_index_tuning_framework
- Arquivo: `codigo-sql/procedures/dbo.spu_index_tuning_framework.sql` · criado 2026-04-16 · alterado 2026-04-16
- Parâmetros/retorno: @fase varchar
- Referencia: dbo.spu_indices_candidatos_merge [P], dbo.spu_sugestao_criacao_indices_ausentes [P]

### dbo.spu_indices_candidatos_merge
- Arquivo: `codigo-sql/procedures/dbo.spu_indices_candidatos_merge.sql` · criado 2026-04-16 · alterado 2026-04-16
- Parâmetros/retorno: —
- Referencia: —

### dbo.spu_info_lista_credencial_usuarios
- Arquivo: `codigo-sql/procedures/dbo.spu_info_lista_credencial_usuarios.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.sis_usuario [U], dbo.sis_usuario_grupo [U]

### dbo.spu_info_lista_local
- Arquivo: `codigo-sql/procedures/dbo.spu_info_lista_local.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_grupo int
- Referencia: dbo.configuracao_equipamento [U], dbo.local_vigente [V]

### dbo.spu_info_lista_placa_irregular
- Arquivo: `codigo-sql/procedures/dbo.spu_info_lista_placa_irregular.sql` · criado 2025-06-02 · alterado 2026-10-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_situacao [U], dbo.cad_veiculo_monitorado [U], dbo.cadastro_veiculo [V], dbo.situacao_placa_irregular [U]

### dbo.spu_info_local
- Arquivo: `codigo-sql/procedures/dbo.spu_info_local.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int
- Referencia: dbo.configuracao_equipamento [U], dbo.local_vigente [V]

### dbo.spu_info_pista
- Arquivo: `codigo-sql/procedures/dbo.spu_info_pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V]

### dbo.spu_info_status_conexao
- Arquivo: `codigo-sql/procedures/dbo.spu_info_status_conexao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int
- Referencia: dbo.configuracao_equipamento [U], dbo.local_vigente [V], dbo.status_conexao [U]

### dbo.spu_inicia_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_inicia_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.spu_ajusta_infracao, dbo.spu_status_infracao

### dbo.spu_insere_arquivo_recebido
- Arquivo: `codigo-sql/procedures/dbo.spu_insere_arquivo_recebido.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int, @nome_arquivo varchar, @data_criacao_arquivo datetime, @estado_arquivo int
- Referencia: dbo.arquivos [U]

### dbo.spu_insere_config_equip_medicao
- Arquivo: `codigo-sql/procedures/dbo.spu_insere_config_equip_medicao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_medicao [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.produto [U]

### dbo.spu_insere_evento_csx
- Arquivo: `codigo-sql/procedures/dbo.spu_insere_evento_csx.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @proprietario varchar, @data_hora datetime, @id_categoria int, @id_evento int, @descricao varchar, @mensagem varchar, @id_prioridade int, @id_nivel int, @usuario varchar
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.eventos_csx_usuarios [U]

### dbo.spu_insere_evento_csx_manual
- Arquivo: `codigo-sql/procedures/dbo.spu_insere_evento_csx_manual.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @proprietario varchar, @data_hora datetime, @usuario varchar, @tipo_evento int, @id_categoria_evento_manual int
- Referencia: dbo.cad_evento_manual [U], dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.spu_replica_erro

### dbo.spu_insert_status_conexao
- Arquivo: `codigo-sql/procedures/dbo.spu_insert_status_conexao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int, @status int, @ip nchar
- Referencia: dbo.local_vigente [V], dbo.status_conexao [U]

### dbo.spu_insert_status_div
- Arquivo: `codigo-sql/procedures/dbo.spu_insert_status_div.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int, @status int, @codigo_DIV int, @comunicacao_ok bit, @grupo_centena_ok bit, @grupo_decena_ok bit, @grupo_unidade_ok bit, @grupo_vermelho_ok bit, @grupo_amarelo_ok bit, @grupo_verde_ok bit, @endereco_DIV int
- Referencia: dbo.local_vigente [V], dbo.status_DIV [U]

### dbo.spu_insert_status_energia
- Arquivo: `codigo-sql/procedures/dbo.spu_insert_status_energia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int, @status int
- Referencia: dbo.local_vigente [V], dbo.status_energia [U]

### dbo.spu_insert_veiculo_irregular
- Arquivo: `codigo-sql/procedures/dbo.spu_insert_veiculo_irregular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @data datetime, @velocidade numeric, @comprimento numeric, @pista int, @placa char, @formato char, @id_veiculo_local int, @flag int, @id_veiculo_unic bigint, @id_classe char, @imagem image, @lote int, @data_afericao datetime, @id_imagem_local int, @id_imagem_importacao int
- Referencia: dbo.imagem_importacao [U], dbo.imagem_monitorado [U], dbo.local_vigente [V], dbo.tipo_imagem [U], dbo.veiculo_monitorado [U], dbo.veiculo_monitorado_imagem [U]

### dbo.spu_insert_veiculo_tempo_real
- Arquivo: `codigo-sql/procedures/dbo.spu_insert_veiculo_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_veiculo_local int, @data datetime, @placa char, @velocidade decimal, @comprimento decimal, @pista tinyint, @flag int, @segundos decimal, @id_veiculo_unic bigint, @id_classe char, @id_local int, @ocupacao int
- Referencia: dbo.local_vigente [V], dbo.veiculo_tempo_real

### dbo.spu_insertVeiculo
- Arquivo: `codigo-sql/procedures/dbo.spu_insertVeiculo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_veiculo_local int, @data datetime, @velocidade decimal, @comprimento decimal, @pista tinyint, @flag int, @segundos decimal, @id_veiculo_unic int, @id_local int, @id_classe char
- Referencia: dbo.veiculo [U]

### dbo.spu_integridade_arquivos
- Arquivo: `codigo-sql/procedures/dbo.spu_integridade_arquivos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.falha_arquivos_importados [U]

### dbo.spu_integridade_infracoes
- Arquivo: `codigo-sql/procedures/dbo.spu_integridade_infracoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.falha_sequencia_imagem [U], dbo.imagem_info [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U], dbo.veiculo_imagem [U]

### dbo.spu_janela_processamento
- Arquivo: `codigo-sql/procedures/dbo.spu_janela_processamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @data_ini datetime, @data_fim datetime
- Referencia: dbo.infracao [U], dbo.processo [U], dbo.sis_usuario [U]

### dbo.spu_Limpa_Interacoes_Duplicadas_PPV
- Arquivo: `codigo-sql/procedures/dbo.spu_Limpa_Interacoes_Duplicadas_PPV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ppv_veiculo [U], dbo.ppv_veiculos_interacao_XML_detalhes [U], dbo.ppv_veiculos_interacoes_XML [U]

### dbo.spu_limpa_janela_antiga
- Arquivo: `codigo-sql/procedures/dbo.spu_limpa_janela_antiga.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dia_inativo int
- Referencia: dbo.infracao [U], dbo.infracao_processo [U], dbo.spu_ajusta_janela

### dbo.spu_limpa_janela_validacao
- Arquivo: `codigo-sql/procedures/dbo.spu_limpa_janela_validacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.infracao [U], dbo.infracao_janela [U], dbo.infracao_processo [U], dbo.infracao_remessa [U]

### dbo.spu_lista_arquivo_recebido
- Arquivo: `codigo-sql/procedures/dbo.spu_lista_arquivo_recebido.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @nome_arquivo varchar
- Referencia: dbo.arquivos [U]

### dbo.spu_lista_arquivos_recebidos
- Arquivo: `codigo-sql/procedures/dbo.spu_lista_arquivos_recebidos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @serie_equipamento int, @dias_atras int
- Referencia: dbo.arquivos [U]

### dbo.spu_listar_sugestoes_amostras
- Arquivo: `codigo-sql/procedures/dbo.spu_listar_sugestoes_amostras.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @id_pista tinyint, @dia date, @metrologica bit, @page_offset int, @page_limit int
- Referencia: dbo.fcn_lista_amostras_periodo_local_pista [IF], dbo.fcn_listar_sugestoes_amostras [IF]

### dbo.spu_log_atualiza_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_log_atualiza_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id int, @registros int
- Referencia: dbo.log_processos [U]

### dbo.spu_log_atualiza_processo_total
- Arquivo: `codigo-sql/procedures/dbo.spu_log_atualiza_processo_total.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id int, @registros int, @iteracoes int, @total int
- Referencia: dbo.log_processos [U]

### dbo.spu_log_detalhe
- Arquivo: `codigo-sql/procedures/dbo.spu_log_detalhe.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id int, @data datetime, @adicionados int, @arquivos int, @tempo_exec time
- Referencia: dbo.log_processos_detalhe [U]

### dbo.spu_log_finaliza_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_log_finaliza_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id int
- Referencia: dbo.log_processos [U]

### dbo.spu_log_inicia_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_log_inicia_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @nome varchar, @total int
- Referencia: dbo.log_processos [U]

### dbo.spu_manutencao_alterar
- Arquivo: `codigo-sql/procedures/dbo.spu_manutencao_alterar.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_manutencao int, @id_status int, @descricao varchar, @id_local int, @serie_equipamento int, @id_pista int, @tipo_grupo_autuador varchar, @data_ocorrencia datetime, @data_cadastro datetime, @data_inicio datetime, @data_previsto datetime, @id_ocorrencia int, @numero_oficio int, @ano_oficio int, @id_tecnico int, @id_auxiliar int, @data_conclusao datetime, @data_ultima_alteracao datetime, @id_ultimo_usuario int, @encaminhar bit
- Referencia: dbo.manutencao [U], dbo.v_enquadramentos_manutencao [V]

### dbo.spu_manutencao_comentario_inserir
- Arquivo: `codigo-sql/procedures/dbo.spu_manutencao_comentario_inserir.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_manutencao int, @id_usuario int, @comentario varchar
- Referencia: dbo.manutencao_comentarios [U]

### dbo.spu_manutencao_inserir
- Arquivo: `codigo-sql/procedures/dbo.spu_manutencao_inserir.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_status int, @descricao varchar, @id_local int, @serie_equipamento int, @id_pista int, @tipo_grupo_autuador varchar, @data_ocorrencia datetime, @data_cadastro datetime, @data_inicio datetime, @data_previsto datetime, @id_ocorrencia int, @numero_oficio int, @ano_oficio int, @id_tecnico int, @id_auxiliar int, @data_conclusao datetime, @data_ultima_alteracao datetime, @id_ultimo_usuario int, @encaminhar bit
- Referencia: dbo.manutencao [U], dbo.v_enquadramentos_manutencao [V]

### dbo.spu_Monitoramento
- Arquivo: `codigo-sql/procedures/dbo.spu_Monitoramento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.fcn_FormataNumero [FN], dbo.imagem_importacao [U], dbo.sp_send_dbmail, dbo.veiculo_estatistica [U], dbo.veiculo_importacao [U]

### dbo.spu_monitoramento_mapa
- Arquivo: `codigo-sql/procedures/dbo.spu_monitoramento_mapa.sql` · criado 2026-10-01 · alterado 2026-10-01
- Parâmetros/retorno: @equipamentos ListaEquipamentos
- Referencia: dbo.ListaEquipamentos, dbo.local_vigente [V], dbo.log_alerta [U]

### dbo.spu_mover_infracao_arquivo_morto
- Arquivo: `codigo-sql/procedures/dbo.spu_mover_infracao_arquivo_morto.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.infracao [U], dbo.spu_replica_erro

### dbo.spu_movimento_inserir
- Arquivo: `codigo-sql/procedures/dbo.spu_movimento_inserir.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_evento_conexao int, @id_equipamento int, @placa char, @data_movimento datetime, @data_recebido datetime
- Referencia: dbo.pmesp_movimento [V]

### dbo.spu_movimento_inserir_2017
- Arquivo: `codigo-sql/procedures/dbo.spu_movimento_inserir_2017.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_movimento bigint, @id_evento_conexao int, @id_equipamento int, @placa char, @data_movimento datetime, @data_recebido datetime, @data_transmitido datetime
- Referencia: dbo.pmesp_movimento_2017 [U]

### dbo.spu_muralha_info_mobile_boletim
- Arquivo: `codigo-sql/procedures/dbo.spu_muralha_info_mobile_boletim.sql` · criado 2025-06-20 · alterado 2025-07-02
- Parâmetros/retorno: @id_atendimento int, @id_atendimento_guarnicao int
- Referencia: muralha.atendimento [U], muralha.atendimento_guarnicao [U], muralha.boletim [U], muralha.boletim_local

### dbo.spu_muralha_info_mobile_ligacao
- Arquivo: `codigo-sql/procedures/dbo.spu_muralha_info_mobile_ligacao.sql` · criado 2025-06-21 · alterado 2025-08-05
- Parâmetros/retorno: @id_atendimento int, @id_atendimento_guarnicao int
- Referencia: muralha.atendimento [U], muralha.atendimento_guarnicao [U], muralha.cidade [U], muralha.fato [U], muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_individuo [U], muralha.registro_fato_individuo_tipo [U], muralha.registro_fato_tipo [U]

### dbo.spu_muralha_info_mobile_ocorrencia
- Arquivo: `codigo-sql/procedures/dbo.spu_muralha_info_mobile_ocorrencia.sql` · criado 2025-06-20 · alterado 2025-08-05
- Parâmetros/retorno: @id_atendimento int, @id_atendimento_guarnicao int
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.atendimento [U], muralha.atendimento_guarnicao [U], muralha.ocorrencia [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### dbo.spu_obter_id_veiculo_unic
- Arquivo: `codigo-sql/procedures/dbo.spu_obter_id_veiculo_unic.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @quantidade int
- Referencia: dbo.veiculo [U], dbo.veiculo_importacao [U]

### dbo.spu_obter_imagens_exportacao
- Arquivo: `codigo-sql/procedures/dbo.spu_obter_imagens_exportacao.sql` · criado 2026-09-03 · alterado 2026-09-03
- Parâmetros/retorno: @data_ini date, @data_fim date, @apenas_img_obj bit, @apenas_img_diurna bit, @id_classe char, @marca varchar, @modelo varchar, @ano_modelo int, @cor varchar, @serie_equipamento varchar, @ID_modelos varchar, @tipo_hatch_sedan varchar, @IDs_serie_equipamentos varchar
- Referencia: —

### dbo.spu_obter_limite_carga_PBT
- Arquivo: `codigo-sql/procedures/dbo.spu_obter_limite_carga_PBT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_classificacao char, @comprimento int
- Referencia: dbo.v_ppv_qfv [V]

### dbo.spu_ObterAmostraRemessa
- Arquivo: `codigo-sql/procedures/dbo.spu_ObterAmostraRemessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.amostragem [U], dbo.amostragem_nivel [U], dbo.amostragem_tamanho [U], dbo.chave_valor [U], dbo.enquadramento [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa_amostragem [U]

### dbo.spu_obterInfoVelocidadeEquipamento
- Arquivo: `codigo-sql/procedures/dbo.spu_obterInfoVelocidadeEquipamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Id_Local int, @Data date
- Referencia: dbo.configuracao_equipamento_velocidade_limite [U], dbo.descricao_pista_gst [U], dbo.local_pista_vigente [V], dbo.local_vigente [V], dbo.veiculo_pesquisa [V]

### dbo.spu_obterRemessasApagarHomologacao
- Arquivo: `codigo-sql/procedures/dbo.spu_obterRemessasApagarHomologacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.remessa [U], dbo.tmp_arquivos_ftp_hom, dbo.tmp_diretorios_ftp_hom

### dbo.spu_obterVeiculosGtwPorFiltros
- Arquivo: `codigo-sql/procedures/dbo.spu_obterVeiculosGtwPorFiltros.sql` · criado 2025-10-12 · alterado 2025-10-17
- Parâmetros/retorno: @placa varchar, @dataIni datetime, @dataFim datetime, @equipamentos varchar, @faixa varchar, @classificacao varchar, @buscarApenasVeiculoComImagem bit, @consultaMapa bit, @exportarConsulta bit, @offset int, @itensPorPagina int, @marca varchar, @modelo varchar, @idCor int, @anoFabricacao int, @anoModelo int, @renavam varchar, @chassi varchar, @idLocalidade int, @restricao varchar, @filtroPlaca int, @tipoPlaca varchar, @tipoVeiculo varchar, @deveAplicarFiltroDeImagens bit, @somenteUltimaPassagem bit, @com_pesagem bit, @apenas_veiculos_carga bit
- Referencia: —

### dbo.spu_pmesp_atualiza_atraso
- Arquivo: `codigo-sql/procedures/dbo.spu_pmesp_atualiza_atraso.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.pmesp_atraso [U], dbo.pmesp_evento_conexao [U], dbo.pmesp_movimento [V], dbo.v_locais_pmesp [V]

### dbo.spu_pmesp_atualiza_desconectado
- Arquivo: `codigo-sql/procedures/dbo.spu_pmesp_atualiza_desconectado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_PMESP_ObterTempoOffline [FN], dbo.pmesp_desconectado [U], dbo.v_locais_pmesp [V]

### dbo.spu_pmesp_atualiza_perda
- Arquivo: `codigo-sql/procedures/dbo.spu_pmesp_atualiza_perda.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.pmesp_movimento [V], dbo.pmesp_perda [U], dbo.v_locais_pmesp [V], dbo.veiculo [U], dbo.veiculo_estatistica [U]

### dbo.spu_popula_painel_contrato
- Arquivo: `codigo-sql/procedures/dbo.spu_popula_painel_contrato.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.painel_captura_excesso_eventos, dbo.painel_captura_iniciado, dbo.painel_captura_travado, dbo.painel_conexao_instavel, dbo.painel_contrato [U], dbo.painel_div_desconexao, dbo.painel_equipamento_offline, dbo.painel_erro_atualizacao_software, dbo.painel_erro_funcionamento_painel_controlador, dbo.painel_erro_leitura_arquivo_lote, dbo.painel_erro_no_div, dbo.painel_erro_resposta_polling, dbo.painel_falha_comunicacao_camera, dbo.painel_nao_envia_eventos, dbo.painel_sem_veiculos_validos, dbo.painel_ultima_deteccao, dbo.painel_ultima_infracao [V], dbo.spu_replica_erro, dbo.status_tempo_real [U]

### dbo.spu_popularTabelaReindex
- Arquivo: `codigo-sql/procedures/dbo.spu_popularTabelaReindex.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.reindex [U]

### dbo.spu_ppv_sis_usuario_token
- Arquivo: `codigo-sql/procedures/dbo.spu_ppv_sis_usuario_token.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @endereco_ip varchar, @ret uniqueidentifier OUT
- Referencia: dbo.sis_usuario_token [U]

### dbo.spu_ppv_sis_usuario_token_encerra
- Arquivo: `codigo-sql/procedures/dbo.spu_ppv_sis_usuario_token_encerra.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @token uniqueidentifier
- Referencia: dbo.sis_usuario_token [U]

### dbo.spu_ppv_sis_usuario_token_valida
- Arquivo: `codigo-sql/procedures/dbo.spu_ppv_sis_usuario_token_valida.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @token uniqueidentifier, @endereco_ip varchar
- Referencia: dbo.sis_usuario_token [U]

### dbo.spu_processa_filtros
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_filtros.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @id_usuario int
- Referencia: dbo.infracao [U], dbo.spu_ajusta_janela, dbo.spu_busca_infracao_processamento, dbo.spu_processa_infracao_filtro, dbo.spu_verificar_filtros

### dbo.spu_processa_imagens_teste
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_imagens_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @id_usuario int, @data_fim datetime
- Referencia: dbo.chave_valor [U], dbo.spu_ajusta_janela, dbo.spu_busca_infracao_processamento, dbo.spu_processa_infracao_direto, dbo.spu_replica_erro

### dbo.spu_processa_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @id_inconsistencia int, @id_imagem int, @x_obliteracao int, @y_obliteracao int, @largura_obliteracao int, @altura_obliteracao int, @tempo_proc int, @tempo_cli int, @codigoAgenteDigitado int, @AgenteDigitado varchar, @data_proc datetime, @status_processo int, @observacao varchar
- Referencia: dbo.infracao [U], dbo.infracao_janela [U], dbo.infracao_processo [U], dbo.infracao_processo_obliteracao [U], dbo.infracao_processo_observacao [U], dbo.infracao_processo_usuario_digitado [U], dbo.spu_replica_erro, dbo.veiculo_imagem [U]

### dbo.spu_processa_infracao_agendamento
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao_agendamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @id_inconsistencia int
- Referencia: dbo.agendamento_processamento [U], dbo.spu_processa_infracao_direto, dbo.spu_remover_agendamento, dbo.spu_replica_erro, dbo.spu_status_infracao

### dbo.spu_processa_infracao_com_digitacao
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao_com_digitacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @id_inconsistencia int, @id_imagem int, @x_obliteracao int, @y_obliteracao int, @largura_obliteracao int, @altura_obliteracao int, @placa char, @id_marca_processo int, @id_especie_processo int, @uf_processo varchar, @tempo_proc int, @tempo_cli int, @codigoAgenteDigitado int, @AgenteDigitado varchar, @observacao varchar, @classificacao_veiculo_processo varchar, @data_proc datetime
- Referencia: dbo.cad_especie_processo [U], dbo.cad_localidade [U], dbo.cad_marca_cet_processo [U], dbo.cad_uf_processo [U], dbo.cad_veiculo [U], dbo.infracao [U], dbo.infracao_processo_digitacao [U], dbo.spu_processa_infracao, dbo.spu_replica_erro, dbo.veiculo [U]

### dbo.spu_processa_infracao_contestacao
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao_contestacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @decisao int
- Referencia: dbo.contestacao_ligacao [U], dbo.infracao [U], dbo.infracao_contestacao [U], dbo.infracao_janela [U], dbo.infracao_processo [U], dbo.infracao_processo_contestacao [U], dbo.spu_replica_erro

### dbo.spu_processa_infracao_direto
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao_direto.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @id_inconsistencia int
- Referencia: dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_obliteracao [U], dbo.spu_conclui_infracao_processo, dbo.spu_processa_infracao, dbo.spu_processa_obliteracao, dbo.spu_replica_erro

### dbo.spu_processa_infracao_direto_digitacao
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao_direto_digitacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @id_inconsistencia int, @placa char, @id_marca_processo int, @id_especie_processo int, @uf_processo int
- Referencia: dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_obliteracao [U], dbo.spu_conclui_infracao_processo, dbo.spu_processa_infracao_com_digitacao, dbo.spu_replica_erro

### dbo.spu_processa_infracao_filtro
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_infracao_filtro.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_usuario int, @id_processo int, @id_inconsistencia int, @espera bit, @id_filtro int
- Referencia: dbo.infracao_processo_filtro [U], dbo.spu_espera_infracao, dbo.spu_processa_infracao

### dbo.spu_processa_obliteracao
- Arquivo: `codigo-sql/procedures/dbo.spu_processa_obliteracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao_processo int, @id_imagem int, @sequencia_obliteracao int, @x_obliteracao int, @y_obliteracao int, @largura_obliteracao int, @altura_obliteracao int
- Referencia: dbo.infracao_processo_obliteracao [U], dbo.spu_replica_erro

### dbo.spu_ProcessarAutomatico
- Arquivo: `codigo-sql/procedures/dbo.spu_ProcessarAutomatico.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int
- Referencia: dbo.infracao [U], dbo.infracao_imagem [U], dbo.spu_processa_infracao, dbo.spu_status_infracao

### dbo.spu_ProcessarAutomaticoPPV
- Arquivo: `codigo-sql/procedures/dbo.spu_ProcessarAutomaticoPPV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int
- Referencia: dbo.infracao [U], dbo.infracao_imagem [U], dbo.spu_processa_infracao, dbo.spu_status_infracao, dbo.veiculo [U]

### dbo.spu_qtde_aproximada_lotes
- Arquivo: `codigo-sql/procedures/dbo.spu_qtde_aproximada_lotes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @tamanho_lotes int, @id_processo_remessa int, @data_inicial datetime, @data_final datetime
- Referencia: dbo.enquadramento [U], dbo.infracao [U], dbo.infracao_remessa [U]

### dbo.spu_readic_imagem
- Arquivo: `codigo-sql/procedures/dbo.spu_readic_imagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_imagem int, @md5_imagem char, @conteudo varbinary
- Referencia: dbo.imagem [U], dbo.imagem_info [U], dbo.veiculo_imagem [U]

### dbo.spu_regras_importacao
- Arquivo: `codigo-sql/procedures/dbo.spu_regras_importacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao_importacao [U], dbo.regra_importacao_veiculo [U], dbo.regras_importacao [U], dbo.veiculo_importacao [U]

### dbo.spu_relatorio_aproveitamento_semanal_datas
- Arquivo: `codigo-sql/procedures/dbo.spu_relatorio_aproveitamento_semanal_datas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getUltimaSemanaProcessada [FN], dbo.relatorio_aproveitamento_semanal [V]

### dbo.spu_relatorio_aproveitamento_semanal_datas_ini_fim
- Arquivo: `codigo-sql/procedures/dbo.spu_relatorio_aproveitamento_semanal_datas_ini_fim.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getUltimaSemanaProcessada [FN]

### dbo.spu_RelatorioObterEventosCiclo
- Arquivo: `codigo-sql/procedures/dbo.spu_RelatorioObterEventosCiclo.sql` · criado 2025-10-05 · alterado 2025-10-05
- Parâmetros/retorno: @placa varchar, @data_ini datetime, @data_fim datetime
- Referencia: muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### dbo.spu_RelatorioObterEventosCiclom
- Arquivo: `codigo-sql/procedures/dbo.spu_RelatorioObterEventosCiclom.sql` · criado 2025-10-05 · alterado 2025-10-05
- Parâmetros/retorno: @placa varchar, @data_ini datetime, @data_fim datetime
- Referencia: muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### dbo.spu_RelatorioObterResumoDiario
- Arquivo: `codigo-sql/procedures/dbo.spu_RelatorioObterResumoDiario.sql` · criado 2025-10-05 · alterado 2025-10-05
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime
- Referencia: muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### dbo.spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhadoTESTE
- Arquivo: `codigo-sql/procedures/dbo.spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhadoTESTE.sql` · criado 2025-10-06 · alterado 2025-10-06
- Parâmetros/retorno: @area_monitorada varchar, @placa varchar, @data_ini date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### dbo.spu_remove_cad_isento_antigo
- Arquivo: `codigo-sql/procedures/dbo.spu_remove_cad_isento_antigo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_arquivos_importados [U], dbo.cad_isento [U], dbo.fcn_FormataNumero [FN], dbo.spu_replica_erro

### dbo.spu_remove_config_importacao_antigo
- Arquivo: `codigo-sql/procedures/dbo.spu_remove_config_importacao_antigo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.cei, dbo.configuracao_equipamento_importacao [U], dbo.fcn_FormataNumero [FN], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.tmp_config_equip_importacao_remover, dbo.veiculo_importacao [U]

### dbo.spu_remove_imagens_CAI
- Arquivo: `codigo-sql/procedures/dbo.spu_remove_imagens_CAI.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_FormataNumero [FN], dbo.imagem [U], dbo.imagem_removida [U], dbo.infracao [U], dbo.infracao_contestacao [U], dbo.infracao_obliteracao [U], dbo.infracao_processo_obliteracao [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.veiculo_imagem [U]

### dbo.spu_remove_imagens_teste_CAI
- Arquivo: `codigo-sql/procedures/dbo.spu_remove_imagens_teste_CAI.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_FormataNumero [FN], dbo.imagem [U], dbo.imagem_removida [U], dbo.infracao [U], dbo.infracao_obliteracao [U], dbo.infracao_processo_obliteracao [U], dbo.infracao_remessa [U], dbo.spu_log_atualiza_processo, dbo.spu_log_finaliza_processo, dbo.spu_log_inicia_processo, dbo.spu_replica_erro, dbo.veiculo [U], dbo.veiculo_imagem [U]

### dbo.spu_remover_agendamento
- Arquivo: `codigo-sql/procedures/dbo.spu_remover_agendamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.agendamento_processamento [U], dbo.spu_replica_erro

### dbo.spu_remover_imagens_pan_velocidade
- Arquivo: `codigo-sql/procedures/dbo.spu_remover_imagens_pan_velocidade.sql` · criado 2025-10-14 · alterado 2025-10-15
- Parâmetros/retorno: —
- Referencia: dbo.bkp_ipatinga_imagem [U], dbo.bkp_ipatinga_imagem_info [U], dbo.bkp_ipatinga_infracao_imagem [U], dbo.bkp_ipatinga_infracao_obliteracao [U], dbo.bkp_ipatinga_infracao_processo_concluido [U], dbo.bkp_ipatinga_veiculo_imagem [U], dbo.i, dbo.imagem [U], dbo.imagem_info [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_obliteracao [U], dbo.infracao_processo_concluido [U], dbo.spu_replica_erro, dbo.tipo_imagem [U], dbo.veiculo_imagem [U]

### dbo.spu_replica_erro
- Arquivo: `codigo-sql/procedures/dbo.spu_replica_erro.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: —

### dbo.spu_reposiciona_infracao_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_reposiciona_infracao_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @novo_id_processo int
- Referencia: dbo.descarga_infracao [V], dbo.infracao [U], dbo.infracao_obliteracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.processo [U], dbo.spu_replica_erro, dbo.spu_status_infracao

### dbo.spu_reposiciona_infracoes_sem_escala
- Arquivo: `codigo-sql/procedures/dbo.spu_reposiciona_infracoes_sem_escala.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data date, @id_local int
- Referencia: dbo.infracao [U], dbo.infracao_processo [U], dbo.processo [U], dbo.sis_usuario [U]

### dbo.spu_reposiciona_lote_reprovado
- Arquivo: `codigo-sql/procedures/dbo.spu_reposiciona_lote_reprovado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.lote_reprovado [U], dbo.remessa [U], dbo.spu_replica_erro, dbo.spu_reposiciona_infracao_processo

### dbo.spu_ReposicionaInfracoesRemessa
- Arquivo: `codigo-sql/procedures/dbo.spu_ReposicionaInfracoesRemessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_ant int, @id_processo int, @id_remessa int
- Referencia: dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_processo [U], dbo.infracao_remessa [U], dbo.remessa [U]

### dbo.spu_reprocessar_filtro_triagem_espera
- Arquivo: `codigo-sql/procedures/dbo.spu_reprocessar_filtro_triagem_espera.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.agenda_estatico [U], dbo.agenda_estatico_item [U], dbo.infracao [U], dbo.veiculo [U]

### dbo.spu_retornar_modo_operacao_PPV
- Arquivo: `codigo-sql/procedures/dbo.spu_retornar_modo_operacao_PPV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getModoOperacaoGeralPPV [IF], dbo.fcn_getModoOperacaoPesagemPPV [IF], dbo.ppv_ocorrencias [U], dbo.spu_replica_erro

### dbo.spu_status_importacao
- Arquivo: `codigo-sql/procedures/dbo.spu_status_importacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_importados [U], dbo.imagem_importacao [U], dbo.veiculo_importacao [U]

### dbo.spu_status_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_status_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.infracao [U], dbo.infracao_janela [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.infracao_processo_digitacao [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.spu_ajusta_infracao, dbo.spu_getProximoProcesso [P], dbo.spu_replica_erro, dbo.veiculo [U]

### dbo.spu_sugestao_criacao_indices_ausentes
- Arquivo: `codigo-sql/procedures/dbo.spu_sugestao_criacao_indices_ausentes.sql` · criado 2026-04-16 · alterado 2026-04-16
- Parâmetros/retorno: —
- Referencia: —

### dbo.spu_sumariza_veiculos
- Arquivo: `codigo-sql/procedures/dbo.spu_sumariza_veiculos.sql` · criado 2025-06-02 · alterado 2025-07-02
- Parâmetros/retorno: @data_inicio date, @data_fim date
- Referencia: dbo.veiculo_pesquisa [V], dbo.veiculo_sumarizado [U]

### dbo.spu_sumariza_veiculos_faixa_velocidade
- Arquivo: `codigo-sql/procedures/dbo.spu_sumariza_veiculos_faixa_velocidade.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_inicio date, @data_fim date
- Referencia: dbo.spu_replica_erro, dbo.v_veiculo_sumarizado_faixa_velocidade [V], dbo.veiculo_sumarizado_faixa_velocidade [U]

### dbo.spu_sumariza_veiculos_faixa_velocidade_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_sumariza_veiculos_faixa_velocidade_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: @data_inicio date, @data_fim date
- Referencia: dbo.spu_replica_erro, dbo.v_veiculo_sumarizado_faixa_velocidade_temp [V], dbo.veiculo_sumarizado_faixa_velocidade [U]

### dbo.spu_sumariza_veiculos_relatorio
- Arquivo: `codigo-sql/procedures/dbo.spu_sumariza_veiculos_relatorio.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_inicio date, @data_fim date
- Referencia: dbo.spu_replica_erro, dbo.v_veiculo_sumarizado_relatorio [V], dbo.veiculo_sumarizado_relatorio [U]

### dbo.spu_sumariza_veiculos_relatorio_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_sumariza_veiculos_relatorio_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: @data_inicio date, @data_fim date
- Referencia: dbo.spu_replica_erro, dbo.v_veiculo_sumarizado_relatorio_temp [V], dbo.veiculo_sumarizado_relatorio [U]

### dbo.spu_sumariza_veiculos_temp
- Arquivo: `codigo-sql/procedures/dbo.spu_sumariza_veiculos_temp.sql` · criado 2025-10-20 · alterado 2025-10-20
- Parâmetros/retorno: @data_inicio date, @data_fim date
- Referencia: dbo.veiculo_pesquisa_temp [V], dbo.veiculo_sumarizado [U]

### dbo.spu_update_statistics
- Arquivo: `codigo-sql/procedures/dbo.spu_update_statistics.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @MinModificationPercent float, @MinRowCount bigint, @SamplePercent int, @UseFullScan bit, @MaxDOP int, @MaintenanceWindowEnd datetime, @Debug bit
- Referencia: —

### dbo.spu_update_statistics_20250527
- Arquivo: `codigo-sql/procedures/dbo.spu_update_statistics_20250527.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: —

### dbo.spu_valida_entrada_infracao
- Arquivo: `codigo-sql/procedures/dbo.spu_valida_entrada_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.cad_isento [U], dbo.cad_veiculo [U], dbo.configuracao_equipamento [U], dbo.infracao [U], dbo.local [U], dbo.veiculo [U]

### dbo.spu_valida_infracao_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_valida_infracao_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao_processo int
- Referencia: dbo.alerta [U], dbo.filtro [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_completa [V], dbo.infracao_processo [U], dbo.spu_replica_erro, dbo.spu_verificar_filtros

### dbo.spu_valida_processo
- Arquivo: `codigo-sql/procedures/dbo.spu_valida_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao_processo int
- Referencia: dbo.configuracao_equipamento_afericao [U], dbo.enquadramento [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.local [U], dbo.processo [U], dbo.sis_usuario [U]

### dbo.spu_valida_processo_digitacao
- Arquivo: `codigo-sql/procedures/dbo.spu_valida_processo_digitacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao_processo int
- Referencia: dbo.cad_isento [U], dbo.cad_modalidade_isento [U], dbo.cad_veiculo [U], dbo.configuracao_equipamento_afericao [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_digitacao [U], dbo.local [U], dbo.veiculo [U], ip.id_infracao_processo

### dbo.spu_veiculo_tempo_real
- Arquivo: `codigo-sql/procedures/dbo.spu_veiculo_tempo_real.sql` · criado 2025-06-02 · alterado 2026-10-01
- Parâmetros/retorno: @id uniqueidentifier, @placa char, @data datetime, @id_local int, @id_pista tinyint, @velocidade smallint, @enviado_cliente bit, @data_enviado datetime, @classificacao char, @estado_veiculo tinyint, @perfil_1 varchar, @perfil_2 varchar, @placa_frontal varchar, @info_adicional varchar
- Referencia: muralha.veiculo_tempo_real [U]

### dbo.spu_veiculo_tempo_real_estado_veiculo
- Arquivo: `codigo-sql/procedures/dbo.spu_veiculo_tempo_real_estado_veiculo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @estado_veiculo tinyint, @id uniqueidentifier
- Referencia: muralha.veiculo_tempo_real [U]

### dbo.spu_veiculo_tempo_real_imagem
- Arquivo: `codigo-sql/procedures/dbo.spu_veiculo_tempo_real_imagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id uniqueidentifier, @id_veiculo_tempo_real uniqueidentifier, @imagem image, @indice_imagem tinyint
- Referencia: muralha.veiculo_tempo_real_imagem [U]

### dbo.spu_VeiculoEstatistica_ApagarSumarizados
- Arquivo: `codigo-sql/procedures/dbo.spu_VeiculoEstatistica_ApagarSumarizados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_sumariza_veiculos, dbo.veiculo_estatistica [U], dbo.veiculo_sumarizado [U]

### dbo.spu_VeiculoEstatistica_ApagarSumarizados_ALT
- Arquivo: `codigo-sql/procedures/dbo.spu_VeiculoEstatistica_ApagarSumarizados_ALT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_sumariza_veiculos, dbo.veiculo_estatistica_01, dbo.veiculo_sumarizado [U]

### dbo.spu_verificar_dados_importacao
- Arquivo: `codigo-sql/procedures/dbo.spu_verificar_dados_importacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.fcn_getDestinatariosAlerta [FN], dbo.fcn_getNomeContrato [FN], dbo.sp_send_dbmail, dbo.veiculo [U], dbo.veiculo_estatistica [U], dbo.veiculo_importacao [U], dbo.veiculo_sumarizado [U]

### dbo.spu_verificar_filtros
- Arquivo: `codigo-sql/procedures/dbo.spu_verificar_filtros.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int, @id_processo int, @id_filtro_aplicado int OUT, @id_inconsistencia int OUT, @espera bit OUT
- Referencia: dbo.fcn_getSqlVerificaFiltros [FN], dbo.filtro [U], dbo.infracao [U]

### dbo.spuObterDadosVeiculoGtw
- Arquivo: `codigo-sql/procedures/dbo.spuObterDadosVeiculoGtw.sql` · criado 2025-10-12 · alterado 2025-10-14
- Parâmetros/retorno: @id_veiculo_unic bigint
- Referencia: dbo.classe_veiculo [U], dbo.imagem [U], dbo.local_pista_vigente [V], dbo.v_veiculo_pesagem [V], dbo.v_veiculo_pesagem_distancia_eixos [V], dbo.v_veiculo_pesagem_eixo [V], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_pesquisa [V]

### muralha.analise_automatica_correlacionamento
- Arquivo: `codigo-sql/procedures/muralha.analise_automatica_correlacionamento.sql` · criado 2026-05-13 · alterado 2026-05-29
- Parâmetros/retorno: —
- Referencia: muralha.config_chave_valor [U], muralha.correlacionamento_automatico [U], muralha.correlacionamento_automatico_placa [U], muralha.correlacionamento_automatico_placa_invalida [U], muralha.correlacionamento_automatico_processamento [U], muralha.registro_fato [U], muralha.registro_fato_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.gerenciar_passagem_correlacionamento
- Arquivo: `codigo-sql/procedures/muralha.gerenciar_passagem_correlacionamento.sql` · criado 2026-05-28 · alterado 2026-07-13
- Parâmetros/retorno: @idCorrelacionamento int, @idPassagemPlacaAlvo uniqueidentifier, @idPassagemPlacaCorrelacionada uniqueidentifier, @idMotivoInvalido int, @idUsuario int
- Referencia: muralha.correlacionamento_automatico [U], muralha.correlacionamento_automatico_placa [U], muralha.correlacionamento_automatico_placa_invalida [U]

### muralha.sp_MonitoramentoAreaMonitorada_V2
- Arquivo: `codigo-sql/procedures/muralha.sp_MonitoramentoAreaMonitorada_V2.sql` · criado 2025-10-06 · alterado 2025-10-06
- Parâmetros/retorno: @area_monitorada varchar, @placa varchar, @data_ini date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.sp_obter_passagens_veiculo
- Arquivo: `codigo-sql/procedures/muralha.sp_obter_passagens_veiculo.sql` · criado 2025-09-12 · alterado 2025-09-12
- Parâmetros/retorno: @placa nvarchar, @data_inicio datetime, @data_final datetime
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.sp_PermanenciaAreasMonitoradas
- Arquivo: `codigo-sql/procedures/muralha.sp_PermanenciaAreasMonitoradas.sql` · criado 2025-10-03 · alterado 2025-10-03
- Parâmetros/retorno: @area int, @placa varchar, @data_inicio date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.sp_PermanenciaAreasMonitoradasNew
- Arquivo: `codigo-sql/procedures/muralha.sp_PermanenciaAreasMonitoradasNew.sql` · criado 2025-10-03 · alterado 2025-10-03
- Parâmetros/retorno: @area int, @placa varchar, @data_inicio date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.SP_RelatorioEstatisticoAlarmes
- Arquivo: `codigo-sql/procedures/muralha.SP_RelatorioEstatisticoAlarmes.sql` · criado 2025-09-18 · alterado 2025-09-18
- Parâmetros/retorno: @DataInicio date, @DataFim date, @IdLocal int, @IdTipoAlerta uniqueidentifier, @TipoRelatorio varchar
- Referencia: dbo.local [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.sp_RelatorioFatosPendentes
- Arquivo: `codigo-sql/procedures/muralha.sp_RelatorioFatosPendentes.sql` · criado 2025-09-13 · alterado 2025-09-13
- Parâmetros/retorno: @data_inicio datetime, @data_fim datetime
- Referencia: dbo.sis_usuario [U], muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_natureza [U], muralha.registro_fato_status [U], muralha.registro_fato_tipo [U]

### muralha.sp_RelatorioFatosPendentesData
- Arquivo: `codigo-sql/procedures/muralha.sp_RelatorioFatosPendentesData.sql` · criado 2025-09-13 · alterado 2025-09-13
- Parâmetros/retorno: @data_inicio datetime, @data_fim datetime
- Referencia: —

### muralha.sp_RelatorioFluxoVeicularDinamico
- Arquivo: `codigo-sql/procedures/muralha.sp_RelatorioFluxoVeicularDinamico.sql` · criado 2025-09-13 · alterado 2025-09-13
- Parâmetros/retorno: @PontoColetaOrigem varchar, @PontoColetaDestino varchar, @DataInicio datetime, @DataFim datetime
- Referencia: dbo.T1, muralha.veiculo_tempo_real [U]

### muralha.sp_RelatorioPendenciasRegistroFato
- Arquivo: `codigo-sql/procedures/muralha.sp_RelatorioPendenciasRegistroFato.sql` · criado 2025-09-13 · alterado 2025-09-13
- Parâmetros/retorno: @data_inicio date, @data_fim date, @tipo_falta varchar, @somente_privados bit
- Referencia: —

### muralha.spu_atualizar_envio_tempo_real_equip
- Arquivo: `codigo-sql/procedures/muralha.spu_atualizar_envio_tempo_real_equip.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.spu_replica_erro, muralha.config_envio_tempo_real_equipamento [U], muralha.config_intervalo_envio_tempo_real [U]

### muralha.spu_cadastrar_equipamento_tempo_real
- Arquivo: `codigo-sql/procedures/muralha.spu_cadastrar_equipamento_tempo_real.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.spu_replica_erro, muralha.config_envio_tempo_real_equipamento [U]

### muralha.spu_cadastrar_local_municipio_regiao
- Arquivo: `codigo-sql/procedures/muralha.spu_cadastrar_local_municipio_regiao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_municipio_regiao [U], dbo.local_vigente [V], dbo.spu_replica_erro, muralha.fcn_LocalidadeContrato [IF]

### muralha.spu_captura_comboio
- Arquivo: `codigo-sql/procedures/muralha.spu_captura_comboio.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @cad_veiculo_monitorado char
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.config_alerta_comboio [U], muralha.controla_execucao_job [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_captura_ocorrencia
- Arquivo: `codigo-sql/procedures/muralha.spu_captura_ocorrencia.sql` · criado 2025-06-02 · alterado 2025-07-02
- Parâmetros/retorno: —
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.spu_captura_comboio [P], muralha.spu_captura_roubo_a_banco [P], muralha.spu_captura_transporte_clandestino [P], muralha.tipo_alerta_ocorrencia [U]

### muralha.spu_captura_roubo_a_banco
- Arquivo: `codigo-sql/procedures/muralha.spu_captura_roubo_a_banco.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @cad_veiculo_monitorado char
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.config_alerta_comboio [U], muralha.controla_execucao_Job [U], muralha.ponto_interesse [U], muralha.ponto_interesse_equipamentos [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_captura_transporte_clandestino
- Arquivo: `codigo-sql/procedures/muralha.spu_captura_transporte_clandestino.sql` · criado 2025-06-02 · alterado 2025-07-02
- Parâmetros/retorno: @cad_veiculo_monitorado char
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_exclusao [U], muralha.config_alerta_transp_clandestino [U], muralha.controla_execucao_Job [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_ContagemPassagensPorLocal
- Arquivo: `codigo-sql/procedures/muralha.spu_ContagemPassagensPorLocal.sql` · criado 2026-04-20 · alterado 2026-04-29
- Parâmetros/retorno: @data_ini datetime2, @data_fim datetime2, @lista nvarchar
- Referencia: muralha.veiculo_tempo_real [U], v.value

### muralha.spu_correlacionamento_placas
- Arquivo: `codigo-sql/procedures/muralha.spu_correlacionamento_placas.sql` · criado 2025-07-01 · alterado 2026-05-05
- Parâmetros/retorno: @placa_informada varchar, @data_inicio datetime, @data_final datetime, @tempo_passagem_minutos int, @considerar_antes_depois bit, @num_min_passagens_correlacionadas int
- Referencia: dbo.classe_veiculo [U], dbo.res, dbo.rf, muralha.alerta [U], muralha.cad_veiculo_monitorado [U], muralha.fcn_perfil_comportamental_base_locais_com_mancha [IF], muralha.fcn_perfil_comportamental_estadia_por_manchas [IF], muralha.registro_fato [U], muralha.registro_fato_documento [U], muralha.registro_fato_endereco [U], muralha.registro_fato_objeto [U], muralha.registro_fato_tipo [U], muralha.registro_fato_veiculo [U], muralha.spu_correlacionamento_placas_base [P], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_correlacionamento_placas_base
- Arquivo: `codigo-sql/procedures/muralha.spu_correlacionamento_placas_base.sql` · criado 2025-09-04 · alterado 2026-05-05
- Parâmetros/retorno: @placa_informada varchar, @data_inicio datetime, @data_final datetime, @tempo_passagem_minutos int, @considerar_antes_depois bit, @num_min_passagens_correlacionadas int
- Referencia: dbo.cadastro_veiculo [V], muralha.alerta [U], muralha.antecedentes_criminais [U], muralha.cad_veiculo_monitorado [U], muralha.config_chave_valor [U], muralha.proprietario_veiculo [U], muralha.registro_fato_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_correlacionamento_placas_especifico
- Arquivo: `codigo-sql/procedures/muralha.spu_correlacionamento_placas_especifico.sql` · criado 2026-03-17 · alterado 2026-04-09
- Parâmetros/retorno: @placa_base varchar, @placa_correlacionada varchar, @data_inicio datetime, @data_final datetime, @tempo_passagem_minutos int, @considerar_antes_depois bit, @offset int, @itens_por_pagina int
- Referencia: dbo.local_pista_vigente [V], muralha.veiculo_tempo_real [U]

### muralha.spu_correlacionamento_placas_eventos
- Arquivo: `codigo-sql/procedures/muralha.spu_correlacionamento_placas_eventos.sql` · criado 2026-03-09 · alterado 2026-03-09
- Parâmetros/retorno: @data_inicio datetime, @data_final datetime, @tempo_passagem_minutos int, @considerar_antes_depois bit
- Referencia: dbo.cadastro_veiculo [V], muralha.alerta [U], muralha.antecedentes_criminais [U], muralha.cad_veiculo_monitorado [U], muralha.proprietario_veiculo [U], muralha.registro_fato_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_correlacionamento_placas_NEW_PERFORMANCE
- Arquivo: `codigo-sql/procedures/muralha.spu_correlacionamento_placas_NEW_PERFORMANCE.sql` · criado 2026-02-25 · alterado 2026-02-25
- Parâmetros/retorno: @placa_informada varchar, @data_pesquisa varchar, @considerar_antes_depois bit
- Referencia: muralha.veiculo_tempo_real [U]

### muralha.spu_correlacionamento_placas_registro_fato
- Arquivo: `codigo-sql/procedures/muralha.spu_correlacionamento_placas_registro_fato.sql` · criado 2026-03-09 · alterado 2026-05-07
- Parâmetros/retorno: @data_inicio datetime, @data_final datetime, @tempo_passagem_minutos int, @considerar_antes_depois bit, @num_min_correlacoes int, @incluir_abaixo_minimo bit
- Referencia: dbo.cadastro_veiculo [V], muralha.alerta [U], muralha.analise_correlacionamento_placas_registro_fato, muralha.antecedentes_criminais [U], muralha.cad_veiculo_monitorado [U], muralha.correlacionamento_placas, muralha.proprietario_veiculo [U], muralha.registro_fato [U], muralha.registro_fato_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_encerrar_atendimento
- Arquivo: `codigo-sql/procedures/muralha.spu_encerrar_atendimento.sql` · criado 2025-06-13 · alterado 2025-07-02
- Parâmetros/retorno: @idAtendimento int, @idGuarnicao int
- Referencia: muralha.atendimento [U], muralha.atendimento_guarnicao [U], muralha.guarnicao [U]

### muralha.spu_FaltasRegistroFato
- Arquivo: `codigo-sql/procedures/muralha.spu_FaltasRegistroFato.sql` · criado 2025-09-15 · alterado 2025-09-15
- Parâmetros/retorno: @data_inicio datetime, @data_fim datetime
- Referencia: dbo.sis_usuario [U], muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_natureza [U], muralha.registro_fato_status [U], muralha.registro_fato_tipo [U]

### muralha.spu_gerar_ocorrencia
- Arquivo: `codigo-sql/procedures/muralha.spu_gerar_ocorrencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @idOcorrencia uniqueidentifier, @idAlerta uniqueidentifier, @idTipoAlerta uniqueidentifier, @idStatusAlertaOcorrenciaGerada uniqueidentifier, @idStatusOcorrenciaPendente uniqueidentifier, @data datetime, @idUsuario int, @alertaVinculado bit, @idAlertaVinculado uniqueidentifier
- Referencia: dbo.spu_replica_erro, muralha.alerta [U], muralha.ocorrencia [U]

### muralha.spu_getCalendarioIntensidade
- Arquivo: `codigo-sql/procedures/muralha.spu_getCalendarioIntensidade.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U], dbo.veiculo_sumarizado [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_getComparativoFluxoInfracao
- Arquivo: `codigo-sql/procedures/muralha.spu_getComparativoFluxoInfracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getComparativoPeriodoAnoAnterior
- Arquivo: `codigo-sql/procedures/muralha.spu_getComparativoPeriodoAnoAnterior.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getComparativoPeriodoMesAnterior
- Arquivo: `codigo-sql/procedures/muralha.spu_getComparativoPeriodoMesAnterior.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getDistribuicaoPorFaixaRolagem
- Arquivo: `codigo-sql/procedures/muralha.spu_getDistribuicaoPorFaixaRolagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getEvolucaoPorClassificacao
- Arquivo: `codigo-sql/procedures/muralha.spu_getEvolucaoPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getFluxoDiaHorarioGrafico
- Arquivo: `codigo-sql/procedures/muralha.spu_getFluxoDiaHorarioGrafico.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: dbo.fcn_ObterDatasHorasPeriodo [IF]

### muralha.spu_getFluxoDiaMinutoGrafico
- Arquivo: `codigo-sql/procedures/muralha.spu_getFluxoDiaMinutoGrafico.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: dbo.fcn_ObterDatasMinutosPeriodo [IF]

### muralha.spu_getFluxoEquipamentoPorCategoria
- Arquivo: `codigo-sql/procedures/muralha.spu_getFluxoEquipamentoPorCategoria.sql` · criado 2025-06-02 · alterado 2025-07-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U], dbo.veiculo_sumarizado [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_getGraficoPrevisaoFutura
- Arquivo: `codigo-sql/procedures/muralha.spu_getGraficoPrevisaoFutura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getInfracoesDiaGrafico
- Arquivo: `codigo-sql/procedures/muralha.spu_getInfracoesDiaGrafico.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: dbo.fcn_ObterDatasPeriodo [IF]

### muralha.spu_getQtdeFluxoFaixaVelocidade
- Arquivo: `codigo-sql/procedures/muralha.spu_getQtdeFluxoFaixaVelocidade.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: dbo.faixa_velocidade [U]

### muralha.spu_getQtdeFluxoPorPorteVeicular
- Arquivo: `codigo-sql/procedures/muralha.spu_getQtdeFluxoPorPorteVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: muralha.v_porte_veiculo_ref [V]

### muralha.spu_getRankingPorFaixaRolagem
- Arquivo: `codigo-sql/procedures/muralha.spu_getRankingPorFaixaRolagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.local_pista_vigente [V], dbo.veiculo [U], dbo.veiculo_sumarizado [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_getRelExtratoAlertaOcorrencia
- Arquivo: `codigo-sql/procedures/muralha.spu_getRelExtratoAlertaOcorrencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime, @dataFim datetime
- Referencia: muralha.alerta [U], muralha.tipo_alerta_ocorrencia [U]

### muralha.spu_getRelExtratoAlertaOcorrenciaPorEquipamento
- Arquivo: `codigo-sql/procedures/muralha.spu_getRelExtratoAlertaOcorrenciaPorEquipamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime, @dataFim datetime
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_getTotalizadorPorCategoria
- Arquivo: `codigo-sql/procedures/muralha.spu_getTotalizadorPorCategoria.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Tipo_Info tinyint, @Id_Municipio int, @Id_Regiao tinyint
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U], dbo.veiculo_sumarizado [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_getVeiculosPorClassificacao
- Arquivo: `codigo-sql/procedures/muralha.spu_getVeiculosPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int
- Referencia: dbo.fcn_ObterDatasMinutosPeriodo [IF]

### muralha.spu_getVeiculosPorPeriodo
- Arquivo: `codigo-sql/procedures/muralha.spu_getVeiculosPorPeriodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Tipo int
- Referencia: dbo.fcn_ObterDatasMinutosPeriodo [IF]

### muralha.spu_getVelociadeMediaPorPeriodo
- Arquivo: `codigo-sql/procedures/muralha.spu_getVelociadeMediaPorPeriodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Tipo int
- Referencia: dbo.fcn_ObterDatasMinutosPeriodo [IF]

### muralha.spu_marcar_alerta_blitz_processado
- Arquivo: `codigo-sql/procedures/muralha.spu_marcar_alerta_blitz_processado.sql` · criado 2025-11-14 · alterado 2025-11-14
- Parâmetros/retorno: @id_alerta uniqueidentifier, @enviado bit, @data_processamento datetime
- Referencia: muralha.alerta [U]

### muralha.spu_MonitoramentoAreaMonitorada_V1
- Arquivo: `codigo-sql/procedures/muralha.spu_MonitoramentoAreaMonitorada_V1.sql` · criado 2025-10-06 · alterado 2025-10-06
- Parâmetros/retorno: @area_monitorada int, @placa char, @data_ini datetime, @data_fim datetime
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obtem_ocorrencias
- Arquivo: `codigo-sql/procedures/muralha.spu_obtem_ocorrencias.sql` · criado 2025-10-08 · alterado 2025-10-08
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.atendimento [U], muralha.atendimento_guarnicao [U], muralha.atendimento_situacao [U], muralha.boletim [U], muralha.cad_veiculo_monitorado [U], muralha.fato [U], muralha.ocorrencia [U], muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_tipo [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obter_alertas_blitz_pendentes
- Arquivo: `codigo-sql/procedures/muralha.spu_obter_alertas_blitz_pendentes.sql` · criado 2026-02-20 · alterado 2026-02-20
- Parâmetros/retorno: —
- Referencia: dbo.local [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.blitz_digital [U], muralha.blitz_local [U], muralha.blitz_tipo_alerta [U], muralha.cad_veiculo_monitorado [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obter_usuarios_blitz
- Arquivo: `codigo-sql/procedures/muralha.spu_obter_usuarios_blitz.sql` · criado 2025-11-27 · alterado 2025-11-27
- Parâmetros/retorno: @id_blitz_digital int
- Referencia: dbo.local [U], dbo.sis_fcm_token [U], dbo.sis_usuario [U], muralha.agente_localizacao_atual [U], muralha.blitz_digital [U], muralha.blitz_guarnicao [U], muralha.blitz_local [U], muralha.blitz_usuario [U], muralha.calcular_distancia_km [FN], muralha.guarnicao_integrante [U]

### muralha.spu_ObterAlertasMobilePorUsuario
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterAlertasMobilePorUsuario.sql` · criado 2025-09-19 · alterado 2025-09-19
- Parâmetros/retorno: @idUsuario int, @diasParam int
- Referencia: dbo.local_vigente [V], dbo.sis_grupo [U], dbo.sis_usuario_grupo [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.config_grupo_permissao [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterCamerasMonitoramentoAoVivo
- Arquivo: `codigo-sql/procedures/muralha.spu_obterCamerasMonitoramentoAoVivo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], muralha.config_monitoramento_ao_vivo_cameras [U]

### muralha.spu_ObterDadosAlertaOcorrencia
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterDadosAlertaOcorrencia.sql` · criado 2025-06-02 · alterado 2025-09-29
- Parâmetros/retorno: @idAlerta uniqueidentifier
- Referencia: dbo.local_pista_vigente [V], dbo.sis_usuario [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.atendimento [U], muralha.cad_veiculo_monitorado [U], muralha.fn_ObterSom [TF], muralha.motivo_descarte [U], muralha.ocorrencia [U], muralha.ocorrencia_notificacao [U], muralha.ponto_interesse [U], muralha.status_alerta [U], muralha.status_ocorrencia [U], muralha.tipo_alerta_ocorrencia [U], muralha.tipo_ocorrencia_status [U], muralha.v_status_ocorrencia_finalizacao [V], muralha.veiculo_tempo_real [U]

### muralha.spu_ObterDadosLinhaTempo
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterDadosLinhaTempo.sql` · criado 2025-06-16 · alterado 2025-06-17
- Parâmetros/retorno: @idAlerta uniqueidentifier, @idVeiculo uniqueidentifier
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterListaVideosMonitoramentoExibicao
- Arquivo: `codigo-sql/procedures/muralha.spu_obterListaVideosMonitoramentoExibicao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime, @id_local int
- Referencia: dbo.local_vigente [V], dbo.v_equipamento_cameras [V], muralha.video_monitoramento [U]

### muralha.spu_ObterNotificacoesPendentes
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterNotificacoesPendentes.sql` · criado 2025-06-02 · alterado 2026-09-22
- Parâmetros/retorno: @idTipoRegistro uniqueidentifier, @idStatusNotificacao uniqueidentifier, @idTipoNotificacao uniqueidentifier
- Referencia: muralha.tipo_registro [U]

### muralha.spu_ObterNovosAlertas
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterNovosAlertas.sql` · criado 2025-10-06 · alterado 2025-10-06
- Parâmetros/retorno: @lembrete_visualizado bit
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.fn_ObterSom [TF], muralha.fn_RegistroFatoRequerEPossuiBO [TF], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_ObterNovosAlertasMobileV2
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterNovosAlertasMobileV2.sql` · criado 2025-07-10 · alterado 2025-07-10
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_ObterNovosAlertasTeste
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterNovosAlertasTeste.sql` · criado 2025-08-13 · alterado 2025-08-14
- Parâmetros/retorno: @lembrete_visualizado bit
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.config_alarme [U], muralha.config_alarme_tipo [U], muralha.fn_CompararPlacas [IF], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_ObterNovosAlertasV2
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterNovosAlertasV2.sql` · criado 2025-10-04 · alterado 2025-10-04
- Parâmetros/retorno: @lembrete_visualizado bit
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.fn_ObterSom [TF], muralha.fn_RegistroFatoRequerEPossuiBO [TF], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterPassagensVeiculoMapaPorIdAlvo
- Arquivo: `codigo-sql/procedures/muralha.spu_obterPassagensVeiculoMapaPorIdAlvo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_veiculo uniqueidentifier
- Referencia: muralha.v_veiculo_tempo_real [V]

### muralha.spu_ObterUsuariosNotificacaoMobile
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterUsuariosNotificacaoMobile.sql` · criado 2025-06-16 · alterado 2025-07-03
- Parâmetros/retorno: @idTipoAlertaOcorrencia uniqueidentifier
- Referencia: dbo.sis_fcm_token [U], dbo.sis_grupo [U], dbo.sis_usuario [U], dbo.sis_usuario_grupo [U], muralha.config_grupo_permissao [U], muralha.tipo_alerta_ocorrencia [U]

### muralha.spu_ObterUsuariosNotificacaoMobilePorTipoAlerta
- Arquivo: `codigo-sql/procedures/muralha.spu_ObterUsuariosNotificacaoMobilePorTipoAlerta.sql` · criado 2025-07-25 · alterado 2025-07-25
- Parâmetros/retorno: @idTipoAlertaOcorrencia uniqueidentifier
- Referencia: dbo.sis_fcm_token [U], dbo.sis_grupo [U], dbo.sis_usuario [U], dbo.sis_usuario_grupo [U], muralha.config_grupo_permissao [U], muralha.config_mobile_silencio [U], muralha.tipo_alerta_ocorrencia [U]

### muralha.spu_obterVeiculosBlitzEletronica
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosBlitzEletronica.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.config_envio_tempo_real_equipamento [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterVeiculosBlitzWebsocket
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosBlitzWebsocket.sql` · criado 2026-02-06 · alterado 2026-02-06
- Parâmetros/retorno: @data_referencia datetime
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.blitz_digital [U], muralha.blitz_local [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterVeiculosPorFiltros
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosPorFiltros.sql` · criado 2025-10-11 · alterado 2026-07-30
- Parâmetros/retorno: @placa varchar, @dataIni datetime, @dataFim datetime, @equipamentos varchar, @faixa varchar, @classificacao varchar, @buscarApenasVeiculoComImagem bit, @consultaMapa bit, @exportarConsulta bit, @offset int, @itensPorPagina int, @marca varchar, @modelo varchar, @idCor int, @anoFabricacao int, @anoModelo int, @renavam varchar, @chassi varchar, @idLocalidade int, @restricao varchar, @filtrarPorRegistroFato bit, @caracteristicaRegistro varchar, @tipoRegistro int, @naturezaRegistro int, @filtroPlaca int, @tipoPlaca varchar, @tipoVeiculo varchar, @deveAplicarFiltroDeImagens bit, @somenteUltimaPassagem bit
- Referencia: —

### muralha.spu_obterVeiculosPorFiltros_exemplo
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosPorFiltros_exemplo.sql` · criado 2025-06-23 · alterado 2025-06-23
- Parâmetros/retorno: @placa varchar, @dataIni datetime, @dataFim datetime, @equipamentos varchar, @faixa varchar, @classificacao varchar, @marca varchar, @modelo varchar, @cor varchar, @anoFabricacao int, @anoModelo int, @renavam varchar, @chassi varchar, @tipoVeiculo varchar, @municipio varchar, @estado varchar, @restricao bit, @buscarApenasVeiculoComImagem bit, @consultaMapa bit, @exportarConsulta bit, @offset int, @itensPorPagina int
- Referencia: —

### muralha.spu_obterVeiculosPorFiltrosTeste
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosPorFiltrosTeste.sql` · criado 2025-10-11 · alterado 2025-10-11
- Parâmetros/retorno: @placa varchar, @dataIni datetime, @dataFim datetime, @equipamentos varchar, @faixa varchar, @classificacao varchar, @buscarApenasVeiculoComImagem bit, @consultaMapa bit, @exportarConsulta bit, @offset int, @itensPorPagina int, @marca varchar, @modelo varchar, @idCor int, @anoFabricacao int, @anoModelo int, @renavam varchar, @chassi varchar, @idLocalidade int, @restricao varchar, @filtrarPorRegistroFato bit, @caracteristicaRegistro varchar, @tipoRegistro int, @naturezaRegistro int, @filtroPlaca int, @tipoPlaca varchar, @tipoVeiculo varchar, @deveAplicarFiltroDeImagens bit, @somenteUltimaPassagem bit
- Referencia: —

### muralha.spu_obterVeiculosTempoReal
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosTempoReal.sql` · criado 2025-06-02 · alterado 2025-09-04
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V], muralha.config_envio_tempo_real_equipamento [U], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### muralha.spu_obterVeiculosTempoReal_20240926
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosTempoReal_20240926.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V], muralha.config_envio_tempo_real_equipamento [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterVeiculosTempoRealHistorico
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosTempoRealHistorico.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V], muralha.config_envio_tempo_real_equipamento [U], muralha.veiculo_tempo_real [U]

### muralha.spu_obterVeiculosTempoRealHistorico_20240926
- Arquivo: `codigo-sql/procedures/muralha.spu_obterVeiculosTempoRealHistorico_20240926.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_pista_vigente [V], muralha.config_envio_tempo_real_equipamento [U], muralha.veiculo_tempo_real [U]

### muralha.spu_PassagensSequenciais
- Arquivo: `codigo-sql/procedures/muralha.spu_PassagensSequenciais.sql` · criado 2026-04-22 · alterado 2026-04-29
- Parâmetros/retorno: @data_ini date, @data_fim date, @lista varchar
- Referencia: a.value, dbo.local [U], dbo.sl, muralha.veiculo_tempo_real [U]

### muralha.spu_PermanenciaAreasMonitoradasNew
- Arquivo: `codigo-sql/procedures/muralha.spu_PermanenciaAreasMonitoradasNew.sql` · criado 2025-10-03 · alterado 2025-10-03
- Parâmetros/retorno: @area int, @placa varchar, @data_inicio date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.spu_PermanenciaAreasMonitoradasNew2
- Arquivo: `codigo-sql/procedures/muralha.spu_PermanenciaAreasMonitoradasNew2.sql` · criado 2025-10-03 · alterado 2025-10-03
- Parâmetros/retorno: @area int, @placa varchar, @data_inicio date, @data_final date
- Referencia: dbo.Permanencias, muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.spu_PermanenciaAreasMonitoradasNew3
- Arquivo: `codigo-sql/procedures/muralha.spu_PermanenciaAreasMonitoradasNew3.sql` · criado 2025-10-03 · alterado 2025-10-04
- Parâmetros/retorno: @area int, @placa varchar, @data_inicio date, @data_final date
- Referencia: dbo.fn_FormatarSegundosEmDHM [FN], muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.spu_Relatorio_fluxo_veicular_rota
- Arquivo: `codigo-sql/procedures/muralha.spu_Relatorio_fluxo_veicular_rota.sql` · criado 2025-10-01 · alterado 2026-05-29
- Parâmetros/retorno: @data_referencia date, @id_local_origem int, @id_local_destino int
- Referencia: dbo.local_vigente [V], muralha.veiculo_tempo_real [U]

### muralha.spu_Relatorio_placas_veiculares
- Arquivo: `codigo-sql/procedures/muralha.spu_Relatorio_placas_veiculares.sql` · criado 2025-09-28 · alterado 2026-05-29
- Parâmetros/retorno: @placas udtt_ListaPlacas, @dataInicio datetime, @dataFim datetime
- Referencia: dbo.sis_usuario [U], dbo.udtt_ListaPlacas, muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_correcao [U]

### muralha.spu_RelatorioDeEvolucaoSemanalDeFatos
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioDeEvolucaoSemanalDeFatos.sql` · criado 2025-09-16 · alterado 2026-05-29
- Parâmetros/retorno: @param1 varchar, @param2 varchar
- Referencia: muralha.registro_fato [U], muralha.registro_fato_tipo [U]

### muralha.spu_RelatorioDePendenciasNosRegistrosDeFato
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioDePendenciasNosRegistrosDeFato.sql` · criado 2025-09-16 · alterado 2026-05-29
- Parâmetros/retorno: @data_inicio date, @data_fim date, @tipo_falta varchar, @somente_privados bit
- Referencia: —

### muralha.spu_RelatorioDistribuicaoFatos
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioDistribuicaoFatos.sql` · criado 2025-09-24 · alterado 2026-05-29
- Parâmetros/retorno: @DataInicial date, @DataFinal date
- Referencia: muralha.alerta [U], muralha.tipo_alerta_ocorrencia [U]

### muralha.spu_RelatorioEstatisticaPorTipoDeFatoRegistrado
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioEstatisticaPorTipoDeFatoRegistrado.sql` · criado 2025-09-16 · alterado 2026-05-29
- Parâmetros/retorno: @DataInicio datetime, @DataFinal datetime
- Referencia: dbo.local [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioEstatisticaPorTipoDeFatoRegistradoConsolidado
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioEstatisticaPorTipoDeFatoRegistradoConsolidado.sql` · criado 2025-09-16 · alterado 2026-05-29
- Parâmetros/retorno: @DataInicial datetime, @DataFinal datetime
- Referencia: muralha.alerta [U], muralha.tipo_alerta_ocorrencia [U]

### muralha.spu_RelatorioEstatisticoAlarmesJson
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioEstatisticoAlarmesJson.sql` · criado 2025-09-18 · alterado 2026-05-29
- Parâmetros/retorno: @DataInicio date, @DataFim date, @IdLocal int, @IdTipoAlerta uniqueidentifier, @TipoRelatorio varchar
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioEstatisticoFatosRegistrados
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioEstatisticoFatosRegistrados.sql` · criado 2025-09-19 · alterado 2026-05-29
- Parâmetros/retorno: @DataInicio date, @DataFim date
- Referencia: muralha.cidade [U], muralha.estado [U], muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_passagem_veic [U], muralha.registro_fato_tipo [U], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioEstatisticoPorTipoFatoMapa
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioEstatisticoPorTipoFatoMapa.sql` · criado 2025-10-02 · alterado 2026-05-29
- Parâmetros/retorno: @dataInicio varchar, @dataFim varchar
- Referencia: muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_tipo [U]

### muralha.spu_RelatorioEstatisticoPorTipoFatoMapaNew
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioEstatisticoPorTipoFatoMapaNew.sql` · criado 2025-10-14 · alterado 2026-05-29
- Parâmetros/retorno: @dataInicio varchar, @dataFim varchar
- Referencia: muralha.registro_fato [U], muralha.registro_fato_endereco [U], muralha.registro_fato_tipo [U]

### muralha.spu_RelatorioFluxoPassagensVeiculares
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioFluxoPassagensVeiculares.sql` · criado 2025-09-25 · alterado 2026-05-29
- Parâmetros/retorno: @id_local int, @data_inicio date, @data_fim date
- Referencia: dbo.classe_veiculo [U], dbo.local [U], dbo.v_local_pista_vigente [V], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioFluxoVeicularRota
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioFluxoVeicularRota.sql` · criado 2025-10-09 · alterado 2026-05-29
- Parâmetros/retorno: @data_ini date, @data_fim date, @id_local_origem int, @id_local_destino int
- Referencia: dbo.local_vigente [V], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioIrregularidadesDashboard
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioIrregularidadesDashboard.sql` · criado 2025-10-14 · alterado 2026-05-29
- Parâmetros/retorno: @data_ini date, @data_fim date
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado.sql` · criado 2025-10-06 · alterado 2026-05-29
- Parâmetros/retorno: @area_monitorada varchar, @placa varchar, @data_ini date, @data_fim date
- Referencia: muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U], muralha.veiculo_tempo_real [U]

### muralha.spu_RelatorioVeiculosMonitoradosModelo
- Arquivo: `codigo-sql/procedures/muralha.spu_RelatorioVeiculosMonitoradosModelo.sql` · criado 2025-09-29 · alterado 2026-05-29
- Parâmetros/retorno: @data_inicio varchar, @data_final varchar
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.registro_fato [U], muralha.registro_fato_tipo [U], muralha.registro_fato_veiculo [U]

### muralha.spu_salvar_config_monitoramento_ao_vivo
- Arquivo: `codigo-sql/procedures/muralha.spu_salvar_config_monitoramento_ao_vivo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @segundos int, @idUsuario int
- Referencia: dbo.spu_replica_erro, muralha.config_monitoramento_ao_vivo [U]

### muralha.spu_verifica_anomalia
- Arquivo: `codigo-sql/procedures/muralha.spu_verifica_anomalia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: muralha.spu_verifica_anomalia_fluxo [P], muralha.spu_verifica_anomalia_infracao [P], muralha.spu_verifica_anomalia_irregularidades [P]

### muralha.spu_verifica_anomalia_fluxo
- Arquivo: `codigo-sql/procedures/muralha.spu_verifica_anomalia_fluxo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.veiculo_pesquisa [V], muralha.anomalia [U]

### muralha.spu_verifica_anomalia_infracao
- Arquivo: `codigo-sql/procedures/muralha.spu_verifica_anomalia_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.local_vigente [V], muralha.anomalia [U]

### muralha.spu_verifica_anomalia_irregularidades
- Arquivo: `codigo-sql/procedures/muralha.spu_verifica_anomalia_irregularidades.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.anomalia [U], muralha.veiculo_tempo_real [U]

### muralha.spuObterDadosVeiculoTempoReal
- Arquivo: `codigo-sql/procedures/muralha.spuObterDadosVeiculoTempoReal.sql` · criado 2025-06-02 · alterado 2025-06-24
- Parâmetros/retorno: @id_veiculo_tempo_real uniqueidentifier
- Referencia: dbo.classe_veiculo [U], dbo.local_pista_vigente [V], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### muralha.spuObterListaDispositivosContagens
- Arquivo: `codigo-sql/procedures/muralha.spuObterListaDispositivosContagens.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @equipamentos ListaEquipamentos
- Referencia: dbo.infracao [U], dbo.ListaEquipamentos, dbo.local_status_conexao [V], dbo.local_vigente [V], dbo.prd_getEstatisticaTodosCompleto, muralha.fcn_getDispositivosContagensFluxoDiarioJson [FN], muralha.veiculo_tempo_real [U]

### muralha.spuObterListaDispositivosContagensMisto
- Arquivo: `codigo-sql/procedures/muralha.spuObterListaDispositivosContagensMisto.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @equipamentos ListaEquipamentos
- Referencia: dbo.infracao [U], dbo.ListaEquipamentos, dbo.local_status_conexao [V], dbo.local_vigente [V], dbo.prd_getEstatisticaTodosCompleto, muralha.veiculo_tempo_real [U]

### muralha.spuObterListaDispositivosContagensMistoFiltrado
- Arquivo: `codigo-sql/procedures/muralha.spuObterListaDispositivosContagensMistoFiltrado.sql` · criado 2025-08-18 · alterado 2025-08-18
- Parâmetros/retorno: @equipamentos ListaEquipamentos, @categoria varchar
- Referencia: dbo.configuracao_equipamento [U], dbo.grupo_equipamento [U], dbo.infracao [U], dbo.ListaEquipamentos, dbo.local_status_conexao [V], dbo.local_vigente [V], muralha.veiculo_tempo_real [U]

### muralha.spuObterListaDispositivosContagensSimplificado
- Arquivo: `codigo-sql/procedures/muralha.spuObterListaDispositivosContagensSimplificado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @equipamentos ListaEquipamentos
- Referencia: dbo.ListaEquipamentos, dbo.local_status_conexao [V], dbo.local_vigente [V], dbo.prd_getEstatisticaTodosCompleto

### muralha.spuObterNotificacoesNaoTratadas
- Arquivo: `codigo-sql/procedures/muralha.spuObterNotificacoesNaoTratadas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.tipo_alerta_ocorrencia [U], muralha.veiculo_tempo_real [U]

## Funções inline (IF) — 226

### dbo.f_inconsistencias_processo
- Arquivo: `codigo-sql/funcoes/dbo.f_inconsistencias_processo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @id_enquadramento int
- Referencia: dbo.inconsistencia [U], dbo.inconsistencia_processo_enquadramento [U]

### dbo.f_inconsistencias_processo_teste
- Arquivo: `codigo-sql/funcoes/dbo.f_inconsistencias_processo_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @id_enquadramento int
- Referencia: dbo.enquadramento_inconsistencia [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.processo_inconsistencia [U]

### dbo.fcn_buscaLocalStatus
- Arquivo: `codigo-sql/funcoes/dbo.fcn_buscaLocalStatus.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.imagens_sinalizacao [U], dbo.laudo_afericao [U], dbo.local_status [V], dbo.local_status_conexao [V], dbo.local_vigente [V], dbo.remessa_nao_metrologico [U], dbo.videos_fis [U]

### dbo.fcn_espelho_processamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_espelho_processamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @data_base datetime
- Referencia: dbo.infracao [U], dbo.infracao_processo_concluido [U], dbo.processo_ligacao [U]

### dbo.fcn_getAlertaCapturaTravado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaCapturaTravado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getAlertaEvento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaEvento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @idEvento int, @dataInicio datetime, @mensagem bit
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getAlertaEvento_InicializacaoCaptura
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaEvento_InicializacaoCaptura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime, @mensagem bit
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getAlertaExcessoEventos
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaExcessoEventos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @threshold int
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getAlertaLocaisConexaoInstavel
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaLocaisConexaoInstavel.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.status_conexao [U]

### dbo.fcn_getAlertaLocaisOffLine
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaLocaisOffLine.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.local_status_conexao [V], dbo.local_vigente [V], dbo.TEMPO_DECORRIDO [FN]

### dbo.fcn_getAlertaNaoEnviaEventos
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaNaoEnviaEventos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_status [V]

### dbo.fcn_getAmostra
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAmostra.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @nivel varchar, @nqa float, @tamanho_lote int
- Referencia: dbo.amostragem [U], dbo.amostragem_nivel [U], dbo.amostragem_tamanho [U], dbo.chave_valor [U]

### dbo.fcn_getArquivosPendentes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getArquivosPendentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int
- Referencia: dbo.integracao_imagem_gct_info [U], dbo.integracao_sequencia_imagem_gct [U]

### dbo.fcn_getArquivosPendentesVerificacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getArquivosPendentesVerificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.integracao_imagem_gct_info [U], dbo.integracao_sequencia_imagem_gct [U]

### dbo.fcn_getDadosPendentesImportacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getDadosPendentesImportacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.imagem_importacao [U], dbo.infracao_importacao [U], dbo.perfil_importacao [U], dbo.veiculo_importacao [U]

### dbo.fcn_getErrosProcessamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getErrosProcessamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U]

### dbo.fcn_getErrosRespostaPooling
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getErrosRespostaPooling.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getEventosCsxCAV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getEventosCsxCAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_inicio datetime, @data_fim datetime
- Referencia: dbo.arquivo_log_item_log [U], dbo.eventos_csx [U], dbo.eventos_csx_categoria_x_evento [U], dbo.fcn_getEventosCsxDescProprietarioCAV [IF]

### dbo.fcn_getEventosCsxDescCategoriaCAV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getEventosCsxDescCategoriaCAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_desc_categoria [U]

### dbo.fcn_getEventosCsxDescEventoCAV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getEventosCsxDescEventoCAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_categoria_x_evento [U], dbo.eventos_csx_desc_evento [U]

### dbo.fcn_getEventosCsxDescNivelCAV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getEventosCsxDescNivelCAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_desc_nivel [U]

### dbo.fcn_getEventosCsxDescPrioridadeCAV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getEventosCsxDescPrioridadeCAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_desc_prioridade [U]

### dbo.fcn_getEventosCsxDescProprietarioCAV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getEventosCsxDescProprietarioCAV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.equipamento_estatico [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getGruposAbaixo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getGruposAbaixo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int
- Referencia: dbo.grupo_hierarquia [V], dbo.sis_grupo [U], dbo.sis_usuario_grupo [U]

### dbo.fcn_getHistoricoAfericao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getHistoricoAfericao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getHistoricoManutencao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getHistoricoManutencao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getImagensEmProcessamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getImagensEmProcessamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimentos_erro [U], dbo.processo [U], dbo.remessa [U]

### dbo.fcn_getImagensEmProcessamentoConsistentes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getImagensEmProcessamentoConsistentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimento_importacao [U], dbo.movimentos_erro [U], dbo.processo [U], dbo.remessa [U]

### dbo.fcn_getImagensEmProcessamentoInconsistentes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getImagensEmProcessamentoInconsistentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimento_importacao [U], dbo.movimentos_erro [U], dbo.processo [U], dbo.remessa [U]

### dbo.fcn_getInfracaoSumarizadoRelatorio
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getInfracaoSumarizadoRelatorio.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataIni datetime, @dataFim datetime
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.veiculo [U]

### dbo.fcn_getInfracaoSumarizadoRelatorioLocal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getInfracaoSumarizadoRelatorioLocal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataIni datetime, @dataFim datetime, @idLocal int
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.veiculo [U]

### dbo.fcn_getLimiteCargaGrupoQFV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getLimiteCargaGrupoQFV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_classificacao char, @grupo char
- Referencia: dbo.v_ppv_qfv [V]

### dbo.fcn_getListagemErrosDigitacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getListagemErrosDigitacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_digitacao [U], dbo.infracao_processo_usuario [V], dbo.sis_usuario [U]

### dbo.fcn_getModoOperacaoGeralPPV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getModoOperacaoGeralPPV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ppv_ocorrencias [U], dbo.ppv_tp_ocorrencia [U]

### dbo.fcn_getModoOperacaoPesagemPPV
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getModoOperacaoPesagemPPV.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ppv_ocorrencias [U], dbo.ppv_tp_ocorrencia [U]

### dbo.fcn_getModoOperacaoPTZ
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getModoOperacaoPTZ.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.ptz_configuracao_operacao [U], dbo.ptz_modo_operacao [U], dbo.sis_usuario [U]

### dbo.fcn_getProdutividadeOperadores
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProdutividadeOperadores.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao_processo [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.sis_usuario [U]

### dbo.fcn_getProdutividadeOperadores_Alt1
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProdutividadeOperadores_Alt1.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @data datetime
- Referencia: dbo.infracao_processo [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.sis_usuario [U]

### dbo.fcn_getProdutividadeOperadores_Alt2
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProdutividadeOperadores_Alt2.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int, @data_ini datetime, @data_fim datetime
- Referencia: dbo.infracao_processo [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.sis_usuario [U]

### dbo.fcn_getProdutividadeOperadores_Alt3
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProdutividadeOperadores_Alt3.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime
- Referencia: dbo.infracao_processo [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.sis_usuario [U]

### dbo.fcn_getProximasAfericoes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProximasAfericoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V]

### dbo.fcn_getRelAfericaoInicioOperacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelAfericaoInicioOperacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.chave_valor [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorio10MedicaoFluxoVeicular_Dados
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio10MedicaoFluxoVeicular_Dados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.fcn_getInfracaoSumarizadoRelatorio [IF], dbo.fcn_getVeiculoSumarizadoRelatorio [IF], dbo.local_pista_vigente [V], dbo.local_vigente [V]

### dbo.fcn_getRelatorio10MedicaoFluxoVeicular_PorFaixa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio10MedicaoFluxoVeicular_PorFaixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.configuracao_equipamento_velocidade_limite [U], dbo.descricao_pista_gst [U], dbo.fcn_getRelatorio10MedicaoFluxoVeicular_Dados [IF], dbo.local_pista_vigente [V]

### dbo.fcn_getRelatorio10MedicaoFluxoVeicular_PorLocal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio10MedicaoFluxoVeicular_PorLocal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.configuracao_equipamento_velocidade_limite [U], dbo.descricao_pista_gst [U], dbo.fcn_getRelatorio10MedicaoFluxoVeicular_Dados [IF], dbo.local_pista_vigente [V]

### dbo.fcn_getRelatorio11MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio11MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento [IF], dbo.hora [U]

### dbo.fcn_getRelatorio12MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio12MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento [IF], dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.veiculo [U]

### dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorFaixa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorFaixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorio [IF], dbo.fcn_getVeiculoSumarizadoRelatorioEnquadramento [IF], dbo.local_pista_vigente [V], dbo.local_vigente [V]

### dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorLocal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorLocal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorFaixa [IF], dbo.local_pista_vigente [V]

### dbo.fcn_getRelatorio14MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio14MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getRelatorio14MedicaoFluxoVeicular_Dados [IF]

### dbo.fcn_getRelatorio14MedicaoFluxoVeicular_Dados
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio14MedicaoFluxoVeicular_Dados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento [IF], dbo.hora [U]

### dbo.fcn_getRelatorio1MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio1MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.hora [U]

### dbo.fcn_getRelatorio2MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio2MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getInfracaoSumarizadoRelatorioLocal [IF], dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.fcn_ObterDatasPeriodo [IF]

### dbo.fcn_getRelatorio4MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio4MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-10-06
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.classe_veiculo [U], dbo.hora [U], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorio5MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio5MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.hora [U]

### dbo.fcn_getRelatorio6MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio6MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.hora [U]

### dbo.fcn_getRelatorio7MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio7MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento [IF], dbo.hora [U], dbo.infracao [U], dbo.veiculo [U]

### dbo.fcn_getRelatorio8MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio8MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.hora [U]

### dbo.fcn_getRelatorio9MedicaoFluxoVeicular
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorio9MedicaoFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data date, @Id_Local int, @Id_Pista int
- Referencia: dbo.faixa_velocidade_relatorio_rj [U], dbo.hora [U], dbo.veiculo_sumarizado_faixa_velocidade [U]

### dbo.fcn_getRelatorioAtrasoPMESP
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioAtrasoPMESP.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.pmesp_evento_conexao [U], dbo.pmesp_movimento [V]

### dbo.fcn_getRelatorioAtualizacaoCadastros
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioAtualizacaoCadastros.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.fcn_getRelatorioAutuadosZonaRestrEnquadramentos
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioAutuadosZonaRestrEnquadramentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.solicitacao_auditoria_infracao [U]

### dbo.fcn_getRelatorioAvancoParada
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioAvancoParada.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.processo [U]

### dbo.fcn_getRelatorioConfigSensibilidadeLaco
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioConfigSensibilidadeLaco.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_controlador [U], dbo.configuracao_equipamento_controlador_canais [U], dbo.configuracao_equipamento_pista [U], dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V], dbo.produto [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioConsIncons
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioConsIncons.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioConsistenteInconsistenteLocalMes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioConsistenteInconsistenteLocalMes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.local [U], dbo.remessa [U]

### dbo.fcn_getRelatorioConsistenteInconsistenteMes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioConsistenteInconsistenteMes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.local [U]

### dbo.fcn_getRelatorioContestacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioContestacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao_contestacao [U], dbo.infracao_processo [U], dbo.infracao_processo_contestacao [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioDadosModem
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioDadosModem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioDataAfericaoDivergente
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioDataAfericaoDivergente.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.infracao [U], dbo.local [U], dbo.processo [U]

### dbo.fcn_getRelatorioDetecInv
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioDetecInv.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.TryConvertInt [FN]

### dbo.fcn_getRelatorioDivergenciaAuditMes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioDivergenciaAuditMes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.local_vigente [V], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioDivergenciasValidacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioDivergenciasValidacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.enquadramento [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.local [U], dbo.movimento_importacao [U], dbo.remessa [U], dbo.sis_usuario [U], dbo.veiculo [U]

### dbo.fcn_getRelatorioEnvioPMESP
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioEnvioPMESP.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasHorasPeriodo [IF], dbo.pmesp_evento_conexao [U], dbo.pmesp_movimento [V]

### dbo.fcn_getRelatorioERROS
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioERROS.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_digitacao [U], dbo.infracao_remessa [U], dbo.local [U], dbo.movimento_importacao [U], dbo.remessa [U], dbo.sis_usuario [U], dbo.veiculo [U]

### dbo.fcn_getRelatorioErrosExportaAutomatico
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioErrosExportaAutomatico.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.gera_remessa_automatico [U], dbo.gera_remessa_automatico_log [U], dbo.remessa [U]

### dbo.fcn_getRelatorioEventoManual
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioEventoManual.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_categoria [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_nivel [U], dbo.eventos_csx_desc_prioridade [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioEventosCamera
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioEventosCamera.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.fcn_getRelatorioFluxoVeicularPorHora
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioFluxoVeicularPorHora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.hora [U], dbo.local_pista_vigente [V], dbo.veiculo_sumarizado_relatorio [U]

### dbo.fcn_getRelatorioImagemDia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioImagemDia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioImagemDiaInconsistencia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioImagemDiaInconsistencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioImagensAuditadas
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioImagensAuditadas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao_processo [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioImagensVelRegistradas
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioImagensVelRegistradas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.enquadramento [U], dbo.infracao [U], dbo.local [U]

### dbo.fcn_getRelatorioIncLocalEnq
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioIncLocalEnq.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.local_vigente [V], dbo.remessa [U]

### dbo.fcn_getRelatorioInconsistenciaTriagem
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioInconsistenciaTriagem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.local_vigente [V], dbo.remessa [U]

### dbo.fcn_getRelatorioInfracoesConsistentes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioInfracoesConsistentes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U]

### dbo.fcn_getRelatorioInfracoesConsistentesMensal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioInfracoesConsistentesMensal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_medicao [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimento_importacao [U], dbo.remessa [U]

### dbo.fcn_getRelatorioInfSemOblit
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioInfSemOblit.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.infracao_obliteracao [U], dbo.local_vigente [V], dbo.processo [U], dbo.veiculo [U]

### dbo.fcn_getRelatorioInicializacoesCaptura
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioInicializacoesCaptura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.fcn_getRelatorioIsencoes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioIsencoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U]

### dbo.fcn_getRelatorioIsentos
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioIsentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.arquivos_cai_para_cav [U], dbo.cad_arquivos_importados [U], dbo.cad_isento_arquivo [U]

### dbo.fcn_getRelatorioLeituraCorretaOCREstatistica
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioLeituraCorretaOCREstatistica.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.local_vigente [V], dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelatorioLeituraCorretaOCRInfracao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioLeituraCorretaOCRInfracao.sql` · criado 2025-06-02 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_getRelatorioLeituraCorretaOCRInfracaoLocal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioLeituraCorretaOCRInfracaoLocal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_getRelatorioLeituraCorretaOCRMercosulFaixa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioLeituraCorretaOCRMercosulFaixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_getRelatorioLocalImagensDefeituosaHora
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioLocalImagensDefeituosaHora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.veiculo_importacao [U], dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelatorioLogin
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioLogin.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.sis_logon_logoff_usuario [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioManutencao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioManutencao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioManutencoes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioManutencoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.manutencao [U], dbo.produto [U]

### dbo.fcn_getRelatorioMediaMaxCoeficInf
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMediaMaxCoeficInf.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.painel_contrato [U]

### dbo.fcn_getRelatorioMediaTrafego
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMediaTrafego.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_getRelatorioVolumeTrafego [IF]

### dbo.fcn_getRelatorioMedicaoPMESPEnvioPlacas
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMedicaoPMESPEnvioPlacas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.pmesp_movimentos_atraso_dia [U], dbo.v_locais_pmesp [V]

### dbo.fcn_getRelatorioMedicaoPMESPEnvioPlacas1
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMedicaoPMESPEnvioPlacas1.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime
- Referencia: dbo.pmesp_movimentos_atraso_dia [U], dbo.v_locais_pmesp [V]

### dbo.fcn_getRelatorioMotivoInconsistencia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMotivoInconsistencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.CHAVE_VALOR [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.local_vigente [V], dbo.remessa [U]

### dbo.fcn_getRelatorioMovimentos
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMovimentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.arquivos_cai_para_cav [U], dbo.imagem [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimentos_erro [U], dbo.remessa [U], dbo.veiculo_imagem [U]

### dbo.fcn_getRelatorioMovimentosDetalhe
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMovimentosDetalhe.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.arquivos_cai_para_cav [U], dbo.imagem [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.veiculo_imagem [U]

### dbo.fcn_getRelatorioMovimentosImportados
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioMovimentosImportados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.arquivos_cai_para_cav [U], dbo.imagem [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.movimentos_erro [U], dbo.remessa [U], dbo.veiculo_imagem [U]

### dbo.fcn_getRelatorioNumeroTriagemDigitacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioNumeroTriagemDigitacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.infracao_processo [U]

### dbo.fcn_getRelatorioPlacasSuspeitas
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioPlacasSuspeitas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.cad_veiculo_verificar [U], dbo.infracao [U], dbo.processo [U]

### dbo.fcn_getRelatorioPMESP_Perda
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioPMESP_Perda.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.pmesp_movimento [V], dbo.v_locais_pmesp [V], dbo.veiculo [U], dbo.veiculo_estatistica [U]

### dbo.fcn_getRelatorioPrioridadeManutencao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioPrioridadeManutencao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.painel_contrato_vigente_se [V]

### dbo.fcn_getRelatorioPrioridadeManutencaoAT
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioPrioridadeManutencaoAT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.painel_contrato_vigente_se [V]

### dbo.fcn_getRelatorioProcessamentoProdutividade
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioProcessamentoProdutividade.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioProcProdutividadeSumarizado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioProcProdutividadeSumarizado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_usuario [V], dbo.processo [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioQtdeVelMediaLocalMes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioQtdeVelMediaLocalMes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorioRegrasInfracaoEquip
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioRegrasInfracaoEquip.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento_regra_infracao [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioRegrasInfracaoEquipFaixa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioRegrasInfracaoEquipFaixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento_regra_infracao [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioRemessaValidas
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioRemessaValidas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_remessa [U], dbo.local [U]

### dbo.fcn_getRelatorioRevisaoMovimentos
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioRevisaoMovimentos.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.movimento_arquivo [U], dbo.movimentos_erro [U], dbo.remessa [U]

### dbo.fcn_getRelatorioSinteticoAcumulado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioSinteticoAcumulado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.local_vigente [V], dbo.processo [U], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorioSistemaOperacional
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioSistemaOperacional.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.fcn_getRelatorioStatusEwfUwf
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioStatusEwfUwf.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioTeamViewer
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioTeamViewer.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.fcn_getRelatorioTempoVidaCaptura
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioTempoVidaCaptura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioTotImagensDeEquip_PorEnquadramento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioTotImagensDeEquip_PorEnquadramento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.infracao [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioValidacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioValidacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.cad_especie_processo [U], dbo.cad_marca_cet [U], dbo.cad_marca_cet_processo [U], dbo.cad_veiculo [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento_regra_infracao [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.infracao_processo_digitacao [U], dbo.infracao_remessa [U], dbo.local [U], dbo.movimento_importacao [U], dbo.remessa [U], dbo.sis_usuario [U], dbo.veiculo [U]

### dbo.fcn_getRelatorioValidInvalid
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioValidInvalid.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.sis_usuario [U]

### dbo.fcn_getRelatorioVdm
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVdm.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_getRelatorioVolume [IF]

### dbo.fcn_getRelatorioVdmNaoPublicados
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVdmNaoPublicados.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.local_vigente [V], dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelatorioVeiculosInfratores
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVeiculosInfratores.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_getRelatorioVeiculosOficiais
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVeiculosOficiais.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.cad_veiculo [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.processo [U]

### dbo.fcn_getRelatorioVelMediaPorFaixaVel
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVelMediaPorFaixaVel.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.infracao [U], dbo.infracao_remessa [U], dbo.veiculo [U]

### dbo.fcn_getRelatorioVelocidadeMedia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVelocidadeMedia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelatorioVelocidadeMedia1
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVelocidadeMedia1.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.enquadramento [U], dbo.infracao [U], dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelatorioVersaoCaptura
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVersaoCaptura.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.local_vigente [V]

### dbo.fcn_getRelatorioVolume
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVolume.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorioVolume2
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVolume2.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_medicao [U], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorioVolumeAgrupadoClasseHora
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVolumeAgrupadoClasseHora.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.classe_veiculo [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorioVolumeHoraDiaSem
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVolumeHoraDiaSem.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelatorioVolumeTrafego
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioVolumeTrafego.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.local_vigente [V], dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelDistribuicaoFaixaVelocidade
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelDistribuicaoFaixaVelocidade.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data date, @Id_Local int, @Id_Pista int
- Referencia: dbo.faixa_velocidade [U], dbo.hora [U], dbo.veiculo_sumarizado [U]

### dbo.fcn_getRelFluxoInfracaoPorDiaSemana
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelFluxoInfracaoPorDiaSemana.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_InitCap [FN], dbo.fcn_ObterDatasPeriodo [IF], dbo.fn_split_string, dbo.local_vigente [V], dbo.veiculo_sumarizado_relatorio [U]

### dbo.fcn_getRelIndicadorAtrasoProcessamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelIndicadorAtrasoProcessamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U]

### dbo.fcn_getRelLeituraCorretaOCRInfracaoCarga
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelLeituraCorretaOCRInfracaoCarga.sql` · criado 2025-10-09 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_getRelLeituraCorretaOCRInfracaoCargaLocal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelLeituraCorretaOCRInfracaoCargaLocal.sql` · criado 2025-10-09 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_getRelPesagemPorEixoDetalhado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelPesagemPorEixoDetalhado.sql` · criado 2025-09-30 · alterado 2025-09-30
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_getClassificacaoPNCT_v2 [FN], dbo.local_vigente [V], dbo.v_veiculo_pesagem [V], dbo.v_veiculo_pesagem_distancia_eixos [V], dbo.v_veiculo_pesagem_eixo [V], dbo.veiculo_pesquisa [V]

### dbo.fcn_getRelReinicializacaoCapturaMedidor
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelReinicializacaoCapturaMedidor.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_evento [U], dbo.eventos_csx_desc_proprietario [U], dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V]

### dbo.fcn_getUsuariosAbaixo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getUsuariosAbaixo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_usuario int
- Referencia: dbo.grupo_hierarquia [V], dbo.sis_usuario [U], dbo.sis_usuario_grupo [U]

### dbo.fcn_getVeiculoSumarizadoRelatorio
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getVeiculoSumarizadoRelatorio.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataIni datetime, @dataFim datetime
- Referencia: dbo.veiculo_sumarizado_relatorio [U]

### dbo.fcn_getVeiculoSumarizadoRelatorioEnquadramento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getVeiculoSumarizadoRelatorioEnquadramento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataIni datetime, @dataFim datetime
- Referencia: dbo.infracao [U]

### dbo.fcn_getVeiculoSumarizadoRelatorioLocal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getVeiculoSumarizadoRelatorioLocal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataIni datetime, @dataFim datetime, @idLocal int
- Referencia: dbo.veiculo_sumarizado_relatorio [U]

### dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataIni datetime, @dataFim datetime, @idLocal int
- Referencia: dbo.infracao [U]

### dbo.fcn_ImagensProcessamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ImagensProcessamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @consistentes bit, @inconsistentes bit
- Referencia: dbo.infracao [U], dbo.processo [U]

### dbo.fcn_IndicadoresContrato
- Arquivo: `codigo-sql/funcoes/dbo.fcn_IndicadoresContrato.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime
- Referencia: dbo.fcn_IndicadoresFaixa [IF], dbo.fcn_IndicadoresProcessamento [IF]

### dbo.fcn_IndicadoresFaixa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_IndicadoresFaixa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.configuracao_equipamento_pista [U], dbo.date_table [TF], dbo.infracao [U], dbo.local_regra_infracao_vigente [V], dbo.local_vigente [V], dbo.painel_contrato_alerta [U]

### dbo.fcn_IndicadoresProcessamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_IndicadoresProcessamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime, @max_dia_atraso int
- Referencia: dbo.Date_Table [TF], dbo.infracao [U], dbo.infracao_processo_concluido [U], dbo.solicitacao_auditoria [U]

### dbo.fcn_IndicadoresProcessamentoAgrupado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_IndicadoresProcessamentoAgrupado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime, @max_dia_atraso int
- Referencia: dbo.infracao [U], dbo.infracao_erro_desconsiderar [U], dbo.infracao_processo [U], dbo.infracao_processo_digitacao [U], dbo.infracao_remessa [U], dbo.movimento_importacao [U], dbo.remessa [U]

### dbo.fcn_IndicadoresProcessamentoPrincipal
- Arquivo: `codigo-sql/funcoes/dbo.fcn_IndicadoresProcessamentoPrincipal.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.cad_isento_arquivo [U], dbo.fcn_IndicadoresProcessamentoAgrupado [IF], dbo.infracao [U], dbo.solicitacao_auditoria_infracao [U]

### dbo.fcn_IndicadoresProcessamentoPrincipal_alt
- Arquivo: `codigo-sql/funcoes/dbo.fcn_IndicadoresProcessamentoPrincipal_alt.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @ATRASO int
- Referencia: dbo.painel_principal [U], dbo.painel_principal_erros [U]

### dbo.fcn_InfracaoDisponivelContestacao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_InfracaoDisponivelContestacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo_contestacao int
- Referencia: dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_contestacao [U], dbo.infracao_janela [U], dbo.infracao_remessa [U], dbo.local [U]

### dbo.fcn_InfracaoDisponivelUsuario
- Arquivo: `codigo-sql/funcoes/dbo.fcn_InfracaoDisponivelUsuario.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @id_usuario int
- Referencia: dbo.agendamento_processamento [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_remessa [U], dbo.local [U], dbo.processo [U], dbo.remessa [U]

### dbo.fcn_InfracaoDisponivelUsuario_teste
- Arquivo: `codigo-sql/funcoes/dbo.fcn_InfracaoDisponivelUsuario_teste.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_processo int, @id_usuario int
- Referencia: dbo.agendamento_processamento [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_contestacao [U], dbo.infracao_processo [U], dbo.infracao_remessa [U], dbo.local [U], dbo.processo [U], dbo.remessa [U]

### dbo.fcn_InfracoesSemEscala
- Arquivo: `codigo-sql/funcoes/dbo.fcn_InfracoesSemEscala.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.agenda_estatico [U], dbo.agenda_estatico_item [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo [U]

### dbo.fcn_lista_amostras_periodo_local_pista
- Arquivo: `codigo-sql/funcoes/dbo.fcn_lista_amostras_periodo_local_pista.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFinal date, @idLocal int, @idPista int, @metrologica bit
- Referencia: dbo.amostra_imagem [U], dbo.amostra_imagem_manual [U], dbo.configuracao_equipamento_pista [U], dbo.infracao [U], dbo.infracao_imagem [U], dbo.local_vigente [V], dbo.processo_medicao_veiculo [U], dbo.veiculo [U]

### dbo.fcn_ListaPistasAmostra
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ListaPistasAmostra.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_ini datetime, @data_fim datetime
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_pista [U], dbo.local_regra_infracao_vigente [V], dbo.local_vigente [V]

### dbo.fcn_listar_sugestoes_amostras
- Arquivo: `codigo-sql/funcoes/dbo.fcn_listar_sugestoes_amostras.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @id_pista tinyint, @dia date, @metrologica bit, @page_offset int, @page_limit int
- Referencia: dbo.fcn_pontua_infracao [FN], dbo.infracao_amostra [V], dbo.infracao_rejeita_amostra [U]

### dbo.fcn_Locais_Vigentes_Na_Data
- Arquivo: `codigo-sql/funcoes/dbo.fcn_Locais_Vigentes_Na_Data.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data datetime
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_captura_veiculo [U], dbo.local [U]

### dbo.fcn_LocalImagensDefeituosa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_LocalImagensDefeituosa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicial datetime, @DataFinal datetime, @HoraInicial time, @HoraFinal time
- Referencia: dbo.veiculo_pesquisa [V]

### dbo.fcn_LocalImagensDefeituosaAtualizaPainel
- Arquivo: `codigo-sql/funcoes/dbo.fcn_LocalImagensDefeituosaAtualizaPainel.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicial datetime, @DataFinal datetime, @HoraInicial time, @HoraFinal time
- Referencia: dbo.veiculo_pesquisa_sumariza [V]

### dbo.fcn_maxDataHoraEventoPorProprietario
- Arquivo: `codigo-sql/funcoes/dbo.fcn_maxDataHoraEventoPorProprietario.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @idEvento int
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U]

### dbo.fcn_ObterDadosIdImagemIntegracaoGCT
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosIdImagemIntegracaoGCT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.infracao_imagem [U]

### dbo.fcn_ObterDadosImagemIntegracaoGCT
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosImagemIntegracaoGCT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.imagem [U], dbo.imagem_info [U], dbo.infracao [U], dbo.tipo_imagem [U], dbo.veiculo_imagem [U]

### dbo.fcn_ObterDadosInfracaoIntegracaoGCT
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosInfracaoIntegracaoGCT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.infracao [U], dbo.local [U], dbo.veiculo [U]

### dbo.fcn_ObterDadosMiniaturaImagemIntegracaoGCT
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosMiniaturaImagemIntegracaoGCT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: —

### dbo.fcn_ObterDadosRemessa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosRemessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.cad_especie_processo [U], dbo.cad_marca_cet_processo [U], dbo.cad_veiculo [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.imagem [U], dbo.inconsistencia_cav [U], dbo.infracao [U], dbo.infracao_obliteracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.local [U], dbo.remessa [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_video [U]

### dbo.fcn_ObterDadosRemessa_Infracao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosRemessa_Infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.cad_especie_processo [U], dbo.cad_marca_cet_processo [U], dbo.cad_veiculo [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.imagem [U], dbo.inconsistencia_cav [U], dbo.infracao [U], dbo.infracao_obliteracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.local [U], dbo.remessa [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_video [U]

### dbo.fcn_ObterDadosRemessa_VM
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosRemessa_VM.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.cad_especie [U], dbo.cad_especie_processo [U], dbo.cad_marca_cet [U], dbo.cad_marca_cet_processo [U], dbo.cad_veiculo [U], dbo.configuracao_equipamento_pista [U], dbo.enquadramento [U], dbo.imagem [U], dbo.inconsistencia_cav [U], dbo.infracao [U], dbo.infracao_obliteracao [U], dbo.infracao_processo [U], dbo.infracao_processo_concluido [U], dbo.infracao_remessa [U], dbo.local [U], dbo.percurso [U], dbo.percurso_aux [U], dbo.remessa [U], dbo.veiculo [U], dbo.veiculo_imagem [U], dbo.veiculo_montante [U], dbo.veiculo_video [U]

### dbo.fcn_ObterDadosTarja
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDadosTarja.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_infracao int
- Referencia: dbo.cad_veiculo [U], dbo.configuracao_equipamento [U], dbo.configuracao_equipamento_afericao [U], dbo.configuracao_equipamento_pista [U], dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento [U], dbo.enquadramento_regra_infracao [U], dbo.infracao [U], dbo.local [U], dbo.veiculo [U]

### dbo.fcn_ObterDatasHorasPeriodo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDatasHorasPeriodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicial datetime, @DataFinal datetime
- Referencia: —

### dbo.fcn_ObterDatasMinutosPeriodo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDatasMinutosPeriodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicial datetime, @DataFinal datetime
- Referencia: —

### dbo.fcn_ObterDatasPeriodo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDatasPeriodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @DataInicial date, @DataFinal date
- Referencia: —

### dbo.fcn_ObterDiaSemana
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterDiaSemana.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data date
- Referencia: —

### dbo.fcn_ObterLoteReprovado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterLoteReprovado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: dbo.infracao [U], dbo.infracao_remessa [U], dbo.lote_reprovado [U], dbo.lote_reprovado_detalhe [U], dbo.remessa [U]

### dbo.fcn_ObterLoteReprovadoDetalhe
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterLoteReprovadoDetalhe.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.cad_marca_cet [U], dbo.inconsistencia [U], dbo.infracao_remessa [U], dbo.lote_reprovado [U], dbo.lote_reprovado_detalhe [U], dbo.sis_usuario [U]

### dbo.fcn_ObterProximaSequencia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterProximaSequencia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_veiculo int
- Referencia: dbo.imagem [U], dbo.veiculo_imagem [U]

### dbo.fcn_relatorioFluxoInfracoesPorLocalDia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_relatorioFluxoInfracoesPorLocalDia.sql` · criado 2025-10-06 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.veiculo_pesquisa [V]

### dbo.fcn_relatorioPesagemAnalitico
- Arquivo: `codigo-sql/funcoes/dbo.fcn_relatorioPesagemAnalitico.sql` · criado 2025-10-09 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V], dbo.v_veiculo_pesagem [V]

### dbo.fcn_relatorioPesagemSintetico
- Arquivo: `codigo-sql/funcoes/dbo.fcn_relatorioPesagemSintetico.sql` · criado 2025-10-09 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V], dbo.v_veiculo_pesagem [V]

### dbo.fcn_relatorioVeiculoCargaAnalitico
- Arquivo: `codigo-sql/funcoes/dbo.fcn_relatorioVeiculoCargaAnalitico.sql` · criado 2025-10-09 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.fcn_relatorioVeiculoCargaSintetico
- Arquivo: `codigo-sql/funcoes/dbo.fcn_relatorioVeiculoCargaSintetico.sql` · criado 2025-10-09 · alterado 2025-10-09
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.fcn_ObterDatasPeriodo [IF], dbo.local_vigente [V], dbo.veiculo_sumarizado [U]

### dbo.fcn_ValidarGeracaoLM
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ValidarGeracaoLM.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_remessa int
- Referencia: dbo.enquadramento_regra_infracao [U], dbo.fcn_ObterDadosRemessa [IF], dbo.infracao [U], dbo.infracao_remessa [U], dbo.remessa [U], dbo.veiculo [U]

### dbo.fcn_VerificaAcesso
- Arquivo: `codigo-sql/funcoes/dbo.fcn_VerificaAcesso.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @acao char, @id_usuario int
- Referencia: dbo.sis_menu [U], dbo.sis_menu_direitos [U], dbo.sis_usuario_grupo [U]

### dbo.fn_split_string
- Arquivo: `codigo-sql/funcoes/dbo.fn_split_string.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @string nvarchar, @delimiter nvarchar
- Referencia: a.value

### muralha.fcn_getRelatorioFluxoVeicular
- Arquivo: `codigo-sql/funcoes/muralha.fcn_getRelatorioFluxoVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.fcn_getVeiculoSumarizadoRelatorioLocal [IF], dbo.hora [U]

### muralha.fcn_getRelDistribuicaoPorteVeicular
- Arquivo: `codigo-sql/funcoes/muralha.fcn_getRelDistribuicaoPorteVeicular.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.hora [U], dbo.veiculo_sumarizado [U], muralha.v_porte_veiculo_ref [V]

### muralha.fcn_getRelFluxoMensalPorClassificacao
- Arquivo: `codigo-sql/funcoes/muralha.fcn_getRelFluxoMensalPorClassificacao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @Data_Ini datetime, @Data_Fim datetime, @Id_Local int, @Id_Pista int
- Referencia: dbo.classe_veiculo [U], dbo.hora [U], dbo.veiculo_sumarizado [U]

### muralha.fcn_LocalidadeContrato
- Arquivo: `codigo-sql/funcoes/muralha.fcn_LocalidadeContrato.sql` · criado 2025-06-02 · alterado 2025-10-23
- Parâmetros/retorno: —
- Referencia: dbo.cad_localidade [U]

### muralha.fcn_ObterAlertas
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterAlertas.sql` · criado 2025-06-02 · alterado 2025-09-24
- Parâmetros/retorno: —
- Referencia: dbo.sis_usuario [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.motivo_descarte [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.tipo_registro [U], muralha.veiculo_tempo_real [U]

### muralha.fcn_ObterAlertasAlt
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterAlertasAlt.sql` · criado 2025-06-02 · alterado 2025-07-09
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.sis_usuario [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.motivo_descarte [U], muralha.ocorrencia [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U], muralha.tipo_registro [U], muralha.veiculo_tempo_real [U]

### muralha.fcn_ObterDadosAlertaOcorrencia
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterDadosAlertaOcorrencia.sql` · criado 2025-06-02 · alterado 2025-07-10
- Parâmetros/retorno: @idAlerta uniqueidentifier
- Referencia: dbo.local_vigente [V], dbo.sis_usuario [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.motivo_descarte [U], muralha.ocorrencia [U], muralha.ocorrencia_notificacao [U], muralha.ponto_interesse [U], muralha.status_alerta [U], muralha.status_ocorrencia [U], muralha.tipo_alerta_ocorrencia [U], muralha.tipo_ocorrencia_status [U], muralha.tipo_registro [U], muralha.v_status_ocorrencia_finalizacao [V], muralha.veiculo_tempo_real [U]

### muralha.fcn_ObterImagensObjAlertaVinculado
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterImagensObjAlertaVinculado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_alerta uniqueidentifier
- Referencia: muralha.alerta_veiculo [U], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### muralha.fcn_ObterInfoAlerta
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoAlerta.sql` · criado 2025-07-01 · alterado 2025-07-01
- Parâmetros/retorno: @placa varchar
- Referencia: muralha.alerta [U], muralha.cad_veiculo_monitorado [U], muralha.status_alerta [U], muralha.tipo_alerta_ocorrencia [U]

### muralha.fcn_ObterInfoAntecedentesProprietario
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoAntecedentesProprietario.sql` · criado 2025-07-01 · alterado 2025-07-01
- Parâmetros/retorno: @id_proprietario int
- Referencia: muralha.antecedentes_criminais [U]

### muralha.fcn_ObterInfoAntecedentesProprietarioPorPlaca
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoAntecedentesProprietarioPorPlaca.sql` · criado 2025-07-01 · alterado 2025-07-01
- Parâmetros/retorno: @placa varchar
- Referencia: muralha.antecedentes_criminais [U], muralha.fcn_ObterInfoProprietarioVeiculo [IF]

### muralha.fcn_ObterInfoBoletimOcorrencia
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoBoletimOcorrencia.sql` · criado 2025-07-01 · alterado 2025-09-22
- Parâmetros/retorno: @placa varchar
- Referencia: muralha.boletim [U], muralha.boletim_situacao [U], muralha.registro_fato [U], muralha.registro_fato_tipo [U], muralha.registro_fato_veiculo [U]

### muralha.fcn_ObterInfoBoletimOcorrenciaAlerta
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoBoletimOcorrenciaAlerta.sql` · criado 2025-07-01 · alterado 2025-09-19
- Parâmetros/retorno: @id_alerta uniqueidentifier
- Referencia: muralha.atendimento [U], muralha.boletim [U], muralha.boletim_situacao [U], muralha.ocorrencia [U], muralha.registro_fato [U], muralha.registro_fato_tipo [U]

### muralha.fcn_ObterInfoBoletimOcorrenciaAlertaPorPlaca
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoBoletimOcorrenciaAlertaPorPlaca.sql` · criado 2025-07-01 · alterado 2025-09-19
- Parâmetros/retorno: @placa varchar
- Referencia: muralha.atendimento [U], muralha.boletim [U], muralha.boletim_situacao [U], muralha.fcn_ObterInfoAlerta [IF], muralha.ocorrencia [U], muralha.registro_fato [U], muralha.registro_fato_tipo [U]

### muralha.fcn_ObterInfoProprietarioVeiculo
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoProprietarioVeiculo.sql` · criado 2025-07-01 · alterado 2025-09-04
- Parâmetros/retorno: @placa varchar
- Referencia: dbo.cadastro_veiculo [V], muralha.proprietario [U], muralha.proprietario_veiculo [U]

### muralha.fcn_ObterInfoRegistroFatoAlertaPorPlaca
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoRegistroFatoAlertaPorPlaca.sql` · criado 2025-09-24 · alterado 2025-09-25
- Parâmetros/retorno: @placa varchar
- Referencia: muralha.atendimento [U], muralha.boletim [U], muralha.boletim_situacao [U], muralha.fcn_ObterInfoAlerta [IF], muralha.ocorrencia [U], muralha.registro_fato [U], muralha.registro_fato_status [U], muralha.registro_fato_tipo [U]

### muralha.fcn_ObterInfoRegistroFatoPorPlaca
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoRegistroFatoPorPlaca.sql` · criado 2025-09-23 · alterado 2025-09-24
- Parâmetros/retorno: @placa varchar
- Referencia: muralha.boletim [U], muralha.boletim_situacao [U], muralha.registro_fato [U], muralha.registro_fato_status [U], muralha.registro_fato_tipo [U], muralha.registro_fato_veiculo [U]

### muralha.fcn_ObterInfoVeiculo
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterInfoVeiculo.sql` · criado 2025-07-01 · alterado 2025-09-24
- Parâmetros/retorno: @placa varchar
- Referencia: dbo.cadastro_veiculo [V]

### muralha.fcn_ObterOcorrencias
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterOcorrencias.sql` · criado 2025-06-02 · alterado 2025-09-16
- Parâmetros/retorno: —
- Referencia: dbo.sis_usuario [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.motivo_descarte [U], muralha.ocorrencia [U], muralha.status_ocorrencia [U], muralha.tipo_alerta_ocorrencia [U], muralha.tipo_registro [U], muralha.veiculo_tempo_real [U]

### muralha.fcn_ObterOcorrenciasAlt
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterOcorrenciasAlt.sql` · criado 2025-06-02 · alterado 2025-07-07
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], dbo.sis_usuario [U], muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.motivo_descarte [U], muralha.ocorrencia [U], muralha.status_ocorrencia [U], muralha.tipo_alerta_ocorrencia [U], muralha.tipo_registro [U], muralha.veiculo_tempo_real [U]

### muralha.fcn_perfil_comportamental_base_locais_com_mancha
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_base_locais_com_mancha.sql` · criado 2025-07-31 · alterado 2025-09-04
- Parâmetros/retorno: —
- Referencia: dbo.local_vigente [V], muralha.area_monitorada [U], muralha.equipamentos_area_monitorada [U]

### muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora.sql` · criado 2025-07-31 · alterado 2025-08-06
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: muralha.veiculo_tempo_real [U]

### muralha.fcn_perfil_comportamental_base_rotas_entre_pcls
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_base_rotas_entre_pcls.sql` · criado 2025-07-31 · alterado 2025-10-05
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: muralha.veiculo_tempo_real [U]

### muralha.fcn_perfil_comportamental_estadia_por_manchas
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_estadia_por_manchas.sql` · criado 2025-08-05 · alterado 2025-08-26
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: muralha.fcn_perfil_comportamental_base_locais_com_mancha [IF], muralha.fcn_perfil_comportamental_base_rotas_entre_pcls [IF]

### muralha.fcn_perfil_comportamental_info_veiculo
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_info_veiculo.sql` · criado 2025-07-31 · alterado 2025-09-24
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: dbo.cadastro_veiculo [V], muralha.veiculo_tempo_real [U], muralha.veiculo_tempo_real_imagem [U]

### muralha.fcn_perfil_comportamental_passagens_por_dia_com_mancha
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_passagens_por_dia_com_mancha.sql` · criado 2025-07-31 · alterado 2025-08-06
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: muralha.fcn_perfil_comportamental_base_locais_com_mancha [IF], muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora [IF]

### muralha.fcn_perfil_comportamental_passagens_por_dia_hora_com_mancha
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_passagens_por_dia_hora_com_mancha.sql` · criado 2025-07-31 · alterado 2025-08-06
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: muralha.fcn_perfil_comportamental_base_locais_com_mancha [IF], muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora [IF]

### muralha.fcn_perfil_comportamental_passagens_por_pcl_com_mancha
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_passagens_por_pcl_com_mancha.sql` · criado 2025-07-31 · alterado 2025-08-06
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: muralha.fcn_perfil_comportamental_base_locais_com_mancha [IF], muralha.fcn_perfil_comportamental_base_passagens_por_dia_hora [IF]

### muralha.fcn_perfil_comportamental_rotas_entre_pcls_com_mancha
- Arquivo: `codigo-sql/funcoes/muralha.fcn_perfil_comportamental_rotas_entre_pcls_com_mancha.sql` · criado 2025-07-31 · alterado 2025-07-31
- Parâmetros/retorno: @placa nvarchar, @dataInicio datetime, @dataFim datetime
- Referencia: dbo.fcn_perfil_comportamental_base_rotas_entre_pcls [IF], muralha.fcn_perfil_comportamental_base_locais_com_mancha [IF]

### muralha.fcn_relAlertasDetalhado
- Arquivo: `codigo-sql/funcoes/muralha.fcn_relAlertasDetalhado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime, @dataFim datetime
- Referencia: dbo.local_vigente [V], muralha.cad_veiculo_monitorado [U], muralha.fcn_ObterAlertasAlt [IF]

### muralha.fcn_relOcorrenciasDetalhado
- Arquivo: `codigo-sql/funcoes/muralha.fcn_relOcorrenciasDetalhado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime, @dataFim datetime
- Referencia: dbo.local_vigente [V], muralha.cad_veiculo_monitorado [U], muralha.fcn_ObterOcorrenciasAlt [IF]

### muralha.fn_CompararPlacas
- Arquivo: `codigo-sql/funcoes/muralha.fn_CompararPlacas.sql` · criado 2025-08-13 · alterado 2025-08-14
- Parâmetros/retorno: @Placa1 varchar, @Placa2 varchar
- Referencia: —

### muralha.fn_GeradorAlertaPorCadMonitorado_V2
- Arquivo: `codigo-sql/funcoes/muralha.fn_GeradorAlertaPorCadMonitorado_V2.sql` · criado 2026-08-19 · alterado 2026-08-19
- Parâmetros/retorno: @IdTipoAlertaOcorrencia uniqueidentifier, @PlacaEntrada char
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.config_semelhanca_placa [U]

## Funções escalares (FN) — 60

### dbo.DAC_AIT
- Arquivo: `codigo-sql/funcoes/dbo.DAC_AIT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) smallint OUT, @tipo char, @serie char, @auto int
- Referencia: —

### dbo.fcn_calcula_espaco_imagens_dia
- Arquivo: `codigo-sql/funcoes/dbo.fcn_calcula_espaco_imagens_dia.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) bigint OUT, @dia date
- Referencia: —

### dbo.fcn_checkSemelhancaPlaca
- Arquivo: `codigo-sql/funcoes/dbo.fcn_checkSemelhancaPlaca.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) tinyint OUT, @placa1 char, @placa2 char
- Referencia: dbo.configuracao_semelhanca_placa [U]

### dbo.fcn_contar_sugestoes_amostras
- Arquivo: `codigo-sql/funcoes/dbo.fcn_contar_sugestoes_amostras.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @id_local int, @id_pista tinyint, @dia date, @metrologica bit
- Referencia: dbo.fcn_lista_amostras_periodo_local_pista [IF], dbo.fcn_listar_sugestoes_amostras [IF]

### dbo.fcn_FormataNumero
- Arquivo: `codigo-sql/funcoes/dbo.fcn_FormataNumero.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) varchar OUT, @Numero bigint
- Referencia: —

### dbo.fcn_getAlertaCapturaTravadoHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaCapturaTravadoHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getAlertaCapturaTravado [IF]

### dbo.fcn_getAlertaEventoHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaEventoHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @title nvarchar, @idEvento int, @dataInicio datetime, @mensagem bit
- Referencia: dbo.fcn_getAlertaEvento [IF]

### dbo.fcn_getAlertaExcessoEventosHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaExcessoEventosHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getAlertaExcessoEventos [IF]

### dbo.fcn_getAlertaFalhaCarregamentoAgendaHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaFalhaCarregamentoAgendaHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.eventos_csx_pesquisa [V]

### dbo.fcn_getAlertaLocaisConexaoInstavelHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaLocaisConexaoInstavelHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getAlertaLocaisConexaoInstavel [IF]

### dbo.fcn_getAlertaLocaisOfflineHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaLocaisOfflineHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getAlertaLocaisOffLine [IF]

### dbo.fcn_getAlertaNaoEnviaEventosHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAlertaNaoEnviaEventosHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getAlertaNaoEnviaEventos [IF]

### dbo.fcn_getAproveitamentoImagens
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAproveitamentoImagens.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @data date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.processo [U]

### dbo.fcn_getAproveitamentoImagensAgrupado
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAproveitamentoImagensAgrupado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @dataLimite date
- Referencia: dbo.fcn_getAproveitamentoImagensAgrupadoPeriodo [FN], dbo.infracao [U], dbo.processo [U]

### dbo.fcn_getAproveitamentoImagensAgrupadoPeriodo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getAproveitamentoImagensAgrupadoPeriodo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @dataInicio date, @dataFim date
- Referencia: dbo.inconsistencia [U], dbo.infracao [U], dbo.local_vigente [V], dbo.processo [U]

### dbo.fcn_getClassificacaoCad
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getClassificacaoCad.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @placa char
- Referencia: dbo.cad_tipo [U], dbo.cad_veiculo [U]

### dbo.fcn_getClassificacaoPerfil
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getClassificacaoPerfil.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @classe char
- Referencia: —

### dbo.fcn_getClassificacaoPNCT_v2
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getClassificacaoPNCT_v2.sql` · criado 2025-09-30 · alterado 2025-09-30
- Parâmetros/retorno: (retorno) nchar OUT, @id_classe char, @qtde_eixos tinyint, @comprimento decimal, @pbt float, @distancia_E1E2 float, @distancia_E2E3 float, @distancia_E3E4 float, @distancia_E4E5 float, @distancia_E5E6 float, @distancia_E6E7 float, @distancia_E7E8 float, @distancia_E8E9 float
- Referencia: —

### dbo.fcn_getClassificacaoVeiculoRef
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getClassificacaoVeiculoRef.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) char OUT, @comprimento float
- Referencia: dbo.classificacao_veiculo_ref [U]

### dbo.fcn_getDataInicioSumariza
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getDataInicioSumariza.sql` · criado 2025-06-02 · alterado 2025-10-21
- Parâmetros/retorno: (retorno) date OUT
- Referencia: —

### dbo.fcn_getDestinatariosAlerta
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getDestinatariosAlerta.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: —

### dbo.fcn_getErrosProcessamentoHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getErrosProcessamentoHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @data datetime
- Referencia: dbo.fcn_getErrosProcessamento [IF]

### dbo.fcn_getErrosRespostaPoolingHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getErrosRespostaPoolingHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @title nvarchar, @dataInicio datetime
- Referencia: dbo.fcn_getErrosRespostaPooling [IF]

### dbo.fcn_getHistoricoAfericaoHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getHistoricoAfericaoHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getHistoricoAfericao [IF]

### dbo.fcn_getHistoricoManutencaoHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getHistoricoManutencaoHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getHistoricoManutencao [IF]

### dbo.fcn_getIdConfiguracaoEquipamento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getIdConfiguracaoEquipamento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @idLocal int, @dataHora datetime
- Referencia: dbo.local [U]

### dbo.fcn_getImagensProcessamentoHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getImagensProcessamentoHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @id_processo int, @consistentes bit, @inconsistentes bit
- Referencia: dbo.fcn_ImagensProcessamento [IF], dbo.processo [U]

### dbo.fcn_getInfracaoHorarioValida
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getInfracaoHorarioValida.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @id_infracao int
- Referencia: dbo.configuracao_equipamento_regra_infracao [U], dbo.enquadramento_regra_infracao [U], dbo.infracao [U], dbo.local [U]

### dbo.fcn_getNomeContrato
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getNomeContrato.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.chave_valor [U]

### dbo.fcn_getProdutividadeOperacoes
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProdutividadeOperacoes.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.infracao_processo [U], dbo.processo [U]

### dbo.fcn_getProdutividadeOperadoresHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProdutividadeOperadoresHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getProdutividadeOperadores [IF]

### dbo.fcn_getProximasAfericoesHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getProximasAfericoesHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getProximasAfericoes [IF]

### dbo.fcn_getRelatorioIsencoesHTML
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioIsencoesHTML.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: dbo.fcn_getRelatorioIsencoes [IF]

### dbo.fcn_getSqlVerificaFiltros
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getSqlVerificaFiltros.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @id_infracao int, @id_enquadramento int, @id_processo int, @id_local int, @id_pista int, @id_classe char, @data_ini datetime, @data_fim datetime, @sql_criterio varchar, @select_count bit
- Referencia: —

### dbo.fcn_getUltimaSemanaProcessada
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getUltimaSemanaProcessada.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT
- Referencia: dbo.infracao [U], dbo.processo [U]

### dbo.fcn_InitCap
- Arquivo: `codigo-sql/funcoes/dbo.fcn_InitCap.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) varchar OUT, @InputString varchar
- Referencia: —

### dbo.fcn_ObterErrosRemessa
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterErrosRemessa.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @id_remessa int
- Referencia: dbo.infracao [U], dbo.infracao_processo [U], dbo.infracao_processo_digitacao [U], dbo.infracao_remessa [U], dbo.movimento_importacao [U], dbo.remessa [U], dbo.remessa_amostragem [U]

### dbo.fcn_ObterVariavelFluxo
- Arquivo: `codigo-sql/funcoes/dbo.fcn_ObterVariavelFluxo.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) float OUT, @fluxo int
- Referencia: —

### dbo.fcn_PMESP_ObterTempoOffline
- Arquivo: `codigo-sql/funcoes/dbo.fcn_PMESP_ObterTempoOffline.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @id_local int
- Referencia: dbo.fcn_ObterDatasMinutosPeriodo [IF], dbo.pmesp_evento_conexao [U]

### dbo.fcn_pontua_infracao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_pontua_infracao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) smallint OUT, @id_infracao int
- Referencia: dbo.infracao_amostra [V], dbo.infracao_processo_concluido [U], dbo.processo [U]

### dbo.fcn_SplitString
- Arquivo: `codigo-sql/funcoes/dbo.fcn_SplitString.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) varchar OUT, @string nvarchar, @delimiter char, @part int
- Referencia: —

### dbo.fcn_totalErrosPlacas
- Arquivo: `codigo-sql/funcoes/dbo.fcn_totalErrosPlacas.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) tinyint OUT, @placa1 char, @placa2 char
- Referencia: —

### dbo.fn_calculateJaro
- Arquivo: `codigo-sql/funcoes/dbo.fn_calculateJaro.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) float OUT, @str1 varchar, @str2 varchar
- Referencia: dbo.fn_calculateMatchWindow [FN], dbo.fn_calculateTranspositions [FN], dbo.fn_GetCommonCharacters [FN]

### dbo.fn_calculateMatchWindow
- Arquivo: `codigo-sql/funcoes/dbo.fn_calculateMatchWindow.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @s1_len int, @s2_len int
- Referencia: —

### dbo.fn_calculatePrefixLength
- Arquivo: `codigo-sql/funcoes/dbo.fn_calculatePrefixLength.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @firstWord varchar, @secondWord varchar
- Referencia: —

### dbo.fn_calculateSimilarity
- Arquivo: `codigo-sql/funcoes/dbo.fn_calculateSimilarity.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) float OUT, @str1 varchar, @str2 varchar
- Referencia: dbo.fn_calculateJaro [FN], dbo.fn_calculatePrefixLength [FN]

### dbo.fn_calculateTranspositions
- Arquivo: `codigo-sql/funcoes/dbo.fn_calculateTranspositions.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @s1_len int, @str1 varchar, @str2 varchar
- Referencia: —

### dbo.fn_diagramobjects
- Arquivo: `codigo-sql/funcoes/dbo.fn_diagramobjects.sql` · criado 2025-06-06 · alterado 2025-06-06
- Parâmetros/retorno: (retorno) int OUT
- Referencia: —

### dbo.fn_FormatarSegundosEmDHM
- Arquivo: `codigo-sql/funcoes/dbo.fn_FormatarSegundosEmDHM.sql` · criado 2025-10-03 · alterado 2025-10-03
- Parâmetros/retorno: (retorno) varchar OUT, @total_segundos bigint
- Referencia: —

### dbo.fn_GetCommonCharacters
- Arquivo: `codigo-sql/funcoes/dbo.fn_GetCommonCharacters.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) varchar OUT, @firstWord varchar, @secondWord varchar, @matchWindow int
- Referencia: —

### dbo.InitCap
- Arquivo: `codigo-sql/funcoes/dbo.InitCap.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) varchar OUT, @inStr varchar
- Referencia: —

### dbo.MAX_DATE
- Arquivo: `codigo-sql/funcoes/dbo.MAX_DATE.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) datetime OUT, @date_a datetime, @date_b datetime
- Referencia: —

### dbo.TEMPO_DECORRIDO
- Arquivo: `codigo-sql/funcoes/dbo.TEMPO_DECORRIDO.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) varchar OUT, @data datetime
- Referencia: —

### dbo.TryConvertInt
- Arquivo: `codigo-sql/funcoes/dbo.TryConvertInt.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @Value varchar
- Referencia: —

### dbo.VerificarEmail
- Arquivo: `codigo-sql/funcoes/dbo.VerificarEmail.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) bit OUT, @email varchar
- Referencia: —

### muralha.calcular_distancia_km
- Arquivo: `codigo-sql/funcoes/muralha.calcular_distancia_km.sql` · criado 2025-11-27 · alterado 2025-11-27
- Parâmetros/retorno: (retorno) decimal OUT, @lat1 decimal, @lon1 decimal, @lat2 decimal, @lon2 decimal
- Referencia: —

### muralha.fcn_getDispositivosContagensFluxoDiarioJson
- Arquivo: `codigo-sql/funcoes/muralha.fcn_getDispositivosContagensFluxoDiarioJson.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT, @id_local int
- Referencia: dbo.hora [U], dbo.local_vigente [V], muralha.veiculo_tempo_real [U]

### muralha.fcn_ObterRemetenteEmailmuralha
- Arquivo: `codigo-sql/funcoes/muralha.fcn_ObterRemetenteEmailmuralha.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) nvarchar OUT
- Referencia: —

### muralha.fn_ValidarPlacasIguaisSemelhantes
- Arquivo: `codigo-sql/funcoes/muralha.fn_ValidarPlacasIguaisSemelhantes.sql` · criado 2025-06-02 · alterado 2025-06-24
- Parâmetros/retorno: (retorno) int OUT, @placa1 char, @placa2 char
- Referencia: muralha.config_semelhanca_placa [U]

### muralha.fn_ValidarPlacasParciaisIguais
- Arquivo: `codigo-sql/funcoes/muralha.fn_ValidarPlacasParciaisIguais.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: (retorno) int OUT, @placaEntrada char, @placaParcial char
- Referencia: —

## Funções tabela (TF) — 16

### dbo.Date_Table
- Arquivo: `codigo-sql/funcoes/dbo.Date_Table.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @data_inicial datetime, @data_fim datetime
- Referencia: —

### dbo.fcn_getRelatorioProcessamentoVelsis
- Arquivo: `codigo-sql/funcoes/dbo.fcn_getRelatorioProcessamentoVelsis.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio date, @dataFim date
- Referencia: dbo.configuracao_equipamento [U], dbo.equipamento_estatico [U], dbo.inconsistencia [U], dbo.infracao [U], dbo.local [U], dbo.veiculo [U]

### dbo.fcn_LocalEventosConexao
- Arquivo: `codigo-sql/funcoes/dbo.fcn_LocalEventosConexao.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @dataInicio datetime, @dataFim datetime
- Referencia: dbo.local_vigente [V], dbo.status_conexao [U]

### dbo.fcn_periodos_falha_horario
- Arquivo: `codigo-sql/funcoes/dbo.fcn_periodos_falha_horario.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @id_local int, @data_ini datetime, @data_fim datetime
- Referencia: dbo.eventos_csx [U], dbo.eventos_csx_desc_proprietario [U], dbo.MAX_DATE [FN]

### dbo.fcn_pesquisaIsento
- Arquivo: `codigo-sql/funcoes/dbo.fcn_pesquisaIsento.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @placa char, @id_enquadramento int, @data_base datetime
- Referencia: dbo.cad_isento [U], dbo.cad_isento_arquivo [U]

### dbo.fcn_pesquisaIsento_ANT
- Arquivo: `codigo-sql/funcoes/dbo.fcn_pesquisaIsento_ANT.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @placa char, @id_enquadramento int, @data_base datetime
- Referencia: dbo.cad_isento [U], dbo.cad_isento_arquivo [U]

### dbo.fcn_pesquisaIsento_NOVO
- Arquivo: `codigo-sql/funcoes/dbo.fcn_pesquisaIsento_NOVO.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @placa char, @id_enquadramento int, @data_base datetime
- Referencia: dbo.cad_isento [U], dbo.cad_isento_arquivo [U]

### dbo.fn_gera_alerta
- Arquivo: `codigo-sql/funcoes/dbo.fn_gera_alerta.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: —
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.fcn_ValidarPlacasIguaisSemelhantes, muralha.tipo_alerta_ocorrencia [U]

### muralha.fn_GeradorAlertaPorCadMonitorado
- Arquivo: `codigo-sql/funcoes/muralha.fn_GeradorAlertaPorCadMonitorado.sql` · criado 2025-06-02 · alterado 2025-06-02
- Parâmetros/retorno: @IdTipoAlertaOcorrencia uniqueidentifier, @PlacaEntrada char
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.fn_ValidarPlacasIguaisSemelhantes [FN], muralha.fn_ValidarPlacasParciaisIguais [FN], muralha.tipo_alerta_ocorrencia [U]

### muralha.fn_GeradorAlertaPorCadMonitorado_TESTE
- Arquivo: `codigo-sql/funcoes/muralha.fn_GeradorAlertaPorCadMonitorado_TESTE.sql` · criado 2026-08-12 · alterado 2026-08-12
- Parâmetros/retorno: @IdTipoAlertaOcorrencia uniqueidentifier, @PlacaEntrada char
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.config_semelhanca_placa [U], muralha.fn_ValidarPlacasParciaisIguais_V2 [TF]

### muralha.fn_GeradorAlertaPorCadMonitorado_V2_20260819
- Arquivo: `codigo-sql/funcoes/muralha.fn_GeradorAlertaPorCadMonitorado_V2_20260819.sql` · criado 2026-08-19 · alterado 2026-08-19
- Parâmetros/retorno: @IdTipoAlertaOcorrencia uniqueidentifier, @PlacaEntrada char
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.fn_ValidarPlacasIguaisSemelhantes_V2 [TF], muralha.fn_ValidarPlacasParciaisIguais_V2 [TF], muralha.tipo_alerta_ocorrencia [U]

### muralha.fn_GeradorAlertaPorCadMonitorado_V3
- Arquivo: `codigo-sql/funcoes/muralha.fn_GeradorAlertaPorCadMonitorado_V3.sql` · criado 2025-10-02 · alterado 2025-10-02
- Parâmetros/retorno: @IdTipoAlertaOcorrencia uniqueidentifier, @PlacaEntrada char, @DataVeiculo datetime, @Id_Local int
- Referencia: muralha.cad_veiculo_monitorado [U], muralha.cad_veiculo_monitorado_equipamento [U], muralha.cad_veiculo_monitorado_periodo [U], muralha.fn_ValidarPlacasIguaisSemelhantes_V2 [TF], muralha.fn_ValidarPlacasParciaisIguais_V2 [TF], muralha.tipo_alerta_ocorrencia [U]

### muralha.fn_ObterSom
- Arquivo: `codigo-sql/funcoes/muralha.fn_ObterSom.sql` · criado 2025-08-21 · alterado 2025-08-22
- Parâmetros/retorno: @IdAlerta uniqueidentifier
- Referencia: muralha.alerta [U], muralha.cad_veiculo_monitorado [U], muralha.config_alarme [U], muralha.config_alarme_tipo [U]

### muralha.fn_RegistroFatoRequerEPossuiBO
- Arquivo: `codigo-sql/funcoes/muralha.fn_RegistroFatoRequerEPossuiBO.sql` · criado 2025-09-22 · alterado 2025-09-22
- Parâmetros/retorno: @IdRegistroFato int
- Referencia: muralha.registro_fato [U], muralha.registro_fato_natureza [U], muralha.registro_fato_tipo [U]

### muralha.fn_ValidarPlacasIguaisSemelhantes_V2
- Arquivo: `codigo-sql/funcoes/muralha.fn_ValidarPlacasIguaisSemelhantes_V2.sql` · criado 2025-08-14 · alterado 2025-08-14
- Parâmetros/retorno: @placa1 char, @placa2 char, @erros_cad_monitorado tinyint
- Referencia: muralha.config_semelhanca_placa [U]

### muralha.fn_ValidarPlacasParciaisIguais_V2
- Arquivo: `codigo-sql/funcoes/muralha.fn_ValidarPlacasParciaisIguais_V2.sql` · criado 2025-08-14 · alterado 2025-08-14
- Parâmetros/retorno: @placaEntrada char, @placaParcial char
- Referencia: —

## Triggers — 8

### dbo.TG_veiculo_estatistica_verifica_classificacao
- Arquivo: `codigo-sql/triggers/dbo.TG_veiculo_estatistica_verifica_classificacao.sql` · criado 2025-10-12 · alterado 2025-10-12
- Parâmetros/retorno: —
- Referencia: dbo.inserted, dbo.veiculo_estatistica [U]

### dbo.TG_veiculo_estatistica_verificar_placa_mercosul
- Arquivo: `codigo-sql/triggers/dbo.TG_veiculo_estatistica_verificar_placa_mercosul.sql` · criado 2025-10-12 · alterado 2025-10-12
- Parâmetros/retorno: —
- Referencia: dbo.inserted, dbo.veiculo_estatistica [U]

### dbo.TG_veiculo_pesagem_controle_verifica_classificacao
- Arquivo: `codigo-sql/triggers/dbo.TG_veiculo_pesagem_controle_verifica_classificacao.sql` · criado 2025-10-12 · alterado 2025-10-12
- Parâmetros/retorno: —
- Referencia: dbo.inserted, dbo.veiculo [U], dbo.veiculo_estatistica [U], dbo.veiculo_pesagem_eixo [U]

### dbo.TG_veiculo_verifica_classificacao
- Arquivo: `codigo-sql/triggers/dbo.TG_veiculo_verifica_classificacao.sql` · criado 2025-10-12 · alterado 2025-10-12
- Parâmetros/retorno: —
- Referencia: dbo.inserted, dbo.veiculo [U]

### dbo.TG_veiculo_verificar_placa_mercosul
- Arquivo: `codigo-sql/triggers/dbo.TG_veiculo_verificar_placa_mercosul.sql` · criado 2025-10-12 · alterado 2025-10-12
- Parâmetros/retorno: —
- Referencia: dbo.inserted, dbo.veiculo [U]

### muralha.TG_veiculo_tempo_real_verificar_placa_mercosul
- Arquivo: `codigo-sql/triggers/muralha.TG_veiculo_tempo_real_verificar_placa_mercosul.sql` · criado 2025-09-10 · alterado 2025-09-10
- Parâmetros/retorno: —
- Referencia: dbo.inserted, muralha.veiculo_tempo_real [U]

### muralha.TRG_alerta_semelhanca_placa_after_insert
- Arquivo: `codigo-sql/triggers/muralha.TRG_alerta_semelhanca_placa_after_insert.sql` · criado 2025-08-14 · alterado 2025-08-14
- Parâmetros/retorno: —
- Referencia: dbo.inserted, muralha.alerta [U], muralha.alerta_veiculo [U], muralha.cad_veiculo_monitorado [U], muralha.fn_CompararPlacas [IF], muralha.veiculo_tempo_real [U]

### muralha.trg_ins_localizacao_atual
- Arquivo: `codigo-sql/triggers/muralha.trg_ins_localizacao_atual.sql` · criado 2025-07-31 · alterado 2025-07-31
- Parâmetros/retorno: —
- Referencia: dbo.inserted, muralha.agente_localizacao_hist [U]

