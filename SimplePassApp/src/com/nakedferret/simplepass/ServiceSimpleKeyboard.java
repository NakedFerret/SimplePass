package com.nakedferret.simplepass;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;

// A keyboard service that takes care of basic text input. Nothing fancy
public class ServiceSimpleKeyboard extends InputMethodService implements
		OnKeyboardActionListener {

	private KeyboardView keyboardView;
	private Keyboard qwertyKeyboard, symbolKeyboard, currKeyboard;

	@Override
	public void onInitializeInterface() {
		qwertyKeyboard = new Keyboard(this, R.xml.qwerty);
		symbolKeyboard = new Keyboard(this, R.xml.symbols);
	}

	@Override
	public View onCreateInputView() {
		keyboardView = (KeyboardView) getLayoutInflater().inflate(
				R.layout.input, null);
		keyboardView.setOnKeyboardActionListener(this);
		return keyboardView;
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);
		setKeyboard(qwertyKeyboard);
		keyboardView.setPreviewEnabled(false); // Don't show popups
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {

		switch (primaryCode) {
		case Keyboard.KEYCODE_SHIFT:
			toggleShift();
			break;
		case Keyboard.KEYCODE_CANCEL:
			requestHideSelf(0);
			break;
		case Keyboard.KEYCODE_DELETE:
			deleteLastLetter();
			break;
		case Keyboard.KEYCODE_MODE_CHANGE:
			setNextKeyboard();
			break;
		default:
			commitCharacter(primaryCode);
		}
	}

	void setKeyboard(Keyboard keyboard) {
		keyboardView.setKeyboard(keyboard);
		currKeyboard = keyboard;
	}

	void setNextKeyboard() {
		setKeyboard((currKeyboard == qwertyKeyboard) ? symbolKeyboard
				: qwertyKeyboard);
	}

	private void commitCharacter(int keyCode) {
		if (keyboardView.isShifted())
			keyCode = Character.toUpperCase((char) keyCode);

		String c = String.valueOf((char) keyCode);
		getCurrentInputConnection().commitText(c, 1);
	}

	private void deleteLastLetter() {
		getCurrentInputConnection().deleteSurroundingText(1, 0);
	}

	private void toggleShift() {
		if (keyboardView.isShifted())
			keyboardView.setShifted(false);
		else
			keyboardView.setShifted(true);
	}

	@Override
	public void onPress(int primaryCode) {

	}

	@Override
	public void onRelease(int primaryCode) {

	}

	@Override
	public void onText(CharSequence text) {

	}

	@Override
	public void swipeDown() {

	}

	@Override
	public void swipeLeft() {

	}

	@Override
	public void swipeRight() {

	}

	@Override
	public void swipeUp() {

	}

}