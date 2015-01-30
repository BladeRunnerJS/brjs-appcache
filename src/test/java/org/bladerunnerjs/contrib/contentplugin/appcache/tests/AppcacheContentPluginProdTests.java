package org.bladerunnerjs.contrib.contentplugin.appcache.tests;

import org.bladerunnerjs.contrib.contentplugin.appcache.mocks.MockContentPlugin;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppcacheContentPluginProdTests extends SpecTest
{
	private App app;
	private Aspect aspect;
    private MemoizedFile unbundledResources;
	private StringBuffer pageResponse = new StringBuffer();

	@Before
	public void setUp() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
				.and(brjs).automaticallyFindsMinifierPlugins()
				.and(brjs).hasContentPlugins(new MockContentPlugin())
				.and(brjs).hasBeenCreated();
		app = brjs.app("appcacheApp");
		aspect = app.aspect("default");
        unbundledResources = aspect.file("unbundled-resources");
	}

	@After
	public void tearDown()
	{
		app = null;
		aspect = null;
		pageResponse.delete(0, pageResponse.length());
	}

	@Test
	public void testCacheManifestIsGeneratedWithValidConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

        when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("CACHE MANIFEST");
	}

	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}

    @Test
    public void testManifestIsDisabledWhenNoConfig() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated();
        when(app).requestReceived("appcache/prod.appcache", pageResponse);
        then(pageResponse).containsText("# AppCache is currently disabled. Enable it by specifying a version in your appcache.conf file")
                .and(pageResponse).doesNotContainText("../v/prod/prodMock");
    }

    @Test
    public void testManifestIsDisabledWhenEmptyConfig() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "");
        when(app).requestReceived("appcache/prod.appcache", pageResponse);
        then(pageResponse).containsText("# AppCache is currently disabled. Enable it by specifying a version in your appcache.conf file")
                .and(pageResponse).doesNotContainText("../v/prod/prodMock");
    }
	
	@Test
	public void testManifestUsesBlankVersionWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
		.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("# v\n");
	}

	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}

	@Test
	public void testCacheManifestContainsProdMockContent() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/prodMock");
	}
	
	@Test
	public void testCacheManifestContainsProdMockContentWithSpaceReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/prodSpace%20Mock");
	}
	
	@Test
	public void testCacheManifestDoesNotContainItself() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).doesNotContainText("appcache/prod.appcache");
	}

	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
	
	@Test
	public void testCacheManifestDoesNotContainCompositeFiles() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).doesNotContainText("compositeProd");
	}
	
	@Test
	public void testCacheManifestDoesNotListUnusedJSBundles() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageHasContent("<@js.bundle prod-minifier='closure-whitespace' @/>\nrequire('appns/Class1');")
			.and(aspect).hasClass("Class1")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("js/prod/closure-whitespace/bundle.js")
			.and(pageResponse).doesNotContainText("js/prod/combined/bundle.js")
			.and(pageResponse).doesNotContainText("js/prod/closure-simple/bundle.js")
			.and(pageResponse).doesNotContainText("js/prod/closure-advanced/bundle.js");
	}
	
	@Test
	public void testCacheManifestDoesNotListUnusedCssResourceBundles() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsFiles("themes/common/style.css", "theme/common/some-file.txt")
    		.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
			when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).doesNotContainText("some-file.txt");
	}

    @Test
    public void testTimestampReplacedInVersion() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1.2.3-$timestamp");
        when(app).requestReceived("appcache/prod.appcache", pageResponse);
        then(pageResponse).containsText("# v1.2.3-")
                .and(pageResponse).doesNotContainText("$timestamp");
    }

    @Test
    public void testBrjsVersionReplacedInVersion() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1.2.3-$brjsVersion");
        when(app).requestReceived("appcache/prod.appcache", pageResponse);
        then(pageResponse).containsText("# v1.2.3-dev\n")
                .and(pageResponse).doesNotContainText("$brjsVersion");
    }

    @Test
    public void testVersionIsStoredCorrectly() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1.2.3-$brjsVersion");
        when(app).requestReceived("appcache/prod.appcache", pageResponse);
        then(aspect).containsTransientNodeProperty("appcache", "version", "1.2.3-dev");
    }

    @Test
    public void testUnbundledResourcesFilesAreListedCorrectly() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234")
                .and(unbundledResources).containsFile("some-file");
        when(app).requestReceived("appcache/prod.appcache", pageResponse);
        then(pageResponse).containsText("../unbundled-resources/some-file")
                .and(pageResponse).containsText("../v/dev/unbundled-resources/some-file");
    }

}
