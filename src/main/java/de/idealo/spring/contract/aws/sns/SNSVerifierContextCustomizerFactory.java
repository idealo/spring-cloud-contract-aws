package de.idealo.spring.contract.aws.sns;

import java.util.List;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

public class SNSVerifierContextCustomizerFactory implements ContextCustomizerFactory {
    @Override
    public ContextCustomizer createContextCustomizer(final Class<?> testClass, final List<ContextConfigurationAttributes> configAttributes) {
        AutoConfigureSNSVerifier snsVerifier = AnnotatedElementUtils.findMergedAnnotation(testClass, AutoConfigureSNSVerifier.class);

        return snsVerifier != null ? new SNSVerifierContextCustomizer(snsVerifier) : null;
    }
}
