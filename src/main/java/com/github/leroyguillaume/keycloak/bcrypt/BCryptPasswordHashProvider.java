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
        int cost;
        if (iterations == -1) {
            cost = defaultIterations;
        } else {
            cost = iterations;
        }
        return BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(cost, rawPassword.toCharArray());
    }

    @Override
    public void close() {

    }

    @Override
    public boolean verify(String rawPassword, PasswordCredentialModel credential) {
        BCrypt.Version hashVersion = BCrypt.Version.VERSION_2A;
        final String hash = credential.getPasswordSecretData().getValue();
        if (hash.startsWith("$2y$")) {
            hashVersion = BCrypt.Version.VERSION_2Y;
        } else if (hash.startsWith("$2b$")) {
            hashVersion = BCrypt.Version.VERSION_2B;
        } 
        BCrypt.Result verifier = BCrypt.verifyer(hashVersion).verify(rawPassword.toCharArray(), hash.toCharArray());
        return verifier.verified;
    }
}
