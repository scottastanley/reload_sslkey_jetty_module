package com.bb.reload_ssl_keys;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The FileWatcher provides the logic for monitoring the directory containing a specified
 * file path for changes.  When the watcher is notified of a change in the file at the specified
 * path, then the run() method on the provided callback is executed.
 * 
 * @author Scott Stanley
 */
public class FileWatcher {
    static final Logger LOG = LoggerFactory.getLogger(FileWatcher.class);

    final private Path m_fileName;
    final private Path m_parentDir;
    final private WatchService m_watcher;
    private boolean m_stopped = false;
    final private Thread m_watcherThread;
    final private Debouncer m_callbackDebouncer;

    public FileWatcher(final Path filePath, final Runnable callback, final Long callbackDelaySeconds) 
            throws IOException {
        //
        // Configure the watcher
        //
        m_watcher = FileSystems.getDefault().newWatchService();
        
        m_fileName = filePath.getFileName();
        m_parentDir = filePath.getParent();
  
        m_parentDir.register(m_watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        
        //
        // Start the thread waiting on changes
        //
        m_callbackDebouncer = new Debouncer(callback, callbackDelaySeconds);
        Runnable runner = () -> {
            while (! m_stopped) {
                // Get the event key
                WatchKey key;
                try {
                    key = m_watcher.take();
                } catch (InterruptedException x) {
                    return;
                }
                
                // Process the events
                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE ||
                            kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path modifiedPath = Path.class.cast(event.context());
                        
                        if (modifiedPath.equals(m_fileName)) {
                            File updatedFile = m_parentDir.resolve(modifiedPath).toFile();
                            LOG.info("File modified: ", updatedFile.getAbsolutePath());
                            m_callbackDebouncer.trigger();
                        }
                    }
                }
                
                // Reset the key for future events
                key.reset();
            }
        };
        
        m_watcherThread = new Thread(runner);
        m_watcherThread.start();
    }
    
    /**
     * Stop the file watcher
     */
    public void stopWatcher() {
        m_stopped = true;
        m_watcherThread.interrupt();
        
        try {
            m_watcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
