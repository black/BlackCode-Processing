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
   A subclass of IMap defined by flipping another map in u direction.
   
   @author Satoru Sugihara
*/
public class IUFlipMap extends IMap{
    public IMap map;
    public IUFlipMap(IMap m){ map = m; }
    public double get(double u, double v){ return map.get(1-u,v); }
    public void flipU(){ map.flipU(); } //is it ok to modify the original?
    public void flipV(){ map.flipV(); } //is it ok to modify the original?
}
