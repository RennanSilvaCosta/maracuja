package controller;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dto.NewUserDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.UsuarioService;
import util.Constantes;
import validator.ValidatorFormRegister;
import validator.exceptions.ValidatorExceptionsMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControllerRegisterScreen {

    @FXML
    ProgressIndicator progress;

    @FXML
    JFXButton btnSair, btnCriarConta;

    @FXML
    JFXTextField txtNomeEmpresa, txtNomeUsuario, txtEmail;

    @FXML
    JFXPasswordField txtSenha, txtConfirmarSenha;

    @FXML
    Label labelNomeEmpresaError, labelNomeUsuarioError, labelEmailError, labelSenhaError, labelConfirmarSenhaError;

    Map<Integer, String> responseHttp = new HashMap<>();
    Map<String, String> errors = new HashMap<>();
    ValidatorFormRegister validatorFormRegister = new ValidatorFormRegister();
    UsuarioService usuarioService = new UsuarioService();

    public boolean createNewAccount() {
        btnCriarConta.setOnAction(actionEvent -> {
            errors.clear();
            clearLabelsErrors();
            alterColorInputsForDefault();
            errors = validatorFormRegister.registrationFormValidation(txtNomeEmpresa.getText(), txtNomeUsuario.getText(), txtEmail.getText(), txtSenha.getText(), txtConfirmarSenha.getText());
            if (errors.isEmpty()) {
                progress.setVisible(true);
                new addUsuario().start();
            } else {
                setErrorMessages(errors);
            }
        });
        return true;
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();
        clearLabelsErrors();
        alterColorInputsForDefault();

        if (fields.contains("nomeEmpresa")) {
            txtNomeEmpresa.setUnFocusColor(Paint.valueOf(Constantes.COLOR_RED));
            labelNomeEmpresaError.setText(errors.get("nomeEmpresa"));
            new Shake(txtNomeEmpresa).play();
        }
        if (fields.contains("nomeUsuario")) {
            txtNomeUsuario.setUnFocusColor(Paint.valueOf(Constantes.COLOR_RED));
            labelNomeUsuarioError.setText(errors.get("nomeUsuario"));
            new Shake(txtNomeUsuario).play();
        }
        if (fields.contains("email")) {
            txtEmail.setUnFocusColor(Paint.valueOf(Constantes.COLOR_RED));
            labelEmailError.setText(errors.get("email"));
            new Shake(txtEmail).play();
        }
        if (fields.contains("senha")) {
            txtSenha.setUnFocusColor(Paint.valueOf(Constantes.COLOR_RED));
            labelSenhaError.setText(errors.get("senha"));
            new Shake(txtSenha).play();
        }
        if (fields.contains("confirmarSenha")) {
            txtConfirmarSenha.setUnFocusColor(Paint.valueOf(Constantes.COLOR_RED));
            labelConfirmarSenhaError.setText(errors.get("confirmarSenha"));
            new Shake(txtConfirmarSenha).play();
        }
    }

    private void setMessageHettpResponse(Map<Integer, String> response) {
        Set<Integer> fields = response.keySet();
        if (fields.contains(Constantes.STATUS_CODE_SUCCESSFUL) || fields.contains(Constantes.STATUS_CODE_NOCONTENT)) {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.INFORMATION, "OK", ButtonType.OK);
                dialog.initStyle(StageStyle.UTILITY);
                dialog.setTitle(ValidatorExceptionsMessage.FORM_REGISTER_TITLE_ALERT_SUCCESS);
                dialog.setHeaderText(ValidatorExceptionsMessage.FORM_REGISTER_HEADER_ALERT_SUCCESS);
                dialog.setContentText(ValidatorExceptionsMessage.FORM_REGISTER_CONTENTTEXT_ALERT_SUCCESS);
                dialog.showAndWait()
                        .filter(resposta -> resposta.equals(ButtonType.OK))
                        .ifPresent(resposta-> sair());
            });
        } else if (fields.contains(Constantes.STATUS_CODE_BAD_REQUEST)) {
            Platform.runLater(() -> {
                enableJTextFields();
                txtEmail.setUnFocusColor(Paint.valueOf(Constantes.COLOR_RED));
                labelEmailError.setText(ValidatorExceptionsMessage.EMAIL_INVALIDO);
                Alert dialog = new Alert(Alert.AlertType.ERROR, "OK", ButtonType.OK);
                dialog.initStyle(StageStyle.UTILITY);
                dialog.setTitle(ValidatorExceptionsMessage.FORM_REGISTER_TITLE_ALERT_FAIL);
                dialog.setHeaderText(ValidatorExceptionsMessage.FORM_REGISTER_HEADER_ALERT_FAIL);
                dialog.setContentText(ValidatorExceptionsMessage.EMAIL_JA_CADASTRADO);
                dialog.showAndWait();
            });
        }
    }

    public class addUsuario extends Thread {
        @Override
        public void run() {
            responseHttp.clear();
            clearLabelsErrors();
            alterColorInputsForDefault();
            progress.setVisible(true);
            disableJTextFields();
            responseHttp = usuarioService.createNewUser(new NewUserDTO(txtNomeEmpresa.getText(), txtNomeUsuario.getText(), txtEmail.getText(), txtSenha.getText()));
            progress.setVisible(false);
            setMessageHettpResponse(responseHttp);
        }
    }

    @FXML
    private void keyPressed(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ENTER) {
            errors.clear();
            clearLabelsErrors();
            alterColorInputsForDefault();
            errors = validatorFormRegister.registrationFormValidation(txtNomeEmpresa.getText(), txtNomeUsuario.getText(), txtEmail.getText(), txtSenha.getText(), txtConfirmarSenha.getText());
            if (errors.isEmpty()) {
                progress.setVisible(true);
                new addUsuario().start();
            } else {
                setErrorMessages(errors);
            }
        }
    }

    private void clearLabelsErrors() {
        labelNomeEmpresaError.setText("");
        labelNomeUsuarioError.setText("");
        labelEmailError.setText("");
        labelSenhaError.setText("");
        labelConfirmarSenhaError.setText("");
    }

    private void alterColorInputsForDefault() {
        txtNomeEmpresa.setUnFocusColor(Paint.valueOf(Constantes.COLOR_YELLOW));
        txtNomeUsuario.setUnFocusColor(Paint.valueOf(Constantes.COLOR_YELLOW));
        txtEmail.setUnFocusColor(Paint.valueOf(Constantes.COLOR_YELLOW));
        txtSenha.setUnFocusColor(Paint.valueOf(Constantes.COLOR_YELLOW));
        txtConfirmarSenha.setUnFocusColor(Paint.valueOf(Constantes.COLOR_YELLOW));
    }

    private void disableJTextFields() {
        txtNomeEmpresa.setDisable(true);
        txtNomeUsuario.setDisable(true);
        txtEmail.setDisable(true);
        txtSenha.setDisable(true);
        txtConfirmarSenha.setDisable(true);
        btnCriarConta.setDisable(true);
        btnSair.setDisable(true);
    }

    private void enableJTextFields() {
        txtNomeEmpresa.setDisable(false);
        txtNomeUsuario.setDisable(false);
        txtEmail.setDisable(false);
        txtSenha.setDisable(false);
        txtConfirmarSenha.setDisable(false);
        btnCriarConta.setDisable(false);
        btnSair.setDisable(false);
    }

    public void sair() {
        Stage stage = (Stage) btnSair.getScene().getWindow();
        stage.close();
    }
}
