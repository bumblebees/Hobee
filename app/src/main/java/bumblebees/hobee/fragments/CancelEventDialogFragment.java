package bumblebees.hobee.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.SocketIO;

public class CancelEventDialogFragment extends DialogFragment{



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Gson gson = new Gson();
        final Event event = gson.fromJson(getArguments().getString("event"), Event.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cancel_event, null);
        TextView dialogWarningText = (TextView) view.findViewById(R.id.dialogWarningText);
        final EditText dialogCancelReason = (EditText)view.findViewById(R.id.dialogCancelReason);
        String warningText = "You are about to cancel the event "+event.getEvent_details().getEvent_name()+". This action cannot be reversed.";
        if(event.getEvent_details().getUsers_accepted().size() > 1){
            warningText+= "\nWarning: Your rating will be decreased.";
        }
        dialogWarningText.setText(warningText);

        builder.setView(view)
                .setPositiveButton("Cancel event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SocketIO.getInstance().cancelEvent(event, dialogCancelReason.getText().toString());
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CancelEventDialogFragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Cancel event?");
        return builder.create();
    }
}
