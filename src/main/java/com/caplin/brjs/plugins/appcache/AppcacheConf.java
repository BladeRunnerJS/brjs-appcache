package com.caplin.brjs.plugins.appcache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.ConfFile;
import org.bladerunnerjs.model.exception.ConfigException;

public class AppcacheConf extends ConfFile<YamlAppcacheConf>
{
	private String[] languages;

	public AppcacheConf(BRJSNode node) throws ConfigException
	{
		super(node, YamlAppcacheConf.class, node.file("appcache.conf"));
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

	public String[] getLanguages()
	{
		if (this.languages == null)
		{
			String languagesConfig = conf.languages;
			if (languagesConfig == null || languagesConfig.trim().isEmpty())
			{
				this.languages = new String[0];
			}
			else
			{
				this.languages = splitLanguagesConfig(languagesConfig);
			}
		}

		return this.languages;
	}

	private String[] splitLanguagesConfig(String languagesConfig)
	{
		List<String> splitLanguages = new ArrayList<String>(Arrays.asList(languagesConfig.split(",")));
		int i = 0;
		for (String language : splitLanguages)
		{
			splitLanguages.set(i, language.trim());
			++i;
		}
		splitLanguages.removeAll(Arrays.asList(""));
		return splitLanguages.toArray(new String[splitLanguages.size()]);
	}

}
