/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http.filters;

import org.mule.api.MuleMessage;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpConstants;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestWildcardFilterTestCase extends DynamicPortTestCase
{
    //private static final String HTTP_ENDPOINT = "http://localhost:60201";
    //private static final String REF_ENDPOINT = "http://localhost:60225";
    private static final String TEST_HTTP_MESSAGE = "Hello=World";
    private static final String TEST_BAD_MESSAGE = "xyz";

    protected String getConfigResources()
    {
        return "http-wildcard-filter-test.xml";
    }

    public void testReference() throws Exception
    {
        MuleClient client = new MuleClient();
        MuleMessage result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inReference")).getEndpointURI().getAddress(), TEST_HTTP_MESSAGE, null);
        assertEquals(TEST_HTTP_MESSAGE, result.getPayloadAsString());
    }

    public void testHttpPost() throws Exception
    {
        MuleClient client = new MuleClient();
        MuleMessage result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inHttpIn")).getEndpointURI().getAddress(), TEST_HTTP_MESSAGE, null);
        assertEquals(TEST_HTTP_MESSAGE, result.getPayloadAsString());
    }

    public void testHttpGetNotFiltered() throws Exception
    {
        Map props = new HashMap();
        props.put(HttpConstants.METHOD_GET, "true");
        
        MuleClient client = new MuleClient();
        MuleMessage result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inHttpIn")).getEndpointURI().getAddress() + "/" + "mulerulez", TEST_HTTP_MESSAGE, props);
        assertEquals(TEST_HTTP_MESSAGE, result.getPayloadAsString());
    }

    public void testHttpGetFiltered() throws Exception
    {
        Map props = new HashMap();
        props.put(HttpConstants.METHOD_GET, "true");
        
        MuleClient client = new MuleClient();
        MuleMessage result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inHttpIn")).getEndpointURI().getAddress() + "/" + TEST_BAD_MESSAGE, "mule", props);
        
        assertEquals(HttpConstants.SC_NOT_ACCEPTABLE, result.getIntProperty(HttpConnector.HTTP_STATUS_PROPERTY, 0));
        assertNotNull(result.getExceptionPayload());
    }
    
    @Override
    protected int getNumPortsToFind()
    {
        return 2;
    }
}
