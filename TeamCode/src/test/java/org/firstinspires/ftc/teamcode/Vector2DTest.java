package org.firstinspires.ftc.teamcode;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by noah on 11/18/17.
 */
public class Vector2DTest {
    static final double VECTOR_ACCEPTABLE_EPSILON = 0.001;

    @Test
    public void getXComponent() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        assertEquals(3.42, vec.GetXComponent(), 0);
    }

    @Test
    public void getYComponent() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        assertEquals(7.61, vec.GetYComponent(), 0);
    }

    @Test
    public void getMagnitude() throws Exception {
        Vector2D vec = new Vector2D(Math.sqrt(2), Math.sqrt(2));
        assertEquals(2, vec.GetMagnitude(), VECTOR_ACCEPTABLE_EPSILON);
    }

    @Test
    public void getDirection() throws Exception {
        Vector2D vec = new Vector2D(Math.sqrt(2), Math.sqrt(2));
        assertEquals(45, vec.GetDirection(), VECTOR_ACCEPTABLE_EPSILON);
    }

    @Test
    public void set() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        vec.Set(new Vector2D(1, 1));
        assertEquals(1, vec.GetXComponent(), 0);
    }

    @Test
    public void setPolar() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        vec.SetPolar(2, 45);
        assertEquals(Math.sqrt(2), vec.GetXComponent(), VECTOR_ACCEPTABLE_EPSILON);
    }

    @Test
    public void setComponents() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        vec.SetComponents(2, 2);
        assertEquals(2, vec.GetXComponent(), 0);
    }

    @Test
    public void add() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        Vector2D vecSecond = new Vector2D(1, 1);
        vec.Add(vecSecond);
        assertEquals(4.42, vec.GetXComponent(), 0);
    }

    @Test
    public void addPolar() throws Exception {
        Vector2D vec = new Vector2D(Math.sqrt(2), Math.sqrt(2));
        vec.AddPolar(2, 180);
        assertEquals(112.5, vec.GetDirection(), VECTOR_ACCEPTABLE_EPSILON);
    }

    @Test
    public void addComponents() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        vec.AddComponents(2, 2);
        assertEquals(5.42, vec.GetXComponent(), 0);
    }

    @Test
    public void dotProduct() throws Exception {
        Vector2D vec = new Vector2D(2, 2);
        Vector2D vecSecond = new Vector2D(3, 3);
        assertEquals(vec.DotProduct(vecSecond), 12, 0);
    }

    @Test
    public void multiplyScalar() throws Exception {
        Vector2D vec = new Vector2D(3.42, 7.61);
        vec.MultiplyScalar(2);
        assertEquals(6.84, vec.GetXComponent(), 0);
    }

    @Test
    public void rotate() throws Exception {
        Vector2D vec = new Vector2D(Math.sqrt(2), Math.sqrt(2));
        vec.Rotate(100);
        assertEquals(145, vec.GetDirection(), VECTOR_ACCEPTABLE_EPSILON);
    }

    @Test
    public void flipVertically() throws Exception {
        Vector2D vec = new Vector2D(1, 1);
        vec.FlipVertically();
        assertEquals(-1, vec.GetYComponent(), 0);
    }

    @Test
    public void flipHorizontally() throws Exception {
        Vector2D vec = new Vector2D(1, 1);
        vec.FlipHorizontally();
        assertEquals(-1, vec.GetXComponent(), 0);
    }

}