package com.caplin.brjs.plugins.appcache;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

/**
 * Generates content for request appcache manifest files.
 */
public class AppcacheContentPlugin extends AbstractContentPlugin
{

	private final ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		// @formatter:off
		contentPathParserBuilder.accepts("/appcache/dev.appcache").as("dev-appcache-request")
		                        .and("/appcache/prod.appcache").as("prod-appcache-request")
                                .and("appcache/appcache.version").as("appcache-version");
		// @formatter:on

		contentPathParser = contentPathParserBuilder.build();
	}

	@Override
	public String getRequestPrefix()
	{
		return "appcache";
	}

	@Override
	public String getCompositeGroupName()
	{
		return null;
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return getValidContentPaths("dev-appcache-request");
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return getValidContentPaths("prod-appcache-request");
	}

	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin()
	{
		return new ArrayList<String>();
	}

	public List<String> getPluginsThatMustAppearAfterThisPlugin()
	{
		return new ArrayList<String>();
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor urlContent, String brjsVersion) throws ContentProcessingException
	{
		if (!contentPath.formName.equals("dev-appcache-request") && !contentPath.formName.equals("prod-appcache-request") && !contentPath.formName.equals("appcache-version"))
		{
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}

        String content = null;
        String version = null;

        if(contentPath.formName.equals("appcache-version")) {
            boolean isDev = brjsVersion.equals("dev");
            content = getVersion(bundleSet, isDev, brjsVersion);
        } else {
            boolean isDev = contentPath.formName.equals("dev-appcache-request");
            version = getVersion(bundleSet, isDev, brjsVersion);
            AppcacheManifestBuilder manifestBuilder = new AppcacheManifestBuilder(brjs, bundleSet, brjsVersion, version, isDev);

            try {
                content = manifestBuilder.getManifest();
            } catch (ConfigException | PropertiesException | MalformedTokenException e) {
                e.printStackTrace();
            }
        }
		return new CharResponseContent(bundleSet.getBundlableNode().root(), content);
		
	}

	/**************************************
	 * 
	 * Private
	 * 
	 **************************************/

	private List<String> getValidContentPaths(String requestFormName) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		try
		{
			requestPaths.add(contentPathParser.createRequest(requestFormName));
			requestPaths.add(contentPathParser.createRequest("appcache-version"));
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}

    /**
     * Generates a version number to use for the manifest. The version is generated from either:
     * 1) The config file if not empty
     * 2) The BRJS version if not in dev
     * 3) Empty string fallback
     */
    private String getVersion(BundleSet bundleSet, boolean isDev, String brjsVersion)
    {
        String version;

        AppcacheConf config = null;
        try {
            config = new AppcacheConf(bundleSet.getBundlableNode());
        } catch (ConfigException e) {
            e.printStackTrace();
        }

        if (config != null && config.getVersion() != null && !config.getVersion().trim().isEmpty())
        {
            version = config.getVersion();
        }
        else if (!isDev)
        {
            version = brjsVersion;
        }
        else
        {
            version = "";
        }

        return version;
    }

}
