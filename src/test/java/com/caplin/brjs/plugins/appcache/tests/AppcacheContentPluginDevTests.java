package com.caplin.brjs.plugins.appcache.tests;

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
	public void testDevCacheManifestIsGenerated() throws Exception
	{
		given(app).hasBeenCreated().and(aspect).hasBeenCreated();
		when(app).requestReceived("/default-aspect/appcache/dev.appcache", pageResponse);
		then(pageResponse).containsText("CACHE MANIFEST");
	}
}
