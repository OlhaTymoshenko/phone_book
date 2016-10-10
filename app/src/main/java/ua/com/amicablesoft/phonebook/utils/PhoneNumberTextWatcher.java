package ua.com.amicablesoft.phonebook.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * Created by lapa on 10.10.16.
 */
public class PhoneNumberTextWatcher implements TextWatcher {
    private boolean isInAfterTextChanged;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isInAfterTextChanged) {
            isInAfterTextChanged = true;
            if (s.length() > 0 && !s.toString().startsWith("+")) {
                s.insert(0, "+");
            }

            try {
                String phoneNumber = PhoneNumberUtil.getInstance().format(PhoneNumberUtil.getInstance()
                        .parse(s.toString(), null), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                s.clear();
                s.append(phoneNumber);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }

            isInAfterTextChanged = false;
        }
    }
}
