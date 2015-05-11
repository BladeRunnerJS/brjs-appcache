package org.bladerunnerjs.contrib.contentplugin.appcache;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.PropertiesException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

/**
 * Generates content for request appcache manifest files.
 */
public class AppcacheContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin
{

	private final ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		// @formatter:off
		contentPathParserBuilder.accepts("/appcache/dev.appcache").as("dev-appcache-request")
		                        .and("/appcache/prod.appcache").as("prod-appcache-request");
		// @formatter:on

		contentPathParser = contentPathParserBuilder.build();
	}

	@Override
	public String getRequestPrefix()
	{
		return "appcache";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		try
		{
			String requestFormName = (requestMode == RequestMode.Dev) ? "dev-appcache-request" : "prod-appcache-request";
			requestPaths.add(contentPathParser.createRequest(requestFormName));
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException
	{
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
		if (!parsedContentPath.formName.equals("dev-appcache-request") && !parsedContentPath.formName.equals("prod-appcache-request"))
		{
			throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
		}

		RequestMode requestMode = (parsedContentPath.formName.equals("dev-appcache-request")) ? RequestMode.Dev : RequestMode.Prod;
        String content = null;
        try {
        	AppcacheManifestBuilder manifestBuilder = new AppcacheManifestBuilder(brjs, bundleSet, version, requestMode);
            content = manifestBuilder.getManifest(requestMode);
        } catch (ConfigException | PropertiesException | MalformedTokenException e) {
            throw new ContentProcessingException(e);
        }
		return new CharResponseContent(bundleSet.bundlableNode().root(), content);
	}

}
