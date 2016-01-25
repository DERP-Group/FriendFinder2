/**
 * 
 */
document.addEventListener('DOMContentLoaded', function() {
    init();
}, false);

var mappingTokenResponse;
var mappingTokenError;
var linkSteamIdResponse;
var linkSteamIdError;
var token;

function init(){
	//Grab token from query params
	token = window.uQuery('token');
	
	if(token){
		var url = 'http://127.0.0.1:9080/livefinder/auth/mappingToken?token=' + token;
		reqwest({
		    url: url
		    , method: 'get'
		    , type: 'json'
		    , contentType: 'application/json'
		}).then(redeemMappingTokenSuccess, redeemMappingTokenFailure);
	}else{
		alert('A valid token was not provided.');
	}
}

function redeemMappingTokenSuccess(response){
	mappingTokenResponse = response;
	populateForm(response);
} 

function redeemMappingTokenFailure(error, message){
	mappingTokenError = error;
	qwery('#steamSubmitButton')[0].disabled = true;
	alert(error.responseText);
}

function populateForm(userAccount){
	var steamId = userAccount.steamId;
	if(steamId){
		qwery('#steamExternalId')[0].value = steamId;
	}
}

function linkSteamId(){
	var steamId = qwery('#steamExternalId')[0].value;
	if(steamId){
		var url = 'http://127.0.0.1:9080/livefinder/auth/steam/linkIds?mappingToken=' + token + '&externalId=' + steamId;
		reqwest({
		    url: url
		    , method: 'get'
		    , type: 'json'
		    , contentType: 'application/json'
		}).then(linkSteamIdSuccess, linkSteamIdFailure);
	}else{
		alert('A valid steamId must be provided.');
	}
}

function linkSteamIdSuccess(response){
	linkSteamIdResponse = response;
} 

function linkSteamIdFailure(error, message){
	linkSteamIdError = error;
	alert(error.responseText);
}