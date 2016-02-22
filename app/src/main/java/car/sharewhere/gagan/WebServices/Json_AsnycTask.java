package car.sharewhere.gagan.WebServices;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;


public class Json_AsnycTask extends AsyncTask<Void, Void, String> {
    Asnychronus_notifier    listener;
    Context                 con;
    String                  URL;
    Fragment frg;
    ProgressDialog          pd;
    HashMap<String, String> data;
    String method;


    public void setOnResultsListener(Asnychronus_notifier listener) {
        this.listener = listener;
    }

    public Json_AsnycTask(Context con, String URL, String method, HashMap<String, String> data) {
        this.con = con;
        this.URL = URL;
        this.data = data;
        this.method = method;
    }

    public Json_AsnycTask(Context con, Fragment frg, String URL, String method, HashMap<String, String> data) {
        this.method = method;
        this.con = con;
        this.URL = URL;
        this.frg = frg;
        this.data = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


//        new MainActivity().setRefreshActionButtonState(true);



//        pd = ProgressDialog.show(con, "", "");
//        pd.setContentView(R.layout.progress_dialog);
////        pd.setCancelable(true);
//        pd.show();

    }

    @Override
    protected synchronized String doInBackground(Void... params) {
       /* JSONObject jobject = new JSONObject();
//        String url = params[0];
        jobject = executeHttpPost_server(url, method, parameter, parameterValue);
        return jobject;*/





        try {
            WebServiceHelper webHelper = new WebServiceHelper();
            Log.e("Data Send", "data......    " + data);

            if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD1) || method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD2)) {
                return webHelper.performGetCall(URL);
            }
            else
            {
                return webHelper.performPostCall(URL, data);
            }
        } catch (Exception abc) {
            Log.e("exception",String.valueOf(abc));
            return "";
        }



    }


    @Override
    protected void onPostExecute(String response) {
        // TODO Auto-generated method stub
        super.onPostExecute(response);
//        ((MainActivity)con).setRefreshActionButtonState(false);
//        new MainActivity().setRefreshActionButtonState(false);

//                pd.dismiss();
        if(response.equals("No Internet"))
        {
//            Util.show_Toast("Problem in internet connection.",con);
//            response = "{"+GlobalConstants.STATUS+":\"NoInternet\"}";
            response = "{"+GlobalConstants.MESSAGE+":\"Problem in internet connection.\"}";
        }
        if(response.equals("Server Error"))
        {
//            Util.show_Toast("Problem in internet connection.",con);
            response = "{"+GlobalConstants.MESSAGE+":\"Internal Server Error.\"}";
        }
        else if(response.equals("")){
            response = "{"+GlobalConstants.MESSAGE+":\"Connection Timeout.\"}";
        }


            try {

                JSONObject result = new JSONObject(response);

                if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD1)) {
                    listener.onResultsSucceeded_Get_Method1(result);
                }
                else if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD2)) {
                    listener.onResultsSucceeded_Get_Method2(result);
                }
                else if (method.equalsIgnoreCase(GlobalConstants.POST_SERVICE_METHOD1)) {
                    listener.onResultsSucceeded_Post_Method1(result);
                }
                else if (method.equalsIgnoreCase(GlobalConstants.POST_SERVICE_METHOD2)) {
                    listener.onResultsSucceeded_Post_Method2(result);
                }
                else {
                    listener.onResultsSucceeded_Post_Method3(result);
                }
            }
            catch (Exception e) {

            }

    }

/*
    @SuppressLint("NewApi")
    public JSONObject executeHttpPost_server(String url, String method, ArrayList<String> param, ArrayList<String> value) {
        JSONObject jArray = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            HttpClient httpClient = new DefaultHttpClient();

            if (method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD1) || method.equalsIgnoreCase(GlobalConstants.GET_SERVICE_METHOD2)) {
                httpget = new HttpGet(url);
                httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httpget.setHeader("Accept", "application/json");
                httpResponse = httpClient.execute(httpget);
                jArray = getFunction();
            } else {
                httppost = new HttpPost(url);

                List<NameValuePair> paramsL = new LinkedList<>();
                for (int i = 0; i < param.size(); i++) {
                    paramsL.add(new BasicNameValuePair(param.get(i).toString(), value.get(i).toString()));

                }

                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httppost.setHeader("Accept", "application/json");
                HttpEntity entity = new UrlEncodedFormEntity(paramsL, "UTF-8");
                httppost.setEntity(entity);

                httpResponse = httpClient.execute(httppost);

                resul = EntityUtils.toString(httpResponse.getEntity());


                JSONObject object = new JSONObject(resul);

                jArray = object;


            }


        } catch (Exception e) {
            Log.e("exception", "exception" + e);
        }
        return jArray;
    }


    private JSONObject getFunction() {
        InputStream isb = null;
        String result = "";
        JSONObject jArray = null;

        HttpEntity resultentity = httpResponse.getEntity();
        try {
            isb = resultentity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(isb, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isb.close();
            result = sb.toString();

            jArray = new JSONObject(result);
        } catch (Exception e) {
        }
        return jArray;
    }

    private JSONObject postFunction() {
        JSONObject result = null;
        try {
            result = new JSONObject(resul);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
*/


}

