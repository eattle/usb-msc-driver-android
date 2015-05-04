package com.example.eattle.devicehost.scsi;

import junit.framework.TestCase;

/**
 * Created by hyeonguk on 15. 5. 4..
 */
public class Write10ScsiCommandTest extends TestCase {

    final byte[] expected = new byte[]{
            0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
            0x00, 0x00, (byte) 0x00, 0x00, 0x0A, 0x2A, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00
    };

    private byte[] generateWrite10ScsiCommand(int lba) {
        Write10ScsiCommand command = new Write10ScsiCommand();
        command.setLba(lba);
        return command.generateCommand();
    }

    private byte[] testWrite10Lba(int lba) {
        byte[] actual = generateWrite10ScsiCommand(lba);
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            if (4 <= i && i <= 7) {
                continue;
            }
            if (17 <= i && i <= 20) {
                continue;
            }
            assertEquals(expected[i], actual[i]);
        }
        int base = 17;
        for (int i = 0; i < 4; i++) {
            byte actualLbaByte = actual[(3 - i) + base];
            byte expectedLbaByte = (byte) ((lba >> (i * 8)) & 0xFF);
            assertEquals(expectedLbaByte, actualLbaByte);
        }
        return actual;
    }

    public void testGenerateCommand() throws Exception {
        byte[] a = testWrite10Lba(0);
        byte[] b = testWrite10Lba(123456789);
        byte[] c = testWrite10Lba((int) 2147483648L);
        RandomnessTester randomnessTester = new RandomnessTester();
        randomnessTester.testRandomTag(a, b);
        randomnessTester.testRandomTag(b, c);
        randomnessTester.testRandomTag(a, c);
    }
}