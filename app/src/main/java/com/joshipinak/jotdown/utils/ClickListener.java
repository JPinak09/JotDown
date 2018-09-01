package com.joshipinak.jotdown.utils;

import android.view.View;

interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
