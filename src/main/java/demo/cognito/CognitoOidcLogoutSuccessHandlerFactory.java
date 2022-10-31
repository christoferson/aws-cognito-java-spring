package demo.cognito;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CognitoOidcLogoutSuccessHandlerFactory extends SimpleUrlLogoutSuccessHandler {

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@Value("${spring.security.oauth2.client.registration.cognito.client-id}")
	private String cognitoClientId;
	
	@Value("${aws.cognito.logout-url}")
	private String cognitoLogoutUri;	
	
	public CognitoOidcLogoutSuccessHandler getInstance(Map<String, String> properties) {
		return new CognitoOidcLogoutSuccessHandler(cognitoClientId, cognitoLogoutUri, properties);
	}

}
