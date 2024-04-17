package io.github.jagodevreede.sdkmanui.view;

import io.github.jagodevreede.sdkman.api.http.CancelableTask;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class PopupView {
    private static Logger logger = LoggerFactory.getLogger(PopupView.class);
    private final Stage primaryStage;

    public PopupView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showError(Throwable e) {
        logger.warn(e.getMessage(), e);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception occurred");
            alert.setHeaderText(e.getMessage());

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
        });
    }

    public void showInformation(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(message);

            alert.showAndWait();
        });
    }

    public ProgressWindow showProgress(String message, CancelableTask cancelableTask) {
        ProgressBar progressBar = new ProgressBar();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Platform.runLater(() -> {
            alert.setTitle("Please wait");
            alert.setHeaderText(message);

            alert.getDialogPane().setContent(progressBar);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeCancel) {
                cancelableTask.cancel();
            }
        });
        return new ProgressWindow(progressBar, alert);
    }

    public record ProgressWindow(ProgressBar progressBar, Alert alert) {}
}
