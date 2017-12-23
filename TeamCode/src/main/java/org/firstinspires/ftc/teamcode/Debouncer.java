package org.firstinspires.ftc.teamcode;

/**
 * Created by matt on 12/21/17.
 */

public class Debouncer {

    private boolean changed = false;
    private boolean sanitizedValue = false;

    public boolean debounce(boolean currentRawValue) {
        if (currentRawValue && !changed) {
            sanitizedValue = !sanitizedValue;
            changed = true;
        } else if (!currentRawValue) {
            changed = false;
        }

        return sanitizedValue;
    }
}
