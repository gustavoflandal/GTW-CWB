CREATE VIEW [dbo].[v_ppv_qfv] AS

SELECT 
pqt.id_classificacao, pqt.codigo, pqt.numero_grupos, pqt.numero_eixos, pqt.pbt, pqt.pbt_tolerancia,
pqt.comprimento, pqt.comprimento_aet,
pqd1.limite_inferior AS comprimento_ini, pqd1.limite_superior AS comprimento_fim, pqt.descricao, pqt.requer_aet
,pqg1.eixos AS grupo1_eixos, pqgc1.carga AS grupo1_carga, pqgc1.carga_tolerancia AS grupo1_carga_tolerancia, pqgc1.descrição AS grupo1_desc, pqgc1.eixo_1 AS grupo1_eixo1, pqgc1.eixo_2 AS grupo1_eixo2, pqgc1.eixo_3 AS grupo1_eixo3 
,pqg2.eixos AS grupo2_eixos, pqgc2.carga AS grupo2_carga, pqgc2.carga_tolerancia AS grupo2_carga_tolerancia, pqgc2.descrição AS grupo2_desc, pqgc2.eixo_1 AS grupo2_eixo1, pqgc2.eixo_2 AS grupo2_eixo2, pqgc2.eixo_3 AS grupo2_eixo3 
,pqg3.eixos AS grupo3_eixos, pqgc3.carga AS grupo3_carga, pqgc3.carga_tolerancia AS grupo3_carga_tolerancia, pqgc3.descrição AS grupo3_desc, pqgc3.eixo_1 AS grupo3_eixo1, pqgc3.eixo_2 AS grupo3_eixo2, pqgc3.eixo_3 AS grupo3_eixo3 
,pqg4.eixos AS grupo4_eixos, pqgc4.carga AS grupo4_carga, pqgc4.carga_tolerancia AS grupo4_carga_tolerancia, pqgc4.descrição AS grupo4_desc, pqgc4.eixo_1 AS grupo4_eixo1, pqgc4.eixo_2 AS grupo4_eixo2, pqgc4.eixo_3 AS grupo4_eixo3 
,pqg5.eixos AS grupo5_eixos, pqgc5.carga AS grupo5_carga, pqgc5.carga_tolerancia AS grupo5_carga_tolerancia, pqgc5.descrição AS grupo5_desc, pqgc5.eixo_1 AS grupo5_eixo1, pqgc5.eixo_2 AS grupo5_eixo2, pqgc5.eixo_3 AS grupo5_eixo3 
,pqg6.eixos AS grupo6_eixos, pqgc6.carga AS grupo6_carga, pqgc6.carga_tolerancia AS grupo6_carga_tolerancia, pqgc6.descrição AS grupo6_desc, pqgc6.eixo_1 AS grupo6_eixo1, pqgc6.eixo_2 AS grupo6_eixo2, pqgc6.eixo_3 AS grupo6_eixo3 
,pqg7.eixos AS grupo7_eixos, pqgc7.carga AS grupo7_carga, pqgc7.carga_tolerancia AS grupo7_carga_tolerancia, pqgc7.descrição AS grupo7_desc, pqgc7.eixo_1 AS grupo7_eixo1, pqgc7.eixo_2 AS grupo7_eixo2, pqgc7.eixo_3 AS grupo7_eixo3 
,pqd_d12.limite_inferior AS D12_inferior, pqd_d12.limite_superior AS D12_superior
,pqd_d23.limite_inferior AS D23_inferior, pqd_d23.limite_superior AS D23_superior
,pqd_d34.limite_inferior AS D34_inferior, pqd_d34.limite_superior AS D34_superior
,pqd_d45.limite_inferior AS D45_inferior, pqd_d45.limite_superior AS D45_superior
,pqd_d56.limite_inferior AS D56_inferior, pqd_d56.limite_superior AS D56_superior
,pqd_d67.limite_inferior AS D67_inferior, pqd_d67.limite_superior AS D67_superior
,pqd_d78.limite_inferior AS D78_inferior, pqd_d78.limite_superior AS D78_superior
,pqd_d89.limite_inferior AS D89_inferior, pqd_d89.limite_superior AS D89_superior
FROM ppv_qfv_tipos pqt
LEFT JOIN ppv_qfv_grupo pqg1 ON pqt.grupo_1 = pqg1.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc1 ON pqg1.id_conf_eixos = pqgc1.id_conf_eixos
LEFT JOIN ppv_qfv_grupo pqg2 ON pqt.grupo_2 = pqg2.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc2 ON pqg2.id_conf_eixos = pqgc2.id_conf_eixos
LEFT JOIN ppv_qfv_grupo pqg3 ON pqt.grupo_3 = pqg3.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc3 ON pqg3.id_conf_eixos = pqgc3.id_conf_eixos
LEFT JOIN ppv_qfv_grupo pqg4 ON pqt.grupo_4 = pqg4.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc4 ON pqg4.id_conf_eixos = pqgc4.id_conf_eixos
LEFT JOIN ppv_qfv_grupo pqg5 ON pqt.grupo_5 = pqg5.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc5 ON pqg5.id_conf_eixos = pqgc5.id_conf_eixos
LEFT JOIN ppv_qfv_grupo pqg6 ON pqt.grupo_6 = pqg6.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc6 ON pqg6.id_conf_eixos = pqgc6.id_conf_eixos
LEFT JOIN ppv_qfv_grupo pqg7 ON pqt.grupo_7 = pqg7.id_grupo
LEFT JOIN ppv_qfv_grupo_config pqgc7 ON pqg7.id_conf_eixos = pqgc7.id_conf_eixos
LEFT JOIN ppv_qfv_distancias pqd1 ON pqt.comprimento = pqd1.id_distancia
LEFT JOIN ppv_qfv_distancias pqd2 ON pqt.comprimento_aet = pqd2.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d12 ON pqt.D12 = pqd_d12.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d23 ON pqt.D23 = pqd_d23.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d34 ON pqt.D34 = pqd_d34.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d45 ON pqt.D45 = pqd_d45.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d56 ON pqt.D56 = pqd_d56.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d67 ON pqt.D67 = pqd_d67.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d78 ON pqt.D78 = pqd_d78.id_distancia
LEFT JOIN ppv_qfv_distancias pqd_d89 ON pqt.D89 = pqd_d89.id_distancia
