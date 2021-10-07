package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	CognitoOidcClientInitiatedLogoutSuccessHandler logoutHandler = new CognitoOidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
    	logoutHandler.setPostLogoutRedirectUri("http://localhost:8080/bye");
        http.csrf()
            .and()
            .authorizeRequests()
            	.antMatchers("/bye").permitAll()
            	.antMatchers("/").permitAll().anyRequest().authenticated()
            .and()
//            .authorizeRequests(authz -> authz.mvcMatchers("/")
//                .permitAll()
//                .anyRequest()
//                .authenticated())
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
