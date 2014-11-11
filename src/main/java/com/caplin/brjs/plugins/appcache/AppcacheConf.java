package com.caplin.brjs.plugins.appcache;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ConfFile;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.ConfigException;

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
