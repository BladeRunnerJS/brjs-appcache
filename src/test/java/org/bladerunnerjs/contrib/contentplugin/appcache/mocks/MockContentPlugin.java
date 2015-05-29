package org.bladerunnerjs.contrib.contentplugin.appcache.mocks;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import java.util.Arrays;
import java.util.List;

public class MockContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin
{
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getRequestPrefix()
	{
		return "mock";
	}

	@Override
	public String getCompositeGroupName()
	{
		return null;
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		return contentPathParserBuilder.build();
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		if (requestMode == RequestMode.Dev) {
			return Arrays.asList(new String[] { "devMock", "devSpace Mock" });			
		}
		return Arrays.asList(new String[] { "prodMock", "prodSpace Mock" });
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException {
		return null;
	}
}
