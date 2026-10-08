
CREATE VIEW [dbo].[agenda_camera_vigente]
AS
	select ac.id_agenda_camera, ac.id_local, data_criacao_agenda 
	from agenda_camera ac (nolock)
	JOIN (select id_local, MAX(id_agenda_camera) as id_agenda_camera
			from agenda_camera (nolock) 
			group by id_local) as t1 
		on	t1.id_agenda_camera = ac.id_agenda_camera 
			AND t1.id_local = ac.id_local




