package Backend.ms_clasificator.Models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;

public enum PrimitiveType {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    DATE,
    DATETIME,
    TIME,
    BINARY,
    TEXT;

    public void validate(String value) {
        if (value == null || value.isBlank()) return;

        try {
            switch (this) {
                case INTEGER  -> Integer.parseInt(value);
                case DOUBLE   -> Double.parseDouble(value);
                case BOOLEAN  -> {
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false"))
                        throw new IllegalArgumentException();
                }
                case DATE     -> LocalDate.parse(value);
                case DATETIME -> LocalDateTime.parse(value);
                case TIME     -> LocalTime.parse(value);
                case BINARY   -> Base64.getDecoder().decode(value);
                case STRING, TEXT -> { }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "El valor '" + value + "' no es compatible con el tipo " + this.name()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "El valor '" + value + "' no es compatible con el tipo " + this.name()
            );
        }
    }
}
