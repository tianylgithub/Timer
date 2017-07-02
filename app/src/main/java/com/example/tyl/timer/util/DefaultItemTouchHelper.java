package com.example.tyl.timer.util;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by TYL on 2017/6/19.
 */

public class DefaultItemTouchHelper extends ItemTouchHelper {

    public DefaultItemTouchHelper(DefaultItemTouchHelpCallback.OnItemTouchCallbackListener  onItemTouchCallbackListener) {
        super(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
    }


}




