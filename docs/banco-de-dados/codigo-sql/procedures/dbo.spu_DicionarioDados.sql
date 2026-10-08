CREATE procedure [dbo].[spu_DicionarioDados]
as
select 
	OBJECT_NAME(c.object_id)		as 'Tabela',
	c.column_id						as 'Coluna',
	c.name							as 'Nome da Coluna',
	t.name							as 'Tipo',
	c.max_length					as 'Tamanho',
	case
		when c.is_nullable = 1
			then 'Sim'
		else
			'Não'
	end								as 'Aceita Nulo',
	case
		when c.is_identity = 1
			then 'Sim'
		else
			'Não'
	end								as 'Identity',
	Case 
		When pk.CONSTRAINT_NAME is null
			then 'Não'
		else
			'Sim'
	end								as 'Primary Key',
	case
		when exists (select 1 
						from INFORMATION_SCHEMA.KEY_COLUMN_USAGE fk
						where	c.object_id = object_id(fk.TABLE_NAME)
							and c.column_id = fk.ORDINAL_POSITION
							and SUBSTRING(fk.CONSTRAINT_NAME,1,2) = 'FK')
			then 'Sim'						
		else 
			'Não'
	end								as 'Foreign Key'
from sys.columns c
	inner join sys.types t
		on c.user_type_id = t.user_type_id
	left join INFORMATION_SCHEMA.KEY_COLUMN_USAGE pk
		on c.object_id = object_id(pk.TABLE_NAME)
		and c.column_id = pk.ORDINAL_POSITION
		and SUBSTRING(pk.CONSTRAINT_NAME,1,2) = 'PK'
order by 1,2		


