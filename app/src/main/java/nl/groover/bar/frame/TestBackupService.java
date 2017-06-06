package nl.groover.bar.frame;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TestBackupService extends Service {

	public class LocalBinder extends Binder {
        public TestBackupService getService() {
            return TestBackupService.this;
        }
    }
 
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    public void close() {
        
    }
 
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }
 
    private final IBinder mBinder = new LocalBinder();
 
}
