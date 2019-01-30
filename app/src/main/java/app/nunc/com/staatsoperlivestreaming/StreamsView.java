package app.nunc.com.staatsoperlivestreaming;

import app.nunc.com.staatsoperlivestreaming.Model.Events;

public interface EventsView extends NetworkView {

    void setEvents(Events events);

    void showProgress();

    void hideProgress();

}
