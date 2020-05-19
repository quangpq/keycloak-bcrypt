package com.github.leroyguillaume.keycloak.bcrypt;

import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * @author <a href="mailto:pro.guillaume.leroy@gmail.com">Guillaume Leroy</a>
 */
public class BCryptPasswordHashProvider implements PasswordHashProvider {
    private final int defaultIterations;
    private final String providerId;

    public BCryptPasswordHashProvider(String providerId, int defaultIterations) {
        this.providerId = providerId;
        this.defaultIterations = defaultIterations;
    }

    @Override
    public boolean policyCheck(PasswordPolicy policy, PasswordCredentialModel credential) {
        int policyHashIterations = policy.getHashIterations();
        if (policyHashIterations < 1) {
            policyHashIterations = defaultIterations;
        }

        return credential.getPasswordCredentialData().getHashIterations() == policyHashIterations
                && providerId.equals(credential.getPasswordCredentialData().getAlgorithm());
    }

    @Override
    public PasswordCredentialModel encodedCredential(String rawPassword, int iterations) {
        String encodedPassword = encode(rawPassword, iterations);

        // bcrypt salt is stored as part of the encoded password so no need to store salt separately
        return PasswordCredentialModel.createFromValues(providerId, new byte[0], iterations, encodedPassword);
    }

    @Override
    public String encode(String rawPassword, int iterations) {
        int cost = iterations < 1 ? defaultIterations : iterations;
        return BCrypt.with(BCrypt.Version.VERSION_2B).hashToString(cost, rawPassword.toCharArray());
    }

    @Override
    public void close() {

    }

    @Override
    public boolean verify(String rawPassword, PasswordCredentialModel credential) {
        BCrypt.Version hashVersion = BCrypt.Version.VERSION_2A;
        String securedPassword = credential.getPasswordSecretData().getValue();
        if (securedPassword.startsWith("$2y$")) {
            hashVersion = BCrypt.Version.VERSION_2Y;
        } else if (securedPassword.startsWith("$2b$")) {
            hashVersion = BCrypt.Version.VERSION_2B;
        } 
        return BCrypt.verifyer(hashVersion).verify(rawPassword.toCharArray(), securedPassword).verified;
    }
}
