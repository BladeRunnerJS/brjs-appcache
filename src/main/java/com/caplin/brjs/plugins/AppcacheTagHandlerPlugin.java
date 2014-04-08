package com.caplin.brjs.plugins;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

public class AppcacheTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	private ContentPathParser contentPathParser;

	@Override
	public String getTagName()
	{
		return "appcache.url";
	}

	@Override
	public String getGroupName()
	{
		return null;
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try
		{
			AppcacheConf conf = new AppcacheConf(bundleSet.getBundlableNode());
			String version = conf.getVersion();
			if (version != null && !version.trim().isEmpty())
			{
				writer.write(contentPathParser.createRequest("dev-appcache-request"));
			}
		}
		catch (MalformedTokenException | ConfigException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		try
		{
			writer.write(contentPathParser.createRequest("prod-appcache-request"));
		}
		catch (MalformedTokenException e)
		{
			throw new IOException(e);
		}
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
		this.contentPathParser = brjs.plugins().contentProvider("appcache").getContentPathParser();
	}

}