//*******************************************************************
//    ValidatedTextFieldTableCell.java
//
//    More information on the PICMG IoT data model can be found within
//    the PICMG family of IoT specifications.  For more information,
//    please visit the PICMG web site (www.picmg.org)
//
//    Copyright (C) 2020,  PICMG
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
package org.picmg.configurator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.UnaryOperator;

/**
 * this generic class extends the capabilities of the TextFieldTableCell to
 * allow for text formatting of the input data.
 *
 * @param <S> The type of data that is held in the table
 * @param <T> The type of data stored in the cell
 */
public class ValidatedTextFieldTableCell<S, T> extends TextFieldTableCell<S, T> {
    TextField tf;
    boolean cancel = true;
    String previousText = "";
    TableColumn column;
    TablePosition position;
    TableView view;
    private TextFormatter<String> formatter;        // the text formatter
    ObjectProperty<StringConverter<T>> converter; // the user-specified string converter


    /**
     * ValidatedTextFieldTableCell()
     * Constructor.
     *
     * @param operator - the filter operator to be used for this cell
     */
    public ValidatedTextFieldTableCell(UnaryOperator<TextFormatter.Change> operator) {
        // create the text formatter from the operator
        // internal member data
        // the filter operator
        this.formatter = new TextFormatter<>(operator);
        this.converter = new SimpleObjectProperty(this, "converter");
        // set the default converter (this gets used during commit)
        setConverter((StringConverter<T>) new DefaultStringConverter());
    }

    /**
     * Provide a callback function used by table's cell factory.
     *
     * @param operator - the filter operator for this cell
     * @param <S>      - the type of data stored in the table
     * @param <T>      - the type of data stored in the cell
     * @return - A callback function that provides a new instance of the cell
     */
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>>
    forTableColumn(UnaryOperator<TextFormatter.Change> operator) {
        return (cb) -> new ValidatedTextFieldTableCell(operator);
    }

    /**
     * getValue()
     * This is a special helper function that gets the value of the field from
     * the data model.
     *
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
     * setValue()
     * this is a special helper function that sets the changed value back to the
     * dta model.
     *
     * @param data  - the data model to write to
     * @param value - the new value to write
     */
    private void setValue(S data, String value) {
        Method fn;
        try {
            fn = data.getClass().getMethod("setValue", String.class);
            fn.invoke(data, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }
    }

    public void cancelEdit() {
        super.cancelEdit();
        setText(getValue((S) getTableRow().getItem()));
        if (cancel) {
            clickCommit(getConverter().fromString(tf.getText()));
            setValue((S) getTableRow().getItem(), tf.getText());
            cancel = false;
        }
        cancel = true;
    }

    @Override
    /** startEdit - because the TextFieldTableCell implementation does not support text
     * formatters, this applies one
     **/
    public void startEdit() {

        super.startEdit();
        // the text field should now have focus.  Use this fact
        // to access the text field.
        if (this.getScene().getFocusOwner() instanceof TextField) {
            if (formatter != null) ((TextField) this.getScene().getFocusOwner()).setTextFormatter(formatter);
            this.tf = (TextField) this.getScene().getFocusOwner();
            previousText = tf.getText();
            tf.addEventFilter(KeyEvent.KEY_PRESSED,
                    event ->
                    {
                        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                            this.cancel = false;
                            commitEdit(getConverter().fromString(tf.getText()));
                            setValue((S) getTableRow().getItem(), tf.getText());
                            if(getIndex() + 1 < getTableView().getItems().size()) {
                                getTableView().getSelectionModel().select(getIndex() + 1);
                                getTableView().getFocusModel().focus(getIndex() + 1, getTableColumn());
                            }
                            else
                            {
                                if (getTableView().getItems().size() > 0) {
                                    getTableView().getSelectionModel().select(0);
                                    getTableView().getFocusModel().focus(0, getTableColumn());
                                }
                            }

                            event.consume();
                        } else if (event.getCode() == KeyCode.ESCAPE) {
                            this.cancel = false;
                            commitEdit(getConverter().fromString(previousText));
                            setValue((S) getTableRow().getItem(), previousText);
                            cancelEdit();
                            event.consume();

                        } else {
                            column = this.getTableColumn();
                            position = this.getTableView().getEditingCell();
                            view = this.getTableView();
                        }
                    });

        }

    }

    public void clickCommit(T event) {
        if (view != null && column != null)
            ListView.EditEvent.fireEvent(column, new TableColumn.CellEditEvent(view, position, column.editCommitEvent(), event));
    }

}
