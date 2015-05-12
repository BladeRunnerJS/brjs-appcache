package org.bladerunnerjs.contrib.contentplugin.appcache.tests;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppcacheTagHandlerPluginTests extends SpecTest
{
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();

	@Before
	public void setUp() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins().and(brjs).hasBeenCreated();
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
	public void tagHasContentWithVersionGiven() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(brjs).hasVersion("1234");
		when(aspect).indexPageLoadedInDev(pageResponse, "en");
		then(pageResponse).containsText("manifest='appcache/dev.appcache'");
	}
	
	@Test
	public void tagHas404FileWhenVersionRemovedAfterPreviouslyHavingVersion() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(brjs).hasVersion("1234");
		when(aspect).indexPageLoadedInDev(new StringBuffer(), "en")
			.and(brjs).hasVersion("")
			.and(aspect).indexPageLoadedInDev(pageResponse, "en");
		then(pageResponse).containsText("manifest='appcache-disabled'");
	}
	
	@Test
	public void tagRemainsDisabledAfterHaving404() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(brjs).hasVersion("1234");

		StringBuffer firstTwoResponses = new StringBuffer();
		when(aspect).indexPageLoadedInDev(firstTwoResponses, "en")
			.and(brjs).hasVersion("")
			.and(aspect).indexPageLoadedInDev(firstTwoResponses, "en")
			.and(aspect).indexPageLoadedInDev(pageResponse, "en");
		
		then(pageResponse).containsText("manifest='appcache-disabled'");
	}
}
