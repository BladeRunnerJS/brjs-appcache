package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.brjs.plugins.appcache.mocks.MockContentPlugin;

public class AppcacheContentPluginDevTests extends SpecTest
{
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();

	@Before
	public void setUp() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
				.and(brjs).automaticallyFindsMinifiers()
				.and(brjs).hasContentPlugins(new MockContentPlugin())
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
		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE MANIFEST");
	}

	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}

	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}

	@Test
	public void testCacheManifestContainsDevMockContent() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../devMock");
	}
	
	@Test
	public void testCacheManifestContainsDevMockContentWithSpaceReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("../devSpace%20Mock");
	}
	
	@Test
	public void testCacheManifestDoesNotContainItself() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).doesNotContainText("../appcache/dev.appcache");
	}

	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(aspect).requestReceived("appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
}
