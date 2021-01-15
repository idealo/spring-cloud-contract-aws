package de.idealo.spring.contract.aws.sqs;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cloud.aws.messaging.support.converter.NotificationRequestConverter;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import de.idealo.spring.contract.aws.sns.AmazonSNSDummyClient;

public class SQSMessageVerifier implements MessageVerifier<Message<String>> {

    public static final String BEAN_NAME = "sqsMessageVerifier";

    private final AmazonSQSDummyClient amazonSQSDummyClient;
    private final boolean notificationInput;

    public SQSMessageVerifier(final AmazonSQSDummyClient amazonSQSDummyClient, boolean notificationInput) {
        this.amazonSQSDummyClient = amazonSQSDummyClient;
        this.notificationInput = true;
    }


    @Override
    public Message<String> receive(final String destination, final long timeout, final TimeUnit timeUnit) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(destination)
                .withWaitTimeSeconds(Math.toIntExact(TimeUnit.SECONDS.convert(timeout, timeUnit)));
        ReceiveMessageResult result = amazonSQSDummyClient.receiveMessage(request);

        if (!result.getMessages().isEmpty()) {
            com.amazonaws.services.sqs.model.Message message = result.getMessages().get(0);
            Map<String, Object> attributes = message.getMessageAttributes().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().getStringValue()));
            return new GenericMessage<>(message.getBody(), attributes);
        } else {
            return null;
        }
    }

    @Override
    public Message<String> receive(final String destination) {
        return this.receive(destination, 5, TimeUnit.SECONDS);
    }

    @Override
    public void send(final Message<String> message, final String destination) {
        Map<String, MessageAttributeValue> attributes = message.getHeaders().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> new MessageAttributeValue().withStringValue(x.getValue().toString())));

        try {
            String payload = notificationInput ? new JSONObject()
                    .put("Type", "Notification")
                    .put("Subject", "")
                    .put("Message", message.getPayload())
                    .toString() : message.getPayload();

            SendMessageRequest request = new SendMessageRequest(destination, payload)
                    .withMessageAttributes(attributes);
            amazonSQSDummyClient.sendMessage(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void send(final T payload, final Map<String, Object> headers, final String destination) {
        this.send(MessageBuilder.createMessage((String) payload, new MessageHeaders(headers)), destination);
    }
}
