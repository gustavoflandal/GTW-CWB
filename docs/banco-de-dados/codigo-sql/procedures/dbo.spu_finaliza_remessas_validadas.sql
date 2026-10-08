
CREATE PROCEDURE [dbo].[spu_finaliza_remessas_validadas]  
AS

BEGIN

	SET NOCOUNT ON

    DECLARE @count INT = 0 

	IF ( EXISTS(SELECT TOP 1 1 
				FROM 
					remessa (nolock) 
				WHERE	data_exportacao IS NOT NULL 
					AND data_validacao IS NULL))

		BEGIN	

			DECLARE @remessas_validadas TABLE 
			(
				id_remessa INT, 
				total_infracao INT, 
				data_conclusao DATETIME
			)

			INSERT INTO @remessas_validadas 
			SELECT 
				rem.id_remessa, 
				rem.total_infracao,
				MAX(ipc.data_conclusao) AS data_conclusao 
			FROM remessa rem (NOLOCK) 
				INNER JOIN infracao_remessa ir (nolock) 
					ON rem.id_remessa = ir.id_remessa 
				INNER JOIN infracao_processo_concluido ipc (nolock) 
					ON	ir.id_infracao = ipc.id_infracao 
					AND ipc.id_processo = 3 
			WHERE	rem.data_exportacao IS NOT NULL 
				AND rem.data_validacao IS NULL 
			GROUP BY 
				rem.id_remessa, rem.total_infracao
			HAVING 
				COUNT(ipc.id_infracao_processo_concluido) = rem.total_infracao 

			IF(@@ROWCOUNT > 0)
				
				BEGIN
				
					BEGIN TRY 
				
						BEGIN TRAN 
							
							UPDATE remessa with(rowlock) 
							SET data_validacao = sub1.data_conclusao 
							FROM @remessas_validadas AS sub1 
							WHERE remessa.id_remessa = sub1.id_remessa 
		
							SET @count = @@ROWCOUNT 

						COMMIT 

					END TRY 

					BEGIN CATCH 

						IF(@@TRANCOUNT > 0) 
							ROLLBACK 
					
					END CATCH 
				END 

		END 

	PRINT @count 

END



