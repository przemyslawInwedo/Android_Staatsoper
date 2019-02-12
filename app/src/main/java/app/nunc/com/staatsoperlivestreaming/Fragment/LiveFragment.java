package app.nunc.com.staatsoperlivestreaming.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.nunc.com.staatsoperlivestreaming.Adapter.ListAdapter;
import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.View.EventsView;
import app.nunc.com.staatsoperlivestreaming.Model.Events;
import app.nunc.com.staatsoperlivestreaming.Model.Results;
import app.nunc.com.staatsoperlivestreaming.Presenter.LivePerformancePresenter;
import app.nunc.com.staatsoperlivestreaming.R;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class LiveFragment extends Fragment implements EventsView {

    public static List<Events> events = new ArrayList<>();
    private LivePerformancePresenter livePerformancePresenter;
    private ListView listView;
    private ListAdapter listAdapter;
    private FrameLayout root;
    private ArrayList<Results> resultsList = new ArrayList<>();
    private View progress;

    public LiveFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        livePerformancePresenter = new LivePerformancePresenter(this);
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        listView = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        root = view.findViewById(R.id.root);

        livePerformancePresenter.getAvailableStreams();
        listAdapter = new ListAdapter(getActivity(), resultsList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt(Keys.CLICKED_POSITION, i);
                editor.putString(Keys.STREAM_ID, LiveFragment.events.get(0).getResults().get(i).getId());
                editor.apply();
                SingleEventFragment singleEventFragment = new SingleEventFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(getId(), singleEventFragment, singleEventFragment.getTag())
                        .setTransitionStyle(TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }


    @Override
    public void setEvents(Events events) {
        resultsList.clear();
        LiveFragment.events.clear();
        LiveFragment.events.add(events);
        resultsList.addAll(events.getResults());
        listView.setAdapter(listAdapter);
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);

    }

    @Override
    public void onError(Throwable e) {

    }
}
