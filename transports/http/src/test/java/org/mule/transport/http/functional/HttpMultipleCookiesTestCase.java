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

import org.mule.tck.DynamicPortTestCase;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;

public class HttpMultipleCookiesTestCase extends DynamicPortTestCase
{
    //private static final int LOCAL_JETTY_SERVER_PORT = 4020;
    protected static String TEST_MESSAGE = "Test Http Request ";
    protected static final Log logger = LogFactory.getLog(HttpMultipleCookiesTestCase.class);

    private CountDownLatch simpleServerLatch = new CountDownLatch(1);
    private CountDownLatch simpleServerShutdownLatch = new CountDownLatch(1);
    private static AtomicBoolean cookiesRecieved = new AtomicBoolean(false);

    private Server server = null;

    public HttpMultipleCookiesTestCase()
    {
        super();
        setStartContext(false);
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        startServer();
        assertTrue(simpleServerLatch.await(RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
    }

    @Override
    protected void doTearDown() throws Exception
    {
        // TODO Auto-generated method stub
        super.doTearDown();
        muleContext.stop();
        stopServer();
        assertTrue(simpleServerShutdownLatch.await(RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
    }
            
    @Override
    protected String getConfigResources()
    {
        return "http-multiple-cookies-test.xml";
    }

    public void testSendDirectly() throws Exception
    {
        muleContext.start();
        sendMessage(getPorts().get(1));
    }

    public void testSendviaMule() throws Exception
    {
        muleContext.start();
        sendMessage(getPorts().get(0));
    }

    protected void sendMessage(int port) throws Exception
    {
        HttpClient client2 = new HttpClient();
        client2.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        HttpState state = new HttpState();
        Cookie cookie1 = new Cookie("localhost", "TheFirst", "First", "/", null, false);
        state.addCookie(cookie1);
        Cookie cookie2 = new Cookie("localhost", "TheSecond", "Value2", "/", null, false);
        state.addCookie(cookie2);
        Cookie cookie3 = new Cookie("localhost", "TheThird", "Value3", "/", null, false);
        state.addCookie(cookie3);

        client2.setState(state);
        PostMethod method = new PostMethod("http://localhost:" + port);
        Thread.sleep(5000);
        client2.executeMethod(method);
        assertEquals(TEST_MESSAGE, method.getResponseBodyAsString());
        assertTrue("Cookies were not recieved", cookiesRecieved.get());

        for (Cookie cookie : client2.getState().getCookies())
        {
            logger.debug(cookie.getName() + " " + cookie.getValue());
        }
        assertEquals(6, client2.getState().getCookies().length);

    }

    protected void startServer() throws Exception
    {
        logger.debug("server starting");
        Server server = new Server();
        Connector connector = new SocketConnector();
        connector.setPort(getPorts().get(1));
        server.setConnectors(new Connector[]{connector});

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(HelloServlet.class.getName(), "/");

        server.start();
        // server.join();
        simpleServerLatch.countDown();
        logger.debug("Server started");
    }
    
    protected void stopServer() throws Exception
    {
        logger.debug("server stopping");
        
        if(server != null && server.isRunning())
        {
            assertEquals(1, server.getConnectors());            
            // this test only uses one connector
            server.getConnectors()[0].stop();
        }
        
        simpleServerShutdownLatch.countDown();
        logger.debug("Server stopped");
    }

    public static class HelloServlet extends HttpServlet
    {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
        {
            try
            {
                response.setContentType("text/xml");
                response.setContentLength(TEST_MESSAGE.length());
                for (int i = 0; i < 3; i++)
                {
                    javax.servlet.http.Cookie cookie1 = new javax.servlet.http.Cookie("OutputCookieName" + i,
                        "OutputCookieValue" + i);
                    response.addCookie(cookie1);
                }
                cookiesRecieved.set(false);
                javax.servlet.http.Cookie[] cookies = request.getCookies();
                if (cookies != null)
                {
                    for (javax.servlet.http.Cookie cookie : cookies)
                    {
                        logger.debug(cookie.getName() + " " + cookie.getValue());
                        cookiesRecieved.set(true);
                    }
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(TEST_MESSAGE);

            }
            catch (Exception e)
            {
                logger.error("Servlet error", e);
                throw new ServletException(e);
            }
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
        {
            doGet(request, response);
        }

    }

    @Override
    public void handleTimeout(long timeout, edu.emory.mathcs.backport.java.util.concurrent.TimeUnit unit)
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected int getNumPortsToFind()
    {
        return 2;
    }
}
