package domain.com.jsondata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrKohvi on 10.11.2017.
 */

public class Question
{
    public String _question;
    public String _id;
    public ArrayList<Answer> answers;
    public int selectedAnswer;

    public Question(String question, String id){
        this._question = question;
        this._id = id;
        answers = new ArrayList<>();
        this.selectedAnswer = 0;
    }
}
