CREATE FUNCTION [dbo].[fcn_getClassificacaoPNCT_v2] (@id_classe CHAR(1), @qtde_eixos TINYINT, @comprimento DECIMAL(6,1), @pbt FLOAT,
			@distancia_E1E2 FLOAT, @distancia_E2E3 FLOAT, @distancia_E3E4 FLOAT, @distancia_E4E5 FLOAT, @distancia_E5E6 FLOAT,
			@distancia_E6E7 FLOAT, @distancia_E7E8 FLOAT, @distancia_E8E9 FLOAT)
RETURNS NCHAR(3)
AS
BEGIN
	DECLARE @classificacao_pnct NCHAR(3)
	SET @classificacao_pnct = (
			SELECT CASE WHEN @qtde_eixos > 9 THEN 'L1'
				        WHEN @id_classe = 'O' AND @qtde_eixos IN (2,3) THEN
							CASE WHEN @qtde_eixos = 2 AND (@distancia_E1E2 > 3.5) THEN 'A1'
								 WHEN @qtde_eixos = 3 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) THEN 'B1'
								 ELSE 'L1'
							END
						
						WHEN @qtde_eixos = 2 AND (@distancia_E1E2 > 3.5) THEN 'A2'
			
						WHEN @qtde_eixos = 3 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) THEN 'B2'
						WHEN @qtde_eixos = 3 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) THEN 'B3'
						
						WHEN @comprimento BETWEEN 19.8 AND 25.0 AND @qtde_eixos = 4 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) THEN 'C5'
						WHEN (@comprimento > 25.0 AND @comprimento <= 30.0) AND @qtde_eixos = 4 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) THEN 'C6'
						WHEN @qtde_eixos = 4 AND (@distancia_E1E2 <= 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) THEN 'C1'
						WHEN @qtde_eixos = 4 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) THEN 'C2'
						WHEN @qtde_eixos = 4 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) THEN 'C3'
						WHEN @qtde_eixos = 4 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) THEN 'C4'
			
						
						WHEN @comprimento BETWEEN 19.8 AND 25.0 AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) THEN 'D6'
						WHEN (@comprimento > 25.0 AND @comprimento <= 30.0) AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) THEN 'D11'
						WHEN @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) THEN 'D2'
						WHEN @comprimento BETWEEN 19.8 AND 25.0 AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) THEN 'D7'
						WHEN (@comprimento > 25.0 AND @comprimento <= 30.0) AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) THEN 'D9'
						WHEN @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) THEN 'D5'
						WHEN @comprimento <= 18.6 AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) THEN 'D3'
						WHEN @comprimento <= 19.8 AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) THEN 'D8'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 > 2.4) THEN 'D10'
						WHEN @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 <= 2.4) THEN 'D1'
						WHEN @qtde_eixos = 5 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) THEN 'D4'
						
						WHEN @comprimento <= 18.6 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E1'
						WHEN @comprimento <= 18.6 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E2'
						WHEN @comprimento < 18.6 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 > 2.4) THEN 'E3'
						WHEN @comprimento <= 25.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 <= 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 > 2.4) THEN 'E4'
						WHEN @comprimento <= 25.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E5'
						WHEN @comprimento <= 25.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E6'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 > 2.4) THEN 'E7'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 > 2.4) THEN 'E8'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) THEN 'E9'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E10'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E11'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) THEN 'E12'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 6 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) THEN 'E13'
			
						WHEN @comprimento <= 25.0 AND @qtde_eixos = 7 AND (@distancia_E1E2 <= 2.4) AND (@distancia_E2E3 > 2.4) AND (@distancia_E3E4 <= 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 <= 2.4) THEN 'F1'
						WHEN @comprimento <= 20.0 AND @qtde_eixos = 7 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 <= 2.4) THEN 'F2'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 7 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 <= 2.4) THEN 'F3'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 7 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 > 2.4) THEN 'F4'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 7 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 > 2.4) THEN 'F5'

						WHEN @comprimento <= 30.0 AND @qtde_eixos = 8 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 <= 2.4) AND (@distancia_E6E7 > 2.4) AND (@distancia_E7E8 <= 2.4) THEN 'G1'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 8 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 <= 2.4) AND (@distancia_E7E8 <= 2.4) THEN 'G2'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 8 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 > 2.4) AND (@distancia_E7E8 <= 2.4) THEN 'G3'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 8 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 > 2.4) AND (@distancia_E5E6 <= 2.4) AND (@distancia_E6E7 > 2.4) AND (@distancia_E7E8 > 2.4) THEN 'G4'

						WHEN @comprimento <= 30.0 AND @qtde_eixos = 9 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 <= 2.4) AND (@distancia_E6E7 > 2.4) AND (@distancia_E7E8 <= 2.4) AND (@distancia_E8E9 <= 2.4) THEN 'H1'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 9 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 <= 2.4) AND (@distancia_E7E8 > 2.4) AND (@distancia_E8E9 <= 2.4) THEN 'H2'
						WHEN @comprimento <= 30.0 AND @qtde_eixos = 9 AND (@distancia_E1E2 > 2.4) AND (@distancia_E2E3 <= 2.4) AND (@distancia_E3E4 > 2.4) AND (@distancia_E4E5 <= 2.4) AND (@distancia_E5E6 > 2.4) AND (@distancia_E6E7 <= 2.4) AND (@distancia_E7E8 > 2.4) AND (@distancia_E8E9 <= 2.4) THEN 'H3'
			
						WHEN @qtde_eixos <= 2 AND @pbt <= 2000.0 THEN CASE WHEN @id_classe = 'M' AND @pbt BETWEEN 1.0 AND 250.0 THEN 'J1' WHEN @id_classe IN ('P','','T') THEN 'I1' ELSE 'L1' END
						ELSE 'L1'
				   END AS classificacao_pnct
		)
	RETURN @classificacao_pnct;
END
