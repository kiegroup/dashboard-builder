<%@taglib uri="factory.tld" prefix="factory"%>
<%@taglib uri="mvc_taglib.tld" prefix="mvc"%>
<%@taglib uri="bui_taglib.tld" prefix="panel"%>
<%@taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>

<!-- Submit button -->
<tr>
    <td align="center" colspan="2" style="padding:5px">
        <input class="skn-button" type="button" value="Apply changes"
            onClick="return bam_kpiedit_submitProperties(this);">
    </td>
</tr>