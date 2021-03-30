# Keycloak BCrypt

Add a password hash provider to handle BCrypt passwords inside Keycloak.
Use https://github.com/patrickfav/bcrypt instead of JBCrypt.

## Build
```bash
./gradlew jar
```

## Test with docker-compose
```bash
cp build/libs/keycloak-bcrypt-1.5.0.jar docker/
docker-compose up -d
```

## Install
```
curl -L https://github.com/leroyguillaume/keycloak-bcrypt/releases/download/1.5.0/keycloak-bcrypt-1.5.0.jar > KEYCLOAK_HOME/standalone/deployments/keycloak-bcrypt-1.5.0.jar
```
You need to restart Keycloak.
## Install with Docker Compose

```
mkdir deployments
curl -L https://github.com/leroyguillaume/keycloak-bcrypt/releases/download/1.5.0/keycloak-bcrypt-1.5.0.jar > deployments/keycloak-bcrypt-1.5.0.jar
```

docker-compose.yml
```yml
keycloak:
    image: jboss/keycloak:12.0.4
    volumes:
      - ./deployments/keycloak-bcrypt-1.5.0.jar:/opt/jboss/keycloak/standalone/deployments/keycloak-bcrypt-1.5.0.jar


## How to use
Go to `Authentication` / `Password policy` and add hashing algorithm policy with value `bcrypt`.

To test if installation works, create new user and set its credentials.
