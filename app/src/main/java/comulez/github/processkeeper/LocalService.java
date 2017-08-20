package comulez.github.processkeeper;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import comulez.github.processkeeper.aidl.IProcessAidlInterface;

public class LocalService extends Service {
    private String TAG = "LocalService";

    private MyBinder binder;
    private MyConn conn;

    public LocalService() {
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        binder = new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (conn == null) {
            conn = new MyConn();
            bindService(new Intent(this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return binder;
    }

    class MyBinder extends IProcessAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return "LocalService";
        }
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "连接远程服务。");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "断开远程服务。");
            Toast.makeText(LocalService.this, "远程服务被杀死", Toast.LENGTH_SHORT).show();
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        Log.i(TAG, "onDestroy");
    }
}
