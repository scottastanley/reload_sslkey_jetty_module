[description]
Enable the logic to watch the SSL key file and trigger
a reload of the SslContextFactory when the key file changes.

[depend]
ssl

[lib]
lib/reload_sslkey_module-1.2.jar

[xml]
etc/reload-ssl-keys.xml

[files]

[ini-template]
### Reload SSL Keys Configuration
--module=reload-ssl-keys

## Reload delay period (seconds).  Logic waits for a period of reloadDelaySec after the last
## modification to the SSL key file prior to triggering the reload.  This is intended to prevent 
## attempts to reload the file before it has completely been written.
# reloadsslkeys.reloadDelaySec = 15
