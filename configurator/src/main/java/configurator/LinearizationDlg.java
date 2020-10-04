package configurator;
/********************************************************************
 * LinearizeDlg
 * 
 * This class implements the structure and functionality associated
 * with a dialog box that contains both tabular and graphical
 * representation of an sensor's linearization curve.  The 
 * horixontal (X) axis of the chart is expressed in sensor output
 * units (e.g. Volts), and the vertical axis is expressed in
 * the native units for the sensor (e.g. Newtons). 
 * This dialog box is intended to be opened in the process of
 * configuring a sensor node.
 * 
 * =================================================================
 * Copyright(C) 2020, PICMG
 * All Rights Reserved
 */
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import jsonreader.JsonArray;
import jsonreader.JsonObject;
import jsonreader.JsonValue;

public class LinearizationDlg extends Stage {
	private Scene scene;
    private TableView<Data<Number, Number>> table = new TableView<>();
    private boolean result = false;
    private String sensorOutputUnits = "";
    private String sensorUnits = "";
    XYChart.Series<Number,Number> tableData = new XYChart.Series<Number,Number>();
    
	public LinearizationDlg() {
		super();
        setTitle("Set Linearization Parameters");

        // create a line chart and configure its behavior
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Sensor Output "+sensorOutputUnits);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Sensor Value "+sensorUnits);
        LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setAxisSortingPolicy(SortingPolicy.NONE);

//        // create the data series for the line chart and populate it our data
//        
//        tableData.getData().add(new XYChart.Data<Number,Number>( 1, 567));
//        tableData.getData().add(new XYChart.Data<Number,Number>(10, 800));
//        tableData.getData().add(new XYChart.Data<Number,Number>(20, 780));
//        tableData.getData().add(new XYChart.Data<Number,Number>(40, 810));
//        tableData.getData().add(new XYChart.Data<Number,Number>(80, 850));

        // attach the data series to the chart
        lineChart.getData().add(tableData);

        //=====================================================
        //  Set up the table column for the X axis values
        TableColumn<Data<Number,Number>,Number> xCol = new TableColumn<>("x");        
        // connect edits of the column back to the data set
        xCol.setCellValueFactory(
       		new PropertyValueFactory<XYChart.Data<Number,Number>,Number>("XValue")
        );
        // handle editing of the column, using the default converter to number
        xCol.setCellFactory(TextFieldTableCell.<Data<Number,Number>, Number>forTableColumn(new NumberStringConverter()));
        // when edits are committed, sort the table so that it is always in ascending
        // order relative to the x axis
        xCol.setOnEditCommit(t -> {
            CellEditEvent<Data<Number,Number>, Number> evt = (CellEditEvent<Data<Number,Number>, Number>) t;
            Data<Number,Number> row = evt.getTableView().getItems().get(evt.getTablePosition().getRow());
            row.setXValue(evt.getNewValue());
            evt.getTableView().getItems().sort((d1,d2) -> {
            	if (d1.getXValue().doubleValue()<d2.getXValue().doubleValue()) return -1;
            	if (d1.getXValue().doubleValue()>d2.getXValue().doubleValue()) return 1;
            	return 0;
            });
        });
        // set column behavior and attach the column to the table
        xCol.setSortable(false);
        xCol.setResizable(false);
        xCol.setEditable(true);
        table.getColumns().add(xCol);
        
        //=====================================================
        //  Set up the table column for the Y axis values
        TableColumn<Data<Number,Number>,Number> yCol = new TableColumn<>("y");
        // connect edits of the table back to the data set
        yCol.setCellValueFactory(
            	new PropertyValueFactory<XYChart.Data<Number,Number>,Number>("YValue")
            );
        // handle editing of the column, using the default converter to number
        yCol.setCellFactory(TextFieldTableCell.<Data<Number,Number>, Number>forTableColumn(new NumberStringConverter()));
        // set column behavior and attach the column to the table
        yCol.setSortable(false);
        yCol.setResizable(false);
        yCol.setEditable(true);
        table.getColumns().add(yCol);
        
        //====================================================
        // load the data series into the table and make the table editable
        table.setItems(tableData.getData());
        table.setEditable(true);
        //table.prefWidthProperty().set(xCol.widthProperty().get()+yCol.widthProperty().get()+5);
        table.prefWidthProperty().bind(xCol.widthProperty().add(yCol.widthProperty()).add(20));
        table.setFixedCellSize(35);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1)));
        table.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        table.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        
        //====================================================
        // configure the layout
        HBox hboxTableButtons = new HBox();
        Button bplus = new Button("+");
        Button bminus = new Button("- ");
	    hboxTableButtons.setPadding(new Insets(25, 25, 25, 25));
	    hboxTableButtons.setSpacing(20);
        hboxTableButtons.setAlignment(Pos.CENTER);
        hboxTableButtons.getChildren().addAll(bplus,bminus);
        VBox vbox = new VBox();
	    vbox.setPadding(new Insets(25, 5, 5, 25));
        vbox.getChildren().addAll(table,hboxTableButtons);
        
	    VBox vbox2 = new VBox(lineChart);
	    vbox2.setPadding(new Insets(25, 25, 5, 5));

        HBox hbox = new HBox();
	    hbox.setPadding(new Insets(25, 25, 25, 25));
	    hbox.setSpacing(20);
        Button bOk = new Button("OK");
        Button bCancel = new Button("Cancel");
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(bOk,bCancel);

	    BorderPane borderpane = new BorderPane();
        borderpane.setCenter(vbox2);
        borderpane.setLeft(vbox);
        borderpane.setBottom(hbox);
        scene = new Scene(borderpane);

        /*
         * Button functions
         */
        bplus.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	if (table.getItems().size()<5) {
            		XYChart.Data<Number,Number> i = table.getItems().get(table.getItems().size()-1);
            		table.getItems().add(new XYChart.Data<Number,Number>(i.getXValue(),i.getYValue()));
            	}
            }
        });
        bminus.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	if (table.getItems().size()>2) {
            		table.getItems().remove(table.getItems().size()-1);
                    table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1)));
            	}
            }
        });
        bOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	result = true;
            	close();
            }
        });
        bCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	close();
            }
        });

        setScene(scene);
        setHeight(500);
        setWidth(800);
	}
	
	/*
	 * Set the table data values from the specified JsonArray structure. 
	 * Each element in the array should consist of a JsonObject with two keywords:
	 * xvalue, and yvalue 
	 */
	public void setDataValues(JsonArray jsonData) {
		// remove any data that already exists
		tableData.getData().clear();		
		
		if (jsonData != null) {			
			// iterate to add the data points
			jsonData.forEach(o -> {
				double xvalue = ((JsonObject)o).getDouble("xvalue");
				double yvalue = ((JsonObject)o).getDouble("yvalue");
		        tableData.getData().add(new XYChart.Data<Number,Number>( xvalue, yvalue));
			});		
		}
		while (tableData.getData().size()<2) {
			tableData.getData().add(new XYChart.Data<Number,Number>(0,0));
		}
	}
	
	/*
	 * Get the table data values from the form and populate them into a 
	 * JsonArray.  Each element in the array will consist of an object that
	 * represents a data point.  The xvalue and yvalue keywords will specify
	 * the x and y position of the data point.
	 */
	public JsonArray getDataValues() {
		JsonArray ary = new JsonArray();
		tableData.getData().forEach(d -> {
			JsonObject obj = new JsonObject();
			obj.put("xvalue", new JsonValue(Double.toString(d.getXValue().doubleValue())));
			obj.put("yvalue", new JsonValue(Double.toString(d.getYValue().doubleValue())));
		});	
		return ary;
	}
	
	@Override
	public void showAndWait() {
		result = false;
		super.showAndWait();
	}
	
	public boolean getResult() {
		return result;
	}
}
