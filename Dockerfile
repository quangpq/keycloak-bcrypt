ARG gradle_version=jdk17
ARG keycloak_version

FROM gradle:${gradle_version} as build

WORKDIR /app

COPY . .

RUN gradle assemble

FROM quay.io/keycloak/keycloak:${keycloak_version} as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=mariadb

COPY --from=build /app/build/libs/*.jar /opt/keycloak/providers/

WORKDIR /opt/keycloak
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:${keycloak_version}
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENV KC_DB=mariadb
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]