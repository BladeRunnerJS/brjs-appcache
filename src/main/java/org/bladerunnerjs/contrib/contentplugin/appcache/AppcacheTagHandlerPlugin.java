package org.bladerunnerjs.contrib.contentplugin.appcache;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.ConfigException;
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
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException
	{
		try
		{
			String requestType = (requestMode == RequestMode.Dev) ? "dev-appcache-request" : "prod-appcache-request";
			if (isAppcacheEnabled(bundleSet.getBundlableNode(), requestMode))
			{
				writer.write(".." + contentPathParser.createRequest(requestType));
			}
			else if (requestMode == RequestMode.Prod) {
				return;
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
	public void setBRJS(BRJS brjs)
	{
		this.contentPathParser = brjs.plugins().contentPlugin("appcache").getContentPathParser();
	}

	private boolean isAppcacheEnabled(BundlableNode node, RequestMode requestMode)
	{
        String version = null;
        try {
            AppcacheConf config = new AppcacheConf(node);
            if (config != null)
            {
                version = config.getVersion(requestMode);
            }
        } catch (ConfigException e) {
            e.printStackTrace();
        }

        return version != null;
    }

}