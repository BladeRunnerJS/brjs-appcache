package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppcacheVersionContentPluginTests extends SpecTest
{
    private App app;
    private Aspect aspect;
    private StringBuffer pageResponse = new StringBuffer();

    @Before
    public void setUp() throws Exception
    {
        given(brjs).automaticallyFindsBundlerPlugins()
                .and(brjs).automaticallyFindsMinifierPlugins()
                .and(brjs).hasContentPlugins()
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
    public void testVersionIsNullIfNoConfigInDev() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(aspect).hasTransientNodeProperty("appcache", "version", null);
        when(aspect).requestReceivedInDev("appcache-js/version.js", pageResponse);
        then(pageResponse).containsText("window.$BRJS_APPCACHE_VERSION = null;");
    }

    @Test
    public void testVersionIsNullIfNoConfigInProd() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(aspect).hasTransientNodeProperty("appcache", "version", null);
        when(aspect).requestReceivedInProd("appcache-js/version.js", pageResponse);
        then(pageResponse).containsText("window.$BRJS_APPCACHE_VERSION = null;");
    }

    @Test
    public void testVersionIsCorrectWhenConfigSetInDev() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).hasTransientNodeProperty("appcache", "version", "1.2.3");
        when(aspect).requestReceivedInDev("appcache-js/version.js", pageResponse);
        then(pageResponse).containsText("window.$BRJS_APPCACHE_VERSION = \"1.2.3\";");
    }

    @Test
    public void testVersionIsCorrectWhenConfigSetInProd() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
                .and(aspect).hasTransientNodeProperty("appcache", "version", "1.2.3");
        when(aspect).requestReceivedInProd("appcache-js/version.js", pageResponse);
        then(pageResponse).containsText("window.$BRJS_APPCACHE_VERSION = \"1.2.3\";");
    }
}
