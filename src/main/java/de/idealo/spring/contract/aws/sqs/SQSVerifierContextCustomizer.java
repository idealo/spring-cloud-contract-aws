package de.idealo.spring.contract.aws.sqs;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

import de.idealo.spring.contract.aws.sns.AmazonSNSDummyClient;

public class SQSVerifierContextCustomizer implements ContextCustomizer {

    private final AutoConfigureSQSVerifier autoConfigureSQSVerifier;

    SQSVerifierContextCustomizer(AutoConfigureSQSVerifier autoConfigureSQSVerifier) {
        this.autoConfigureSQSVerifier = autoConfigureSQSVerifier;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, beanFactory);

        String[] queueNames = this.autoConfigureSQSVerifier.value();
        AmazonSQSDummyClient amazonSQS = new AmazonSQSDummyClient(queueNames);

        beanFactory.initializeBean(amazonSQS, AmazonSQSDummyClient.BEAN_NAME);
        beanFactory.registerSingleton(AmazonSQSDummyClient.BEAN_NAME, amazonSQS);

        SQSMessageVerifier sqsMessageVerifier = new SQSMessageVerifier(amazonSQS, this.autoConfigureSQSVerifier.notificationInput());
        beanFactory.initializeBean(sqsMessageVerifier, SQSMessageVerifier.BEAN_NAME);
        beanFactory.registerSingleton(SQSMessageVerifier.BEAN_NAME, sqsMessageVerifier);
    }
}
