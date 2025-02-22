package org.example;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BluetoothHeadsetManager {

    private static final Logger logger = Logger.getLogger(BluetoothHeadsetManager.class.getName());
    private static final Set<RemoteDevice> devicesDiscovered = new HashSet<>();
    private static final Object inquiryCompletedEvent = new Object();
    private static final Object serviceSearchCompletedEvent = new Object();
    private static final DiscoveryListener listener = new DiscoveryListener() {
        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            devicesDiscovered.add(remoteDevice);
            try {
                logger.info("Device discovered: " + remoteDevice.getFriendlyName(false) + " - " + remoteDevice.getBluetoothAddress());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error getting device name", e);
            }
        }

        @Override
        public void inquiryCompleted(int discType) {
            logger.info("Device inquiry completed");
            synchronized (inquiryCompletedEvent) {
                inquiryCompletedEvent.notifyAll();
            }
        }

        @Override
        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            for (ServiceRecord serviceRecord : servRecord) {
                String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url != null) {
                    logger.info("Service discovered: " + url);
                }
            }
            synchronized (serviceSearchCompletedEvent) {
                serviceSearchCompletedEvent.notifyAll();
            }
        }

        @Override
        public void serviceSearchCompleted(int transID, int respCode) {
            logger.info("Service search completed");
            synchronized (serviceSearchCompletedEvent) {
                serviceSearchCompletedEvent.notifyAll();
            }
        }
    };

    public static Set<RemoteDevice> discoverDevices() throws IOException, InterruptedException {
        devicesDiscovered.clear();
        synchronized (inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                logger.info("Starting device inquiry...");
                inquiryCompletedEvent.wait();
            }
        }
        return devicesDiscovered;
    }

    public static void connectToDevice(RemoteDevice device) {
        String url = "btspp://" + device.getBluetoothAddress() + ":1;authenticate=true;encrypt=true";
        StreamConnection connection = null;
        DataInputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            connection = (StreamConnection) Connector.open(url);
            inputStream = connection.openDataInputStream();
            outputStream = connection.openOutputStream();
            logger.info("Connected to device: " + device.getFriendlyName(false));
            // Handle data communication here
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to connect to device", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to close streams or connection", e);
            }
        }
    }


    public static void disconnectFromDevice() {
        // Implement disconnection logic here
        logger.info("Disconnected from device");
    }

    public static void main(String[] args) {
        try {
            Set<RemoteDevice> devices = discoverDevices();
            for (RemoteDevice device : devices) {
                logger.info("Discovered device: " + device.getFriendlyName(false) + " - " + device.getBluetoothAddress());
                // Connect to the first discovered device for demonstration
                connectToDevice(device);
                break;
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error during Bluetooth operation", e);
        }
    }
}