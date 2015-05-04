package com.example.eattle.devicehost.device;

/**
 * Created by hyeonguk on 15. 5. 4..
 */
public class MockBlockDevice implements BlockDevice {

    private int readCount = 0;
    private int writeCount = 0;

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(int writeCount) {
        this.writeCount = writeCount;
    }

    @Override
    public void readBlock(int lba, byte[] buffer) {
        setReadCount(getReadCount() + 1);
    }

    @Override
    public void writeBlock(int lba, byte[] buffer) {
        setWriteCount(getWriteCount() + 1);
    }

    @Override
    public long getLastLogicalBlockAddress() {
        return 1024;
    }

    @Override
    public long getBlockLength() {
        return 512;
    }
}
