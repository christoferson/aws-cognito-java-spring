package demo;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .and()
            .authorizeRequests(authz -> authz.mvcMatchers("/")
                .permitAll()
                .anyRequest()
                .authenticated())
            .oauth2Login()
            	.defaultSuccessUrl("/", true)
            	//.failureUrl("./xxxx.html")
            //.failureHandler(new MyFailureHandler())
            .and()
            .logout()
            .logoutSuccessUrl("/");
        
        //http.csrf().disable();
    }
    
    @Bean 
    AuthenticationEventPublisher eventPublisher(ApplicationEventPublisher application) {
        AuthenticationEventPublisher authentication = 
            new MyAuthenticationEventPublisher(application);
//        authentication.setAdditionalExceptionMappings(
//            Collections.singletonMap(OAuth2AuthenticationException.class, FooEvent.class));
        return authentication;
    }
    
    public static class MyAuthenticationEventPublisher extends DefaultAuthenticationEventPublisher {
    	public MyAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    		super(applicationEventPublisher);
    	}
    	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
    		System.out.println(exception);
    		System.out.println(authentication);
    		if (exception instanceof OAuth2AuthenticationException) {
    			OAuth2AuthenticationException oe = (OAuth2AuthenticationException) exception;
    			System.out.println(oe.getMessage());
    			System.out.println(oe.getError());
    		}
    		super.publishAuthenticationFailure(exception, authentication);
    	}
    }
    
//    private ProviderManager providerManager() {
//        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(...);
//        JwtAuthenticationProvider authenticationProvider = 
//            new JwtAuthenticationProvider(jwtDecoder);
//        authenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
//        ProviderManager providerManager = new ProviderManager
//            (Arrays.asList(authenticationProvider));
//        providerManager.setAuthenticationEventPublisher(fooPublisher);
//        return providerManager;
//    }
    
    private class MyFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    	
    	@Override
    	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    			AuthenticationException exception) throws IOException, ServletException {
    		super.onAuthenticationFailure(request, response, exception);
    		System.out.println(exception);
    	}
    }

//	public void configure(HttpSecurity http) throws Exception {
//		http.oauth2Login().and().authorizeRequests().antMatchers("/**").authenticated().and().csrf()
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//	}

}
