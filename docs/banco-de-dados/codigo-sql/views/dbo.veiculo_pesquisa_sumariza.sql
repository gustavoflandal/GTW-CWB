CREATE VIEW [dbo].[veiculo_pesquisa_sumariza]      
AS      
      
 SELECT id_veiculo      
    ,id_veiculo_local      
    ,data      
    ,placa      
    ,velocidade      
    ,comprimento      
    ,pista      
    ,flag      
    ,segundos      
    ,id_veiculo_unic      
    ,id_classe      
    ,id_local      
    ,sequencia_local      
    ,ocupacao      
 FROM   veiculo (NOLOCK)   
 --WHERE  data >= '2025-10-13 13:00:00:00'
      
 UNION      
      
 SELECT NULL AS [id_veiculo]      
    ,id_veiculo_local      
    ,data      
    ,placa      
    ,velocidade      
    ,comprimento      
    ,pista      
    ,flag      
    ,segundos      
    ,id_veiculo_unic      
    ,id_classe      
    ,id_local      
    ,sequencia_local      
    ,ocupacao      
 FROM   veiculo_estatistica (NOLOCK)      
 --WHERE  data >= '2025-10-13 13:00:00:00'
