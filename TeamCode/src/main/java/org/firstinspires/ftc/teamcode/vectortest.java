package org.firstinspires.ftc.teamcode;

/**
 * Created by matt on 10/14/17.
 */

public class vectortest {
    public void main() {
        Vector2D v = new Vector2D(1, 0);
        v.SetPolar(1, 0);
        System.out.println(v.GetXComponent() + "  " +  v.GetYComponent());
    }
}
