/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2012 Satoru Sugihara

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

import java.util.ArrayList;

/**
   3D vector filed defined by a NURBS curve.
   
   @author Satoru Sugihara
*/

public class IFieldAgent extends IAgent{
    public ArrayList<I3DFieldI> fields;
    public ArrayList<I2DFieldI> fields2;
    
    public IFieldAgent(){ fields = new ArrayList<I3DFieldI>(); fields2 = new ArrayList<I2DFieldI>(); }
    public IFieldAgent(I3DFieldI f){ this(); fields.add(f); }
    public IFieldAgent(I3DFieldI[] f){ this(); for(int i=0; i<f.length; i++) fields.add(f[i]); }
    
    public IFieldAgent add(I3DFieldI f){ fields.add(f); return this; }
    public IFieldAgent addField(I3DFieldI f){ return add(f); }
    public IFieldAgent remove(I3DFieldI f){ fields.remove(f); return this; }
    public IFieldAgent removeField(I3DFieldI f){ return remove(f); }

    public IFieldAgent add(I2DFieldI f){ fields2.add(f); return this; }
    public IFieldAgent addField(I2DFieldI f){ return add(f); }
    public IFieldAgent remove(I2DFieldI f){ fields2.remove(f); return this; }
    public IFieldAgent removeField(I2DFieldI f){ return remove(f); }
    
    public void interact(ArrayList<IDynamics> agents){
	for(int i=0; i<agents.size(); i++){
	    if(agents.get(i) instanceof IParticleI){
		IParticleI p = (IParticleI)agents.get(i);
		for(int j=0; j<fields.size(); j++){
		    p.push(fields.get(j).get(p.pos()));
		}
		for(int j=0; j<fields2.size(); j++){
		    p.push(fields2.get(j).get(p.pos()).to3d());
		}
	    }
	}
    }
    
    
}
