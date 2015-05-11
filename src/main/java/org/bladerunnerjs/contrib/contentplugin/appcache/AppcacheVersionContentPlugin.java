package org.bladerunnerjs.contrib.contentplugin.appcache;


import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class AppcacheVersionContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin {

    private final ContentPathParser contentPathParser;

    {
        ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
        contentPathParserBuilder.accepts("appcache-js/version.js").as("appcache-version-request");
        contentPathParser = contentPathParserBuilder.build();
    }

    @Override
    public String getRequestPrefix() {
        return "appcache-js";
    }

    @Override
    public String getCompositeGroupName() {
        return "text/javascript";
    }

    @Override
    public ContentPathParser getContentPathParser() {
        return contentPathParser;
    }

    @Override
    public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String brjsVersion) throws MalformedRequestException, ContentProcessingException {
    	ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
        if (!parsedContentPath.formName.equals("appcache-version-request"))
        {
            throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
        }

        String version = (String) bundleSet.bundlableNode().nodeProperties("appcache").getTransientProperty("version");
        String content = "window.$BRJS_APPCACHE_VERSION = ";

        if(version != null) {
            content += "\"" + version + "\";";
        } else {
            content += "null;";
        }
        return new CharResponseContent(bundleSet.bundlableNode().root(), content);
    }

    @Override
    public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
        List<String> requestPaths = new ArrayList<>();
        try
        {
            requestPaths.add(contentPathParser.createRequest("appcache-version-request"));
        }
        catch (MalformedTokenException e)
        {
            throw new ContentProcessingException(e);
        }
        return requestPaths;
    }

    @Override
    public void setBRJS(BRJS brjs) {
    }
}
