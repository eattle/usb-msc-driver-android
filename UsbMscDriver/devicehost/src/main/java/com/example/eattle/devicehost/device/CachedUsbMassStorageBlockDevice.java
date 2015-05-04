package com.example.eattle.devicehost.device;

/**
 * Created by hyeonguk on 15. 5. 4..
 */
public class CachedUsbMassStorageBlockDevice implements BlockDevice {

    private BlockDevice blockDevice;

    private final int CacheSize = 8;
    private final Object[] cache = new Object[CacheSize];
    private final int[] tag = new int[CacheSize];
    private final boolean[] referenced = new boolean[CacheSize];

    private int clock = 0;

    public CachedUsbMassStorageBlockDevice(BlockDevice blockDevice) {
        this.blockDevice = blockDevice;

        for (int i = 0; i < CacheSize; i++) {
            tag[i] = -1;
            cache[i] = new byte[(int) blockDevice.getBlockLength()];
        }
    }

    @Override
    public void readBlock(int lba, byte[] buffer) {
        for (int i = 0; i < CacheSize; i++) {
            if (((Integer) lba).equals(tag[i])) {
                referenced[i] = true;
                System.arraycopy((byte[]) cache[i], 0, buffer, 0, (int) blockDevice.getBlockLength());
                return;
            }
        }
        while (referenced[getClock()]) {
            referenced[getClock()] = false;
            incrementClock();
        }
        referenced[getClock()] = true;
        tag[getClock()] = lba;
        blockDevice.readBlock(lba, (byte[]) cache[getClock()]);
        System.arraycopy((byte[]) cache[getClock()], 0, buffer, 0, (int) blockDevice.getBlockLength());
        incrementClock();
    }

    @Override
    public void writeBlock(int lba, byte[] buffer) {
        blockDevice.writeBlock(lba, buffer);
        for (int i = 0; i < CacheSize; i++) {
            if (((Integer) lba).equals(tag[i])) {
                referenced[i] = true;
                System.arraycopy(buffer, 0, (byte[]) cache[i], 0, (int) blockDevice.getBlockLength());
                return;
            }
        }
        while (referenced[getClock()]) {
            referenced[getClock()] = false;
            incrementClock();
        }
        referenced[getClock()] = true;
        tag[getClock()] = lba;
        System.arraycopy(buffer, 0, (byte[]) cache[getClock()], 0, (int) blockDevice.getBlockLength());
        incrementClock();
    }

    @Override
    public long getLastLogicalBlockAddress() {
        return blockDevice.getLastLogicalBlockAddress();
    }

    @Override
    public long getBlockLength() {
        return blockDevice.getBlockLength();
    }

    private int getClock() {
        return clock;
    }

    private void incrementClock() {
        clock++;
        while (clock >= CacheSize) {
            clock -= CacheSize;
        }
    }
}
