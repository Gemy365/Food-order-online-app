package com.example.android.eatitserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Model.MyResponse;
import com.example.android.eatitserver.Model.Notification;
import com.example.android.eatitserver.Model.Sender;
import com.example.android.eatitserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtMessage, edtTitle;
    FButton btnSend;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Init API Serves To Send Notifications For All Users.
        mService = Commons.getFCMClient();

        edtMessage = (MaterialEditText) findViewById(R.id.edt_message);

        edtTitle = (MaterialEditText) findViewById(R.id.edt_title);

        btnSend = (FButton) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Notification.
                Notification notification = new Notification(edtTitle.getText().toString(), edtMessage.getText().toString());

                Sender toTopic = new Sender();

                toTopic.to = new StringBuilder("/topics/").append("news").toString();

                toTopic.notification = notification;

                // Send Notifications For All Users.
                mService.sendNotification(toTopic)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.isSuccessful())
                                    Toast.makeText(SendMessage.this, "Message Sent", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessage.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }
}
