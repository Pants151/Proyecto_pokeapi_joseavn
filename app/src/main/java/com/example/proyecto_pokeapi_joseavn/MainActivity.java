package com.example.proyecto_pokeapi_joseavn;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar el sonido
        mediaPlayer = MediaPlayer.create(this, R.raw.sfx_press);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 1. Averiguar qué vista se ha tocado
            int x = (int) ev.getRawX();
            int y = (int) ev.getRawY();
            View touchedView = findTargetView(getWindow().getDecorView(), x, y);

            // 2. Comprobar si es un Botón o un Campo de Texto
            if (touchedView != null) {
                if (touchedView instanceof Button || touchedView instanceof EditText) {
                    playSound(); // ¡Solo suena aquí!
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Busca la vista exacta bajo el dedo
    private View findTargetView(View view, int x, int y) {
        // Si la vista no es visible, ignorarla
        if (view.getVisibility() != View.VISIBLE) return null;

        // Comprobar si el toque está dentro de las coordenadas de esta vista
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();

        if (x < left || x > right || y < top || y > bottom) {
            return null; // El toque está fuera
        }

        // Si es un contenedor (Layout), buscar primero en sus hijos (los botones están dentro)
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            // Recorremos al revés para encontrar el elemento que está más "arriba" visualmente
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                View target = findTargetView(group.getChildAt(i), x, y);
                if (target != null) return target;
            }
        }

        // Si no es un grupo, o ninguno de sus hijos fue tocado, devolvemos la vista actual
        return view;
    }

    private void playSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.sfx_press);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}