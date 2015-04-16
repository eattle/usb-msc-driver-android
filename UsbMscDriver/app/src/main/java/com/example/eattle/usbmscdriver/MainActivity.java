package com.example.eattle.usbmscdriver;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eattle.devicehost.device.BlockDevice;
import com.example.eattle.devicehost.host.BlockDeviceApp;
import com.example.eattle.devicehost.host.UsbDeviceHost;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private UsbDeviceHost usbDeviceHost;

    private void showToast(String text) {
        Log.i("Toast", text);
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void testMasterBootRecordMagic(BlockDevice blockDevice) {
        byte[] buffer = new byte[(int) blockDevice.getBlockLength()];
        blockDevice.readBlock(0, buffer);

        showToast("MBR MAGIC 1 " + Integer.toHexString(buffer[510] & 0xFF) + Integer.toHexString(buffer[511] & 0xFF));

        buffer[510] = 0x54;
        buffer[511] = (byte) 0xBB;
        blockDevice.writeBlock(0, buffer);

        buffer = new byte[(int) blockDevice.getBlockLength()];
        blockDevice.readBlock(0, buffer);
        showToast("MBR MAGIC 2 " + Integer.toHexString(buffer[510] & 0xFF) + Integer.toHexString(buffer[511] & 0xFF));

        buffer[510] = 0x55;
        buffer[511] = (byte) 0xAA;
        blockDevice.writeBlock(0, buffer);

        buffer = new byte[(int) blockDevice.getBlockLength()];
        blockDevice.readBlock(0, buffer);
        showToast("MBR MAGIC 3 " + Integer.toHexString(buffer[510] & 0xFF) + Integer.toHexString(buffer[511] & 0xFF));
    }

    private void testThroughput(BlockDevice blockDevice) {

        final int BLOCKS = 2048;

        byte[][] buffer = new byte[BLOCKS][(int) blockDevice.getBlockLength()];

        long beginRead = System.currentTimeMillis();
        for (int i = 0; i < BLOCKS; i++) {
            blockDevice.readBlock(i, buffer[i]);
        }
        long endRead = System.currentTimeMillis();
        long beginWrite = System.currentTimeMillis();
        for (int i = 0; i < BLOCKS; i++) {
            blockDevice.writeBlock(i, buffer[i]);
        }
        long endWrite = System.currentTimeMillis();

        long timeRead = endRead - beginRead;
        long timeWrite = endWrite - beginWrite;

        showToast("timeRead: " + String.valueOf(timeRead));
        showToast("timeWrite: " + String.valueOf(timeWrite));
        showToast("throughputRead: " + String.valueOf(BLOCKS * blockDevice.getBlockLength() * 8 / (timeRead / 1000.0) / 1024.0) + " kbps");
        showToast("throughputWrite: " + String.valueOf(BLOCKS * blockDevice.getBlockLength() * 8 / (timeWrite / 1000.0) / 1024.0) + " kbps");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        usbDeviceHost = new UsbDeviceHost();
        usbDeviceHost.start(this, new BlockDeviceApp() {
            @Override
            public void onConnected(BlockDevice blockDevice) {
                testMasterBootRecordMagic(blockDevice);
                testThroughput(blockDevice);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbDeviceHost.stop();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
