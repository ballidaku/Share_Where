package car.ameba.ridelele.utills;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import car.ameba.gagan.sharewherecars.R;

/**
 Created by sharan on 22/2/16. */
public class Dialogs
{
    public Dialog dialog;

//    public RadioGroup radioGroup;

    public RadioGroup send_message(Context con, View.OnClickListener oc_send)
    {
        dialog = new Dialog(con);
        dialog.setTitle("Message");
        dialog.setContentView(R.layout.dialog_send_message);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
//        final EditText ed_        = (EditText) dialog.findViewById(R.id.ed_);
        Button         btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button         btn_send   = (Button) dialog.findViewById(R.id.btn_send);

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

      /*  ed_.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                radioGroup.clearCheck();
            }
        });*/

        btn_send.setOnClickListener(oc_send);

     /*   radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i)
            {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                if (null != rb && i > -1)
                {
                    ed_.setText("");
                }
            }

        });*/

        dialog.show();

        return radioGroup;

    }



    public void turnGPSOn(final Context con)
    {

        final Dialog dialog = new Dialog(con);

        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mobile_custom_verify);
        dialog.setCancelable(false);

        TextView text       = (TextView) dialog.findViewById(R.id.text);
        Button         ok         = (Button) dialog.findViewById(R.id.ok);
        Button         cancel     = (Button) dialog.findViewById(R.id.cancel);
        final EditText edt_mobile = (EditText) dialog.findViewById(R.id.text_mobile);
        edt_mobile.setVisibility(View.GONE);

        text.setText(("Enable GPS to get your location"));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                con.startActivity(intent);

                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                dialog.dismiss();
                ((Activity)con).finish();
            }
        });

        dialog.show();
    }
}
