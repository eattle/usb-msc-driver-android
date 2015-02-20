package com.example.eattle.devicehost.host;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.example.eattle.devicehost.device.BlockDevice;
import com.example.eattle.devicehost.device.MyUsbSerialDevice;
import com.example.eattle.devicehost.device.UsbMassStorageBlockDevice;
import com.example.eattle.devicehost.device.UsbSerialDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hyeonguk on 15. 2. 20..
 */
public class UsbDeviceHost {
    private final static List<UsbDevice> mDevices = new ArrayList<UsbDevice>();
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {

                            UsbDeviceConnection connection = mUsbManager.openDevice(device);
                            UsbInterface usbInterface = device.getInterface(0);
                            connection.claimInterface(usbInterface, true);

                            usbInterface.getInterfaceSubclass();
                            UsbEndpoint readEndpoint = usbInterface.getEndpoint(0);
                            UsbEndpoint writeEndpoint = usbInterface.getEndpoint(1);

                            if (readEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                                UsbEndpoint temp = readEndpoint;
                                readEndpoint = writeEndpoint;
                                writeEndpoint = temp;
                            }

                            UsbSerialDevice serialDevice = new MyUsbSerialDevice(connection, readEndpoint, writeEndpoint);
                            BlockDevice blockDevice = new UsbMassStorageBlockDevice(serialDevice);

                            Log.i("getLastLBA", String.valueOf(blockDevice.getLastLogicalBlockAddress()));
                            Log.i("getBlockLength", String.valueOf(blockDevice.getBlockLength()));

                            app.onConnected(blockDevice);
                        } else {
                            // permission denied
                        }
                    }
                }
            }
        }
    };
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private BlockDeviceApp app;
    private Activity activity;

    public void start(Activity activity, BlockDeviceApp blockDeviceApp) {
        mUsbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        activity.registerReceiver(mUsbReceiver, filter);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            mUsbManager.requestPermission(device, mPermissionIntent);
        }
        app = blockDeviceApp;
        this.activity = activity;
    }

    public void stop() {
        activity.unregisterReceiver(mUsbReceiver);
    }
}
