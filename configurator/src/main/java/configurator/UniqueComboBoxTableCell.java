package configurator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * This generic class is intended to be used in place of the ComboBoxTableCell class
 * in cases where the combo box list data must be different for each row.  This cell
 * will display content in a popup list when there there is a "choices" property
 * in the data model S.  The data model must also have functions defined for
 * getChoices, setChoices, getValue and setValue (where value is the specific value
 * being set by this cell and choices is an observable list of choices (can be
 * empty or null).
 * One final improvement with this cell is that the value of any edits will be
 * committed when the cell loses focus (within the same pane).
 * With the exception of the above noted difference, this control has been designed
 * to be functionally equivalent to the JavaFx comboBox as described in the
 * online documentation.
 * @param <S> - The type object associated with the TableView this cell is stored in
 * @param <T> - The type object stored/selected in the cell
 */
public class UniqueComboBoxTableCell<S, T> extends TableCell<S, T> {
    ComboBox<T> cb;  // the combo box associated with this cell
    TextField tf;    // the textfield control to use when there are no choices
    ObjectProperty<S> item;            // the item stored in this cell
    ObservableList<T> choices;                  // the dynamic list of choices
    static Image redDotImage;
    static Image yellowDotImage;

    // the following properties match those found in the java documentation for
    // ComboBox.
    BooleanProperty comboBoxEditable;  // when true, the combo box can be edited
    ObjectProperty<StringConverter<T>> converter; // the user-specified string converter

    /**
     * Constructors
     *
     * Although the documentation shows multiple different constructors, the following
     * is the only one that is ever used since cells are constructed by forColumn()
     */
    UniqueComboBoxTableCell(StringConverter<T> converter, ObservableList<T> items) {
        // Add the style for the table cell
        this.getStyleClass().add("unique-combo-box-table-cell");

        // create the object properties and assign values - these  properties match
        // those of the combobox found in the online documentation
        this.converter = new SimpleObjectProperty(this, "converter");
        if (converter == null) {
            this.converter.set((StringConverter) new DefaultStringConverter());
        } else {
            this.converter.set(converter);
        }

        // combo box is always non-editable
        this.comboBoxEditable = new SimpleBooleanProperty(this, "comboBoxEditable");
        this.comboBoxEditable.set(false);

        this.item = new SimpleObjectProperty<>();

        // save these items for later when the combo box gets built
        this.choices = items;

        if (redDotImage==null) {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            InputStream is = classLoader.getResourceAsStream("red_dot.png");
            if (is != null) redDotImage = new Image(is);
        }
        if (yellowDotImage==null) {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            InputStream is = classLoader.getResourceAsStream("yellow_dot.png");
            if (is != null) yellowDotImage = new Image(is);
        }

    }

    /** forColumn()
     * These are called back by the cell factory to create an instance of the cell.
     */
    @SafeVarargs
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(T... tArry) {
        return forTableColumn((StringConverter)new DefaultStringConverter(), (Object[])tArry);
    }

    @SafeVarargs
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> sc, T... tArry) {
        return forTableColumn(sc, FXCollections.observableArrayList(tArry));
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(ObservableList<T> ol) {
        return forTableColumn((StringConverter)new DefaultStringConverter(), (ObservableList)ol);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> sc, ObservableList<T> ol) {
        return (cell) -> new UniqueComboBoxTableCell(sc, ol);
    }

    private void updateCellMarking() {
        Method fn;
        if ((item==null)||(item.get()==null)) return;
        try {
            fn = item.get().getClass().getMethod("getState", (Class<?>[]) null);
            String state = (String) fn.invoke(item.get());
            ImageView iv;
            switch (state) {
                case "error":
                    iv = new ImageView(redDotImage);
                    iv.setFitWidth(12);
                    iv.setFitHeight(12);
                    setGraphic(iv);
                    break;
                case "warn":
                    iv = new ImageView(yellowDotImage);
                    iv.setFitWidth(12);
                    iv.setFitHeight(12);
                    setGraphic(iv);
                    break;
                default:
                    setGraphic(null);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
    }

    /**
     * CancelEdit()
     * From the user documentation:
     * Call this function to transition from an editing state into a
     * non-editing state, without saving any user input.
     */
    public void cancelEdit() {
        setText(getValue(item.get()));
        cb.setValue(converter.get().fromString(getValue(item.get())));
        tf.setText(getValue(item.get()));
        updateCellMarking();
        super.cancelEdit();
    }

    /**
     * getConverter()
     * from the user documentation:
     * Returns the StringConverter used in this cell.
     * @return the string converter used in this cell
     */
    public StringConverter<T> getConverter() {
        return this.converter.get();
    }

    /**
     * getItems()
     * From the user documentation:
     * Returns the items to be displayed in the ChoiceBox when it is showing.
     * @return the choice box choices
     */
    public ObservableList<T> getItems() {
        return this.choices;
    }

    /**
     * isComboBoxEditable()
     * From the user documentation:
     * Returns true if the ComboBox is editable.
     * @return - a boolean - true if editable
     */
    public boolean isComboBoxEditable() {
        return this.comboBoxEditable.get();
    }

    /**
     * setComboBoxEditable()
     * From the user documentation:
     * Configures the ComboBox to be editable (to allow user input
     * outside of the options provide in the dropdown list).
     * @param value - the value to set
     */
    public void setComboBoxEditable(boolean value) {
        this.comboBoxEditable.set(value);
    }

    /**
     * setConverter()
     * From the user documentation:
     * Sets the StringConverter to be used in this cell.
     * @param value - the value to set
     */
    public void setConverter(StringConverter<T> value) {
        this.converter.set(value);
    }

    /**
     * getDescription()
     * This is a special helper function that gets the value of the description field from
     * the data model.
     * @param data - the data model for the row of the table
     * @return - the value for the cell
     */
    private String getDescription(S data) {
        Method fn;
        try {
            fn = data.getClass().getMethod("getDescription", (Class<?>[]) null);
            return (String) fn.invoke(data);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
        return "";
    }

    /**
     * getMaxValue()
     * This is a special helper function that gets the max value of the field from
     * the data model.
     * @param data - the data model for the row of the table
     * @return - the value for the cell
     */
    private String getMaxValue(S data) {
        Method fn;
        try {
            fn = data.getClass().getMethod("getMaxValue", (Class<?>[]) null);
            return (String) fn.invoke(data);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
        return "";
    }

    /**
     * getMinValue()
     * This is a special helper function that gets the value of the field from
     * the data model.
     * @param data - the data model for the row of the table
     * @return - the value for the cell
     */
    private String getMinValue(S data) {
        Method fn;
        try {
            fn = data.getClass().getMethod("getMinValue", (Class<?>[]) null);
            return (String) fn.invoke(data);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
        return "";
    }

    /**
     * getValue()
     * This is a special helper function that gets the value of the field from
     * the data model.
     * @param data - the data model for the row of the table
     * @return - the value for the cell
     */
    private String getValue(S data) {
        Method fn;
        try {
            fn = data.getClass().getMethod("getValue", (Class<?>[]) null);
            return (String) fn.invoke(data);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
        return "";
    }

    /**
     * getEditable()
     * This is a special helper function that gets the value of the editable field from
     * the data model.
     * @param data - the data model for the row of the table
     * @return - the value for the cell
     */
    private boolean getEditable(S data) {
        Method fn;
        try {
            fn = data.getClass().getMethod("getEditable", (Class<?>[]) null);
            return (boolean) fn.invoke(data);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
        return true;
    }

    /**
     * setValue()
     * this is a special helper function that sets the changed value back to the
     * dta model.
     * @param data - the data model to write to
     * @param value - the new value to write
     */
    private void setValue(S data,T value) {
        Method fn;
        try {
            fn = data.getClass().getMethod("setValue", String.class);
            fn.invoke(data, converter.get().toString(value));
        } catch (NoSuchMethodException |InvocationTargetException | IllegalAccessException e) {
        }
    }

    /**
     * startEdit()
     * From the user documentation:
     * Call this function to transition from a non-editing state into an
     * editing state, if the cell is editable.
     */
    @Override
    public void startEdit() {
        // only execute this code if the item can be edited
        if ((!this.isEditable())||(!this.getTableColumn().isEditable())||(!this.getTableView().isEditable())) return;

        // get a fresh copy of the item from the table
        item.set(getTableRow().getItem());

        // create a combo box for the cell if one doesn't already exist
        if (this.cb==null) {
            // set the choices for the combo box
            this.cb = new ComboBox<>(this.choices);
            // handle special processing for special keys
            cb.setOnKeyPressed((handler) -> {
                if (handler.getCode() == KeyCode.ESCAPE) {
                    // abort any edit in progress
                    cb.setValue(getConverter().fromString(getValue(item.get())));
                    cancelEdit();
                    handler.consume();
                }
            });

            // handle changes in the combo box
            cb.setOnAction((handler) -> {
                if (getConverter() != null) {
                    if (cb.getValue()!=null) {
                        setValue(item.get(), cb.getValue());
                        commitEdit(cb.getValue());
                    } else {
                        commitEdit((T)getText());
                    }
                    handler.consume();
                }
            });

            // handle loss of focus event - this works if the focus is lost to
            // another cell in the table, but not another pane altogether
            cb.focusedProperty().addListener((event) -> {
                if (!cb.isFocused()) {
                    setValue(item.get(), cb.getValue());
                    commitEdit(cb.getValue());
                    updateCellMarking();
                    setText(converter.get().toString(cb.getValue()));
                }
            });

            this.tf = new TextField();
            // handle special processing for special keys
            // On key pressed is important (rather than onIKeyReleased) because
            // escape and enter must be intercepted before loss of focus.
            tf.setOnKeyReleased((handler) -> {
                if (handler.getCode() == KeyCode.ESCAPE) {
                    // abort any edit in progress
                    cancelEdit();
                    handler.consume();
                } else if ((handler.getCode() == KeyCode.ENTER) ||
                        (handler.getCode() == KeyCode.TAB))
                {
                    // commit any edit in progress
                    commitEdit(getConverter().fromString(tf.getText()));
                    setValue(item.get(), converter.get().fromString(tf.getText()));
                    handler.consume();
                }
            });

            // handle loss of focus event - this works if the focus is lost to
            // another cell in the table, but not another pane altogether
            tf.focusedProperty().addListener((event) -> {
                if (!tf.isFocused()) {
                    commitEdit(getConverter().fromString(tf.getText()));
                    setValue(item.get(), converter.get().fromString(tf.getText()));
                }
            });

            // bind the combobox editable property so that changes at the cell level will be
            // reflected at the combo box
            this.cb.editableProperty().bind(this.comboBoxEditable);

        }

        // select the item in the combo box
        this.cb.getSelectionModel().select(getItem());

        // after this is called, the onStartEdit for the cell will be invoked.  It is important
        // that the list box options be updated after making this call just in case they are
        // updated during the call;
        super.startEdit();

        // see if there is a choices property in this cell's information
        S rowData = getTableView().getSelectionModel().getSelectedItem();

        // see if there is a method to get the choices property, if so, the name of the
        // method should be "getChoices"
        this.choices = null;
        try {
            Method getChoices = rowData.getClass().getMethod("getChoices", (Class<?>[]) null);
            AnnotatedType at = getChoices.getAnnotatedReturnType();
            if (at.getType().getTypeName().equals("javafx.collections.ObservableList<java.lang.String>")) {
                // return type matches - invoke the function call to get the choices
                this.choices = (ObservableList<T>) getChoices.invoke(rowData);
                this.cb.setItems(this.choices);
            }
        } catch (NoSuchMethodException | InvocationTargetException |IllegalAccessException e) {
        }

        // set the initial text in the combo box
        String val = getValue(rowData);
        tf.setText(val);
        cb.setValue((T)val);

        // clear the text in the row
        setText("");

        cb.setPrefWidth(1e100);
        tf.setPrefWidth(1e100);
        if (choices==null) {
            // there are no choices - disable the popup and enable text entry
            // insert the text field into the cell by setting it as the graphic
            // make sure that the box width fills the cell
            tf.setDisable(false);
            setGraphic(tf);
            tf.selectAll();
            tf.requestFocus();
            cb.setDisable(true);
        } else {
            // insert the combo box into the cell by setting it as the graphic
            // make sure that the box width fills the cell
            cb.setDisable(false);
            setGraphic(cb);
            cb.requestFocus();
            tf.setDisable(true);
        }
    }

    /**
     * updateItem()
     * From the user documentation:
     * The updateItem method should not be called by developers, but it is
     * the best method for developers to override to allow for them to
     * customise the visuals of the cell.
     * @param item - The new item for the cell.
     * @param empty - whether or not this cell represents data from the list.
     *              If it is empty, then it does not represent any domain data,
     *              but is a cell being used to render an "empty" row.
     */
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            // per the user documentation, if the item is empty or null,
            // remove any cell text and graphic
            setText(null);
            setGraphic(null);
        } else if (!isEditing()) {
            this.item.set(getTableRow().getItem());
            if ((this.item!=null)&&(this.item.get()!=null)) {
                // disable the control if it is not editable.
                if (!isEditable()) {
                    getTableRow().setDisable(true);
                } else {
                    getTableRow().setDisable(false);
                }

                Tooltip tt = getTooltip();
                if (getTooltip() == null) {
                    tt = new Tooltip();
                    this.setTooltip(tt);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(getDescription(this.item.get()));

                if (getMaxValue(this.item.get())!=null) {
                    sb.append("\nMaximum Value = "+getMaxValue(this.item.get()));
                }
                if (getMinValue(this.item.get())!=null) {
                    sb.append("\nMinimum Value = "+getMinValue(this.item.get()));
                }
                tt.setText(sb.toString());
                tt.setPrefWidth(350);
                tt.setWrapText(true);
            }

            // here the control is not editing - make sure that it is no
            // longer visible
            updateCellMarking();

            // set the proper text - using the converter if specified
            String value = "";
            if (getItem()!=null) {
                if (converter.get() != null) {
                    // here if the converter is specified
                    value = converter.get().toString(getItem());
                } else {
                    value = getItem().toString();
                }
            }

            // set the value in the control
            setText(value);

            // now set the value in the object
            try {
                Method setValue = item.getClass().getMethod("getChoices", (Class<?>[]) null);
                setValue.invoke(value);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            }
        } else {
            System.out.println("here");
        }
    }
}

