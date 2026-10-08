CREATE PROCEDURE [dbo].[spu_gerencia_contrato_atualiza_status_config_equip]
AS
INSERT INTO gerencia_contrato_alerta (
        id_alerta,
		serie_equipamento,
		id_pista,
        valor,
        alerta)
SELECT 
	4,
	serie_equipamento,
	NULL,
	CASE 
		WHEN statusConfigEquip = 1 
			THEN 'OK'
		WHEN statusConfigEquip = 0 
			THEN 'Desatualizado'
		ELSE 
			''
	END,
	CASE 
		WHEN statusConfigEquip = 0 
			THEN 1 
		ELSE 
			0 
	END
FROM (	SELECT
			lv.serie_equipamento,
			(
			CASE 
				WHEN	configEquipOK.ultima_data IS NOT NULL 
					AND configEquipOK.ultima_data > COALESCE( configEquipCaptura.ultima_data, 0)
					AND configEquipOK.ultima_data > COALESCE(configEquipGTW.ultima_data , 0) 
					THEN 1
				WHEN	configEquipOK.ultima_data IS NULL 
					AND configEquipCaptura.ultima_data IS NULL
					AND configEquipGTW.ultima_data IS NULL
					THEN NULL
				ELSE 
					0 
			END
			) as statusConfigEquip
		FROM local_vigente lv (nolock)
			-- CSX_EVENT_CONFIGEQUIP_NEWER_IN_CAPTURA
			LEFT JOIN fcn_maxDataHoraEventoPorProprietario(5001) configEquipCaptura 
				ON CAST(lv.serie_equipamento AS CHAR(7)) = configEquipCaptura.proprietario
			-- CSX_EVENT_CONFIGEQUIP_NEWER_IN_GTW
			LEFT JOIN fcn_maxDataHoraEventoPorProprietario(5002) configEquipGTW 
				ON CAST(lv.serie_equipamento AS CHAR(7)) = configEquipGTW.proprietario
			-- CSX_EVENT_CONFIGEQUIP_OK
			LEFT JOIN fcn_maxDataHoraEventoPorProprietario(5003) configEquipOK 
				ON CAST(lv.serie_equipamento AS CHAR(7)) = configEquipOK.proprietario
		WHERE	lv.data_inicio < GETDATE()
			AND lv.desativado = 0
	) AS statusConfigEquip



