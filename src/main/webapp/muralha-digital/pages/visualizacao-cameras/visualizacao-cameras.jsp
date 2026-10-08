<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 

<html lang="en">
   <head>
      <title>Muralha Digital</title>
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <meta http-equiv="Pragma" content="no-cache" />
      <meta http-equiv="Cache-Control" content="no-cache, must-revalidate" />
      <meta http-equiv="Expires" content="0" />
      <link rel="stylesheet" href="camera-api/css/visualizacao-cameras.css">
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
   </head>
   </head>
   <script>
      var urlRoot 	= "${root}";
   </script>
   <body class="homepage">
      <div class="col-auto" hidden>
         <div id="toggle-disable-id" role="group"  class="btn-group my-1 ml-2">
            <input type="radio" class="btn-check" name="optionsED" id="optionEnable" value="ON" checked>
            <label class="btn btn-outline-secondary" for="optionEnable">Enable</label>                        
            <input type="radio" class="btn-check" name="optionsED" id="optionDisable" value="OFF">
            <label class="btn btn-outline-secondary" for="optionDisable">Disable</label>
         </div>
      </div>
      <div class="container-fluid mt-3 mb-3">
         <div class="row">
            <div class="col-md-1"></div>
            <div class="col-md-3 d-grid">
               <span class="p-2 bg-secondary text-white text-center rounded"><strong>VÍDEO EM TEMPO REAL</strong></span>
            </div>
            <div class="col-md-6 d-flex justify-content-center">
               <select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--">
                   <option value="0" selected="selected">--Equipamento--</option>
               </select>
            </div>
            <div class="col-md-1 d-grid gap-2">
               <button type="button" class="btn btn-success" id="iniciar" onclick="DivInit_WebSocketInit()">INICIAR</button>
            </div>
            <div class="col-md-1"></div>
         </div>
      </div>
      <div id="mainDiv" class="container-fluid" hidden>
         <div class="container">
            <div style="display: none;">
               <span id="device_ipc_tab">IPC/SD</span>
               <label style="display: inline-block;width: 100px;text-align: right;" t="com.Language">Idioma</label>
               <select id="onChangeLanguage" style="width:80px">
                  <option value="English">English</option>
                  <option value="Portugues" selected>Portugues</option>
               </select>
            </div>
            <div class="content" id="device_ipc_content">

                  <ul class="h5-menu-list" style="display: none;">
                     <li data-ip="" data-port="" data-user="" data-pswd="" t=""></li>
                  </ul>

               <div class="h5-left">
                  <div class="h5-play-wrap">
                  </div>
                  <div class="h5-left" style="display: none;">
                  	<div class="h5-form-item" style="margin-top: 20px;">
	                	<label t="com.SplitWindow">Split Windows</label>
	                    <select class="h5-select" sel-for="onChangeWdnNum">
	                        <option value="1" selected>1x1</option>
	                        <option value="2">2x2</option>
	                        <option value="3">3x3</option>
	                        <option value="4">4x4</option>
	                     </select>
	                  </div>
	                  <div class="h5-form-item fn-clear">
	                     <label t="com.Stream Type"></label>
	                     <select id="h5_stream" sel-for="onChangeStream"></select>
	                  </div>
	                  <div class="h5-form-item fn-clear">
	                     <label t="com.ChannelList"></label>
	                     <select id="h5_channel" sel-for="onChangeChannel"></select>
	                  </div>
	                  <div  class="h5-form-item fn-clear">
	                     <label t="com.IVSEnable"></label>
	                     <select id="h5_ivs">
	                        <option value="0" t="com.No"></option>
	                        <option value="1" t="com.Yes"></option>
	                     </select>
	                  </div>               
	               </div>
	           </div>
               <div class="h5-right" style="display: none;">
                  <fieldset class="h5-fieldset-wrap">
                     <legend t="com.PtzControl"></legend>
                     <div class="h5-step-wrap">
                        <span t="com.Step"></span>
                        <select id="h5_ptz_step" style="width: 130px;">
                           <option value="1">1</option>
                           <option value="2">2</option>
                           <option value="3">3</option>
                           <option value="4">4</option>
                           <option value="5" selected="">5</option>
                           <option value="6">6</option>
                           <option value="7">7</option>
                           <option value="8">8</option>
                        </select>
                     </div>
                     <div class="h5-ptz-wrap">
                        <input type="button" class="h5-button" t="com.LeftUp" onmousedown="onHandlePTZ('LeftUp', false)" onmouseup="onHandlePTZ('LeftUp', true)">
                        <input type="button" class="h5-button" t="com.Up"  onmousedown="onHandlePTZ('Up', false)" onmouseup="onHandlePTZ('Up', true)">
                        <input type="button" class="h5-button" t="com.RightUp" onmousedown="onHandlePTZ('RightUp', false)" onmouseup="onHandlePTZ('RightUp', true)">
                        <input type="button" class="h5-button" t="com.Left" onmousedown="onHandlePTZ('Left', false)" onmouseup="onHandlePTZ('Left', true)">
                        <input type="button" class="h5-button" t="" >
                        <input type="button" class="h5-button" t="com.Right" onmousedown="onHandlePTZ('Right', false)" onmouseup="onHandlePTZ('Right', true)">
                        <input type="button" class="h5-button" t="com.LeftDown" onmousedown="onHandlePTZ('LeftDown', false)" onmouseup="onHandlePTZ('LeftDown', true)">
                        <input type="button" class="h5-button" t="com.Down" onmousedown="onHandlePTZ('Down', false)" onmouseup="onHandlePTZ('Down', true)">
                        <input type="button" class="h5-button" t="com.RightDown" onmousedown="onHandlePTZ('RightDown', false)" onmouseup="onHandlePTZ('RightDown', true)">
                     </div>
                     <div class="h5-zoomfocus-wrap">
                        <input type="button" class="h5-button" t="com.ZoomWide" onmousedown="onHandlePTZ('ZoomWide', false)" onmouseup="onHandlePTZ('ZoomWide', true)">
                        <input type="button" class="h5-button" t="com.ZoomTele"  onmousedown="onHandlePTZ('ZoomTele', false)" onmouseup="onHandlePTZ('ZoomTele', true)">
                        <input type="button" class="h5-button" t="com.FocusFar"  onmousedown="onHandlePTZ('FocusFar', false)" onmouseup="onHandlePTZ('FocusFar', true)">
                        <input type="button" class="h5-button" t="com.FocusNear"  onmousedown="onHandlePTZ('FocusNear', false)" onmouseup="onHandlePTZ('FocusNear', true)">
                        <input type="button" class="h5-button" t="com.IrisSmall"  onmousedown="onHandlePTZ('IrisSmall', false)" onmouseup="onHandlePTZ('IrisSmall', true)">
                        <input type="button" class="h5-button" t="com.IrisLarge"  onmousedown="onHandlePTZ('IrisLarge', false)" onmouseup="onHandlePTZ('IrisLarge', true)">
                     </div>
                     <div class="h5-preset-wrap">
                        <div class="h5-item-form" style="margin-bottom: 10px;">
                        	<label t="com.Preset"></label>
                           	<select id="h5_preset" style="width: 115px;">
	                           <option value="1" selected="">1</option>
	                           <option value="2">2</option>
	                           <option value="3">3</option>
	                           <option value="4">4</option>
	                           <option value="5">5</option>
                        	</select>
                        </div>
                        <input type="button" class="h5-button" t="com.Go" onclick="onHandlePTZ('GotoPreset', false)">
                        <input type="button" class="h5-button" t="com.Add" onclick="onHandlePTZ('SetPreset', false)">
                        <input type="button" class="h5-button" t="com.Delete" onclick="onHandlePTZ('ClearPreset', false)" >
                     </div>
                  </fieldset>

               </div>
            </div>
         </div>
      </div>
      <br/>	
	  <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	  <script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
      <script src="assets/js/visualizacao-cameras.js"></script>
      <script src="camera-api/module/PlayerControl.js"></script>
      <script src="camera-api/controles.js"></script>	
   </body>
</html>