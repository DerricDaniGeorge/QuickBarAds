package com.derric.quickbar.helper;

public interface Movable {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
