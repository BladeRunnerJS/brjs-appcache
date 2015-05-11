package org.bladerunnerjs.contrib.contentplugin.appcache;

import org.bladerunnerjs.api.BRJSNode;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.ConfigValidationChecker;
import org.bladerunnerjs.yaml.AbstractYamlConfFile;

public class YamlAppcacheConf extends AbstractYamlConfFile
{

	public String version;
	public String devVersion;

	@Override
	public void verify() throws ConfigException
	{
		ConfigValidationChecker.validate(this);
	}

	@Override
	public void initialize(BRJSNode node) {
		// TODO Auto-generated method stub
		
	}

}
