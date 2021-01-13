# Spring Cloud Contract AWS

spring-cloud-contract-aws helps you test your [Spring Cloud AWS](https://spring.io/projects/spring-cloud-aws) messaging interfaces together with [Spring Cloud Contract](https://spring.io/projects/spring-cloud-contract). 
It provides the necessary auto-configuration so that your messages can be picked up and tested against your contracts without any fuss.

Currently these services are supported:

- AWS SNS (outbound)

## Installation

tbd

## Usage

### AWS SNS

Spring Cloud Contract tests that verify a notification sent from your service to AWS SNS can be annotated with `@AutoConfigureSNSVerifier`, which takes the topics that should be registered as argument.

**Example**

```java
@SpringBootTest
@AutoConfigureSNSVerifier({"your-topic-name"})
public abstract class SNSTestBase {
}
```