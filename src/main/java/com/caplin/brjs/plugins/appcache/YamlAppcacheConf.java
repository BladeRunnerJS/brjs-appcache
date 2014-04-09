package com.caplin.brjs.plugins.appcache;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;

public class YamlAppcacheConf extends AbstractYamlConfFile
{

	public String version;
	public String languages;

	@Override
	public void initialize()
	{
		version = null;
		languages = null;
	}

	@Override
	public void verify() throws ConfigException
	{
		ConfigValidationChecker.validate(this);
	}

}
