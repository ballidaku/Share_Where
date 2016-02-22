package car.sharewhere.gagan.Async_Thread;

import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 Created by Gagan on 8/18/15. */
public class WebServiceHandler
{

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams)
    {

        URL    url;
        String response = "";
        try
        {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //  conn.setReadTimeout(15000);
            //conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(1024);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null)
                {

                    response += line;

                }

            }
            else
            {
                response = "ERROR";

                // throw new Exception(responseCode + "");
            }
        }
      /*  catch (Exception e)
        {
            response = "ERROR";
            e.printStackTrace();
        }*/
        catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            return "SLOW";
        }
        catch (ConnectTimeoutException e)
        {
            e.printStackTrace();
            return "SLOW";
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return "ERROR";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "SLOW";
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean       first  = true;
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
            {
                first = false;

            }
            else
            {
                result.append("&");

            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String performGetCall(String url) throws Exception
    {
        URL               obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in       = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String         inputLine;
        StringBuffer   response = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        //print result

        Log.e("==getCall response====", response.toString());
        return response.toString(); //this is your response

    }

}