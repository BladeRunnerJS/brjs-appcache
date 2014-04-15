package com.caplin.brjs.plugins.appcache.tests;

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
	private StringBuffer pageResponse = new StringBuffer();

	@Before
	public void setUp() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
				.and(brjs).automaticallyFindsMinifiers()
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
		when(app).requestReceived("/default-aspect/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("CACHE MANIFEST");
	}
	
	@Test
	public void testManifestUsesVersionFromConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated()
				.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		when(app).requestReceived("/default-aspect/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("# v1234\n");
	}

	@Test
	public void testManifestUsesVersionFromNodePropertiesWhenNoConfig() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		aspect.nodeProperties("appcache").setPersisentProperty("version", "5678");

		when(app).requestReceived("/default-aspect/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("# v5678\n");
	}
	
	@Test
	public void testCacheManifestContainsCacheSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("CACHE:");
	}
	
	@Test
	public void testCacheManifestContainsNetworkSection() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/appcache/prod.appcache", pageResponse);
		then(pageResponse).containsText("NETWORK:");
	}
	
}
