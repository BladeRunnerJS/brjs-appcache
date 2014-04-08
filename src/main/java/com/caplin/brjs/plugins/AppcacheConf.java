package com.caplin.brjs.plugins;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.ConfFile;
import org.bladerunnerjs.model.exception.ConfigException;

public class AppcacheConf extends ConfFile<YamlAppcacheConf>
{

	public AppcacheConf(BRJSNode node) throws ConfigException
	{
		super(node, YamlAppcacheConf.class, node.file("appcache.conf"));
	}

	public String getVersion()
	{
		return conf.version;
	}

}
