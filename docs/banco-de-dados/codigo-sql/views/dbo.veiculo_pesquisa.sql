CREATE   VIEW [dbo].[veiculo_pesquisa]  
AS  
  
SELECT  
    v.id_veiculo,  
    v.id_veiculo_local,  
    v.data,  
    v.placa,  
    v.velocidade,  
    v.comprimento,  
    v.pista,  
    v.flag,  
    v.segundos,  
    v.id_veiculo_unic,  
    v.id_classe,  
    v.id_local,  
    v.sequencia_local,  
    v.ocupacao,  
    (CAST(ROUND(v.velocidade, 0, 1) AS INT) / 10) AS id_faixa_velocidade,  
    v.velocidade_aux AS velocidade_piezo,  
    v.velocidade_media,  
    v.serie_equipamento_Montante,  
    v.id_veiculo_Local_Montante,  
    v.numero_eixos,  
    vpe.peso_total_eixos,  
    v.rodagem_dupla,  
    v.categoria,  
    v.com_pesagem,  
    v.placa_mercosul,  
    v.classificacao,  
	v.id_captura,
    CASE  
        WHEN v.id_classe IN ('M','B') THEN 'MOTO'  
        WHEN v.id_classe IN ('T','P','V','F',' ') THEN 'PEQUENO'  
        WHEN v.id_classe = 'O'  
             OR (v.id_classe IN ('C','Q') AND v.comprimento < 15)  
            THEN 'MÉDIO'  
        ELSE 'GRANDE'  
    END AS porte_veiculo  
FROM veiculo v (NOLOCK)  
  
LEFT JOIN (  
    SELECT  
        id_veiculo_unic,  
        SUM(peso) AS peso_total_eixos  
    FROM dbo.veiculo_pesagem_eixo  
    GROUP BY id_veiculo_unic  
) vpe  
    ON vpe.id_veiculo_unic = v.id_veiculo_unic  
  
UNION  
  
SELECT  
    NULL AS id_veiculo,  
    id_veiculo_local,  
    data,  
    placa,  
    velocidade,  
    comprimento,  
    pista,  
    flag,  
    segundos,  
    id_veiculo_unic,  
    id_classe,  
    id_local,  
    sequencia_local,  
    ocupacao,  
    (CAST(ROUND(velocidade, 0, 1) AS INT) / 10) AS id_faixa_velocidade,  
    velocidade_aux AS velocidade_piezo,  
    velocidade_media,  
    serie_equipamento_Montante,  
    id_veiculo_Local_Montante,  
    numero_eixos,  
    CAST(NULL AS DECIMAL(18,2)) AS peso_total_eixos,  
    rodagem_dupla,  
    categoria,  
    com_pesagem,  
    placa_mercosul,  
    classificacao,  
	id_captura,
    CASE  
        WHEN id_classe IN ('M','B') THEN 'MOTO'  
        WHEN id_classe IN ('T','P','V','F',' ') THEN 'PEQUENO'  
        WHEN id_classe = 'O'  
             OR (id_classe IN ('C','Q') AND comprimento < 15)  
            THEN 'MÉDIO'  
        ELSE 'GRANDE'  
    END AS porte_veiculo  
FROM veiculo_estatistica (NOLOCK);