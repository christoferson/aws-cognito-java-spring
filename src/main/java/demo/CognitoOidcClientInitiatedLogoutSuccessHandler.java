package demo;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
// Based on OidcClientInitiatedLogoutSuccessHandler
// https://<DOMAIN_PREFIX>.auth.<AWS_REGION>.amazoncognito.com/logout
public class CognitoOidcClientInitiatedLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(CognitoOidcClientInitiatedLogoutSuccessHandler.class);

	private final ClientRegistrationRepository clientRegistrationRepository;

	private String postLogoutRedirectUri;
	
	private String logoutUri;

	public CognitoOidcClientInitiatedLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
		Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String targetUrl = null;
		if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OidcUser) {
			String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(); //cognito
			ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
			URI endSessionEndpoint = this.endSessionEndpoint(clientRegistration);
			if (endSessionEndpoint != null) {
				String idToken = idToken(authentication);
				String clientid = clientRegistration.getClientId();
				String postLogoutRedirectUri = postLogoutRedirectUri(request);
				targetUrl = endpointUri(endSessionEndpoint, idToken, postLogoutRedirectUri, clientid);
			}
		}
		return (targetUrl != null) ? targetUrl : super.determineTargetUrl(request, response);
	}

	private URI endSessionEndpoint(ClientRegistration clientRegistration) {
		if (clientRegistration == null) {
			return null;
		}

		ProviderDetails providerDetails = clientRegistration.getProviderDetails();
		Object endSessionEndpoint = providerDetails.getConfigurationMetadata().get("end_session_endpoint");
		if (endSessionEndpoint != null) { // null
			return URI.create(endSessionEndpoint.toString());
		}
		return URI.create(this.logoutUri);
	}

	private String idToken(Authentication authentication) {
		return ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
	}

	private String postLogoutRedirectUri(HttpServletRequest request) {
		if (this.postLogoutRedirectUri == null) {
			return null;
		}
		// @formatter:off
		UriComponents uriComponents = UriComponentsBuilder
				.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
				.replacePath(request.getContextPath())
				.replaceQuery(null)
				.fragment(null)
				.build();
		return UriComponentsBuilder.fromUriString(this.postLogoutRedirectUri)
				.buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString()))
				.toUriString();
		// @formatter:on
	}

	private String endpointUri(URI endSessionEndpoint, String idToken, String postLogoutRedirectUri, String clientid) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endSessionEndpoint);

		if (postLogoutRedirectUri != null) {
			//builder.queryParam("post_logout_redirect_uri", postLogoutRedirectUri);
			// In Cognito redirect_uri is for when you want to logout+reauthenticate as another user. switch user functionality.
			// It will require parameters for re-authentication
			//builder.queryParam("redirect_uri", postLogoutRedirectUri); 
			// Logout and redirect to logout complete page
			builder.queryParam("logout_uri", postLogoutRedirectUri);
		}
		builder.queryParam("client_id", clientid);

		// @formatter:off
		return builder.encode(StandardCharsets.UTF_8)
				.build()
				.toUriString();
		// @formatter:on
	}

	/**
	 * Set the post logout redirect uri template to use. Supports the {@code "{baseUrl}"}
	 * placeholder, for example:
	 *
	 * <pre>
	 * 	handler.setPostLogoutRedirectUri("{baseUrl}");
	 * </pre>
	 *
	 * will make so that {@code post_logout_redirect_uri} will be set to the base url for
	 * the client application.
	 * @param postLogoutRedirectUri - A template for creating the
	 * {@code post_logout_redirect_uri} query parameter
	 * @since 5.3
	 */
	public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
		Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
		this.postLogoutRedirectUri = postLogoutRedirectUri;
	}

	public String getLogoutUri() {
		return logoutUri;
	}

	public CognitoOidcClientInitiatedLogoutSuccessHandler withLogoutUri(String logoutUri) {
		this.logoutUri = logoutUri;
		return this;
	}
	
	
}


