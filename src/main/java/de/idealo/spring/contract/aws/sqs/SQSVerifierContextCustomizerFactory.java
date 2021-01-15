package de.idealo.spring.contract.aws.sqs;

import java.util.List;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import de.idealo.spring.contract.aws.sns.AutoConfigureSNSVerifier;

public class SQSVerifierContextCustomizerFactory implements ContextCustomizerFactory {
    @Override
    public ContextCustomizer createContextCustomizer(final Class<?> testClass, final List<ContextConfigurationAttributes> configAttributes) {
        AutoConfigureSQSVerifier sqsVerifier = AnnotatedElementUtils.findMergedAnnotation(testClass, AutoConfigureSQSVerifier.class);

        return sqsVerifier != null ? new SQSVerifierContextCustomizer(sqsVerifier) : null;
    }
}
