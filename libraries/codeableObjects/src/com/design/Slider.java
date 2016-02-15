/*
 * Codeable Objects by Jennifer Jacobs is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * Based on a work at hero-worship.com/portfolio/codeable-objects.
 *
 * This file is part of the Codeable Objects Framework.
 *
 *     Codeable Objects is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codeable Objects is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codeable Objects.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.design;

import java.math.BigDecimal;

import com.math.Geom;

import processing.core.PApplet;
import processing.core.PFont;

public class Slider {

    private float x;
    private float y;
    private float width;
    private float height;
    private float value;
    private float minTarget;
    private float maxTarget;
    private boolean selected;
    private String name;
    private PApplet myParent;
    private PFont font;
    private String units;
    private double milToInchConversion=25.4;
    
    public Slider(PApplet myParent) {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        value = 0;
        minTarget = 0;
        maxTarget = 0;
        selected = false;
        value = (float) 0.5;
        this.myParent = myParent;
        font = myParent.loadFont("din_bold.vlw");
        myParent.textFont(font, 14);
    }

    public void init(float x, float y, float width, float height, float v, float minT, float maxT, String name, String units) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.value = myParent.map(v, minT, maxT, 0, 1);
        this.minTarget = minT;
        this.maxTarget = maxT;
        this.name = name;
        this.units= units;
    }

    public void draw() {
        myParent.noFill();
        myParent.strokeWeight(2);
        myParent.stroke(255, 255, 255);
        myParent.rect(x, y, width, height);
        if (selected) {


            myParent.fill(255, 255, 0);
        } else myParent.fill(255, 0, 0);
        myParent.noStroke();
        float sliderWidth = myParent.map(value, 0, 1, 0, width - 2);

        myParent.rect(x + 1, y + 1, sliderWidth, height - 2);

        myParent.fill(255);

        myParent.text(name, x, y + height + 15);
        myParent.fill(255, 255, 0);
        myParent.text(Double.toString(Geom.round(getSliderValue(),2,BigDecimal.ROUND_HALF_UP))+units, x+width+10, y+height/2+7);

    }

    public boolean checkForMousePress(float mouseX, float mouseY) {
        if (mouseX >= x && mouseX < x + width && mouseY > y && mouseY < y + height) {
            selected = true;
        } else selected = false;
        return selected;
    }

    public boolean checkForMouseDrag(float mouseX, float mouseY) {
        if (selected && mouseX >= x && mouseX < x + width) {
            value = myParent.map(mouseX, x, x + width, 0, 1);
            return true;
        }
        return false;
    }

    public float getSliderValue() {
        return myParent.map(value, 0, 1, minTarget, maxTarget);
    }

    public void setSliderValue(float val) {
        value = myParent.map(val, minTarget, maxTarget, 0, 1);
    }


}
