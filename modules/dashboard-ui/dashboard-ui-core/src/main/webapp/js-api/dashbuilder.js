/*
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Javascript API V1 for Dashbuilder.
 * See http://www.dashbuilder.org for details
 */

// Default dashbuilder path
var DASHBUILDER_PATH = '/dashbuilder';

DASHBUILDER_START_LOAD_KPI = function() {
   loadDiv = document.getElementById("title_" + arguments[0]);		
   if( loadDiv ) {
      loadDiv.innerHTML = "Loading KPI..."; 
      loadDiv.style.cssText = "color:white;padding:5px;background-color:blue;width:100px;";  
   }

   kpiFrame = document.getElementById("iframe_" + arguments[0]);		
   if( kpiFrame ) {
      kpiFrame.style.border="1px solid #0000FF";
   }
}

DASHBUILDER_END_LOAD_KPI = function() {
   loadDiv = document.getElementById("title_" + arguments[0]);		
   if( loadDiv ) {
       loadDiv.style.display='none';
   }

   kpiFrame = document.getElementById("iframe_" + arguments[0]);		
   if( kpiFrame ) {
      kpiFrame.style.border="none";
   }
}


/*
 * Embeds a chart according to the following parameters:
 * idElement: Unique identifier for container element where KPI will be placed in.
 * kpi: Unique identifier of KPI
 * width: KPI region width
 * height: KPI region height
 * lang (optional): Language selector (i.e. 'en', 'es', 'ja')
 */
function dashbuilder_embed_chart(idElement, kpi, width, height, lang) {
    var frameURL=dashbuilder_get_kpi_url(kpi,lang);
    dashbuilder_embed_iframe(idElement, frameURL, kpi, width, height, false, false);
}

/*
 * Embeds a chart according to the following parameters:
 * idElement: Unique identifier for container element where KPI will be placed in.
 * kpi: Unique identifier of KPI
 * initialWidth: Table initial width (will be resized to required size)
 * initialHeight: Table initial height (will be resized to required size)
 * maxWidth:  Table max width  (if content goes beyond that size, scrollbars will be shown)
 * maxHeight: Table max height (if content goes beyond that size, scrollbars will be shown)
 * lang (optional): Language selector (i.e. 'en', 'es', 'ja')
 */
function dashbuilder_embed_table(idElement, kpi, initialWidth, initialHeight, maxWidth, maxHeight, lang) {
    var frameURL=dashbuilder_get_kpi_url(kpi,lang);
    dashbuilder_embed_iframe(idElement, frameURL, kpi, initialWidth, initialHeight, true, true, maxWidth, maxHeight, lang);
}


/*
 * Embeds a KPI element inside a given container
 * idElement: Unique identifier for container element where KPI will be placed in.
 * kpi: Unique identifier of KPI
 * width: KPI region width
 * height: KPI region height
 * autoResize: Tries to resize IFRAME content automatically (highly recommended for tables).
 * scrolling: Specifies whether scroll bars should be displayed (default is false)
 * language: Language selector (i.e. 'en', 'es', 'ja')
 */
function dashbuilder_embed_kpi(idElement, kpi, width, height, autoResize, scrolling, lang) {
    var frameURL=dashbuilder_get_kpi_url(kpi,lang);
    dashbuilder_embed_iframe(idElement, frameURL, kpi, width, height, autoResize, scrolling);
}


/*
 * Embeds iframe to show a given KPI url
 */
function dashbuilder_embed_iframe(idElement, frameURL, kpi, width, height, scrolling, autoResize, maxWidth, maxHeight ) {
    // Create new IFRAME element
    mframe = document.createElement("IFRAME");
    mframe.id = "iframe_"+idElement;
    mframe.setAttribute("src", frameURL);

    if( scrolling) {  
      mframe.setAttribute("scrolling", "auto");
    } else {
       mframe.setAttribute("scrolling", "no");
    }
    
    var styles = "";
  
    mframe.width  = width+"px";
    mframe.height = height+"px";    
 
    if( maxWidth ) {
        styles +="max-width:"+maxWidth+"px;";        
    }    

    if( maxHeight ) {
        styles +="max-height:"+maxHeight+"px;";        
    }    

    styles +="border:0px;";
    styles +="margin:0px;";
    styles +="padding:0px;";
    
    mframe.style.cssText = styles;   

    if( autoResize ) {
       mframe.onload = function() {	
    	dashbuilder_autoResize(mframe.id);	
        DASHBUILDER_END_LOAD_KPI(idElement, kpi);
       }
    } else {
        mframe.onload = function() {		
        DASHBUILDER_END_LOAD_KPI(idElement, kpi);
       }
    }

    // Create new DIV element
    mdiv = document.createElement("DIV");
    mdiv.id = "title_"+idElement;
 
    // Create a DIV element on top to display 'loading message'.

    var myElement = document.getElementById(idElement);         

    myElement.appendChild(mdiv);
    myElement.appendChild(mframe);

    DASHBUILDER_START_LOAD_KPI(idElement, kpi);    
}

/*
 * Returns the URL to embed a given KPI (by code), for a given locale
 * kpi: Unique identifier of KPI
 * language: Language selector (i.e. 'en', 'es', 'ja')
 */
function dashbuilder_get_kpi_url(kpi,language) {
    url = DASHBUILDER_PATH + '/kpi/show?kpi=' + kpi;
    if( language ){
       url += '&locale='+language;
    }
    return url;
}


/**
 * Helper function that resizes IFRAME to its size
 * id: IFRAME identifier
 */
function dashbuilder_autoResize(id){
    var newHeight;
    var newWidth;

    if(document.getElementById){

        var iframeElement = document.getElementById(id);
        if( iframeElement ) {

            newHeight=document.getElementById(id).contentWindow.document.body.scrollHeight + 20;
            newWidth =document.getElementById(id).contentWindow.document.body.scrollWidth+20;
            iframeElement.height = newHeight + "px";
            iframeElement.width  = newWidth  + "px";
        }
    }
}