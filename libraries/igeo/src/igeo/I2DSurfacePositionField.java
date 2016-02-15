/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2013 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo;

/**
   2D vector filed defined by a NURBS surface position (vector from the origin).
   
   @author Satoru Sugihara
*/

public class I2DSurfacePositionField extends I2DField{
    public I2DSurfacePositionField(ISurfaceI srf){ super(new I2DSurfacePositionFieldGeo(srf)); }
    static public class I2DSurfacePositionFieldGeo extends I2DSurfaceFieldGeo{
	public I2DSurfacePositionFieldGeo(ISurfaceI srf){ super(srf,srf); }
    }
    public I2DSurfacePositionField noDecay(){ super.noDecay(); return this; }
    public I2DSurfacePositionField linearDecay(double threshold){ super.linearDecay(threshold); return this; }
    public I2DSurfacePositionField linear(double threshold){ super.linear(threshold); return this; }
    public I2DSurfacePositionField gaussianDecay(double threshold){ super.gaussianDecay(threshold); return this; }
    public I2DSurfacePositionField gaussian(double threshold){ super.gaussian(threshold); return this; }
    public I2DSurfacePositionField gauss(double threshold){ super.gauss(threshold); return this; }
    public I2DSurfacePositionField decay(IDecay decay, double threshold){ super.decay(decay,threshold); return this; }
    public I2DSurfacePositionField constantIntensity(boolean b){ super.constantIntensity(b); return this; }
    /** if bidirectional is on, field force vector is flipped when velocity of particle is going opposite */
    public I2DSurfacePositionField bidirectional(boolean b){ super.bidirectional(b); return this; }
    
    public I2DSurfacePositionField threshold(double t){ super.threshold(t); return this; }
    public I2DSurfacePositionField intensity(double i){ super.intensity(i); return this; }
}
