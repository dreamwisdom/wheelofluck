package local.isi.wheelofluck.events;

import android.app.Activity;

import local.isi.wheelofluck.view.Arrow;

/**
 * Created by Dream on 9/12/2015.
 */
public class ArrowEvent {
//    Activity gameActivity;
//
//    public ArrowEvent(Activity gameActivity) {
//        this.gameActivity = gameActivity;
//    }
//
//    public Activity getGameActivity() {
//        return gameActivity;
//    }
//
//    public void setGameActivity(Activity gameActivity) {
//        this.gameActivity = gameActivity;
//    }
    int nbArrow;

    public ArrowEvent(int nbArrow) {
        this.nbArrow = nbArrow;
    }

    public int getNbArrow() {
        return nbArrow;
    }

    public void setNbArrow(int nbArrow) {
        this.nbArrow = nbArrow;
    }
}
