/*
  This file is part of the ezGestures project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezGestures is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezGestures is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezGestures.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.gestures.preset;

/**
 * Holds the regex pattern for a clockwise twirl.
 */
/* $Id: CWTwirl.java 31 2008-11-12 16:01:48Z prisonerjohn $ */
public interface CWTwirl {
	public static final String CW_PATTERN = "^(?:RDLU(?:RDLU)*(?:RDL|RD|R)?|DLUR(?:DLUR)*(?:DLU|DL|D)?|LURD(?:LURD)*(?:LUR|LU|L)?|ULDR(?:ULDR)*(?:ULD|UL|U)?)$";
}
