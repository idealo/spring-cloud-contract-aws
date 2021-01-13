package de.idealo.spring.contract.aws.sns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;

class SNSVerifierContextCustomizerTest {

    @Test
    void shouldCreateClientWithTopics() {
        AutoConfigureSNSVerifier snsVerifier = AnnotationUtils.findAnnotation(TestSNSTopics.class, AutoConfigureSNSVerifier.class);
        SNSVerifierContextCustomizer customizer = new SNSVerifierContextCustomizer(snsVerifier);

        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        BeanFactoryStub factoryStub = new BeanFactoryStub();
        when(context.getBeanFactory()).thenReturn(factoryStub);
        when(context.getEnvironment()).thenReturn(mock(ConfigurableEnvironment.class));
        customizer.customizeContext(context, null);

        assertThat(factoryStub.getClient().listTopics().getTopics()).extracting("topicArn")
                .containsExactlyInAnyOrder("arn:aws:sns:eu-central-1:000000000000:topic1", "arn:aws:sns:eu-central-1:000000000000:topic2");
    }

    @AutoConfigureSNSVerifier({"topic1", "topic2"})
    private class TestSNSTopics {

    }

    private class BeanFactoryStub extends DefaultListableBeanFactory {

        private Object bean;

        public AmazonSNSDummyClient getClient() {
            return (AmazonSNSDummyClient) bean;
        }

        @Override
        public Object initializeBean(Object existingBean, String beanName) throws BeansException {
            this.bean = existingBean;
            return bean;
        }

        @Override
        public void registerSingleton(String beanName, Object singletonObject) {

        }

    }

}