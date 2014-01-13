//------------------------------------------------------
// Copyright Eloqua Corporation.
//
var elqSiteID = '1795';
var elqVer = 'v200';
//
var elqERoot = 'now.eloqua.com/';
var elqSecERoot = 'secure.eloqua.com/';
//
//------------------------------------------------------
var elqCurESite = '', elqAltESite = '', elqWCount = 0, elqTryI = false;
var elqStr = '', elqTID = null, elqTRun = false, elqLoad = false, elqFail = false;
if (location.protocol == 'https:') { elqCurESite = 'https://' + elqSecERoot; elqAltESite = 'http://' + elqERoot;}
else { elqCurESite = 'http://' + elqERoot; elqAltESite = 'https://' + elqSecERoot;}
var elqCurE = elqCurESite + 'visitor/' + elqVer + '/svrGP.aspx';
if ((navigator.appName == 'Netscape') && (parseInt(navigator.appVersion) > 4)) { elqTryI = true; }
if (((navigator.appName == 'Microsoft Internet Explorer') || (navigator.userAgent.indexOf('AOL') != -1))) { elqTryI = true; }
function elqClearT() { if(elqTRun) { clearTimeout(elqTID); elqTID = null; elqTRun = false;}}
function elqWrite() {
	if ((elqWCount > 75) || elqFail) { elqClearT(); }
	else { elqWCount++;
		if (!elqLoad) {	elqTRun = true;	elqTID = setTimeout('elqInit();', 1000);}
		else { elqClearT();	if (!elqFail) {document.write(elqStr); }}}}
function elqInit(){	elqClearT(); elqWrite();}
function elqReplace(string,text,by) {
    var strLength = string.length, txtLength = text.length;
    if ((strLength == 0) || (txtLength == 0)) return string;
    var i = string.indexOf(text);
    if ((!i) && (text != string.substring(0,txtLength))) return string;
    if (i == -1) return string;
    var newstr = string.substring(0,i) + by;
    if (i+txtLength < strLength)
        newstr += elqReplace(string.substring(i+txtLength,strLength),text,by);
    return newstr; }
function elqQString(strVariable, strDefault, intUnencode) {
	var strSearchString = strVariable.toLowerCase() + '=';
	var strQString = location.search + '&';
	var strQStringLower = strQString.toLowerCase();
	var intStart = strQStringLower.indexOf(strSearchString);
	if (intStart != -1) {
		strQString = strQString.substring(intStart);
		strQString = strQString ? strQString.substring(strQString.indexOf('=') + 1, strQString.indexOf('&')) : '';}
	if ((intStart == -1) || (strQString == '')) { strQString = strDefault;}
	if (intUnencode == 1) {	strQString = elqReplace(strQString,'%26','&');}
	else if (intUnencode == 2) { strQString = elqReplace(elqReplace(strQString,'%26','&'),'%23','#');}
	return encodeURI(strQString);}
function elqAddQS(strURL, strAdd) {
	if ((strAdd == '') || (strURL == '')) {return strURL;}
	var intP = strURL.indexOf('?');
	var intH = (strURL.indexOf('#') != -1) ? strURL.indexOf('#') : strURL.length;
	if (intP == -1) {return (strURL.substring(0,intH) + '?' + strAdd + strURL.substring(intH,strURL.length));}
	if (intP == strURL.length - 1) { return (strURL + strAdd);}
	return (strURL.substring(0,intP+1) + strAdd + '&' + strURL.substring(intP+1,strURL.length));}
