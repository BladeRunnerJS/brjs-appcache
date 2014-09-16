package com.caplin.brjs.plugins.appcache;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Generates an appcache manifest URL when the "appcache.url" tag is used.
 */
public class AppcacheTagHandlerPlugin extends AbstractTagHandlerPlugin
{

	private ContentPathParser contentPathParser;

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
			if (isAppcacheEnabled(bundleSet.getBundlableNode(), true))
			{
				writer.write(".." + contentPathParser.createRequest("dev-appcache-request"));
			}
			else
			{
                // http://stackoverflow.com/a/7941620
                // Appcache is only disabled if the manifest does not exist,
                // so we set the tag to something that will return a 404
                writer.write("appcache-disabled");
			}
		}
		catch (MalformedTokenException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
            if(isAppcacheEnabled(bundleSet.getBundlableNode(), false))
            {
			    writer.write(".." + contentPathParser.createRequest("prod-appcache-request"));
            }
		}
		catch (MalformedTokenException e)
		{
			throw new IOException(e);
		}
    }

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.contentPathParser = brjs.plugins().contentPlugin("appcache").getContentPathParser();
	}

	private boolean isAppcacheEnabled(BundlableNode node, boolean isDev)
	{
        String version = null;
        try {
            AppcacheConf config = new AppcacheConf(node);
            if (config != null)
            {
                version = config.getVersion(isDev);
            }
        } catch (ConfigException e) {
            e.printStackTrace();
        }

        return version != null;
    }

}