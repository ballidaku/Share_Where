package car.ameba.ridelele.WebServices;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import car.ameba.ridelele.utills.GlobalConstants;

public class Json_AsnycTask extends AsyncTask<Void, Void, String>
{
    Asnychronus_notifier    listener;
    Context                 con;
    String                  URL;
    Fragment                frg;
    ProgressDialog          pd;
    HashMap<String, String> data;
    String                  method;

    public void setOnResultsListener(Asnychronus_notifier listener)
    {
        this.listener = listener;
    }

    public Json_AsnycTask(Context con, String URL, String method, HashMap<String, String> data)
    {
        this.con = con;
        this.URL = URL;
        this.data = data;
        this.method = method;
    }

    public Json_AsnycTask(Context con, Fragment frg, String URL, String method, HashMap<String, String> data)
    {
        this.method = method;
        this.con = con;
        this.URL = URL;
        this.frg = frg;
        this.data = data;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        //        new MainActivity().setRefreshActionButtonState(true);

        //        pd = ProgressDialog.show(con, "", "");
        //        pd.setContentView(R.layout.progress_dialog);
        ////        pd.setCancelable(true);
        //        pd.show();

    }

    @Override
    protected synchronized String doInBackground(Void... params)
    {
       /* JSONObject jobject = new JSONObject();
//        String url = params[0];
        jobject = executeHttpPost_server(url, method, parameter, parameterValue);
        return jobject;*/

        try
        {
            WebServiceHelper webHelper = new WebServiceHelper();
            Log.e("Data Send", "data......    " + data);

            if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD1) || method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD2))
            {
                return webHelper.performGetCall(URL);
            }
            else
            {
                return webHelper.performPostCall(URL, data);
            }
        }
        catch (Exception abc)
        {
            Log.e("exception", String.valueOf(abc));
            return "";
        }

    }

    @Override
    protected void onPostExecute(String response)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(response);
        //        ((MainActivity)con).setRefreshActionButtonState(false);
        //        new MainActivity().setRefreshActionButtonState(false);

        //                pd.dismiss();
        if (response.equals("No Internet"))
        {
            //            Util.show_Toast("Problem in internet connection.",con);
            //            response = "{"+GlobalConstants.STATUS+":\"NoInternet\"}";
            response = "{" + GlobalConstants.MESSAGE + ":\"Problem in internet connection.\"}";
        }
        if (response.equals("Server Error"))
        {
            //            Util.show_Toast("Problem in internet connection.",con);
            response = "{" + GlobalConstants.MESSAGE + ":\"Internal Server Error.\"}";
        }
        else if (response.isEmpty())
        {
            response = "{" + GlobalConstants.MESSAGE + ":\"Empty Response.\"}";
        }

        try
        {

            JSONObject result = new JSONObject(response);

            if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD1))
            {
                listener.onResultsSucceeded_Get_Method1(result);
            }
            else if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD2))
            {
                listener.onResultsSucceeded_Get_Method2(result);
            }
            else if (method.equalsIgnoreCase(GlobalConstants.POST_SERVICE_METHOD1))
            {
                listener.onResultsSucceeded_Post_Method1(result);
            }
            else if (method.equalsIgnoreCase(GlobalConstants.POST_SERVICE_METHOD2))
            {
                listener.onResultsSucceeded_Post_Method2(result);
            }
            else if (method.equalsIgnoreCase(GlobalConstants.POST_SERVICE_METHOD3))
            {
                listener.onResultsSucceeded_Post_Method3(result);
            }
            else if (method.equalsIgnoreCase(GlobalConstants.POST_SERVICE_METHOD4))
            {
                listener.onResultsSucceeded_Post_Method4(result);
            }
            else
            {
                listener.onResultsSucceeded_Post_Method5(result);
            }
        }
        catch (Exception e)
        {

        }

    }

}

