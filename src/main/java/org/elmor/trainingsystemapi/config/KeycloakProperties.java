package org.elmor.trainingsystemapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {
    private String host;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
}
