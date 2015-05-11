package org.bladerunnerjs.contrib.contentplugin.appcache.tests;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
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

        when(aspect).indexPageLoadedInProd(pageResponse, "en");

        then(pageResponse).containsText("manifest=''");
    }

    @Test
    public void appcacheUrlTagIsEmptyWithEmptyConfigFile() throws Exception
    {
        given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
                .and(aspect).containsFileWithContents("conf/appcache.conf", "");

        when(aspect).indexPageLoadedInProd(pageResponse, "en");

        then(pageResponse).containsText("manifest=''");
    }

    @Test
    public void tagHasContentWithBlankVersionGiven() throws Exception
    {
        given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: ");

        when(aspect).indexPageLoadedInProd(pageResponse, "en");

        then(pageResponse).containsText("manifest='../appcache/prod.appcache'");
    }

    @Test
    public void tagHasContentWithVersionGiven() throws Exception
    {
        given(aspect).indexPageHasContent("manifest='<@appcache.url@/>'")
                .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");

        when(aspect).indexPageLoadedInProd(pageResponse, "en");

        then(pageResponse).containsText("manifest='../appcache/prod.appcache'");
    }
}
