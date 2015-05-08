package com.example.eattle.devicehost.device;

import junit.framework.TestCase;

/**
 * Created by hyeonguk on 15. 5. 4..
 */
public class CachedUsbMassStorageBlockDeviceTest extends TestCase {

    CachedUsbMassStorageBlockDevice cachedUsbMassStorageBlockDevice;
    MockBlockDevice mockBlockDevice;

    public void setUp() {
        mockBlockDevice = new MockBlockDevice();
        cachedUsbMassStorageBlockDevice = new CachedUsbMassStorageBlockDevice(mockBlockDevice);
    }

    public void tearDown() {
        mockBlockDevice = null;
        cachedUsbMassStorageBlockDevice = null;
    }

    public void testReadBlockBasic() throws Exception {
        byte[] buffer = new byte[512];
        assertEquals(0, mockBlockDevice.getReadCount());
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        assertEquals(1, mockBlockDevice.getReadCount());
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        assertEquals(2, mockBlockDevice.getReadCount());
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        assertEquals(3, mockBlockDevice.getReadCount());
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        assertEquals(4, mockBlockDevice.getReadCount());
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        assertEquals(5, mockBlockDevice.getReadCount());
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        assertEquals(5, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
    }

    public void testReadBlockBig() throws Exception {
        byte[] buffer = new byte[512];
        for (int i = 0; i < 64; i++) {
            assertEquals(i, mockBlockDevice.getReadCount());
            cachedUsbMassStorageBlockDevice.readBlock(i, buffer);
            assertEquals(i + 1, mockBlockDevice.getReadCount());
        }
        assertEquals(64, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        for (int i = 64 - 8; i < 64; i++) {
            cachedUsbMassStorageBlockDevice.readBlock(i, buffer);
        }
        assertEquals(64, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        for (int i = 0; i < 64; i++) {
            cachedUsbMassStorageBlockDevice.readBlock(i, buffer);
        }
        assertEquals(128, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
    }

    public void testWriteBlockBasic() throws Exception {
        byte[] buffer = new byte[512];
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(1, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(2, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(3, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(4, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(5, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(10, mockBlockDevice.getWriteCount());
        assertEquals(0, mockBlockDevice.getReadCount());
    }

    public void testWriteBlockBig() throws Exception {
        byte[] buffer = new byte[512];
        for (int i = 0; i < 64; i++) {
            cachedUsbMassStorageBlockDevice.flush();
            assertEquals(i, mockBlockDevice.getWriteCount());
            cachedUsbMassStorageBlockDevice.writeBlock(i, buffer);
            cachedUsbMassStorageBlockDevice.flush();
            assertEquals(i + 1, mockBlockDevice.getWriteCount());
        }
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(64, mockBlockDevice.getWriteCount());
        assertEquals(0, mockBlockDevice.getReadCount());
        for (int i = 64 - 8; i < 64; i++) {
            cachedUsbMassStorageBlockDevice.writeBlock(i, buffer);
        }
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(64 + 8, mockBlockDevice.getWriteCount());
        assertEquals(0, mockBlockDevice.getReadCount());
        for (int i = 0; i < 64; i++) {
            cachedUsbMassStorageBlockDevice.writeBlock(i, buffer);
        }
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(64 + 8 + 64, mockBlockDevice.getWriteCount());
        assertEquals(0, mockBlockDevice.getReadCount());
    }

    public void testReadAndWrite() throws Exception {
        byte[] buffer = new byte[512];
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(4, buffer);
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(5, buffer);
        assertEquals(1, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(5, buffer);
        assertEquals(1, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(5, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(6, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(7, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(8, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(9, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(10, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(11, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(12, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(13, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(14, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(15, buffer);
        assertEquals(11, mockBlockDevice.getReadCount());
        assertEquals(6, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(10, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(11, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(12, buffer);
        assertEquals(11, mockBlockDevice.getReadCount());
        assertEquals(6, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(11, mockBlockDevice.getReadCount());
        assertEquals(9, mockBlockDevice.getWriteCount());

    }

    public void testFlush() throws Exception {
        byte[] buffer = new byte[512];
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(0, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(5, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(5, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(0, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(1, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(2, buffer);
        cachedUsbMassStorageBlockDevice.writeBlock(3, buffer);
        cachedUsbMassStorageBlockDevice.readBlock(4, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(0, mockBlockDevice.getReadCount());
        assertEquals(9, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.readBlock(5, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(1, mockBlockDevice.getReadCount());
        assertEquals(9, mockBlockDevice.getWriteCount());
        cachedUsbMassStorageBlockDevice.writeBlock(5, buffer);
        cachedUsbMassStorageBlockDevice.flush();
        assertEquals(1, mockBlockDevice.getReadCount());
        assertEquals(10, mockBlockDevice.getWriteCount());
    }
}