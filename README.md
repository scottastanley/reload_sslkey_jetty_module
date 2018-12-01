# Reload SSL Keys Jetty Module

This simple set of code implements the logic to watch the SSL key file being used by the SslContextFactory in Jetty and when the file changes, calls the SslContextFactory.reload() method to load the new keys.  The intention of this module is for it to be used to support the use of [LetsEncrypt](https://letsencrypt.org) SSL keys using the simple command line script provided by [certbot](https://certbot.eff.org).  Since LetsEncrypt SSL keys expire in 90 days, it is necessary to implement an automated mechanism for updating the keys.

Thanks go to [Daniel Flower](https://danielflower.github.io) for his original post, [LetsEncrypt certs with embedded Jetty](https://danielflower.github.io/2017/04/08/Lets-Encrypt-Certs-with-embedded-Jetty.html), outlining the basic ideas of automating using LetsEncrypt SSL keys in Jetty.

## Using the module
This module is not set up with a clean automated installation process at this point.  But, manual usage is simple enough...

### Requirements
**Jetty Version**: > 9.4.x (build and tested using 9.4.14.v20181114)

**Java Version**: > 1.8

### Installation
1. Build the module using Maven; `mvn clean package`

1. Copy the JAR file to the $JETTY\_BASE/lib/ folder; `cp target/reload_sslkey_module-1.0.jar $JETTY_BASE/lib/`

1. Copy the module definition to $JETTY\_BASE/modules/; `cp src/main/resources/reload-ssl-keys.mod $JETTY_BASE/modules/`

1. Copy the XML file to $JETTY\_BASE/etc/; `cp src/main/resources/reload-ssl-keys.xml $JETTY_BASE/etc/`

1. Create a .ini file in the $JETTY\_BASE/start.d/ directory; for example `$JETTY\_BASE/start.d/reload-ssl-keys.ini`.  The contents of this file should be;

```
### Reload SSL Keys Configuration
--module=reload-ssl-keys
```

For more information on configuring Jetty see the [Current Jetty Documentation](https://www.eclipse.org/jetty/documentation/current/), particularly [Chapter 3. An Introduction to Jetty Configuration](https://www.eclipse.org/jetty/documentation/current/quick-start-configure.html).
