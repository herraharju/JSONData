package domain.com.jsondata;

/**
 * Created by MrKohvi on 10.11.2017.
 */

public class Answer
{
    public String _id;
    public String _q_id;
    public String _answer;

    public Answer(String id, String qid, String answer){
        this._id = id;
        this._q_id = qid;
        this._answer = answer;
    }
}
