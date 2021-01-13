package de.idealo.spring.contract.aws.sns;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.messaging.Message;

public class SNSMessageVerifier implements MessageVerifier<Message<String>> {

    private final AmazonSNSDummyClient amazonSNSDummyClient;

    public SNSMessageVerifier(final AmazonSNSDummyClient amazonSNSDummyClient) {
        this.amazonSNSDummyClient = amazonSNSDummyClient;
    }

    @Override
    public Message<String> receive(final String destination, final long timeout, final TimeUnit timeUnit) {
        return amazonSNSDummyClient.pollNotification(destination, timeout, timeUnit);
    }

    @Override
    public Message<String> receive(final String destination) {
        return this.receive(destination, 5, TimeUnit.SECONDS);
    }

    @Override
    public void send(final Message<String> message, final String destination) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> void send(final T payload, final Map<String, Object> headers, final String destination) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
