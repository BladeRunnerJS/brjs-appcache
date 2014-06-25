package com.caplin.brjs.plugins.appcache;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
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
		contentPathParserBuilder.accepts("appcache/dev.appcache").as("dev-appcache-request")
		                        .and("appcache/prod.appcache").as("prod-appcache-request");
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
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return getValidContentPaths("dev-appcache-request");
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return getValidContentPaths("prod-appcache-request");
	}

	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin()
	{
		return new ArrayList<String>();
	}

	@Override
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
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os, String version) throws ContentProcessingException
	{
		if (!contentPath.formName.equals("dev-appcache-request") && !contentPath.formName.equals("prod-appcache-request"))
		{
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}

		try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
		{
			AppcacheConf config = new AppcacheConf(bundleSet.getBundlableNode());
			boolean isDev = contentPath.formName.equals("dev-appcache-request");
			AppcacheManifestBuilder manifestBuilder = new AppcacheManifestBuilder(brjs, bundleSet, config, version, isDev);
			String manifest = manifestBuilder.getManifest();

			writer.write(manifest);
		}
		catch (IOException | ConfigException | PropertiesException e)
		{
			throw new ContentProcessingException(e);
		}
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
