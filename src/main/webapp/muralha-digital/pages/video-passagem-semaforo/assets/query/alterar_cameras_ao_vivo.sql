select *from muralha.config_monitoramento_ao_vivo_cameras

update muralha.config_monitoramento_ao_vivo_cameras
set url_stream = 'http://10.0.5.81/api/mjpegvideo.cgi?Quality=50&FrameRate=10'



update muralha.config_monitoramento_ao_vivo_cameras
set url_stream = 'http://admin:C0ns1lux@10.0.5.51/cgi-bin/mjpg/video.cgi?channel=1&subtype=1', id_local = 95

update muralha.config_monitoramento_ao_vivo_cameras
set url_stream = 'http://admin:C0ns1lux@189.42.79.131:5000/cgi-bin/mjpg/video.cgi?channel=0&subtype=1', id_local = 1

update muralha.config_monitoramento_ao_vivo_cameras
set id_local = 1