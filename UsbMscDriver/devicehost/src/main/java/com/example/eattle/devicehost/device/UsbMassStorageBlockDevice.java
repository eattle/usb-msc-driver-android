package com.example.eattle.devicehost.device;

import com.example.eattle.devicehost.scsi.Read10ScsiCommand;
import com.example.eattle.devicehost.scsi.ReadCapacity10ScsiCommand;
import com.example.eattle.devicehost.scsi.Write10ScsiCommand;

/**
 * Created by hyeonguk on 15. 2. 19..
 */
public class UsbMassStorageBlockDevice implements BlockDevice {

    final private UsbSerialDevice usbSerialDevice;
    final private long lastLogicalBlockAddress, blockLength;

    public UsbMassStorageBlockDevice(UsbSerialDevice usbSerialDevice) {
        this.usbSerialDevice = usbSerialDevice;
        ReadCapacity10ScsiCommand command = new ReadCapacity10ScsiCommand();
        usbSerialDevice.write(command.generateCommand());
        byte[] readBuffer = new byte[512];
        usbSerialDevice.read(readBuffer);

        byte[] csw = new byte[512];
        usbSerialDevice.read(csw);

        lastLogicalBlockAddress = ((((int) readBuffer[0]) & 0xff) << 24) + ((((int) readBuffer[1]) & 0xff) << 16) + ((((int) readBuffer[2]) & 0xff) << 8) + (((int) readBuffer[3]) & 0xff);
        blockLength = (readBuffer[4] << 24) + (readBuffer[5] << 16) + (readBuffer[6] << 8) + readBuffer[7];
    }

    @Override
    public void readBlock(int lba, byte[] buffer) {
        Read10ScsiCommand command = new Read10ScsiCommand();
        command.setLba(lba);
        usbSerialDevice.write(command.generateCommand());
        byte[] readBuffer = new byte[512];
        usbSerialDevice.read(readBuffer);
        System.arraycopy(readBuffer, 0, buffer, 0, 512);

        byte[] csw = new byte[512];
        usbSerialDevice.read(csw);
    }

    @Override
    public void writeBlock(int lba, byte[] buffer) {
        Write10ScsiCommand command = new Write10ScsiCommand();
        command.setLba(lba);
        usbSerialDevice.write(command.generateCommand());
        usbSerialDevice.write(buffer);
        byte[] csw = new byte[512];
        usbSerialDevice.read(csw);
    }

    @Override
    public long getLastLogicalBlockAddress() {
        return lastLogicalBlockAddress;
    }

    @Override
    public long getBlockLength() {
        return blockLength;
    }
}
