package ipl.estg.happyguest.app.home.home;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import ipl.estg.happyguest.databinding.InsertCodeBinding;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.api.APIRoutes;

public class InsertCode {

    private TextInputLayout inputCode;
    private EditText txtCode;
    private APIRoutes api;
    private Token token;

    public void insertCode(InsertCodeBinding insertCodeBinding) {
        Toast.makeText(insertCodeBinding.getRoot().getContext(), "Insert code", Toast.LENGTH_SHORT).show();
        // TextInputLayouts and EditTexts
        inputCode = insertCodeBinding.inputCode;
        txtCode = insertCodeBinding.textCode;

        inputCode.setError(null);
        String code = txtCode.getText().toString();

        if (code.isEmpty()) {
            inputCode.setError("Please insert a code");
        }
    }
}
