package com.caplin.brjs.plugins.appcache.mocks;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import java.util.Arrays;
import java.util.List;

public class MockCompositeContentPlugin extends AbstractContentPlugin
{
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getRequestPrefix()
	{
		return "mockComposite";
	}

	@Override
	public String getCompositeGroupName()
	{
		return "composite";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		return contentPathParserBuilder.build();
	}


	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return Arrays.asList(new String[] { "compositeDev" });
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return Arrays.asList(new String[] { "compositeProd" });
	}

	@Override
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		return null;
	}
}
