(function() {

	if (!Function.prototype.bind)
	{
		Function.prototype.bind = function(oThis)
		{
			if (typeof this !== "function")
			{
				// closest thing possible to the ECMAScript 5 internal IsCallable function
				throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");
			}

			var aArgs = Array.prototype.slice.call(arguments, 1),
				fToBind = this,
				fNOP = function () {},
				fBound = function ()
				{
					return fToBind.apply(this instanceof fNOP ? this : oThis || window, aArgs.concat(Array.prototype.slice.call(arguments)));
				};

			fNOP.prototype = this.prototype;
			fBound.prototype = new fNOP();

			return fBound;
		};
	}

	superfish.Template =
	{
		debug: false,
		blocks: {},
		vars: {},
		pub: {},
		blocksPattern: /\[([A-Z0-9_\.]+?)\](.*?)\[\/\1\]/gim,

		preProcessors:
		{
			cleaning: [/<\!--[\s\S]*?-->|\r|\t|\n|\s{2,}/gm, ''],
			tokens: [/#\{(.+?)\}/gi, "<!--|-->$1<!--|-->"],
			ifTag: [/<if\s+(.+?)>/gi, "<!--|-->function(){if($1) {return [''<!--|-->"],
			elseIfTag: [/<\/if>\s*?<else if\s+(.+?)>/gi, "<!--|-->''].join('')} else if ($1) {return [''<!--|-->"],
			elseTag: [/<\/if>\s*?<else>/gi, "<!--|-->''].join('')} else {return [''<!--|-->"],
			ifElseCloseTag: [/<\/(if|else)>/gi, "<!--|-->''].join('')}}.call(this)<!--|-->"],
			foreachTag: [/<foreach\s+(.+?)(?:\s+in\s+(.+?)(?:\s+as\s+(.+?))?)?>/gi, function(match, p1, p2, p3)
			{
				p3 = p3 || 't._item'; // iterrated item variable name
				p2 = p2 || p1; // iterated structure variable name
				p1 = p1 || 't._index'; // iterrator index name

				return "<!--|-->function(struct){var t=struct, _t="+p2+", _r=[]; if (_t && _t.length){for(var i=0, l=_t.length; i<l; i++){t=_t[i]; t._parent=struct; "+p1+" = i; "+p3+" = t; _r.push([''<!--|-->";
			}],
			foreachCloseTag: [/<\/foreach>/gi, "<!--|-->''].join(''));} return _r.join('')}}.call(this, t)<!--|-->"],
			forTag: [/<for\s+(.+?)>/gi, "<!--|-->function(){var _r=[]; for($1){_r.push([''<!--|-->"],
			forCloseTag: [/<\/for>/gi, "<!--|-->''].join(''));} return _r.join('')}.call(this)<!--|-->"],
			scriptTag: [/<script(?:\s.+?)?>(.*?)<\/script>/gim, "<!--|-->function(){$1}.call(this)<!--|-->"],
			linkedBlockTag: [/<@(.+?)(?:\s+(.+?))?\s?\/>/gi, function(match, p1, p2)
			{
				return (p2) ? "<!--|-->this._rd('"+p1+"', "+p2+")<!--|-->" : "<!--|-->this._rd('"+p1+"', t."+p1+"||t)<!--|-->";
			}]
		},

		initialize: function(rawData)
		{
			this.pub =
			{
				_fv: this.findVar,
				_rd: this.render,
				vars: this.vars,
				blocks: this.blocks
			};

			if (rawData)
			{
				this.add(rawData);
			}
		},

		parse: function(rawData)
		{
			var parsedBlocks = {};
			var foundBlock = null;
			var preProcessor, rawBlock, parsedBlock, particle;

			for (var name in this.preProcessors)
			{
				if (this.preProcessors.hasOwnProperty(name))
				{
					preProcessor = this.preProcessors[name];
					rawData = rawData.replace(preProcessor[0], preProcessor[1]);
				}
			}

			do
			{
				foundBlock = this.blocksPattern.exec(rawData);

				if (foundBlock)
				{
					rawBlock = foundBlock[2]; // Block's raw text
					parsedBlock = [];

					rawBlock = rawBlock.split('<!--|-->');

					for (var i=0, l=rawBlock.length; i<l; i++)
					{
						particle = rawBlock[i];

						if (particle)
						{
							if (i%2 === 1) //token
							{
								parsedBlock.push(particle.replace(/\$([A-Z0-9_\.]+)/gi, "t.$1").replace(/\@([A-Z0-9_\.]+)/gi, "this._fv(t, '$1')").replace(/\^([A-Z0-9_\.]+)/gi, "this.vars.$1"));
							}
							else
							{

								parsedBlock.push("'"+particle.replace(/'/g, "\\'")+"'");
							}
						}
					}

					parsedBlocks[foundBlock[1]] = 't._parent = this.vars; return ['+parsedBlock.join(',')+'].join("");';
				}
			}
			while (foundBlock);

			return parsedBlocks;
		},

		compile: function(blocks)
		{
			for(var blockName in blocks)
			{
				if (blocks.hasOwnProperty(blockName))
				{
					this.blocks[blockName] = new Function('t', blocks[blockName]);
				}
			}
		},

		render: function(name, data)
		{
			data = data || {};

			if (this.blocks[name])
			{
				return this.blocks[name].call(this.pub, data);
			}
		},

		add: function(rawData)
		{
			if (typeof rawData === 'string')
			{
				this.compile(this.parse(rawData));
			}
			else
			{
				this.compile(rawData);
			}
		},

		findVar: function(hayStack, needle)
		{
			if (hayStack[needle])
			{
				return hayStack[needle];
			}
			else if (hayStack._parent)
			{
				return this._fv(hayStack._parent, needle);
			}
			else
			{
				return '';
			}
		}
	};


	spsupport.p = {
    sfDomain:       superfish.b.pluginDomain,
    sfDomain_:      superfish.b.pluginDomain,
    imgPath:        (superfish.b.sm ? superfish.b.pluginDomain.replace("http:","https:") : superfish.b.pluginDomain.replace("https","http")) + "images/",
    cdnUrl:         superfish.b.cdnUrl,
    appVersion:     superfish.b.appVersion,
    clientVersion:  superfish.b.clientVersion,
    site:           superfish.b.pluginDomain,
    sessRepAct:     "trackSession.action",
    isIE:           0,
    isIEQ:          +document.documentMode == 5 ? 1 : 0 ,
    applTb:         0,
    applTbVal:      30,
    siteDomain: "",
    nil: {
        pPos: 0
    },
    presFt: '',
    sfIcon: {
        jBtns: 0,
        nl:     0,
        maxSmImg: {
            w: 88,
            h: 70
        },
        ic:     0,
        evl:    'sfimgevt',
        icons:  [],
        big: {
            w: 95,
            h: 25
        },
        small: {
            w: 65,
            h: 25
        },
        an: 0,
        imPos: 0,
        timer: 0,
        prog: {
            time: 1000,
            node: 0,
            color: '#398AFD',
            colorRgba: 'rgba(57, 138, 253, 0.3)',
            opac : '0.3',
            e: 0,   /* end */
            w: [93, 63],
            h: 23
        }
    },
    sfsrp: {
      ic: 0,
      ind: -1,
      prop: [
      {
          w: 95,
          h: 25
      },
      {
          w: 120,
          h: 41
      },
      {
          w: 89,
          h: 23
      },
      {
          w: 53,
          h: 53
      },
      {
          w: 135,
          h: 23
      }
    ]
    },
    temp: 0,

    onFocus: -1,
    psuHdrHeight: 22,
    psuRestHeight: 26,
    oopsTm: 0,
    iconTm: 0,
    dlsource: superfish.b.dlsource,
    w3iAFS: superfish.b.w3iAFS,
    CD_CTID: superfish.b.CD_CTID,
    userid: superfish.b.userid,
    statsReporter: superfish.b.statsReporter,
    minImageArea: ( 60 * 60 ),
    aspectRatio: ( 1.0/2.0 ),
    supportedSite: 0,
    ifLoaded: 0,
    ifExLoading: 0,
    itemsNum: 1,
    tlsNum: 1,
    statSent: 0,
    blSite: 0,

    pipFlow: 0,
    icons: 0,
    jqueryFrame: 0,
    partner: superfish.b.partnerCustomUI ? superfish.b.images + "/" : "",

    prodPage: {
        s: 0,   // sent - first request
        i: 0,   // images count
        p: 0,   // product image
        e: 0,   // end of slideup session
        d: 210, // dimension of image
        l: 1000, // line - in px from top
        reset: function(){
            this.s = 0;
            this.i = 0;
            this.p = 0;
            this.e = 0;
        }
    },

    SRP: {
        p: [],    // pic
        i: 0,    // images count
        c: 0,   /* counter of tries */
        ind: 0, /* index of current image */
        lim: 0, /* limit of requests on SRP */
        reset: function(){
            this.p = [];
            this.i = 0;
            this.c = 0;
            this.ind = 0;
        }
    },
    bh: {
      x1: 0,
      y1: 0,
      x2: 0,
      y2: 0,
      busy: 0,
      tm: 0
    },

    pageType: "NA",

    iJq:    0,   // "inject jquery flag"
    jJqcb:  0,   // jquery callback
    iSf:    0,   // "inject sf flag"
    iTpc:   0,   // "inject top ppc flag"
    iCpn:   0,   // "inject coupons flag"
    iInln:  0,   // "inject inline flag"
    iCharmSavings: 0,

    before: -1   // Close before
};

spsupport.api = {
    jsonpRequest: function(url, data, successFunc, errorFunc, callBack, postCB){
        try{
            if( callBack == null ){
                var date = new Date();
                callBack = "superfishfunc" + date.getTime();
            }
            window[callBack] = function(json) {
                if(successFunc != null)
                    successFunc(json);
            };
            sufio.io.script.get({
                url: url + ( url.indexOf("?") > -1 ? "&" : "?" ) + "callback=" + callBack,
                content: data,
                load: function(response, ioArgs) {
                    window[callBack]  = null;
                    if( !spsupport.p.isIE ){
                        if( postCB) {
                            setTimeout(function() {
                                postCB();
                            }, 50);
                        }
                    }
                },
                error: function(response, ioArgs) {
                    window[callBack]  = null;
                    if(errorFunc != null)
                        errorFunc( response, ioArgs);

                    if( !spsupport.p.isIE ){
                        if( postCB) {
                            setTimeout(function() {
                                postCB();
                            }, 50);
                        }
                    }
                }

            });
        }
        catch(ex){
        }
    },

    sTime: function( p ){
        if( p == 0 ){
            this.sTB = new Date().getTime();
            this.sT = 0;
        }else if(p == 1){
            this.sT = new Date().getTime() - this.sTB;
        }else{
            return ( spsupport.p.before == 1 && this.sT == 0 ? new Date().getTime() - this.sTB : this.sT );
        }
    },

    getDomain: function(){
        /*if (spsupport.p.siteDomain) {
            return spsupport.p.siteDomain;
        }
        var dD = document.location.host;
        var dP = dD.split(".");
        var len = dP.length;
        if ( len > 2 ){
            var co = ( dP[ len - 2 ] == "co" || dP[ len - 2 ] == "com" ? 1 : 0 );
            dD = ( co ? dP[ len - 3 ] + "." : "" ) + dP[ len - 2 ] + "." + dP[ len - 1 ];
        }*/

	    var domainName = spsupport.p.siteDomain || superfish.utilities.extractDomainName(document.location.host)

        spsupport.p.siteDomain = domainName;
        return domainName;
    },

	validDomain: function(){
        try{
            var d = document;
            if( d == null || d.domain == null ||
                d == undefined || d.domain == undefined || d.domain == ""
                || d.location == "about:blank" || d.location == "about:Tabs"
                || d.location.toString().indexOf( "superfish.com/congratulation.jsp" ) > -1  ){
                return false;
            }else{
                return (/^([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,5}$/).test( d.domain );
            }
        }catch(e){
            return false;
        }
    },

    init: function(){
        var sp = spsupport.p;
        if( window.sufio )
            return;

        if(!superfish.b.ignoreWL && !spsupport.api.validDomain() ) {
            return;
        }

        this.dojoReady = 0;
        sp.cdnUrl = (superfish.b.sm ? sp.cdnUrl.replace("http:","https:") : sp.cdnUrl);

        if ( ! top.djConfig ){
            djConfig = {};
        }
        djConfig.afterOnLoad = true;
        djConfig.baseScriptUri = sp.cdnUrl;
        djConfig.useXDomain = true;
        djConfig.scopeMap = [ ["dojo", "sufio"], ["dijit", "sufiw"], ["dojox", "sufix"] ];
        djConfig.require = ["dojo.io.script" ,"dojo._base.html", "dojo.window"];
        djConfig.modulePaths =  {
            "dojo": sp.cdnUrl + "dojo",
            "dijit": sp.cdnUrl + "dijit",
            "dojox": sp.cdnUrl + "dojox"
        };

        superfish.b.inj(window.document, sp.cdnUrl + "dojo/dojo.xd.js",
            1,
            function(){
                sufio.addOnLoad(function(){
                    spsupport.api.dojoLoaded();
                });
            });
    },

    // sends request on search results page
    sSrp: function() {
        var sp = spsupport.p;
        var sa = spsupport.api;
        var im = sp.SRP.p[sp.SRP.ind];
        sp.pageType = (spsupport.whiteStage.rv || spsupport.p.textOnly ? "PP" : "SRP");
        if (sp.SRP.c < sp.SRP.lim && sp.SRP.ind < sp.SRP.p.length) { // && im.getAttribute("nosureq") != "1") {
            sp.SRP.ind++;
            if (im.getAttribute("nosureq") != "1" && sa.validateInimg(im)) {
                sp.prodPage.p = im;
                sa.puPSearch(1, im);
                sp.SRP.c++;
            }
            else {
                sa.sSrp();
            }
        }
    },

    useUserId: function() {
        if(superfish.b.injectCharmSavings) {
            spsupport.api.injectCharmSavings();
        }
    },

    useUserData: function(data) {
        var userData = null;
        try{
           userData = spsupport.p.$.parseJSON(data);
        }
        catch(ex){
           userData = null;
        }

        if (superfish.b.injectMarimedia) {
            if (typeof(spsupport.p.siteType) === 'undefined' && userData && userData.marimedia && userData.marimedia < 3) {
                superfish.thirdPart.userCountry = userData.uc? userData.uc.toLowerCase() : "--";
                superfish.thirdPart.marimediaShown = userData.marimedia - 1;
                superfish.thirdPart.inject("marimedia");
            }
        }
    },

    gotMessage: function(param, from)
    {
        if(from && from.indexOf(superfish.b.sfDomain) == -1 )
        {
            return;
        }

        if (param)
        {
            param = param + "";
            var prep = param.split(superfish.b.xdMsgDelimiter);
        }
        var sp = spsupport.p;
        var i, su = 0;

        if (prep && prep.length)
        {
            var fromPsu = ( prep.length > 5 ? 1 : 0);

	        if (fromPsu)
            {
                if (sp.prodPage.e)
                {
                    return;
                }
            }

            param = ( +prep[ 0 ] );

            if (param > 3000)
            {
                return;
            }

	        var sfu = superfish.util;
	        var sa = spsupport.api;

	        if(param == 101) // Sys down
	        {
	                sfu.sysDown();
	        }
	        else
	        {
		        if(param == -9741) // Received new UserId from server
		        {
		            var userid =  prep[1];

		            spsupport.p.userid = userid;
		            superfish.b.userid = userid;
		            sp.userid=userid;
		            sa.useUserId();
		        }
	            if(param == -9999) // Reload page
	            {
	                window.location.reload();
	            }
	            if(param == -1234)
	            {
	                sp.presFt = prep[1];
	            }
	            if(param == -3333) // User data
	            {
	                sa.useUserData(prep[1]);
	            }
	            else // Initialize

	                if(param == -7890) // Iframe loaded
	                {
	                    sp.ifLoaded = 1;
	                    sp.uninst = +prep[2];
						sp.uninstSu = +prep[3];
						if (sp.uninstSu == 1) {
							superfish.b.slideup = 0;
							superfish.b.slideupSrp = 0;
							superfish.b.slideupAndInimg = 0;
						}
		                if (sp.uninst)
		                {
	                       spsupport.api.killIcons();
	                       return;
	                    }

		                spsupport.p.$('#sfButtons div').css({display: 'inline-block'}); // Enable the activation areas for "find similar" buttons

	                    sp.vv = +prep[1]; //is valid version?

	                    //check valid version:
	                    if(!sp.vv)
	                    {
		                    spsupport.checkAppVersion(
	                            spsupport.p.$,
	                            superfish.clientVersion,
	                            function(){  }, //on valid cb
	                            this, //scope
	                            null, //acceptHref
	                            function(name) { //setcookie cb
		                            superfish.util.sendRequest("{\"cmd\": 9, \"name\": \"" + name + "\" }"); //iframe will set cookie
	                                sp.vv = 1;
	                            },
	                            sp.userid,
	                            "initial", //action source
	                            superfish.b.dlsource,
	                            spsupport.api.dtBr(), //browser
	                            superfish.b.ip
	                        );
	                    }

	                    if( sfu.standByData != 0 )
	                    {
		                    sa.sTime(0);
	                        superfish.utilities.sfWatcher.setState("gotMessage sendRequest");
	                        sfu.sendRequest( sfu.standByData );
	                        sfu.standByData = 0;
	                    }

	                    spsupport.statsREP.sendRequestCallback();
	                    // count site activations
	                    spsupport.statsREP.reportStats(spsupport.statsREP.repMode.awake);

	                }
	                else if(param >= 200 && param < 2000)
	                {
	                    superfish.utilities.sfWatcher.setState("gotMessage param="+param);

	                    // 200 - ( identical only - false, failure )
	                    // 211 - ( identical only - false, identical is not empty, similar is not empty )

		                sp.itemsNum = +prep[1];
                        sp.tlsNum = +prep[2];
                        sfu.updIframeSize(sp.itemsNum, sp.tlsNum, fromPsu);
                        sfu.showContent();
                        sa.sTime(1);

		                if (prep[6] && !superfish.inimg.hasTemplate())
		                {
			                superfish.Template.initialize(prep[6]);
		                }

                        if (!fromPsu) {

	                        if (superfish.inimg)
                            {
	                            superfish.inimg.setReload && superfish.inimg.setReload();

	                            for (i in superfish.inimg.res) {
                                    superfish.inimg.res[i] = 0;
                                }
                                if(spsupport.slideup) {
                                    spsupport.slideup.res = 0;
                                }

                                if (param < 221) {
                                }
                                else {
	                                superfish.inimg.res[prep[3]] = sp.itemsNum;
	                                superfish.inimg.spl && superfish.inimg.spl(prep[3]);
                                    if(spsupport.slideup) {
                                        spsupport.slideup.res = 4;
                                    }
                                }
                            }
                            if (sfu.currImg == sp.sfIcon.ic.img && sp.sfIcon.prog.e > 0) {
                                sp.before = 0;
                                if (param == 222 && (superfish.b.slideup && sp.pageType !='SRP' || superfish.b.slideupSrp && sp.pageType =='SRP')) {
                                    su = 1;
                                }
                                sfu.openPopup(sp.imPos, sp.appVersion, su);
                            }
                        }

                        sp.before = 0;

                        if( param == 200 ){
                            if( !fromPsu) {
                                if (superfish.p.onAir != 2) {
                                    if (sfu.currImg) {
                                        sfu.currImg.setAttribute("sfnoicon", "1");
                                    }

                                    if (! superfish.b.coupons || prep.length <= 2) {
                                        sp.oopsTm = setTimeout(function() {
                                            if(!sp.isIE){
                                                sfu.jPopup().fadeOut(800, function() {
                                                    sfu.closePopup();
                                                });
                                            }
                                            else {
                                                sfu.closePopup();
                                            }
                                        }, 3000 );
                                    }
                                }
                            }
                            else {
                                if( !sp.prodPage.e &&  sp.prodPage.s ) {
                                    if(superfish.b.inimgSrp && sp.prodPage.i == 0) {
                                        sp.prodPage.s = 0;
                                        sa.sSrp();
                                    }
                                    else {
                                        superfish.utilities.sfWatcher.setState("gotMessage param="+param+" no results");
                                    }
                                }
                            }
                            superfish.utilities.sfWatcher.complete("no results retuned");
                        }
                        else if( param > 200 ){
                            if( superfish.b && superfish.b.inimg  && fromPsu){
                                superfish.utilities.sfWatcher.setState("gotMessage param="+param+" reuslts");
                                if( sp.prodPage.s && !sp.prodPage.e /* && superfish.p.onAir == 2 */){
                                    sp.prodPage.e = 1;
                                    superfish.utilities.sfWatcher.setState("gotMessage param="+param+" reuslts -show");
                                    if (superfish.b.inimg && superfish.inimg /*&& (superfish.inimg.itn || superfish.isAuto)*/ ) {
                                        if (superfish.isAuto){
                                            superfish.autoSession = sfu.currentSessionId;
                                            sfu.openPopup(sp.imPos, sp.appVersion, 0);
                                            superfish.util.sendRequest("{\"cmd\": 7 }");
                                            if (superfish.inimg.itn == 0){
                                                superfish.inimg.itn = 1;
                                                }

                                        }
                                        superfish.utilities.sfWatcher.setState("gotMessage param="+param+" reuslts - inimg/slideup");
                                        if (superfish.b.slideup && sp.pageType !='SRP' || superfish.b.slideupSrp && sp.pageType =='SRP' ) {
                                            superfish.utilities.sfWatcher.setState("gotMessage param="+param+" reuslts slideup");
                                            spsupport.slideup.init(prep[3], sfu, spsupport.p, superfish.b, sp.prodPage.p);
                                            var ind = +prep[4];
                                            if (superfish.b.slideupAndInimg && superfish.inimg.itNum[ind] > 0) {
                                                superfish.inimg.init(prep[3], ind, sfu, spsupport.p, superfish.b, sp.prodPage.p);
                                            }
                                        }
                                        else {
                                            superfish.utilities.sfWatcher.setState("gotMessage param="+param+" reuslts image");
                                            superfish.inimg.init(prep[3], +prep[4], sfu, spsupport.p, superfish.b, sp.prodPage.p);
                                        }

                                        sa.fixDivsPos();
                                        if(superfish.b.inimgSrp && sp.prodPage.i == 0) {
                                            sp.prodPage.s = 0;
                                            sp.prodPage.e = 0;
                                            sa.sSrp();
                                        }
                                    }
                                    else if (superfish.b.initPSU) {
                                        superfish.b.initPSU( prep[2] );
                                    }
                                }
                                superfish.util.requestImg();
                            }
                            else if(param == 211){
                                 superfish.utilities.sfWatcher.complete("search popup");
                            }
                        }
                    }
                    // searchget
                    else if (param > 2001) {

                        if (prep[1]) {
                            sp.before = 0;
                            superfish.sg.init(prep[1]);
                            sfu.closePopup();
                        }
                    }

                    else if( param == 20 ){
                        sfu.closePopup();
                    }
                    else if( param == 30 ){
                        superfish.publisher.report(101);
                    }
                else if ( param == 40 ){
                    // Add wakeup flag to product image
                    sp.prodPage.p.sfwakeup = prep[1];
                }
                else if(param == 60) {//trigger custom info action
                    superfish.info.ev();
                }
                else if (param == -3219){ // data response
                    superfish.dataApi.init(superfish.b.pluginDomain, superfish.b.userid, superfish.b.dlsource, spsupport.api.dtBr())
                    superfish.dataApi.setSearchResult(spsupport.p.$.parseJSON(prep[1]));
                    //window.superfishDataCallback(spsupport.p.$.parseJSON(prep[1]));
                }
                else if (param == -3218){ // got user country code.
                    superfish.b.uc = prep[1];
                }
	        }
        }
    },

    dojoLoaded: function()  {
        this.dojoReady = 1;
        var sp = spsupport.p;
        spsupport.p.textOnly = 0;

        if (!spsupport.sites.isBlackList()) {
            if (!spsupport.p.iJq)
            {
               spsupport.p.iJq = 1;

                superfish.b.cdnJQUrl = (superfish.b.sm ? superfish.b.cdnJQUrl.replace("http:","https:") : superfish.b.cdnJQUrl);

               var helper=document.createElement("iframe");

               helper.style.position =  'absolute';
               helper.style.width = '1px';
               helper.style.height = '1px';
               helper.style.top = '0';
               helper.style.left = '0';
               helper.style.visibility = 'hidden';
               document.body.appendChild(helper);

               try {
                    sp.wnd = helper.contentWindow || helper.contentDocument;
                    sp.wnd.document.open();
                    sp.wnd.document.write('<!DOCTYPE html><html><head>');
	                sp.wnd.document.write('<script type="text/javascript">var ga = document.createElement("script"); ga.type = "text/javascript"; ga.src = "'+superfish.b.cdnJQUrl+'"; var s = document.getElementsByTagName("script")[0]; s.parentNode.insertBefore(ga, s); </s'+'cript>');
                    //sp.wnd.document.write('<script type="text/javascript" src="'+superfish.b.cdnJQUrl+'"></s'+'cript>');
                    sp.wnd.document.write('<script type="text/javascript">parent.spsupport.api.onJqLoad(1);</s'+'cript>');
	                sp.wnd.document.write('</head><body></body></html>');
                    sp.wnd.document.close();
                    sp.jqueryFrame = 1;
               }
               catch(e) {
                    sfjq.load({
                        url:      superfish.b.cdnJQUrl,
                        callback: function(){
                            if (!spsupport.p.jJqcb) {
                                spsupport.p.jJqcb = 1;
                                spsupport.p.$ = sfjq.jq;
                                spsupport.api.jQLoaded();
                            }
                        }
                    });

               }
            }
        }
        else { // try loading coupons anyway. be a separate part.
            superfish.utilities.sfWatcher.complete("WS blacklist");
            spsupport.p.blSite = 1;
            spsupport.api.userIdInit();
        }
    },

    onJqLoad: function(c) {
	    var sp = spsupport.p;

	    if (sp.wnd.$) {
	        var jQueryInit;
	        var jDoc = sp.wnd.$(window.document);
	        jQueryInit = sp.wnd.$.fn.init;

	      sp.wnd.$.fn.init = function(arg1, arg2, rootjQuery)
	      {
	          arg2 = arg2 || jDoc || window.document;
	          return new jQueryInit(arg1, arg2, rootjQuery);
	      };

	      sp.wnd.$.expr[":"].regex = function (a, b, c) {
	        var d = c[3].split(","),
	        e = /^(data|css):/,
	        f = {
	            method: d[0].match(e) ? d[0].split(":")[0] : "attr",
	            property: d.shift().replace(e, "")
	        }, g = "ig",
	        h = new RegExp(d.join("").replace(/^\s+|\s+$/g, ""), g);
	        return h.test(sp.wnd.$(a)[f.method](f.property))
	    };
	      sp.$ = sp.wnd.$;

	        spsupport.api.jQLoaded();
	    }
	    else {
		    setTimeout( function(){
			    if (c<50) spsupport.api.onJqLoad(c+1);
		    }, 50+c*10);

	    }
    },


    jQLoaded: function() {
            var sp = spsupport.p;
            sp.isIE = sp.$.browser ? sp.$.browser.msie : 0;
            sp.isFF = sp.$.browser ? sp.$.browser.mozilla : 0;
            sp.isIE7 = sp.isIE  && parseInt(sp.$.browser.version, 10) === 7;
            sp.isIE8 = sp.isIE  && parseInt(sp.$.browser.version, 10) === 8;

            if (!spsupport.sites.isBlackStage()) {
                spsupport.sites.searchget();
              	if (spsupport.whiteStage && spsupport.whiteStage.init) {
                    spsupport.whiteStage.init(spsupport.p.$);
                }
                superfish.utilities.sfWatcher.setState("user init");

                spsupport.api.userIdInit();

                if(sp.isIE && window.spMsiSupport){
                    if( !this.isOlderVersion( '1.2.1.0', sp.clientVersion ) ){
                        spMsiSupport.validateUpdate();
                    }
                    if(sp.isIE7) {
                        sp.isIEQ = 1;
                    }
                }

                if ((spsupport.p.$('#BesttoolbarsChromeToolbar').length || spsupport.p.$('#main-iframe-wrapper').length) && spsupport.api.dtBr() == 'ch') {
                    sp.applTb = 1;
                    applTbVal = spsupport.p.$('#main-iframe-wrapper').height() || spsupport.p.$('#BesttoolbarsChromeToolbar').height();
                }
                if (spsupport.p.$('.bt-btpersonas-toolbar').length && spsupport.api.dtBr() == 'ch') {
                    sp.applTb = 1;
                    applTbVal = spsupport.p.$('.bt-btpersonas-toolbar').height();
                }

                (function(a){
                    function d(b){
                        var c=b||window.event,d=[].slice.call(arguments,1),e=0,f=!0,g=0,h=0;return b=a.event.fix(c),b.type="mousewheel",c.wheelDelta&&(e=c.wheelDelta/120),c.detail&&(e=-c.detail/3),h=e,c.axis!==undefined&&c.axis===c.HORIZONTAL_AXIS&&(h=0,g=-1*e),c.wheelDeltaY!==undefined&&(h=c.wheelDeltaY/120),c.wheelDeltaX!==undefined&&(g=-1*c.wheelDeltaX/120),d.unshift(b,e,g,h),(a.event.dispatch||a.event.handle).apply(this,d)}
                        var b=["DOMMouseScroll","mousewheel"];
                            if(a.event.fixHooks)
                                for(var c=b.length;c;)a.event.fixHooks[b[--c]]=a.event.mouseHooks;a.event.special.mousewheel={setup:function(){
                                    if(this.addEventListener)for(var a=b.length;a;)this.addEventListener(b[--a],d,!1);
                                    else this.onmousewheel=d},teardown:function(){
                                    if(this.removeEventListener)for(var a=b.length;a;)this.removeEventListener(b[--a],d,!1);
                                    else this.onmousewheel=null}},a.fn.extend({
                                mousewheel:function(a){return a?this.bind("mousewheel",a):this.trigger("mousewheel")},
                                unmousewheel:function(a){return this.unbind("mousewheel",a)}})})(spsupport.p.$);

                setTimeout( function(){
                    spsupport.sites.care();
                    spsupport.sites.urlChange();
                }, 1 );

                setTimeout( function(){
                    sp.$(window).unload(function() {
                        if(superfish.p && superfish.p.onAir){
                            superfish.util.bCloseEvent(sp.$("#SF_CloseButton")[0], 2);
                        }
                    });
                }, 2000 );
        }
    },

    userIdInit: function(){
        var sp = spsupport.p;
        var spa = spsupport.api;
        var data = {
            "dlsource":sp.dlsource
        }
        if(sp.w3iAFS != ""){
            data.w3iAFS = sp.w3iAFS;
        }

        if( sp.CD_CTID != "" ){
            data.CD_CTID = sp.CD_CTID;
        }

        if(sp.userid != "" && sp.userid != undefined){
            spa.onUserInitOK({
                userId: sp.userid,
                statsReporter: sp.statsReporter
            });
        }
        else { // widget
            spa.jsonpRequest(
                sp.sfDomain_ + "initUserJsonp.action",
                data,
                spa.onUserInitOK,
                spa.onUserInitFail,
                "superfishInitUserCallbackfunc"
                );
        }
    },

    onUserInitOK: function(obj) {
        var sa = spsupport.api;
        var sp = spsupport.p;

        if(!obj || !obj.userId || (obj.userId == "")){
            sa.onUserInitFail();
        } else{
            sp.userid = obj.userId;
            superfish.utilities.sfWatcher.setUserid(sp.userid);
            sp.statsReporter = obj.statsReporter;

            if (!spsupport.p.blSite) {
//                if (sp.dlsource == "conduit"){
//                    if (superfish.b.testConduit) {
//                        if (superfish.utilities.abTestUser(100, 10)) {
//                            superfish.b.tg = 'tg';
//                        }
//                        else {
//                            superfish.b.testConduit = 0
//                            superfish.b.tg = 'cg';
//                        }
//                    }
//                }

	            //superfish.b.newUI = true;
	            //superfish.inimg = superfish.inimg_new;

	            if (superfish.b.newUI)
                {
	                if (superfish.utilities.abTestUser(100, 50))
	                {
		                superfish.inimg = superfish.inimg_new;
		                superfish.b.tg = 'tg';
	                }
	                else
	                {
		                superfish.b.newUI = false;
		                superfish.b.tg = 'cg';
	                }
                }

	            if (superfish.b.newDomainExtraction)
	            {
		            if (superfish.utilities.abTestUser(100, 50))
		            {
			            superfish.b.tg = 'tg';
		            }
		            else
		            {
			            superfish.b.newDomainExtraction = false;
			            superfish.b.tg = 'cg';
		            }
	            }

	            if (superfish.b.usePHPImages)
                {
	                if (superfish.utilities.abTestUser(100, 50))
	                {
		                superfish.b.tg = 'tg';
	                }
	                else
	                {
		                superfish.b.usePHPImages = false;
		                superfish.b.tg = 'cg';
	                }
                }

                if (spsupport.p.dlsource == "qoksnqgp") {
                    if (superfish.utilities.abTestUser(100, 50)) {
                        superfish.b.tg = 'tg';
                    }
                    else {                        
                        superfish.b.tg = 'cg';
                    }
                }

                if (superfish.b.inimgExploreBecomeSearch) {
                    if (superfish.utilities.abTestUser(100, 50)) {
                        superfish.b.tg = 'tg';
                    }
                    else {
                        superfish.b.inimgExploreBecomeSearch = 0;
                        superfish.b.tg = 'cg';
                    }
                }

                sa.isURISupported( document.location );
            }
            else {
                if(superfish.b.injectCharmSavings && !superfish.b.generateUserId) {
                    spsupport.api.injectCharmSavings();
                }

                spsupport.api.requestCouponsWl();
            }
        }
    },

    isURISupported: function(url){
        var sfa = spsupport.api;
        spsupport.p.merchantName = "";
        var wl_ver = superfish.b.wlVersion;
        if(superfish.utilities)
            wl_ver = (superfish.utilities.versionManager.useNewVer(100,superfish.b.wlStartDate,superfish.b.wlDestDate))? superfish.b.wlVersion: superfish.b.wlOldVersion;
        sfa.jsonpRequest(
            spsupport.p.sfDomain_ + "getSupportedSitesJSON.action?ver=" + wl_ver,
            0,
            sfa.isURISupportedCB,
            sfa.isURISupportedFail,
            "SF_isURISupported");
    },

    injCpn: function(st) {  /* st - site type: 1 - wl, 2 - cwl, 3 - blws*/
        var sp = spsupport.p;
        var st1; // = (st == 1 ? "wl" : "cwl"); // that's the old value;
        switch(st){
            case 1:
                st1="wl";
                break;
            case 2:
                st1="cwl";
                break;
            case 3:
                st1="blws"; // blws - black list window shopper;
                break;
            case 4:
                st1="pip"; // pip open
                break;
            case 5:
                st1="st"; // st - store
                break;
            case 6:
                st1="cpip"; // coupons pip
                break;


            default:
                st1="na";
        }
        sp.iCpn = 1;

        if (st == 4 || st == 5)
            return;

        if (st == 6 && !superfish.b.dlsrcEnableCpnPip)
            return;

        superfish.b.inj(window.document, superfish.b.site + "coupons/get.jsp?pi=" + sp.dlsource + "&psi="+ sp.CD_CTID + "&ui=" + sp.userid + "&st="+ st1 + (superfish.b.CD_CTID ? "&cc="+ superfish.b.CD_CTID : "") + "&v=" + sp.appVersion  /* + "&mn="+spsupport.p.merchantName */, 1);
    },

    injInln: function() {  /* st - site type: 1 - wl, 2 - cwl*/
//        var sp = spsupport.p;
//        sp.iInln = 1;
//        superfish.b.inj(window.document, superfish.b.site + "inline/get.jsp?pi=" + sp.dlsource + "&ui=" + sp.userid + "&v=" + sp.appVersion  /* + "&mn="+spsupport.p.merchantName */, 1);
    },

    prc: function(id, pc) {
        var num = id.charCodeAt(id.length - 1) + id.charCodeAt(id.length - 2) - 96;
        var rg = 100/148; /* range: (122+122)-(48+48) */
        return (num*rg < pc);
    },

    isURISupportedCB: function(obj)
    {
	    spsupport.p.$(document).ready(function() {
		    spsupport.api.onDocumentLoaded(obj);
	    });
    },

	onDocumentLoaded: function(obj)
	{
		superfish.utilities.sfWatcher.setState("isURISupportedCB");

		var sfa = spsupport.api;
		var sp = spsupport.p;
		var sfb = superfish.b;
		var w = spsupport.whiteStage;
		sfb.inln = 0;

		sp.totalItemCount = obj.totalItemCount;
		var domain = sfa.getDomain();

		if (sfb.sm && domain != 'google.com') {
			sfb.icons = 0;
			sfb.inimg = 0;
			sfb.inimgSrp = 0;
			sfb.ignoreWL = 0;
			sfb.stDt = 0;
			sfb.topPpc = 0;
			sfb.rvDt = 0;
			sfb.inImgDt = 0;
		}

		if (sfb.slideup) {
			sfb.inimg = 1;
		}

		if (sfb.slideupSrp) {
			sfb.inimgSrp = 1;
		}

		if (spsupport.txtSr && spsupport.txtSr.dt) {
			spsupport.txtSr.wl = obj;
			if (!spsupport.txtSr.dt.sendLate) {
				spsupport.txtSr.useWl();
			}
		}
		var d = domain.toLowerCase().split('.');
		var sS;
		if (w.bl.indexOf(d[ 0 ] + '.') == -1) {
			sS = obj.supportedSitesMap[domain];
		}
		if (!sS && spsupport.txtSr && spsupport.txtSr.dt) {
			sS = spsupport.txtSr.siteInfo(domain);
		}
		superfish.partner.init();
		superfish.publisher.init();

		if( sS ) {
			sp.supportedSite = 1;
		}
		else {
			if (!sfb.ignoreWL) {
				w.st = (sfb.stDt ? w.isStore() : 0);
			}
			if (sfb.ignoreWL || w.st) {
				sS = sfa.getSiteInfo();

				if (w.st) {
					sp.prodPage.d = 140;
					sp.prodPage.l = 1100;
					superfish.b.inimgSrp = 0;
				}
			}
		}

		if( sS && !sfa.isBLSite( obj )){
			if (sfb.topPpc && !sp.iTpc) {
				sp.iTpc = 1;
				spsupport.sites.topPpc(sS);
			}
			superfish.utilities.sfWatcher.setState("Start store");
			sfa.injectIcons(sS);
		}
		else {
			if(superfish.b.inImgDt) {
				if(w.isProductInPage()){
					superfish.utilities.sfWatcher.setState("start pip");
					sfa.pipDetected();
				}
				else{
					superfish.utilities.sfWatcher.complete("not supported site type");
					if (superfish.b.injectMarimedia) {
						superfish.thirdPart.init("marimedia");
					}
				}

			}

			if( !sp.icons ){
				setTimeout(sfa.saveStatistics, 400);
			}
		}

		if(superfish.b.injectCharmSavings && !superfish.b.generateUserId) {
			spsupport.api.injectCharmSavings();
		}

		sfa.requestCouponsWl();

		var ifSg = (superfish.sg ? superfish.sg.sSite : 0);

		if((sfb.inimgSrp == 0 && sp.pageType !="PP" ) && ifSg == 0)
			superfish.utilities.sfWatcher.complete("no Inimage in SRP");

		if((sfb.inimg == 0 && sp.pageType =="PP") && ifSg == 0)
			superfish.utilities.sfWatcher.complete("no Inimage PP");
	},

    injectCharmSavings: function() {
        if (spsupport.p.iCharmSavings || !spsupport.p.userid) {
            return;
        }
        var protocol = superfish.b.sm ? 'https://' : 'http://';
        var url = protocol + "dj3895b110uri.cloudfront.net/js/sparrow.js?u=" + spsupport.p.userid;
        superfish.b.inj(window.document, url, 1);
        spsupport.p.iCharmSavings = 1;
    },

    requestCouponsWl: function() {
        var sa = spsupport.api;
        var sfb = superfish.b;
        if (!sfb.cpn[0]) {
            return;
        }
        var cpn_ver = superfish.b.cpnVersion;
        if(superfish.utilities)
            cpn_ver = (superfish.utilities.versionManager.useNewVer(100,superfish.b.cpnStartDate,superfish.b.cpnDestDate))? superfish.b.cpnVersion: superfish.b.cpnOldVersion;
        sa.jsonpRequest(
            spsupport.p.sfDomain_ + "getCouponsSupportedSites.action?ver=" + cpn_ver,
            0,
            sa.cpnWlCB,
            sa.cpnWlFail,
            sfb.cpnWLcb);
    },

    pipDetected: function() {
        var sfa = spsupport.api;
        var sfb = superfish.b;
        sfb.icons = 0;
        var sS = sfa.getSiteInfo();
        spsupport.p.pipFlow = 1;
        spsupport.pip.start(sS);
    },

    getRandomInt: function(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    },

    getSiteInfo: function(){
    	 var sS = {};
         sS.imageURLPrefixes = "";
         sS.merchantName = spsupport.p.siteDomain;
         return sS;
    },

    cpnWlCB: function(o) {
        var sp = spsupport.p;

        if (sp.iCpn)
            return;

        var ourHostName = document.location.host.toLowerCase();
        var subsHosts = ourHostName.replace(/[^.]/g, "").length; // how many time there are "."
        var shouldInject = false;

        o.a = "," + o.a + ",";
        for(i=0 ; i < subsHosts ; i++) {
            if(o.a.indexOf(","+ourHostName+",") != -1){
                 shouldInject = true;
            }
            ourHostName = ourHostName.substring(ourHostName.indexOf(".")+1,ourHostName.length);
        }

        if (!shouldInject){
            var domainName = spsupport.p.siteDomain.split(".")[0];
            if (spsupport.whiteStage && spsupport.whiteStage.bl.indexOf(domainName) > -1)
                shouldInject = true;
        }

        if (shouldInject)
            spsupport.api.injCpn(2);
        else
            spsupport.api.cpnInjectRest();

    },

    cpnWlFail: function(o) {
        spsupport.api.cpnInjectRest();
    },

    cpnInjectRest: function (){
        var sfa = spsupport.api;
        var sfb = superfish.b;
        var sp = spsupport.p;
        var w = spsupport.whiteStage;

        if (sfb.cpn[0] && !sp.iCpn){
            var cpnSiteType = -1;

            if (w.st=='pip')
                cpnSiteType = 4;
            else if (w.st == 'st')
                cpnSiteType = 5;
            else
                cpnSiteType = 6;


            sfa.injCpn(cpnSiteType);
        }
    },

    isURISupportedFail: function(obj) {},

    isBLSite: function(obj){
        var isBL = 0;
        if ( obj.blockedSubDomains ){
            for (var s = 0 ; s < obj.blockedSubDomains.length && !isBL ; s++ ){
                var loc = top.location + "";
                if (loc.indexOf(obj.blockedSubDomains[s]) >= 0){
                    isBL = 1;
                }
            }
        }
        return isBL;
    },

    injectIcons: function(sS) {
        spsupport.p.supportedImageURLs = sS.imageURLPrefixes;
        spsupport.p.merchantName = sS.merchantName;
        spsupport.events.reportEvent("inject-icons", "info");
        spsupport.api.siteType();
        spsupport.statsREP.init();
        spsupport.sites.firstTimeRep();
        spsupport.sites.preInject();
        spsupport.api.careIcons(0);
    },

    addSuperfishSupport: function(){
        superfish.b.xdmsg.init(
            spsupport.api.gotMessage,
            ( spsupport.p.isIE7 ? 200 : 0 ) );

        if( !top.superfishMng ){
            top.superfishMng = {};
        }
        if( !top.superfish ){
            top.superfish = {};
        }

        if( !top.superfish.p ){ // params
            top.superfish.p = {
                site: spsupport.p.site,
                totalItemsCount: spsupport.p.totalItemCount,
                cdnUrl: spsupport.p.cdnUrl,
                appVersion: spsupport.p.appVersion
            };
        }

        if( !top.superfish.util && !spsupport.p.iSf){
            spsupport.p.iSf = 1;
            if (superfish.b.newUI) {
                superfish.b.inj(window.document, superfish.b.site + "js/sf_allenby_new.js?version=" + spsupport.p.appVersion, 1);
            }
            else {
	            superfish.b.inj(window.document, superfish.b.site + "js/sf_allenby.js?version=" + spsupport.p.appVersion, 1);
	            //superfish.b.inj(window.document, superfish.b.site + "js/sf_si.js?version=" + spsupport.p.appVersion, 1);
            }
        }else{
            superfish.utilities.sfWatcher.complete("no inimage at all");
        }

    },

    careIcons: function( rep ){
        superfish.utilities.sfWatcher.setState("careIcons");

        var sp = spsupport.p,
            sa = spsupport.api,
            spd = spsupport.domHelper;
        var doc = {elem: sp.$(document)};

        sp.icons = this.startDOMEnumeration();
        if (window.conduitToolbarCB && sp.icons > 0 && spsupport.isShowConduitWinFirstTimeIcons){
            conduitToolbarCB("openPageForFirstTimeIcons");
        }

        if(sp.icons > 0 || spsupport.sites.ph2bi()){
            sa.addSuperfishSupport();

            spd.addOnresizeEvent(function() {
                sa.fixDivsPos();
            });

            spd.addFocusEvent(function() {
                sp.onFocus = 1;
                sa.startDOMEnumeration();
            });

            // check for document resize
            var checkDocResize = function(){
                doc.width = doc.elem.width();
                doc.height = doc.elem.height();
                setTimeout(function(){
                    // check dimensions for change
                    if( doc.height !== doc.elem.height() || doc.width !== doc.elem.width() ){
                        checkDocResize();
                    } else {
                        // start the product grabber
                        sa.startDOMEnumeration();
                    }
                }, 250);
            };

            // monitor all click events in the document
            doc.elem.on('click', function(e){
                // target only a elements
                if( e.target.nodeName.toLowerCase() === 'a' ){
                    checkDocResize();
                }
            });

            spd.addUnloadEvent(sa.unloadEvent);
            sa.vMEvent();

            sp.$(document).ready(function(){
                setTimeout(function(){
                    sa.wRefresh(1300);
                    sa.saveStatistics()
                }, spsupport.sites.gRD() );
            });
        }
        else {
            if(rep == 7){
                spsupport.api.saveStatistics();
            }
            else {
                setTimeout(function(){
                    spsupport.api.careIcons( ++rep );
                }, 1300 + rep * 400) ;
            }
        }
    },

    siteType: function() {
       var sp = spsupport.p, w = spsupport.whiteStage;
       if (!sp.siteType) {
       sp.siteType = (sp.supportedSite ? "wl" :
                    (w.st ? "st" :                    
                    (w.pip ? "pip" :
                    (superfish.b.ignoreWL ? "ign" :
                    "other"))));
       }
    },

    vMEvent: function(){
        try{
            if( window.superfish && window.superfish.util ){
                var pDiv = superfish.util.bubble();
                if( pDiv ){
                    spsupport.domHelper.addEListener( pDiv, spsupport.api.blockDOMSubtreeModified, "DOMSubtreeModified");
                    return;
                }
            }
        }catch(e){}
        setTimeout( "spsupport.api.vMEvent()", 500 );
    },

    puPSearch: function(rep, im){
        if (rep < 101) {
            var sp = spsupport.p;
            var sg = superfish.sg;
            var si = superfish.inimg;
            if(superfish.b.inimg || sg && sg.sSite){
                if( sp.prodPage.s < 2 || (superfish.p && superfish.p.onAir == 1) ){
                    setTimeout(function(){
                        var sfu = superfish.util;
                        if (sfu) {
                            if( sp.prodPage.s < 2 && !sp.prodPage.e){
                                var o = spsupport.api.getItemPos(im);
                                spsupport.p.imPos = o;
                                var ob;
                                var ifSg = (sg ? sg.sSite : 0);
                                var ii = (superfish.b.inimg && si && !ifSg ? si.vi(o.w, o.h) : 0);
                                var physIi = ii;
                                ii = spsupport.api.careIi(ii, 1);
                                var siInd = (si ? si.iiInd : 0);
                                if (si) {
                                    si.itNum[siInd] = Math.min(ii, physIi);
                                }
                                var c1 = 1;
                                ob = spsupport.api.getItemJSON(im);
                                if (superfish.b.slideup) {
                                    ii = Math.max(ii, 4);
                                }

                                if (!ifSg && ii == 0) {
                                    return;
                                }
                                sfu.prepareData(ob, 1, ifSg, c1, ii, siInd);

                                sfu.openPopup(o, sp.appVersion, 1, 1);
                                sfu.lastAIcon.x = o.x;
                                sfu.lastAIcon.y = o.y;
                                sfu.lastAIcon.w = o.w;
                                sfu.lastAIcon.h = o.h;
                                sfu.lastAIcon.img = im;
                                sp.prodPage.s = 2;
                            }
                        }
                        else {
                            setTimeout(function() {
                                spsupport.api.puPSearch(rep+1, im);
                            }, 100);
                        }
                    }, 30);
                }
            }
        }
    },

    onDOMSubtreeModified: function( e ){
        var spa = spsupport.api;
        spa.killIcons();
        if(spa.DOMSubtreeTimer){
            clearTimeout(spa.DOMSubtreeTimer);
        }
        spa.DOMSubtreeTimer = setTimeout("spsupport.api.onDOMSubtreeModifiedTimeout()",1000);
    },
    onDOMSubtreeModifiedTimeout: function(){
        clearTimeout(spsupport.api.DOMSubtreeTimer);
        spsupport.api.startDOMEnumeration();
    },
    blockDOMSubtreeModified: function(e,elName){
        e.stopPropagation();
    },
    createImg: function( src ) {
        var img = new Image();
        img.src = src;
        return img;
    },
    loadIcons: function() {
        var sp = spsupport.p;
        var nm = superfish.b.whiteIcon ? 'wi' : 'si';
        if( sp.sfIcon.icons.length == 0 ){
            for (var i = 0; i < 4; i++) {
                sp.sfIcon.icons[ i ] = spsupport.api.createImg( sp.imgPath + sp.partner + nm + i + ".png?v=" + sp.appVersion );
            }
        }
    },

    setLimits: function(ip, pp) {
        var sp = spsupport.p;
        var y2;
        if (superfish.b.sfsrp) {
            var bw = sp.sfsrp.prop[sp.sfsrp.ind].w;
            var sc = (bw - ip.w)/2;
            sp.bh.x1 = ip.w < bw ? (ip.x - sc) : ip.x;
            sp.bh.y1 = ip.y;
            sp.bh.x2 = ip.w < bw  ? (ip.x + ip.w + sc) : (ip.x+ip.w);
            sp.bh.y2 = ip.y+ ip.h + sp.sfsrp.prop[sp.sfsrp.ind].h/2;
        }
        else {
            y2 = ip.y+ip.h;
            if (sp.applTb) {
                y2 += sp.applTbVal;
            }
            sp.bh.x1 = ip.x;
            sp.bh.y1 = ip.y;
            sp.bh.x2 = ip.x+ip.w;
            sp.bh.y2 = y2;
        }
    },

    bindEvents: function() {
        var sp = spsupport.p;
        if(!spsupport.p.iiPlOn) {
            spsupport.api.unbindEvents(spsupport.api.hoverMove, spsupport.api.hoverMove);
           sp.$(document).on({
                "mousemove": spsupport.api.hoverMove,
                "mousewheel": spsupport.api.hoverMove
           });
       }
    },

    inside: function(e) {
        var sp = spsupport.p;
        var t = e.pageX > sp.bh.x1 && e.pageX < sp.bh.x2 && e.pageY > sp.bh.y1 && e.pageY < sp.bh.y2;
         return (t);
    },

    hoverMove : function(e){
        var sp = spsupport.p,
            sa = spsupport.api;
        if (!sa.inside(e)) {
            setTimeout(function() {
                if (!sp.bh.busy) {
                    sp.bh.busy = 1;
                    sp.sfIcon.ic.style.top = -200 + "px";
                    if (sp.sfIcon.nl) {sp.sfIcon.nl.show();}
                    sa.unbindEvents();
                }
            }, 10);
        }
    },

    unbindEvents: function(func1, func2) {
        var sp = spsupport.p;
        sp.$(document).off({
            "mousemove": func1,
            "mousewheel": func2
        });
    },

    killIcons: function() {
        superfish.publisher.imgs = [];
        var bs = this.sfButtons();
        if( bs ){
            document.body.removeChild( bs );
            }
        if (spsupport.p.sfIcon && spsupport.p.sfIcon.ic) {
            spsupport.p.sfIcon.ic.style.top = -200 + "px";
        }
    },

    fixDivsPos: function() {
        var sp = spsupport.p;
        var bc = sp.$('#sfButtons');
        sp.$('div', bc).each(function() {
            if( this.img ){
                var p = spsupport.api.getImagePosition(this.img);
                this.style.left = p.x + 'px';
                this.style.top = p.y + 'px';
            }
        });
        spsupport.api.fixIiPos();
    },

    fixIiPos: function() {
        if (superfish.inimg && superfish.inimg.fixPosition()) {
            superfish.inimg.fixPosition();
        }
//        var ii, ps, ts, ls;
//        if (superfish.inimg && superfish.inimg.ii) {
//            for (var i in superfish.inimg.ii) {
//                ii = superfish.inimg.ii[i];
//                if (ii && ii.img) {
//                    ps = spsupport.api.getImagePosition(ii.img);
//                    ts = ps.y + ps.h;
//                    ls = ps.x;
//                    if (superfish.inimg.pad) {
//                        ls = ps.x - superfish.inimg.pad[i];
//                    }
//                    if(!superfish.inimg.iiExtands) ts -= (superfish.inimg.h + superfish.inimg.hAdd);
//                    if (ls != 0 && !isNaN(ls)) {
//                        ii.style.left = ls + 'px';
//                        ii.style.top = ts + 'px';
//                    }
//                }
//            }
//        }
    },

    startDOMEnumeration: function(){
	    var bodyElement = document.getElementsByTagName('body')[0];
	    var sfa = spsupport.api;
        var ss = spsupport.sites;
        var sp = spsupport.p;
        var sb = superfish.b;

	    if (!bodyElement)
	    {
		    return;
	    }

        if (sp.uninst == 1 || spsupport.p.pipFlow) {
            if(sp.uninst == 1)
                superfish.utilities.sfWatcher.complete("startDOMEnumeration - uninstall");
            return 0;
        }
        var found = 0;
        sfa.killIcons();
        sp.SRP.p = [];
        if( ss.validRefState() ){
            if (sb.icons) {
                var imSpan = sp.$('<span/>', {id: 'sfButtons'}).appendTo(bodyElement)[0];
            }

            var iA = ss.gVI();
            var images = ( iA ? iA : document.images );
            var imgType = 0;
            var noSu;


            for( var i=0, l=images.length; i < l; i++ ){
                imgType = sfa.isImageSupported( images[i] );
//                if (spsupport.whiteStage.rv && imgType) {
//                    imgType = (sb.rvi(images[i]) ? imgType : 0);
//                }
                if( imgType ){
                    if (sb.icons) {
                        if (! found) {
                            if (sb.sfsrp) {
                                if (!sp.sfIcon.ic) {
                                    sfa.addSfsrpIcon();
                                }
                            }
                            else {
                                sfa.loadIcons();
                                sfa.addSFProgressBar( imSpan );
                                if (!sp.sfIcon.ic) {
                                    sfa.addSFIcon(bodyElement);
                                }
                            }
                            sfa.addAn();
                        }
                        sfa.addSFDiv(imSpan, images[i]);
                        if (superfish.b.multipleIcons) {
                            sfa.addSFButton(imSpan, images[i]);
                        }
                    }
                    noSu = images[i].getAttribute("nosureq");
                        if (noSu != "1") {
                            if(!sb.multiImg){
                                var imgPos = sfa.getImagePosition(images[i]);
                                var res = sfa.validateSU(images[i], parseInt( imgPos.y + images[i].height - 45 ));
                                if( !res &&  !sp.prodPage.i /* && !sp.SRP.i */ ){
                                    sp.SRP.p[sp.SRP.p.length] = images[i];
                                    sp.SRP.i ++;
                                }
                            }

                            superfish.publisher.pushImg(images[i]);
                        }
                    found++;
                }
            }

            // enter srp
            if((superfish.b.inimgSrp) && spsupport.sites.su() && (!sp.prodPage.p && !sp.prodPage.s || spsupport.sites.isSrp()) && sp.SRP.p.length ){
                if( superfish.sg ){
                    superfish.sg.sSite = 0;
                }
                sp.SRP.lim = superfish.b.inimgSrp ?
                    superfish.b.inimgSrp
                    : 0;

                sp.SRP.lim = Math.min(sp.SRP.lim, sp.SRP.p.length);

                sfa.sSrp();
            }
            if(found > 0){
                if (sb.icons) {
                    sp.sfIcon.nl = sp.$("div", imSpan);
                }

                setTimeout(function(){
                    if( !spsupport.p.statSent ){
                        sfa.saveStatistics();
                        spsupport.p.statSent = 1;
                    }
                }, 700);
            }
            else {
                if (spsupport.txtSr && spsupport.txtSr.dt) {
                    ss.txtSrch();
                }
                else if (sp.siteType == 'st') {
                    sfa.pipDetected();
                }else{
                    superfish.utilities.sfWatcher.complete("no good images");
                }
            }
        }
        return found;
    },

    imageSupported: function( src ){
        if( src.indexOf( "amazon.com" ) > -1  && src.indexOf( "videos" ) > -1 ){
            return 0;
        }
        try{
            var sIS = spsupport.p.supportedImageURLs;

            if( sIS.length == 0 )
                return 1;
            for( var i = 0; i < sIS.length; i++ ){
                if( src.indexOf( sIS[ i ] ) > -1 ){
                    return 1;
                }
            }
        }catch(ex){
            return 0;
        }
        return 0;
    },

    isImageSupported: function(img){
        var sp = spsupport.p;
        var evl = +img.getAttribute(sp.sfIcon.evl);

        if(evl == -1) {
            return 0;
        }
        if(evl == 1) {
            return 1;
        }

        var src = "";
        try{
            src = img.src.toLowerCase();
        }catch(e){
            return 0;
        }

        var iHS = src.indexOf("?");
        if( iHS != -1 ){
            src = src.substring( 0, iHS );
        }

        if( src.length < 4 ){
            return 0;
        }

        var ext = src.substring(src.length - 4, src.length);

	    if (superfish.b.usePHPImages) // A/B Test PHP images
	    {
		    if(ext == ".gif" || ext == ".png")
		    {
			    return 0;
		    }
	    }
	    else  // ORIGINAL (cg)
	    {
	        if(ext == ".gif" || ext == ".png" || ext == ".php")
	        {
	            return 0;
	        }
	    }

        var iW = img.width;
        var iH = img.height;

        if( ( iW * iH ) < sp.minImageArea ) {
            return 0;
        }
        if(!(spsupport.whiteStage.rv || spsupport.whiteStage.pip)){
        	var ratio = iW/iH;
            if( ( iW * iH > 2 * sp.minImageArea ) &&
                ( ratio < sp.aspectRatio || ratio > ( 2.5 ) ) ) {
                return 0;
            }
        }

        if (img.getAttribute("usemap")) {
            return 0;
        }

        if( !this.imageSupported( img.src ) ) {
            return 0;
        }

        // check if item is visible
        if( !spsupport.api.isVisible( img ) ){
            return 0;
        }

        // check if object is not hiding in a scroll list
        if( !spsupport.api.isViewable( img ) ){
            return 0;
        }

        var imgPos = spsupport.api.getImagePosition(img);
        if (imgPos.x < 0 || imgPos.y < 10) {
            return 0;
        }

        if( spsupport.sites.imgSupported( img ) ){
            if(( iW <= sp.sfIcon.maxSmImg.w ) || ( iH <= sp.sfIcon.maxSmImg.h ) ){
                return 2;
            }
            else {
                return 1;
            }
        }
        else{
            return 0;
        }
    },

    wRefresh : function( del ){
        setTimeout( function() {
            spsupport.api.startDOMEnumeration();
        }, del * 2 );
    },

    // checks if the element is not hiding in an overflow:hidden parent
    isViewable: function ( obj ){

        var p = spsupport.api.overflowParent( obj.parentNode );

        if( p ){
            var r = spsupport.api.getItemPos( obj ),
                pPos = spsupport.api.getItemPos( p );

            // check the elements position relative to it's overflowing parent
            if ( r.x >= pPos.x + pPos.w || r.y >= pPos.y + pPos.h ){
                return 0;
            } else if ( r.x + r.w <= pPos.x || r.y + r.h <= pPos.y ) {
                return 0;
            }
        }

        return 1;
    },

    // returns the hiding parent of the element
    overflowParent: function( obj ){
        if( !obj || !obj.parentNode || obj == document ) return 0;

        if( obj.offsetHeight < obj.scrollHeight || obj.offsetWidth < obj.scrollWidth ){
            if( spsupport.p.$(obj).css('overflow') === 'hidden' && spsupport.p.$(obj.parentNode).css('overflow') === 'visible' ){
                return obj;
            }
        }
        return spsupport.api.overflowParent( obj.parentNode );
    },

    // checks if the element is visible
    isVisible: function( obj ){

        var width = obj.offsetWidth,
            height = obj.offsetHeight;

        return !(
            ( width === 0 && height === 0 )
            || ( !spsupport.p.$.support.reliableHiddenOffsets && ((obj.style && obj.style.display) || spsupport.p.$.css( obj, "display" )) === "none")
            || ((obj.style && obj.style.visibility || spsupport.p.$.css( obj, "visibility" )) === "hidden")
        );
    },

    sfIPath: function( iType ){ /* 1 - large, 2 - small */
        var sp = spsupport.p;
        var icn = ( iType == 2  ?  2  :  0 );
        return( {
            r : sp.sfIcon.icons[icn].src,
            o : sp.sfIcon.icons[icn + 1].src
        } );
    },

    getSrUrl: function(nI, su) {
        var sa = this;
        var sfu = superfish.util;
        var sp = spsupport.p;
        var act = superfish.b.lp ? "findByUrlLanding.action" : "findByUrlSfsrp.action";
        var flp = 0;  /* from landing page */
        var pu = window.location.href;
        var osi;    /* original session id */
        if (pu.indexOf(act) > -1) {
            flp = 1;
            var ar = pu.split("&");
            for (var i=1; i<ar.length; i++) {
                if (ar[i].indexOf("sessionid") > -1) {
                    osi = ar[i].split("=")[1];
                    break;
                }
            }

        }
        var o = sa.getItemJSON(nI.img);
        var stt = spsupport.p.siteType;
        var ac = superfish.p.site + act + "?";
        ac = ac +
            "userid=" + decodeURIComponent(o.userid) +
            "&sessionid=" + sfu.getUniqueId() +
            "&dlsource=" + sp.dlsource +
            ( sp.CD_CTID != "" ? "&CD_CTID=" + sp.CD_CTID : "" ) +
            "&merchantName=" + o.merchantName +
            "&imageURL=" + o.imageURL.replace(/&/g, superfish.b.urlDel) +
            "&imageTitle=" + o.imageTitle +
            "&imageRelatedText=" + o.imageRelatedText + (spsupport.whiteStage && spsupport.whiteStage.matchedBrand ? spsupport.whiteStage.matchedBrand : "") +
            "&documentTitle=" + o.documentTitle  +
            "&productUrl=" + o.productUrl +
            (o.pr ? "&pr=" +  o.pr : "") +
            "&slideUp=" + su +
            "&ii=0&identical=0" +
            "&pageType=" + sp.pageType +
            "&siteType=" + stt +
            (spsupport.p.isIE7 || flp ? "" : "&pageUrl=" + pu) +
            "&ip=" + superfish.b.ip +
            (osi ? "&origSessionId=" + osi : "") +
            ((superfish.b.tg && superfish.b.tg != "") ? "&tg=" + superfish.b.tg : "") +
            "&br=" + sa.dtBr();
        return ac;
    },

    osr: function(nI, su) {   /* open search results */
        window.location.href = spsupport.api.getSrUrl(nI, su);
    },

    sfsrp: function(nI, su) {   /* open search results */
        var ac = spsupport.api.getSrUrl(nI, su);
        return window.open(ac);
    },

    goSend: function(ev, nI, anim) {
        var sfu = superfish.util;
        var sa = this;
        var sp = spsupport.p;
        var img = nI.img;        
        if(sfu) {
            sp.imPos = sa.getItemPos(img);
            if (ev == 1 || ev == 2) {
                if (sfu.currImg != img) {
                    sfu.currImg = img;

                    if (superfish.p.onAir) {
                        sfu.closePopup();
                    }
                    sfu.prepareData(sa.getItemJSON(img), 0, 0, 0, 0);
                    nI.sent = 1;
                    clearTimeout(sp.iconTm);
                    clearTimeout(sp.oopsTm);
                    sp.prodPage.e = 1;
                }
            }
            if (ev == 1 || ev == 3) {
                superfish.utilities.sfWatcher.reset();
                superfish.utilities.sfWatcher.setState("manual Search "+ ev);
                nI.src = spsupport.api.sfIPath(nI.type).r;
                sa.resetPBar(anim, nI);
//                this.shOverlay();
                sp.sfIcon.prog.e = 2;
                if (sfu.currImg == img && sp.before == 0) {
                    sfu.updIframeSize(sp.itemsNum, sp.tlsNum, 0);
//                    sfu.lastAIcon.x = sp.imPos.x;
//                    sfu.lastAIcon.y = sp.imPos.y;
//                    sfu.lastAIcon.w = sp.imPos.w;
//                    sfu.lastAIcon.h = sp.imPos.h;
//                    sfu.lastAIcon.img = img;
                    sfu.openPopup(sp.imPos, sp.appVersion, 0);
                    superfish.utilities.sfWatcher.complete("search same item");
                }
            }
        }
        else {
            setTimeout (function(){
                spsupport.api.goSend(ev, nI, anim);
            }, 400);
        }
    },

    resetPBar: function(anim, nI) {
        var sp = spsupport.p;
        var pBar = sp.sfIcon.prog.node;
        //var pBar = sp.$(sp.sfIcon.prog.node);
//        pBar.stop().css({
//            width: "0px",
//            display: "none"
//        });
        if( pBar ){
            anim.stop();
            sufio.style(
                pBar ,{
                    width: "0px",
                    display: "none"
                });
        }
        nI.sent = 0;
    },

    hdIcon: function() {
      var sp = spsupport.p;
      sp.$(sp.sfIcon.ic).css({
          'top': '-100px'
        });
    },

    addSfsrpIcon: function(){
         var sp = spsupport.p,
            d = window.document;
         var nm = 'sf' + sp.sfsrp.ind,
            ni = sp.sfIcon.ic = d.createElement('div');

        if (sp.sfsrp.ind == 1) {
            spsupport.p.$(ni).append('<img id="sfsrpIm" style="max-width:33px;max-height:33px;position: absolute; top: 2px; left: 65px; background: #ff0000;"></img>');
        }
        ni.setAttribute(sp.sfIcon.evl, '-1');
        ni.title = " See Similar ";
        ni.style.position = "absolute";
        ni.style.top = -200 + "px";
        ni.style.cursor = "pointer";
        ni.style.width = sp.sfsrp.prop[sp.sfsrp.ind].w + 'px';
        ni.style.height = sp.sfsrp.prop[sp.sfsrp.ind].h + 'px';
        ni.style.backgroundPosition = '0 0';
        ni.style.backgroundImage='url(' + sp.imgPath + nm + '.png?v=' + sp.appVersion +')';
        ni.onmouseover  = function(e){
             ni.style.backgroundPosition = '0px -' + sp.sfsrp.prop[sp.sfsrp.ind].h + 'px';
        };

        ni.onmouseout  = function(e){
             ni.style.backgroundPosition = '0 0';
        };

        ni.onclick = function(e){
            var evt = e || window.event;
            if (this.img) {
               spsupport.api.sfsrp(ni, 0);
            }
        };
        d.body.appendChild(ni);

    },

    addSFIcon: function(parent){
        var sp = spsupport.p,
            sfu = superfish.util,
            sa = spsupport.api,
            hWidth = parseInt(sp.sfIcon.prog.w[0]/4.2),
            hWidth2 = parseInt(sp.sfIcon.prog.w[1]/4.2),
            hTime = parseInt(sp.sfIcon.prog.time/4.2),
            nm = superfish.b.sfsrp ? 'sf' : (superfish.b.whiteIcon ? 'wi' : 'si'),
            // create see similar button
            nI = sp.sfIcon.ic = sa.createImg( sp.imgPath + sp.partner + nm + 0 + ".png?v=" + sp.appVersion );

        nI.setAttribute(sp.sfIcon.evl, '-1');
        nI.setAttribute("id", 'sf_see_similar');
        nI.title = " See Similar ";
        nI.style.position = "absolute";
        nI.style.top = -200 + "px";
        nI.style.left = -200 + "px";
        nI.style.opacity = "1";
        nI.style.zIndex = 32005;
        nI.style.cursor = "pointer";
        // Fix IE bug
        nI.style.width = "" + sp.sfIcon.big.w + "px";
        nI.style.height = "" + sp.sfIcon.big.h + "px";
        nI.type = 1;
        nI.src = sa.sfIPath(nI.type).r;

        var anim = sufio.animateProperty({
            node: sp.sfIcon.prog.node,
            duration: sp.sfIcon.prog.time,
            properties: {
                width: {
                    start: "0",
                    end:  sp.sfIcon.prog.w[0],
                    unit: "px"
                }
            },
            onEnd: function() {
                if (superfish.b.lp) {
                    sa.osr(nI, 0);
                }
                else {
                    // hide wakeup bubble if it's showing
                    if( superfish.inimg && superfish.inimg.wakeupDiv ){
                        superfish.inimg.wakeupDiv.css('top','-1000em');
                    }
                    sa.goSend(3, nI, anim);
                }
            }
        });

        var anim2 = sufio.animateProperty({
            node: sp.sfIcon.prog.node,
            duration: sp.sfIcon.prog.time,
            properties: {
                width: {
                    start: "0",
                    end:  sp.sfIcon.prog.w[1],
                    unit: "px"
                }
            },
            onEnd: function() {
                if (superfish.b.lp) {
                    sa.osr(nI, 0);
                }
                else {
                    // hide wakeup bubble if it's showing
                    if( superfish.inimg && superfish.inimg.wakeupDiv ){
                        superfish.inimg.wakeupDiv.css('top','-1000em');
                    }
                    sa.goSend(3, nI, anim2);
                }
            }
        });


//        sufio.connect( anim,'onAnimate', function( curveValue ){
//            if ( !nI.sent ) {
//                if( parseInt(curveValue.width) >= hWidth && !superfish.b.lp){
//                    spsupport.api.goSend(2, nI, anim);
//                }
//            }
//        });
//
//        sufio.connect( anim2,'onAnimate', function( curveValue ){
//            if ( !nI.sent ) {
//                if( parseInt(curveValue.width) >= hWidth2 && !superfish.b.lp){
//                    spsupport.api.goSend(2, nI, anim2);
//                }
//            }
//        });


	    function iconClick(event)
	    {
		    if( event && event.button == 2 )
		    {
			    return;
		    }

		    if (this.img) {
			    var anm = (nI.type == 1 ? anim: anim2);
			    if (superfish.b.lp) {
				    spsupport.api.osr(nI, 0);
			    }
			    else if (superfish.b.sfsrp) {
				    spsupport.api.sfsrp(nI, 0);
			    }
			    else {
				    spsupport.api.goSend(1, this, anm);
			    }
		    }
	    }

	    function iconMouseOut(event)
	    {
		    if( event.relatedTarget != sp.sfIcon.prog.node )
		    {
			    this.src = spsupport.api.sfIPath(this.type).r;
			    sp.sfIcon.prog.e = (sp.sfIcon.prog.e == 2 ? 2 : 0);
			    var anm = (nI.type == 1 ? anim : anim2);
                            clearTimeout(sp.sfIcon.timer);
			    spsupport.api.resetPBar(anm, this);
			    if (sp.sfIcon.prog.e == 0) {
				    if (sfu) {
					    sfu.hideLaser();
				    }
				    else {
					    if (spsupport.p.sfIcon.an) {
						    spsupport.p.sfIcon.an.style.top = '-2000px';
						    spsupport.p.sfIcon.an.style.left = '-2000px';
					    }
				    }
			    }
			    if (sp.before == 2) {
				    if (sfu) {
					    sfu.reportClose();
				    }
			    }
		    }
	    }


	    sp.$(nI).mouseover(function(event)
	    {
            if (superfish.b.sfsrp)
            {
                nI.src = spsupport.api.sfIPath(nI.type).o;
            }
            else
            {
                var relTarget = event.relatedTarget;

                if ( relTarget != sp.sfIcon.prog.node)
                {
                    this.src = spsupport.api.sfIPath(this.type).o;
                    sp.sfIcon.prog.e = 1;
                    if (sp.sfIcon.prog.node)
                    {
                        var iProp = ( this.type == 2  ?  sp.sfIcon.small  :  sp.sfIcon.big );
                        var dif = iProp.h - sp.sfIcon.prog.h;
                        var nt = parseInt( this.style.top ), nl = parseInt( this.style.left );
                        sp.$(sp.sfIcon.prog.node).css({
                                display: "block",
                                top: nt + dif - (superfish.b.whiteIcon ? 1 : 2) + "px",
                                left: nl + (superfish.b.whiteIcon ? 1 : 2) + "px"
                        });
                        var ip = spsupport.api.getItemPos(this.img);
                        var anm = (this.type == 1 ? anim : anim2);
                        anm.play();
                        sp.sfIcon.timer = setTimeout(function() {
                            spsupport.api.goSend(2, nI, anm);
                        }, hTime);
                        
                        if (superfish.util)
                        {
                                superfish.util.hideLaser();
                                superfish.util.showLaser(ip);
                        }
                }
                }
            }
        });

	    sp.$(nI).mouseout(iconMouseOut);
	    sp.$(nI).click(iconClick);

	    sp.$(sp.sfIcon.prog.node).mousedown(iconClick);
	    sp.$(sp.sfIcon.prog.node).mouseout(function(event)
	    {
		    if( !event.relatedTarget || event.relatedTarget != sp.sfIcon.ic)
		    {
			    iconMouseOut(event);
		    }
	    });

	    parent && parent.appendChild(nI);
    },

    // position the see similar button in the given image
    positionSFDiv: function( img ){
        var sp = spsupport.p,
            spi = sp.sfIcon,
            nI = spi.ic,
            imgPos = spsupport.api.getImagePosition(img),
            t, l, ipos;

        if (superfish.b.sfsrp) {
            if (sp.sfsrp.ind == 1) {
                sp.$('#sfsrpIm', nI).attr('src', img.src);
            }
            t = img.height > 199 ? (imgPos.y + img.height - sp.sfsrp.prop[sp.sfsrp.ind].h + 3) : (imgPos.y + img.height - img.height/6);
            l = sp.sfsrp.ind == 3 || img.width > sp.sfsrp.prop[sp.sfsrp.ind].w*2 ? (imgPos.x + 1) : (imgPos.x - (sp.sfsrp.prop[sp.sfsrp.ind].w - img.width)/2);
            ipos = {t: t, l: l};
        }
        else {
            if(( img.width <= spi.maxSmImg.w ) || ( img.height <= spi.maxSmImg.h ) ) {
                sp.$(nI).css({
                    width:  spi.small.w + "px",
                    height: spi.small.h + "px"
                });

                nI.src = spsupport.api.sfIPath(2).r;
                nI.type = 2;
            }
            else {
                sp.$(nI).css({
                    width:  spi.big.w + "px",
                    height: spi.big.h + "px"
                });
                nI.src = spsupport.api.sfIPath(1).r;
                nI.type = 1;
            }

             ipos = spsupport.api.calcIconPos(img, nI, imgPos);
        }

        if(img.getAttribute("nosudispl")){
            ipos.t -=12;
            if(!superfish.inimg.iiExtands )   // when inImage is on the picture - take see more up.
                ipos.t -= (superfish.inimg.h + superfish.inimg.hAdd);
	}
        nI.style.top = "" + parseInt(ipos.t) + "px";
        nI.style.left = "" + parseInt(ipos.l) + "px";
        nI.style.opacity = "1";
    },

    calcIconPos: function(img, nI, imgPos) {
            var sp = spsupport.p,
            spi = sp.sfIcon,
            t, l;

            var io = (nI.type == 1 ? spi.big : spi.small);
            t = superfish.b.whiteIcon ? (imgPos.y + 2) : (imgPos.y + img.height - io.h);
                // show see similar button on left side of image
            var align = (img.width > spi.big.w*2 ? (imgPos.x + 1) : (imgPos.x - (io.w - img.width)/2));

            // check for wakeup div
            if( superfish.inimg && superfish.inimg.wakeupDiv ){
                // show see similar button in center of image
                align = (imgPos.x - (io.w - img.width)/2);
            }

            l = superfish.b.whiteIcon ? (imgPos.x - (io.w - img.width)/2) : align;
            return {t: t, l: l};
    },

    addSFButton: function(pr, img) {
        // spsupport.log(img.src);
        var sp = spsupport.p,
            sa = spsupport.api,
            nm = superfish.b.sfsrp ? 'sf' : (superfish.b.whiteIcon ? 'wi' : 'si'),
            nI = sa.createImg( sp.imgPath + sp.partner + nm + 0 + ".png?v=" + sp.appVersion ),
            imgPos = spsupport.api.getImagePosition(img);
        nI.style.position = "absolute";
        nI.type = 1;
        nI.src = sa.sfIPath(nI.type).r;
        var ipos = spsupport.api.calcIconPos(img, nI, imgPos);
        nI.style.top = "" + parseInt(ipos.t) + "px";
        nI.style.left = "" + parseInt(ipos.l) + "px";
        nI.style.opacity = "1";
        pr.appendChild(nI);
    },

    addSFDiv: function(pr, img) {

        var bi = 600,
            sp = spsupport.p,
            spi = sp.sfIcon,
            sa = spsupport.api,
            dv = document.createElement("div"),
            imgPos = spsupport.api.getImagePosition(img);
            //t = this;

        var r = spsupport.sites.rules();
        if(r && r.checkIsGoodImage){
            if(!r.checkIsGoodImage(img,imgPos))
                return;
        }
        else{
            // normal check
            if (img.width > bi || img.height > bi || imgPos.x < 0 || imgPos.y < 10) {
                return;
            }
        }

        var zind = r && r.getZIndex ? (r.getZIndex() + "") : "32002";


	    sp.$(dv).css({
            //display:         "inline-block",
            display:         (sp.ifLoaded) ? 'inline-block' : 'none', // Start activation areas hidden, as we do not want to display the "find similar" buttons on disabled. We will enable them after the -7890 message
            border:          'none',
            backgroundColor:  '#fff',
            opacity:          '0.01',
            zIndex:           zind,//'14', //"12002",
            position:        "absolute",
            width:           img.width + "px",
            height:          img.height + "px",
            top:             imgPos.y + "px",
            left:            imgPos.x + "px"
        });

        dv.img = img;

        dv.onmouseover  = function(e){
            e = !e ? window.event : e;

            var relTarget = e.relatedTarget ? e.relatedTarget : e.fromElement;
            if (superfish.p.onAir && superfish.util && this.img == superfish.util.currImg) {
                return;
            }

            sp.bh.busy = 1;

            if (relTarget != spi.ic) {
                var imgPos = sa.getImagePosition(img),
                    lf = parseInt(this.style.left),
                    tp = parseInt(this.style.top);
                if (superfish.util) {
                    superfish.util.hideLaser();
                }
                if (Math.abs(imgPos.x - lf) > 100 || Math.abs(imgPos.y - tp) > 100) {
                    sa.fixDivsPos();
                }
                spi.ic.img = this.img;
                // position the 'see similar' button
                sa.positionSFDiv( img );
                sa.setLimits(imgPos);
                sa.bindEvents();
            }

            sp.sfIcon.nl.show();
            this.style.display = "none";
            sp.bh.busy = 0;
        };
        pr.appendChild(dv);
    },

    validateInimg: function(im) {
        var si = superfish.inimg,
            sg = superfish.sg;
        var ifSg = (sg ? sg.sSite : 0);
        if (ifSg || (superfish.b.slideup && spsupport.p.pageType !='SRP' || superfish.b.slideupSrp && spsupport.p.pageType =='SRP' )) {
            return 1;
        }
        var o = spsupport.api.getItemPos(im);
        var ii = (superfish.b.inimg && si ? si.vi(o.w, o.h) : 0);
        ii = spsupport.api.careIi(ii, 1);

        if (ii == 0 && superfish.isAuto)
        	ii = 2;

        return ii;
    },

    careIi: function(ii, flow) {
        ii = spsupport.p.siteType == "wl" && ii > 8 ? (flow == 1 ? 0 : 6) : ii;
        ii = ii > 6 ? 6 : ii;
        ii = (ii > 1 ? ii : 0);
        if (superfish.b.slideup && spsupport.p.pageType !='SRP' || superfish.b.slideupSrp && spsupport.p.pageType =='SRP' ) {
            if (superfish.b.slideupAndInimg) {
                ii = Math.max(ii, 4);
            }
            else {
                superfish.inimg.itn = ii = 4;
            }
        }
        return ii;
    },

    validateSU: function( im, iT ){
        var sp = spsupport.p;
        var cnd = (superfish.b.inimg ? parseInt(iT) > 0 : true);
        var cndM = im.width > sp.prodPage.d && im.height > sp.prodPage.d;
        var isSrp = spsupport.sites.isSrp();
        cndM = cndM && !isSrp;
        cndM = spsupport.whiteStage.pip ? cndM : cndM && parseInt(iT) < sp.prodPage.l;
        cndM = cndM && cnd && sp.prodPage.p != im || spsupport.sites.validProdImg();
        var validIi = spsupport.api.validateInimg(im);
        if( spsupport.sites.su() && !sp.prodPage.s &&
            ( spsupport.p.supportedSite || spsupport.whiteStage.st ?
                cndM && validIi :
                sp.prodPage.p != im && validIi)
            ){
            sp.prodPage.s = 1;
            sp.prodPage.i ++;
            sp.pageType = "PP";
            sp.prodPage.p = im;
            sp.SRP.reset();
            spsupport.sites.offInt();
            setTimeout(function() {
                superfish.utilities.sfWatcher.setState("validateSU");
                spsupport.api.puPSearch(1, im);
            }, 30);
            //spsupport.log("validated");
            return(1);
        }
        //spsupport.log("not valid");
        return(0);
    },

    addSFProgressBar: function(iNode){
        var bProp = spsupport.p.sfIcon.prog;
        if(!bProp.node) {
            bProp.node = sufio.place("<div id='sfIconProgressBar'></div>", iNode, "after" );
            var rd = 4;
            var col = spsupport.p.isIE8 || spsupport.p.isIEQ ? bProp.color : bProp.colorRgba;
            bProp.node.setAttribute('style', '-moz-border-radius : '+rd+'px; -webkit-border-radius : '+rd+'px; border-radius: '+rd+'px;');
            sufio.style( bProp.node ,{
                position: "absolute",
                overflow: "hidden",
                width: "0px",
                height: bProp.h + "px",
                zIndex: "32008",
                cursor: "pointer",
                backgroundColor: col 
               // opacity: bProp.opac
            });
            
            if (spsupport.p.isIE8 || spsupport.p.isIEQ) {
                sufio.style( bProp.node ,{
                    opacity: bProp.opac
                });
            }
        }
    },

    addAn: function(){
        var sp = spsupport.p;
        if( !sp.sfIcon.an ) {
            sp.sfIcon.an = sufio.place("<div id='sfImgAnalyzer'></div>", sufio.body());
            //var scanImageUrl = spsupport.p.imgPath + spsupport.p.partner + "/scan.png";

	        sufio.style(sp.sfIcon.an ,{
                position: "absolute",
                overflow: "hidden",
                width: "24px",
                height: "100px",
                zIndex: "32000",
                top: "-200px",
                left: "-200px",
                //filter: "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + scanImageUrl + "',sizingMethod='scale')",
                background: "url(" + sp.imgPath + sp.partner + "scan.png) repeat-y"

            });
        }
    },

    sfButtons: function(){
        return document.getElementById("sfButtons");
    },

    getImagePosition: function(img) {

        var sp = spsupport.p;
        var jqImage = spsupport.p.$(img),
            imgPos = jqImage.position(),
            imgOffset = jqImage.offset();

        // returns an object that duplicates the returned dojo coords method.
        // the returned options are for legacy purposes
        var y = parseInt(imgOffset.top);
        if (sp.applTb) {
            y -= sp.applTbVal;
        }
        /*if (sp.jqueryFrame && sp.isIE8 && document.documentElement) {
            //y += document.documentElement.scrollTop;
        }*/

        return {
            l: parseInt(imgPos.left),
            t: parseInt(imgPos.top),
            w: parseInt(jqImage.outerWidth(true)),
            h: parseInt(jqImage.outerHeight(true)),
            x: parseInt(imgOffset.left),
            y: y
        }
    },

    getMeta: function(name) {
        var mtc = spsupport.p.$('meta[name = "'+name+'"]');
        if(mtc.length){
            return  mtc[0].content;
        }
        return '';
    },

    getLinkNode: function(node, level){
        var lNode = 0;
        if (node) {
            var tn = node;
            for (var i = 0; i < level; i++) {
                if (tn) {
                    if (tn.nodeName.toUpperCase() == "A") {
                        lNode = tn;
                        break;
                    }
                    else {
                        tn = tn.parentNode;
                    }
                }
            }
        }
        return lNode;
    },
            
    textFromNodes: function(nodes) {
        var txt = '', found = false;
        var thisText = '';
        var thatTexts = [];
        nodes.each(
            function(i) {
               // console.log(i);
               // console.log(this);
                thisText = spsupport.api.getTextOfChildNodes(this);
               // console.log(thisText + " ============ ", thatTexts);
                found = false;
                for (var j = 0; j < thatTexts.length; j++) {
                    if (thisText == thatTexts[j]) {
                        found = true;
                    }
                }
                if (!found) {
                    txt += (" " + thisText);
                }
                //console.log(i + " " + txt);
                thatTexts[i] = thisText;
            });
        txt = txt.replace(/\s+/g," ");
        txt = spsupport.p.$.trim(txt);
        return txt;   
    },

    textFromLink: function(url){
       // spsupport.log("Start " + url);
        var txt = '';
        var origUrl = url;

       if( url.indexOf( "javascript" ) == -1 ){
            var guyReq="(http(s)?)?(://)?("+document.domain+")?((www.)("+document.domain+"))?";
            var patt1=new RegExp(guyReq,"i");
            url = url.replace(patt1, "");

            var _url = ""
            var plus = url.lastIndexOf("+", url.length - 1);
            _url = (plus > -1 ? url.substr(0, plus) : url);
            var urlLC = _url.toLowerCase();
            
            var q = 'a[href *= "' + _url + '"], a[href *= "' + urlLC  + '"]';
            //spsupport.log(q);
            var nodes = spsupport.p.$(q); //(sec ? spsupport.p.$(q, sec) : spsupport.p.$(q));
            //spsupport.log("nodes.length = " + nodes.length);
            if (nodes.length == 0) {
                var slash = url.lastIndexOf('/');
                if (slash > -1) {
                    urlLC = url.substr(slash + 1, origUrl.length - 1);
                    q = 'a[href *= "' + urlLC  + '"]';
                    nodes = spsupport.p.$(q); //(sec ? spsupport.p.$(q, sec) : spsupport.p.$(q));
                }
            }
            txt = spsupport.api.textFromNodes(nodes);
        }
       // spsupport.log("=========>>>>>>>>>>>> " + txt);
        var words = txt.split(' ');
        if (words.length < 3) {
            var questionSign = url.lastIndexOf( "?", url.length - 1 );
            var urlNoParams = ( questionSign > -1 ? url.substr(0, questionSign) : "" );
            nodes = (spsupport.p.$('a[href *= "' + urlNoParams  + '"]'));
            txt = spsupport.api.textFromNodes(nodes);
        }
        return txt;
    },

    getTextOfChildNodes: function(node){
        var txtNode = "";
        var ind;
        for( ind = 0; node && ind < node.childNodes.length; ind++ ){
            if(node.childNodes[ind].nodeType == 3) { // "3" is the type of <textNode> tag
                txtNode = spsupport.p.$.trim( txtNode + " " + node.childNodes[ ind ].nodeValue );
            }
            if( node.childNodes[ ind ].childNodes.length > 0 ) {
                txtNode = spsupport.p.$.trim( txtNode +
                    " "  + spsupport.api.getTextOfChildNodes( node.childNodes[ ind ] ) );
            }
        }
        return txtNode;
    },

    vTextLength: function( t ) {
        if(!t)
            return "";

        if( t.length > 1000 ){
            return "";
        }else if(t.length < 320 ){
            return t;
        }else{
            if( spsupport.p.isIE7 ){
                return t.substr(0, 320);
            }
            return t;
        }
    },

    showDivs: function() {
      var bs = this.sfButtons();
      spsupport.p.$('div', spsupport.p.$(bs)).show();
    },

    trimText : function(text) {
        if(!text)
            return "";
        var maxLength = 200;
        if(typeof text == 'string' && text.length > maxLength) {
            return text.substr(0, maxLength);
        }
        else
            return text;
    },

    getItemJSON: function( img ) {
        var spa = spsupport.api;
        var sp = spsupport.p;
//        var ws = spsupport.whiteStage;
        var iURL = "";
        try{
            iURL = decodeURIComponent( img.src );
        }catch(e){
            iURL = img.src;
        }
        var tmpMinWidth = 200;
        var tmpMinHeight = 200;
        var relData;

        //relData= spsupport.sites.getRelText( img, spa.getLinkNode, spa.textFromLink );
        relData= spsupport.sites.getRelText(img);

        if(!relData || !relData.iText || relData.iText.length < 15 ){
            if(img.width >= tmpMinWidth && img.height >= tmpMinHeight){
                // it's time to assume that it's a Product page !!!
                relData = spsupport.sites.getRelTextPP(img);
            }
        }

        if(relData && relData.iText){
            relData.iText = sp.$.trim(relData.iText);
            //relData.iText = relData.iText.replace(/[\n\r\t\*]/gi, '');
        }

        var pt;
        if (sp.pageType && sp.pageType!="NA") {
            pt = sp.pageType;
        }
        else {
            pt =  sp.prodPage.i > 0 ? "PP": "SRP";
            sp.pageType = pt;
        }

        var dt = (pt == 'PP' && img != sp.prodPage.p ? '' : document.title + spsupport.api.getMK( img ) );
        var irt = ( relData ? spa.vTextLength(  relData.iText  )  : '' );
        var it = (relData && relData.iTitle ? relData.iTitle : '');
        it += ' ' + img.title + ' ' + img.alt;
        it = spsupport.p.$.trim(it);
        var pu = ( relData ? relData.prodUrl : "" );
        var pr = superfish.b.price.get(img);

        dt = spa.trimText(dt);
        it = spa.trimText(it);
        irt = spa.trimText(irt);

        var jsonObj = {
            userid: encodeURIComponent( sp.userid ),
            merchantName: encodeURIComponent(spa.merchantName()),
            dlsource: sp.dlsource ,
            appVersion: sp.appVersion,
	    documentTitle: dt.replace(/[']/g, "\047").replace(/["]/g, "\\\""),
            imageURL: encodeURIComponent( spsupport.sites.vImgURL( iURL ) ),
            imageTitle: it.replace(/[']/g, "\047").replace(/["]/g, "\\\""),
            imageRelatedText: irt.replace(/[']/g, "\047").replace(/["]/g, "\\\""),
            pr: pr,
            productUrl: encodeURIComponent(pu)
        };
        return jsonObj;
    },

    /**
     * Return the item name from the page title text
     */
    getTitleText: function(){
    	var text = spsupport.p.$('title').text().toLowerCase();
		var reviewIndex = text.indexOf("review");
		var pipeIndex = text.indexOf("|");
		if (pipeIndex !== -1){
			text = text.substr(0, pipeIndex);
		}
		if (reviewIndex !== -1 ){
			//case the first word is review
			if(reviewIndex < 5){
				text = text.split("review")[1];
			}else{
				text = text.substr(0, reviewIndex-1);
			}
		}
		return text;
    },

    getItemPos: function(img) {
        var iURL = "";
        try{
            iURL = decodeURIComponent( img.src );
        }catch(e){
            iURL = img.src;
        }

        var imgPos = spsupport.api.getImagePosition( img );
        var jsonObj = {
            imageURL: encodeURIComponent( spsupport.sites.vImgURL( iURL ) ),
            x: imgPos.x,
            y: imgPos.y,
            w: img.width || img.offsetWidth,
            h: img.height || img.offsetHeight
        };
        return jsonObj;
    },

    // Get Meta Keywords
    getMK: function( i ){
        var dd = document.domain.toLowerCase();
        if( ( dd.indexOf("zappos.com") > -1 || dd.indexOf("6pm.com") > -1 ) &&
            ( spsupport.p.prodPage.i > 0 && spsupport.p.prodPage.p  == i ) ){
            var kw = spsupport.p.$('meta[name = "keywords"]');
            if( kw.length){
                kw = kw[0].content.split(",");
                var lim = kw.length > 2 ? kw.length - 3 : kw.length - 1;
                var kwc = "";
                for( var j = 0; j <= lim; j++ ){
                    kwc = kw[ j ] + ( j < lim ? "," : ""  )
                }
                return " [] " + kwc;
            }
        }
        return "";
    },

    merchantName: function()  {
        return  spsupport.p.merchantName;
    },
    superfish: function(){
        return window.top.superfish;
    },

    sendMessageToExtenstion: function( msgName, data ){
        var d = document;
        if(sufio){
            var jsData = sufio.toJson(data);

	        if (spsupport.p.isIE) {
                try {
                    // The bho get the parameters in a reverse order
                    window.sendMessageToBHO(jsData, msgName);
                } catch(e) {}
            } else {
                var el = d.getElementById("sfMsgId");
                if (!el){
                    el = d.createElement("sfMsg");
                    el.setAttribute("id", "sfMsgId");
                    d.body.appendChild(el);
                }
                el.setAttribute("data", jsData);
                var evt = d.createEvent("Events");
                evt.initEvent(msgName, true, false);
                el.dispatchEvent(evt);
            }
        }
    },
    saveStatistics: function() {
        var sp = spsupport.p;
        if( document.domain.indexOf("superfish.com") > -1 ||
            sp.dlsource == "conduit" ||
            sp.dlsource == "pagetweak" ||
            sp.dlsource == "similarweb"){
            return;
        }
        var imageCount = 0;
        var sfButtons = spsupport.api.sfButtons();
        if( sfButtons != null ){
            imageCount = sfButtons.children.length;
        }
        var data = {
            "imageCount": imageCount,
            "ip": superfish.b.ip
        }
        if( spsupport.api.isOlderVersion( '1.2.0.0', sp.clientVersion ) ){
            data.Url = document.location;
            data.userid = sp.userid;
            data.versionId = sp.clientVersion;
            data.dlsource = sp.dlsource;

            if( sp.CD_CTID != "" ) {
                data.CD_CTID = sp.CD_CTID;
            }
            spsupport.api.jsonpRequest( sp.sfDomain_ + "saveStatistics.action", data );
        } else  {
            spsupport.api.sendMessageToExtenstion("SuperFishSaveStatisticsMessage", data);
        }
    },

    isOlderVersion: function(bVer, compVer) {
        var res = 0;
        var bTokens = bVer.split(".");
        var compTokens = compVer.split(".");

        if (bTokens.length == 4 && compTokens.length == 4){
            var isEqual = 0;
            for (var z = 0; z <= 3 && !isEqual && !res ; z++){
                if (+(bTokens[z]) > +(compTokens[z])) {
                    res = 1;
                    isEqual = 1;
                } else if (+(bTokens[z]) < +(compTokens[z])) {
                    isEqual = 1;
                }
            }
        }
        return res;
    },

    leftPad: function( val, padString, length) {
        var str = val + "";
        while (str.length < length){
            str = padString + str;
        }
        return str;
    },

    getDateFormated: function(){
        var dt = new Date();
        return dt.getFullYear() + spsupport.api.leftPad( dt.getMonth() + 1,"0", 2 ) + spsupport.api.leftPad( dt.getDate(),"0", 2 ) + "";
    },

    dtBr: spsupport.dtBr,

    nofityStatisticsAction:function(action) {
        var sp = spsupport.p;
        if(sp.w3iAFS != ""){
            data.w3iAFS = sp.w3iAFS;
        }
        if(sp.CD_CTID != ""){
            data.CD_CTID = sp.CD_CTID;
        }

        spsupport.api.jsonpRequest( sp.sfDomain_ + "notifyStats.action", {
            "action": action,
            "userid": sp.userid,
            "versionId": sp.clientVersion,
            "dlsource": sp.dlsource,
            "browser": navigator.userAgent
        });
    },
    unloadEvent: function(){
    }
};

spsupport.domHelper = {
    oldOnMouseMove: 0,

    addOnresizeEvent : function(func){
        if (typeof window.onresize != 'function'){
            window.onresize = func;
        } else {
            var oldonresize = window.onresize;
            window.onresize = function(e) {
                if( oldonresize ){
                    if (spsupport.p.isIE) {
                        oldonresize(e);
                    }
                    else {
                        setTimeout( oldonresize,350 );
                    }
                }
                if( spsupport.p.isIE ) {
                    func(e);
                }
                else {
                    setTimeout(func, 200);
                }
            }
        }
    },
    addFocusEvent: function(func){
        var oldonfocus = window.onfocus;
        if (typeof window.onfocus != 'function') {
            window.onfocus = func;
        }else{
            window.onfocus = function(e)  {
                if (oldonfocus) {
                    oldonfocus(e);
                }
                func(e);
            }
        }
    },

    addBlurEvent: function(func){
        var oldonblur = window.onblur;
        if (typeof window.onblur != 'function') {
            window.onblur = func;
        }else{
            window.onblur = function(e)  {
                if (oldonblur) {
                    oldonblur(e);
                }
                func(e);
            }
        }
    },

    addUnloadEvent : function(func){
        var oldonunload = window.onunload;
        if (typeof window.onunload != 'function'){
            window.onunload = func;
        } else {
            window.onunload = function(e) {
                if (oldonunload) {
                    oldonunload(e);
                }
                func(e);
            }
        }
    },

    addEListener : function(node, func, evt ){
        if( window.addEventListener ){
            node.addEventListener(evt,func,false);
        }else{
            node.attachEvent(evt,func,false);
        }
    }
};

spsupport.api.init();
})();
