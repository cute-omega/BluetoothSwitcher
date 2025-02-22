package org.example;

/*
  Created with IntelliJ IDEA.

  @Author: Jcsim
 * @Date: 2020/11/25 15:14
 * @Description:蓝牙客户端类
 */

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothClient {

    private OnDiscoverListener onDiscoverListener = null;//发现监听
    private OnClientListener onClientListener = null;//客户端监听

    /**
     * 客户端监听
     */
    public interface OnClientListener {

        void onConnected(DataInputStream inputStream, OutputStream outputStream);

        void onConnectionFailed();

        void onDisconnected();

        void onClose();
    }

    /**
     * 发现监听
     */
    public interface OnDiscoverListener {

        void onDiscover(RemoteDevice remoteDevice);
    }

    /**
     * 无参构造函数
     */
    public BluetoothClient() { }

    /**
     * 查找所有
     */
    public void find() throws IOException, InterruptedException {
        //附近所有的蓝牙设备，必须先执行 runDiscovery
        Set<RemoteDevice> devicesDiscovered = RemoteDeviceDiscovery.getDevices();
        //连接
        for (RemoteDevice remoteDevice : devicesDiscovered) {
            onDiscoverListener.onDiscover(remoteDevice);
        }
    }

    /**
     * 启动连接
     */
    public void startClient(RemoteDevice remoteDevice) {
//        String url = RemoteDeviceDiscovery.searchService(remoteDevice, serviceUUID);
//        System.out.println("url=="+url);
//        1 为通道;authenticate=true;encrypt=true表示需要验证pin码
//        btspp://<蓝牙设备地址>:<通道号>
        String url = "btspp://" + remoteDevice.getBluetoothAddress() + ":1;authenticate=true;encrypt=true";
        try {
            //流连接
            StreamConnection streamConnection = (StreamConnection) Connector.open(url);
            if (this.onClientListener != null) {
                this.onClientListener.onConnected(streamConnection.openDataInputStream(), streamConnection.openOutputStream());
            } else {
                System.out.println("请打开蓝牙");
            }
        } catch (BluetoothConnectionException e) {
            e.printStackTrace();
            System.out.println("蓝牙连接错误，请查看蓝牙是否打开。");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public OnDiscoverListener getOnDiscoverListener() {
        return onDiscoverListener;
    }

    public void setOnDiscoverListener(OnDiscoverListener onDiscoverListener) {
        this.onDiscoverListener = onDiscoverListener;
    }

    public OnClientListener getOnClientListener() {
        return onClientListener;
    }

    public void setOnClientListener(OnClientListener onClientListener) {
        this.onClientListener = onClientListener;
    }

}