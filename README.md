# LiveFinder

Service to locate your friends or those you follow in online communities


# Compiling

`mvn clean package`


# Running

`java -jar service/target/livefinder-service.jar server livefinder.json`


# Configuration

Refer to the Dropwizard configuration module for framework-specific configuration (logging, HTTP ports, etc.): http://www.dropwizard.io/manual/configuration.html

See `livefinder.json` for an example configuration file suitable for a development environment.

See `livefinder_local.json` for an example configuration file suitable for local development.

## Enabling SSL

Run `make_certs.sh` to generate a self-signed cert and keystore. It requires a configuration file (`selfsigned.cnf`) and a private key for encryption.

Refer to the Alexa documentation for how to create the configuration file:

https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/testing-an-alexa-skill#create-a-private-key-and-self-signed-certificate-for-testing
