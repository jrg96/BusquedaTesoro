package com.hfad.servicio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hfad.busquedatesoro.R;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

public class ServicioNotificacion extends Service {
    private Timer timer = new Timer();
    private TimerTask tareaNotificacion;
    private Handler handler = new Handler(Looper.getMainLooper());

    private FirebaseFirestore firebaseFirestore;

    private int cuenta_anterior = 0;
    private int cuenta_actual = 0;

    public ServicioNotificacion() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("tesoros").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        cuenta_actual++;
                    }
                }
            }
        });


        tareaNotificacion = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (cuenta_actual  != cuenta_anterior) {
                            cuenta_anterior = cuenta_actual;

                            Notification noti = new Notification.Builder(ServicioNotificacion.this)
                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                    .setContentTitle("Busqueda de tesoro")
                                    .setContentText("Nuevos tesoros han aparecido, ve a encontrarlos!")
                                    .build();

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            noti.flags |= Notification.FLAG_AUTO_CANCEL;
                            notificationManager.notify(0, noti);
                        }
                    }
                });
            }
        };

        timer.schedule(tareaNotificacion, 0, 5000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
