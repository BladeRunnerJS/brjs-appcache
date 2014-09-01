package com.caplin.brjs.plugins.appcache.tests;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.brjs.plugins.appcache.mocks.MockCompositeContentPlugin;
import com.caplin.brjs.plugins.appcache.mocks.MockContentPlugin;

public class AppcacheContentPluginVersionTests extends SpecTest
{
    private App app;
    private Aspect aspect;
    private StringBuffer pageResponse = new StringBuffer();

    @Before
    public void setUp() throws Exception
    {
        given(brjs).automaticallyFindsBundlerPlugins()
                .and(brjs).automaticallyFindsMinifierPlugins()
                .and(brjs).hasContentPlugins(new MockContentPlugin(), new MockCompositeContentPlugin())
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
    public void testVersionIsEmptyInDev() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated();
        when(aspect).requestReceivedInDev("appcache/appcache.version", pageResponse);
        then(pageResponse).isEmpty();
    }

    @Test
    public void testVersionIsBrjsVersionInProd() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated();
        when(aspect).requestReceivedInProd("appcache/appcache.version", pageResponse);
        then(pageResponse).textEquals("prod");
    }

    @Test
    public void testVersionIsCorrectInDevWithConfig() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
        when(aspect).requestReceivedInProd("appcache/appcache.version", pageResponse);
        then(pageResponse).textEquals("1234");
    }

    @Test
    public void testVersionIsCorrectInProdWithConfig() throws Exception
    {
        given(app).hasBeenCreated().and(aspect).hasBeenCreated()
            .and(aspect).containsFileWithContents("conf/appcache.conf", "version: 1234");
        when(aspect).requestReceivedInProd("appcache/appcache.version", pageResponse);
        then(pageResponse).textEquals("1234");
    }
}
