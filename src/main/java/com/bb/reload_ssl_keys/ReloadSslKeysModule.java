package com.bb.reload_ssl_keys;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class ReloadSslKeysModule extends AbstractLifeCycle {
    static final Logger LOG = Log.getLogger(ReloadSslKeysModule.class);
    
    private SslContextFactory m_sslCtxFactory = null;
    private FileWatcher m_fileWatcher = null;


    public ReloadSslKeysModule() {
    }
    
    public void setSslContextFactory(final SslContextFactory sslCtxFactory) {
        m_sslCtxFactory = sslCtxFactory;
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
            }); 
        }
    }

    @Override
    protected void doStop() throws Exception {
        m_fileWatcher.stopWatcher();
    }
}
