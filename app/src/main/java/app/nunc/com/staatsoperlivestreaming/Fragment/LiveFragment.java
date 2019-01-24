package app.nunc.com.staatsoperlivestreaming.Fragment;

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
import app.nunc.com.staatsoperlivestreaming.Model.Events;
import app.nunc.com.staatsoperlivestreaming.Model.Results;
import app.nunc.com.staatsoperlivestreaming.Presenter.AvailableStreamsPresenter;
import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.View.EventsView;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class LiveFragment extends Fragment implements EventsView {

    public static List<Events> events = new ArrayList<>();
    private AvailableStreamsPresenter availableStreamsPresenter;
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
        availableStreamsPresenter = new AvailableStreamsPresenter(this);
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        listView = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        root = view.findViewById(R.id.root);
        availableStreamsPresenter.getAvailableStreams();
        listAdapter = new ListAdapter(getActivity(), resultsList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", i);
                SingleEventFragment singleEventFragment = new SingleEventFragment();
                singleEventFragment.setArguments(bundle);
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
