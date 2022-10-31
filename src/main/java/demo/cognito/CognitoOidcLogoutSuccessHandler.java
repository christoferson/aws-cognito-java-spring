package demo.cognito;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class CognitoOidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	private final String clientId;
	private final String logoutUrl;
	private final Map<String, String> properties = new HashMap<>();

	public CognitoOidcLogoutSuccessHandler(String clientId, String logoutUrl, Map<String, String> properties) {
		
		Objects.requireNonNull(clientId);
		Objects.requireNonNull(logoutUrl);
		Objects.requireNonNull(properties);
		
		this.clientId = clientId;
		this.logoutUrl = logoutUrl;
		this.properties.putAll(properties);
		
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		UriComponents baseUrl = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
				.replacePath(request.getContextPath())
				.replaceQuery(null)
				.fragment(null)
				.build();

		return UriComponentsBuilder.fromUri(URI.create(logoutUrl))
				.queryParam("client_id", clientId)
				.queryParam("logout_uri", baseUrl + this.properties.get("post_logout_redirect_path"))
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUriString();

	}

}
