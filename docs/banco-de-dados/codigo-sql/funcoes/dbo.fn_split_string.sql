CREATE FUNCTION [dbo].[fn_split_string]
(
    @string NVARCHAR(MAX),
    @delimiter NVARCHAR(1)
)
RETURNS TABLE AS RETURN
(
	--DECLARE @string NVARCHAR(MAX) = 'Segunda-feira', @delimiter NVARCHAR(1) = '-'
    SELECT Split.a.value('let $n := . return count(../*[. << $n]) + 1', 'int') AS id,
		   Split.a.value('.', 'NVARCHAR(MAX)')                                 AS value
    FROM  (
				SELECT CAST('<X>'+REPLACE(@string, @delimiter, '</X><X>')+'</X>' AS XML) AS String
		  ) AS a
		  CROSS APPLY String.nodes('/X') AS Split(a)
)
