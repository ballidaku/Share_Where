package car.sharewhere.gagan.utills;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import car.sharewhere.gagan.sharewherecars.R;

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
}
