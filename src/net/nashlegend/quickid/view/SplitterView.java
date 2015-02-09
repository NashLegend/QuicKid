
package net.nashlegend.quickid.view;

import net.nashlegend.quickid.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SplitterView extends FrameLayout {

    TextView textView;
    public String splitter;

    public SplitterView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_splitter_view, this);
        textView = (TextView) findViewById(R.id.textview_splitter);
    }

    public SplitterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplitterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build(String txt) {
        splitter = txt;
        textView.setText(splitter);
    }

}
