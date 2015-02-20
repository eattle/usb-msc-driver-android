package com.example.eattle.devicehost;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.example.eattle.devicehost.scsi.Read10ScsiCommand;
import com.example.eattle.devicehost.scsi.ReadCapacity10ScsiCommand;
import com.example.eattle.devicehost.scsi.Write10ScsiCommand;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    final byte[] readCapacity10Expected = new byte[]{
            0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00,
            0x00, 0x00, (byte) 0x80, 0x00, 0x0A, 0x25, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00
    };
    final byte[] read10Expected = new byte[]{
            0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
            0x00, 0x00, (byte) 0x80, 0x00, 0x0A, 0x28, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00
    };
    final byte[] write10Expected = new byte[]{
            0x55, 0x53, 0x42, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
            0x00, 0x00, (byte) 0x00, 0x00, 0x0A, 0x2A, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00
    };

    public ApplicationTest() {
        super(Application.class);
    }

    private void assertReadCapacity10(byte[] actual) {
        for (int i = 0; i < readCapacity10Expected.length; i++) {
            if (4 <= i && i <= 7) {
                continue;
            }
            assertEquals(readCapacity10Expected[i], actual[i]);
        }
    }

    private byte[] generateReadCapacity10ScsiCommand() {
        ReadCapacity10ScsiCommand command = new ReadCapacity10ScsiCommand();
        return command.generateCommand();
    }

    public void testReadCapacity10() {
        byte[] a = generateReadCapacity10ScsiCommand();
        byte[] b = generateReadCapacity10ScsiCommand();
        assertReadCapacity10(a);
        assertReadCapacity10(b);
        testRandomTag(a, b);
    }

    private void testRandomTag(byte[] a, byte[] b) {
        boolean notEquals = false;
        for (int i = 4; i <= 7; i++) {
            if (!Integer.valueOf(a[i]).equals(b[i])) {
                notEquals = true;
            }
        }
        assertTrue(notEquals);
    }

    private byte[] testRead10Lba(int lba) {
        byte[] actual = generateRead10ScsiCommand(lba);
        assertEquals(read10Expected.length, actual.length);
        for (int i = 0; i < read10Expected.length; i++) {
            if (4 <= i && i <= 7) {
                continue;
            }
            if (17 <= i && i <= 20) {
                continue;
            }
            assertEquals(read10Expected[i], actual[i]);
        }
        int base = 17;
        for (int i = 0; i < 4; i++) {
            byte actualLbaByte = actual[base + (3 - i)];
            byte expectedLbaByte = (byte) ((lba >> (i * 8)) & 0xFF);
            assertEquals(expectedLbaByte, actualLbaByte);
        }
        return actual;
    }

    private byte[] generateRead10ScsiCommand(int lba) {
        Read10ScsiCommand command = new Read10ScsiCommand();
        command.setLba(lba);
        return command.generateCommand();
    }

    private byte[] generateWrite10ScsiCommand(int lba) {
        Write10ScsiCommand command = new Write10ScsiCommand();
        command.setLba(lba);
        return command.generateCommand();
    }

    public void testRead10() {
        byte[] a = testRead10Lba(0);
        byte[] b = testRead10Lba(123456789);
        byte[] c = testRead10Lba((int) 2147483648L);
        testRandomTag(a, b);
        testRandomTag(b, c);
        testRandomTag(a, c);
    }

    private byte[] testWrite10Lba(int lba) {
        byte[] actual = generateWrite10ScsiCommand(lba);
        assertEquals(write10Expected.length, actual.length);
        for (int i = 0; i < write10Expected.length; i++) {
            if (4 <= i && i <= 7) {
                continue;
            }
            if (17 <= i && i <= 20) {
                continue;
            }
            assertEquals(write10Expected[i], actual[i]);
        }
        int base = 17;
        for (int i = 0; i < 4; i++) {
            byte actualLbaByte = actual[(3 - i) + base];
            byte expectedLbaByte = (byte) ((lba >> (i * 8)) & 0xFF);
            assertEquals(expectedLbaByte, actualLbaByte);
        }
        return actual;
    }

    public void testWrite10() {
        byte[] a = testWrite10Lba(0);
        byte[] b = testWrite10Lba(123456789);
        byte[] c = testWrite10Lba((int) 2147483648L);
        testRandomTag(a, b);
        testRandomTag(b, c);
        testRandomTag(a, c);
    }
}