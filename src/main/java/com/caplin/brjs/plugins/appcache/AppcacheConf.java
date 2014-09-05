package com.caplin.brjs.plugins.appcache;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ConfFile;
import org.bladerunnerjs.model.exception.ConfigException;

public class AppcacheConf extends ConfFile<YamlAppcacheConf>
{
	public AppcacheConf(BundlableNode node) throws ConfigException
	{
		super(node, YamlAppcacheConf.class, node.file("conf/appcache.conf"));
	}

	public String getVersion()
	{
		return conf.version;
	}

    public String getVersion(boolean isDev)
    {
        if(isDev)
        {
            return getDevVersion();
        }
        else
        {
            return getVersion();
        }
    }

    public String getDevVersion()
    {
        return conf.devVersion;
    }

}
