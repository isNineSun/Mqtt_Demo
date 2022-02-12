package com.example.mqtt_study;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient mqttAndroidClient;//建立Client客户端实例

    final String serverUri = "tcp://api.easylink.io";//服务器地址

    String clientId = "ExampleAndroidClient";//客户端标识，可以随便起，保证不重复即可
    final String subscriptionTopic = "exampleAndroidTopic";//订阅的主题
    final String publishTopic = "exampleAndroidPublishTopic";
    final String publishMessage = "Hello World!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //新建Client实例
        clientId = clientId + System.currentTimeMillis();//在clientId即客户端标识后面加上时间戳来保证唯一性
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        //新建回调函数
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            //连接完成
            public void connectComplete(boolean reconnect, String serverURI) {
            }
            //失去连接
            @Override
            public void connectionLost(Throwable cause) {
            }
            //信息送达
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                makeToast(new String(message.getPayload()));
            }
            //推送完成
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        //连接设置、是否自动重连、是否清理Session
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);


        //正式开始建立连接
        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    makeToast("连接成功");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    makeToast("连接失败");
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }

    }

    public void subscribeToTopic(){
        try {
            //开始订阅
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    makeToast("成功订阅"+subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    makeToast("未能订阅"+subscriptionTopic);
                }
            });

        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void publishMessage(){

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(publishTopic, message);
            if(!mqttAndroidClient.isConnected()){
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void makeToast(String str){
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
    }

}