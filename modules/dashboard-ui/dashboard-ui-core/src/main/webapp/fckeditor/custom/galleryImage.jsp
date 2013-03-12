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
<%@ page import="org.jboss.dashboard.LocaleManager"%>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="bui_taglib.tld" prefix="panel" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<i18n:bundle baseName="org.jboss.dashboard.ui.messages" locale="<%=LocaleManager.currentLocale()%>"/>

    <script type="text/javascript" language="javascript">

        function openOrClose(itemId) {
            var divElement = document.getElementById(itemId);
            if (divElement.style.display == 'none') {
                divElement.style.display = 'block';
            }
            else {
                divElement.style.display = 'none';
            }
            return false;
        }
    </script>

<mvc:formatter name="org.jboss.dashboard.ui.formatters.ImageSelectionFormatter">
    <mvc:formatterParam name="numCols" value="1"/>
    <mvc:fragment name="outputStart">
        <div align="center" style="overflow:auto; background:#F1F1E3; padding:10px; padding-left:15px;height:400px; border-top:solid 1px #c2c28f; border-left:solid 1px #c2c28f; border-bottom:solid 1px #aaaa7d; border-right:solid 1px #aaaa7d;">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
    </mvc:fragment>
    <mvc:fragment name="galleryOutputStart">
        <tr>
            <td align="left" style="FONT-SIZE: 10pt; font-weight: bold; COLOR: #000000; font-family : Verdana, Arial, Helvetica, sans-serif;">
                <a href="#" onclick="return openOrClose('gallery<mvc:fragmentValue name="galleryIndex"/>')">
                    <mvc:fragmentValue name="galleryName"/></a>
            </td>
        </tr>
        <tr>
        <td align="left"><div id="gallery<mvc:fragmentValue name="galleryIndex"/>" style="display:none;"><table>
    </mvc:fragment>
    <mvc:fragment name="galleryResourceRowStart">
        <tr>
    </mvc:fragment>
    <mvc:fragment name="galleryResourceOutput">
        <mvc:fragmentValue name="resourceUID" id="resourceUID">
        <td nowrap="nowrap">
            <span
                    onmouseover="var imageDiv=document.getElementById('<%=resourceUID%>');
                    var x,y;
                    if(event.clientX){x=event.clientX;}else if(event.pageX){x=event.pageX;}
                    if(event.clientY){y=event.clientY;}else if(event.pageY){y=event.pageY};

                    // Correct document scroll
                    if(window.pageYOffset){ y+=window.pageYOffset; }
                    else if( document.documentElement.scrollTop ){ y+=document.documentElement.scrollTop; }
                    if(window.pageXOffset){ x+=window.pageXOffset; }
                    else if( document.documentElement.scrollLeft){ x+= document.documentElement.scrollLeft; }
                    x+=10;
                    imageDiv.style.top = y+'px';
                    if(x>imageDiv.offsetWidth) x -=  imageDiv.offsetWidth;
                    imageDiv.style.left = x+'px';
                    imageDiv.innerHTML = '<img src=\'<mvc:fragmentValue name="resourceUrl"/>\' border=0>';
                    document.getElementById('<%=resourceUID%>').style.display = 'block';
                   "
                    onmouseout="document.getElementById('<%=resourceUID%>').style.display = 'none';"
                    >
            <a href="#"
               onclick="var useFull=<%=Boolean.valueOf(request.getParameter("fullUrl"))%>;var baseUrl=useFull?'<mvc:context includeHost="true" uri=""/>':''; window.top.opener.SetUrl(baseUrl+'<mvc:fragmentValue name="resourceUrl"/>' );window.top.opener.ResetSizes();  window.close();  return false; "
                ><mvc:fragmentValue name="resourceId"/></a>
            </span>
            <span style="position:absolute; padding:5px; background-color:#ffffff; display:none;" id="<%=resourceUID%>"></span>
        </td>
        </mvc:fragmentValue>
    </mvc:fragment>
    <mvc:fragment name="galleryResourceRowEnd">
        </tr>
    </mvc:fragment>
    <mvc:fragment name="galleryOutputEnd">
        </table></div></td>
        </tr>
    </mvc:fragment>
    <mvc:fragment name="outputEnd">
        </table>
        </div>
    </mvc:fragment>
</mvc:formatter>




<div style="height:4px;">&nbsp;</div>
<div style="padding-left:10px; padding-top:10px; background-color:#F1F1E3; height:80px; border-top:solid 1px #c2c28f; border-left:solid 1px #c2c28f; border-bottom:solid 1px #aaaa7d; border-right:solid 1px #aaaa7d;">
<form action="<factory:formUrl/>" enctype="multipart/form-data" method="POST">
    <factory:handler bean="org.jboss.dashboard.ui.components.ImageUploadHandler" action="uploadImage"/>
    <table>
    <tr>
        <factory:property bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="fileError" id="fileError">
        <td <%=Boolean.TRUE.equals(fileError) ? "class='skn-error'":""%>>
            <i18n:message key="uploadImage"/>
        </td>
        </factory:property>
        <td>
            <input type="file" class="skn-input" name="<factory:bean bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="file" />" >
        </td>
    </tr>
    <tr>
        <factory:property bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="nameError" id="nameError">
        <td <%=Boolean.TRUE.equals(nameError) ? "class='skn-error'":""%>>
            <i18n:message key="name"/>
        </td>
        </factory:property>
        <td>
            <input class="skn-input"
                   name="<factory:bean bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="name" />"
                   value="<factory:property bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="name" />"
                    >
        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">
            <input type="submit" class="skn-button" value="<i18n:message key="upload"/>">
        </td>
    </tr>
    </table>

</form>
</div>

<factory:property bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="operationSuccess" id="operationSuccess">
    <%if(Boolean.TRUE.equals(operationSuccess)){%>
    <script type="text/javascript" language="Javascript">
        window.top.opener.SetUrl( '<factory:property valueIsHTML="true" bean="org.jboss.dashboard.ui.components.ImageUploadHandler" property="lastResourceUrl"/>'.replace(/&amp;/g,'&') );
        window.top.opener.ResetSizes(); window.close();
    </script>
    <%}%>
</factory:property>
