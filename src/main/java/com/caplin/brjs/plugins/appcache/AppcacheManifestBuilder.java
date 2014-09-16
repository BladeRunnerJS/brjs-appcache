package com.caplin.brjs.plugins.appcache;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;

import java.util.List;

/**
 * Builds manifest file strings based on a given set of parameters.
 */
public class AppcacheManifestBuilder
{
	private BRJS brjs;
	private BundleSet bundleSet;
	private String version;
	private String brjsVersion;
	private boolean isDev;

	/**
	 * Creates an {@link AppcacheManifestBuilder} instance for generating prod manifest files.
	 */
	public AppcacheManifestBuilder(BRJS brjs, BundleSet bundleSet, String brjsVersion, String version)
	{
		this(brjs, bundleSet, brjsVersion, version, false);
	}

	/**
	 * Creates an {@link AppcacheManifestBuilder} instance for generating prod or dev manifest
	 * files, depending on the value of the isDev parameter.
	 */
	public AppcacheManifestBuilder(BRJS brjs, BundleSet bundleSet, String brjsVersion, String version, boolean isDev)
	{
		this.brjs = brjs;
		this.bundleSet = bundleSet;
        this.brjsVersion = brjsVersion;
		this.version = version;
		this.isDev = isDev;
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
	public String getManifest() throws PropertiesException, ContentProcessingException, ConfigException, MalformedTokenException
	{
		String manifest = getManifestHeader();
        if(version != null)
        {
            // If the version is null, then the appcache should be disabled. We're only refreshing the manifest so that
            // the new index page without the manifest attribute will be picked up, and we don't really want it to re-cache
            // all the other files, so we leave them out of the manifest
            manifest += getManifestCacheFiles();
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

	private String getManifestCacheFiles() throws ContentProcessingException, ConfigException, MalformedTokenException, PropertiesException {
		StringBuilder cacheFiles = new StringBuilder();
        cacheFiles.append("CACHE:\n");

        // See #getContentPaths for an explanation on why we need configured languages
        Locale[] languages = getConfiguredLocales();
        for (ContentPlugin plugin : brjs.plugins().contentPlugins())
        {
            String pluginCacheFiles = getManifestCacheFilesForPlugin(plugin, languages);
            cacheFiles.append(pluginCacheFiles);
        }
        cacheFiles.append("\n");

		return cacheFiles.toString();
	}

	private String getManifestCacheFilesForPlugin(ContentPlugin plugin, Locale[] languages) throws ContentProcessingException, MalformedTokenException
	{
		// Don't specify plugins that are part of a composite in the manifest in prod;
		// these files are already bundled inside the composite file and don't need to be
		// also cached (in fact they are left out of the built prod app and cant be cached)
		if (!isDev && plugin.getCompositeGroupName() != null)
		{
			return "";
		}

		StringBuilder cacheFiles = new StringBuilder();
		App app = bundleSet.getBundlableNode().app();
		for (String contentPath : getContentPaths(plugin, languages))
		{
            // Do not specify the manifest itself in the cache manifest file, otherwise it
            // will be nearly impossible to inform the browser a new manifest is available.
            if(plugin.instanceOf(AppcacheContentPlugin.class) && (contentPath.equals("/appcache/dev.appcache") || contentPath.equals("/appcache/prod.appcache"))) {
                continue;
            }
			String path = (isDev) ? app.createDevBundleRequest(contentPath, brjsVersion) : app.createProdBundleRequest(contentPath, brjsVersion);
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
		Locale[] locales = bundleSet.getBundlableNode().app().appConf().getLocales();

		if (locales.length == 0)
		{

			locales = new Locale[] { new Locale("en") };
		}

		return locales;
	}

	/**
	 * Gets the content paths provided by a particular plugin, for a given set of locales.
	 */
	private List<String> getContentPaths(ContentPlugin plugin, Locale[] languages) throws ContentProcessingException
	{
		// TODO Any language-specific files requested by browsers set to a language not in the
		// locale list will not appear in the appcache manifest, and will not be cached. The
		// application will still work, it's just that some files will have to be requested over the
		// network every time.
		// This should be fixable when flat-file export is added to BRJS, as the mechanism with
		// which language specific files are generated will have changed.
		// In the meantime we let the developer specify required languages in the appcache.conf
		// file, or assume "en" as a default if none are provided.

		List<String> contentPaths;
		if (isDev)
		{
			contentPaths = plugin.getValidDevContentPaths(bundleSet, languages);
		}
		else
		{
			contentPaths = plugin.getValidProdContentPaths(bundleSet, languages);
		}
		return contentPaths;
	}

	/**
	 * Generates the manifest NETWORK section string
	 */
	private String getManifestNetworkFiles()
	{
		return "NETWORK:\n*";
	}
}
