/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gcewing.sg.gui.malisis.renderer.element.shape;

import gcewing.sg.gui.malisis.renderer.element.Face;
import gcewing.sg.gui.malisis.renderer.element.Shape;
import gcewing.sg.gui.malisis.renderer.element.face.BottomFace;
import gcewing.sg.gui.malisis.renderer.element.face.EastFace;
import gcewing.sg.gui.malisis.renderer.element.face.NorthFace;
import gcewing.sg.gui.malisis.renderer.element.face.SouthFace;
import gcewing.sg.gui.malisis.renderer.element.face.TopFace;
import gcewing.sg.gui.malisis.renderer.element.face.WestFace;

/**
 * Basic Cube {@link Shape} using predefined {@link Face faces}.
 *
 * @author Ordinastie
 *
 */
public class Cube extends Shape
{
	public Cube()
	{
		super(new NorthFace(), new SouthFace(), new EastFace(), new WestFace(), new TopFace(), new BottomFace());
		storeState();
	}
}
