/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.nio.http.issues;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.runners.Parameterized.Parameters;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.tcp.issues.AbstractStreamingDownloadMule1389TestCase;

public class StreamingDownloadMule1389ITCase extends AbstractStreamingDownloadMule1389TestCase
{
    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");

    public StreamingDownloadMule1389ITCase(final ConfigVariant variant, final String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "nio/streaming-download-mule-1389-service.xml"},
            {ConfigVariant.FLOW, "nio/streaming-download-mule-1389-flow.xml"}});
    }
}