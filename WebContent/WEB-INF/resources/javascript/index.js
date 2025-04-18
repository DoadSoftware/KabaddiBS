var match_data;
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;	
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function millisToMinutesAndSeconds(millis) {
  var m = Math.floor(millis / 60000);
  var s = ((millis % 60000) / 1000).toFixed(0);
  return (m < 10 ? '0' + m : m) + ":" + (s < 10 ? '0' + s : s);
}
function displayMatchTime() {
	processKabaddiProcedures('READ-MATCH-AND-POPULATE',null);
}
function initialiseForm(whatToProcess, dataToProcess)
{
	switch (whatToProcess) {
	case 'TIME':
		if(match_data) {
			if(document.getElementById('match_time_hdr')) {
				document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
					millisToMinutesAndSeconds(match_data.clock.matchTotalMilliSeconds);
			}
		}
		
		break;
	case 'UPDATE-CONFIG':
		document.getElementById('configuration_file_name').value = $('#select_configuration_file option:selected').val();
		document.getElementById('selectedBroadcaster').value = dataToProcess.broadcaster;
		document.getElementById('vizIPAddress').value = dataToProcess.ipAddress;
		document.getElementById('vizPortNumber').value = dataToProcess.portNumber;
		
		break;
	}
}
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){  
			if($(this).is("select")) {
				formData.append($(this).attr('id'),$('#' + $(this).attr('id') + ' option:selected').val());  
			} else {
				formData.append($(this).attr('id'),$(this).val());  
			}	
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'RESET_MATCH':
		url_path = 'reset_and_upload_match_setup_data';
		break;
	case 'SAVE_MATCH':
		url_path = 'upload_match_setup_data';
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(data) {

        	switch(whatToProcess.toUpperCase()) {
        	case 'RESET_MATCH':
        		alert('Match has been reset');
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'SAVE_MATCH':
        		document.setup_form.method = 'post';
        		document.setup_form.action = 'setup_to_match';
        	   	document.setup_form.submit();
        		break;
        	}
        	
        },    
        error: function(jqXHR, textStatus, errorThrown) {    
        	console.error('Error occurred in ' + whatToProcess + ' with error description: ' + textStatus, errorThrown);
        }
    
    });		
	
}
function processUserSelectionData(whatToProcess,dataToProcess){
	//alert(whatToProcess);
	switch (whatToProcess) {
	case 'LOGGER_FORM_KEYPRESS':
		switch (dataToProcess) {
		case '-':
			processKabaddiProcedures('RESET-ALL-ANIMATION');
			break;
		case 65:
			processKabaddiProcedures('SWAP_DATA');
			break;	
		case 32:
			processKabaddiProcedures('CLEAR-ALL');
			break;
		case 189:
			if(confirm('It will Also Delete Your Preview from Directory...\r\n\r\n Are You Sure To Animate Out?') == true){
				processKabaddiProcedures('ANIMATE-OUT');
			}
			break;	
		case 187:
			processKabaddiProcedures('ANIMATE-OUT-SCOREBUG');
			break;
		case 112:
			processKabaddiProcedures('POPULATE-SCOREBUG');
			break;
		case 113:
			processKabaddiProcedures('POPULATE-SCORELINE');
			break;	
		case 114:
			processKabaddiProcedures('POPULATE-TOURNAMENT_LOGO');
			break;
		case 115:
			processKabaddiProcedures('POPULATE-GOLDEN_RAID');
			break;
		case 116:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#kabaddi_div").hide();
			addItemsToList('STARTING_LINE_UP_OPTION',null);
			break;
		case 117:
			processKabaddiProcedures('POPULATE-TEAMS_LOGOS');
			break;
		case 118:
			processKabaddiProcedures('POPULATE-FF_SCORELINE');
			break;	
		case 77:
			processKabaddiProcedures('POPULATE-FF_MATCH_ID');
			break;
		case 82:
			processKabaddiProcedures('POPULATE-FF_TOURNAMENT ROULES');
			break;		
		case 83:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#kabaddi_div").hide();
			processKabaddiProcedures('MATCH-PROMO_GRAPHICS-OPTIONS');
			break;
		case 27:
			$('#select_graphic_options_div').empty();
			document.getElementById('select_graphic_options_div').style.display = 'none';
			$("#select_event_div").show();
			$("#match_configuration").show();
			$("#kabaddi_div").show();
		break;
		case 88:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#kabaddi_div").hide();
			processKabaddiProcedures('EXCEL_FF_GRAPHICS_OPTION');

			break;
		case "RE_CONNECT":
			processKabaddiProcedures('RE_CONNECT');
			break;
		
		}
		
		break;
	}
}
function processUserSelection(whichInput)
{	
	var error_msg = '';

	switch ($(whichInput).attr('name')) {
	case 'load_scene_btn':
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
	  	document.initialise_form.submit();
		break;
	case 'select_configuration_file':
		processKabaddiProcedures('GET-CONFIG-DATA');
		break;
	case 'cancel_match_setup_btn':
		document.setup_form.method = 'post';
		document.setup_form.action = 'setup_to_match';
	   	document.setup_form.submit();
		break;
	case 'matchFileName':
		if(document.getElementById('matchFileName').value) {
			document.getElementById('matchFileName').value = 
				document.getElementById('matchFileName').value.replace('.json','') + '.json';
		}
		break;
	case 'load_match_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		processKabaddiProcedures('LOAD_MATCH',$('#select_kabaddi_matches option:selected'));
		break;
	case 'cancel_graphics_btn':
		$('#select_graphic_options_div').empty();
		document.getElementById('select_graphic_options_div').style.display = 'none';
		$("#select_event_div").show();
		$("#match_configuration").show();
		$("#kabaddi_div").show();
		break;
	case 'populate_match_promo_btn':
		processKabaddiProcedures('POPULATE-FF_MATCH_PROMO');
		break;
	case 'populate_ff_Excel_btn':
		processKabaddiProcedures('POPULATE-FF_GRAPHICS');
		break;
	}
}
function processKabaddiProcedures(whatToProcess, whichInput)
{
	var value_to_process; 
	
	switch(whatToProcess) {
	case 'READ-MATCH-AND-POPULATE':
		if(match_data == null){
			return false;
		}
		value_to_process = $('#matchFileTimeStamp').val();
		break;
	case 'GET-CONFIG-DATA':
		value_to_process = $('#select_configuration_file option:selected').val();
		break;
	
	case 'LOAD_MATCH':
		value_to_process = whichInput.val();
		//alert(value_to_process);
		break;	
	case 'POPULATE-FF_MATCH_ID':
		value_to_process = 'D://DOAD_In_House_Everest//Everest_Sports//Everest_GIKPL_2025//Scenes//MatchID.sum';
		break;
	case 'POPULATE-FF_TOURNAMENT ROULES':
		value_to_process = 'D://DOAD_In_House_Everest//Everest_Sports//Everest_GIKPL_2025//Scenes//TournamentRules.sum';
		break;
	case 'POPULATE-FF_MATCH_PROMO':
			value_to_process = 'D://DOAD_In_House_Everest//Everest_Sports//Everest_GIKPL_2025//Scenes//MatchID.sum'+","+
				$('#selectMatchPromo option:selected').val();
		break;
	case 'POPULATE-FF_GRAPHICS':
		 value_to_process = 'D://DOAD_In_House_Everest//Everest_Sports//Everest_GIKPL_2025//Scenes//FF_Row_Col.sum'+","+
				$('#selectMatchPromo option:selected').val();
		break;
	}

	$.ajax({    
        type : 'Get',     
        url : 'processKabaddiProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + value_to_process, 
        dataType : 'json',
        success : function(data) {
			match_data = data;
        	switch(whatToProcess) {
			case 'SWAP_DATA':
				alert('values are swaped');
				break;
			case 'GET-CONFIG-DATA':
				initialiseForm('UPDATE-CONFIG',data);
				break;
			case 'READ-MATCH-AND-POPULATE':
				if(data){
					/*if($('#matchFileTimeStamp').val() != data.matchFileTimeStamp) {
						document.getElementById('matchFileTimeStamp').value = data.matchFileTimeStamp;
						processKabaddiProcedures('READ-MATCH-AND-POPULATE',null);
						match_data = data;
					}*/
					processKabaddiProcedures('READ-MATCH-AND-POPULATE',null);
						match_data = data;
					addItemsToList('LOAD_EVENTS',data);
					//processKabaddiProcedures('READ-MATCH-AND-POPULATE',null);
					
					if(data.clock) {
						if(document.getElementById('match_time_hdr')) {
							document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
								millisToMinutesAndSeconds(data.clock.matchTotalMilliSeconds);
						}
					}
				}
				break;
			case 'LOAD_MATCH':
				match_data = data;
				addItemsToList('LOAD_EVENTS',data);
				processKabaddiProcedures('READ-MATCH-AND-POPULATE',null);
				document.getElementById('kabaddi_div').style.display = '';
				document.getElementById('select_event_div').style.display = '';
				//setInterval(displayMatchTime, 500);
				break;
			case 'MATCH-PROMO_GRAPHICS-OPTIONS':
				addItemsToList('MATCH-PROMO-OPTIONS', data);
				break;	
			case 'EXCEL_FF_GRAPHICS_OPTION':
				addItemsToList('EXCEL_FF_GRAPHICS-OPTIONS', data);
				break;	
        	case 'POPULATE-SCOREBUG': case 'POPULATE-SCORELINE': case 'POPULATE-TOURNAMENT_LOGO': case 'POPULATE-GOLDEN_RAID':
        	case 'POPULATE-FF_MATCH_ID':case 'POPULATE-FF_TOURNAMENT ROULES':case 'POPULATE-FF_MATCH_PROMO':case "POPULATE-FF_GRAPHICS":
        		if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-SCOREBUG':
						processKabaddiProcedures('ANIMATE-IN-SCOREBUG');		
						break;
					case 'POPULATE-SCORELINE':
						processKabaddiProcedures('ANIMATE-IN-SCORELINE');		
						break;
					case 'POPULATE-TOURNAMENT_LOGO':
						processKabaddiProcedures('ANIMATE-IN-TOURNAMENT_LOGO');		
						break;	
					case 'POPULATE-GOLDEN_RAID':
						processKabaddiProcedures('ANIMATE-IN-GOLDEN_RAID');		
						break;
					case 'POPULATE-FF_MATCH_ID':
						processKabaddiProcedures('ANIMATE-IN-FF_MATCH_ID');		
						break;
					case 'POPULATE-FF_TOURNAMENT ROULES':
						processKabaddiProcedures('ANIMATE-IN-FF_TOURNAMENT ROULES');	
						break;
					case 'POPULATE-FF_MATCH_PROMO':
						processKabaddiProcedures('ANIMATE-IN-FF_MATCH_PROMO');	
						break;
					case "POPULATE-FF_GRAPHICS":
						processKabaddiProcedures('ANIMATE-IN-FF_FF_GRAPHICS');	
						break;
					}
				}
				break;
        	}
    		processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error: function(jqXHR, textStatus, errorThrown) {    
        	console.error('Error occurred in ' + whatToProcess + ' with error description: ' + textStatus, errorThrown);
        }    
	});
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var div,row,header_text,event_text,select,option,tr,th,thead,text,table,tbody,teamName;
	var cellCount=0;
	switch (whatToProcess) {
	case 'LOAD_EVENTS':
		
		$('#select_event_div').empty();
		
		if (dataToProcess)
		{
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');
			
			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);

			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('h6');
			header_text.id = 'match_time_hdr';
			header_text.innerHTML = 'Match Time: 00:00';
			row.insertCell(0).appendChild(header_text);
			
			/*if(dataToProcess.events != null && dataToProcess.events.length > 0) {
				max_cols = dataToProcess.events.length;
				if (max_cols > 20) {
					max_cols = 20;
				}
				event_text = document.createElement('h6');
				for(var i = 0; i < max_cols; i++) {
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventTeamId != 0){
						if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventTeamId == dataToProcess.homeTeamId){
							teamName = dataToProcess.homeTeam.teamName4;
						}
										
						if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventTeamId == dataToProcess.awayTeamId){
							teamName = dataToProcess.awayTeam.teamName4;
						}				
					}else{
						teamName = '';
					}
					
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType == 'suspension'){
						//alert(dataToProcess.events[(dataToProcess.events.length - 1) - i].playerId);
						teamName = teamName + '-' + dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId;
					}
					
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType) {
						if(event_text.innerHTML) {
							event_text.innerHTML = event_text.innerHTML + ', ' + dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType.replaceAll('_',' ') + ' {' + teamName + '}' ; // .match(/\b(\w)/g).join('')
						} else {
							event_text.innerHTML = dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType.replaceAll('_',' ') + ' {' + teamName + '}'; // .match(/\b(\w)/g).join('')
						}
					}
				}
				event_text.innerHTML = 'Events: ' + event_text.innerHTML;
				row.insertCell(1).appendChild(event_text);
			}*/
			
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			thead = document.createElement('thead');
			tbody = document.createElement('tbody');
			tr = document.createElement('tr');
			for (var j = 0; j <= 1; j++) {
			    th = document.createElement('th'); // Column
				th.scope = 'col';
			    switch (j) {
				case 0:
				    th.innerHTML = dataToProcess.homeTeam.teamName1 + ': ' + dataToProcess.homeTeamScore ;
					break;
				case 1:
					th.innerHTML = dataToProcess.awayTeam.teamName1 + ': ' + dataToProcess.awayTeamScore ;
					break;
				}	
				tr.appendChild(th);
			}
			thead.appendChild(tr);
			table.appendChild(thead);
			document.getElementById('select_event_div').appendChild(table);
			
			/*tbody = document.createElement('tbody');
			for(var i = 0; i <= dataToProcess.homeSquad.length - 1; i++) {
				row = tbody.insertRow(tbody.rows.length);
				for(var j = 1; j <= 2; j++) {
					text = document.createElement('text');
					switch(j){
					case 1:
						text.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].full_name ;
						break;
					case 2:
						text.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].full_name ;
						break;
					}
					
					row.insertCell(j-1).appendChild(text);
				}
			}				
			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('header');
			header_text.innerHTML = 'Substitutes';
			row.insertCell(0).appendChild(header_text);*/
			
			/*max_cols = dataToProcess.homeSubstitutes.length;
			if(dataToProcess.homeSubstitutes.length < dataToProcess.awaySubstitutes.length) {
				max_cols = dataToProcess.awaySubstitutes.length;
			}
			
			for(var i = 0; i <= max_cols-1; i++) {
				
				row = tbody.insertRow(tbody.rows.length);
				
				for(var j = 0; j <= 1; j++) {
					
					addSelect = false;
					
					switch(j) {
					case 0:
						if(i <= parseInt(dataToProcess.homeSubstitutes.length - 1)) {
							addSelect = true;
						}
						break;
					case 1:
						if(i <= parseInt(dataToProcess.awaySubstitutes.length - 1)) {
							addSelect = true;
						}
						break;
					}

					text = document.createElement('label');
					
					if(addSelect == true) {
					
						switch(j){
						case 0:
							text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].full_name;
							break;
							
						case 1:
							text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].full_name;
							break;
						}
						text.setAttribute('style','cursor: pointer;');
					
					}	
				
					row.insertCell(j).appendChild(text);
				
				}
			}				

			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);*/
		}
	    
		break;
	case "MATCH-PROMO-OPTIONS":
	    $('#select_graphic_options_div').empty();
	
	    header_text = document.createElement('h6');
	    header_text.innerHTML = 'Select Graphic Options';
	    document.getElementById('select_graphic_options_div').appendChild(header_text);
	
	    table = document.createElement('table');
	    table.setAttribute('class', 'table table-bordered');
	
	    tbody = document.createElement('tbody');
	    table.appendChild(tbody);
	    document.getElementById('select_graphic_options_div').appendChild(table);
	
	    row = tbody.insertRow();
	    cellCount = 0;
	
	    // Dropdown cell
	    select = document.createElement('select');
	    select.id = 'selectMatchPromo';
	    select.name = select.id;
	
	    dataToProcess.forEach(function(oop) {
	        option = document.createElement('option');
	        option.value = oop.matchnumber;
	        option.text = `${oop.matchnumber} - ${oop.home_Team.teamName1} Vs ${oop.away_Team.teamName1}`;
	        select.appendChild(option);
	    });
	
	    row.insertCell(cellCount++).appendChild(select);
	
	    // Buttons cell
	    let buttonsDiv = document.createElement('div');
	
	    let populateBtn = document.createElement('input');
	    populateBtn.type = 'button';
	    populateBtn.name = 'populate_match_promo_btn';
	    populateBtn.value = 'Populate Match Promo';
	    populateBtn.id = populateBtn.name;
	    populateBtn.setAttribute('onclick', "processUserSelection(this)");
	    buttonsDiv.appendChild(populateBtn);
	
	    let cancelBtn = document.createElement('input');
	    cancelBtn.type = 'button';
	    cancelBtn.name = 'cancel_graphics_btn';
	    cancelBtn.id = cancelBtn.name;
	    cancelBtn.value = 'Cancel';
	    cancelBtn.setAttribute('onclick', "processUserSelection(this)");
	    buttonsDiv.appendChild(cancelBtn);
	
	    row.insertCell(cellCount++).appendChild(buttonsDiv);
	
	    document.getElementById('select_graphic_options_div').style.display = '';
	    break;
	    
	   case "EXCEL_FF_GRAPHICS-OPTIONS":
	   
	    $('#select_graphic_options_div').empty();
	
	    header_text = document.createElement('h6');
	    header_text.innerHTML = 'Select Graphic Options';
	    document.getElementById('select_graphic_options_div').appendChild(header_text);
	
	    table = document.createElement('table');
	    table.setAttribute('class', 'table table-bordered');
	
	    tbody = document.createElement('tbody');
	    table.appendChild(tbody);
	    document.getElementById('select_graphic_options_div').appendChild(table);
	
	    row = tbody.insertRow();
	    cellCount = 0;
	
	    // Dropdown cell
	    select = document.createElement('select');
	    select.id = 'selectMatchPromo';
	    select.name = select.id;
	
	    for (let i = 0; i < dataToProcess.length; i++) {
			option = document.createElement('option');
			option.value = dataToProcess[i];
			option.text = dataToProcess[i];
			select.appendChild(option);
		}
		row.insertCell(cellCount).appendChild(select);
		option.setAttribute('onclick', "processUserSelection(this)");
		cellCount++;
	    row.insertCell(cellCount++).appendChild(select);
	
	    // Buttons cell
	    div = document.createElement('div');
	
	    option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'populate_ff_Excel_btn';
	    option.value = 'Populate EXCEL';
	    option.id = option.name;
	    option.setAttribute('onclick', "processUserSelection(this)");
	    div.appendChild(option);
	
	    option= document.createElement('input');
	    option.type = 'button';
	    option.name = 'cancel_graphics_btn';
	    option.id = option.name;
	    option.value = 'Cancel';
	    option.setAttribute('onclick', "processUserSelection(this)");
	    div.appendChild(option);
	
	    row.insertCell(cellCount++).appendChild(div);
	
	    document.getElementById('select_graphic_options_div').style.display = '';
	     setTimeout(function () {
	        $('#selectMatchPromo').select2({
	            placeholder: 'Select an option',
	            allowClear: true
	        });
	    }, 0);
	    break;

	}
}
function removeSelectDuplicates(select_id)
{
	var this_list = {};
	$("select[id='" + select_id + "'] > option").each(function () {
	    if(this_list[this.text]) {
	        $(this).remove();
	    } else {
	        this_list[this.text] = this.value;
	    }
	});
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}	
