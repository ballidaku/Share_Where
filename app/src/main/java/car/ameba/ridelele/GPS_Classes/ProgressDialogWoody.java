package car.ameba.ridelele.GPS_Classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by gagandeep on 22 Jan 2016.
 */

public class ProgressDialogWoody extends Dialog
{


    public ProgressDialogWoody(Context context)
    {

        super(context);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        ImageView iv = new ImageView(context);
//        iv.setImageResource(resourceIdOfImage);

        ProgressBar pb = new ProgressBar(context);

//        pb.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        pb.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        layout.addView(pb, params);
        addContentView(layout, params);
    }

    @Override
    public void show()
    {
        super.show();
//DEFINE ANIMATION WHICH YOU WANT
//        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
//                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF,
//                .5f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(700);
//        iv.setAnimation(anim);
//        iv.startAnimation(anim);
    }

    @Override
    public void hide() {
        super.hide();
    }
}
