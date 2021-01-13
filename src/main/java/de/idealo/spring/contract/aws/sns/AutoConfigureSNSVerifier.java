package de.idealo.spring.contract.aws.sns;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Import;

/**
 * Annotation that can be specified on a Spring Cloud Contract test base class to load Amazon SNS scaffolding.
 * It will cause a dummy AmazonSNS client bean to be injected into your context.
 * <p>
 * The typical usage of this annotation is like:
 * <pre class="code">
 * &#064;SpringBootTest
 * &#064;AutoConfigureSNSVerifier({"your-topic-name"})
 * public abstract class SNSTestBase {
 * }
 * </pre>
 */
@AutoConfigureMessageVerifier
@Import(SNSMessageVerifier.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AutoConfigureSNSVerifier {

    String[] value();

}
