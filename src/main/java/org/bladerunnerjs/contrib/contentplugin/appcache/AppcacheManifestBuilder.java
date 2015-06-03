package org.bladerunnerjs.contrib.contentplugin.appcache;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.PropertiesException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.RequestMode;

/**
 * Builds manifest file strings based on a given set of parameters.
 */
public class AppcacheManifestBuilder
{
	private BRJS brjs;
	private BundleSet bundleSet;
	private String version;

	/**
	 * Creates an {@link AppcacheManifestBuilder} instance for generating prod or dev manifest
	 * files, depending on the value of the isDev parameter.
	 */
	public AppcacheManifestBuilder(BRJS brjs, BundleSet bundleSet, String version)
	{
		this.brjs = brjs;
		this.bundleSet = bundleSet;
		this.version = version;
	}

	/**
	 * Generates the manifest file as a string.
	 * 
	 * @return The manifest file
	 * @throws PropertiesException
	 * @throws ContentProcessingException
	 * @throws ConfigException
	 * @throws MalformedTokenException
	 */
	public String getManifest(RequestMode requestMode) throws PropertiesException, ContentProcessingException, ConfigException, MalformedTokenException
	{
		StringBuilder manifest = new StringBuilder();
		appendManifestHeader(manifest);
		appendManifestCacheFiles(manifest, requestMode);
		appendManifestNetworkFiles(manifest);
		return manifest.toString(); 
	}

	/**************************************
	 * 
	 * Private
	 * 
	 **************************************/

	private void appendManifestHeader(StringBuilder manifest) throws PropertiesException
	{
        manifest.append("CACHE MANIFEST\n");
        manifest.append("# version " + version + "\n\n");
	}

	private void appendManifestCacheFiles(StringBuilder manifest, RequestMode requestMode) throws ContentProcessingException, ConfigException, MalformedTokenException, PropertiesException {
		manifest.append("CACHE:\n");

        // See #getContentPaths for an explanation on why we need configured languages
        for (ContentPlugin plugin : brjs.plugins().contentPlugins())
        {
            appendManifestCacheFilesForPlugin(manifest, requestMode, plugin, bundleSet.bundlableNode().app().appConf().getLocales());
        }
        manifest.append("\n");
	}

	private void appendManifestCacheFilesForPlugin(StringBuilder manifest, RequestMode requestMode, ContentPlugin plugin, Locale[] languages) throws ContentProcessingException, MalformedTokenException
	{
		// Don't specify plugins that are part of a composite in the manifest in prod;
		// these files are already bundled inside the composite file and don't need to be
		// also cached (in fact they are left out of the built prod app and cant be cached)
		boolean isDev = requestMode == RequestMode.Dev;
		
		if ( !isDev && (plugin.instanceOf(CompositeContentPlugin.class) && (plugin.castTo(CompositeContentPlugin.class)).getCompositeGroupName() != null) )
		{
			return;
		}
		
		if ( version.equals("dev") || version.length() == 0 ) {
			return;
		}
		
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		if (!(bundlableNode instanceof Aspect)) {
			return;
		}
		App app = bundleSet.bundlableNode().app();
		Aspect aspect  = (Aspect) bundlableNode;
		for (String contentPath : plugin.getUsedContentPaths(bundleSet, requestMode, languages))
		{
            // Do not specify the manifest itself in the cache manifest file, otherwise it
            // will be nearly impossible to inform the browser a new manifest is available.
            if (plugin.instanceOf(AppcacheContentPlugin.class) && (contentPath.startsWith("/appcache/"))) {
                continue;
            }
			String path = app.requestHandler().createBundleRequest(aspect, contentPath, version);
			// Spaces need to be URL encoded or the manifest doesnt load the files correctly
			path = path.replaceAll(" ", "%20");
			// Path begins with .. as paths are relative to the manifest file,
			// and the manifest is in the "appcache/" directory
			if (path.startsWith("/")) {
				manifest.append(".." + path + "\n");
			} else {
				manifest.append("../" + path + "\n");				
			}
		}
	}

	/**
	 * Generates the manifest NETWORK section string
	 */
	private void appendManifestNetworkFiles(StringBuilder manifest)
	{
		manifest.append("NETWORK:\n*");
	}
}
