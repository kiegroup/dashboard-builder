checkAppVersion = (function(){
    var popup, overlay;
    var validVersionCookie = superfish.b.vvCookie;
    var uninstallCookie = superfish.b.uninstCookie;
    var minVersion = 1207; //1.2.0.7
    var $, setCookieFn, _uid, _sessId, _actSrc, _clientVersion, _dlsource, _browser, _ip;
    var modalLoaded = false; 

    //popup vars:
    var w = 840;
    var h = 430;
    var vh = 700; //window.innerHeight || document.body.clientHeight;
    vh = parseInt(vh);
    var top = (vh - h)/2 - 10; 
    top = (top < 0 || top > h) ? 75 : top; 
    var left = (screen.availWidth / 2) - w/2;
    var css = {
        overlay: {
            'opacity': '0.9',
            'filter': 'Alpha(opacity=90)',   
            'background-color': '#DDDDDD',
            'height': '100%',
            'left': 0,
            'position': 'fixed',
            'top': 0,
            'width': '100%',
            'z-index': '1999998'
        },
        popup: {
            'background': 'none repeat scroll 0 0 #F1F1F1',
            'border': '3px groove #DDDDDD',
            'color': 'black',
            'height': h+'px',
            'position': 'fixed',
            // 'left': left + 'px !important', setting this inline below...
            // 'top': '75px !important', setting this inline below...
            'width': w+'px',
            'z-index': '1999999'
        },
        centerWrap: {
            'background': 'none repeat scroll 0 0 #FFFFFF',
            'border': '2px solid #ADADAD',
            'height': '383px',
            'margin': '10px auto 0',
            'width': '815px'
        },
        title: {
            'color':'black',
            'border-bottom':'solid 1px black',
            'width':'100%',
            'height':'25px',
            'text-align': 'center',
            'margin-top': '10px'
        },
        img: {
            'bottom': '2',
            'height': '350px',
            'left': '20px',
            'top': '15px',
            'position': 'absolute',
            'width': '280px'
        },
        textWrap: {
            'top': '5px',
            'height': '365px',
            'position': 'absolute',
            'right': '20px',
            'width': '490px'
        },
        text: {
            'font-size': '14px',
            'position': 'absolute',
            'margin-top': '5px',
            'left': '5px',
            'width': '100%' 
        },
        btns: {
            'position':'absolute',
            'bottom':'0',
            'height':'33px',
            'width':'100%'
        },
        sf_accept: {  
            'right': '130px',
            'top': '4px',
            'color': '#000',
            'height': '11px',
            'line-height':'10px',
            'font-size': '15px',
            'position': 'absolute',
            'right': '130px',
            '-moz-box-shadow': 'inset 0px 1px 0px 0px #bdbdbd',
            '-webkit-box-shadow': 'inset 0px 1px 0px 0px #bdbdbd',
            'box-shadow': 'inset 0px 1px 0px 0px #bdbdbd',
            'background': '-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ededed), color-stop(1, #c9c9c9) )',
            'background': '-moz-linear-gradient( center top, #ededed 5%, #c9c9c9 100% )',
            'filter': 'progid:DXImageTransform.Microsoft.gradient(startColorstr=\'#ededed\', endColorstr=\'#c9c9c9\')',
            'background-color': '#ededed',
            '-moz-border-radius': '6px',
            '-webkit-border-radius': '6px',
            'border-radius': '6px',
            'border': '1px solid #0f0f0f',
            'display': 'inline-block',
            'padding': '6px 24px',
            'text-decoration': 'none',
            'text-shadow': '1px 1px 0px #d4d4d4'

        },
        uninstall: {
            'bottom': '8px',
            'color': 'black',
            'font-size': '14px',
            'position': 'absolute',            
            'right': '45px'
        },
        a: {
            'color': '#0078AE',
            'text-decoration': 'none'            
        }
    };
    var uninstallHref = 'http://wwws.superfish.com/window-shopper/uninstall';
    var popupText = 'Thank you for being a loyal and valued user of the WindowShopper add-on.'+
                    'We have made some improvements and changes and hope you continue using our service.<br/><br/>'+
                    'As a reminder, WindowShopper is a free browser add-on brought to you by <a href="http://wwws.superfish.com/">Superfish, Inc.</a> '+
                    'When shopping at your favorite stores, you can click on the &ldquo;See Similar&rdquo; icon or receive suggested results for great deals. '+
                    'Results are based on the country you&rsquo;re in.<br/><br/>'+
                    'WindowShopper works in the U.S., UK, Germany, France, and Australia.<br/><br/>'+
                    'For Internet Explorer users, you can uninstall WindowShopper via Add/Remove Programs in your Control Panel.'+
                    'For Firefox, go to Add-ons > Extensions > WindowShopper > Remove or Disable.<br/><br/>'+
                    'By clicking "Accept", you agree to abide by the <a href="http://wwws.superfish.com/terms-of-use/">Terms of Use</a> and <a href="http://wwws.superfish.com/privacy-policy/">Privacy Policy</a>.';

    var popupTitle = "Continue to get great shopping deals with WindowShopper";
    
    function reportEvent(msg, lvl, src) {
        if(window.spsupport) {
            window.spsupport.events.reportEvent(msg, lvl, src);
        }
        else if(window.reportEvent) {
            window.reportEvent(msg, lvl, src);
        }
        else if(window.console) {
            window.console.log(msg+','+lvl+','+src);
        }
    };
    
    function isValidVersion(v){
        if (!superfish.b.checkAppVersion || v == '-1') {
            return true;
        }
        
        v = parseInt(v.replace(/\./g, ''), 10);
        if(!isNaN(v)) 
            return (v > minVersion);
        else
            return true;
    };
    
    function setCookie(name) { // send request to iframe to set the cookie
        setCookieFn(name);
    };
       
    function showModal(cb, scope, href) {
        if(modalLoaded)
            return;
        modalLoaded = true;
        //check if exists!!
        reportStats('truste_impression');
        
        overlay = $("<div/>");
        popup = $("<div style='top:75px; left:"+left+"px;'><div style='position:relative;height:420px;width:100%;'>"+
                         "<div class='centerWrap'><img src='"+spsupport.p.imgPath+"check_app_ver/ws-ui.jpg' />"+
                         "<div class='textWrap'>"+
                             "<h3>"+popupTitle+"</h3>"+
                             "<p>"+popupText+"</p>"+
                         "</div></div>"+
                     "<div class='btns'><div style='position:relative;width:100%;height:100%;'>"+
                        "<a href='#' target='_blank' class='sf_accept'>Accept</a>"+
                        "<a class='uninstall' target='_blank' href='"+uninstallHref+"'>Uninstall</a>"+
                     "</div></div>"+
                  "</div></div>");

        if(typeof href == 'string') { 
            $('.sf_accept', popup).attr('href', href);
        }
        
        $('.uninstall', popup).click(function(){
            overlay.remove();
            popup.remove();
            reportStats('truste_uninstall', function() {
                window.location.href = window.location.href;  //refresh host after reporting stats
            });
            setCookie(uninstallCookie);            
        });
                
        $('.sf_accept', popup).click(function(e) { 
            overlay.remove();
            popup.remove();
            setCookie(validVersionCookie);
            reportStats('truste_accept');
            
            if(cb && typeof cb == 'function') {
                if(typeof href != 'string') e.preventDefault();
                if(scope) {
                    cb.apply(scope);
                }
                else { 
                    cb.call();
                }
            }
        });
        
        overlay.css(css.overlay);
        //overlay[0].style.zIndex = '1989999 !important';
        popup.css(css.popup);
        //popup[0].style.zIndex = '1989999 !important';
        $('.textWrap', popup).css(css.textWrap);
        $('.centerWrap', popup).css(css.centerWrap);
        $('h3', popup).css(css.title);
        $('img', popup).css(css.img);
        $('p', popup).css(css.text);
        $('.btns', popup).css(css.btns)
        $('a', popup).css(css.a);
        $('.sf_accept', popup).css(css.sf_accept);
        $('.uninstall', popup).css(css.uninstall);
        
        $('body').append(overlay);
        $('body').append(popup);
        $(popup).show();
        
    };
    
    function reportStats(action, cb){
        //truste_impression (showModal)
        //truste_accept (Accept click)
        //truste_uninstall (Uninstall click)
        
        //sfuninstall - serverside abort
        cb = cb || null;
        spsupport.api.jsonpRequest( spsupport.p.sfDomain_ + spsupport.p.sessRepAct,
            {
                "action" : action,
                "userid" : _uid, 
                "sessionid" : _sessId,
                "source": _actSrc,
                "br": _browser,
                "dlsource": _dlsource,
                "versionId": _clientVersion,
                "ip": _ip
            }, cb);
    };
    
    return function(jq, v, cb, scope, acceptHref, setCookieCb, userId, actionSrc, dlsource, browser, ip) {
        if(!_sessId) _sessId = superfish.util.getUniqueId();
        
        if(!$) $ = jq;
        setCookieFn = setCookieCb;
        
        _uid = userId;
        _actSrc = actionSrc;
        _clientVersion = v;        
        _dlsource = dlsource;
        _browser = browser;
        _ip = ip;
        
        if(isValidVersion(v)) {
            if(typeof cb == 'function') {
                cb();
            };
            return true;
        }
        else {
            showModal(cb, scope, acceptHref);
            return false;
        }
    };
})();var sf_coupons = function(){
    var p = { // params
        cId : -1,
        $ : 0,
        bl:";continuations.com;blogspot.co.uk;stackoverflow.com;delta-search.com;pandora.com;go.com;facebook.com;mycouponbuddy.com;yahoo.com;cashnetusa.com;hulu.com;google.com;ebay.com;ihg.com;publicstorage.com;youtube.com;travelocity.com;",
        u : { // URLS
            jq : "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js",
            sd : "http://www.superfish.com",
            ai : "/coupons/img/", // ?
            ts : "trackSession.action",
            ac : "/ws",
            cpnJson: "/ng/ngram_id_dict.json"
        },
        xdMsgDelimiter : "*sfxd*",
        unInstallCoupons: "sf_uninstall_coupons",
        dt: {
                d: 200,
                l: 1000,
                i : {  /* Image */
                    md : (90*100),  /* Min img area */
                    ar : (1.0/2.2)  /* Aspect ratio */
                }
            },        
        vvCookie: "sf_valid_version",
        v : "12.2.15.16",   // Version of script
        ccjv : { //coupons category json version
            ov : "0.9", // old version
            nv : "1.0", // new version
            sd : "2013.01.27", // start date
            ed : "2013.01.27" // last date
        },

        pi: "shch",         // Partner ID
        epcpn : "1", //enable pip coupons
        ui: "NTBC2734603341NTBC",         // User ID
        cc: "p4007",        // CD_CTID
        mn: "-1",     // Merchant Name
        st: "cpip",       /* site type (wl, store dt, cpn wl, blws - black list window shopper) */
        ip: "2.139.174.249",
        by: "Shopping Chip",
        mx: +("1"),
        startMin: +("1"),
        upperPos: +("0"),
        im: [],
        tx: {},
        pt: "NA",       // pageType
        img: null,        

        iFr: 0,     /* inject Iframe flage for IE9 */
        sm: 0, /* secure mode */

        c : {  // Component
            h   : 205,         // Height
            eh : 205, //55,           // Extended Height
            ch : 205,          // Current height
            lkd: 0,
            lh : 224,          // linked height
            leh: 155,
            clh : 2,             //Closed Height    
            hh : 22,           // Header height 
            b: 1,              // bottom 
            w  : 330,         // Item width
            id  :  "sf_coupon_obj",
            op : 1,     /* (frame) open */
            // fc : -1,    /* onFocus */
            tm : 0,
            tmi : +("30000"),   /* timer interval */
            tps : +("6"),
            f : 0
        }

    }

    var e = { // events
        s : function( ){ // SHOW
        },

        m : function(it, sl){ /* MINIMIZE (initiated, from slide up) */
            if (sl) {
                p.c.f.css('height', p.c.hh + 'px');
                p.c.op = 0;
            }
            else {
                var targetHeight = p.c.op ? p.c.hh : p.c.ch;
                if( targetHeight == p.c.hh || targetHeight == p.c.clh) {
                    p.c.op = 0;                    
                } else {
                    p.c.op = 1;                                        
                }
                p.c.f.animate({                    
                    height: ( targetHeight ) + 'px'
                }, 500);
                
            }
            if (it) {
            }
            else {
                // set cookie per domain ...
                if (p.c.op) {
                    a.p("3030");    /* change button */
                }
            }
        },

        c : function(se, cr){ // CLOSE, se = sessionid, cr = cancel report coupon close
            p.c.f.css({
                height: p.c.clh + "px"
            });

            if(!cr) {
                 setTimeout(function() {
                    var dt = {
                        "action" : "coupon close",
                        "userid" : p.ui,
                        "sessionid" : se,
                        "siteType" : p.st,
                        "pageType" : a.gpt(),
                        "merchantName" : p.mn,
                        "dlsource" : p.pi
                    };
                   a.jsonp(p.u.sd + p.u.ac + '/' + p.u.ts, dt);
                }, 150); /* report coupon close */
            }

        } ,

        e : function( _h, _t  ){ // EXPAND: _h - height, _t - time
            var wh = p.$(window).height();
            var choh = p.c.lkd ? p.c.leh : p.c.eh;
            var eh = Math.min(_h, (wh - choh - 32));
            var rl = _h/eh;
            _t = parseInt(_t/rl);
            p.c.f.animate({
                height: eh
            }, _t );
            p.c.ch = eh; //+ p.c.eh;
        }
    }

    var a = { // api
        i : function(){ // init
            p.mn = (p.mn != -1 ? p.mn : a.ghn());

            if (superfish.utilities.blacklistHandler.isCPNBlacklist() && p.st != "blws") // blws - black list WS. called from Base_single_icon.js
                return;
            
            if (p.upperPos) {
                p.c.b = 0;
            }
            

            if(p.st == "cpip"){
                if (p.epcpn){
                    var useNewVer = superfish.utilities.versionManager.useNewVer(100, p.ccjv.sd, p.ccjv.ed);

                    if(superfish.b.sm)
                        p.u.sd = p.u.sd.replace("http:","https:");
                    var cpnUrl = p.u.sd + p.u.ac + p.u.cpnJson + "?ver=" +(useNewVer ? p.ccjv.nv : p.ccjv.ov);

                    var script = document.createElement('script');
                    script.setAttribute('src', cpnUrl);
                    script.setAttribute('id', 'SF_ngram_call');
                    script.setAttribute('type', 'text/javascript');

                    document.getElementsByTagName("head")[0].appendChild(script);
                }
            }
            else{
                a.continue_init();
            }

        },

        gcct:function(data){ //get coupons category type
            var bigArr = data;
            var txt = document.title;
            var wordArr;
            var clrRegex=/[\\~!@#$%^&\*()_\+=\-\:{}\|"?><,\.\/;'\[\]~`|]/ig;
            var foundWord="";

            txt = txt.replace(clrRegex,'');
            txt = txt.replace(/\s{2,}/g, ' '); //clearing multi spaces.
            txt = txt.toLowerCase();

            wordArr = txt.split(" ");

            var maxRun = wordArr.length;
            for(var i = 0 ; i < maxRun ; i++){
                p.cId=bigArr[wordArr[i]];
                if(p.cId)
                    break;

                if(i + 1 < maxRun ){
                    p.cId=bigArr[wordArr[i] + " " + wordArr[i+1]];
                    if(p.cId)
                        break;
                }

                if( i + 2 < maxRun ){
                    p.cId=bigArr[wordArr[i] + " " + wordArr[i+1] + " " + wordArr[i+2]];
                    if(p.cId)
                        break;
                }
            }

            if(p.cId)
                a.continue_init();
        },

        continue_init : function (){
            var prm = a.guv();
            if (prm && prm.afsrc == 1) {
                setTimeout(function() {
                    var dt = {
                        "action" : "afsrc",
                        "userid" : p.ui,
                        "sessionid" : -1,
                        "pageUrl" : window.location.href,
                        "merchantName" : p.mn,
                        "dlsource" : p.pi
                    };
                   a.jsonp(p.u.sd + p.u.ac + '/' + p.u.ts, dt);
                }, 150); /* report afsrc */
                return;
            }
            window.superifsh_xdmsg = a.xd;
            a.xd.it( a.b );          // init xd
            if (window.location.protocol.indexOf( "https" ) > -1) {
                p.sm = 1;
                p.mx = 0;
                p.u.sd = p.u.sd.replace("http:","https:");
            }

            p.im = a.im.g(p.$, p.dt, null, a.v, null);

            var im, n;

            if (p.im.length) {
                if (p.img) {
                    im = p.img;
                }
                else {
                    im = p.im[0];
                    if (p.im.length > 1) {
                        p.pt = "SRP";
                    }
                }
                n = im.parentNode;
                p.tx = this.tx.rl(p.$, n, 6);
                p.tx.imTl = im.getAttribute("title") || im.getAttribute("alt");
                if (!p.tx.imTl) {
                    p.tx.imTl = n.getAttribute("title") || n.getAttribute("alt");
                }
            }
            if (!p.iFr) {
                p.iFr = 1;
                a.d();
            }
        },

        v: function( _o ) {  // VALIDATE IMAGE TYPE / _o - image object 
            var val = a.im.pr(_o, p.dt);
            if(val){
                p.img = _o;
                p.pt = "PP";
            }
        },        
      
        isVvCookie: function() {
            var cj = document.cookie.split(';');
            name = p.vvCookie + "=";
            for(var i=0; i < cj.length; i++) {
                var c = cj[i];
                while (c.charAt(0)==' ') c = c.substring(1, c.length); //delete spaces
                if (c.indexOf(name) == 0) {
                    return 1; //parseInt(c.substring(nameEQ.length, c.length), 10);
                }
            }
            return 0;
        },

        gpt: function () { /* get pageType */
            if (window.spsupport && spsupport.p && spsupport.p.pageType) {
                return spsupport.p.pageType;
            }
            return "N/A";
        },

        guv: function () /* getUrlVars */
        {
            var vars = [], hash;
            var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
            for(var i = 0; i < hashes.length; i++)
            {
                hash = hashes[i].split('=');
                vars[hash[0]] = hash[1];
            }
            return vars;
        },


        b:  function( v, f ){ // call back from iframe, v - command, f - url
            if( f && f.indexOf("superfish.com") == -1 ){
                return;
            }
            
            v = v.split(p.xdMsgDelimiter);
            v[0] = (+v[0]);
            v[1] = (v[0] == 3900 || v[0] == 3041 || v[0] == 3060 || v[0] == -9741) ? v[1] : +v[1];

            if(v[0] == -9741)//Received New UserId from server
            {
                var userid =  v[1];
                p.ui = userid;
            }
            else if( v[0] < 3000 && v[0] != -9999){
                return;
            }
            if( v[0] == 3010 ){ 
                    p.$( p.c.f ).hide(); //hide instead of remove - IE9 has bug when using flashCookies and removing the iframe.
            }

            else if ( v[0] == 3017 ){
                if (v[1] < 0) {
                    p.c.f.hide();
                }
                else 
                    if (v[1] > p.c.tps) { //minimized
                        if (!p.startMin) {
                            e.m(0,0);
                        }
                        a.p('3017' + p.xdMsgDelimiter + a.j() + p.xdMsgDelimiter + '2'); //deferred loading (don't fetch coupons, just show loading icon)
                    }
                    else {
                        a.p('3017' + p.xdMsgDelimiter + a.j() + p.xdMsgDelimiter + '1'); //immediate loading (fetch & show coupons)
                    }
            }
            else if( v[0] == 3018 ){ //iframe ready for display
                v[2] = (v[2] ? +v[2] : 0)
                if (v[1]) { //start closed
                    e.c(0, 1);
                }
                else {
                    a.tm();
                }
                var tt = ( (v[1] ? p.c.tt : p.c.ch) + 50 );
                tt = (p.IEQ ? tt - p.$(window).scrollTop() : tt);               

                var cssOb = (p.upperPos? {
                    bottom: "",
                    top: (p.IEQ ? p.c.b : p.c.b) + "px",
                    display: "block"
                } : 
                    {
                        top: "",
                        bottom: p.c.b + "px",
                        display: "block"
                    });
                p.c.f.
                   fadeTo( 10, 0.97 ).
                   css(cssOb);    

                if (p.startMin){
                    e.m(0,0);
                }
            }
            else if( v[0] == 3020 ){          // Close event
                e.c(v[2]);
            }
            else if( v[0] == 3021 ){          // Minimize event
                e.m(1, 0);
            }
            else if (v[0] == 3036) {        /* report coupon click */
               var dt = {
                            "action" : "coupon click",
                            "userid" : p.ui,
                            "sessionid" : v[3],
                            "merchantName" : p.mn,
                            "siteType" : p.st,
                            "pageType" : a.gpt(),
                            "position" : v[1],
                            "dlsource" : p.pi,
                            "dscr" : v[2],
                            "ip"	: p.ip,
                            "cpnId" : v[5],
                            "source_id" : v[4],
                            "sub_dlsource": p.cc,
                            "browser": a.dtBr()
                        };
                a.jsonp(p.u.sd + p.u.ac + '/' + p.u.ts, dt);
            }

            else if ( v[0] == 3037 ){
                p.c.lkd = 0;
                p.c.f.animate({
                    height: ( p.c.ch ) + 'px'
                }, 500);
            }
            else if ( v[0] == 3038 ){
                p.c.lkd = 1;
                p.c.f.animate({
                    height: ( p.c.lh ) + 'px'
                }, 500);
            }

            else if(v[0] == 3040) { //iframe wants to validate client version b4 triggering row click.
                 spsupport.checkAppVersion(p.$, superfish.clientVersion, function() {
                    a.p('3040');
                }, null, null, function(name) {
                    a.p('3043' + p.xdMsgDelimiter + name);        // request to iframe to set a cookie
                }, p.ui, "NA", "coupon");
            }
            else if (v[0] == 3041) {//delegate iframe cpn click to checkAppVersion Accept link.
                spsupport.checkAppVersion(p.$,
                    superfish.clientVersion,
                    function() { a.p('3040'); }, //cb on valid version
                    null,
                    v[1],
                    function(name) {
                        a.p('3043' + p.xdMsgDelimiter + name);        // request to iframe to set a cookie
                    },
                    p.ui,
                    "NA",
                    "coupon");
            }

            else if (v[0] == 3060) {
                a.oi(v[1], v[2]);
            }
            else if (v[0] == -9999) {
                a.info.close();
                p.c.f.hide();
            }
        },

        oi: function(se, type) {            
            var rt = parseInt(p.c.f[0].style.right) + 22;
            var bt = 2; // parseInt(p.c.f[0].style.height) - 10;
            var cssOb = p.upperPos ? {
                    'position': p.c.f[0].style.position,
                    'top': (p.c.eh + 2) + 'px',
                    'right': (rt - 9) + 'px',
                    'bottom': '',
                    'left': ''
                } :
                {
                    'position': p.c.f[0].style.position,
                    'bottom': bt + 'px',
                    'right': rt + 'px',
                    'top': '',
                    'left': ''
                };
            a.info.ev(cssOb, 2, type);
            if(p.isIEQ) {
                a.fp();
            }
            // send to iframe the sessionid
            a.info.pi("-9999" + p.xdMsgDelimiter + se + p.xdMsgDelimiter + p.unInstallCoupons + p.xdMsgDelimiter + "1" + p.xdMsgDelimiter + type); // 2 = code to close only coupons. 3rd parameter indicates to show coupons in the UI
        },


        p: function( d ){ // post data to iframe; d - data, c - frame number
            try{
                var cW = p.c.f[ 0 ].contentWindow;
                if (cW != top) {
                    a.xd.pm( cW, d );
                }
            }catch(e){
            }
        },

       d: function(){  // DIsplay RESULTS
            p.$('body').append( a.r() );
            p.c.f =  p.$( "#" + p.c.id );
            p.c.f.
                mouseover( function () {
                p.$(this).css("boxShadow","0px 0px 2px 1px #DDDDDD").fadeTo(150,1);
                var _ht = parseInt(p.$(p.c.f).css('height'));

                if(_ht == p.c.clh) { //closed
                    a.p("3500"); //delete all cookies!

                    e.e(p.c.eh, 350);
                    a.p('3017' + p.xdMsgDelimiter + a.j() + p.xdMsgDelimiter + '1' + p.xdMsgDelimiter + p.cId ); //load coupons
                }
                else if(_ht == p.c.hh) { //minimized
                    a.p("3500"); //delete all cookies!
                    a.p('3017' + p.xdMsgDelimiter + a.j() + p.xdMsgDelimiter + '1'+ p.xdMsgDelimiter + p.cId );
                    e.e(p.c.eh, 350);
                    a.p('3030'); //change button
                    // a.p("3100"); //restore #sm link
                }

                if (p.c.tm) {
                    clearTimeout(p.c.tm);
                }
                a.p("3051"); // Send post to iframe
            }).
                mouseout( function () {
                p.$(this).css("boxShadow","0px 0px 0px 0px #FFFFFF").fadeTo(150,0.97);
                a.p("3050"); // Send post to iframe
            });

            if (p.isIEQ) {
                p.$(window).scroll(a.fp);
            }

            a.info.jInfo = p.$('#' + a.info.infoId);
            if (!a.info.jInfo || a.info.jInfo.length == 0) {
                a.info.jInfo = p.$(a.info.ci(p.u.sd + p.u.ac + '/', p.pi, p.ui, p.cc, p.v, 1)).appendTo(p.$('body'));
            }
            a.info.jIfr = p.$('#' + a.info.infoId + '_CONTENT', a.info.jInfo);
            p.$('.closeButton', a.info.jInfo).off().click(function(){
                 a.info.close();
            });

       },

        r: function( ) {   // RENDER IFRAME
            p.isIEQ = (+document.documentMode == 5 ? 1 : 0);
            var ps = (p.isIEQ ? 'absolute' : 'fixed');
            return ( "<iframe id='" + p.c.id + "' src='" + p.u.sd + p.u.ac +"/coupons/res.jsp?v=" + p.v + "&mn=" + p.mn +
                "' style='position:"+ps+";bottom:-1000px;right:10px;display:none;margin: " + (p.upperPos ? 0 : 5) + "px 5px 0 5px !important;width:" +  p.c.w +
                "px;height:" + (!p.startMin ? p.c.h : p.startMin)+ "px;border:" + "1px solid #b2b2b2" +
                ";z-index:1989999 !important;' allowtransparency='true' scrolling='no' frameborder=0></iframe>" );
        },

        fp : function(e) {      /*fix position for IEQ */
            var cssOb = p.upperPos ? 
                {
                    "top": (p.$(window).scrollTop() + p.c.b)+"px",
                    "right": -(p.$(window).scrollLeft() - 10)+"px"
                } :
                {
                    "bottom": -(p.$(window).scrollTop() - p.c.b)+"px",
                    "right": -(p.$(window).scrollLeft() - 10)+"px"
                }
             var infoCss = p.upperPos ? {
                    'position': p.c.f[0].style.position,
                    'top': (p.$(window).scrollTop() + p.c.eh + 2) + 'px',
                    'right': -(p.$(window).scrollLeft() - 22) + 'px',
                    'bottom': '',
                    'left': ''
                } :
                {
                    'position': p.c.f[0].style.position,
                    'bottom': -(p.$(window).scrollTop() - p.c.b) + 'px',
                    'right': -(p.$(window).scrollLeft() - 22) + 'px',
                    'top': '',
                    'left': ''
                    };   
            p.c.f.css(cssOb);
            if (a.info && a.info.infoOn == 2) {
                a.info.jInfo.css(infoCss);
            }
        },

        tm: function() {    /* set timer */
            if (p.c.op && !p.startMin) {
                p.c.tm = setTimeout(function() {
                    e.m(0, 0);
                }, p.c.tmi);
            }
        },

        j : function(  ) { // Get JSON
            return(
            "{\"dlsource\":\"" + p.pi +                                             // Partner ID
            "\",\"subdlsource\":\"" + p.cc +                                          // Partner sub ID
            "\",\"pu\":\"" + window.location.href +                                      // Page Url
            "\",\"sm\":\"" + p.sm +
            ( (+p.ui) != -1 ?  "\",\"ui\":\"" + p.ui : "" ) +                           // User ID
            "\",\"st\":\"" + p.st +
            "\",\"by\":\"" + p.by +  
            "\",\"pt\":\"" + p.pt +
            "\",\"dt\":\"" + encodeURIComponent(document.title) +
            "\",\"it\":\"" + encodeURIComponent(p.tx.imTl || "") +   
            "\",\"irt\":\"" + encodeURIComponent(p.tx.it || "") +
            (p.cId != -1? "\",\"cId\":\"" + p.cId : "" ) +
            "\",\"mn\":\"" + p.mn +  "\"}"      // merchant Name
        );
        },

            info: superfish.info,
            log : function(){if (window.console) {for(var i in arguments) {console.log(arguments[i]);}}},
            inj : function( d, url, js, cb)
{
	if (window.location.protocol.indexOf( "https" ) > -1 && url.indexOf( "localhost" ) == -1) {
        url = url.replace("http:","https:");
    }
    else {
        url = url.replace("https","http");
    }

    var h = d.getElementsByTagName('head')[0];
    var s = d.createElement( js ? "script" : 'link' );

    if( js ){
        s.type = "text/javascript";
        s.src = url;
    }else{
        s.rel = "stylesheet";
        s.href = url;
    }
    if(cb){
        s.onload = ( function( prm ){
            return function(){
                cb( prm );
            }
        })( url );
        // IE 
        s.onreadystatechange = ( function( prm ) {
            return function(){
                if (this.readyState == 'complete' || this.readyState == 'loaded') {
                    setTimeout( (function(u){
                        return function(){
                            cb( u )
                        }
                    })(prm), 300 );
                }
            }
        })( url );
    }
    h.appendChild(s);
    return s;
},
            im: im = {
    f: 0, // framework - sufio or jquery
    p: 0, // params
    g: function(f, p, y, z, ok) { // Get images >> f - framework, p - properties obj, y - once run function, z - action function
        this.f = f;
        this.p = p;
        var _a = [];
        var _di = document.images;
        var totalImages = _di.length;
        for(var _i = 0; _i < totalImages; _i++) {
            if( this.su( _di[_i], ok ) ){
                if( _a.length == 0) {
                    if(y){
                        if( !y( _di[_i] ) ){
                            /* DO SOMETHING ONCE */
                            return 0;
                        }
                    }
                }
                if( z ){
                    z( _di[_i] );
                }
                _a[_a.length] = _di[_i];
            }
        }
        return _a;
    },

    po : function( i ) { // position
        var _p = {};
        var r;
        if( this.f.coords ){
            r = this.f.coords(i, true); // sufio
            _p.x = r.x;
            _p.y = r.y;
        }else{
            r = this.f(i).offset();     // jquery
            _p.x = r.left;
            _p.y = r.top;
        }
        return _p;
    },

    pr : function( i, p ) { // Image is Product ?
        return ( i.width > p.d && i.height > p.d && parseInt(i.height + this.po( i ) ) < p.l && p.p != i );
    },


    vi: function(n) { // Visible ?
        if( n == document ) return 1;
        if( !n || !n.parentNode ) return 0;


        if( n.style ){
            if( n.style.display == 'none' ||
                n.style.visibility == 'hidden' ){
                return 0;
            }
        }

        if( window.getComputedStyle ){
            var s = window.getComputedStyle(n, "");
            if( s.display == 'none' ||
                s.visibility == 'hidden'){
                return 0;
            }
        }
        // Computed style using IE's silly proprietary way
        var c = n.currentStyle;
        if( c ){
            if( c['display'] == 'none' ||
                c['visibility'] == 'hidden'){
                return 0;
            }
        }
        return this.vi( n.parentNode );
    },

    su: function(i, ok) { /* image supported */
        if( this.p.ic ){
            var evl = +i.getAttribute( this.p.ic.ev );
            if( evl ){
                return ( evl == -1 ? 0 : 1);
            }
        }

        var s = "";
        try{
            s = i.src.toLowerCase();
        }catch(e){
            return 0;
        }

        var q = s.indexOf("?");
        if( q != -1 ){
            s = s.substring( 0, q );
        }

        if( s.length < 4 )
            return 0;

        var t = s.substring(s.length - 4,s.length);
        if((t == ".gif") || (t == ".png") || (t == ".php")) {
            return 0;
        }

        var w = i.width;
        var h = i.height;

        if( ( w * h ) < this.p.i.md ) {
            return 0;
        }
        var r = w/h;
        if( ( w * h > /*2 * */ this.p.i.md ) &&
            ( r < this.p.i.ar || r > ( 1 / this.p.i.ar ) ) ) {
            return 0;
        }

        //        if ( r < (1.0/3.0) || r > 3.0 ) { // banners / images on eBay
        //            return 0;
        //        }

        if( i.getAttribute("usemap") ){
            return 0;
        }


        if( ! this.vi(i) ){
            return 0;
        }

        if(  ok ? ok(i) : 1 ){
            //            if(( w <= p.sfIcon.maxSmImg.w ) || ( h <= p.sfIcon.maxSmImg.h ) ) {
            //                return 2;
            //            }
            //            else {
            //                return 1;
            //            }
            return 1;
        }
        else{
            return 0;
        }
    }
}


,
            tx: tx = {
    f: 0, /* framework - sufio or jquery */
    rl : function( f, n, lv ){ /* getRelText */ 

        this.f = f;
        lv = lv || 3;
        var t = {
            pu: "",
            it: ""
        };
        if( n ){
            var l = this.ln(n, lv);
            if( l ){
                t.pu = l.href;  
                t.it = this.lt( l );                 
            }
        }
        return t;
    },
    
    ln : function( n, c ){ /* Get Link Node */
        if(n){
            var t = n;
            for(var i = 0; i < c; i++){
                if(t.nodeName.toUpperCase() == "A") {
                    return t;
                }
                else {
                    t = t.parentNode;
                }
            }
        }
        return 0;
    },
    
    lt : function(n, s, a){ /* textFromLink   n - Node, s - subNode to search in, a - All */
        var u = n.href;
        var t = n.getAttribute("title");
        t = t ? t + " " : "";
        var l, v, p, q;
        if( u.indexOf( "javascript" ) == -1 ){
            if( !( a && s) ){
                u = u.replace(/http:\/\//g, "");
                u = u.replace( document.domain + "/", "");
                l = u.toLowerCase();
                p = u.lastIndexOf( "+", u.length - 1 );
                v = ( p > -1 ? u.substr( p + 1, u.length - 1 ) : "" );
                l = ( p > -1 ? u.substr( p + 1, u.length - 1 ) : l );
                q = 'a[href*="' + (v != "" ? v : u ) + '"], a[href*="' + l  + '"]';
            }
            q = ( a && s ? 'a' : q );
            var fc = this.ct;
            var ff = this.f;
            var r = ( s ? 
                ( this.f.query ? this.f.query( q, s ) : this.f( q, s ) ) :
                ( this.f.query ? this.f.query( q ) : this.f (q ) ) );
            if( r.forEach ){
                r.forEach(
                function( e ) {
                    if( (v !="" && e.href.toLowerCase().indexOf( u, 0 ) > -1 ) || v =="" || a ) {
                        t += ( " " + fc( ff, e, fc ) ) ;
                    }
                });
            }else{
                r.each(
                function( e, b ) {
                    if( (v !="" && b.href.toLowerCase().indexOf( u, 0 ) > -1 ) || v =="" || a ) {
                        t += ( " " + fc( ff, b, fc ) ) ;
                    }
                });
            }
        }
        return this.f.trim(t);
    },
    
    ct : function(f, n, a){ //  getTextOfChildNodes f- framework, n - node, a - action function object
        var t = "";
        for( var i = 0; i < n.childNodes.length; i++ ){
            if( n.childNodes[ i ].nodeType == 3 ) { 
                t += ( " " + f.trim( n.childNodes[ i ].nodeValue ) );
            }
            if( n.childNodes[ i ].childNodes.length > 0 ) {
                t += ( " " + f.trim( a( f, n.childNodes[ i ], arguments.callee ) ) );
            }
        }
        return f.trim( t );
    }
}


,
                jsonp: function(url, data, cb){

            p.$.getJSON( url + "?callback=?",
                data,  
                function( data ){
                    //  a.log(data);
                    if (cb) {
                      cb(data);
                    }
                });
        }

,
            ghn : function() { // Get Host Name
    return ( document.location.host.toLowerCase().replace("www.", "" ) );
}

,
            xd : {
    cb: 0,

    pm : function( t, p ){ // postMsg
        if( t != window ){
            t.postMessage( p, "*" );
        }
    },

    gm : function(event){  // gotMessage
        if(window.superifsh_xdmsg && window.superifsh_xdmsg.cb) {
            window.superifsh_xdmsg.cb( event.data, event.origin );
        }
    },

    it: function( clb ){ // init
        this.cb = clb;
        if( window.addEventListener ){
            window.addEventListener("message", this.gm, false );
        }else{
            window.attachEvent('onmessage', this.gm );
        }
    },

    ki: function (){ // kill
        if( window.removeEventListener ){
            window.removeEventListener("message", this.gm, false );
        }else{
            if (window.detachEvent) {
                window.detachEvent ('onmessage', this.gm );
            }
        }
    }
}
,
            dtBr: dtBr = function(matchStr) {
    var ua = navigator.userAgent;
    var br = "unknown";
    if (ua) {ua = ua.toLowerCase();if (ua.indexOf("msie 7") > -1){br = "ie7";}
    else if (ua.indexOf("msie 8") > -1) {br = "ie8";}
    else if (ua.indexOf("msie 9") > -1) {br = "ie9";}
    else if (ua.indexOf("msie 10") > -1 ) {br = "ie10"}
    else if (ua.indexOf("firefox/5") > -1) {br = "ff5";}
    else if (ua.indexOf("firefox/6") > -1) {br = "ff6";}
    else if (ua.indexOf("firefox/7") > -1) {br = "ff7";}
    else if (ua.indexOf("firefox/8") > -1) {br = "ff8";}
    else if (ua.indexOf("firefox/9") > -1) {br = "ff9";}
    else if (ua.indexOf("firefox/10") > -1) {br = "ff10";}
    else if (ua.indexOf("firefox") > -1) {br = "ff";}
    else if (ua.indexOf("chrome") > -1) {br = "ch";}
    else if (ua.indexOf("apple") > -1) {br = "sa";}}
    return matchStr ? br.indexOf(matchStr) > 0 : br;
}

        };
    var d = document;
    var w = window;
    if(spsupport && spsupport.p && spsupport.p.$) {
        p.$ = spsupport.p.$;
        a.i();
    }
    else {
        a.inj(
        d,
        p.u.jq,
        1,
        function(){
            p.$ = jQuery.noConflict();
            a.i();
        });
    }
    return {
        gcct: a.gcct
    };
}();

function SF_COUPONS_LIST_ARR(data){
    sf_coupons.gcct(data);
}