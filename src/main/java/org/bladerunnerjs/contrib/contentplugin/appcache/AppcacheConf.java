package org.bladerunnerjs.contrib.contentplugin.appcache;

import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.ConfFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.model.RequestMode;

public class AppcacheConf extends ConfFile<YamlAppcacheConf>
{
	public AppcacheConf(BundlableNode node) throws ConfigException
	{
		super(node, YamlAppcacheConf.class, node.file("conf/appcache.conf"));
	}

	public String getVersion() throws ConfigException
	{
		return getConf().version;
	}

    public String getVersion(RequestMode requestMode) throws ConfigException
    {
        if(requestMode == RequestMode.Dev)
        {
            return getDevVersion();
        }
        else
        {
            return getVersion();
        }
    }

    public String getDevVersion() throws ConfigException
    {
        return getConf().devVersion;
    }

}
