package de.idealo.spring.contract.aws.sns;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.amazonaws.services.sns.AbstractAmazonSNS;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;

public class AmazonSNSDummyClient extends AbstractAmazonSNS {

    public static final String BEAN_NAME = "amazonSNS";

    private final Map<String, BlockingQueue<Message<String>>> outbound;

    public AmazonSNSDummyClient(String[] topics) {
        this.outbound = Stream.of(topics)
                .collect(Collectors.toMap(x -> x, x -> new LinkedBlockingDeque<>(), (x, y) -> x));
    }

    @Override
    public ListTopicsResult listTopics(final ListTopicsRequest request) {
        ListTopicsResult result = new ListTopicsResult();
        List<Topic> topics = this.outbound.keySet().stream()
                .map(name -> {
                    Topic topic = new Topic();
                    topic.setTopicArn("arn:aws:sns:eu-central-1:000000000000:" + name);
                    return topic;
                })
                .collect(Collectors.toList());
        result.setTopics(topics);
        return result;
    }

    @Override
    public PublishResult publish(final PublishRequest request) {
        String topicName = request.getTopicArn().substring(request.getTopicArn().lastIndexOf(':') + 1);
        Map<String, Object> attributes = request.getMessageAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().getStringValue()));

        outbound.get(topicName).add(new GenericMessage<>(request.getMessage(), attributes));

        PublishResult publishResult = new PublishResult();
        publishResult.setMessageId("testid");
        return publishResult;
    }

    public Message<String> pollNotification(String destination, long timeout, TimeUnit timeUnit) {
        try {
            return outbound.get(destination).poll(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
