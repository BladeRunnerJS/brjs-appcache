package com.caplin.brjs.plugins.appcache.tests;

import com.caplin.brjs.plugins.appcache.mocks.MockCompositeContentPlugin;
import com.caplin.brjs.plugins.appcache.mocks.MockContentPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	public void testCacheManifestIsGeneratedWithValidConfig() throws Exception
	{
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
        then(pageResponse).containsText("CACHE MANIFEST");
	}

	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");

		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}

	@Test
	public void testManifestIsDisabledWhenNoConfig() throws Exception
	{
        given(app).hasBeenCreated().and(aspect).hasBeenCreated();
        when(app).requestReceived("appcache/dev.appcache", pageResponse);
        then(pageResponse).containsText("# AppCache is currently disabled. Enable it by specifying a version in your appcache.conf file")
                .and(pageResponse).doesNotContainText("../v/dev/devMock");
	}

	@Test
	public void testManifestIsDisabledWhenEmptyConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("conf/appcache.conf", "");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
        then(pageResponse).containsText("# AppCache is currently disabled. Enable it by specifying a version in your appcache.conf file")
                .and(pageResponse).doesNotContainText("../v/dev/devMock");
	}
	
	@Test
	public void testManifestUsesBlankVersionWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: ");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v\n");
	}

	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
        when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}

	@Test
	public void testCacheManifestContainsDevMockContent() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/devMock");
	}
	
	@Test
	public void testCacheManifestContainsDevMockContentWithSpaceReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/devSpace%20Mock");
	}
	
	@Test
	public void testCacheManifestDoesNotContainItself() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("appcache/dev.appcache");
	}

	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
        when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
	
	@Test
	public void testCacheManifestDoesContainCompositeFiles() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/compositeDev");
	}

	@Test
	public void testCacheManifestDoesContainDevMockContentWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: ");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../v/dev/devMock");
	}

	@Test
	public void testTimestampReplacedInVersion() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1.2.3-$timestamp");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
        then(pageResponse).containsText("# v1.2.3-")
                .and(pageResponse).doesNotContainText("$timestamp");
	}

	@Test
	public void testBrjsVersionReplacedInVersion() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1.2.3-$brjsVersion");
		when(app).requestReceived("appcache/dev.appcache", pageResponse);
        then(pageResponse).containsText("# v1.2.3-dev\n")
                .and(pageResponse).doesNotContainText("$brjsVersion");
	}

}
