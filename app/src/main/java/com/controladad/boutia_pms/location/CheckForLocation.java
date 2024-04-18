package com.controladad.boutia_pms.location;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;

import com.controladad.boutia_pms.R;

import timber.log.Timber;

public class CheckForLocation {

    public static boolean checkIfGpsIsEnabled(Context context) {

        android.location.LocationManager lm = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Timber.e(ex);
            return false;
        }
    }

    public static AlertDialog gpsCheck(Context context) {

        if (!checkIfGpsIsEnabled(context)) {
            // Notify the user
            Timber.d("Dialog: gpsCheck(): Where are in the AlertDialogBuilder");
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), (paramDialogInterface, paramInt) -> {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(myIntent);
            });
            dialog.setCancelable(false);
            return dialog.create();
        } else {
            return null;
        }
    }
}
