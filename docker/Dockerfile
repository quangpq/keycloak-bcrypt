ARG keycloak_version

FROM quay.io/keycloak/keycloak:${keycloak_version} as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=mariadb

ARG keycloak_bcrypt_version=1.5.5
COPY ./keycloak-bcrypt-${keycloak_bcrypt_version}.jar /opt/keycloak/providers/
COPY ./nashorn-core-15.4.jar /opt/keycloak/providers/nashorn-core-15.4.jar
COPY ./asm-7.3.1.jar /opt/keycloak/providers/asm-7.3.1.jar
COPY ./asm-commons-7.3.1.jar /opt/keycloak/providers/asm-commons-7.3.1.jar
COPY ./asm-tree-7.3.1.jar /opt/keycloak/providers/asm-tree-7.3.1.jar
COPY ./asm-util-7.3.1.jar /opt/keycloak/providers/asm-util-7.3.1.jar

WORKDIR /opt/keycloak
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:${keycloak_version}
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENV KC_DB=mariadb
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]