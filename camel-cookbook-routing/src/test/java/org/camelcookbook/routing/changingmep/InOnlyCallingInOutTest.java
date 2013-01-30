/*
 * Copyright (C) Scott Cranton and Jakub Korab
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.routing.changingmep;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class InOnlyCallingInOutTest extends CamelTestSupport {

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:beforeMessageModified")
    private MockEndpoint beforeMessageModified;

    @EndpointInject(uri = "mock:modifyMessage")
    private MockEndpoint modifyMessage;

    @EndpointInject(uri = "mock:afterMessageModified")
    private MockEndpoint afterMessageModified;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new InOnlyCallingInOutRouteBuilder();
    }

    @Test
    public void testInOnlyMEPChangedForModifyMessage() throws InterruptedException {
        final String messageBody = "Camel Rocks";
        final ExchangePattern callingMEP = ExchangePattern.InOnly;

        beforeMessageModified.setExpectedMessageCount(1);
        beforeMessageModified.message(0).body().isEqualTo(messageBody);
        // Should match calling MEP
        beforeMessageModified.message(0).exchangePattern().isEqualTo(callingMEP);

        modifyMessage.setExpectedMessageCount(1);
        modifyMessage.message(0).body().isEqualTo(messageBody);
        // Should always be InOut
        modifyMessage.message(0).exchangePattern().isEqualTo(ExchangePattern.InOut);

        afterMessageModified.setExpectedMessageCount(1);
        afterMessageModified.message(0).body().isEqualTo("[" + messageBody + "] has been modified!");
        // the exchange pattern is restored after the inOut call to the calling MEP
        afterMessageModified.message(0).exchangePattern().isEqualTo(callingMEP);

        template.sendBody("direct:in", callingMEP, messageBody);

        assertMockEndpointsSatisfied();

        Exchange modifyMessageExchange = modifyMessage.getReceivedExchanges().get(0);
        Exchange afterMessageModifiedExchange = afterMessageModified.getReceivedExchanges().get(0);

        // these are not the same exchange objects
        assertNotEquals(modifyMessageExchange, afterMessageModifiedExchange);

        // the transactions are the same
        assertEquals(modifyMessageExchange.getUnitOfWork(), afterMessageModifiedExchange.getUnitOfWork());
    }

    @Test
    public void testInOutMEPChangedForModifyMessage() throws InterruptedException {
        final String messageBody = "Camel Rocks";
        final ExchangePattern callingMEP = ExchangePattern.InOut;

        beforeMessageModified.setExpectedMessageCount(1);
        beforeMessageModified.message(0).body().isEqualTo(messageBody);
        // Should match calling MEP
        beforeMessageModified.message(0).exchangePattern().isEqualTo(callingMEP);

        modifyMessage.setExpectedMessageCount(1);
        modifyMessage.message(0).body().isEqualTo(messageBody);
        // Should always be InOut
        modifyMessage.message(0).exchangePattern().isEqualTo(ExchangePattern.InOut);

        afterMessageModified.setExpectedMessageCount(1);
        afterMessageModified.message(0).body().isEqualTo("[" + messageBody + "] has been modified!");
        // the exchange pattern is restored after the inOut call to the calling MEP
        afterMessageModified.message(0).exchangePattern().isEqualTo(callingMEP);

        template.sendBody("direct:in", callingMEP, messageBody);

        assertMockEndpointsSatisfied();

        Exchange modifyMessageExchange = modifyMessage.getReceivedExchanges().get(0);
        Exchange afterMessageModifiedExchange = afterMessageModified.getReceivedExchanges().get(0);

        // these are not the same exchange objects
        assertNotEquals(modifyMessageExchange, afterMessageModifiedExchange);

        // the transactions are the same
        assertEquals(modifyMessageExchange.getUnitOfWork(), afterMessageModifiedExchange.getUnitOfWork());
    }

}
