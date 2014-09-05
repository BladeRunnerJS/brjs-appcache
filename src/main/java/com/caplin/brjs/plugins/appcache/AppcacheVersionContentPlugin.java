package com.caplin.brjs.plugins.appcache;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppcacheVersionContentPlugin extends AbstractContentPlugin {

    private final ContentPathParser contentPathParser;
    private BRJS brjs;

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
    public List<String> getPluginsThatMustAppearAfterThisPlugin() {
        return Arrays.asList(CommonJsContentPlugin.class.getCanonicalName());
    }

    @Override
    public ResponseContent handleRequest(ParsedContentPath parsedContentPath, BundleSet bundleSet, UrlContentAccessor urlContentAccessor, String brjsVersion) throws ContentProcessingException {
        if (!parsedContentPath.formName.equals("appcache-version-request"))
        {
            throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
        }

        String version = (String) bundleSet.getBundlableNode().nodeProperties("appcache").getTransientProperty("version");
        String content = "$BRJS_APPCACHE_VERSION = ";

        if(version != null) {
            content += "\"" + version + "\";";
        } else {
            content += "null;";
        }
        return new CharResponseContent(bundleSet.getBundlableNode().root(), content);
    }

    @Override
    public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
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
    public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
        return getValidDevContentPaths(bundleSet, locales);
    }

    @Override
    public void setBRJS(BRJS brjs) {
        this.brjs = brjs;
    }
}
