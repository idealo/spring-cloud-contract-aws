package de.idealo.spring.contract.aws.sqs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Import;

import de.idealo.spring.contract.aws.sns.SNSMessageVerifier;

@AutoConfigureMessageVerifier
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AutoConfigureSQSVerifier {

    String[] value();

    boolean notificationInput() default false;

}
