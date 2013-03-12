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
<script language="JavaScript" type="text/javascript">
 <!--
    var slideSideMenu = new Object();
    slideSideMenu.minWidth = 0;
    slideSideMenu.maxWidth = 188;
    slideSideMenu.increment = 30;
    slideSideMenu.speed = 5;
    slideSideMenu.divStyleToMove = null;
    slideSideMenu.cookieName = "region_left_web_2_0_cookie";
    slideSideMenu.getCookieVal = function (offset) {
   	var endstr = document.cookie.indexOf (";", offset);
   	if (endstr == -1)
      		endstr = document.cookie.length;
   	return unescape(document.cookie.substring(offset, endstr));
    }
    slideSideMenu.GetCookie = function (name)  {
   	var arg = name + "=";
	var alen = arg.length;
   	var clen = document.cookie.length;
   	var i = 0;
	while (i < clen)
	{
	var j = i + alen;
	if (document.cookie.substring(i, j) == arg)
	 return this.getCookieVal (j);
	i = document.cookie.indexOf(" ", i) + 1;
	if (i == 0) break;
	}
	return null;
    }

    slideSideMenu.SetCookie = function (name, value) {
	   var argv = this.SetCookie.arguments;
	   var argc = this.SetCookie.arguments.length;
	   var expires = (argc > 2) ? argv[2] : null;
	   var path = (argc > 3) ? argv[3] : null;
	   var domain = (argc > 4) ? argv[4] : null;
	   var secure = (argc > 5) ? argv[5] : false;
	   document.cookie = name + "=" + escape (value) +
	        ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
	        ((path == null) ? "; path=/" : ("; path=" + path)) +
	        ((domain == null) ? "" : ("; domain=" + domain)) +
	        ((secure == true) ? "; secure" : "");
    }

    slideSideMenu.EvaluateInitialStatus = function () {
        if ( (IE && document.readyState != 'complete') || !document.getElementById("region_left_web_2_0")) {
            setTimeout("slideSideMenu.EvaluateInitialStatus()",100);
        } else {
            this.divStyleToMove = document.getElementById("region_left_web_2_0").style;
            //this.divStyleToMove.width = this.maxWidth+'px';
            var state = this.GetCookie(this.cookieName) ;
            state = state == null ? 1 : state;
            if( state == 1 ){
                this.openIt( );
            } else {
                this.SetCookie(this.cookieName, 0);
                this.divStyleToMove.width = this.minWidth+'px';
            }
        }
    }

    slideSideMenu.start = function() {
	var state = this.GetCookie(this.cookieName) ;
	state = state == null ? 0 : state;
     	if (state == 1) this.slide(null, true);
	else this.slide(null, false);
    }

    slideSideMenu.slide = function( pos , back){
        this.divStyleToMove.overflow = 'hidden';
        this.divStyleToMove.display = 'block';
        pos = (pos != null) ? pos : (back ? this.maxWidth: this.minWidth);
        if(pos < this.minWidth) pos = this.minWidth;
        if(pos > this.maxWidth) pos = this.maxWidth;
        this.divStyleToMove.width =  pos + "px";
        //alert("Move "+(back?"back":"")+" div to pos "+pos);
        var end = back ? (pos<=this.minWidth) : (pos>=this.maxWidth);

        if ( !end ){
            var totalIncrement = back ? (pos-this.increment):(pos+this.increment);
	    setTimeout("slideSideMenu.slide("+totalIncrement+","+back+")", this.speed);
        } else {
             this.divStyleToMove.width =  (!back ? this.maxWidth: this.minWidth) + "px";
             this.divStyleToMove.overflow = back ? 'hidden':'visible';
             this.divStyleToMove.display = back ? 'none':'block';
             this.SetCookie(this.cookieName, back ? 0:1);
        }
    }

    slideSideMenu.openIt = function(){
      	this.SetCookie(this.cookieName, 1);
        this.divStyleToMove.width = this.maxWidth + "px";
    }

    setTimeout ("slideSideMenu.EvaluateInitialStatus()",100);

//-->
</script>

<table border="0" cellpadding="0" cellspacing="0" class="Table_Top">
  <tr>
    <td class="Bg_Top-left" align="left">
		<div id="General_Logo"><panel:region region="General_Logo"/></div>
	</td>
    <td class="Bg_Top-right">
    <table align="right">
    	<tr>
        	<td><div id="Header_Right-top"><panel:region region="Header_Right-top"/></div></td>
        </tr>
        <tr>
        	<td><div id="Header_Right-bottom"><panel:region region="Header_Right-bottom"/></div></td>
        </tr>
    </table>
    	
    	
    </td>
  </tr>
</table>
<table width="99%" border="0" cellspacing="0" cellpadding="0">
  <tr style="height:10px; vertical-align:top;">
  	<td colspan="5" style=" height:10px;"></td>
  </tr>
  <tr>
    <td style="vertical-align:top; text-align:left; width:25%;">
        
               	<table cellpadding="0" cellspacing="0" style="width:100%; border:none; margin:0px; padding:0px;">
                    	<tr>
	                     	<td></td>
                        </tr>
               		<tr>
                     		<td><panel:region region="left_top"/></td>
                    	</tr>
               		<tr>
                     		<td></td>
                        </tr>
              	</table>
              	<table cellpadding="0" cellspacing="0"  style="width:100%; border:none; margin-top:5px; padding:0px">
                	<tr>
	                     	<td></td>
                    </tr>
                 	<tr>
                     		<td><panel:region region="left_bottom"/></td>
                	</tr>
                	<tr>
	                     	<td></td>
                     </tr>
                 </table>
	</div>
   </td>
   <td style="width:10px; vertical-align:top; padding-top:25px;">&nbsp;</td>
   <td style="vertical-align:top; text-align:left; width:50%;">
    	<table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr><td class="contentColumn" style="vertical-align:top;"><panel:region region="center_1"/></td><tr>
            <tr><td class="contentRow" 	  style="vertical-align:top;"><panel:region region="center_2"/></td><tr>
            <tr><td class="contentTab" 	  style="vertical-align:top;"><panel:region region="center_3"/></td><tr>
            <tr><td class="contentColumn" style="vertical-align:top;"><panel:region region="center_4"/></td><tr>
            <tr><td class="contentRow" 	  style="vertical-align:top;"><panel:region region="center_5"/></td><tr>
            <tr><td class="contentTab" 	  style="vertical-align:top;"><panel:region region="center_6"/></td><tr>
        </table>
        
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td style="vertical-align:top;" class="contentColumn"><panel:region region="col2_1"/></td>
		<td style="vertical-align:top;" class="contentColumn"><panel:region region="col2_2"/></td>
	<tr>
	</table>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td style="vertical-align:top;" class="contentColumn"><panel:region region="col3_1"/></td>
		<td style="vertical-align:top;" class="contentColumn"><panel:region region="col3_2"/></td>
		<td style="vertical-align:top;" class="contentColumn"><panel:region region="col3_3"/></td>
		<tr>
        </table>
    </td>
   <td style="width:10px; vertical-align:top; padding-top:25px;">&nbsp;</td>
   <td style="vertical-align:top; text-align:left; width:25%;"><table cellpadding="0" cellspacing="0" style="width:100%; border:none; margin:0px; padding:0px;">
     <tr>
       <td></td>
     </tr>
     <tr>
       <td><panel:region region="right_top"/></td>
     </tr>
     <tr>
       <td></td>
     </tr>
   </table>
     <table cellpadding="0" cellspacing="0"  style="width:100%; border:none; margin-top:5px; padding:0px">
       <tr>
         <td></td>
       </tr>
       <tr>
         <td><panel:region region="right_bottom"/></td>
       </tr>
       <tr>
         <td></td>
       </tr>
     </table>
     </div></td>
  </tr>
</table>
