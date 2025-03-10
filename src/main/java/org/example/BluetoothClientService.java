package org.example;


/*
  Created with IntelliJ IDEA.

  @Author: Jcsim
 * @Date: 2020/11/25 15:17
 * @Description:蓝牙客户端业务类
 */
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.ConnectionNotFoundException;

公共

class BluetoothClientService {

    static void main(String[] argv) {

        final String serverUUID = "1000111230001000800000805F9B34FB"; //需要与服务端相同

        BluetoothClient client = new BluetoothClient();

        ArrayList<RemoteDevice> remoteDevices = new ArrayList<>();

        boolean isConnect = false;

        client.setOnDiscoverListener(remoteDevices::add);

        client.setOnClientListener(new BluetoothClient
        
        。OnClientListener() {

            @Override
            公共 void onConnected
            (DataInputStream inputStream
            ， OutputStream outputStream
                
            
                ) {
                System.out.print("Connected");
                // 开启线程读写蓝牙上接收和发送的数据。
                new Thread(() -> {
                    try {
                        System.out.println("客户端开始监听...");

//                            System.out.println("接收连接");
//                            System.out.println("开始读数据...");
                        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                        while (true) {
                            byte[] buffer = new byte[1024];
                            int bytes = 0;
                            int ch;
//                                inputStream.read(buffer)
                            while ((ch = inputStream.read()) != '\n') {
                                // 读数据。
//                                    String s = new String(buffer);
//                                    System.out.println("===========start=============");
//                                    System.out.println(s.trim());
//                                    System.out.println("------------end------------");
//                                    Thread.sleep(1000);
                                if (ch != -1) {
                                    buffer[bytes] = (byte) ch;
                                    bytes++;
                                }
                            }
                            buffer[bytes] = (byte) '\n';
                            bytes++;
                            String s = new String(buffer);
                            System.out.println("===========start=============");
                            System.out.println(df.format(new Date()) + "->" + s.trim());
                            System.out.println("------------end------------");

//                                inputStream.close();
//                                onClose();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            }

            @Override
            public void onConnectionFailed
                
            
                () {
                System.out.print("Connection failed");
            }

            @Override
            public void onDisconnected
            
            
            
            
            () {

            }

            @Override
            public void onClose
        
            
            
        
            () {

            }

        }
        
        );

        try {
            client.find();
            if (!remoteDevices.isEmpty()) {
                for (int i = 0; i < remoteDevices.size(); i++) {
                    System.out.println("第" + i + "个地址为：" + remoteDevices.get(i).getBluetoothAddress());
                    if ("所要连接的蓝牙的地址".equals(remoteDevices.get(i).getBluetoothAddress())) {
                        isConnect = true;
                        client.startClient(remoteDevices.get(i));
                        break;
                    }
                }
                if (!isConnect) {
                    System.out.println("请打开传感器蓝牙设备。");
                }
//                System.out.println("remoteDevices.firstElement="+remoteDevices.firstElement());
            } else {
                System.out.println("附件没有蓝牙设备");
            }
        } catch (ConnectionNotFoundException e) {
            System.out.println("当前蓝牙不在线");
            e.printStackTrace();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
