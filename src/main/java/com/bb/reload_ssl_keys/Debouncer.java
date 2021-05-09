package com.bb.reload_ssl_keys;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Debouncer wraps around a simple Runnable object and provides the delay/debouncing logic 
 * preventing the run() method from being called until a specified timeout has passed.  All subsequent
 * triggers of execution during the timeout period reset the timer for the timeout, however they 
 * run() method is not called until the timeout has passed with no triggers.
 * 
 * @author Scott Stanley
 */
public class Debouncer {
    static final Logger LOG = LoggerFactory.getLogger(Debouncer.class);
    private final ScheduledExecutorService m_exec;
    private ScheduledFuture<?> m_future = null;
    private Long m_delaySeconds;
    private final Runnable m_callback;

    /**
     * Create a new Debouncer for the specified callback with the given delay.
     * 
     * @param callback The callback to be wrapped in the Debouncer
     * @param delaySeconds The number of seconds to delay before executing the callback
     */
    public Debouncer(final Runnable callback, final Long delaySeconds) {
        m_callback = callback;
        m_delaySeconds = delaySeconds;
        m_exec = Executors.newSingleThreadScheduledExecutor();
        LOG.debug("Created Debouncer(" + delaySeconds + "secs)");
    }

    /**
     * Trigger the execution of the callback after the delay.
     */
    public synchronized void trigger() {
        LOG.debug("Debouncer triggered: ");
        
        // If a scheduled job exists and has not run, cancel it
        if (m_future != null && !m_future.isDone()) {
            m_future.cancel(false);
            m_future = null;
            
            LOG.debug("Cancelled existing execution");
        }

        // Register the callback to run after the specified delay
        m_future = m_exec.schedule(m_callback, m_delaySeconds, TimeUnit.SECONDS);
        LOG.debug("Scheduled new execution");
    }
    
    /**
     * Shutdown the debouncer.
     */
    public synchronized void shutdown(final boolean awaitTermination) {
        LOG.debug("Shutting down debouncer");
        m_exec.shutdown();
        
        if (awaitTermination) {
            try {
                LOG.debug("Awating debouncer termination");
                m_exec.awaitTermination(m_delaySeconds, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                
            }
        }
    }
}
