package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.brjs.plugins.appcache.mocks.MockContentPlugin;

public class AppcacheContentPluginProdTests extends SpecTest
{
	private App app;
	private Aspect aspect;
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
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("CACHE MANIFEST");
	}

	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}

	@Test
	public void testManifestUsesBrjsVersionWhenNoConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		// The version here is 'dev' because the BRJS spec test framework currently always passes in 'dev' 
		// as the version to the ContentPlugin.writeContent method. We know that we're not actually in dev
		// mode because then the manifest would have a blank version (see the equivalent test in 
		// AppcacheContentPluginDevTests)
		then(pageResponse).containsText("# vdev\n");
	}
	
	@Test
	public void testManifestUsesBrjsVersionWhenEmptyConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("conf/appcache.conf", "");
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		// The version here is 'dev' because the BRJS spec test framework currently always passes in 'dev' 
		// as the version to the ContentPlugin.writeContent method. We know that we're not actually in dev
		// mode because then the manifest would have a blank version (see the equivalent test in 
		// AppcacheContentPluginDevTests)
		then(pageResponse).containsText("# vdev\n");
	}
	
	@Test
	public void testManifestUsesBrjsVersionWhenBlankConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
		.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		// The version here is 'dev' because the BRJS spec test framework currently always passes in 'dev' 
		// as the version to the ContentPlugin.writeContent method. We know that we're not actually in dev
		// mode because then the manifest would have a blank version (see the equivalent test in 
		// AppcacheContentPluginDevTests)
		then(pageResponse).containsText("# vdev\n");
	}

	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}

	@Test
	public void testCacheManifestContainsProdMockContent() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("../../v/dev/prodMock");
	}
	
	@Test
	public void testCacheManifestContainsProdMockContentWithSpaceReplaced() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("../../v/dev/prodSpace%20Mock");
	}
	
	@Test
	public void testCacheManifestDoesNotContainItself() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).doesNotContainText("appcache/prod.appcache");
	}

	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("static/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
}
