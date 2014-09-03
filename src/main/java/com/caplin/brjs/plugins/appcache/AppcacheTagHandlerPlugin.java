package com.caplin.brjs.plugins.appcache;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

/**
 * Generates an appcache manifest URL when the "appcache.url" tag is used.
 */
public class AppcacheTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	private ContentPathParser contentPathParser;
	private boolean devAppcachePreviouslyEnabled = false;

    public static String currentVersion = null;

	@Override
	public String getTagName()
	{
		return "appcache.url";
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
            this.currentVersion = getConfiguredVersion(bundleSet.getBundlableNode(), true, version);
            System.out.println("Dev: " + this.currentVersion);
			// We enable appcache in dev by populating the tag if there's a config file with a
			// version specified
			if (this.currentVersion != null)
			{
				writer.write(".." + contentPathParser.createRequest("dev-appcache-request"));
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
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
            String appcacheVersion = getConfiguredVersion(bundleSet.getBundlableNode(), false, version);
            if(appcacheVersion != null)
            {
			    writer.write(".." + contentPathParser.createRequest("prod-appcache-request"));
            }
		}
		catch (MalformedTokenException | ConfigException e)
		{
			throw new IOException(e);
		}
    }

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.contentPathParser = brjs.plugins().contentPlugin("appcache").getContentPathParser();
	}

	/**
	 * Gets the appcache manifest version as configured in the appcache.conf file. If no version is
	 * configured this method will return null.
	 * 
	 * @param node
	 *            The BRJSNode that the config should be retrieved for
	 * @return Configured manifest version
	 * @throws ConfigException
	 *             If the version could not be read from the config file
	 */
	private String getConfiguredVersion(BundlableNode node, boolean isDev, String brjsVersion) throws ConfigException
	{
        AppcacheConf config = null;
        try {
            config = new AppcacheConf(node);
        } catch (ConfigException e) {
            e.printStackTrace();
        }

        String version;
        if (config != null && config.getVersion(isDev) != null)
        {
            version = config.getVersion(isDev);
            version = version.replaceAll("\\$timestamp", "" + new Date().getTime());
            version = version.replaceAll("\\$brjsVersion", brjsVersion);
        }
        else
        {
            version = "";
        }

        return version;
	}

}