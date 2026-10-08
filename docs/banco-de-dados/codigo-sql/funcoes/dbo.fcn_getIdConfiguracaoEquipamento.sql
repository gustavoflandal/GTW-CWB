
CREATE FUNCTION [dbo].[fcn_getIdConfiguracaoEquipamento]
  ( @idLocal INT, @dataHora DATETIME)
  
RETURNS INT
AS

BEGIN

	DECLARE @idConfiguracaoEquipamento INT

	SET @idConfiguracaoEquipamento = (	SELECT TOP 1 
											id_configuracao_equipamento
										FROM 
											[local] lcl (nolock)
										WHERE	lcl.id_local = @idLocal
											AND lcl.data_atualizacao <= @dataHora
										ORDER BY
											lcl.data_atualizacao DESC
									)
				
	RETURN @idConfiguracaoEquipamento

END





