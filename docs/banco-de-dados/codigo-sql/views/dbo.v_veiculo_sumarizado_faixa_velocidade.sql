CREATE VIEW [dbo].[v_veiculo_sumarizado_faixa_velocidade] AS   
  
SELECT dados.dia,  
    dados.hora,  
    dados.id_local,  
    dados.id_pista,  
    dados.faixa_velocidade,  
    COUNT(dados.id_veiculo_unic) AS veiculos_detectados  
FROM   (  
   SELECT vp.id_local,  
       CAST(vp.data AS DATE) AS dia,  
       DATEPART(HOUR, vp.data) AS hora,  
       vp.pista AS id_pista,  
       vp.id_veiculo_unic,  
       CASE WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 0 AND 5 THEN 0  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 6 AND 10 THEN 1  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 11 AND 15 THEN 2  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 16 AND 20 THEN 3  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 21 AND 25 THEN 4  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 26 AND 30 THEN 5  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 31 AND 35 THEN 6  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 36 AND 40 THEN 7  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 41 AND 45 THEN 8  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 46 AND 50 THEN 9  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 51 AND 55 THEN 10  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 56 AND 60 THEN 11  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 61 AND 65 THEN 12  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 66 AND 70 THEN 13  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 71 AND 75 THEN 14  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 76 AND 80 THEN 15  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 81 AND 85 THEN 16  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 86 AND 90 THEN 17  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 91 AND 95 THEN 18  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 96 AND 100 THEN 19  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 101 AND 105 THEN 20  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 106 AND 110 THEN 21  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 111 AND 115 THEN 22  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 116 AND 120 THEN 23  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 121 AND 125 THEN 24  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 126 AND 130 THEN 25  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 131 AND 135 THEN 26  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 136 AND 140 THEN 27  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 141 AND 145 THEN 28  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 146 AND 150 THEN 29  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 151 AND 155 THEN 30  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 156 AND 160 THEN 31  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 161 AND 165 THEN 32  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 166 AND 170 THEN 33  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 171 AND 175 THEN 34  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 176 AND 180 THEN 35  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 181 AND 185 THEN 36  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 186 AND 190 THEN 37  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 191 AND 195 THEN 38  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) BETWEEN 196 AND 200 THEN 39  
      WHEN ROUND(CAST(vp.velocidade AS FLOAT), 0) > 200 THEN 40  
       END AS faixa_velocidade  
   FROM   veiculo_pesquisa vp (NOLOCK)  
    ) dados  
GROUP BY  
    dados.dia,  
    dados.hora,  
    dados.id_local,  
    dados.id_pista,  
    dados.faixa_velocidade  
  
