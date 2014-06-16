package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppcacheTagHandlerPluginProdTests extends SpecTest
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
	public void tagIsPresentWithNoConfigFile() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'");

		when(aspect).indexPageLoadedInProd(pageResponse, "en");

		then(pageResponse).containsText("manifest='appcache/prod.appcache'");
	}
	
	@Test
	public void generatedVersionIsSavedToNodePropertiesWithNoConfigFile() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'");

		when(aspect).indexPageLoadedInProd(pageResponse, "en");

		then(aspect).containsPersistentNodeProperty("appcache", "version", "prod");
	}

	@Test
	public void tagIsPresentWithEmptyConfigFile() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "");

		when(aspect).indexPageLoadedInProd(pageResponse, "en");

		then(pageResponse).containsText("manifest='appcache/prod.appcache'");
	}

	@Test
	public void generatedVersionIsSavedToNodePropertiesWithEmptyConfigFile() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "");

		when(aspect).indexPageLoadedInProd(pageResponse, "en");

		then(aspect).containsPersistentNodeProperty("appcache", "version", "prod");
	}
	
	@Test
	public void tagIsPresentWithConfigFileWithNoVersionValue() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");

		when(aspect).indexPageLoadedInProd(pageResponse, "en");

		then(pageResponse).containsText("manifest='appcache/prod.appcache'");
	}
	
	@Test
	public void generatedVersionIsSavedWithConfigFileWithNoVersionValue() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
			.and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");

		when(aspect).indexPageLoadedInProd(pageResponse, "en");

		then(aspect).containsPersistentNodeProperty("appcache", "version", "prod");
	}
	
	@Test
	public void tagIsPresentWithConfigFileWithVersionSet() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
    		.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
    
    	when(aspect).indexPageLoadedInProd(pageResponse, "en");
    
    	then(pageResponse).containsText("manifest='appcache/prod.appcache'");
	}
	
	@Test
	public void specifiedVersionIsSavedWithConfigFileWithVersionSet() throws Exception
	{
		given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
		.and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
		
		when(aspect).indexPageLoadedInProd(pageResponse, "en");
		
		then(aspect).containsPersistentNodeProperty("appcache", "version", "1234");
	}
}
