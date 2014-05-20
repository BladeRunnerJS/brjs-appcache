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
		String version = conf.version;
		if (version != null && version.trim().isEmpty())
		{
			version = null;
		}
		return version;
	}

}
