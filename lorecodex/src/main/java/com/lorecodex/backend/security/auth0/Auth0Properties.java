package com.lorecodex.backend.security.auth0;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth0")
public record Auth0Properties(
        String domain,
        String audience,
        String issuerUri,
        String jwksUri,
        String principalClaim,
        String rolesClaim,
        String permissionsClaim,
        String emailClaim,
        Boolean autoProvisionUser
) {
    public Auth0Properties {
        if (principalClaim == null || principalClaim.isBlank()) {
            principalClaim = "sub";
        }
        if (rolesClaim == null || rolesClaim.isBlank()) {
            rolesClaim = "roles";
        }
        if (permissionsClaim == null || permissionsClaim.isBlank()) {
            permissionsClaim = "permissions";
        }
        if (emailClaim == null || emailClaim.isBlank()) {
            emailClaim = "email";
        }
        if (autoProvisionUser == null) {
            autoProvisionUser = true;
        }
    }
}
