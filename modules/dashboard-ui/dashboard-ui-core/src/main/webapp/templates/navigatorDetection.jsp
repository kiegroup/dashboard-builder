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
var IE = false;
var IE10 = false;
var NS = false;
var FX = false;
var OP = false;
var CH = false;
var DHTML_support = false;
var navigatorVersion = 0;
checkBrowser();

function checkIEversion(v) {
    var r = RegExp('msie' + (!isNaN(v) ? ('\\s' + v) : ''), 'i');
    return r.test(navigator.userAgent);
}

function checkBrowser() {
    var userAgent = navigator.userAgent;
    if (userAgent.indexOf('Netscape') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('Netscape') + 9, userAgent.length));
        NS = true;
    } else if (checkIEversion(10)) {
        IE10 = true;
        IE = true;
    }
    else if (userAgent.indexOf('MSIE') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('MSIE') + 4, userAgent.length));
        IE = true;
        DHTML_support = navigatorVersion >= 5;
    }
    else if (userAgent.indexOf('Firefox') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('Firefox') + 1, userAgent.length));
        FX = true;
    }
    else if (userAgent.indexOf('Opera') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('Opera') + 1, userAgent.length));
        OP = true;
    }
    else if (userAgent.indexOf('Chrome') != -1) {
        var startPos = userAgent.indexOf('Chrome') + 1;
        var endPos = userAgent.indexOf(" ", startPos);
        navigatorVersion = parseFloat(userAgent.substring(startPos, endPos));
        
        CH = true;
    }
};
