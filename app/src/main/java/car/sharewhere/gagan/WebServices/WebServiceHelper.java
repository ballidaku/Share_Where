package car.sharewhere.gagan.WebServices;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by gagandeep on 22/9/15.
 */
public class WebServiceHelper
{
    public String performPostCall(String requestURL,HashMap<String,String> postDataParams) {

    URL    url;
    String response = "";
    int responseCode = 0;
    try {
        url = new URL(requestURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
       // conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);


        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
//            JSONObject obj = new JSONObject(postDataParams);
        writer.write(getPostDataString(postDataParams));
//            Log.e("eeeeeeeeeee",""+obj.toString());
//            writer.write(obj.toString());
        writer.flush();
        writer.close();
        os.close();
//            Log.e("#######","###########");
        responseCode=conn.getResponseCode();


        if (responseCode == HttpsURLConnection.HTTP_OK)
        {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line=br.readLine()) != null)
            {

                response+=line;

            }

            Log.e("response RRRRRRR =", response);
        }
        else
        {
            throw new Exception(responseCode+"");
        }

    } catch (Exception e) {
        if(Integer.valueOf(e.getMessage().trim()) == HttpsURLConnection.HTTP_CLIENT_TIMEOUT)

        {
            response = "No Internet";
        }
         if(Integer.valueOf(e.getMessage().trim()) == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
            response = "Server Error";
        }
        else {
            response = "No Internet";
        }
        e.printStackTrace();
    }
    finally {

        return response;

    }
}





    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;

            }
            else

                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }



    public String performGetCall(String url) throws Exception
    {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            // optional default is GET
            con.setRequestMethod("GET");

            //add request header

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result

            Log.e("==getCall response====", response.toString());
            return response.toString(); //this is your response
        }
        catch (Exception e)
        {

            e.printStackTrace();
            return "No Internet";
        }
    }


}
