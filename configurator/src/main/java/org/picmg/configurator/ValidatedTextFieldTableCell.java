package org.picmg.configurator;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import java.util.function.UnaryOperator;

/**
 * this generic class extends the capabilities of the TextFieldTableCell to
 * allow for text formatting of the input data.
 * @param <S> The type of data that is held in the table
 * @param <T> The type of data stored in the cell
 *
 */
public class ValidatedTextFieldTableCell<S,T> extends TextFieldTableCell<S,T> {
    private TextFormatter<String> formatter;        // the text formatter

    /**
     * ValidatedTextFieldTableCell()
     * Constructor.
     * @param operator - the filter operator to be used for this cell
     */
    public ValidatedTextFieldTableCell(UnaryOperator<TextFormatter.Change> operator) {
        // create the text formatter from the operator
        // internal member data
        // the filter operator
        this.formatter = new TextFormatter<>(operator);

        // set the default converter (this gets used during commit)
        setConverter((StringConverter<T>) new DefaultStringConverter());
    }

    /**
     * Provide a callback function used by table's cell factory.
     * @param operator - the filter operator for this cell
     * @param <S> - the type of data stored in the table
     * @param <T> - the type of data stored in the cell
     * @return - A callback function that provides a new instance of the cell
     */
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>>
        forTableColumn(UnaryOperator<TextFormatter.Change> operator) {
            return (cb) -> new ValidatedTextFieldTableCell(operator);
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
            if (formatter!=null) ((TextField)this.getScene().getFocusOwner()).setTextFormatter(formatter);
        }
    }
}
