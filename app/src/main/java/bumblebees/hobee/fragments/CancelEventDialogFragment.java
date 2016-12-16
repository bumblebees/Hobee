package bumblebees.hobee.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import bumblebees.hobee.EventViewActivity;
import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.MQTTService;
import bumblebees.hobee.utilities.Profile;

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
                        cancelEvent(event, dialogCancelReason.getText().toString());
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


    /**
     * Cancels the current event.
     * @param event - current event
     */
    private void cancelEvent(final Event event, final String reason){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String serverIP = getResources().getString(R.string.hobee_main_server);
                int port = 3002;
                try {
                    InetAddress serverAdress = InetAddress.getByName(serverIP);
                    Socket socket = new Socket(serverAdress, port);
                    JSONObject cancelJson = new JSONObject();
                    cancelJson.put("topic", event.getTopic());
                    cancelJson.put("reason", reason);

                    String message = cancelJson.toString();
                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    if(!printWriter.checkError()){
                        printWriter.println(message);
                        printWriter.flush();
                    }
                    socket.close();
                 } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();



    }
}
