# aws-cognito-java-spring

Aws Cognito Spring Java Client 

- Login and Logout using Cognito Authentication Provider



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

Set the Spring OAUTH2 AWS Cognito Client Configuration

- {aws.region}
- {cognito.user_pool_id}
- {cognito.client_id}
- {cognito.client_secret}
- {cognito.client_name}

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

Set AWS Cognito Logout Configuration

- {cognito.domain}
- {aws.region}

```
aws:        
  cognito:
    logout-url: https://{cognito.domain}.auth.{aws.region}.amazoncognito.com/logout
```

#### WebSecurityConfigurerAdapter (SecurityConfiguration)

```
http.csrf()
    .and()
    .authorizeRequests()
    	.antMatchers(postLogoutRedirectPath).permitAll()
    	.antMatchers("/").permitAll()
    	.anyRequest().authenticated()
    .and()
    .oauth2Login()
    	.defaultSuccessUrl("/", true)
    .and()
    .logout()
    //.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Disable Logout Confirmation
    .logoutSuccessHandler(logoutSuccessHandler)
    .logoutSuccessUrl("/");
```

### WebMvcConfigurer (CognitoWebConfiguration)

```
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("home");
    registry.addViewController("/secured").setViewName("secured");
    registry.addViewController("/bye").setViewName("bye");
}
```

