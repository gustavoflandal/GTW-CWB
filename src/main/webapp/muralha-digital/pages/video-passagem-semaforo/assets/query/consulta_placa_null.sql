select vtr.* from muralha.veiculo_tempo_real vtr
where id_local = 1
and data > '2024-03-01 00:00:01'
and placa is null
--and placa_frontal is null

--PASSAGENS TEMPO REAL
--Data a partir de 01/03/2024
--TOTAL: 12756 veiculos
--Perda OCR frontal: 3154 veiculos
--Perda OCR traseira: 22 veiculos