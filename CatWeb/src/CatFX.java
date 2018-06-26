import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class CatFX extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    private Button btnLog;
    private Pane pane;
    private Scene scene;
    private TextField emailField;
    private PasswordField pwField;
    private Label userName;
    private Label pass;
    private ListView<String> list;
    private Stage stage;

    private HBox hBox;
    private HBox hBox1;
    private VBox vBox;
    private VBox layout;

    private URL url;
    private HttpURLConnection connection;
    private BufferedReader input;
    private StringBuffer response;
    private JSONObject autoryzacja;
    private JSONObject kotek;
    private JSONArray kotki;
    private String email;
    private String password;
    private String inputLine;
    private String token;
    private ObservableList<String> catList;


    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Cat App");

        pane = new Pane();
        pane.setPrefSize(400,400);
        pane.setPadding(new Insets(25, 25, 25, 25));

        btnLog = new Button("Zaloguj");
        emailField = new TextField();
        pwField = new PasswordField();
        userName = new Label("Wpisz emali: ");
        pass = new Label("Wpisz hasło: ");

        hBox = new HBox(5,userName,emailField);
        hBox1 = new HBox(5,pass,pwField);
        vBox=new VBox(7,hBox,hBox1,btnLog);

        scene = new Scene(vBox, 300, 175);

        hBox.setAlignment(Pos.CENTER);
        hBox1.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);

        primaryStage.setScene(scene);
        primaryStage.show();

        btnLog.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent e) {
                url = null;
                email = emailField.getText();
                password = pwField.getText();
                try {
                    url = new URL("http://smieszne-koty.herokuapp.com/oauth/token?grant_type=password&email="+email+"&password="+password);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(3000);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    response = new StringBuffer();


                    while ((inputLine = input.readLine()) != null){
                        response.append(inputLine);
                    }

                    input.close();

                    autoryzacja = new JSONObject(response.toString());
                    token = autoryzacja.getString("access_token");

                    url = new URL("http://smieszne-koty.herokuapp.com/api/kittens"+"?access_token="+token);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(3000);

                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    response = new StringBuffer();

                    while ((inputLine = input.readLine()) != null) {
                        response.append(inputLine);
                    }

                    input.close();

                    kotki = new JSONArray(response.toString());
                    catList = FXCollections.observableArrayList();
                    for (int i = 0; i < kotki.length(); i++) {
                        kotek = kotki.getJSONObject(i);
                        catList.add(kotek.getString("name"));
                    }

                    layout = new VBox();

                    list = new ListView<String>();
                    list.setItems(catList);

                    layout.getChildren().addAll(list);


                    stage = new Stage();
                    stage.setTitle("Lista kotków");
                    stage.setScene(new Scene(layout, 400, 450));


                    stage.show();
                    // Hide this current window (if this is what you want)
                    ((Node)(e.getSource())).getScene().getWindow().hide();

                } catch (IOException er) {
                   showAlert("Użyto niepoprawne dane do logowania !");
                }


            }
        });
    }

    public static void showAlert(String statement){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd!");
        alert.setHeaderText("Coś poszło nie tak...");
        alert.setContentText(statement);
        alert.showAndWait();
    }
}
