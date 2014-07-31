package com.example.quickid;

import android.app.Fragment;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;

public class DialpadFragment extends Fragment implements View.OnClickListener,
		View.OnLongClickListener, View.OnKeyListener,
		AdapterView.OnItemClickListener, TextWatcher,
		PopupMenu.OnMenuItemClickListener, DialpadKeyButton.OnPressedListener {

	public DialpadFragment() {
		// TODO Auto-generated constructor stub
	}

	private void setupKeypad(View fragmentView) {
		final int[] buttonIds = new int[] { R.id.zero, R.id.one, R.id.two,
				R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven,
				R.id.eight, R.id.nine, R.id.star, R.id.pound };

		final int[] numberIds = new int[] { R.string.dialpad_0_number,
				R.string.dialpad_1_number, R.string.dialpad_2_number,
				R.string.dialpad_3_number, R.string.dialpad_4_number,
				R.string.dialpad_5_number, R.string.dialpad_6_number,
				R.string.dialpad_7_number, R.string.dialpad_8_number,
				R.string.dialpad_9_number, R.string.dialpad_star_number,
				R.string.dialpad_pound_number };

		final int[] letterIds = new int[] { R.string.dialpad_0_letters,
				R.string.dialpad_1_letters, R.string.dialpad_2_letters,
				R.string.dialpad_3_letters, R.string.dialpad_4_letters,
				R.string.dialpad_5_letters, R.string.dialpad_6_letters,
				R.string.dialpad_7_letters, R.string.dialpad_8_letters,
				R.string.dialpad_9_letters, R.string.dialpad_star_letters,
				R.string.dialpad_pound_letters };

		final Resources resources = getResources();

		DialpadKeyButton dialpadKey;
		TextView numberView;
		TextView lettersView;

		for (int i = 0; i < buttonIds.length; i++) {
			dialpadKey = (DialpadKeyButton) fragmentView
					.findViewById(buttonIds[i]);
			dialpadKey.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.MATCH_PARENT));
			dialpadKey.setOnPressedListener(this);
			numberView = (TextView) dialpadKey
					.findViewById(R.id.dialpad_key_number);
			lettersView = (TextView) dialpadKey
					.findViewById(R.id.dialpad_key_letters);
			final String numberString = resources.getString(numberIds[i]);
			numberView.setText(numberString);
			dialpadKey.setContentDescription(numberString);
			if (lettersView != null) {
				lettersView.setText(resources.getString(letterIds[i]));
			}
		}

		// Long-pressing one button will initiate Voicemail.
		fragmentView.findViewById(R.id.one).setOnLongClickListener(this);

		// Long-pressing zero button will enter '+' instead.
		fragmentView.findViewById(R.id.zero).setOnLongClickListener(this);

	}

	@Override
	public void onPressed(View view, boolean pressed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
