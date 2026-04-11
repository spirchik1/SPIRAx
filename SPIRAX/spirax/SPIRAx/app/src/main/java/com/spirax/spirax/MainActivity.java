package com.yourpackage.robloxlauncher; // Замени на имя твоего пакета

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int VPN_REQUEST_CODE = 0x0F;

    private Button btnPlayRoblox;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayRoblox = findViewById(R.id.btnPlayRoblox);
        tvStatus = findViewById(R.id.tvStatus);

        btnPlayRoblox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // При нажатии на кнопку запускаем подготовку к обходу
                prepareVpnAndLaunchRoblox();
            }
        });
    }

    private void prepareVpnAndLaunchRoblox() {
        tvStatus.setText("Запуск службы обхода...");
        
        // Запрашиваем у системы разрешение на создание VPN-соединения.
        // Это обязательный шаг для работы ByeDPI на Android.
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null) {
            // Если разрешение еще не дано, показываем системный диалог
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        } else {
            // Если разрешение уже есть, запускаем VPN-сервис
            startVpnService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Пользователь дал разрешение, можно запускать сервис
                startVpnService();
            } else {
                // Пользователь не дал разрешение — без него обход не заработает
                tvStatus.setText("Ошибка: нет разрешения для VPN");
            }
        }
    }

    private void startVpnService() {
        tvStatus.setText("Обход блокировки активен. Запуск Roblox...");
        
        // Здесь мы запускаем нашу службу обхода DPI.
        // Код самого сервиса ByeDpiVpnService мы напишем на следующем шаге.
        Intent intent = new Intent(this, ByeDpiVpnService.class);
        startService(intent);
        
        // После запуска сервиса, открываем официальное приложение Roblox
        launchRobloxApp();
    }

    private void launchRobloxApp() {
        // Ищем приложение Roblox в системе по его идентификатору (package name)
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.roblox.client");
        if (launchIntent != null) {
            // Если Roblox установлен, запускаем его
            startActivity(launchIntent);
        } else {
            // Если нет, показываем сообщение об ошибке
            tvStatus.setText("Roblox не установлен! Скачайте из Google Play.");
        }
    }
}