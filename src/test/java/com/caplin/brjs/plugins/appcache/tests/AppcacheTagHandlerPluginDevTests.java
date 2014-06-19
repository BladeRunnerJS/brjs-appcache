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
		given(brjs).automaticallyFindsBundlers().and(brjs).hasBeenCreated();
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

// This test currently fails due to a BRJS issue
// https://github.com/BladeRunnerJS/brjs/issues/524
//	@Test
//	public void appcacheUrlTagIsEmptyWithEmptyConfigFile() throws Exception
//	{
//		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
//			.and(aspect).containsFileWithContents("conf/appcache.conf", "");
//
//		when(aspect).indexPageLoadedInDev(pageResponse, "en");
//
//		then(pageResponse).containsText("manifest=''");
//	}

	@Test
	public void tagIsEmptyWithBlankVersionGiven() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");

		when(aspect).indexPageLoadedInDev(pageResponse, "en");

		then(pageResponse).containsText("manifest=''");
	}

	@Test
	public void tagHasContentWithVersionGiven() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		when(aspect).indexPageLoadedInDev(pageResponse, "en");

		then(pageResponse).containsText("manifest='appcache/dev.appcache'");
	}
	
	@Test
	public void tagHas404FileWhenVersionRemovedAfterPreviouslyHavingVersion() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		StringBuffer firstResponse = new StringBuffer();
		when(aspect).indexPageLoadedInDev(firstResponse, "en")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ")
			.and(aspect).indexPageLoadedInDev(pageResponse, "en");
		
		then(pageResponse).containsText("manifest='appcache-404'");
	}
	
	@Test
	public void tagIsBlankAfterHaving404() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

		StringBuffer firstTwoResponses = new StringBuffer();
		when(aspect).indexPageLoadedInDev(firstTwoResponses, "en")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ")
			.and(aspect).indexPageLoadedInDev(firstTwoResponses, "en")
			.and(aspect).indexPageLoadedInDev(pageResponse, "en");
		
		then(pageResponse).containsText("manifest=''");
	}
}
