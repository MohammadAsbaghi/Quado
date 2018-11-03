package ir.help7.quado.models;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

/**
 * Created by phpci on 5/22/2018.
 */

public class Cube extends View{
    private int color;
    private Point point;
    private int width;


    public Cube(Context context, int color, Point point) {
        super(context);
        this.color = color;
        this.point = point;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int get_Width() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
