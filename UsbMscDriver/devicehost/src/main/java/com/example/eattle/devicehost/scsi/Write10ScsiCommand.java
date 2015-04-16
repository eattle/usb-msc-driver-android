package com.example.eattle.devicehost.scsi;

import java.util.Random;

/**
 * Created by hyeonguk on 15. 2. 19..
 */
public class Write10ScsiCommand implements ScsiCommand {
    private int lba;

    @Override
    public byte[] generateCommand() {

        Random random = new Random();
        byte[] tag = new byte[4];
        random.nextBytes(tag);

        byte[] buffer = new byte[]{
                0x55, 0x53, 0x42, 0x43, tag[0], tag[1], tag[2], tag[3], 0x00, 0x02,
                0x00, 0x00, (byte) 0x00, 0x00, 0x0A, 0x2A, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00
        };

        final int lbaValue = getLba();
        buffer[20] = (byte) (lbaValue & 0xFF);
        buffer[19] = (byte) (((lbaValue & 0xFF00) >> 8) & 0xFF);
        buffer[18] = (byte) (((lbaValue & 0xFF0000) >> 16) & 0xFF);
        buffer[17] = (byte) (((lbaValue & 0xFF000000) >> 24) & 0xFF);

        return buffer;
    }

    public int getLba() {
        return lba;
    }

    public void setLba(int lba) {
        this.lba = lba;
    }
}
