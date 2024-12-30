package antifraud.config;

import antifraud.enums.RoleNames;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
public class AuthorizationRuleConfigurer {

    void configureAuthorizationRules(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        configurePublicEndpoints(auth);
        configureAdminEndpoints(auth);
        configureMerchantEndpoints(auth);
        configureSupportEndpoints(auth);
        configureSharedEndpoints(auth);

        auth.anyRequest().denyAll();
    }

    private void configurePublicEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll();
        auth.requestMatchers("/actuator/health").permitAll();
        auth.requestMatchers("/actuator/shutdown").permitAll();
        auth.requestMatchers("/swagger-ui/index.html").permitAll();
    }

    private void configureAdminEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole(RoleNames.ADMINISTRATOR.name());
        auth.requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole(RoleNames.ADMINISTRATOR.name());
        auth.requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole(RoleNames.ADMINISTRATOR.name());
        auth.requestMatchers("/actuator/info").hasRole(RoleNames.ADMINISTRATOR.name());
        auth.requestMatchers("/actuator/metrics").hasRole(RoleNames.ADMINISTRATOR.name());
    }

    private void configureMerchantEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasRole(RoleNames.MERCHANT.name());
    }

    private void configureSupportEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/api/antifraud/suspicious-ip/**").hasRole(RoleNames.SUPPORT.name());
        auth.requestMatchers("/api/antifraud/stolencard/**").hasRole(RoleNames.SUPPORT.name());
        auth.requestMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasRole(RoleNames.SUPPORT.name());
        auth.requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole(RoleNames.SUPPORT.name());
    }

    private void configureSharedEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(RoleNames.ADMINISTRATOR.name(), RoleNames.SUPPORT.name());
    }
}
