
CREATE VIEW [dbo].[v_enquadramentos_manutencao]
AS

SELECT tipo_apait, CASE WHEN tipo_apait <> 'QV' THEN id_enquadramento ELSE NULL END AS id_enquadramento, RTRIM(descricao_apait) descricao_apait FROM enquadramento_regra_infracao (NOLOCK) 
WHERE tipo_apait IS NOT NULL AND tipo_apait NOT IN ('QX','QT') 
GROUP BY tipo_apait, CASE WHEN tipo_apait <> 'QV' THEN id_enquadramento ELSE NULL END, descricao_apait


