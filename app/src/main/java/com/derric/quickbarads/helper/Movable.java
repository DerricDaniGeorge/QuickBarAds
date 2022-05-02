package com.derric.quickbarads.helper;

public interface Movable {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
