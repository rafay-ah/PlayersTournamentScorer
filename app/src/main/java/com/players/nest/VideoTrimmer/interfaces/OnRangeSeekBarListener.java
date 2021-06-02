package com.players.nest.VideoTrimmer.interfaces;

import com.players.nest.VideoTrimmer.view.RangeSeekBarView;

public interface OnRangeSeekBarListener {

    void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value);
}
