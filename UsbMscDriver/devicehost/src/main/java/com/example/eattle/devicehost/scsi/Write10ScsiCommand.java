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
                0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, (byte) 0x00, 0x00, 0x0A, 0x2A, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00
        };
        for (int i = 4; i <= 7; i++) {
            buffer[i] = tag[i - 4];
        }

        int base = 17;
        int lbaV = getLba();
        for (int i = 0; i < 4; i++) {
            int position = base + (3 - i);
            buffer[position] = (byte) (lbaV & 0xFF);
            lbaV = (lbaV & 0xFFFFFF00) >> 8;
        }
        return buffer;
    }

    public int getLba() {
        return lba;
    }

    public void setLba(int lba) {
        this.lba = lba;
    }
}
