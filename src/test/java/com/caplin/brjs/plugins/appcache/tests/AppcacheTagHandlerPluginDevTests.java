package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppcacheTagHandlerPluginDevTests extends SpecTest
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
	public void tagIsEmptyWithNoConfigFile() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'");

		when(aspect).indexPageLoadedInDev(pageResponse, "en");

		then(pageResponse).containsText("manifest=''");
	}

	@Test
	public void appcacheUrlTagIsEmptyWithEmptyConfigFile() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "");

		when(aspect).indexPageLoadedInDev(pageResponse, "en");

		then(pageResponse).containsText("manifest=''");
	}

	@Test
	public void tagHasContentWithBlankVersionGiven() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: ");

		when(aspect).indexPageLoadedInDev(pageResponse, "en");

		then(pageResponse).containsText("manifest='../appcache/dev.appcache'");
	}

	@Test
	public void tagHasContentWithVersionGiven() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");

		when(aspect).indexPageLoadedInDev(pageResponse, "en");

		then(pageResponse).containsText("manifest='../appcache/dev.appcache'");
	}
	
	@Test
	public void tagHas404FileWhenVersionRemovedAfterPreviouslyHavingVersion() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");

		when(aspect).indexPageLoadedInDev(new StringBuffer(), "en")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "")
			.and(aspect).indexPageLoadedInDev(pageResponse, "en");
		
		then(pageResponse).containsText("manifest='appcache-disabled'");
	}
	
	@Test
	public void tagIsBlankAfterHaving404() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "devVersion: 1234");

		StringBuffer firstTwoResponses = new StringBuffer();
		when(aspect).indexPageLoadedInDev(firstTwoResponses, "en")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "")
			.and(aspect).indexPageLoadedInDev(firstTwoResponses, "en")
			.and(aspect).indexPageLoadedInDev(pageResponse, "en");
		
		then(pageResponse).containsText("manifest=''");
	}
}
