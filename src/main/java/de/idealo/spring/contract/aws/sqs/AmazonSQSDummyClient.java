package de.idealo.spring.contract.aws.sqs;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AbstractAmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class AmazonSQSDummyClient extends AbstractAmazonSQSAsync {

    public static final String BEAN_NAME = "amazonSQS";

    private final Map<String, BlockingQueue<Message<String>>> queues;

    public AmazonSQSDummyClient(String[] queueNames) {
        this.queues = Stream.of(queueNames)
                .collect(Collectors.toMap(x -> x, x -> new LinkedBlockingQueue<>()));
    }

    @Override
    public GetQueueUrlResult getQueueUrl(final GetQueueUrlRequest request) {
        return new GetQueueUrlResult()
                .withQueueUrl(request.getQueueName());
    }

    @Override
    public GetQueueAttributesResult getQueueAttributes(final GetQueueAttributesRequest request) {
        return new GetQueueAttributesResult();
    }

    @Override
    public SendMessageResult sendMessage(final SendMessageRequest request) {
        Map<String, Object> attributes = request.getMessageAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().getStringValue()));

        this.queues.get(request.getQueueUrl()).add(new GenericMessage<>(request.getMessageBody(), attributes));

        return new SendMessageResult();
    }

    @Override
    public ReceiveMessageResult receiveMessage(final ReceiveMessageRequest request) {
        try {
            Message<String> message = this.queues.get(request.getQueueUrl()).poll(request.getWaitTimeSeconds(), TimeUnit.SECONDS);
            com.amazonaws.services.sqs.model.Message sqsMessage = new com.amazonaws.services.sqs.model.Message()
                    .withMessageId("testid")
                    .withBody(message.getPayload());

            return new ReceiveMessageResult().withMessages(sqsMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ReceiveMessageResult();
        }
    }

    @Override
    public Future<DeleteMessageResult> deleteMessageAsync(final DeleteMessageRequest request, final AsyncHandler<DeleteMessageRequest, DeleteMessageResult> asyncHandler) {
        DeleteMessageResult result = new DeleteMessageResult();
        asyncHandler.onSuccess(request, result);
        return CompletableFuture.completedFuture(result);
    }
}
