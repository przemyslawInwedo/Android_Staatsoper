package app.nunc.com.staatsoperlivestreaming;

import app.nunc.com.staatsoperlivestreaming.Model.Stream;

public interface StreamsView extends NetworkView {

    void setStream(Stream stream);

    void showProgress();

    void hideProgress();

}
