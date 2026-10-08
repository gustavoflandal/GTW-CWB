<%@ page language="java" pageEncoding="utf-8"%>
<html lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Muralha Digital</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <style>
        body {
            background-color: #f8f9fa; /* cinza claro */
        }

        /* Moldura leve ao redor dos vídeos */
        .quadro-videos {
            border: 1px solid #ccc;
            border-radius: 12px;
            background-color: #fff;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 20px;
            margin-top: 10px;
        }

        /* Título do grupo de vídeos */
        .titulo-quadro {
            text-align: center;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
            font-size: 1.1rem;
        }

        /* Layout dos vídeos lado a lado */
        .video-wrapper {
            display: flex;
            justify-content: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        /* Cada bloco de vídeo */
        .video-box {
		    width: 100%; /* ocupa toda a largura do container pai */
		    max-width: 480px; /* opcional: limita o tamanho máximo */
		    margin: 10px auto; /* centraliza horizontalmente */
		}
		
		.video-container {
		    position: relative;
		    width: 100%;
/* 		    padding-bottom: 56.25%; /* proporção 16:9, pode ajustar para 50% se quiser mais quadrado */ */
		    overflow: hidden;
		    border-radius: 8px;
		    background-color: #000;
		}
		
		.video-container iframe {
		    position: absolute;
		    top: 0;
		    left: 0;
		    width: 100%;
		    height: 100%;
		    border: none;
		}

        /* Título de cada vídeo */
        .video-title {
            margin-top: 6px;
            font-size: 0.95rem;
            color: #555;
            font-weight: 500;
            background-color: #f1f1f1;
            border-radius: 6px;
            padding: 4px 0;
        }

        /* Hover opcional: destaca levemente o vídeo */
        .video-box:hover .video-container {
            box-shadow: 0 0 10px rgba(0,0,0,0.2);
        }
    </style>
</head>

<script>
    var urlRoot = "${root}";
</script>

<body class="homepage">
    <div class="container">
        <div class="row pt-1">
            <div class="col-md-1"></div>

            <div class="col-md-10">
                <div class="row pb-3">
                    <div class="col-md-12 d-grid">                    
                        <span class="p-2 bg-secondary text-white text-center rounded">
                            <strong>MONITORAMENTO AO VIVO</strong>
                        </span>
                    </div>
                </div>
            </div>

            <div class="col-md-1"></div>
        </div>

        <!-- Moldura clara e moderna -->
        <div class="row pt-1">
	        <div class="quadro-videos">
	            <div class="titulo-quadro">Câmeras Ativas</div>
	
	            <div class="video-wrapper">
	
	                <div class="video-box">
	                   <div class="video-container">
					        <img src="http://189.42.79.131:8188/api/mjpegvideo.cgi?Quality=50&FrameRate=3&Resolution=480x360">
					    </div>
	                    <div class="video-title">Câmera 100 - BR 116, Próximo a Havan Parolin</div>
	                </div>
	                <div class="video-box">
	                   <div class="video-container">
					        <img src="http://189.42.79.131:8186/api/mjpegvideo.cgi?Quality=50&FrameRate=3&Resolution=480x360">
					    </div>
	                    <div class="video-title">Câmera 101 - Rua João Tschannerl</div>
	                </div>
	                <div class="video-box">
	                   <iframe width="480" height="360" src="https://www.youtube-nocookie.com/embed/Sn987SWNzTI?si=W6vxkGP4OdiIZCYi&autoplay=1&mute=1&controls=0&modestbranding=1&rel=0&showinfo=0" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
	                    <div class="video-title">Câmera 452 -  Ruas e Avenidas de São Paulo.</div>
	                </div>
	                <div class="video-box">
	                   	<iframe width="480" height="360" src="https://www.youtube-nocookie.com/embed/9vBjxVTCC2k?si=U2Q8co8aSGUGQR0H&autoplay=1&mute=1&controls=0&modestbranding=1&rel=0&showinfo=0" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
	                    <div class="video-title">Câmera 304 - AO VIVO: IMAGENS CRUZAMENTO EXPEDICIONÁRIO X PADRE NOBREGA TUBARÃO SC</div>
	                </div>
	                <div class="video-box">
	                	<iframe width="480" height="360" src="https://www.youtube-nocookie.com/embed/EW2u8pd3rYQ?si=ey9mZgXKSWPOUyV-&autoplay=1&mute=1&controls=0&modestbranding=1&rel=0&showinfo=0" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
	                	<div class="video-title">Câmera 308 - AV. MARCOLINO MARTINS CABRAL | SE-CONNECT - TUBARAO SC</div>
	                </div>
	                <div class="video-box">
	                    <iframe width="480" height="360" src="https://www.youtube-nocookie.com/embed/_HZIb2JV7IY?si=682PdVKZ2cumte3d&autoplay=1&mute=1&controls=0&modestbranding=1&rel=0&showinfo=0" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
	                    <div class="video-title">Câmera 401 - BR 101 - SENTIDO SUL - MALUVAN HOME & SHOP</div>
	                </div>
	                
	            </div>
	        </div>
		</div>
    </div>
     <!-- <script src="assets/js/video-ao-vivo.js"></script>  -->
</body>
</html>