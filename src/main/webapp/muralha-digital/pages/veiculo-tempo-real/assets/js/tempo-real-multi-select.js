
var isIE11 = !!window.MSInputMethodContext && !!document.documentMode;

var $selectMultiEquipamentos = $("#edit-StatesPp-id, select[multiple='multiple']");
var $jsonMultiEquipamentos = $(" #bsMultiSelectJson");
var $multiSelects =  $selectMultiEquipamentos.add($jsonMultiEquipamentos);

window.cssPatch = null;

function PopulaSelectLocais()
{
    $selectMultiEquipamentos.bsMultiSelect
	(
        {
            cssPatch: window.cssPatch,
            setSelected: function(o,v) { 
                o.selected=v;
                if (!isIE11)                            
                    $('.toast').toast('show').find('.toast-body').css('text-decoration',v?'none':'line-through').text(o.text);
            },
        }
    );

    $jsonMultiEquipamentos.bsMultiSelect(
	{
        cssPatch: window.cssPatch,
        setSelected: function(o,i) 
		{
			o.selected = i;
			
			if( init ) {
				AlertCsx_E_TimeOut_8000ms('Para adicionar ou remover novos locais é preciso reiniciar a seleção! Aperte F5');
				return;
			}
			
			if( o.selected )			
				dispositivosSelecionados[dispositivosSelecionados.length] = {idLocal: o.value, descLocal: o.text};
			else
				RemoveLocalSelecionado(o.value);            

            if (!isIE11)                        
                $('.toast').toast('show').find('.toast-body').css('text-decoration',i?'none':'line-through').text(o.text);
        },
        // for all elements (selected by selector)
        options : options, 
        placeholder: "Clique aqui para selecionar os equipamentos",
        getDisabled : function(){ 
			return $('#optionDisable').prop("checked");			
        },
        
        // for each element
        buildConfiguration: function(element, configuration){
            configuration.label=$('#'+element.id+'Label').get(0); // TODO: resolve the bug
            configuration.getSize = function() {
                    var v = $("#bs-size-name-id").val();
                    return v=="LG"?"lg":(v=="SM"?"sm":null);
            }    
        }
    });
}

function RemoveLocalSelecionado(idLocal)
{
	dispositivosSelecionados = dispositivosSelecionados.filter(item => item.idLocal !== idLocal);
	console.log('Local Removido:: ' + idLocal);
	console.log(dispositivosSelecionados);
}

// ------------------------------------------------------------------------
// TODO: define it inside BsMultiSelect
// there are two possible sources: 1) ul.form-control.form-control-xx and 2) .input-group.input-group-xx
// this is quite strange but this works for BS nice (abstraction leak, I know)
// I would prefer to ref to original select[multiple='multiple'].custom-select.custom-select-xx in (1)
// For this select[multiple='multiple'].form-control should be changed to .custom-select
// Also then I will need to add .form-control-xx for ul.form-control (if this is not input-group)
// too many abstraction leaks in any case
// for json enabled: should be custom function that should include .input-group.input-group-xx
$("#bs-size-name-id").change( function()
{
    var size = this.value;
    if (size=="LG"){
        $(":not(.input-group) > .form-control").addClass("form-control-lg");
        $(":not(.input-group) > .form-control").removeClass("form-control-sm");
        $(":not(.input-group) > .btn").removeClass("btn-sm");
        $(":not(.input-group) > .btn").addClass("btn-lg");
        $(".input-group").addClass( "input-group-lg");
        $(".input-group").removeClass("input-group-sm"); 
        $(".form-select").addClass( "form-select-lg");
        $(".form-select").removeClass("form-select-sm"); 
        $(".col-form-label").addClass( "col-form-label-lg");
        $(".col-form-label").removeClass("col-form-label-sm"); 
    }else if(size=="SM") {
        $(":not(.input-group) > .form-control").removeClass("form-control-lg");
        $(":not(.input-group) > .form-control").addClass("form-control-sm");
        $(":not(.input-group) > .btn").addClass("btn-sm");
        $(":not(.input-group) > .btn").removeClass("btn-lg");
        $(".input-group").removeClass("input-group-lg");
        $(".input-group").addClass("input-group-sm");
        $(".form-select").removeClass( "form-select-lg");
        $(".form-select").addClass("form-select-sm"); 
        $(".col-form-label").removeClass( "col-form-label-lg");
        $(".col-form-label").addClass("col-form-label-sm"); 
    }else{
        $(":not(.input-group) > .form-control").removeClass("form-control-lg");
        $(":not(.input-group) > .form-control").removeClass("form-control-sm");
        $(":not(.input-group) > .btn").removeClass("btn-sm");
        $(":not(.input-group) > .btn").removeClass("btn-lg");
        $(".input-group").removeClass("input-group-lg");
        $(".input-group").removeClass("input-group-sm");
        $(".form-select").removeClass( "form-select-lg");
        $(".form-select").removeClass("form-select-sm"); 
        $(".col-form-label").removeClass( "col-form-label-lg");
        $(".col-form-label").removeClass("col-form-label-sm"); 
    }
    $selectMultiEquipamentos.each( function(index, element){
        if ( $(element).data("DashboardCode.BsMultiSelect")  )
            $(element).bsMultiSelect("UpdateSize");
    });
    if ( $('#bsMultiSelectJson').data("DashboardCode.BsMultiSelect")  ){
        $('#bsMultiSelectJson').bsMultiSelect("UpdateSize");
    }
    if ( $('#bsMultiSelectJson2').data("DashboardCode.BsMultiSelect")  ){
        $('#bsMultiSelectJson2').bsMultiSelect("UpdateSize");
    }
});

function MarcarTodos()
{
	$('#bsMultiSelectJson').bsMultiSelect('SelectAll');
}

function DesmarcarTodos()
{
	$('#bsMultiSelectJson').bsMultiSelect('DeselectAll');
}

function dispose()
{
    $multiSelects.bsMultiSelect("Dispose");
    $multiSelects.show(); 
}
