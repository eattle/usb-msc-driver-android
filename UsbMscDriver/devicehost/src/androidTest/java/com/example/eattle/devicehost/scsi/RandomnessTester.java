package com.example.eattle.devicehost.scsi;

import static junit.framework.Assert.assertTrue;

/**
 * Created by hyeonguk on 15. 5. 4..
 */
public class RandomnessTester {
    public void testRandomTag(byte[] a, byte[] b) {
        boolean notEquals = false;
        for (int i = 4; i <= 7; i++) {
            if (!Integer.valueOf(a[i]).equals(b[i])) {
                notEquals = true;
            }
        }
        assertTrue(notEquals);
    }
}
