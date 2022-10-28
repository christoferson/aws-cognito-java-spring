# aws-cognito-java-spring
Aws Cognito Spring Java Client



#### Maven Dependency 

```xml
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-oauth2-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-oauth2-jose</artifactId>
		</dependency>
```


#### Application Configuration

Set the AWS Cognito settings in the spring security oauth configuration
{aws.region}
{cognito.user_pool_id}
{cognito.client_id}
{cognito.client_secret}
{cognito.client_name}

```
spring:
  security:
    oauth2:
      client:
        registration:
          cognito:
            client-id: {cognito.client_id}
            client-secret: {cognito.client_secret}
            provider: cognito
            scope: openid
            redirect-uri: http://localhost:8080/login/oauth2/code/cognito
            client-name: {cognito.client_name}
            authorization-grant-type: authorization_code
        provider:
          cognito:
            issuerUri: https://cognito-idp.{aws.region}.amazonaws.com/{cognito.user_pool_id}
            user-name-attribute: cognito:username
```


#### WebSecurityConfigurerAdapter

