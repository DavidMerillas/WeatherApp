package com.example.davidmerillas.weatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentWeather())
                    .commit();
        }


        // Bot�n de ayuda
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirCiudad();
            }
        });
    }

    /**
     * Metodo que muestra la ventana donde podremos cambiar la ciudad
     */
    private void pedirCiudad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setTitle("Introduce Ciudad...");
        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cambiarCiudad(input.getText().toString());
            }
        });
        builder.show();
    }

    /**
     * Metodo que cambia la ciudad
     *
     * @param ciudad - Ciudad que queremos visualizar
     */
    public void cambiarCiudad(String ciudad) {
        FragmentWeather wf = (FragmentWeather) getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.cambiarCiudad(ciudad);
    }
}
