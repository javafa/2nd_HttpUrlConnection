package com.veryworks.android.httpurlconnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnGet;
    EditText editUrl;
    TextView txtResult,txtTitle;
    RelativeLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = (Button) findViewById(R.id.btnGet);
        editUrl = (EditText) findViewById(R.id.editUrl);
        txtResult = (TextView) findViewById(R.id.textResult);
        txtTitle = (TextView) findViewById(R.id.textTitle);

        progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = editUrl.getText().toString();
                getUrl(urlString);
            }
        });
    }

    public void getUrl(String urlString){

        if(!urlString.startsWith("http")){
            urlString = "http://"+urlString;
        }

        new AsyncTask<String, Void, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];
                try {
                    // 1. String 을 url 객체로 변환
                    URL url = new URL(urlString);
                    // 2. url 로 네트워크 연결시작
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 3. url 연결에 대한 옵션 설정
                    // 가.GET  : 데이터 요청시 사용하는 방식
                    // 나.POST : 데이터 입력시
                    // 다.PUT  : 데이터 수정시
                    // 라.DELETE : 데이터 삭제시
                    connection.setRequestMethod("GET");
                    // 4. 서버로부터 응답코드 회신
                    // 응답코드의 종류는 HttpURLConnection에 상수로 정의되어 있음
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 4.1 서버연결로 부터 스트림을 얻고, 버퍼래퍼로 감싼다
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        // 4.2 반복문을 돌면서 버퍼의 데이터를 읽어온다
                        StringBuilder result = new StringBuilder();
                        String lineOfData = "";
                        while ((lineOfData = br.readLine()) != null) {
                            result.append(lineOfData);
                        }

                        return result.toString();

                    } else {
                        Log.e("HTTPConnection", "Error Code=" + responseCode);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // title 태그값 추출하기
                String title = result.substring(result.indexOf("<title>")+7,result.indexOf("</title>"));
                txtTitle.setText(title);

                // 결과값 메인 UI 에 세팅
                txtResult.setText(result);
                progressLayout.setVisibility(View.GONE);
            }

        }.execute(urlString);


    }
}
