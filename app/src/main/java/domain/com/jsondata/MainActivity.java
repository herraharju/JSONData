package domain.com.jsondata;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    private ArrayList<Question> questions;
    private ArrayList<Answer> answers;
    private CustomAdapter adapter;
    private ListView listView;
    private Button btn_send_answers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

        //get questions
        volleyJSONArrayRequest(Constants.REQUEST_URL_QUES, true);

        //get answers
        volleyJSONArrayRequest(Constants.REQUEST_URL_ANS, false);
    }

    //initialize variables
    private void Init(){
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        btn_send_answers = (Button)findViewById(R.id.btn_send);
        btn_send_answers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SendQuestionsAndAnswers();
            }
        });

        listView = (ListView)findViewById(R.id.lv_questions);
        adapter = new CustomAdapter(this,questions);
        listView.setAdapter(adapter);

    }

    //sends request to server
    private void volleyJSONArrayRequest (String url, final boolean isQuestion)
    {
        //set progress dialog for request
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                //Log.d("Response value: ", response.toString());
                if(isQuestion){
                    ParseQuestionJSONArray(response);
                }
                else{
                    ParseAnswerJSONArray(response);
                }

                if(questions.size() > 0 && answers.size() > 0){
                    //merge questions and answers
                    for (Answer a:answers)
                    {
                        for (Question q:questions)
                        {
                            if(a._q_id.equals(q._id)){
                                q.answers.add(a);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                progressDialog.hide();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error value: ", error.getMessage());
                progressDialog.hide();
            }
        }
        );
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest, Constants.REQUEST_TAG);
    }

    //parses question type of JSON array to list
    private void ParseQuestionJSONArray(JSONArray jsonArray)
    {
        try
        {
            for(int i = 0; i<jsonArray.length();i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String question = jsonObject.getString("question");
                questions.add(new Question(question,id));

            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //parses answer type of JSON array to list
    private void ParseAnswerJSONArray(JSONArray jsonArray)
    {
        try
        {
            for(int i = 0; i<jsonArray.length();i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String questionid = jsonObject.getString("q_id");
                String answer = jsonObject.getString("txt");
                answers.add(new Answer(id,questionid,answer));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //builds url from given questions and answers
    private void SendQuestionsAndAnswers(){
        String urlParameters = "questions="+questions.size()+"&";

        for(int i = 0; i<questions.size();i++){
            Question question = questions.get(i);


            String handledQuestion = question._question.replace(" ","%20");
            String handledAnswer = question.answers.get(question.selectedAnswer)._answer.replace(" ", "%20");

            urlParameters += "question_"+(i+1)+"="+handledQuestion + "&";
            urlParameters += "answer_count_"+(i+1)+"=1&";
            urlParameters += "answer_"+(i+1)+"_1="+handledAnswer;

            if(i < questions.size()-1){
                urlParameters += "&";
            }
        }
        volleyStringRequest(Constants.URL_BODY_SEND + urlParameters);
    }
    //sends parameters on server and receives info about succession
    private void volleyStringRequest (String url)
    {
        //set progress dialog for request
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                //Log.d("Response value: ", response.toString());
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error value: ", error.getMessage());
                progressDialog.hide();
            }
        }
        );
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, Constants.REQUEST_TAG);
    }
}
