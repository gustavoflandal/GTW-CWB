CREATE PROCEDURE [dbo].[spu_replica_erro]
AS
BEGIN 

	DECLARE @ErrorMessage NVARCHAR(4000)
	DECLARE @ErrorSeverity INT
	DECLARE @ErrorState INT

	SELECT 
		@ErrorMessage = ERROR_MESSAGE(),
		@ErrorSeverity = ERROR_SEVERITY(),
		@ErrorState = ERROR_STATE()

	RAISERROR (@ErrorMessage,	-- Message text.
			   @ErrorSeverity,	-- Severity.
			   @ErrorState		-- State.
			   )
END



