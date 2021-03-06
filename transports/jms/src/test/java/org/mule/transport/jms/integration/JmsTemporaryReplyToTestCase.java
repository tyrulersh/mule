/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.jms.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleMessage;
import org.mule.transport.NullPayload;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * TODO this test does not use the Test scenarios, I think it would need a new Method
 * sendAndReceive It might make sense to leave this test as is because it tests that
 * the client also works with ReplyTo correctly
 */
public class JmsTemporaryReplyToTestCase extends AbstractJmsFunctionalTestCase
{

    private static String ECHO_FLOW_NAME = "EchoFlow";

    @Override
    protected String getConfigFile()
    {
        return "integration/jms-temporary-replyTo.xml";
    }

    @Test
    public void testReplyEnabledSync() throws Exception
    {
        MuleMessage response = runFlow("JMSService1SyncFixed", TEST_MESSAGE).getMessage();
        assertEchoResponse(response);
    }

    @Test
    public void testReplyEnabledSyncTimeout() throws Exception
    {
        MuleMessage response = runFlow("JMSService1SyncTimeoutFixed", TEST_MESSAGE).getMessage();
        assertNullPayloadResponse(response);
    }

    @Test
    public void testTemporaryReplyEnabledSync() throws Exception
    {
        MuleMessage response = runFlow("JMSService1Sync", TEST_MESSAGE).getMessage();
        assertEchoResponse(response);
    }

    @Test
    public void testTemporaryReplyEnabledSyncTimeout() throws Exception
    {
        MuleMessage response = runFlow("JMSService1SyncTimeout", TEST_MESSAGE).getMessage();
        assertNullPayloadResponse(response);
    }

    @Test
    public void testTemporaryReplyDisabledSync() throws Exception
    {
        // TODO This behaviour appears inconsistent.  NullPayload would be more appropriate.
        assertThat(runFlow("JMSService2Sync"), is(nullValue()));
    }

    @Test
    public void testDisableTemporaryReplyOnTheConnector() throws Exception
    {
        MuleMessage response = runFlow("JMSService3", TEST_MESSAGE).getMessage();
        assertEquals(TEST_MESSAGE, response.getPayload());
    }

    @Test
    public void testExplicitReplyToAsyncSet() throws Exception
    {
        MuleMessage response = runFlow("JMSService4", TEST_MESSAGE).getMessage();
        // We get the original message back, not the result from the remote component
        assertEchoResponse(response);
    }

    private void assertEchoResponse(MuleMessage response) throws Exception
    {
        assertThat(response.getPayloadAsString(), equalTo(TEST_MESSAGE + " " + ECHO_FLOW_NAME));
    }

    private void assertNullPayloadResponse(MuleMessage response)
    {
        assertThat(response.getPayload(), CoreMatchers.<Object>is(NullPayload.getInstance()));
    }

}
