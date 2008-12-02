package com.aconex.monitoring.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.aconex.monitoring.MonitorableRegistry;
import com.aconex.monitoring.MonitoredCounter;
import com.aconex.monitoring.io.ByteCountingInputStream;

public class ByteCountingInputStreamTest extends TestCase {
    private static final byte[] TEST_BYTES = new byte[] { 0, 1, 2, 3, 4 };
    private MonitoredCounter counter = null;
    private ByteArrayInputStream bais;
    private ByteCountingInputStream pcpCS;
    private MonitorableRegistry registry = new MonitorableRegistry();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        counter = new MonitoredCounter("food", "", registry);
        this.bais = new ByteArrayInputStream(TEST_BYTES);
        this.pcpCS = new ByteCountingInputStream(this.bais, counter);
    }

    public void testBytesRead() throws IOException {
        assertEquals(0, counter.get().longValue());

        for (int i = 0; i < TEST_BYTES.length; i++) {
            assertEquals(TEST_BYTES[i], pcpCS.read());
            assertEquals(i + 1, counter.get().longValue());
        }
        long lastCounterValue = counter.get();
        
        assertEquals("Should have reached End Of Stream", -1, pcpCS.read());
        assertEquals("Counter should not have changed when attempt to read past EOS",
                lastCounterValue, counter.get().longValue());

    }
    
    

    public void testByteBlockRead() throws IOException {
        assertEquals(0, counter.get().longValue());

        byte[] testByteRange = new byte[TEST_BYTES.length];
        pcpCS.read(testByteRange);
        assertEquals("Should have counted all the reads", TEST_BYTES.length, counter.get()
                .longValue());
    }
    
    public void testByteBlockReadSmaller() throws IOException {
        assertEquals(0, counter.get().longValue());

        byte[] testByteRange = new byte[TEST_BYTES.length - 1];
        pcpCS.read(testByteRange);
        assertEquals("Should have counted all the reads", testByteRange.length, counter.get()
                .longValue());

    }

    public void testByteBlockReadSizeMismatch() throws IOException {
        assertEquals(0, counter.get().longValue());

        /*
         * Allocate larger block than the size we'll actually read to confirm it counts the right
         * number of _actual_ bytes read.
         */
        byte[] testByteRange = new byte[TEST_BYTES.length + 10];
        pcpCS.read(testByteRange);
        assertEquals("Should have counted all the reads", TEST_BYTES.length, counter.get()
                .longValue());
    }

    public void testByteBlockRangeRead() throws IOException {
        assertEquals(0, counter.get().longValue());

        byte[] testByteRange = new byte[TEST_BYTES.length];
        pcpCS.read(testByteRange, 0, TEST_BYTES.length);
        assertEquals("Should have counted all the reads", TEST_BYTES.length, counter.get()
                .longValue());
    }
    

    public void testByteBlockReadSizeSmaller() throws IOException {
        assertEquals(0, counter.get().longValue());

        byte[] testByteRange = new byte[TEST_BYTES.length - 2];
        pcpCS.read(testByteRange);
        assertEquals("Should have counted all the reads", testByteRange.length, counter.get()
                .longValue());
    }

    public void testByteBlockRangeReadSizeMismatch() throws IOException {
        assertEquals(0, counter.get().longValue());

        byte[] testByteRange = new byte[TEST_BYTES.length + 10];
        pcpCS.read(testByteRange, 0, TEST_BYTES.length);
        assertEquals("Should have counted all the reads", TEST_BYTES.length, counter.get()
                .longValue());
    }

    @Override
    protected void tearDown() throws Exception {
        counter = null;
        this.bais = null;
        this.pcpCS = null;
        super.tearDown();
    }
}