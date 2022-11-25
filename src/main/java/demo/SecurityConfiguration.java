package demo;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import demo.cognito.CognitoOidcLogoutSuccessHandlerFactory;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CognitoOidcLogoutSuccessHandlerFactory logoutSuccessHandlerFactory;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    
    	String postLogoutRedirectPath = "/bye";
    	
        LogoutSuccessHandler logoutSuccessHandler = logoutSuccessHandlerFactory.getInstance(
        	Collections.singletonMap("post_logout_redirect_path", postLogoutRedirectPath)
        );
        
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
        
        //http.csrf().disable();

    }
    
    @Bean 
    AuthenticationEventPublisher eventPublisher(ApplicationEventPublisher application) {
        AuthenticationEventPublisher authentication = new MyAuthenticationEventPublisher(application);
        return authentication;
    }

}
