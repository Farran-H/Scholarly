package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsAct extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private MapView mMapView;
    private boolean cameraMovedByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // Initialize the MapView
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
    }

    

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Get a reference to the GoogleMap object
        mMap = googleMap;

        // Set a OnCameraMoveStartedListener to detect if the user moved the camera
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    cameraMovedByUser = true;
                }
            }
        });

        // Enable the My Location layer and set an OnMyLocationChangeListener to get the user's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (!cameraMovedByUser) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16f));
                    }
                }
            });
        }

        // Set the custom InfoWindowAdapter
        mMap.setInfoWindowAdapter(this);



        // Add markers to the map
        LatLng csLatLng = new LatLng(52.673904,-8.575619);
        mMap.addMarker(new MarkerOptions()
                .position(csLatLng)
                .title("Computer Science Building")
                .snippet("(CSG, CS1, CS2, CS3)"));

        LatLng libraryLatLng = new LatLng(52.673240, -8.573429);
        mMap.addMarker(new MarkerOptions()
                .position(libraryLatLng)
                .title("Glucksman Library and\nInformation Services Building\n")
                .snippet("(GLG, GL0, GL1, GL2)"));

        LatLng schumannLatLng = new LatLng(52.673176, -8.577914);
        mMap.addMarker(new MarkerOptions()
                .position(schumannLatLng)
                .title("Schuman Building\n")
                .snippet("(SG, S1, S2)"));

        LatLng kbsLatLng = new LatLng(52.672666, -8.577201);
        mMap.addMarker(new MarkerOptions()
                .position(kbsLatLng)
                .title("Kemmy Business School")
                .snippet("(KBG, KB1, KB2, KB3)"));

        LatLng foundationLatLng = new LatLng(52.674473, -8.573752);
        mMap.addMarker(new MarkerOptions()
                .position(foundationLatLng)
                .title("Foundation Building")
                .snippet("(FB, FG, F1, F2) "));

        LatLng ERLatLng = new LatLng(52.674928, -8.572824);
        mMap.addMarker(new MarkerOptions()
                .position(ERLatLng)
                .title("Engineering Research Building ")
                .snippet("(ERB, ER0, ER1, ER2)"));

        LatLng langLatLng = new LatLng(52.675485, -8.573488);
        mMap.addMarker(new MarkerOptions()
                .position(langLatLng)
                .title("Languages Building")
                .snippet("(LCB, LC0, LC1, LC2) "));

        LatLng lonsLatLng = new LatLng(52.673813, -8.569165);
        mMap.addMarker(new MarkerOptions()
                .position(lonsLatLng)
                .title("Lonsdale Building ")
                .snippet("(LB, LG, L1,L2) "));

        LatLng schrLatLng = new LatLng(52.673697, -8.567370);
        mMap.addMarker(new MarkerOptions()
                .position(schrLatLng)
                .title("Schrödinger Building")
                .snippet("(SR1, SR2, SR3)"));

        LatLng pessLatLng = new LatLng(52.674678, -8.567541);
        mMap.addMarker(new MarkerOptions()
                .position(pessLatLng)
                .title("PESS Building")
                .snippet("(PG, PM, P1, P2) "));

        LatLng hsbLatLng = new LatLng(52.677751, -8.568892);
        mMap.addMarker(new MarkerOptions()
                .position(hsbLatLng)
                .title("Health Sciences Building")
                .snippet("(HSG,HS1,HS2,HS3)"));

        LatLng mainbLatLng = new LatLng(52.673984, -8.571949);
        mMap.addMarker(new MarkerOptions()
                .position(mainbLatLng)
                .title("Main Building")
                .snippet("Block A: A0,AM, A1, A2, A3\n" +
                        "Block B: B0, BM, B1, B2,B3\n" +
                        "Block C: CG, C0, CM, C1,C2\n" +
                        "Block D: DG, D0, DM, D1,D2\n" +
                        "Block E: EG, E0, EM, E1, E2"));

        LatLng analogLatLng = new LatLng(52.673196, -8.568757);
        mMap.addMarker(new MarkerOptions()
                .position(analogLatLng)
                .title("Analog Building")
                .snippet("(AD0, AD1, AD2, AD3)"));

        LatLng iwaLatLng = new LatLng(52.678188, -8.569428);
        mMap.addMarker(new MarkerOptions()
                .position(iwaLatLng)
                .title("Irish World Academy Building")
                .snippet("(IWG, IW1, IW2)"));

        LatLng medLatLng = new LatLng(52.678323, -8.568091);
        mMap.addMarker(new MarkerOptions()
                .position(medLatLng)
                .title("Medical School")
                .snippet("(GEMS0, GEMS1, GEMS2, GEMS3) "));
    }

    // Implement the getInfoWindow method to return null, so that getInfoContents is called instead.
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    // Implement the getInfoContents method to inflate the custom layout and set its contents.
    @Override
    public View getInfoContents(Marker marker) {
        // Inflate the layout for the custom InfoWindow
        View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);

        // Find the TextViews in the custom layout and set their text
        TextView titleTextView = infoWindow.findViewById(R.id.title_text_view);
        TextView snippetTextView = infoWindow.findViewById(R.id.snippet_text_view);
        titleTextView.setText(marker.getTitle());
        snippetTextView.setText(marker.getSnippet());

        return infoWindow;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    // Requests location permissions from user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            }
        }
    }
}

