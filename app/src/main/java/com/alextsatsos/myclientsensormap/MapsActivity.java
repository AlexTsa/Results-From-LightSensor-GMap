package com.alextsatsos.myclientsensormap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;  // ορισμός αντικειμένου της τάξης googleMap
    CollectionReference collectionReference; // oρισμός αντικειμένου CollectionReference
    public FirebaseFirestore db; //ορισμός αντικειμένου της τάξης FirebaseFirestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        db = FirebaseFirestore.getInstance(); //αρχικοποίηση της FirebaseFirestore και με την μέθοδο  getInstance κανούμε την σύνδεση με το project(My project ) και το collection Marker

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); //Δημιουργία του fragment
        mapFragment.getMapAsync(this);  //Καλείται η μέθοδος onMapReady
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        collectionReference = db.collection("Marker");  //Δημιουργία ενος αντικείμενου collectionReference έτσι ώστε να έχουμε πρόσβαση στην συλλογή Marker
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {  // Εαν πάρουμε σωστά τα δεδομένα ανάλογα με την ένδειξη του  Listener με την μέθοδο .get τα διαβάζουμε
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {  //Επιστροφεί των documents
                for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) { //Κανούμε μια for για κάθε ένα queryDocumentSnapshot
                    BitmapDescriptor bitmapMarker;  //ορισμός αντικειμένου της τάξης  BitmapDescriptor
                    MarkerClass markerClass = documentSnapshots.toObject(MarkerClass.class);  // με την toObject επιστρέφουμε στο MarkerClass όλες τις τιμές και τις τοποθετούμε στο αντικείμενο markerClass
                    String color = markerClass.getColor();  //παιρνούμε απο το αντικείμενο markerclass  το χρώμα και το βάζουμε στην μεταβλητή color
                    GeoPoint geoPoint = markerClass.getGeoPoint(); //παιρνούμε απο το αντικείμενο markerclass το Geopoint  και το βάζουμε στην μεταβλητή geoPoint
                    Double sensorValue = markerClass.getSensorValue(); //παιρνούμε απο το αντικείμενο markerclass την τιμη του αισθητήρα φωτός  και την βάζουμε στην μεταβλητή sensorValue
                    String snippet = markerClass.getSnippet(); //παιρνούμε απο το αντικείμενο markerclass το αποσπάσμα και την βάζουμε στην μεταβλητή snippet
                    String title = markerClass.getTitle(); //παιρνούμε απο το αντικείμενο markerclass τον τιτλο και την βάζουμε στην μεταβλητή title
                    switch (color) {  // κάνουμε μια switch ανάλογα με τη τιμή του χρώματος που έχει η μεταβλητη color
                        case "Blue":   // άμα είναι Blue το χρώμα
                            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);  //Δημιουγία ενός bitmap descriptor που αναφέρεται στο μπλε χρωμα του default marker
                            break;
                        case "Green":  //To ίδιο κάνουμε και τα υπολοιπο χρώματα ανάλογα με την switch
                            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                            break;
                        case "Rose":
                            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                            break;
                        case "Yellow":
                            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                            break;
                        case "Red":
                            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + color);
                    }

                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());// Δημιουργία αντικείμενο latLng της Τάξης  LatLng με τις τιμες του geoPoint
                    mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(snippet + ", η τίμη από το φώτομετρο είναι :" + sensorValue).icon(bitmapMarker)); //προσθέτουμε ενα marker με θεση το  αντικείμενο latLng , που έχει επιπλέον ενα title ,ενα snippet,
                    //την τιμη του αισθητήρα και με ένα χρώμα .
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {  // Κάνουμε set ενα custom window adapter για το google map
                        @Override
                        public View getInfoWindow(Marker marker) {  // η μέθοδος getInfoWindow παρέχει custom περιέχομενο του προεπιλεγμενού info window frame του marker
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) { // η μέθοδος getInfoContent μας παρέχει ενα custom info window για τον marker
                            View view = getLayoutInflater().inflate(R.layout.customwindow, null); //παίρνουμε το view απο το layout : customwindow
                            TextView titleText = view.findViewById(R.id.texttitle); //αντιστοιχουμε με το στοιχεία που είναι στο customwindow.xml*
                            titleText.setText(marker.getTitle());  // Κάνουμε setText στο titleText του μαρκερ τον τιτλο
                            TextView snippetText = view.findViewById(R.id.textsnippet);  //*
                            snippetText.setText(marker.getSnippet());  // Κάνουμε setText στο snippetText του μαρκερ το απόσπασμα
                            return view;  // Επιστρέφουμε το view που θα τοποθετηθεί μέσα στο info window frame
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {  // Εαν πάει κατι λάθος εμφάνισε ενα μήνυμα τύπου toast
                Toast.makeText(MapsActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
