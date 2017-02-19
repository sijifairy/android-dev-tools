package com.lizhe.devtools.itemview;

/**
 * Created by lz on 2/18/17.
 */

public interface OnItemCallbackListener {
    /**
     * @param fromPosition 起始位置
     * @param toPosition   移动的位置
     */
    void onMove(int fromPosition, int toPosition);

    void onSwipe(int position);
}
