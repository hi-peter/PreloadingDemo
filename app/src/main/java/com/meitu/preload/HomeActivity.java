package com.meitu.preload;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.meitu.preload.info.DownloadInfo;
import com.meitu.preload.manager.PreloadingManager;
import com.meitu.preload.present.IDownloadContract;

import org.w3c.dom.Text;

import java.lang.reflect.Method;


public class HomeActivity extends AppCompatActivity {

    /**
     * 消息处理器
     */
    private Handler handler;
    /**
     * 流量信息对象
     */
    private TrafficBean trafficBean;
    /**
     * 服务
     */
    private ManagerService service;
    private TextView speed;
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = (TextView) findViewById(R.id.tv);
        mTv.setText("requestCount:");
        PreloadingManager.getInstance().setView(this, new IDownloadContract.Presenter() {
            @Override
            public void download(DownloadInfo downloadInfo) {

            }

            @Override
            public void cancel() {

            }

            @Override
            public void onRequestCountChange(final int requestCount) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv.setText(mTv.getText().toString() + requestCount + ",");
                    }
                });
            }
        });
        loadPic();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPic();
            }
        });

    }

    private void loadPic() {
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512553419348&di=1a29a0e858d829ace5e70fd02b244910&imgtype=jpg&src=http%3A%2F%2Fimg2.imgtn.bdimg.com%2Fit%2Fu%3D3397394342%2C3250084641%26fm%3D214%26gp%3D0.jpg").signature(new StringSignature(System.currentTimeMillis() + "")).into((ImageView) findViewById(R.id.iv));
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = ((ManagerService.ServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trafficBean.stopCalculateNetSpeed();
        unbindService(conn);
    }
}
