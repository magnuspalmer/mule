/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.tcp.issues;

import org.mule.api.MuleMessage;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.module.client.MuleClient;
import org.mule.tck.DynamicPortTestCase;
import org.mule.transport.tcp.integration.AbstractStreamingCapacityTestCase;

public abstract class AbstractStreamingDownloadMule1389TestCase extends DynamicPortTestCase
{
    public void testDownloadSpeed() throws Exception
    {
        MuleClient client = new MuleClient();
        long now = System.currentTimeMillis();
        MuleMessage result = client.send(((InboundEndpoint) client.getMuleContext().getRegistry().lookupObject("inTestComponent")).getEndpointURI().getAddress(),
            "request", null);
        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertEquals(InputStreamSource.SIZE, result.getPayloadAsBytes().length);
        long then = System.currentTimeMillis();
        double speed = InputStreamSource.SIZE / (double) (then - now) * 1000 / AbstractStreamingCapacityTestCase.ONE_MB;
        logger.info("Transfer speed " + speed + " MB/s (" + InputStreamSource.SIZE + " B in " + (then - now) + " ms)");
    }

}
