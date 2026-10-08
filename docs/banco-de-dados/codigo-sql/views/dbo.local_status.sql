
CREATE VIEW [dbo].[local_status]
AS
SELECT     
	l.id_local,
	l.serie_equipamento, 
	lsc.status as status_conexao,
	lsc.data_atualizacao as data_atualizacao_conexao,
	lse.status as status_energia,
	lse.data_atualizacao as data_atualizacao_energia,
	lsd.status as status_div,
	lsd.data_atualizacao as data_atualizacao_div,
	lsc.ip,
	dbo.MAX_DATE( 
		lse.data_atualizacao , 
		dbo.MAX_DATE( 
			lsc.data_atualizacao ,  
			lsd.data_atualizacao )
		) AS data_atualizacao  
	
FROM local_vigente AS l (nolock)
	LEFT JOIN local_status_div lsd (nolock) 
		ON lsd.id_local = l.id_local
	LEFT JOIN local_status_conexao lsc (nolock) 
		ON lsc.id_local = l.id_local
	LEFT JOIN local_status_energia lse (nolock) 
		ON lse.id_local = l.id_local


