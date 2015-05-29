package org.bladerunnerjs.contrib.contentplugin.appcache.tests;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.contrib.contentplugin.appcache.mocks.MockCompositeContentPlugin;
import org.bladerunnerjs.contrib.contentplugin.appcache.mocks.MockContentPlugin;
import org.junit.Before;
import org.junit.Test;

public class AppcacheContentPluginTests extends SpecTest
{
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();

	@Before
	public void setUp() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
				.and(brjs).automaticallyFindsMinifierPlugins()
				.and(brjs).hasContentPlugins(new MockContentPlugin(), new MockCompositeContentPlugin())
				.and(brjs).hasBeenCreated();
		app = brjs.app("appcacheApp");
		aspect = app.aspect("default");
	}

	@Test
	public void testCacheManifestIsGeneratedWithValidConfig() throws Exception
	{
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
        then(pageResponse).containsText("CACHE MANIFEST");
	}

	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}
	
	@Test
	public void testManifestUsesBlankVersionWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(brjs).hasVersion("");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v\n");
	}

	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
        when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}

	@Test
	public void testCacheManifestContainsMockContent() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/1234/devMock");
	}
	
	@Test
	public void testCacheManifestContainsMockContentWithSpaceReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/1234/devSpace%20Mock");
	}
	
	@Test
	public void testCacheManifestDoesNotContainItself() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("appcache/dev.appcache");
	}

	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
        when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
	
	@Test
	public void testCacheManifestDoesContainCompositeFiles() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/1234/compositeDev");
	}

	@Test
	public void testCacheManifestDoesContainMockContentWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/1234/devMock");
	}
	
	@Test // if the appcache is disabled the manifest should be empty so the browser doesn't load a cached index.html and still attempt to validate against the manifest contents
	public void manifestIsEmptyIfTheCacheIsDisabled() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("../v/");
	}
	
	@Test
	public void prodManifestContainsProdRequests() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(brjs).hasVersion("1234");
		when(app).requestReceived("appcache/prod.appcache", pageResponse);
		then(pageResponse).containsLines("../v/1234/prodMock")
			.and(pageResponse).doesNotContainText("../v/1234/compositeDev");
	}

}
