
CREATE FUNCTION [dbo].[fcn_ObterVariavelFluxo](@fluxo INT)  
RETURNS FLOAT   
WITH EXECUTE AS CALLER  
AS  
BEGIN   
  
DECLARE @var FLOAT = 0  
  
if @fluxo <= 100   
set @var = 1.7  
  
if @fluxo > 100 AND @fluxo <= 200  
set @var = 3.1  
  
if @fluxo > 200 AND @fluxo <= 400  
set @var = 2.0  
  
if @fluxo > 400 AND @fluxo <= 600  
set @var = 1.3  
  
if @fluxo > 600 AND @fluxo <= 800  
set @var = 0.8  
  
if @fluxo > 800 AND @fluxo <= 1000  
set @var = 0.6  
  
if @fluxo > 1000 AND @fluxo <= 1200  
set @var = 0.6  
  
if @fluxo > 1200 AND @fluxo <= 1400  
set @var = 0.6  
  
if @fluxo > 1400 AND @fluxo <= 1600  
set @var = 0.6  
  
if @fluxo > 1600   
set @var = 0.4  
  
RETURN @var  
  
END;  
