package com.example.eattle.devicehost.scsi;

import junit.framework.TestCase;

/**
 * Created by hyeonguk on 15. 5. 4..
 */
public class ReadCapacity10ScsiCommandTest extends TestCase {

    final byte[] expected = new byte[]{
            0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00,
            0x00, 0x00, (byte) 0x80, 0x00, 0x0A, 0x25, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00
    };

    private void assertReadCapacity10(byte[] actual) {
        for (int i = 0; i < expected.length; i++) {
            if (4 <= i && i <= 7) {
                continue;
            }
            assertEquals(expected[i], actual[i]);
        }
    }

    private byte[] generateReadCapacity10ScsiCommand() {
        ReadCapacity10ScsiCommand command = new ReadCapacity10ScsiCommand();
        return command.generateCommand();
    }

    public void testGenerateCommand() throws Exception {
        byte[] a = generateReadCapacity10ScsiCommand();
        byte[] b = generateReadCapacity10ScsiCommand();
        assertReadCapacity10(a);
        assertReadCapacity10(b);
        RandomnessTester randomnessTester = new RandomnessTester();
        randomnessTester.testRandomTag(a, b);
    }
}