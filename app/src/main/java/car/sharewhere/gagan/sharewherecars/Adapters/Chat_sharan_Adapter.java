package car.sharewhere.gagan.sharewherecars.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import car.sharewhere.gagan.utills.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.utills.Utills_G;

/**
 Created by sharan on 22/9/15. */
public class Chat_sharan_Adapter extends BaseAdapter
{

    ArrayList<HashMap<String, String>> list;
    private Context con;
    SharedPreferences preferences;

    String my_customerID = "", user_img = "", my_img = "";

    Utills_G util;

    public Chat_sharan_Adapter(Context con, ArrayList<HashMap<String, String>> list, String user_img)
    {
        this.list = list;

        this.con = con;
        this.user_img = user_img;

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        my_img = preferences.getString("photo_path", "");
        my_customerID = preferences.getString(GlobalConstants.KeyNames.CustomerId.toString(), null);

        util = new Utills_G();
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list;
    }

    @Override
    public long getItemId(int position)
    {
        return list.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent)
    {

        String       chat_id     = list.get(position).get("chat_id");
        String       message_id  = list.get(position).get("message_id");
        final String sender_id   = list.get(position).get("sender_id");
        final String reciever_id = list.get(position).get("reciever_id");
        final String message     = list.get(position).get("message");
        final String time        = list.get(position).get("time");

        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!sender_id.equalsIgnoreCase(my_customerID))  // if my message
        {
            row = inflater.inflate(R.layout.custom_other_chat, parent, false);
            TextView tvMSG = (TextView) row.findViewById(R.id.txtV_MyChatmsg);

            ImageView DP = (ImageView) row.findViewById(R.id.fb_MyChat);
            LinearLayout FramemsgLayout = (LinearLayout) row.findViewById(R.id.FrameLayoutMyChat);

            TextView tvTime = (TextView) row.findViewById(R.id.txtV_MyChatDate);

//            FrameLayout imageContainer = (FrameLayout) row.findViewById(R.id.FrameLayoutIMAGE);

            util.setRoundImage(con, DP, user_img);

            FramemsgLayout.setVisibility(View.VISIBLE);
//            imageContainer.setVisibility(View.GONE);
            // youVideo.setVisibility(View.GONE);

            tvMSG.setText(message);

            tvTime.setText(time);

        }

        else  // if other message
        {

            row = inflater.inflate(R.layout.custom_my_chat, parent, false);

            TextView tvMSG = (TextView) row.findViewById(R.id.txtV_otherChatmsg);

            ImageView DP = (ImageView) row.findViewById(R.id.fb_otherChat);
            LinearLayout FramemsgLayout = (LinearLayout) row.findViewById(R.id.FrameLayoutotherChat);

            TextView tvTime = (TextView) row.findViewById(R.id.txtV_otherChatDate);

//            FrameLayout imageContainer = (FrameLayout) row.findViewById(R.id.FrameLayoutIMAGE_Other);

            util.setRoundImage(con, DP, my_img);

            FramemsgLayout.setVisibility(View.VISIBLE);
//            imageContainer.setVisibility(View.GONE);

            System.out.println("Chat Data---->" + message);
            tvMSG.setText(message);

            tvTime.setText(time);

           /* row.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    View.OnClickListener delete = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Util_Class.super_dialog.dismiss();

                            new Delete_Message_ProgressTask(con, message_id, position).execute();

                        }
                    };
                    Util_Class.show_super_dialog(con, delete, "");

                    return false;
                }
            });*/

        }

        return row;

    }

    public void add_data(ArrayList<HashMap<String, String>> list)
    {

        this.list = list;
    }

    public void add_dataAll(ArrayList<HashMap<String, String>> list)
    {
        this.list.addAll(list);
    }

}
