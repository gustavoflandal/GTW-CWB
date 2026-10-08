<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Fato</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <style>
        body {
            background: #e6f0fa;
        }
        h2 {
            color: #343a40;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 2px;
            margin: 1.5rem 0;
            text-align: center;
        }
        .image-button {
            position: relative;
            overflow: hidden;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            border: 2px solid #dee2e6;
            border-radius: 10px;
            cursor: pointer;
            background: white;
            padding: 1rem;
            text-align: center;
        }
        .image-button:hover {
            transform: scale(1.05);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
        }
        .image-button:hover::before {
            content: attr(data-hover-text);
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: #0066cc; /* Matching the menu color */
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5rem;
            font-weight: bold;
            text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.7);
            z-index: 1;
        }
        .image-button img {
            width: 350px;
            height: auto;
            border-radius: 5px;
        }
        .image-container {
            display: flex;
            justify-content: center;
            gap: 2rem;
            margin-top: 1rem;
        }
        .image-text {
            margin-top: 0.5rem;
            font-size: 1.2rem;
            color: #343a40;
            text-align: center;
        }
        @media (max-width: 768px) {
            .image-container {
                flex-direction: column;
                align-items: center;
            }
            .image-button img {
                width: 250px;
            }
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <h2>Cadastro de Registro de Fato</h2>
        <div class="row">
            <div class="col-12">
                <div class="image-container">
                    <div class="image-button" onclick="window.location.href='/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?temBoletim=0'"  data-hover-text="Fato sem B.O.	">
                        <img src="assets/images/registro_fato_com_sem_boletim.jpg" class="img-fluid" alt="Registro de Fato 1">
                        <div class="image-text">Fato sem B.O.</div>
                    </div>
					<div class="image-button" 
					     onclick="window.location.href='/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?temBoletim=1'" 
					     data-hover-text="Boletim de Ocorrência (B.O.)">
					    <img src="assets/images/registro_fato_com_sem_boletim.jpg" class="img-fluid" alt="Registro de Fato 2">
					    <div class="image-text">Boletim de Ocorrência (B.O.)</div>
					</div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>