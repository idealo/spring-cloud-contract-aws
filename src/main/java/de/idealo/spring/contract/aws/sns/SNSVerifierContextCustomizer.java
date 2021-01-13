package de.idealo.spring.contract.aws.sns;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

public class SNSVerifierContextCustomizer implements ContextCustomizer {

    private final AutoConfigureSNSVerifier autoConfigureSNSVerifier;

    SNSVerifierContextCustomizer(AutoConfigureSNSVerifier autoConfigureSNSVerifier) {
        this.autoConfigureSNSVerifier = autoConfigureSNSVerifier;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, beanFactory);

        String[] topics = this.autoConfigureSNSVerifier.value();
        AmazonSNSDummyClient amazonSNS = new AmazonSNSDummyClient(topics);

        beanFactory.initializeBean(amazonSNS, AmazonSNSDummyClient.BEAN_NAME);
        beanFactory.registerSingleton(AmazonSNSDummyClient.BEAN_NAME, amazonSNS);
    }
}
