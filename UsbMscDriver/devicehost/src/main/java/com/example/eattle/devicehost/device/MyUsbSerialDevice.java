package com.example.eattle.devicehost.device;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by hyeonguk on 15. 2. 19..
 */
public class MyUsbSerialDevice implements UsbSerialDevice {

    public static final int CBW_SIGNATURE = 0x43425355;
    public static final int CBS_SIGNATURE = 0x53425355;
    public static final int CBW_SIZE = 31;
    public static final int CBS_SIZE = 13;
    final static int TIMEOUT = 4096;
    final static int BUFFER_LENGTH = 512;
    final UsbDeviceConnection connection;
    final UsbEndpoint readEndpoint, writeEndpoint;
    public MyUsbSerialDevice(UsbDeviceConnection connection, UsbEndpoint readEndpoint, UsbEndpoint writeEndpoint) {
        this.connection = connection;
        this.readEndpoint = readEndpoint;
        this.writeEndpoint = writeEndpoint;
    }

    public static int swapEndianness(int i) {
        return ((i & 0xff) << 24) + ((i & 0xff00) << 8) + ((i & 0xff0000) >> 8) + ((i >> 24) & 0xff);
    }

    @Override
    public void read(byte[] data) {
        assert data.length == BUFFER_LENGTH;
        int response = connection.bulkTransfer(readEndpoint, data, data.length, TIMEOUT);
        if (response > 0) {
            if (response == CBS_SIZE) {
                int cswSignature = swapEndianness(ByteBuffer.wrap(Arrays.copyOfRange(data, 0, 4)).getInt());
                if (cswSignature == CBS_SIGNATURE) {
                    return;
                }
            }
        }
    }

    @Override
    public void write(byte[] data) {
        int result = connection.bulkTransfer(writeEndpoint, data, data.length, TIMEOUT);
    }
}
