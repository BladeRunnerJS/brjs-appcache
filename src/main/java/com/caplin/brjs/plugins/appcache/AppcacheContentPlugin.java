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
		                        .and("/appcache/prod.appcache").as("prod-appcache-request");
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor urlContent, String version) throws ContentProcessingException
	{
		if (!contentPath.formName.equals("dev-appcache-request") && !contentPath.formName.equals("prod-appcache-request"))
		{
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}
		
		AppcacheConf config;
		String manifest = null;
		try {
			config = new AppcacheConf(bundleSet.getBundlableNode());
			boolean isDev = contentPath.formName.equals("dev-appcache-request");
			AppcacheManifestBuilder manifestBuilder = new AppcacheManifestBuilder(brjs, bundleSet, config, version, isDev);
			manifest = manifestBuilder.getManifest();
			
		} catch (ConfigException | PropertiesException | MalformedTokenException e) {
			e.printStackTrace();
		}		
		return new CharResponseContent(bundleSet.getBundlableNode().root(), manifest);
		
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
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}

}
