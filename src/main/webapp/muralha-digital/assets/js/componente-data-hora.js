/******************************************************************************
************* Configurações do componente de seleção de data/hora *************
******************************************************************************/
var CONFIG_LOCALE =
{
	today: "Hoje",
	clear: 'Limpar',
	close: 'Fechar',
	selectMonth: 'Escolher mês',
	previousMonth: "Mês anterior",
	nextMonth: "Próximo mês",
	selectYear: 'Escolher ano',
	previousYear: 'Ano anterior',
	nextYear: 'Próximo ano',
	selectDecade: 'Escolher década',
	previousDecade: 'Década anterior',
	nextDecade: 'Próxima década',
	previousCentury: 'Século anterior',
	nextCentury: 'Próximo século',
	pickHour: "Escolher hora",
	incrementHour: 'Incrementar hora',
	decrementHour: 'Decrementar hora',
	pickMinute: 'Escolher minuto',
	incrementMinute: 'Incrementar minuto',
	decrementMinute: 'Decrementar minuto',
	pickSecond: 'Escolher segundo',
	incrementSecond: 'Incrementar segundo',
	decrementSecond: 'Decrementar segundo',
	selectTime: 'Hora',
	selectDate: 'Data',
	locale: "pt-BR",
    format: "dd/MM/yyyy HH:mm"
};

var CONFIG_DISPLAY =
{
//	components: {
//		useTwentyfourHour: true
//	},
	theme: "light", //"auto" (para modo escuro)
	buttons: {
       	today: true,
       	clear: true,
       	close: true,
	},
};

var CONFIG_LOCALE_DATE_ONLY =
{
	today: "Hoje",
	clear: 'Limpar',
	close: 'Fechar',
	selectMonth: 'Escolher mês',
	previousMonth: "Mês anterior",
	nextMonth: "Próximo mês",
	selectYear: 'Escolher ano',
	previousYear: 'Ano anterior',
	nextYear: 'Próximo ano',
	selectDecade: 'Escolher década',
	previousDecade: 'Década anterior',
	nextDecade: 'Próxima década',
	previousCentury: 'Século anterior',
	nextCentury: 'Próximo século',
	pickHour: "Escolher hora",
	incrementHour: 'Incrementar hora',
	decrementHour: 'Decrementar hora',
	pickMinute: 'Escolher minuto',
	incrementMinute: 'Incrementar minuto',
	decrementMinute: 'Decrementar minuto',
	pickSecond: 'Escolher segundo',
	incrementSecond: 'Incrementar segundo',
	decrementSecond: 'Decrementar segundo',
	selectTime: 'Hora',
	selectDate: 'Data',
	locale: "pt-BR",
    format: "dd/MM/yyyy"
};

var CONFIG_DISPLAY_DATE_ONLY =
{
	components: {
       	decades: true,
       	year: true,
       	month: true,
       	date: true,
       	clock: false,
       	hours: false,
       	minutes: false,
       	seconds: false,
    },
    theme: "light", //"auto" (para modo escuro)
	buttons: {
       	today: true,
       	clear: true,
       	close: true,
	},
};

var CONFIG_DISPLAY_DATA_HORA =
{
     components: {
//	   useTwentyfourHour: true,
       decades: false,
       year: true,
       month:true,
       date: true,
       hours: true,
       minutes: true,
       seconds: false
	},
	theme: "light" //"auto" (para modo escuro)
}


/******************************************************************************
******* Configurações do componente de seleção de data/hora - sem minuto ******
******************************************************************************/
var CONFIG_LOCALE_DATA_HORA =
{
	today: "Hoje",
	clear: 'Limpar',
	close: 'Fechar',
	selectMonth: 'Escolher mês',
	previousMonth: "Mês anterior",
	nextMonth: "Próximo mês",
	selectYear: 'Escolher ano',
	previousYear: 'Ano anterior',
	nextYear: 'Próximo ano',
	selectDecade: 'Escolher década',
	previousDecade: 'Década anterior',
	nextDecade: 'Próxima década',
	previousCentury: 'Século anterior',
	nextCentury: 'Próximo século',
	pickHour: "Escolher hora",
	incrementHour: 'Incrementar hora',
	decrementHour: 'Decrementar hora',
	pickMinute: 'Escolher minuto',
	incrementMinute: 'Incrementar minuto',
	decrementMinute: 'Decrementar minuto',
	pickSecond: 'Escolher segundo',
	incrementSecond: 'Incrementar segundo',
	decrementSecond: 'Decrementar segundo',
	selectTime: 'Hora',
	selectDate: 'Data',
	locale: "pt-BR",
    format: "dd/MM/yyyy HH"
};
var CONFIG_DISPLAY_DATA_HORA_SEM_MINUTO =
{
     components: {
//	   useTwentyfourHour: true,
       decades: false,
       year: true,
       month:true,
       date: true,
       hours: true,
       minutes: false,
       seconds: false
	},
	theme: "light" //"auto" (para modo escuro)
}
/*****************************************************************************/
	

function CarregarComponenteData(configApenasData)
{
	if (configApenasData)
	{
		new tempusDominus.TempusDominus(document.getElementById('dataInicio'), {
			localization: CONFIG_LOCALE_DATE_ONLY,
			display: CONFIG_DISPLAY_DATE_ONLY
		});
		new tempusDominus.TempusDominus(document.getElementById('dataFim'), {
			localization: CONFIG_LOCALE_DATE_ONLY,
			display: CONFIG_DISPLAY_DATE_ONLY
		});
	}
	else
	{
		new tempusDominus.TempusDominus(document.getElementById('dataInicio'), {
			localization: CONFIG_LOCALE,
			display: CONFIG_DISPLAY
		});
		new tempusDominus.TempusDominus(document.getElementById('dataFim'), {
			localization: CONFIG_LOCALE,
			display: CONFIG_DISPLAY
		});
	}
}

function CarregarComponenteDataModal()
{
	new tempusDominus.TempusDominus(document.getElementById('dataInicioModal'), {
		localization: CONFIG_LOCALE,
		display: CONFIG_DISPLAY
	});
}

function CarregarComponenteDataHoraModal()
{
	new tempusDominus.TempusDominus(document.getElementById('dataInicioModal'), {
		localization: CONFIG_LOCALE,
		display: CONFIG_DISPLAY_DATA_HORA
	});
}

function CarregaComponenteDataHora()
{
	new tempusDominus.TempusDominus(document.getElementById('dataInicio'), {
			localization: CONFIG_LOCALE,
			display: CONFIG_DISPLAY_DATA_HORA
		});
		new tempusDominus.TempusDominus(document.getElementById('dataFim'), {
			localization: CONFIG_LOCALE,
			display: CONFIG_DISPLAY_DATA_HORA
		});
}

function CarregaComponenteDataHoraSemMinuto()
{
	new tempusDominus.TempusDominus(document.getElementById('dataInicio'), {
			localization: CONFIG_LOCALE_DATA_HORA,
			display: CONFIG_DISPLAY_DATA_HORA_SEM_MINUTO
		});
		new tempusDominus.TempusDominus(document.getElementById('dataFim'), {
			localization: CONFIG_LOCALE_DATA_HORA,
			display: CONFIG_DISPLAY_DATA_HORA_SEM_MINUTO
		});
}

function TratarDataHora(data)
{
	return data.replace(",","");
}