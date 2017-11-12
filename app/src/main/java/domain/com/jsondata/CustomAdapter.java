package domain.com.jsondata;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MrKohvi on 10.11.2017.
 */

public class CustomAdapter extends BaseAdapter
{
    private Context _context;
    private ArrayList<Question> _questions;
    private LayoutInflater _inflater;

    public CustomAdapter(Context context, ArrayList<Question> questions){
        this._context = context;
        this._questions = questions;
        this._inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return _questions.size();
    }

    @Override
    public Object getItem(int i)
    {
        return _questions.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        view = _inflater.inflate(R.layout.custom_list_item, null);

        TextView tv1 = (TextView) view.findViewById(R.id.tv_question);
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner);

        tv1.setText(_questions.get(i)._question);
        final ArrayList<Answer> answersIds = _questions.get(i).answers;
        final ArrayList<String> answers = new ArrayList<>();

        for (Answer ans:answersIds)
        {
            answers.add(ans._answer);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1,answers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long l)
            {
                //handles answer selection in spinner
                int selectedValue = adapterView.getSelectedItemPosition();
                Question q = _questions.get(i);

                q.selectedAnswer = selectedValue;
                _questions.set(i,q);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        spinner.setAdapter(adapter);

        return view;
    }
}
