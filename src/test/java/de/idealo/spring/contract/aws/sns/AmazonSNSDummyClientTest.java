package de.idealo.spring.contract.aws.sns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;

import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.Topic;

class AmazonSNSDummyClientTest {

    @Test
    void shouldReturnTopicsItWasCreatedWith() {
        AmazonSNSDummyClient sut = new AmazonSNSDummyClient(new String[] {"topic1", "topic2"});

        List<Topic> actual = sut.listTopics(new ListTopicsRequest()).getTopics();
        assertThat(actual).extracting("topicArn")
                .containsExactlyInAnyOrder("arn:aws:sns:eu-central-1:000000000000:topic1", "arn:aws:sns:eu-central-1:000000000000:topic2");
    }

    @Test
    void shouldReturnMessageThatWasSent() {
        AmazonSNSDummyClient sut = new AmazonSNSDummyClient(new String[] {"topic"});

        sut.publish("arn:aws:sns:eu-central-1:000000000000:topic", "test message");

        Message<String> actual = sut.pollNotification("topic", 100, TimeUnit.MILLISECONDS);
        assertThat(actual.getPayload()).isEqualTo("test message");
    }

    @Test
    void shouldReturnMessageFromCorrectTopic() {
        AmazonSNSDummyClient sut = new AmazonSNSDummyClient(new String[] {"topic1", "topic2"});

        sut.publish("arn:aws:sns:eu-central-1:000000000000:topic1", "message1");
        sut.publish("arn:aws:sns:eu-central-1:000000000000:topic2", "message2");

        Message<String> actual1 = sut.pollNotification("topic1", 100, TimeUnit.MILLISECONDS);
        assertThat(actual1.getPayload()).isEqualTo("message1");

        Message<String> actual2 = sut.pollNotification("topic2", 100, TimeUnit.MILLISECONDS);
        assertThat(actual2.getPayload()).isEqualTo("message2");
    }

    @Test
    void shouldReturnNullIfNoMessageWasSent() {
        AmazonSNSDummyClient sut = new AmazonSNSDummyClient(new String[] {"topic"});

        Message<String> actual = sut.pollNotification("topic", 1, TimeUnit.MILLISECONDS);
        assertThat(actual).isNull();
    }

}