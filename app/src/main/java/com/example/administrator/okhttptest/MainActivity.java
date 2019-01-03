package com.example.administrator.okhttptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_OK_HTTP_ACTIVITY = "MAIN_ACTIVITY";

    private EditText pageUrlEditor = null;

    private Button syncGetButton = null;

    private Button asyncGetButton = null;

    private Button syncPostButton = null;

    private Button asyncPostButton = null;

    // Display server response text.
    private TextView respTextView = null;

    private OkHttpClient okHttpClient = null;

    // Process child thread sent command to show server response text in activity main thread.
    private Handler displayRespTextHandler = null;

    private static final int COMMAND_DISPLAY_SERVER_RESPONSE = 1;

    private static final String KEY_SERVER_RESPONSE_OBJECT = "KEY_SERVER_RESPONSE_OBJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("dev2qa.com - Android OkHttpClient Example");

        // Init okhttp3 example controls.
        initControls();

        // Click this button to get url response synchronous.
        syncGetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String url = pageUrlEditor.getText().toString();
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    Toast.makeText(getApplicationContext(), "Please input a url start with http or https.", Toast.LENGTH_LONG).show();
                }else {

                    // For synchronous actions, must start a child thread to process.
                    // Otherwise, activity main thread will throw exception.
                    Thread okHttpExecuteThread = new Thread() {
                        @Override
                        public void run() {

                            String url = pageUrlEditor.getText().toString();

                            try {

                                // Create okhttp3.Call object with get http request method.
                                Call call = createHttpGetMethodCall(url);

                                // Execute the request and get the response synchronously.
                                Response response = call.execute();

                                // If request process success.
                                boolean respSuccess = response.isSuccessful();
                                if (respSuccess) {

                                    // Parse and get server response text data.
                                    String respData = parseResponseText(response);

                                    // Notify activity main thread to update UI display text with Handler.
                                    sendChildThreadMessageToMainThread(respData);
                                } else {
                                    sendChildThreadMessageToMainThread("Ok http get request failed.");
                                }
                            } catch (Exception ex) {
                                Log.e(TAG_OK_HTTP_ACTIVITY, ex.getMessage(), ex);
                                sendChildThreadMessageToMainThread(ex.getMessage());
                            }
                        }
                    };

                    // Start the child thread.
                    okHttpExecuteThread.start();
                }
            }
        });

        // Click this button to get url response asynchronous.
        asyncGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = pageUrlEditor.getText().toString();
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    Toast.makeText(getApplicationContext(), "Please input a url start with http or https.", Toast.LENGTH_LONG).show();
                }else {

                    try {
                        // Create okhttp3.Call object with get http request method.
                        Call call = createHttpGetMethodCall(url);

                        // Execute the request and get the response asynchronously.
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                sendChildThreadMessageToMainThread("Asynchronous http get request failed.");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    // Parse and get server response text data.
                                    String respData = parseResponseText(response);

                                    // Notify activity main thread to update UI display text with Handler.
                                    sendChildThreadMessageToMainThread(respData);
                                }
                            }
                        });
                    }catch(Exception ex)
                    {
                        Log.e(TAG_OK_HTTP_ACTIVITY, ex.getMessage(), ex);
                        sendChildThreadMessageToMainThread(ex.getMessage());
                    }
                }
            }
        });

        // Click this button to post data to url synchronously.
        syncPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = pageUrlEditor.getText().toString();
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    Toast.makeText(getApplicationContext(), "Please input a url start with http or https.", Toast.LENGTH_LONG).show();
                }else {

                    // For synchronous actions, must start a child thread to process.
                    // Otherwise, activity main thread will throw exception.
                    Thread okHttpExecuteThread = new Thread() {
                        @Override
                        public void run() {

                            String url = pageUrlEditor.getText().toString();

                            try {

                                // Create okhttp3.Call object with post http request method.
                                Call call = createHttpPostMethodCall(url);

                                // Execute the request and get the response synchronously.
                                Response response = call.execute();

                                // If request process success.
                                boolean respSuccess = response.isSuccessful();
                                if (respSuccess) {

                                    // Parse and get server response text data.
                                    String respData = parseResponseText(response);

                                    // Notify activity main thread to update UI display text with Handler.
                                    sendChildThreadMessageToMainThread(respData);
                                } else {
                                    sendChildThreadMessageToMainThread("Ok http post request failed.");
                                }
                            } catch(Exception ex)
                            {
                                Log.e(TAG_OK_HTTP_ACTIVITY, ex.getMessage(), ex);
                                sendChildThreadMessageToMainThread(ex.getMessage());
                            }
                        }
                    };

                    // Start the child thread.
                    okHttpExecuteThread.start();
                }
            }
        });

        // Click this button to send post data to url asynchronously.
        asyncPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = pageUrlEditor.getText().toString();
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    Toast.makeText(getApplicationContext(), "Please input a url start with http or https.", Toast.LENGTH_LONG).show();
                }else {
                    try
                    {
                        // Create okhttp3.Call object with post http request method.
                        Call call = createHttpPostMethodCall(url);

                        // Execute the request and get the response asynchronously.
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                sendChildThreadMessageToMainThread("Asynchronous http post request failed.");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful())
                                {
                                    // Parse and get server response text data.
                                    String respData = parseResponseText(response);

                                    // Notify activity main thread to update UI display text with Handler.
                                    sendChildThreadMessageToMainThread(respData);
                                }
                            }
                        });
                    }catch(Exception ex)
                    {
                        Log.e(TAG_OK_HTTP_ACTIVITY, ex.getMessage(), ex);
                        sendChildThreadMessageToMainThread(ex.getMessage());
                    }
                }
            }
        });
    }

    /* Initialize okhttp3 example controls */
    private void initControls()
    {
        if(pageUrlEditor == null)
        {
            pageUrlEditor = (EditText)findViewById(R.id.ok_http_url_editor);
        }

        if(syncGetButton == null)
        {
            syncGetButton = (Button)findViewById(R.id.ok_http_client_synchronous_get);
        }

        if(asyncGetButton == null)
        {
            asyncGetButton = (Button)findViewById(R.id.ok_http_client_asynchronous_get);
        }

        if(syncPostButton == null)
        {
            syncPostButton = (Button)findViewById(R.id.ok_http_client_synchronous_post);
        }

        if(asyncPostButton == null)
        {
            asyncPostButton = (Button)findViewById(R.id.ok_http_client_asynchronous_post);
        }

        if(respTextView == null)
        {
            respTextView = (TextView)findViewById(R.id.ok_http_client_resp_text_view);
        }

        if(displayRespTextHandler == null)
        {
            displayRespTextHandler = new Handler()
            {
                // When this handler receive message from child thread.
                @Override
                public void handleMessage(Message msg) {

                    // Check what this message want to do.
                    if(msg.what == COMMAND_DISPLAY_SERVER_RESPONSE)
                    {
                        // Get server response text.
                        Bundle bundle = msg.getData();
                        String respText = bundle.getString(KEY_SERVER_RESPONSE_OBJECT);

                        // Display server response text in text view.
                        respTextView.setText("");
                        respTextView.scrollTo(0,0);
                        respTextView.setText(respText);
                    }
                }
            };
        }

        if(okHttpClient == null)
        {
            okHttpClient = new OkHttpClient();
        }
    }

    /* Create OkHttp3 Call object use get method with url. */
    private Call createHttpGetMethodCall(String url)
    {
        // Create okhttp3 request builder.
        Request.Builder builder = new Request.Builder();

        // Set url.
        builder = builder.url(url);

        // Create request object.
        Request request = builder.build();

        // Create a new Call object.
        Call call = okHttpClient.newCall(request);

        return call;
    }

    /* Create OkHttp3 Call object use post method with url. */
    private Call createHttpPostMethodCall(String url)
    {
        // Create okhttp3 form body builder.
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        // Add form parameter
        formBodyBuilder.add("q", "trump");
        formBodyBuilder.add("first", "2");

        // Build form body.
        FormBody formBody = formBodyBuilder.build();

        // Create a http request object.
        Request.Builder builder = new Request.Builder();
        builder = builder.url(url);
        builder = builder.post(formBody);
        Request request = builder.build();

        // Create a new Call object with post method.
        Call call = okHttpClient.newCall(request);

        return call;
    }

    /* Parse response code, message, headers and body string from server response object. */
    private String parseResponseText(Response response)
    {
        // Get response code.
        int respCode = response.code();

        // Get message
        String respMsg = response.message();

        // Get headers.
        List<String> headerStringList = new ArrayList<String>();

        Headers headers = response.headers();
        Map<String, List<String>> headerMap = headers.toMultimap();
        Set<String> keySet = headerMap.keySet();
        Iterator<String> it = keySet.iterator();
        while(it.hasNext())
        {
            String headerKey = it.next();
            List<String> headerValueList = headerMap.get(headerKey);

            StringBuffer headerBuf = new StringBuffer();
            headerBuf.append(headerKey);
            headerBuf.append(" = ");

            for(String headerValue : headerValueList)
            {
                headerBuf.append(headerValue);
                headerBuf.append(" , ");
            }

            headerStringList.add(headerBuf.toString());
        }


        // Get body text.
        String respBody = "";
        try {
            respBody = response.body().string();
        }catch(IOException ex)
        {
            Log.e(TAG_OK_HTTP_ACTIVITY, ex.getMessage(), ex);
        }

//         Create a server response dto.
//        ServerResponseDTO respDto = new ServerResponseDTO();
//        respDto.setRespCode(respCode);
//        respDto.setRespMessage(respMsg);
//        respDto.setHeaderStringList(headerStringList);
//        respDto.setRespBody(respBody);
//
//        return respDto.toString();

        return respBody;
    }

    // Send message from child thread to activity main thread.
    // Because can not modify UI controls in child thread directly.
    private void sendChildThreadMessageToMainThread(String respData)
    {
        // Create a Message object.
        Message message = new Message();

        // Set message type.
        message.what = COMMAND_DISPLAY_SERVER_RESPONSE;

        // Set server response text data.
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SERVER_RESPONSE_OBJECT, respData);
        message.setData(bundle);

        // Send message to activity Handler.
        displayRespTextHandler.sendMessage(message);
    }
}
