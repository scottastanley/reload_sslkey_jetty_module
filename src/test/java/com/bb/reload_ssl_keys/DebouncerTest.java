package com.bb.reload_ssl_keys;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DebouncerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSingleTrigger_Delay1() {
        int numTriggers = 1;
        long delaySec = 1L;
        long expectedCallbacks = 1;
        
        try {
            TestMonitor mon = new TestMonitor();
            
            Debouncer db = new Debouncer(() -> {mon.callback();}, delaySec);
            
            // Record trigger time and trigger the debouncer
            mon.trigger();
            db.trigger();
            
            // Shutdown the debouncer and wait on it to terminate
            db.shutdown(true);
            mon.waitOnCompletion(expectedCallbacks);
            
            Assert.assertEquals("Unexpected number of triggers", numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec + " should be longer then the delay", 
                              lastCallbackDelaySec >= delaySec);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testSingleTrigger_Delay2() {
        int numTriggers = 1;
        long delaySec = 2L;
        long expectedCallbacks = 1;
        
        try {
            TestMonitor mon = new TestMonitor();
            
            Debouncer db = new Debouncer(() -> {mon.callback();}, delaySec);
            
            // Record trigger time and trigger the debouncer
            mon.trigger();
            db.trigger();
            
            // Shutdown the debouncer and wait on it to terminate
            db.shutdown(true);
            mon.waitOnCompletion(expectedCallbacks);
            
            Assert.assertEquals("Unexpected number of triggers", numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec + " should be longer then the delay", 
                              lastCallbackDelaySec >= delaySec);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testSingleTrigger_Delay3() {
        int numTriggers = 1;
        long delaySec = 3L;
        long expectedCallbacks = 1;
        
        try {
            TestMonitor mon = new TestMonitor();
            
            Debouncer db = new Debouncer(() -> {mon.callback();}, delaySec);
            
            // Record trigger time and trigger the debouncer
            mon.trigger();
            db.trigger();
            
            // Shutdown the debouncer and wait on it to terminate
            db.shutdown(true);
            mon.waitOnCompletion(expectedCallbacks);
            
            Assert.assertEquals("Unexpected number of triggers", numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec + " should be longer then the delay", 
                              lastCallbackDelaySec >= delaySec);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testFiveTriggers_Delay1() {
        int numTriggers = 5;
        long delaySec = 1L;
        long expectedCallbacks = 1;
        
        try {
            TestMonitor mon = new TestMonitor();
            
            Debouncer db = new Debouncer(() -> {mon.callback();}, delaySec);
            
            // Record trigger time and trigger the debouncer
            for (int n = 0; n < numTriggers; n++) {
                mon.trigger();
                db.trigger();
                Thread.sleep(2);
            }
            
            // Shutdown the debouncer and wait on it to terminate
            db.shutdown(true);
            mon.waitOnCompletion(expectedCallbacks);
            
            Assert.assertEquals("Unexpected number of triggers", numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec + " should be longer then the delay", 
                              lastCallbackDelaySec >= delaySec);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    @Test
    public void testTwentyTriggers_Delay2() {
        int numTriggers = 20;
        long delaySec = 1L;
        long expectedCallbacks = 1;
        
        try {
            TestMonitor mon = new TestMonitor();
            
            Debouncer db = new Debouncer(() -> {mon.callback();}, delaySec);
            
            // Record trigger time and trigger the debouncer
            for (int n = 0; n < numTriggers; n++) {
                mon.trigger();
                db.trigger();
                Thread.sleep(2);
            }
            
            // Shutdown the debouncer and wait on it to terminate
            db.shutdown(true);
            mon.waitOnCompletion(expectedCallbacks);
            
            Assert.assertEquals("Unexpected number of triggers", numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec + " should be longer then the delay", 
                              lastCallbackDelaySec >= delaySec);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }
    

    @Test
    public void testFiveTriggers_Delay1_MultiSequence() {
        int numTriggers = 5;
        long delaySec = 1L;
        long expectedCallbacks = 3;
        
        try {
            TestMonitor mon = new TestMonitor();
            
            Debouncer db = new Debouncer(() -> {mon.callback();}, delaySec);
            
            // Record trigger time and trigger the debouncer
            for (int n = 0; n < numTriggers; n++) {
                mon.trigger();
                db.trigger();
                Thread.sleep(2);
            }

            mon.waitOnCompletion(1);
            Assert.assertEquals("Unexpected number of triggers, pass 1", numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks, pass 1", 1, mon.getCallbackCount());
            double lastCallbackDelaySec1 = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec1 + " should be longer then the delay, pass 1", 
                              lastCallbackDelaySec1 >= delaySec);

            for (int n = 0; n < numTriggers; n++) {
                mon.trigger();
                db.trigger();
                Thread.sleep(2);
            }

            mon.waitOnCompletion(2);
            Assert.assertEquals("Unexpected number of triggers, pass 2", 2 * numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks, pass 2", 2, mon.getCallbackCount());
            double lastCallbackDelaySec2 = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec2 + " should be longer then the delay, pass 2", 
                              lastCallbackDelaySec2 >= delaySec);

            for (int n = 0; n < numTriggers; n++) {
                mon.trigger();
                db.trigger();
                Thread.sleep(2);
            }

            mon.waitOnCompletion(3);
            Assert.assertEquals("Unexpected number of triggers, pass 3", 3 * numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks, pass 3", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec3 = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec3 + " should be longer then the delay, pass 3", 
                              lastCallbackDelaySec3 >= delaySec);

            // Shutdown the debouncer and wait on it to terminate
            db.shutdown(true);
            
            Assert.assertEquals("Unexpected number of triggers", 3 * numTriggers, mon.getTriggerCount());
            Assert.assertEquals("Unexpected number of callbacks", expectedCallbacks, mon.getCallbackCount());
            double lastCallbackDelaySec = mon.getLastCallbackDelaySec();
            Assert.assertTrue("Delay " +  lastCallbackDelaySec + " should be longer then the delay", 
                              lastCallbackDelaySec >= delaySec);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Caught an unexpected exception: " + ex.getMessage());
        }
    }

    
    public class TestMonitor {
        private Long m_lastTriggerTime;
        private long m_triggerCount = 0;
        private Long m_lastCallbackTime;
        private long m_callbackCount = 0;
        
        public synchronized void trigger() {
            m_triggerCount++;
            m_lastTriggerTime = System.currentTimeMillis();
            this.notifyAll();
        }
        
        public synchronized void callback() {
            m_callbackCount++;
            m_lastCallbackTime = System.currentTimeMillis();
            this.notifyAll();
        }
        
        public long getTriggerCount() {
            return m_triggerCount;
        }
        
        public long getCallbackCount() {
            return m_callbackCount;
        }
        
        public double getLastCallbackDelaySec() {
            Long delayMillis = new Long(m_lastCallbackTime - m_lastTriggerTime);
            double delaySec = delayMillis.doubleValue() / 1000.0d;
            return delaySec;
        }
        
        public synchronized void waitOnCompletion(final long expectedCallbacks) throws InterruptedException {
            while (m_callbackCount < expectedCallbacks) {
                this.wait(1000L);
            }
        }
    }
}
