package ru.smartro.inventory.base

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

//RealmLiveData!!
class LiveRealmData<T : RealmModel>(realmResults: RealmResults<T>) :
    LiveData<RealmResults<T>>() {

    private val results: RealmResults<T> = realmResults

    private val listener: RealmChangeListener<RealmResults<T>> =
        RealmChangeListener<RealmResults<T>> { setValue(results) }

    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }

}

/**
public class LiveRealmData<T extends RealmModel> extends LiveData<RealmResults<T>> {

    private RealmResults<T> results;
    private final RealmChangeListener<RealmResults<T>> listener =
        new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> results) { setValue(results);}
    };

    public LiveRealmData(RealmResults<T> realmResults) {
        results = realmResults;
    }

    @Override
    protected void onActive() {
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        results.removeChangeListener(listener);
    }
}
 */