<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.jboss.dashboard.ui.formatters.FactoryURL"%>
<%@ page import="org.jboss.dashboard.workspace.Parameters"%>
<%@ page import="org.jboss.dashboard.LocaleManager" %>
<%@ page import="org.jboss.dashboard.ui.HTTPSettings" %>
<%@ page import="org.jboss.dashboard.ui.panel.AjaxRefreshManager" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%= LocaleManager.currentLocale() %>"/>

// JBoss Inc. All rights reserved.

// Boundary for multipart forms.  DO NOT CHANGE IT !!!
var boundary = "AJAX_Boundary_" + new Date().getMilliseconds() * new Date().getMilliseconds() * new Date().getMilliseconds();
var ajaxAlertsEnabled = false;
var ajaxRequestNumber = 0;
var ajaxMaxRequestNumber = <%=AjaxRefreshManager.lookup().getMaxAjaxRequests()%>;

/**
* Loads a given url into element with id tagId. If a body is specified, it uses multipart content-type
* to POST to the url.
*/

var value;
function ajaxRequest(url, body, tagId) {
    return ajaxRequest(url, body, tagId, null, null);
};

function ajaxRequest(url, body, tagId, onAjaxRequestScript, onAjaxResponseScript) {
    var ajaxHandler = new Object();
    url = url.replace(/&amp;/g,'&');
    ajaxHandler.ajaxRequestScript = onAjaxRequestScript;
    ajaxHandler.ajaxResponseScript = onAjaxResponseScript;
    if (ajaxAlertsEnabled) alert("ajax request: " + url + "\nbody:\n" + body + "\n\non " + tagId);
    beforeAjaxRequest();
    ajaxHandler.ajaxTarget = tagId;

    // Execute the ajaxRequestScript specified by client.
    if (onAjaxRequestScript != null) eval(onAjaxRequestScript);

    if (window.XMLHttpRequest) {
        ajaxHandler.ajaxReq = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
        ajaxHandler.ajaxReq = new ActiveXObject('Microsoft.XMLHTTP');
    }

    ajaxHandler.ajaxResponse = function(){
        // Only if req shows "complete"
        var readyState, status;
        try{
            readyState = ajaxHandler.ajaxReq.readyState;
            if (readyState == 4){
                status = ajaxHandler.ajaxReq.status;}
        }
        catch(e){
        }
        if (readyState == 4) {
            // only if "OK"
            if (status == 200) {
                var targetElementId;
                if (ajaxHandler.ajaxReq.responseText.indexOf("</html>")!=-1) {
                    // Just refresh the current screen.
                    document.location.href = '<%=request.getContextPath()%>';
                } else {
                    var element;
                    if (ajaxHandler.ajaxReq.responseText.indexOf("modal_component_")!=-1){
                        element = document.getElementById("modal_container");
                        // Disable the scroll bars for IE and FF
                        window.scrollTo(0,0);
                        document.body.style.overflow = 'hidden'; // IE8 & FF3.5
                        document.getElementsByTagName('html')[0].style.overflow = 'hidden';  // IE7 & IE6
                        setTimeout("doCenterModalDiv()", 1);
                    } else {
                        element = document.getElementById(ajaxHandler.ajaxTarget);
                    }
                    targetElementId = element.id;
                    var newElement = document.createElement(element.tagName);
                    newElement.id = element.id;
                    //alert("Setting "+ ajaxHandler.ajaxReq.responseText);
                    newElement.innerHTML = ajaxHandler.ajaxReq.responseText;
                    if (ajaxAlertsEnabled) alert("Set " + newElement.outerHTML);

                    // remove embedded objects from the old content to avoid js errors caused by flash
                    var objs = element.getElementsByTagName("object");
                    if (objs) {
                        for(var i=0; i < objs.length; i++){
                            objs[i].parentNode.removeChild(objs[i]);
                        }
                    }

                    element.parentNode.replaceChild(newElement, element);
                    // Execute the ajaxResponseScript specified by client.
                    if (ajaxHandler.ajaxResponseScript != null) eval(ajaxHandler.ajaxResponseScript);
                    try{
                        if ( tt_Init )
                            tt_Init(); /*Evaluate tooltips*/
                    } catch(e){/*Ignore errors on tooltip evaluation*/}
                    element = null;
                    newElement=null;

                    // Parsea Script elements y los coloca en el HEAD para evitar problema de Firefox 6/7 / Chrome
                    if (FX || CH || IE10) {
                        var ob = document.getElementById(targetElementId).getElementsByTagName("script");
                        var head = document.getElementsByTagName("head")[0];
                        // pasamos los elementos SCRIPT al HEAD
                        for(var i=0; i < ob.length; i++){
                                script = document.createElement('script');
                                script.type = 'text/javascript';
                                if(ob[i].src != "" && ob[i].src != null){
                                    script.src = ob[i].src;
                                }else{
                                    script.text = ob[i].text;
                                }
                                head.appendChild(script);
                        }
                        // borramos los elementos SCRIPT del target original
                        for(var i=0; i < ob.length; i++){
                            ob[i].parentNode.removeChild(ob[i]);
                        }
                    }
                }
            }


            afterAjaxRequest();
            ajaxHandler.ajaxTarget = '';
            ajaxRequestNumber++;
        }
    }

    var ajaxLoadingDivTimeout;
    function beforeAjaxRequest(){
        if (document.getElementById('modalAjaxLoadingDiv')) {
            ajaxLoadingDivTimeout = setTimeout('if(document.body)document.body.style.cursor = "wait"; if(document.getElementById(\'modalAjaxLoadingDiv\')); document.getElementById(\'modalAjaxLoadingDiv\').style.display=\'block\';',300);
        } else {
            ajaxLoadingDivTimeout = setTimeout('if(document.body)document.body.style.cursor = "wait";if(document.getElementById(\'ajaxLoadingDiv\')); document.getElementById(\'ajaxLoadingDiv\').style.display=\'block\'',300);
        }
    }
    function afterAjaxRequest(){
        if(document.body)document.body.style.cursor = 'default';
        if ( ajaxLoadingDivTimeout ) clearTimeout(ajaxLoadingDivTimeout);
        if (document.getElementById('modalAjaxLoadingDiv')) document.getElementById('modalAjaxLoadingDiv').style.display='none';
        if(document.getElementById('ajaxLoadingDiv')) document.getElementById('ajaxLoadingDiv').style.display='none';
    }

    var ajaxReq = ajaxHandler.ajaxReq;
    if (ajaxReq != null) {
        ajaxReq.onreadystatechange = ajaxHandler.ajaxResponse;
        if (body) {
            ajaxReq.open('POST', url, true);
            //XMLHttpRequest handles only UTF-8
            ajaxReq.setRequestHeader("Content-Type", "multipart/form-data; charset=UTF-8; boundary=" + boundary);
        } else {
            ajaxReq.open('GET', url, true);
        }
        if (ajaxAlertsEnabled)
            alert("Sending body:\n" + body);
        ajaxReq.send(body);
        if (ajaxAlertsEnabled)
            alert("Loading " + url + " into " + tagId);
        return false;
    }
    return true;
};

function getBody(element) {
    var body = '';
    if (element && element.name) {
        body += '--' + boundary + '\r\n';
        body += 'Content-Disposition: form-data; name="' + element.name + '"' + '\r\n\r\n';
        body += element.value + '\r\n';
    }
    return body;
};

/**
* Returns the body multipart representation for a form, adding an ajaxAction parameter.
*/
function getFormBody(form) {
    return getFormBody(form, true);
};

/**
* Returns the body multipart representation for a form, adding an ajaxAction parameter, depending on second parameter.
*/
function getFormBody(form, addAjaxParameter) {
    var body = '';
    for (var i = 0; i < form.length; i++) {
        field = form[i];
        if (!field.name || field.name=='')
            continue;
        if (field.type == 'checkbox' || field.type == 'radio') {
            if (field.checked)
                body += getBody(field);
        }
        else if ((field.type == 'select-one' || field.type == 'select-multiple')) {
            for (var j = 0; j < field.length; j++) {
                if (field[j].selected) {
                    value = field[j].value;
                    if (value == '') value = field[j].text;
                    body += '--' + boundary + '\r\n';
                    body += 'Content-Disposition: form-data; name="' + field.name + '"' + '\r\n\r\n';
                    body += value + '\r\n';
                }
            }
        }
        else {
            body += getBody(field);
        }
    }
    if(addAjaxParameter){
        if(ajaxAlertsEnabled)
            alert("Adding ajax parameter to form to be sent");
        body += '--' + boundary + '\r\n';
        body += 'Content-Disposition: form-data; name="ajaxAction"' + '\r\n\r\n';
        body += 'true\r\n';
    }
    else{
        if(ajaxAlertsEnabled)
            alert("Getting form body without ajax parameter: "+addAjaxParameter);
    }
    body += "--" + boundary;
    form=null;
    return body + "--";
};

var ajaxPreviousHandlers = new Object();

function setAjaxTarget(element, targetId) {
    return setAjaxTarget(element, targetId,  null,  null);
};

function submitAjaxForm(form) {
    if (form) {
        if (form.onsubmit && (ajaxRequestNumber < ajaxMaxRequestNumber)) {
            var wasAjaxed = false;
            var formClass = form.styleClass;
            if (formClass) {
                 wasAjaxed = formClass.indexOf('ajaxedElement') != -1;
            }
            if(wasAjaxed) {
                form.onsubmit();
            }
            else if (form.onsubmit()) {
                submitForm(form);
            }
        }
        else {
            submitForm(form);
        }
    }
};

function submitForm(form) {
    if (form) {
        // Double click control.
        form.onsubmit = function() {
            processDoubleClick();
            return false;
        };
        // Submit the form.
        form.submit();
    }
}

function processDoubleClick() {
	var message = "<i18n:message key="ui.ajax.doubleClickWarn">! Por favor, espere a que finalice la petici\u00F3n en curso !</i18n:message>";
    alert(message);
}

function sendFormToHandler(form, component, property){
    prepareFormForHandler(form, component, property);
    submitAjaxForm(form);
};

function prepareFormForHandler(form, component, property){
    setFormInputValue(form, '<%=FactoryURL.PARAMETER_BEAN%>', component );
    setFormInputValue(form, '<%=FactoryURL.PARAMETER_PROPERTY%>', property );
    setFormInputValue(form, '<%=Parameters.DISPATCH_ACTION%>', "_factory" );
};

function getFormInputValue( form, name ){
    for (var i = 0; i < form.length; i++) {
        var field = form[i];
        if (!field.name) continue;
        if(field.name == name){
            return field.value;
        }
    }
    return null;
};

function setFormInputValue( form, name, value ){
    for (var i = 0; i < form.length; i++) {
        var field = form[i];
        if (!field.name) continue;
        if(field.name == name){
            field.value = value;
            return;
        }
    }
    var theHidden = document.createElement('input');
    theHidden.type = 'hidden';
    theHidden.name = name;
    theHidden.value = value;
    form.appendChild(theHidden);
    form=null;
};

function setAjaxTarget(element, targetId, onAjaxRequestScript, onAjaxResponseScript) {
    var elementClass = element.styleClass;
    if ( elementClass ){
        var wasAjaxed = elementClass.indexOf('ajaxedElement') != -1;
        if ( wasAjaxed ) return;
        element.styleClass += ' ajaxedElement';
    }
    if (element.nodeName.toLowerCase() == "a") {
        var destination = element.href;
        if (element.onclick) eval('ajaxPreviousHandlers[\'' + element.id + '\']=element.onclick;');
        element.onclick = function() {

            // Double click control.
            if (element) {
                element.onclick = function() {
                    processDoubleClick();
                    return false;
                };
            }
            // Evaluate first the user defined 'onclick' function (if any).
            var clickReturn = true;
            if ( ajaxPreviousHandlers[this.id] ){
                clickReturn = ajaxPreviousHandlers[this.id]();
                //alert("There is a previous handler "+ajaxPreviousHandlers[this.id]+" that says " + clickReturn);
            }
            // Process the link.
            if (clickReturn != false) {
                // Check max consecutive ajax request.
                if (ajaxRequestNumber >= ajaxMaxRequestNumber) return true;
                eval("ret = ajaxRequest('" + destination + "&ajaxAction=true', null, '" + targetId + "', '" + onAjaxRequestScript + "', '" + onAjaxResponseScript + "')");
                element=null;
                return ret;
            } else {
                element=null;
                return false;
            }
        };
        element = null;
    }
    else if (element.nodeName.toLowerCase() == "form") {
        var containsFileInputs = false;
        if (element.elements) {
            for( elementIndex=0; elementIndex < element.elements.length; elementIndex++){
                var inputElement = element.elements[elementIndex];
                if( inputElement.type == 'file' ){
                    containsFileInputs = true;
                    break;
                }
            }
        }
        if (containsFileInputs && !isFileUploadSupported()) {
            if(ajaxAlertsEnabled)alert('Form containing file inputs cannot be set to use Ajax');
            return false;
        }
        else {
            if(ajaxAlertsEnabled)alert('Form not containing file inputs can be set to use Ajax');
        }
        if (element.onsubmit) {
            eval('ajaxPreviousHandlers[\'' + element.id + '\']=element.onsubmit;');
        }
        //alert("Putting new onsubmit for "+element.id);
        element.onsubmit = function() {

            // Double click control.
            if (element) {
                element.onsubmit = function() {
                    processDoubleClick();
                    return false;
                };
            }
            // Evaluate first the user defined 'onsubmit' function (if any).
            var clickReturn = true;
            if ( ajaxPreviousHandlers[this.id] ){
                clickReturn = ajaxPreviousHandlers[this.id]();
                //alert("There is a previous handler "+ajaxPreviousHandlers[this.id]+" that says " + clickReturn);
            }
            // Submit the form.
            if (clickReturn != false) {
                var ret = false;
                // Check max consecutive ajax request.
                if (ajaxRequestNumber >= ajaxMaxRequestNumber) return true;
                eval("ret = ajaxRequest(this.action?this.action:'Controller', getFormBody(this, true), '" + targetId + "', '" + onAjaxRequestScript + "', '" + onAjaxResponseScript + "');");
                return ret;
            } else {
                element=null;
                return false;
            }
        }
    } else {
        if (ajaxAlertsEnabled)
            alert("Unsupported element nodeName " + element.nodeName);
    }
    return true;
};

/**
* Modifies an item (form or anchor), so if it is inside a panel, it loads inside the panel area.
*/
function doSetAjax(elementId) {
    return doSetAjax(elementId, null, null);
};
/**
* Modifies an item (form or anchor), so if it is inside a panel, it loads inside the panel area.
*/
function doSetAjax(elementId, onAjaxRequestScript, onAjaxResponseScript) {
    if (window.XMLHttpRequest || window.ActiveXObject) {
        if (ajaxAlertsEnabled) alert("Looking for panel enclosing " + elementId)
        var element = document.getElementById(elementId);
        if (!element) {
            if (ajaxAlertsEnabled) alert("No item with id " + elementId + " found.");
            return;
        }
        var parentElement = element.parentNode;
        while (parentElement) {
            if (parentElement && parentElement.id && (parentElement.id.indexOf("<%=HTTPSettings.AJAX_AREA_PREFFIX%>") == 0)) {
                if (ajaxAlertsEnabled) alert("Found " + parentElement.id);
                var retValue = setAjaxTarget(element, parentElement.id, onAjaxRequestScript, onAjaxResponseScript);
                element = null;
                parentElement = null;
                return retValue;
            }
            parentElement = parentElement.parentNode;
        }
        if (ajaxAlertsEnabled) {
            alert("Cannot find panel envolving item with id " + elementId);
        }
        element = null;
        parentElement = null;
    }
};

function setAjax(elementId) {
    return setAjax(elementId, null, null);
};

function setAjax(elementId, onAjaxRequestScript, onAjaxResponseScript) {
    if (ajaxRequestNumber > ajaxMaxRequestNumber) return false;
    if (NS || IE || OP || FX || CH) setTimeout("doSetAjax('" + elementId + "', '" + onAjaxRequestScript + "', '" + onAjaxResponseScript + "')", 1);
};

function isFileUploadSupported() {
    return false;
};

function refreshPanel(id) {
    setTimeout("doRefreshPanel('"+ id + "')",10);
};

function doRefreshPanel(id) {
    var formm = document.getElementById('<%=AjaxRefreshManager.FORM_IDENTIFIER_PREFFIX%>'+id);
    submitAjaxForm(formm);
    formm = null;
};

function dropPanelToRegion(regionId, panelId, position){
    if (panelId.indexOf('Panel:') == 0){
        document.panelDragAndDropToRegion.panelId.value=panelId.substring('Panel:'.length);
        document.panelDragAndDropToRegion.region.value=regionId;
        document.panelDragAndDropToRegion.position.value=position;
        document.panelDragAndDropToRegion.submit();
    } else {
        document.instanceDragAndDropToRegion.panelId.value=panelId;
        document.instanceDragAndDropToRegion.region.value=regionId;
        document.instanceDragAndDropToRegion.position.value=position;
        document.instanceDragAndDropToRegion.submit();
    }
};

var currentExpandedDiv = null; <%-- This contains the expanded Div ID --%>
var maxVisibleItems = 13; <%-- This is the max of items visible in the panel instances popup --%>
var expandedNode = null; <%-- The current expanded tree node --%>

function popupExpand(id){
    var elm = document.getElementById(id);
    if(!elm) return;
    if(elm.style.display=='block'){
        elm.style.display='none';
    } else {
        elm.style.display='block';
    }
    if (currentExpandedDiv!=null) {
        document.getElementById(currentExpandedDiv).style.display='none';
        document.getElementById('panel_navigation_right').style.display='none';
        document.getElementById('panel_navigation_left').style.display='none';
        document.getElementById('panel_navigation_right_disabled').style.display='none';
        document.getElementById('panel_navigation_left_disabled').style.display='none';
        currentExpandedDiv=null;
    }
    elm = null;
};

function manageBoldness(id) {
    document.getElementById("parent_"+id).style.fontWeight='bold';
    if (expandedNode != null && expandedNode != "parent_"+id) document.getElementById(expandedNode).style.fontWeight='normal';
    expandedNode = "parent_"+id;
};

function showSection(amount) {
    if (amount>0 && isLastSection()) return;
    if (amount<1 && isFirstSection()) return;
    clearSection();
    var currentSection = Number(document.getElementById(currentExpandedDiv+"_current_section").value);
    var nextSection = currentSection + Number(amount);
    var maxItems = Number(document.getElementById(currentExpandedDiv+"_maxUid").value);
    var start;
    var end;
    if (currentSection < nextSection) {
        end = nextSection * maxVisibleItems;
        start = end - maxVisibleItems;
    } else {
        end = (currentSection + Number(amount)) * maxVisibleItems;
        start = end - maxVisibleItems;
    }
    var i;
    for (i=start; (i<end && i<maxItems); i++) {
        document.getElementById(currentExpandedDiv+"_displayable_"+i).style.display="block";
    }
    document.getElementById(currentExpandedDiv+"_current_section").value=nextSection;

    if(!isLastSection()) {
        document.getElementById('panel_navigation_right').style.display='block';
        document.getElementById('panel_navigation_right_disabled').style.display='none';
    } else {
        document.getElementById('panel_navigation_right').style.display='none';
        document.getElementById('panel_navigation_right_disabled').style.display='block';
    }
    if(!isFirstSection()) {
        document.getElementById('panel_navigation_left').style.display='block';
        document.getElementById('panel_navigation_left_disabled').style.display='none';
    } else {
        document.getElementById('panel_navigation_left').style.display='none';
        document.getElementById('panel_navigation_left_disabled').style.display='block';
    }
};

function goToSection(num) {
    var currentSection = Number(document.getElementById(currentExpandedDiv+"_current_section").value);
    showSection(num-currentSection);
};

function isFirstSection() {
    if (Number(document.getElementById(currentExpandedDiv+"_current_section").value)==1) return true;
    return false;
};

function isLastSection() {
    var currentSection = Number(document.getElementById(currentExpandedDiv+"_current_section").value);
    return currentSection == getLastSection();
};

function getLastSection() {
    var maxItems = Number(document.getElementById(currentExpandedDiv+"_maxUid").value);

    var section = parseInt(maxItems/maxVisibleItems);
    if (maxItems % maxVisibleItems > 0) section ++;
    return section;
};

function clearSection() {
    var currentSection = Number(document.getElementById(currentExpandedDiv+"_current_section").value) * maxVisibleItems;
    var maxItems = Number(document.getElementById(currentExpandedDiv+"_maxUid").value);
    var start = currentSection - maxVisibleItems;

    var i;
    for (i=start; i<currentSection && i<maxItems; i++) {
        document.getElementById(currentExpandedDiv+"_displayable_"+i).style.display="none";
    }
};

function doItDraggable(id){
    var dragItem = new Draggable(id, {
        ghosting: !IE,
        onDrag: function(){
            if( document.selection )
                document.selection.empty();
        },
        onStart: function(draggable, event){
            showPanelDropZones();
            showRegionDropZones();
            hideModalDivBackground();
        },
        onEnd: function(draggable, event){
            hidePanelDropZones();
            hideRegionDropZones();
            showModalBackgroundDiv();
            $(id).style.position = 'relative';
        },
        revert:true});
};


function hideModalDivBackground() {
    modifyModalBackgroundDiv(false);
}

function showModalBackgroundDiv() {
    modifyModalBackgroundDiv(true);
}

function modifyModalBackgroundDiv(showIt) {
    document.getElementById('vellumNoShade').style.display = showIt ? "block" : "none";
}

function expandPanelDiv(id) {
    var currentDiv = document.getElementById(id);
    if (currentDiv != null) {
        if (currentExpandedDiv != null) {
            document.getElementById(currentExpandedDiv).style.display='none'
            goToSection(1);
        }
        document.getElementById(id).style.display='block'
        currentExpandedDiv = id;
        document.getElementById('panel_navigation_right').style.display='none';
        document.getElementById('panel_navigation_left').style.display='none';
        document.getElementById('panel_navigation_left_disabled').style.display='none';
        document.getElementById('panel_navigation_right_disabled').style.display='none';

	document.getElementById(currentExpandedDiv+"_current_section").value = 2;
	goToSection(1);
	//if (!isLastSection()) {
        //    document.getElementById('panel_navigation_right').style.display='block';
        //    document.getElementById('panel_navigation_left_disabled').style.display='block';
        //}
    }
};

function checkDraggable(id,uid){
    var draggableTable = document.getElementById(id + "_draggable_" + uid);
    if(draggableTable.style.cursor!='move'){
        draggableTable.style.cursor = 'move';
        doItDraggable(id + "_draggable_" + uid);
    }
    draggableTable = null;
}


var doDraggableTimeout;

function doDraggable(id){
    var dragItem = new Draggable(id, {
        ghosting: !IE,
        onDrag: function(){
            if( document.selection )
                document.selection.empty();
        },
        onStart: function(draggable, event){
            // Bug fixing for IE9 and IE10. See https://prototype.lighthouseapp.com/projects/8887/tickets/343-ie9-triggers-ondragmousemove-event-when-clicking-a-draggable
            // The timeout must be over 200ms. Otherwise, the click event for a draggable region is not read and there is no page change.
            doDraggableTimeout = setTimeout('showPanelDropZones();showRegionDropZones(); clearTimeout(doDraggableTimeout); doDraggableTimeout = null;',200);
        },
        onEnd: function(draggable, event){
            if( doDraggableTimeout ) {
                clearTimeout(doDraggableTimeout);
            } else {
                window.disableMenuForPanel = true;
            }
            hidePanelDropZones();
            hideRegionDropZones();
            $(id).style.position = 'relative';
        },
        revert:true});
};

function doDropable(id, regionId, numPanels){
    Droppables.add(id, {
        accept: 'popupDraggable',
        hoverclass: 'dropOnRegion',
        onDrop: function(element) {
            var droppedId = element.firstChild.innerHTML;
            if(!droppedId)
                droppedId = element.getElementsByTagName('span')[0].innerHTML;
            dropPanelToRegion(regionId, droppedId, numPanels);
        }
    });
}

/////////////////////////////////////////////////////
function showPanelDropZones(){
    modifyPanelDropZones(true);
};

function hidePanelDropZones(){
    modifyPanelDropZones(false);
};

function showRegionDropZones(){
    modifyRegionDropZones(true);
};

function hideRegionDropZones(){
    modifyRegionDropZones(false);
};
/////////////////////////////////////////////////////
function modifyPanelDropZones(showThem) {
    modifyDropZone( "panelDropZoneContainer", showThem );
};
function modifyRegionDropZones(showThem) {
    modifyDropZone( "regionDropZoneContainer", showThem );
};
/////////////////////////////////////////////////////
function modifyDropZone(modifiablePrefix, showThem ){
    var alldivs = document.getElementsByTagName("div");
    for (i = 0; i < alldivs.length; i++) {
        var divElement = alldivs[i];
        if ( divElement.id && (divElement.id.indexOf(modifiablePrefix) == 0) ){
            //Change status for given div.
            if (showThem) {
                divElement.style.display = "block";
            }
            else {
                divElement.style.display = "none";
            }
            if (showThem && IE) { //Fix heights
                if(divElement.style.pixelHeight >0){
                    divElement.style.height = divElement.style.pixelHeight+"px";
                    var internalDivs = divElement.getElementsByTagName("div");
                    for (var j=0; j<internalDivs.length; j++){
                        internalDivs[j].style.height = divElement.style.pixelHeight+"px";
                    }
                }
            }
        }
    }
};

function centerModalDiv(id) {
    Event.observe(window, 'load', function() {
        doCenterModalDiv();
    });
}

function doCenterModalDiv() {
    var element = $('ModalDialogPopUp');
    if (!(element = $(element))) return;

    var vpWidth = $(document).viewport.getWidth();
    var width = element.getWidth();

    element.style.left = (vpWidth / 2) - (width / 2) + 'px';

    var vpHeight = $(document).viewport.getHeight();
    var height = element.getLayout().get('margin-box-height');
    var scrollTop = $(document).viewport.getScrollOffsets().top;

    var avTop = (vpHeight / 2) - (height / 2) + scrollTop;

    if (avTop <= 10)
    avTop = 10;

    element.style.top = avTop+ 'px';
    element.scrollTo();
    element.style.display = 'block';
}
