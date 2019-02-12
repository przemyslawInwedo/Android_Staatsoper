package app.nunc.com.staatsoperlivestreaming.View;

import app.nunc.com.staatsoperlivestreaming.Model.Events;

public interface VideothequeView extends NetworkView {

    void setVideotheque(Events events);

    void showProgress();

    void hideProgress();

}
