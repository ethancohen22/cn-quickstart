// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.oauth;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Lazy(false)
public class OAuth2ClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private final Map<String, ClientRegistration> registrations;

    public OAuth2ClientRegistrationRepository(OAuth2ClientProperties properties) {
        List<ClientRegistration> registrations = new ArrayList<>(new OAuth2ClientPropertiesMapper(properties).asClientRegistrations().values());
        this.registrations = createRegistrationsMap(registrations);
    }

    private Map<String, ClientRegistration> createRegistrationsMap(List<ClientRegistration> registrations) {
        return registrations.stream().collect(
                Collectors.toMap(ClientRegistration::getRegistrationId,
                        registration -> ClientRegistration.withRegistrationId(registration.getRegistrationId())
                                .clientId(registration.getClientId())
                                .clientSecret(registration.getClientSecret())
                                .authorizationUri(registration.getProviderDetails().getAuthorizationUri())
                                .authorizationGrantType(registration.getAuthorizationGrantType())
                                .tokenUri(registration.getProviderDetails().getTokenUri())
                                .jwkSetUri(registration.getProviderDetails().getJwkSetUri())
                                .redirectUri(registration.getRedirectUri())
                                .scope(registration.getScopes())
                                .clientName(registration.getClientName())
                                .providerConfigurationMetadata(Map.of("preconfigured", "true"))
                                .build()
                )
        );
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return registrations.values().iterator();
    }

    public Collection<ClientRegistration> getRegistrations() {
        return registrations.values();
    }


    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }

    public void addRegistration(ClientRegistration registration) {
        registrations.put(registration.getRegistrationId(), registration);
    }

    public void removeRegistration(String clientId) {
        registrations.remove(clientId);
    }

}
