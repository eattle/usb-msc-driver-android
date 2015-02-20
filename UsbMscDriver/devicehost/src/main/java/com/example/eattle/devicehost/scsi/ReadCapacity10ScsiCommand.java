package com.example.eattle.devicehost.scsi;

import java.util.Random;

/**
 * Created by hyeonguk on 15. 2. 19..
 */
public class ReadCapacity10ScsiCommand implements ScsiCommand {
    @Override
    public byte[] generateCommand() {
        Random random = new Random();
        byte[] tag = new byte[4];
        random.nextBytes(tag);
        byte[] buffer = new byte[]{
                0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00,
                0x00, 0x00, (byte) 0x80, 0x00, 0x0A, 0x25, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00
        };
        for (int i = 4; i <= 7; i++) {
            buffer[i] = tag[i - 4];
        }
        return buffer;
    }
}
