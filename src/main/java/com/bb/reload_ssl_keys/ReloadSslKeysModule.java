package com.bb.reload_ssl_keys;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * A simple Jetty module which watches the SSL key file being used by the 
 * SslContextFactory for changes.  When the file is modified, this module
 * forces the SslContextFactory to reload the SSL key.
 * 
 * @author Scott Stanley
 */
public class ReloadSslKeysModule extends AbstractLifeCycle {
    static final Logger LOG = Log.getLogger(ReloadSslKeysModule.class);
    
    private SslContextFactory m_sslCtxFactory = null;
    private FileWatcher m_fileWatcher = null;
    private Long m_reloadDelaySec = 15L;


    public ReloadSslKeysModule() {
    }
    
    public void setSslContextFactory(final SslContextFactory sslCtxFactory) {
        m_sslCtxFactory = sslCtxFactory;
    }
    
    public void setReloadDelaySec(final long reloadDelaySec) {
        m_reloadDelaySec = reloadDelaySec;
    }
    
    @Override
    protected void doStart() throws Exception {
        if (m_sslCtxFactory != null) {
            Path keystorePath = Paths.get(URI.create(m_sslCtxFactory.getKeyStorePath()));
            m_fileWatcher = new FileWatcher(keystorePath, () -> {
                try {
                    LOG.info("Reloading keys in SslContextFactory");
                    m_sslCtxFactory.reload(scf -> {});
                } catch (Exception ex) {
                    LOG.info("Failed reloading SslCOntextFactory", ex);
                }
            }, m_reloadDelaySec); 
        }
    }

    @Override
    protected void doStop() throws Exception {
        m_fileWatcher.stopWatcher();
    }
}
