package com.example.davidmerillas.weatherapp;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class FragmentWeather extends Fragment {


    Typeface weatherFont;

    TextView ciudad;
    TextView actualizado;
    TextView detalles;
    TextView temperatura;
    TextView icono;

    Handler handler;

    public FragmentWeather() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fuente con iconos para el tiempo.
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        actualizarTiempo("Madrid,ES");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        ciudad = (TextView) rootView.findViewById(R.id.ciudad);
        actualizado = (TextView) rootView.findViewById(R.id.actualizado);
        detalles = (TextView) rootView.findViewById(R.id.detalles);
        temperatura = (TextView) rootView.findViewById(R.id.tempe);
        icono = (TextView) rootView.findViewById(R.id.icono);

        icono.setTypeface(weatherFont);
        return rootView;
    }


    /**
     * Metodo que actualiza el tiempo de una ciudad
     *
     * @param ciudad - Ciudad recibida como parametro
     */
    private void actualizarTiempo(final String ciudad) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), ciudad);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.no_existe),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            parseaTiempo(json);
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * Metodo que parsea los elementos recibidos en el JSON y los muestra por pantalla
     *
     * @param json - JSON con los datos de la ciudad
     */
    private void parseaTiempo(JSONObject json) {
        try {
            ciudad.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));
            ciudad.setTextColor(Color.WHITE);
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detalles.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humedad: " + main.getString("humidity") + "%" +
                            "\n" + "Presión: " + main.getString("pressure") + " hPa");

            temperatura.setText(
                    String.format("%.2f", main.getDouble("temp")) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            actualizado.setText("Última actualización: " + updatedOn);

            dibujaIcono(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.e("WeatherApp", "Falta uno o mas elementos dentro del JSON");
        }
    }

    /**
     * Metodo que recibe el icono del tiempo que hace y lo dibuja en pantalla
     *
     * @param actualId
     * @param amanecer
     * @param atardecer
     */
    private void dibujaIcono(int actualId, long amanecer, long atardecer) {
        int id = actualId / 100;
        String icon = "";
        int color=Color.BLACK;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= amanecer && currentTime < atardecer) {
                icon = getActivity().getString(R.string.sol);
                color=Color.YELLOW;
            } else {
                icon = getActivity().getString(R.string.noche);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.tormenta);
                    color=Color.DKGRAY;
                    break;
                case 3:
                    icon = getActivity().getString(R.string.llovizna);
                    color=Color.GRAY;
                    break;
                case 7:
                    icon = getActivity().getString(R.string.niebla);
                    color=Color.GRAY;
                    break;
                case 8:
                    icon = getActivity().getString(R.string.nublado);
                    color=Color.GRAY;
                    break;
                case 6:
                    icon = getActivity().getString(R.string.nieve);
                    color=Color.WHITE;
                    break;
                case 5:
                    icon = getActivity().getString(R.string.lluvia);
                    color=Color.DKGRAY;
                    break;
            }
        }
        this.icono.setTextColor(color);
        this.icono.setText(icon);
    }

    /**
     * Metodo que actualiza el cambio de ciudad
     *
     * @param ciudad
     */
    public void cambiarCiudad(String ciudad) {
        actualizarTiempo(ciudad);
    }

}
