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
<%@ page import="org.jboss.dashboard.ui.components.RedirectionHandler"%>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="resources.tld" prefix="resource" %>

FCKConfig.ToolbarSets['mini'] = [
    ['FitWindow','-','Source','-','Undo','Redo'],['PasteText'],
    '/',
    ['Bold','Italic','Underline'],
    ['OrderedList','UnorderedList'],
<%--    ['Link'],
    ['Image'], --%>
    '/',
    ['Style','FontFormat']
];

FCKConfig.ToolbarSets['miniForHTMLPanel'] = [
    ['FitWindow','Save','-','Source','-','Undo','Redo'],['PasteText'],
    '/',
    ['Bold','Italic','Underline'],
    ['OrderedList','UnorderedList'],
<%--    ['Link'],
    ['Image'], --%>
    '/',
    ['Style','FontFormat']
];

FCKConfig.ToolbarSets["full"] = [
 ['FitWindow','-','Source','-','Undo','Redo'],
 ['DocProps','NewPage','Preview','-','Templates'],
 ['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],
 ['Find','Replace','-','SelectAll','RemoveFormat'],
 '/',
 ['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
 ['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],
 ['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
<%-- ['Link','Unlink','Anchor']
 ['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'],--%>
 ['Table','Rule','Smiley','SpecialChar','PageBreak'],
 '/',
 ['Style','FontFormat','FontName','FontSize'],
 ['TextColor','BGColor','ShowBlocks'] // No comma for the last row.
] ;

FCKConfig.ToolbarSets["fullForHTMLPanel"] = [
    ['FitWindow','Save','-','Source','-','Undo','Redo'],
    ['DocProps','NewPage','Preview','-','Templates'],
    ['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],
    ['Find','Replace','-','SelectAll','RemoveFormat'],
    '/',
    ['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],
    ['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
    ['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],
    ['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
<%--    ['Link','Unlink','Anchor'],
    ['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'], --%>
    ['Table','Rule','Smiley','SpecialChar','PageBreak'],
    '/',
    ['Style','FontFormat','FontName','FontSize'],
    ['TextColor','BGColor','ShowBlocks'] // No comma for the last row
] ;

FCKConfig.ToolbarSets['Default'] = FCKConfig.ToolbarSets['mini'];

FCKConfig.minimizedToolbarSet= 'mini';
FCKConfig.maximizedToolbarSet= 'full';

FCKConfig.EnterMode = 'br';
FCKConfig.ShiftEnterMode = 'p';

FCKConfig.LinkBrowserURL=  '<factory:url bean="org.jboss.dashboard.ui.components.RedirectionHandler" action="redirectToSection" friendly="false"><factory:param name="<%=RedirectionHandler.PARAM_PAGE_TO_REDIRECT%>" value="/fckeditor/custom/FCKSelectImage.jsp?mode=link"/></factory:url>'.replace(/&amp;/g,'&');
FCKConfig.ImageBrowserURL= '<factory:url bean="org.jboss.dashboard.ui.components.RedirectionHandler" action="redirectToSection" friendly="false"><factory:param name="<%=RedirectionHandler.PARAM_PAGE_TO_REDIRECT%>" value="/fckeditor/custom/FCKSelectImage.jsp?mode=image"/></factory:url>'.replace(/&amp;/g,'&');
FCKConfig.FlashBrowserURL= '<factory:url bean="org.jboss.dashboard.ui.components.RedirectionHandler" action="redirectToSection" friendly="false"><factory:param name="<%=RedirectionHandler.PARAM_PAGE_TO_REDIRECT%>" value="/fckeditor/custom/FCKSelectImage.jsp?mode=image"/></factory:url>'.replace(/&amp;/g,'&');
FCKConfig.EditorAreaCSS=   '<resource:link category="skin" resourceId="CSS"/>'.replace(/&amp;/g,'&');

FCKConfig.AutoDetectLanguage=false;

FCKConfig.ImageUpload = false ;
FCKConfig.FlashUpload = false ;
FCKConfig.LinkUpload = false ;

FCKFitWindow.prototype.Execute = function()
{
    var eEditorFrame		= window.frameElement ;
	var eEditorFrameStyle	= eEditorFrame.style ;

	var eMainWindow			= parent ;
	var eDocEl				= eMainWindow.document.documentElement ;
	var eBody				= eMainWindow.document.body ;
	var eBodyStyle			= eBody.style ;
	var eParent ;


    // No original style properties known? Go fullscreen.
	if ( !this.IsMaximized )
	{
        // Registering an event handler when the window gets resized.
		if( FCKBrowserInfo.IsIE )
			eMainWindow.attachEvent( 'onresize', FCKFitWindow_Resize ) ;
		else
			eMainWindow.addEventListener( 'resize', FCKFitWindow_Resize, true ) ;

		// Save the scrollbars position.
		this._ScrollPos = FCKTools.GetScrollPosition( eMainWindow ) ;

		// Save and reset the styles for the entire node tree. They could interfere in the result.
		eParent = eEditorFrame ;
		// The extra () is to avoid a warning with strict error checking. This is ok.
		while( (eParent = eParent.parentNode) )
		{
			if ( eParent.nodeType == 1 )
			{
				eParent._fckSavedStyles = FCKTools.SaveStyles( eParent ) ;
				eParent.style.zIndex = FCKConfig.FloatingPanelsZIndex - 1 ;
			}
		}

		// Hide IE scrollbars (in strict mode).
		if ( FCKBrowserInfo.IsIE )
		{
			this.documentElementOverflow = eDocEl.style.overflow ;
			eDocEl.style.overflow	= 'hidden' ;
			eBodyStyle.overflow		= 'hidden' ;
		}
		else
		{
			// Hide the scroolbars in Firefox.
			eBodyStyle.overflow = 'hidden' ;
			eBodyStyle.width = '0px' ;
			eBodyStyle.height = '0px' ;
		}

		// Save the IFRAME styles.
		this._EditorFrameStyles = FCKTools.SaveStyles( eEditorFrame ) ;

		// Resize.
		var oViewPaneSize = FCKTools.GetViewPaneSize( eMainWindow ) ;

		eEditorFrameStyle.position	= "absolute";
		eEditorFrameStyle.zIndex	= FCKConfig.FloatingPanelsZIndex - 1;
		eEditorFrameStyle.left		= "0px";
		eEditorFrameStyle.top		= "0px";
		eEditorFrameStyle.width		= oViewPaneSize.Width + "px";
		eEditorFrameStyle.height	= oViewPaneSize.Height + "px";

		// Giving the frame some (huge) borders on his right and bottom
		// side to hide the background that would otherwise show when the
		// editor is in fullsize mode and the window is increased in size
		// not for IE, because IE immediately adapts the editor on resize,
		// without showing any of the background oddly in firefox, the
		// editor seems not to fill the whole frame, so just setting the
		// background of it to white to cover the page laying behind it anyway.
		if ( !FCKBrowserInfo.IsIE )
		{
			eEditorFrameStyle.borderRight = eEditorFrameStyle.borderBottom = "9999px solid white" ;
			eEditorFrameStyle.backgroundColor		= "white";
		}

		// Scroll to top left.
		eMainWindow.scrollTo(0, 0);
        if( !FCKBrowserInfo.IsIE ) {
            var editorPos = FCKTools.GetWindowPosition( eMainWindow, eEditorFrame ) ;
            if ( editorPos.x != 0 )
                eEditorFrameStyle.left = ( -1 * editorPos.x ) + "px" ;
            if ( editorPos.y != 0 )
                eEditorFrameStyle.top = ( -1 * editorPos.y ) + "px" ;
        }
		this.IsMaximized = true ;
	}
	else	// Resize to original size.
	{

        // Remove the event handler of window resizing.
		if( FCKBrowserInfo.IsIE )
			eMainWindow.detachEvent( "onresize", FCKFitWindow_Resize ) ;
		else
			eMainWindow.removeEventListener( "resize", FCKFitWindow_Resize, true ) ;

		// Restore the CSS position for the entire node tree.
		eParent = eEditorFrame ;
		// The extra () is to avoid a warning with strict error checking. This is ok.
		while( (eParent = eParent.parentNode) )
		{
			if ( eParent._fckSavedStyles )
			{
				FCKTools.RestoreStyles( eParent, eParent._fckSavedStyles ) ;
				eParent._fckSavedStyles = null ;
			}
		}

		// Restore IE scrollbars
		if ( FCKBrowserInfo.IsIE )
			eDocEl.style.overflow = this.documentElementOverflow ;

		// Restore original size
		FCKTools.RestoreStyles( eEditorFrame, this._EditorFrameStyles ) ;

		// Restore the window scroll position.
		eMainWindow.scrollTo( this._ScrollPos.X, this._ScrollPos.Y ) ;

		this.IsMaximized = false ;
	}

	FCKToolbarItems.GetItem('FitWindow').RefreshState() ;

    this.SwitchToolbars();

    // It seams that Firefox restarts the editing area when making this changes.
	// On FF 1.0.x, the area is not anymore editable. On FF 1.5+, the special
	//configuration, like DisableFFTableHandles and DisableObjectResizing get
	//lost, so we must reset it. Also, the cursor position and selection are
	//also lost, even if you comment the following line (MakeEditable).
	// if ( FCKBrowserInfo.IsGecko10 )	// Initially I thought it was a FF 1.0 only problem.
	if ( FCK.EditMode == FCK_EDITMODE_WYSIWYG )
		FCK.EditingArea.MakeEditable() ;

	FCK.Focus() ;
}

FCKFitWindow.prototype.SwitchToolbars = function() { 
    if ( this.IsMaximized ){
        FCK.ToolbarSet.Load(FCK.Config.maximizedToolbarSet);
    } else {
        FCK.ToolbarSet.Load(FCK.Config.minimizedToolbarSet);
    }
}

FCKEditingArea.prototype.Start=function(A,B) {
	var C=this.TargetElement;
	var D=FCKTools.GetElementDocument(C);

	while(C.firstChild) C.removeChild(C.firstChild);

	if (this.Mode==0) {
		if (FCK_IS_CUSTOM_DOMAIN) A='<script>document.domain="'+FCK_RUNTIME_DOMAIN+'";</script>'+A;
		if (FCKBrowserInfo.IsIE) A=A.replace(/(<base[^>]*?)\s*\/?>(?!\s*<\/base>)/gi,'$1></base>');
		else if (!B) {
			var E=A.match(FCKRegexLib.BeforeBody);
			var F=A.match(FCKRegexLib.AfterBody);
			if (E&&F) {
				var G=A.substr(E[1].length,A.length-E[1].length-F[1].length);
				A=E[1]+'&nbsp;'+F[1];
				// The following line was modified from the original fckeditorcode_gecko.js code to avoid the <br type="_moz"/>
                if (FCKBrowserInfo.IsGecko&&(G.length==0||FCKRegexLib.EmptyParagraph.test(G))) G=' ';   // leave blank space!!
				this._BodyHTML=G;
			} else this._BodyHTML=A;
		};
		var H=this.IFrame=D.createElement('iframe');
		var I='<script type="text/javascript" _fcktemp="true">window.onerror=function(){return true;};</script>';
		H.frameBorder=0;
		H.style.width=H.style.height='100%';
		if (FCK_IS_CUSTOM_DOMAIN&&FCKBrowserInfo.IsIE) {
			window._FCKHtmlToLoad=A.replace(/<head>/i,'<head>'+I);
            H.src='javascript:void( (function(){document.open() ;document.domain="'+document.domain+'" ;document.write( window.parent._FCKHtmlToLoad );document.close() ;window.parent._FCKHtmlToLoad = null ;})() )';
		} else if (!FCKBrowserInfo.IsGecko) {
            H.src='javascript:void(0)';
        };
		C.appendChild(H);
		this.Window=H.contentWindow;
		if (!FCK_IS_CUSTOM_DOMAIN||!FCKBrowserInfo.IsIE) {
			var J=this.Window.document;
			J.open();
			J.write(A.replace(/<head>/i,'<head>'+I));
            J.close();
		};
		if (FCKBrowserInfo.IsAIR) FCKAdobeAIR.EditingArea_Start(J,A);
		if (FCKBrowserInfo.IsGecko10&&!B) {
			this.Start(A,true);return;
		};
		if (H.readyState&&H.readyState!='completed') {
			var K=this;
            setTimeout(function(){try{K.Window.document.documentElement.doScroll("left");}catch(e){setTimeout(arguments.callee,0);return;};K.Window._FCKEditingArea=K;FCKEditingArea_CompleteStart.call(K.Window);},0);
		} else {
			this.Window._FCKEditingArea=this;
            if (FCKBrowserInfo.IsGecko10) this.Window.setTimeout(FCKEditingArea_CompleteStart,500);
            else FCKEditingArea_CompleteStart.call(this.Window);
		}
	} else {
		var L=this.Textarea=D.createElement('textarea');
        L.className='SourceField';
        L.dir='ltr';
        FCKDomTools.SetElementStyles(L,{width:'100%',height:'100%',border:'none',resize:'none',outline:'none'});
        C.appendChild(L);
        L.value=A;
        FCKTools.RunFunction(this.OnLoad);
	}
};

