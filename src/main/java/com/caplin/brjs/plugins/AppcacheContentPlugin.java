package com.caplin.brjs.plugins;

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
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

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
	public String getGroupName()
	{
		return null;
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
		if (!contentPath.formName.equals("dev-appcache-request") && !contentPath.formName.equals("prod-appcache-request"))
		{
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}

		try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
		{
			writeHeader(writer, bundleSet);
			writeCacheFiles(writer, bundleSet);
			writeNetwork(writer);
		}
		catch (IOException | ConfigException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();

		try
		{
			requestPaths.add(contentPathParser.createRequest("dev-appcache-request"));
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();

		try
		{
			requestPaths.add(contentPathParser.createRequest("prod-appcache-request"));
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
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

	private void writeHeader(Writer writer, BundleSet bundleSet) throws IOException, ConfigException
	{
		AppcacheConf conf = new AppcacheConf(bundleSet.getBundlableNode());
		String version = conf.getVersion();
		if (version == null || version.trim().isEmpty())
		{
			version = Long.toString(System.currentTimeMillis());
		}

		writer.write("CACHE MANIFEST\n# v" + version + "\n\n");
	}

	private void writeCacheFiles(Writer writer, BundleSet bundleSet) throws IOException, ContentProcessingException
	{
		writer.write("CACHE:\n");

		for (ContentPlugin plugin : brjs.plugins().contentProviders())
		{
			// Do not specify the manifest itself in the cache manifest file, otherwise it
			// will be nearly impossible to inform the browser a new manifest is available
			if (plugin.instanceOf(AppcacheContentPlugin.class))
			{
				continue;
			}

			for (String path : plugin.getValidDevContentPaths(bundleSet))
			{
				// Path begins with .. as paths are relative to the manifest file,
				// and the manifest is in the "appcache/" directory
				writer.write("../" + path + "\n");
			}
		}

		writer.write("\n");
	}

	private void writeNetwork(Writer writer) throws IOException
	{
		writer.write("\nNETWORK:\n*");
	}

}
