CREATE PROCEDURE [dbo].[sp_dicionario_dados_dependencias](@dbname varchar(100))
AS
declare @tabela varchar(100),
@dependencia varchar(100)
declare cur_dicionario_dados_dependencias cursor for 
select codigo from tabela

declare @objid int, @found_some bit--, @dbname sysname
--set @dbname='Consilux_GTW3'
print 'Banco de Dados:'+@dbname				
open cur_dicionario_dados_dependencias
print @objid
fetch next from cur_dicionario_dados_dependencias into @tabela
while @@fetch_status = 0
begin
select @objid = object_id(@tabela)
if @objid is null
	begin
		select @dbname = db_name()
		raiserror(15009,-1,-1,@tabela,@dbname)
	end
--set @x=@x+@tabela
print @tabela
insert into dependencias(codigo_tabela, nome_dependencia, tipo)
select distinct '.'=@tabela,(s.name + '.' + o.name), type = substring(v.name, 5, 66)
			from sys.objects o, master.dbo.spt_values v, sysdepends d,
				sys.schemas s
			where o.object_id = d.id
				and o.type = substring(v.name,1,2) collate database_default and v.type = 'O9T'
				and d.depid = @objid
				and o.schema_id = s.schema_id
				and deptype < 2
				
				print @dependencia
fetch next from cur_dicionario_dados_dependencias INTO @tabela
end
close cur_dicionario_dados_dependencias
deallocate cur_dicionario_dados_dependencias




