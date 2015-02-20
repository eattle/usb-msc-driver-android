package com.example.eattle.devicehost.scsi;

/**
 * Created by hyeonguk on 15. 2. 15..
 */
public interface ScsiCommand {
    public abstract byte[] generateCommand();
}
