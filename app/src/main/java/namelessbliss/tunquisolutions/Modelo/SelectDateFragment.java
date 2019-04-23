package namelessbliss.tunquisolutions.Modelo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import namelessbliss.tunquisolutions.Fragments.ReporteFinanciero;
import namelessbliss.tunquisolutions.Fragments.ReportePesos;
import namelessbliss.tunquisolutions.R;

public class SelectDateFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    EditText etFecha;

    public SelectDateFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        populateSetDate(year, month + 1, day);
    }

    public void populateSetDate(int year, int month, int day) {
        etFecha.setText(day + "-" + month + "-" + year);
    }

    public void setEtFecha(EditText etFecha) {
        this.etFecha = etFecha;
    }
}

