package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    private DatabaseOperations databaseOperations;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        databaseOperations = new DatabaseOperations();
        if(!databaseOperations.checkDBPresent()){
            System.out.println("DB not present");
            databaseOperations.generateDatabase();
        }
        databaseOperations.insertData("extDir","trial");





        primaryStage.setTitle("Mushroom");
        Button startSystem = new Button("/home/jam/daikon-ext/catkin_ws/src/recorder/");
        startSystem.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){
                //startSystem();
                System.out.println("Check");
            }
        });

        BorderPane initialLayout = new BorderPane();

        MenuBar menuBar = generateMenuBar();
        initialLayout.setTop(menuBar);
        //initialLayout.setLeft(addVBox());
       // initialLayout.setCenter(addGridPane());
      //  initialLayout.setRight(addFlowPane());

     //   initialLayout.getChildren().add(startSystem);
        Scene scene = new Scene(initialLayout, 900, 450);
        initialLayout.prefWidthProperty().bind(scene.widthProperty());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public GridPane addGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        // Category in column 2, row 1
        Text category = new Text("Sales:");
        category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(category, 1, 0);

        // Title in column 3, row 1
        Text chartTitle = new Text("Current Year");
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(chartTitle, 2, 0);

        // Subtitle in columns 2-3, row 2
        Text chartSubtitle = new Text("Goods and Services");
        grid.add(chartSubtitle, 1, 1, 2, 1);



        // Left label in column 1 (bottom), row 3
        Text goodsPercent = new Text("Goods\n80%");
        GridPane.setValignment(goodsPercent, VPos.BOTTOM);
        grid.add(goodsPercent, 0, 2);

        // Chart in columns 2-3, row 3


        // Right label in column 4 (top), row 3
        Text servicesPercent = new Text("Services\n20%");
        GridPane.setValignment(servicesPercent, VPos.TOP);
        grid.add(servicesPercent, 3, 2);

        return grid;
    }

    public VBox addVBox() {

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("Data");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        Hyperlink options[] = new Hyperlink[] {
                new Hyperlink("Sales"),
                new Hyperlink("Marketing"),
                new Hyperlink("Distribution"),
                new Hyperlink("Costs")};

        for (int i=0; i<4; i++) {
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(options[i]);
        }

        return vbox;
    }
    public MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");

        MenuSettings menuSettings = new MenuSettings();

        MenuItem settings = new MenuItem("Settings");
        settings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.print("Settings clicked");
                menuSettings.display();
            }
        });

        if(menuSettings.clicked){
            //System.out.print("hello");
        }


        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });
        menuBar.getMenus().addAll(menuFile,menuEdit,menuView);
        menuFile.getItems().addAll(settings, exit);
        return menuBar;
    }

    private  void startSystem(){
        System.out.println("Initializing roscore...");
        roscoreInit();
        System.out.println("Initializing ServiceHandler...");
        serviceHandler();
    }

    private static void roscoreInit(){
        String[] command = {"/bin/bash","-c","roscore"};
        String[] command2 = {"/bin/bash","-c","rosservice list"};
        ThreadHandler TH = new ThreadHandler(command, false);
        TH.start();
        try{ Thread.sleep(4000); }catch(Exception e){ e.printStackTrace(); }

        ThreadHandler TH2 = new ThreadHandler(command2, false);
        TH2.start();
        try{ Thread.sleep(3000); }catch(Exception e){ e.printStackTrace(); }
        if (TH2.returnedData != null){
            System.out.println("done.");
        } else {
            System.out.println("failed.");
        }
    }

    private  void serviceHandler(){
        String[] command = {"/bin/bash","-c","python2.7" + databaseOperations.retrieveData("extDir") + "scripts/service_handler.py"};
        ThreadHandler TH3 = new ThreadHandler(command, false);
        TH3.start();
        System.out.print(TH3.returnedData);
        System.out.print("done. \nServiceHandler running...");

    }



}
