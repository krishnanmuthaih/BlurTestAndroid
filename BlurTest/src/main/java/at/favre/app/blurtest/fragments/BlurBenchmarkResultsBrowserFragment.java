package at.favre.app.blurtest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.inqbarna.tablefixheaders.TableFixHeaders;

import at.favre.app.blurtest.R;
import at.favre.app.blurtest.activities.MainActivity;
import at.favre.app.blurtest.adapter.ResultTableAdapter;
import at.favre.app.blurtest.models.BenchmarkResultDatabase;
import at.favre.app.blurtest.models.ResultTableModel;
import at.favre.app.blurtest.util.JsonUtil;
import at.favre.app.blurtest.util.TranslucentLayoutUtil;

/**
 * Created by PatrickF on 16.04.2014.
 */
public class BlurBenchmarkResultsBrowserFragment extends Fragment {
	private static final String TAG = BlurBenchmarkResultsBrowserFragment.class.getSimpleName();

	private TableFixHeaders table;
    private ResultTableModel.DataType dataType = ResultTableModel.DataType.AVG;
	private BenchmarkResultDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.results_menu, menu);
        Spinner spinner = (Spinner) menu.findItem(R.id.action_select_datatype).getActionView();
        ArrayAdapter<ResultTableModel.DataType> adapter = new ArrayAdapter<ResultTableModel.DataType>(getActivity(),android.R.layout.simple_spinner_dropdown_item,ResultTableModel.DataType.values());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setNewDataType((ResultTableModel.DataType) adapterView.getAdapter().getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setNewDataType(ResultTableModel.DataType type) {
        dataType = type;
        table.setAdapter(new ResultTableAdapter(getActivity(),loadResultsDB(), dataType));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteData() {
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        settings.edit().putString(MainActivity.PREF_RESULTS,JsonUtil.toJsonString(new BenchmarkResultDatabase())).commit();
        table.setAdapter(new ResultTableAdapter(getActivity(),loadResultsDB(), dataType));
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_resultbrowser,container,false);
		table = (TableFixHeaders) v.findViewById(R.id.table);
		table.setAdapter(new ResultTableAdapter(getActivity(), loadResultsDB(), dataType));
		TranslucentLayoutUtil.setTranslucentThemeInsets(getActivity(), v.findViewById(R.id.tableWrapper));
        return v;
	}

	private BenchmarkResultDatabase loadResultsDB() {
		if (db == null) {
			Log.d(TAG, "start load db");
			SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
			String resultsString = settings.getString(MainActivity.PREF_RESULTS, null);
			if (resultsString != null) {
				db = JsonUtil.fromJsonString(resultsString, BenchmarkResultDatabase.class);
				Log.d(TAG, "done load db");
			} else {
				Log.d(TAG, "done load db");
			}
		}
		return db;
	}
}
