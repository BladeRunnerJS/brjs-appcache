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
	 * Creates an {@link AppcacheManifestBuilder} instance for generating prod manifest files.
	 */
	public AppcacheManifestBuilder(BRJS brjs, BundleSet bundleSet, String version)
	{
		this(brjs, bundleSet, version, RequestMode.Prod);
	}

	/**
	 * Creates an {@link AppcacheManifestBuilder} instance for generating prod or dev manifest
	 * files, depending on the value of the isDev parameter.
	 */
	public AppcacheManifestBuilder(BRJS brjs, BundleSet bundleSet, String version, RequestMode requestMode)
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
		String manifest = getManifestHeader();
        if(version != null)
        {
            // If the version is null, then the appcache should be disabled. We're only refreshing the manifest so that
            // the new index page without the manifest attribute will be picked up, and we don't really want it to re-cache
            // all the other files, so we leave them out of the manifest
            manifest += getManifestCacheFiles(requestMode);
            manifest += getManifestNetworkFiles();
        }
		return manifest;
	}

	/**************************************
	 * 
	 * Private
	 * 
	 **************************************/

	private String getManifestHeader() throws PropertiesException
	{
        String header = "CACHE MANIFEST\n";
        if(version != null)
        {
            header += "# v" + version + "\n\n";
        } else {
            header += "# AppCache is currently disabled. Enable it by specifying a version in your appcache.conf file";
        }
		return header;
	}

	private String getManifestCacheFiles(RequestMode requestMode) throws ContentProcessingException, ConfigException, MalformedTokenException, PropertiesException {
		StringBuilder cacheFiles = new StringBuilder();
        cacheFiles.append("CACHE:\n");

        // See #getContentPaths for an explanation on why we need configured languages
        Locale[] languages = getConfiguredLocales();
        for (ContentPlugin plugin : brjs.plugins().contentPlugins())
        {
            String pluginCacheFiles = getManifestCacheFilesForPlugin(requestMode, plugin, languages);
            cacheFiles.append(pluginCacheFiles);
        }
        cacheFiles.append("\n");

		return cacheFiles.toString();
	}

	private String getManifestCacheFilesForPlugin(RequestMode requestMode, ContentPlugin plugin, Locale[] languages) throws ContentProcessingException, MalformedTokenException
	{
		// Don't specify plugins that are part of a composite in the manifest in prod;
		// these files are already bundled inside the composite file and don't need to be
		// also cached (in fact they are left out of the built prod app and cant be cached)
		boolean isDev = requestMode == RequestMode.Dev;
		
		if (!isDev && plugin.instanceOf(CompositeContentPlugin.class) && (plugin.castTo(CompositeContentPlugin.class)).getCompositeGroupName() != null)
		{
			return "";
		}

		StringBuilder cacheFiles = new StringBuilder();
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		if (!(bundlableNode instanceof Aspect)) {
			return "";
		}
		App app = bundleSet.bundlableNode().app();
		Aspect aspect  = (Aspect) bundlableNode;
		for (String contentPath : plugin.getUsedContentPaths(bundleSet, requestMode, languages))
		{
            // Do not specify the manifest itself in the cache manifest file, otherwise it
            // will be nearly impossible to inform the browser a new manifest is available.
            if(plugin.instanceOf(AppcacheContentPlugin.class) && (contentPath.equals("/appcache/dev.appcache") || contentPath.equals("/appcache/prod.appcache"))) {
                continue;
            }
			String path = app.requestHandler().createBundleRequest(aspect, contentPath, version);
			// Spaces need to be URL encoded or the manifest doesnt load the files correctly
			path = path.replaceAll(" ", "%20");
			// Path begins with .. as paths are relative to the manifest file,
			// and the manifest is in the "appcache/" directory
			cacheFiles.append("../" + path + "\n");
		}
		return cacheFiles.toString();
	}

	/**
	 * Gets the list of configured languages from the config file. If none are configured a default
	 * of "en" will be used
	 */
	private Locale[] getConfiguredLocales() throws ConfigException
	{
		Locale[] locales = bundleSet.bundlableNode().app().appConf().getLocales();

		if (locales.length == 0)
		{

			locales = new Locale[] { new Locale("en") };
		}

		return locales;
	}

	/**
	 * Generates the manifest NETWORK section string
	 */
	private String getManifestNetworkFiles()
	{
		return "NETWORK:\n*";
	}
}
