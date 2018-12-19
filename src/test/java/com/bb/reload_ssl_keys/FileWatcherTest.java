package com.bb.reload_ssl_keys;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bb.reload_ssl_keys.FileWatcher;

public class FileWatcherTest {
    final static String TEST_PATH_BASE = "work/";
    final static String TEST_FILE_NAME = "someFile";

    @Before
    public void setUp() throws Exception {
        File testDir = new File(TEST_PATH_BASE);
        if (! testDir.exists()) {
            testDir.mkdirs();
        } else {
            if (! testDir.isDirectory()) {
                Assert.fail("Work directory exists but is not a directory");
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        File testDir = new File(TEST_PATH_BASE);
        if (testDir.exists()) {
            FileWatcherTest.deleteRecursively(testDir);
        }
    }
    
    /**
     * Delete the provided File and all children recursively, of it is a directory.
     * 
     * @param fileObj The base object to delete
     * @return True if the tree was completely deleted.
     */
    public static boolean deleteRecursively(final File fileObj) {
        if (! fileObj.exists()) {
            return true;
        }
        
        if (fileObj.isFile()) {
            return fileObj.delete();
        } else {
            boolean result = true;
            for (File i : fileObj.listFiles()) {
                result = result && FileWatcherTest.deleteRecursively(i);
            }            
            result = result && fileObj.delete();
            
            return result;
        }
    }
    
    /**
     * Update the last modified time on the file.
     * 
     * @param file
     */
    private static void createFile(final File file) 
            throws IOException {
        if (file.exists()) {
            Assert.fail("Test file already existed on create");
        } else {
            file.createNewFile();
            System.out.println("Created File: " + file.lastModified());
        }
    }
    
    private static void touchFile(final File file) 
            throws IOException, InterruptedException {
        if (file.exists()) {
            // Insure there is at least 1 second time difference 
            // from last file time
            long origLastModified = file.lastModified();
            long currentTime = System.currentTimeMillis();
            if (Math.abs(origLastModified - currentTime) < 1000) {
                Thread.sleep(1000 * 1);
                currentTime = System.currentTimeMillis();
            }
            
            // Touch the file
            file.setLastModified(currentTime);
            System.out.println("Updated File: " + file.lastModified());
        } else {
            Assert.fail("Test file does not exist on touch");
        }
    }
    
    private class TestControlClass {
        private final File m_testFile;
        private final int m_numTotalPasses;
        private int m_currentNumPasses = 0;
        private int m_observedCount = 0;
        
        TestControlClass(final int numTotalPasses,
                         final File testFile) {
            m_testFile = testFile;
            m_numTotalPasses = numTotalPasses;
        }
        
        void incrementObservedCount() {
            m_observedCount++;
        }
        
        int getObservedCount() {
            return m_observedCount;
        }
        
        synchronized void touchFile() 
                throws IOException, InterruptedException {
            if (m_currentNumPasses < m_numTotalPasses) {
                FileWatcherTest.touchFile(m_testFile);
                m_currentNumPasses++;
            } else {
                System.out.println("Notifying watchers");
                this.notifyAll();
            }
        }
        
        synchronized void waitOnTestCompletion() 
                throws InterruptedException {
            System.out.println("Waiting on test completion");
            this.wait(1000 * 60);
        }
    }

    @Test
    public void testOnePass() {
        long callbackDelay = 0L;
        int numPasses = 1;
        
        try {
            // Configure the test file and controller
            File testFile = new File(TEST_PATH_BASE + TEST_FILE_NAME);
            FileWatcherTest.createFile(testFile);

            final TestControlClass controller = new TestControlClass(numPasses, testFile);
            
            // Setup the watcher
            Path filePath = FileSystems.getDefault().getPath(testFile.getAbsolutePath());
            FileWatcher watcher = new FileWatcher(filePath, () -> {
                controller.incrementObservedCount();
                
                try {
                    controller.touchFile();
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail("Unexpected exception touching file: " + e.getMessage());
                }
            }, callbackDelay);

            //
            // Initiate the sequence by touching the file the first time
            //
            controller.touchFile();

            // Stop the watcher
            controller.waitOnTestCompletion();
            watcher.stopWatcher();
            
            // Verify the change count
            Assert.assertEquals("Wrong number of changes", numPasses, controller.getObservedCount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testTwoPasses() {
        long callbackDelay = 0L;
        int numPasses = 2;
        
        try {
            // Configure the test file and controller
            File testFile = new File(TEST_PATH_BASE + TEST_FILE_NAME);
            FileWatcherTest.createFile(testFile);

            final TestControlClass controller = new TestControlClass(numPasses, testFile);
            
            // Setup the watcher
            Path filePath = FileSystems.getDefault().getPath(testFile.getAbsolutePath());
            FileWatcher watcher = new FileWatcher(filePath, () -> {
                controller.incrementObservedCount();
                
                try {
                    controller.touchFile();
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail("Unexpected exception touching file: " + e.getMessage());
                }
            }, callbackDelay);

            //
            // Initiate the sequence by touching the file the first time
            //
            controller.touchFile();

            // Stop the watcher
            controller.waitOnTestCompletion();
            watcher.stopWatcher();
            
            // Verify the change count
            Assert.assertEquals("Wrong number of changes", numPasses, controller.getObservedCount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testThreePasses() {
        long callbackDelay = 0L;
        int numPasses = 3;
        
        try {
            // Configure the test file and controller
            File testFile = new File(TEST_PATH_BASE + TEST_FILE_NAME);
            FileWatcherTest.createFile(testFile);

            final TestControlClass controller = new TestControlClass(numPasses, testFile);
            
            // Setup the watcher
            Path filePath = FileSystems.getDefault().getPath(testFile.getAbsolutePath());
            FileWatcher watcher = new FileWatcher(filePath, () -> {
                controller.incrementObservedCount();
                
                try {
                    controller.touchFile();
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail("Unexpected exception touching file: " + e.getMessage());
                }
            }, callbackDelay);

            //
            // Initiate the sequence by touching the file the first time
            //
            controller.touchFile();

            // Stop the watcher
            controller.waitOnTestCompletion();
            watcher.stopWatcher();
            
            // Verify the change count
            Assert.assertEquals("Wrong number of changes", numPasses, controller.getObservedCount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testOnePass_Delay1() {
        long callbackDelay = 1L;
        int numPasses = 1;
        
        try {
            // Configure the test file and controller
            File testFile = new File(TEST_PATH_BASE + TEST_FILE_NAME);
            FileWatcherTest.createFile(testFile);

            final TestControlClass controller = new TestControlClass(numPasses, testFile);
            
            // Setup the watcher
            Path filePath = FileSystems.getDefault().getPath(testFile.getAbsolutePath());
            FileWatcher watcher = new FileWatcher(filePath, () -> {
                controller.incrementObservedCount();
                
                try {
                    controller.touchFile();
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail("Unexpected exception touching file: " + e.getMessage());
                }
            }, callbackDelay);

            //
            // Initiate the sequence by touching the file the first time
            //
            controller.touchFile();

            // Stop the watcher
            controller.waitOnTestCompletion();
            watcher.stopWatcher();
            
            // Verify the change count
            Assert.assertEquals("Wrong number of changes", numPasses, controller.getObservedCount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testTwoPasses_Delay1() {
        long callbackDelay = 1L;
        int numPasses = 2;
        
        try {
            // Configure the test file and controller
            File testFile = new File(TEST_PATH_BASE + TEST_FILE_NAME);
            FileWatcherTest.createFile(testFile);

            final TestControlClass controller = new TestControlClass(numPasses, testFile);
            
            // Setup the watcher
            Path filePath = FileSystems.getDefault().getPath(testFile.getAbsolutePath());
            FileWatcher watcher = new FileWatcher(filePath, () -> {
                controller.incrementObservedCount();
                
                try {
                    controller.touchFile();
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail("Unexpected exception touching file: " + e.getMessage());
                }
            }, callbackDelay);

            //
            // Initiate the sequence by touching the file the first time
            //
            controller.touchFile();

            // Stop the watcher
            controller.waitOnTestCompletion();
            watcher.stopWatcher();
            
            // Verify the change count
            Assert.assertEquals("Wrong number of changes", numPasses, controller.getObservedCount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testThreePasses_Delay1() {
        long callbackDelay = 1L;
        int numPasses = 3;
        
        try {
            // Configure the test file and controller
            File testFile = new File(TEST_PATH_BASE + TEST_FILE_NAME);
            FileWatcherTest.createFile(testFile);

            final TestControlClass controller = new TestControlClass(numPasses, testFile);
            
            // Setup the watcher
            Path filePath = FileSystems.getDefault().getPath(testFile.getAbsolutePath());
            FileWatcher watcher = new FileWatcher(filePath, () -> {
                controller.incrementObservedCount();
                
                try {
                    controller.touchFile();
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    Assert.fail("Unexpected exception touching file: " + e.getMessage());
                }
            }, callbackDelay);

            //
            // Initiate the sequence by touching the file the first time
            //
            controller.touchFile();

            // Stop the watcher
            controller.waitOnTestCompletion();
            watcher.stopWatcher();
            
            // Verify the change count
            Assert.assertEquals("Wrong number of changes", numPasses, controller.getObservedCount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }
}
