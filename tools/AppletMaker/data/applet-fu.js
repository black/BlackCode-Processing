/*
 * Applet-Fu A Javascript library for cross-browser applet deployment.
 *
 * Copyright (c) 2008, Metamolecular, LLC
 * All rights reserved.
 *
 * http://github.com/rapodaca/applet-fu/tree/master
 * info@metamolecular.com
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of Metamolecular, LLC nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var applet_fu = {
  run: function(attributes, parameters, minimumVersion, fallbackContent){    
    if (applet_fu.isIE()){
      applet_fu.openTagIE(attributes)
    }
    else{
      applet_fu.openTagStandard(minimumVersion || '1.4.2');
    }
    
    applet_fu.writeAttributes(attributes);
    applet_fu.writeParameters(parameters);
    document.write(fallbackContent || 'This content requires Java.');
    document.write('</object>');
  },
  
  /*
   * Creates the opening object tag for standards-compliant browsers. Requests the minumum JRE version
   * specified with minimumVersion.
   */
  openTagStandard: function(minimumVersion){
    document.write('<object type="application/x-java-applet;version=' + minimumVersion + '"');
  },
  
  /*
   * Creates the opening object tag for IE using classid and codebase attributes.
   * If these attributes are not set, EasyApplet will request that the applet be run
   * with the latest installed JRE, or JDK 1.4, whichever is higher. For more information, see:
   *
   * http://java.sun.com/javase/6/webnotes/family-clsid.html
   * http://java.sun.com/javase/6/docs/technotes/guides/deployment/deployment-guide/autodl-files.html
   */
  openTagIE: function(attributes){
    var classid = attributes['classid'] || "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"; //use latest installed JRE; 
    var codebase = attributes['codebase'] || "http://java.sun.com/products/plugin/autodl/jinstall-1_4-windows-i586.cab#Version=1,4,0,0"; //download JRE 1.4 if suitable JRE not found;
    
    document.write('<object classid="' + classid + '" codebase=' + codebase + '"');
  },
  
  writeAttributes: function(attributes){
    for (attribute in attributes){
      document.write(" ");
      document.write(attribute + "='" + attributes[attribute] + "'");
    }
    
    document.write('>');
  },
  
  writeParameters: function(parameters){
    for (parameter in parameters){
      document.write("<param name=" + parameter + " value='" + parameters[parameter] + "'>");
    }
  },
  
  isIE: function(){
    if (navigator == null) return false;
    
    return navigator.userAgent.match(/MSIE/) != null;
  }
}
