/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http.functional;

import org.mule.api.MuleMessage;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;
import org.mule.transport.http.HttpConnector;

public class ChunkingTestCase extends DynamicPortTestCase
{

    protected String getConfigResources()
    {
        return "chunking-test.xml";
    }

    public void testPartiallyReadRequest() throws Exception
    {
        MuleClient client = new MuleClient();
        
        byte[] msg = new byte[100*1024];
        
        MuleMessage result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inMain")).getEndpointURI().getAddress(), 
            msg, null);
        assertEquals("Hello", result.getPayloadAsString());
        assertEquals(200, result.getIntProperty(HttpConnector.HTTP_STATUS_PROPERTY, 0));
        
        result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inMain")).getEndpointURI().getAddress(),
            msg, null);
        assertEquals("Hello", result.getPayloadAsString());
        assertEquals(200, result.getIntProperty(HttpConnector.HTTP_STATUS_PROPERTY, 0));
    }

    @Override
    protected int getNumPortsToFind()
    {
        return 1;
    }

}


