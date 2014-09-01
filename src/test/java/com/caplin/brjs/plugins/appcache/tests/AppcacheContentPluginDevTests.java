package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.brjs.plugins.appcache.mocks.MockCompositeContentPlugin;
import com.caplin.brjs.plugins.appcache.mocks.MockContentPlugin;

public class AppcacheContentPluginDevTests extends SpecTest
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

	@After
	public void tearDown()
	{
		app = null;
		aspect = null;
		pageResponse.delete(0, pageResponse.length());
	}

	@Test
	public void testCacheManifestIsGenerated() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE MANIFEST");
	}

	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}

	@Test
	public void testManifestUsesBlankVersionWhenNoConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v\n");
	}
	
	@Test
	public void testManifestUsesBlankVersionWhenEmptyConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("conf/appcache.conf", "");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v\n");
	}
	
	@Test
	public void testManifestUsesBlankVersionWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v\n");
	}

	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}

	@Test
	public void testCacheManifestContainsDevMockContent() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/devMock");
	}
	
	@Test
	public void testCacheManifestContainsDevMockContentWithSpaceReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/devSpace%20Mock");
	}
	
	@Test
	public void testCacheManifestDoesNotContainItself() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("appcache/dev.appcache");
	}

	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
	
	@Test
	public void testCacheManifestDoesContainCompositeFiles() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/compositeDev");
	}

	@Test
	public void testCacheManifestDoesNotContainDevMockContentWhenNoConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("../v/dev/devMock");
	}

	@Test
	public void testCacheManifestDoesNotContainDevMockContentWhenEmptyConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("../v/dev/devMock");
	}

	@Test
	public void testCacheManifestDoesNotContainDevMockContentWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("../v/dev/devMock");
	}

	@Test
	public void testCacheManifestContainsVersionFile() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/appcache/appcache.version");
	}

	@Test
	public void testCacheManifestContainsCorrectUnversionedFile() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("unbundled-resources/test.json", "{}")
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/appcache/appcache.version");
	}

}
