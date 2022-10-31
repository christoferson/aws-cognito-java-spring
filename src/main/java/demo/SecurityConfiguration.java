package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@Value("${spring.security.oauth2.client.registration.cognito.client-id}")
	private String cognitoClientId;
	
	@Value("${aws.cognito.logout-url}")
	private String cognitoLogoutUri;	
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	LogoutSuccessHandler logoutHandler = new CognitoOidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository)
    		.withPostLogoutRedirectUri("{baseUrl}/bye")
    		.withLogoutUri(cognitoLogoutUri);
    	
        http.csrf()
            .and()
            .authorizeRequests()
            	.antMatchers("/bye").permitAll()
            	.antMatchers("/").permitAll()
            	.anyRequest().authenticated()
            .and()
            .oauth2Login()
            	.defaultSuccessUrl("/", true)
            .and()
            .logout()
            .logoutSuccessHandler(logoutHandler)
            .logoutSuccessUrl("/");
        
        //http.csrf().disable();

    }
    
    @Bean 
    AuthenticationEventPublisher eventPublisher(ApplicationEventPublisher application) {
        AuthenticationEventPublisher authentication = new MyAuthenticationEventPublisher(application);
        return authentication;
    }

}
