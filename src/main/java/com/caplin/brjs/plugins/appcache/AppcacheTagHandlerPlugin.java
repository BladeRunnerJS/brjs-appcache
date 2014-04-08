package com.caplin.brjs.plugins.appcache;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

public class AppcacheTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	private ContentPathParser contentPathParser;
	private boolean devAppcachePreviouslyEnabled = false;

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
			String version = getConfiguredVersion(bundleSet.getBundlableNode());
			if (version != null)
			{
				writer.write(contentPathParser.createRequest("dev-appcache-request"));
				devAppcachePreviouslyEnabled = true;
			}
			else if (devAppcachePreviouslyEnabled)
			{
				// http://stackoverflow.com/a/7941620
				// Appcache is only disabled if the manifest does not exist,
				// so we set the tag to something that will return a 404
				writer.write("appcache-404");
				devAppcachePreviouslyEnabled = false;
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
			storeVersionToNodeProperties(bundleSet.getBundlableNode());
			writer.write(contentPathParser.createRequest("prod-appcache-request"));
		}
		catch (MalformedTokenException | ConfigException | PropertiesException e)
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

	/**
	 * Gets the appcache manifest version as configured in the appcache.conf file. If no version is configured this method will return null.
	 * 
	 * @param node
	 *            The BRJSNode that the config should be retrieved for
	 * @return Configured manifest version
	 * @throws ConfigException
	 *             If the version could not be read from the config file
	 */
	private String getConfiguredVersion(BRJSNode node) throws ConfigException
	{
		AppcacheConf conf = new AppcacheConf(node);
		return conf.getVersion();
	}

	/**
	 * Stores the manifest version for the given node and saves it to the persistent property store so it can be retrieved later. The manifest version will be retrieved from the appcache config file for the node if it exists, otherwise it will be a generated unique number for this page request.
	 * 
	 * @param node
	 *            The BRJSNode that the version should be stored for
	 * @throws ConfigException
	 *             If the version could not be read from the config file
	 * @throws PropertiesException
	 *             If the version could not be saved to the properties store
	 */
	private void storeVersionToNodeProperties(BRJSNode node) throws ConfigException, PropertiesException
	{
		String version = getConfiguredVersion(node);
		if (version == null)
		{
			version = Long.toString(System.currentTimeMillis());
		}
		node.nodeProperties("appcache").setPersisentProperty("version", version);
	}

}