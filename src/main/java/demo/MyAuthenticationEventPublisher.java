package demo;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class MyAuthenticationEventPublisher extends DefaultAuthenticationEventPublisher {
	public MyAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
	}
	@Override
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

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		System.out.println(authentication);
		System.out.println(authentication.getClass().getName());
		if (authentication instanceof OAuth2LoginAuthenticationToken) {
			OAuth2LoginAuthenticationToken token = (OAuth2LoginAuthenticationToken) authentication;
			
			OAuth2AccessToken accessToken = token.getAccessToken();
			System.out.println("AccessToken.Value: " + accessToken.getTokenValue());
			System.out.println("AccessToken.Type: " + accessToken.getTokenType().getValue());
			System.out.println("AccessToken.Scopes: " + accessToken.getScopes());
			System.out.println("AccessToken.ExpiresAt: " + accessToken.getExpiresAt());
		}
		super.publishAuthenticationSuccess(authentication);
	}
}