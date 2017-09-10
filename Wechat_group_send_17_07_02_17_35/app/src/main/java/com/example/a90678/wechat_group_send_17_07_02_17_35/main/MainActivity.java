package com.example.a90678.wechat_group_send_17_07_02_17_35.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.a90678.wechat_group_send_17_07_02_17_35.R;
import com.example.a90678.wechat_group_send_17_07_02_17_35.base.BaseSocketActivity;
import com.example.a90678.wechat_group_send_17_07_02_17_35.eventBusUtil.EventBusConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.SPUtils;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.TipUtils;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseSocketActivity {

    private EditText etIpSet;
    private Button btIpSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIpSet = (EditText) findViewById(R.id.main_ip_set_et);
        btIpSet = (Button) findViewById(R.id.main_ip_set_bt);

        btIpSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etIpText = etIpSet.getText().toString().trim();
                if (TextUtils.isEmpty(etIpText)) {
                    TipUtils.showTip(MainActivity.this, "请输入电脑ip地址");
                    return;
                }
                SPUtils.setIp(MainActivity.this, etIpText);
                startServices();
            }
        });

        initView();
    }

    private void initView() {
        String ip = SPUtils.getIp(this);
        if (TextUtils.isEmpty(ip)) {
            return;
        }
        etIpSet.setText(ip);
        startServices();
    }

    private void startServices() {
        startService(new Intent(this, MainService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_start_all_service:
                EventBus.getDefault().post(new EventBusConstants.StartAllService());
                break;
            case R.id.menu_main_stop_all_service:
                EventBus.getDefault().post(new EventBusConstants.StopAllService());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
