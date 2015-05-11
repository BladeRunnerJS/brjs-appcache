package org.bladerunnerjs.contrib.contentplugin.appcache;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;
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
			if (isAppcacheEnabled(bundleSet.bundlableNode(), requestMode))
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
		Plugin appCachePlugin = (VirtualProxyContentPlugin) brjs.plugins().contentPlugin("appcache");
		this.contentPathParser = (appCachePlugin.castTo(RoutableContentPlugin.class)).getContentPathParser();
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