[description]
Enable the logic to watch the SSL key file and trigger
a reload of the SslContextFactory when the key file changes.

[depend]
ssl

[lib]
lib/reload_sslkey_module-1.0.jar

[xml]
etc/reload-ssl-keys.xml

[files]

[ini-template]
### Reload SSL Keys Configuration
--module=reload-ssl-keys
