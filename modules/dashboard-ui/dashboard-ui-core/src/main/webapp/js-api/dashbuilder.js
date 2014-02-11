/*
 * Javascript API for Dashbuilder
 *
 * TODO: Scroll management
 */

/*
 * Embeds a KPI element inside a given container
 * idElement: Unique identifier for element where KPI will be placed in
 * kpi: Unique identifier of KPI
 * width: KPI region width
 * height: KPI region height
 * language: Language selector (i.e. 'en', 'es', 'ja')
 */
function dashbuilder_embed_kpi(idElement, kpi, width, height, lang) {
    var frameURL=dashbuilder_get_kpi_url(kpi,lang);
    dashbuilder_embed_iframe(idElement, frameURL, width, height);
}


function dashbuilder_embed_iframe(idElement, frameURL, width, height) {
    // Create new IFRAME element
    mframe = document.createElement("IFRAME");
    mframe.setAttribute("src", frameURL);
    mframe.style.width = width+"px";
    mframe.style.height = height+"px";
    mframe.style.border = "0px";

    var myElement = document.getElementById(idElement);
    myElement.appendChild(mframe);
}

/*
 * Returns the URL to embed a given KPI (by code), for a given locale
 * kpi: Unique identifier of KPI
 * language: Language selector (i.e. 'en', 'es', 'ja')
 */
function dashbuilder_get_kpi_url(kpi,language) {
    url = '/dashbuilder/kpi/show?kpi=' + kpi;
    if( language ){
       url += '&locale='+language;
    }
    return url;
}