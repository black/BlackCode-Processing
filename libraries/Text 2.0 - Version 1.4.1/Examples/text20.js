/*
 * text20.js
 *
 * Copyright (c) 2010, Ralf Biedert, German Research Center For
 * Artificial Intelligence.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 *
 * Version:
 *      4.6.0
 *
 * Dependencies:
 *      jquery > 1.4
 *
 */

(function(window, undefined) {


var text20 = {},
    /** Version information to assist debugging */
    version = {
        version: "1.4.1",
        build: "1.4.1-201109061609",
    },

    strings = {
        /** Segment a string into chunks of strings based on punctiation. */
        segment: function(text) {
            var rval = [],
                w = "",
                nw = ""

            // We process the string character for character ...
            for(var i=0; i<text.length; i++) {
                // And classify them as word (w) / non-word (nw) characters 
                var c = text.charAt(i);
                if(c == '.' || c == ' ' || c == '\n' || c == '\t' || c == '!' || c == '#'          // FIXME: Beautify me into array
                    || c == '\'' || c == '"' || c == '?' || c == '-' || c == ';' || c == ','
                    || c == '+' || c == '&' || c == ':' || c == '(' || c == ')' || c == '['
                    || c == ']' || c == '{' || c == '}' || c == "%" || c == '—' || c == '”') {
                    nw += c;
                    if(w.length > 0) {
                        rval.push(w);
                        w = "";
                    }
                } else {
                    w += c;
                    if(nw.length > 0) {
                        nw.isNW = true;
                        rval.push(nw);
                        nw = "";
                    }
                }
            }

            // If there are remainders, add them as well
            if(w.length > 0) {
                rval.push(w);
                w = "";
            }

            if(nw.length > 0) {
                nw.isNW = true;
                rval.push(nw);
                nw = "";
            }

            // Return result
            return rval;
        },

        /** Functions for encoding and decoding base64. Shamelessly stolen from http://www.webtoolkit.info/javascript-base64.html */
        base64: {
            // private property
            _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

            /** public method for encoding */
            encode : function (input) {
                var output = "";
                var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
                var i = 0;

                input = this._utf8_encode(input);

                while (i < input.length) {

                    chr1 = input.charCodeAt(i++);
                    chr2 = input.charCodeAt(i++);
                    chr3 = input.charCodeAt(i++);

                    enc1 = chr1 >> 2;
                    enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                    enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                    enc4 = chr3 & 63;

                    if (isNaN(chr2)) {
                        enc3 = enc4 = 64;
                    } else if (isNaN(chr3)) {
                        enc4 = 64;
                    }

                    output = output +
                    this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                    this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

                }

                return output;
            },

            /** public method for decoding */
            decode : function (input) {
                var output = "";
                var chr1, chr2, chr3;
                var enc1, enc2, enc3, enc4;
                var i = 0;

                input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

                while (i < input.length) {

                    enc1 = this._keyStr.indexOf(input.charAt(i++));
                    enc2 = this._keyStr.indexOf(input.charAt(i++));
                    enc3 = this._keyStr.indexOf(input.charAt(i++));
                    enc4 = this._keyStr.indexOf(input.charAt(i++));

                    chr1 = (enc1 << 2) | (enc2 >> 4);
                    chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                    chr3 = ((enc3 & 3) << 6) | enc4;

                    output = output + String.fromCharCode(chr1);

                    if (enc3 != 64) {
                        output = output + String.fromCharCode(chr2);
                    }
                    if (enc4 != 64) {
                        output = output + String.fromCharCode(chr3);
                    }

                }

                output = this._utf8_decode(output);

                return output;

            },

            // private method for UTF-8 encoding
            _utf8_encode : function (string) {
                string = string.replace(/\r\n/g,"\n");
                var utftext = "";

                for (var n = 0; n < string.length; n++) {

                    var c = string.charCodeAt(n);

                    if (c < 128) {
                        utftext += String.fromCharCode(c);
                    }
                    else if((c > 127) && (c < 2048)) {
                        utftext += String.fromCharCode((c >> 6) | 192);
                        utftext += String.fromCharCode((c & 63) | 128);
                    }
                    else {
                        utftext += String.fromCharCode((c >> 12) | 224);
                        utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                        utftext += String.fromCharCode((c & 63) | 128);
                    }

                }

                return utftext;
            },

            // private method for UTF-8 decoding
            _utf8_decode : function (utftext) {
                var string = "";
                var i = 0;
                var c = c1 = c2 = 0;

                while ( i < utftext.length ) {

                    c = utftext.charCodeAt(i);

                    if (c < 128) {
                        string += String.fromCharCode(c);
                        i++;
                    }
                    else if((c > 191) && (c < 224)) {
                        c2 = utftext.charCodeAt(i+1);
                        string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                        i += 2;
                    }
                    else {
                        c2 = utftext.charCodeAt(i+1);
                        c3 = utftext.charCodeAt(i+2);
                        string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                        i += 3;
                    }

                }
                return string;
            }
        },
        
        /** Computes a crc32. Shamelessly stolen from http://www.webtoolkit.info/javascript-base64.html */
        crc32: function (str) {
            function Utf8Encode(string) {
                string = string.replace(/\r\n/g,"\n");
                var utftext = "";
         
                for (var n = 0; n < string.length; n++) {
         
                    var c = string.charCodeAt(n);
         
                    if (c < 128) {
                        utftext += String.fromCharCode(c);
                    }
                    else if((c > 127) && (c < 2048)) {
                        utftext += String.fromCharCode((c >> 6) | 192);
                        utftext += String.fromCharCode((c & 63) | 128);
                    }
                    else {
                        utftext += String.fromCharCode((c >> 12) | 224);
                        utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                        utftext += String.fromCharCode((c & 63) | 128);
                    }
         
                }
         
                return utftext;
            };
         
            if(!str) return ""
            str = Utf8Encode(str);
         
            var table = "00000000 77073096 EE0E612C 990951BA 076DC419 706AF48F E963A535 9E6495A3 0EDB8832 79DCB8A4 E0D5E91E 97D2D988 09B64C2B 7EB17CBD E7B82D07 90BF1D91 1DB71064 6AB020F2 F3B97148 84BE41DE 1ADAD47D 6DDDE4EB F4D4B551 83D385C7 136C9856 646BA8C0 FD62F97A 8A65C9EC 14015C4F 63066CD9 FA0F3D63 8D080DF5 3B6E20C8 4C69105E D56041E4 A2677172 3C03E4D1 4B04D447 D20D85FD A50AB56B 35B5A8FA 42B2986C DBBBC9D6 ACBCF940 32D86CE3 45DF5C75 DCD60DCF ABD13D59 26D930AC 51DE003A C8D75180 BFD06116 21B4F4B5 56B3C423 CFBA9599 B8BDA50F 2802B89E 5F058808 C60CD9B2 B10BE924 2F6F7C87 58684C11 C1611DAB B6662D3D 76DC4190 01DB7106 98D220BC EFD5102A 71B18589 06B6B51F 9FBFE4A5 E8B8D433 7807C9A2 0F00F934 9609A88E E10E9818 7F6A0DBB 086D3D2D 91646C97 E6635C01 6B6B51F4 1C6C6162 856530D8 F262004E 6C0695ED 1B01A57B 8208F4C1 F50FC457 65B0D9C6 12B7E950 8BBEB8EA FCB9887C 62DD1DDF 15DA2D49 8CD37CF3 FBD44C65 4DB26158 3AB551CE A3BC0074 D4BB30E2 4ADFA541 3DD895D7 A4D1C46D D3D6F4FB 4369E96A 346ED9FC AD678846 DA60B8D0 44042D73 33031DE5 AA0A4C5F DD0D7CC9 5005713C 270241AA BE0B1010 C90C2086 5768B525 206F85B3 B966D409 CE61E49F 5EDEF90E 29D9C998 B0D09822 C7D7A8B4 59B33D17 2EB40D81 B7BD5C3B C0BA6CAD EDB88320 9ABFB3B6 03B6E20C 74B1D29A EAD54739 9DD277AF 04DB2615 73DC1683 E3630B12 94643B84 0D6D6A3E 7A6A5AA8 E40ECF0B 9309FF9D 0A00AE27 7D079EB1 F00F9344 8708A3D2 1E01F268 6906C2FE F762575D 806567CB 196C3671 6E6B06E7 FED41B76 89D32BE0 10DA7A5A 67DD4ACC F9B9DF6F 8EBEEFF9 17B7BE43 60B08ED5 D6D6A3E8 A1D1937E 38D8C2C4 4FDFF252 D1BB67F1 A6BC5767 3FB506DD 48B2364B D80D2BDA AF0A1B4C 36034AF6 41047A60 DF60EFC3 A867DF55 316E8EEF 4669BE79 CB61B38C BC66831A 256FD2A0 5268E236 CC0C7795 BB0B4703 220216B9 5505262F C5BA3BBE B2BD0B28 2BB45A92 5CB36A04 C2D7FFA7 B5D0CF31 2CD99E8B 5BDEAE1D 9B64C2B0 EC63F226 756AA39C 026D930A 9C0906A9 EB0E363F 72076785 05005713 95BF4A82 E2B87A14 7BB12BAE 0CB61B38 92D28E9B E5D5BE0D 7CDCEFB7 0BDBDF21 86D3D2D4 F1D4E242 68DDB3F8 1FDA836E 81BE16CD F6B9265B 6FB077E1 18B74777 88085AE6 FF0F6A70 66063BCA 11010B5C 8F659EFF F862AE69 616BFFD3 166CCF45 A00AE278 D70DD2EE 4E048354 3903B3C2 A7672661 D06016F7 4969474D 3E6E77DB AED16A4A D9D65ADC 40DF0B66 37D83BF0 A9BCAE53 DEBB9EC5 47B2CF7F 30B5FFE9 BDBDF21C CABAC28A 53B39330 24B4A3A6 BAD03605 CDD70693 54DE5729 23D967BF B3667A2E C4614AB8 5D681B02 2A6F2B94 B40BBE37 C30C8EA1 5A05DF1B 2D02EF8D";

            var crc = 0;          
            var x = 0;
            var y = 0;
         
            crc = crc ^ (-1);
            for( var i = 0, iTop = str.length; i < iTop; i++ ) {
                y = ( crc ^ str.charCodeAt( i ) ) & 0xFF;
                x = "0x" + table.substr( y * 9, 8 );
                crc = ( crc >>> 8 ) ^ x;
            }
         
            return crc ^ (-1);
        }
    },



    math = {
        /** Generate a float random number between min and max. */
        random: function(min, max) {
            return (min + parseInt(Math.random() * (max - min + 1)));
        }
    },


    listener = {
        /** Creates a listener manager */
        manager: function() {
            var mgr = {},
                listener = {}

            /** Add a new listener to the given channel */
            mgr.add = function(channel, fnc){
                // Create channel if it's not there
                if (!listener[channel]) { listener[channel] = [] }

                listener[channel].push(fnc);
            }

            /** Get listeners for the given chanel */
            mgr.get = function(channel){
                if (!listener[channel]) { return []; }

                return listener[channel];
            }

            /** Process all listeners for the given channel */
            mgr.process = function(channel, fct){
                this.get(channel).forEach(fct);
            }

            // Return the manager
            return mgr;
        },
    },



    callbacks = {
        /** Returns the currently used prefix */
        prefix: function() {
            return "xclback2482"
        },

        /** Returns the wrapped name for a method name */
        name: function(suffix) {
            return this.prefix() + suffix;
        },

        /** Returns the function object */
        func: function(name) {
           return eval(this.name(name));
        },

        /** Registes a given function globally */
        register: function(name, fnc) {
            eval("window." + this.name(name) + " = fnc;");
        }
    },



    file = {
        /** Returns the absolute path to a relative path */
        absolutePath: function(relativePath) {
            return location.href.substring(0, location.href.lastIndexOf('/') + 1 ) + relativePath;
        }
    },



    cache = {
        /** Creates a new cache. */
        cache: function() {
            var cache = {},
                data = {};

            /** Creates an element for the given id */
            cache.create = function(id){
                var cacheElement = new Object();
                data[id] = cacheElement;
                return data[id];
            }

            /** Returns an element for the given ID */
            cache.get = function(id){
                return data[id];
            }


            /** Removes an for the given ID */
            cache.remove = function(id){
                delete data[id];
            }

            // Return created cache.
            return cache
        }
    },


    system = {
        /** Returns the operating system */
        os: function() {
            var OSName = null;
            if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows";
            if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS";
            if (navigator.appVersion.indexOf("X11")!=-1) OSName="Unix";
            if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux";
            return OSName;
        }
    },



    browser = {
        /** Use this to manually override the offset */
        overrideOffset: null,

        /** Internally used for calibrated offsets */
        calibratedOffset: null,

        /** Returns a unique ID for this browser */
        id: function() {
            return strings.base64.encode((navigator.appVersion))
        },

        /** Returns the offset of the document from the window's screen area */
        documentOffset: function() {
            // If an offset is present, use it
            if(this.overrideOffset)
                return this.overrideOffset;

            // If something has been calibrated, use that
            if(this.calibratedOffset)
                return this.calibratedOffset
                
            return [0, 0]
        },
        
        /**
         * Logs a given string to the console, if possible 
         * 
         * @param {Object} message
         */
        log: function(message) {
            if(console && console.log) console.log(message)   
        }
    },



    dom = {
        /** Creates an unique id */
        id: function() {
            if(!this.lastID) this.lastID = 0
            return "t20id" + this.lastID++;
        },

        /**
         * Ensures the element has an ID. If it has, the ID is returned. If it doesn't,
         * a new ID is created and it is returned.
         *
         * @param {Object} element to IDify
         * @param {Object} The id to assign if none was found (optional) 
         *
         * @returns: The ID created
         */
        ensureID: function(element, id) {
            var e = $(element);

            // If it has an ID return that ...
            if(e.attr("id")) return e.attr("id");

            // ... else, add autoID
            var id = id || dom.id();
            e.attr("id", id);
            e.addClass("autoID");
            return id;
        },


        /** Finds elements in a region */
        elementsAround: function(x, y, xradius, yradius) {
            var rval = []
            
            if(x <= 0 || y <= 0) return rval;

            // FIXME: Better alternative to 'shooting'?
            for(var ypos = y - yradius; ypos < y + yradius; ypos += 10) {
                for(var xpos = x - xradius; xpos < x + xradius; xpos += 10) {

                    // FIXME: This is broken? (Safari 5 uses coordinats differently?)
                    var element = document.elementFromPoint(xpos, ypos);

                    if(!element) continue;
                    if(rval.indexOf(element) >= 0) continue;

                    rval.push(element);
                }
            }

            return rval;
        },


        /** Returns all parents above a given element, including the element */
        parents: function(element) {
            var rval = []

            if(element==null) return rval;
            rval.push(element)

            while(element.parentNode){
                rval.push(element.parentNode)
                element = element.parentNode
            }

            return rval
        },


        /**
         * Finds the position of an element relative to the web page
         *
         * @param {Object} obj
         *
         * @returns: The position as an [x, y, relativeTo] array.
         */
        documentPosition: function(obj){
            if(obj == null) return null;
            
            // Original Object
            var origObj = obj;

            // Will be set below, position of the element relative to document start
            var posX = obj.offsetLeft;
            var posY = obj.offsetTop;

            var relativeTo = "document";

            var scrollCorrectionY = 0;

            // Check all parent nodes if element is fixed to window or normal inside the document ...
            while(obj.parentNode){
                    var type = window.getComputedStyle(obj,"").getPropertyValue("position");
                    if(type=="fixed") {
                        relativeTo = "window"
                    }

                    // Stop on BODY node
                    if(obj.parentNode.nodeName == "BODY") break;
                    
                    // Subtract scrolling of parent (some divs might have this ... BUT IGNORE THE BODY)
                    if (obj.parentNode.scrollTop > 0)
                        scrollCorrectionY -= obj.parentNode.scrollTop;

                    obj = obj.parentNode;
            }
            
            // Restore original object
            obj = origObj;

            // Obtain the screen position ...
            while(obj.offsetParent){
                posX = posX + obj.offsetParent.offsetLeft;
                posY = posY + obj.offsetParent.offsetTop;

                obj = obj.offsetParent;
            }

            if(!posX) return null;
            if(!posY) return null;

            // Correct scrolling
            posY += scrollCorrectionY;

            return [posX, posY, relativeTo];
        },

        /**
         * Returns all text nodes below a given element
         *
         * @param {Object} startNode
         *
         * @returns: All text nodes
         */
         textnodesBelow: function(startNode) {
            if(!startNode) return []

            // TODO: Replace this with powerful selector?
            var rval = [],
                length = startNode.childNodes.length;

            for(var i=0; i<length; i++) {
                var child = startNode.childNodes[i];

                // For every node, either call this function again, or
                // put the text node into the rval
                if(child.nodeName == "#text") {
                    // If we don't do these checks, spans will be included into Tables, resulting in a messed rendering ...
                    if(child.data.length > 0
                        && startNode.nodeName != "TR"
                        && startNode.nodeName != "TABLE"
                        && startNode.nodeName != "HEAD"
                    ) {
                        rval.push(child);
                    }
                }

                // Recursively descend ...
                if(child.childNodes.length > 0) {
                    var subcall = dom.textnodesBelow(child);
                    rval = rval.concat(subcall);
                }
            }

            // Return merged results
            return rval;
        },


        /**
         * Split text nodes into words and make spans out of them
         * 
         * TODO: Change callback behavior in the future: Only act on 
         * strings, not on the actual elements.
         * 
         * @param arrayOfTextnodes The text nodes to spanify. 
         * @param options 
         *      options.callback: Function accepting one parameter, will be called for every span
         *      options.idfunction: If set, will be called with each element to ensure it has an id.  
         *      options.textid: If set, the text id to use.  
         *
         * @returns: Nothing
         */
        spanify: function(arrayOfTextnodes, options){
            if(!this.lastText) this.lastText = 0
            
            // Check if we have an options object
            if (!options) options = {}

            var allTokens = [],
                textID = options.textid || this.lastText++,    // Uniquely assign a text id
                wordID = 0,                  // And prepare word IDs
                eei = options.idfunction || dom.ensureID;

            // Process every text node we got
            arrayOfTextnodes.forEach(function(node){
                var parentNode = node.parentNode;
                var container = document.createElement("span");

                // If parent node already has proper attribute, don't do anything
                if (parentNode.hasAttribute("_markedText"))
                    return;

                // Replace the given node with one of our span containers
                parentNode.replaceChild(container, node);

                // Split this text node
                var allText = node.data,
                    words = strings.segment(allText);

                // Process every single word
                words.forEach(function(token){
                    // In case this this non-word, just add it.
                    if (token.isNW) {
                        container.appendChild(document.createTextNode(token));
                    }

                    // In case this is a proper word, spanify it
                    else {

                        // Create textnode and span for every word
                        var text = document.createTextNode(token),
                            newSpan = document.createElement("span");

                        newSpan.setAttribute("_markedText", "true");
                        newSpan.setAttribute("_textID", textID);
                        newSpan.setAttribute("_wordID", wordID);

                        // The words have an ID
                        var id = eei(newSpan, "span" + textID + "x" + wordID++);

                        // Append them
                        newSpan.appendChild(text);
                        container.appendChild(newSpan);

                        // Callback the listener if we have one
                        if(options.callback) options.callback(newSpan, id, token);

                        // Store child
                        allTokens.push(newSpan);
                    }
                });
            });

            return allTokens;
        },
    },



    connector = {
        config: {
            /** Archive to use. If you place the .jar to another directory you
             *  have to change this path. */
            archive: "text20.jar",

            /** Should we present a load indicator? (Does not really work at
             *  the moment) */
            loadIndicator : false,

            /** Should we warn if the browser was not calibrated. Best to
             *  keep this enabled! */
            warnIfNotCalibrated : true,

            /** Eye tracking devices and locations. */
            trackingDevice: "eyetrackingdevice:auto",
            trackingURL : "discover://nearest",

            /** Brain tracking device enabled. Best keep this off, as the code
             * is very experimental. */
            enableBrainTracker: false,
            brainTrackingURL: "discover://any",

            /** Performance. If you want high volume listeners (that act on
             *  raw data, like the head position), then you have to set this
             *  to true. Otherwise keep it for for performance reasons */
            registerHighVolumeListeners: false,

            /** List of 3rd party extensions to load. See the documentation
             *  for more info on how to write extensions. */
            extensions : [],

            /** Specifies if, how and where logging and recording should be
             *  performed. Logging means application debug output, recording
             *  means generating a session replay file. Please note that for a
             *  proper replay elements also have to be register()ed first. */
            recordingEnabled : false,
            sessionPath : "/tmp/sessions",
            logging : "default",
            diagnosis: true,

            /** If the plugin should check for new versions (see Java console
             *  output). Sends an anonymous ID for statistical purposes, but no
             *  personal information whatsoever. */
            updateCheck : true,

            /** Internal variables. */
            transmitMode : "ASYNC",    // DO NOT TOUCH THIS

            useObjectTag : true,
        },

        variables: {
            /** Listeners for all our channels */
            listeners: listener.manager(),
        },


        /** Will be set upon core.init() */
        connection: null,


        extensions: {

            //
            // Extension will be added in here dynamically
            //

            registry: {
                /** All extension listeners */
                listeners: listener.manager(),

                /** Wrapped call to a registered extension */
                wrapper: function(name, args) {

                    var first = true,
                        cmd = name + "(",
                        i = 0;

                    // Assemble call ...
                    for(i = 0; i<args.length; i++) {
                        var elem = args[i]
                        // split arguments
                        if(!first) { cmd += "," }
                        else { first = false }

                        cmd += "'" + encodeURIComponent(elem) + "'"

                    }
                    cmd += ");"

                    // Call and store the return value
                    var rval = connector.connection.callGeneric(cmd);

                    // Convert variables. Some Java/JavaScript implementations don't do it properly ...
                    if (rval) {
                        // Fix strings ...
                        if(rval.getClass && rval.getClass().getName() == "java.lang.String") {
                            // Check again that we have a to string method
                            if(rval.toString) {
                                rval = "" + rval.toString();
                            } else {
                                rval = "" + rval;
                            }
                        }
                    }

                    return rval;
                },

                /** Registers a special name for latter recognition */
                register: function(name) {
                    eval("text20.connector.extensions." + name + " = function() {"
                        +      "return text20.connector.extensions.registry.wrapper('" + name + "', arguments);"
                        + "}");
                },

                /** Adds a listener for the given extension callback channel */
                listener: function(channel, l) {
                    this.listeners.add(channel, l);
                },

                /** Called back by the connector and dispatches calls to our registered elements */
                callback: function(name, args) {
                    var l = this.listeners.get(name),
                        opts = "";


                    // Assemble arguments
                    for(var i=0; i<args.length; i++) {
                        opts += ",args[" + i + "]"
                    }
                    
                    // Remove first comma
                    if(opts.length > 0) { opts = opts.substr(1) }

                    // Call all listener
                    l.forEach(function f(elem) {
                        eval("elem(" + opts + ");")
                    });
                }
            },
        },


        /** Adds a listener to a given channel */
        listener: function(channel, fct){
            var supportedChannels = ["INITIALIZED", "reducedApplicationGaze", "fixation", "specialCallback", "perusal", "headPosition", "weakSaccade"];

            // Safety check
            if (supportedChannels.indexOf(channel) < 0) {
                alert("ERROR. Trying to register to unknown channel " + channel);
                return;
            }

            // Finally register
            this.variables.listeners.add(channel, fct);
        },


        /** How this connector can connect */
        methods: {
            dummy: {
                /** All of these methods should be considered semi private, thre should not be any need
                    to call them from outside */
                connect: function() {},
                registerCallback: function(name, suffix) {},
                callGeneric: function(cmd){},
                connect: function() {},
                transmitWindowVisibility: function(isVisible) {},
                transmitElementRemoved: function(id) {},
                transmitElementPositionAnchor: function(id, anchor) {},
                transmitBrowserGeometry: function(x, y, w, h) {},
                preference: function(key, value) {},
                transmitElement: function(id, type, content, x, y, w, h) {},
                transmitViewport: function(x, y) {},
                transmitElementMetaInformation: function(id, key, value) { },
                setSessionParameter: function(key, value){},
                dropBrowserCalibration: function() {},
            },

            applet: {
                /** Locally used variables */
                variables: {
                    initialized: false,                 // True if the connection has been initialized
                    engine: null,                       // Engine
                    transmitCache: cache.cache(),       // Cache to check if we need to retransmit
                    appletID: "m8doaaas33a",            // ID of our applet
                    offset: null,                       // Browser offset to use
                    batch: null,                        // Batch call for bulk transmission
                    loadIndicator: this.loadIndicator,  // TODO: What does this do?
                    times: {
                        jsinit: new Date().getTime(),
                        appletadded: false,
                        appletinitialized: false,
                        callinginit: false,
                        finishedinit: false,                        
                    }
                },

                /** Creates a new batch for submission to the applet */
                batch: function() {

                    var batch = {},
                        browserFlag = [],
                        elementGeometry = [],
                        elementMeta = []

                    /** Update an element flag */
                    batch.updateElementFlag = function(id, type, flag){
                        browserFlag.push({ id: id, type: type, flag: flag});
                    }

                    /** Update an element meta info */
                    batch.updateElementMeta = function(id, key, value){
                        elementMeta.push({ id: id, key: key, value: value});
                    }

                    /** Update an element geometry info */
                    batch.updateElementGeometry = function(id, type, content, x, y, w, h){
                        elementGeometry.push({id:id, type:type, content:content, x:x, y:y, w:w, h:h});
                    }

                    /** Generate a batch call */
                    batch.generateBatchCalls = function(){
                        var rval = []

                        // TODO: Generalize calls with single method

                        var assembler = function(prefix, array, keyset) {

                            // Construct proper prefix
                            var str = prefix + "(",
                                opts = ""

                            array.forEach(function(e){
                                // Iterate over keyset (we have to use a counter
                                // because the substr() version was slow as hell
                                for(var i=0; i<keyset.length; i++) {
                                    var f = keyset[i]
                                    var v = e[f] == null ? ".null" : e[f]

                                    if(f == "type" || f == "content") {
                                        v = encodeURIComponent(v)
                                    }

                                    // Concat elements with ',', last one with ';'.
                                    if(i < keyset.length - 1)
                                        opts += v + ","
                                    else
                                        opts += v + ";"
                                }
                            })

                            // Strip last semicolon
                            if (opts.length > 0)
                                opts = opts.substr(0, opts.length - 1);

                            str += opts + ")";
                            rval.push(str)
                        }

                        // Element flags
                        if (browserFlag && browserFlag.length > 0) {
                            assembler("updateElementFlag", browserFlag, ["id", "type", "flag"])
                        }


                        // Element meta
                        if (elementMeta && elementMeta.length > 0) {
                            assembler("updateElementMeta", elementMeta, ["id", "key", "value"])
                        }

                        // Element geometry
                        if (elementGeometry && elementGeometry.length > 0) {
                            assembler("updateElementGeometry", elementGeometry, ["id", "type", "content", "x", "y", "w", "h"])
                        }

                        return rval;
                    }

                    return batch;
                },

                /** Registers a global listener for a given channel */
                registerCallback: function(name, suffix) {
                    var ourname = "_" + name + "Listener";

                    // Register the wrapper
                    this.variables.engine.registerListener(name, ourname);
                    callbacks.register(ourname, suffix);
                },


                /** Starts a new batch call */
                startBatch: function(){
                    this.variables.batch = this.batch()
                },


                /** Ends the current batch call and executes it */
                endBatch: function(){
                    var batch = this.variables.batch,
                        engine = this.variables.engine

                    if (!batch) return;

                    // Execute each call
                    batch.generateBatchCalls().forEach(function(c){
                        engine.batch(c);
                    })

                    this.variables.batch = null;
                },


                handler: {
                    /** Generic callback function */
                    generic: function(listener, pnames) {
                        try {
                            var param = {};

                            // Setup arguments
                            for(var i = 0; i<pnames.length; i++) {
                                param[pnames[i]] = arguments[2+i]
                            }

                            // Call all listener
                            connector.variables.listeners.process(listener, function(f){
                                f(param);
                            });
                        }
                        catch (e) {
                            alert(listener + " processing failure : " + e);
                        }
                    },

                    /** Called when reduced gaze events arrive */
                    onRawReducedGaze : function(x, y){
                        connector.connection.handler.generic("reducedApplicationGaze", ["x", "y"], parseInt(x), parseInt(y))
                    },

                    /** Called when fixation events arrive */
                    onRawFixation: function(_type, _x, _y, _args){
                        var x = parseInt(_x),
                            y = parseInt(_y),
                            s = "UNDEFINED"

                        if(_type == "FIXATION_START") s = "START";
                        if(_type == "FIXATION_END") s = "END";
                        
                        var duration = 0;
                        var meanderivation = 0;
                        
                        // Parse optional arguments
                        var args = _args.toString().split(",");
                        for(var i = 0; i < args.length; i++) {
                            var t = args[i].split("=");
                            var k = t[0];
                            var v = t[1];
                            
                            if(k == "duration") duration = parseInt(v)
                            if(k == "meanderivation") meanderivation = parseInt(v)
                        }
                        

                        connector.connection.handler.generic("fixation", ["x", "y", "type", "duration", "meanderivation"], x, y, s, duration, meanderivation)
                    },

                    /** Called when head movements arrives */
                    onRawHead: function(_date, _x, _y, _z){
                        connector.connection.handler.generic("headPosition", ["x", "y", "z"], parseFloat(_x), parseFloat(_y), parseFloat(_z))
                    },

                    /** Called when a weak saccade occures */
                    onRawWeakSaccade: function(_angle, _distance){
                        connector.connection.handler.generic("weakSaccade", ["angle", "distance"], parseFloat(_angle), parseFloat(_distance))
                    },

                    /** Called when the plugin is ready */
                    onRawPerusal: function(_speed, _x, _y, _w, _h, text){
                        connector.connection.handler.generic("perusal", ["speed", "x", "y", "w", "h", "text"], parseFloat(_speed), parseInt(_x), parseInt(_y), parseInt(_w), parseInt(_h), text)
                    },
                },

                /** Called when the plugin is ready */
                onStatus: function(status, arg1){

                    // Get a number variables
                    var self = text20.connector.connection,
                        times = text20.connector.connection.variables.times,
                        engine = self.variables.engine,
                        registry = text20.connector.extensions.registry

                    try {
                        if (status == "INITIALIZED") {
                            // Give first lifesign and timing report                            
                            times.appletinitialized = new Date().getTime()
                            var time = times.appletinitialized - times.appletadded  
                            text20.browser.log("Callback from plugin received. This means the plugin runs. Init time " + time + "ms.");
                            
                            // Compare versions and warn in case things went wrong
                            if(!arg1 || version.build != arg1) {
                                alert("This version of text20.js (" + version.build + ") is incompatible with the version of text20.jar (" + arg1 + "). We will continue, but there might be errors or strange behavior. Please use the same version.");
                                text20.browser.log("Version mismatch. Plugin reported version " + arg1 + ". Expect misbehavior.");                                                            
                            }                            
                            
                            self.variables.initialized = true;

                            // Check if the engine is there (this should never happen)
                            if(engine == null) {
                                alert("A strange error happened. Engine appears to be null, even though we got an initialization message!")
                                return;
                            }

                            text20.browser.log("Starting to load extensions.");

                            // Check if we can query extensions (what is causing these problems?!!?)
                            if(engine.getExtensions == null) {
                                alert("getExtensions() not found. All extensions are disabled! Please report this bug!");
                            } else {
                                // Hook up all extensions
                                var allExtensions = engine.getExtensions();
                                for(var i = 0; i<allExtensions.size(); i++) {
                                    var ext = allExtensions.get(i);

                                    // LiveConnect bullshit once again. On some platforms automatic wrapping of returend
                                    // objects doesn't work anymore.
                                    if(ext && ext.toString) {
                                        ext = ext.toString();
                                    }

                                    registry.register(ext);
                                }
                            }


                            text20.browser.log("Extensions loaded. Starting to load the browser calibration.");


                            var browserID = text20.browser.id(),
                                dx = engine.getPreference("connector:calibration:" + browserID + ":offset:x", "UNDEFINED"),
                                dy = engine.getPreference("connector:calibration:" + browserID + ":offset:y", "UNDEFINED")

                            text20.browser.log("Obtained raw offsets " + dx + "," + dy + " for browserID '" + browserID + "'")

                            // If something has been set, use the override offset
                            if(dx != "UNDEFINED" && dy != "UNDEFINED") {
                                var newoffset = [0, 0];
                                newoffset[0] = parseInt(dx);
                                newoffset[1] = parseInt(dy);

                                // Only use the new offset if there was no previous override set
                                text20.browser.calibratedOffset = newoffset;
                            } else {
                                // We should probably inform the user that the current browser is not calibrated
                                if(connector.config.warnIfNotCalibrated)
                                    $("body").append("<div style='position: absolute; top:50px; width: 100%; text-align:center; font-size:150%; background-color:red; color:white;'>You should run the browser-calibration first!<br/><br/><span style='font-size:60%;'>Otherwise your gaze data will be off.<br/> You only have to do this calibration once </br>per browser (unless you change things<br/> like the menu- or bookmark-bar...). </span></div>")
                            }


                            text20.browser.log("Calibration loaded. Registering low level handlers.");


                            // Register some of the global callback listeners at the engine ...
                            self.registerCallback("fixation", self.handler.onRawFixation);
                            self.registerCallback("perusal", self.handler.onRawPerusal);
                            self.registerCallback("weakSaccade", self.handler.onRawWeakSaccade);

                            // Only register the high volume listeners if there is a need
                            if(connector.config.registerHighVolumeListeners) {
                                self.registerCallback("reducedApplicationGaze", self.handler.onRawReducedGaze);
                                self.registerCallback("headPosition", self.handler.onRawHead);
                            }


                            text20.browser.log("Handlers registered. Starting up EEG handling and registering additional functions.");

                            // Setup EEG device (TODO: very beta ...)
                            if(connector.config.enableBrainTracker) {
                                var r = connector.extensions.brainTrackerInitEvaluation();
                                alert(r)
                            }


                            // Register mouse clicked listener
                            window.document.onclick = function(e) {
                                connector.extensions.mouseClicked(0, e.button)
                            }
                            
                            times.callinginit = new Date().getTime()
                            text20.browser.log("Functions registered. Calling registered handlers. Midsection time " + (times.callinginit - times.appletinitialized) + "ms.");

                            // Process all initialized listener
                            connector.variables.listeners.process("INITIALIZED", function(f){
                                f();
                            });
                            
                            times.finishedinit = new Date().getTime()
                            text20.browser.log("Time in handlers " + (times.finishedinit - times.callinginit) + "ms.");
                            text20.browser.log("Transfering control to application. Overall init time " + (times.finishedinit - times.jsinit) + "ms. Have fun.");

                        }
                    }
                    catch (e) {
                        alert("statusListener() failure : " + e);
                    }
                },


                /** Called when a special callback is triggered by an extension */
                onSpecialCallback: function(){
                    try {
                        var name = arguments[0],
                            rest = [];

                        for (var i = 1; i < arguments.length; i++) {
                            rest.push(arguments[i])
                        }

                        text20.connector.extensions.registry.callback(name, rest);
                    }
                    catch (e) {
                        alert("specialCallbackListener() failure : " + e);
                    }
                },



                /** Connect the applet to the plugin, i.e., activate gaze! */
                connect: function(){
                    // Assemble extension string
                    var allExtensions = ""
                    connector.config.extensions.forEach(function f(i) {
                        allExtensions += file.absolutePath(i) + ";"
                    })
                    
                    this.variables.times.appletadded = new Date().getTime()

                    var parameterString = "<param name='trackingdevice' value='" + connector.config.trackingDevice + "'>" +
                                          "<param name='trackingconnection' value='" + connector.config.trackingURL + "'>" +
                                          "<param name='enablebraintracker' value='" + connector.config.enableBrainTracker + "'>" +
                                          "<param name='braintrackingconnection' value='" + connector.config.brainTrackingURL + "'>" +
                                          "<param name='callbackprefix' value='" + callbacks.prefix() + "'>" +
                                          "<param name='transmitmode' value='" + connector.config.transmitMode + "'>" +
                                          "<param name='sessionpath' value='" + connector.config.sessionPath + "'>" +
                                          "<param name='recordingenabled' value='" + connector.config.recordingEnabled + "'>" +
                                          "<param name='extensions' value='" + allExtensions + "'>" +
                                          "<param name='updatecheck' value='" + connector.config.updateCheck + "'>" +
                                          "<param name='logging' value='" + connector.config.logging + "'>" +
                                          "<param name='diagnosis' value='" + connector.config.diagnosis + "'>" +
                                          "<param name='configuration' value='" + $.param(text20.core.config) + "'>" +
                                          "<param name='java_arguments' value='-Xmx512m'>" +
                                          "<param name='classloader_cache' value='true'>" +
                                          "<param name='separate_jvm' value='true'>";

                    // (Issue #32)
                    if(connector.config.useObjectTag) {
                        // Append <object> tag
                        $("body").append(
                            '<object type="application/x-java-applet" name="Text20Engine"' +
                            'id="' + this.variables.appletID + '"' +
                            'archive="' + connector.config.archive + '"' +
                            'code="de.dfki.km.text20.browserplugin.browser.browserplugin.impl.BrowserPluginImpl"' +
                            'codebase="./"' +
                            'width="1" height="1" mayscript="true" >' +

                            parameterString +

                            '</object>'
                        );
                    } else {
                        // Append <applet> tag
                        $("body").append(
                            "<applet " +
                            "id='" + this.variables.appletID + "'" +
                            "name='Text20Engine'" +
                            "archive='" + connector.config.archive + "'" +
                            "code='de.dfki.km.text20.browserplugin.browser.browserplugin.impl.BrowserPluginImpl.class'" +
                            "width='1' height='1' mayscript='true' >" +

                            parameterString +

                            "</applet>"
                        );
                    }


                    text20.browser.log("Plugin added. Waiting for a lifesign. This usually takes 5-10 seconds.");

                    // Set applet engine
                    this.variables.engine = $("#" + this.variables.appletID).get(0);

                    // Prepare special callbacks
                    callbacks.register("_augmentedTextStatusFunction", this.onStatus)
                    callbacks.register("specialCallback", this.onSpecialCallback)

                    // Initialize cache
                    this.variables.transmitCache.create("windowPosition")
                    this.variables.transmitCache.create("documentViewport")
                    this.variables.transmitCache.create("elementCache").cache = cache.cache()
                },


                /** Check if the engine is running and up */
                enginecheck: function(fnc) {
                    if (!this.variables.initialized) {
                        alert("Enginecheck: Plugin not initialized yet");
                        return null;
                    }

                    if (!this.variables.engine) {
                        alert("Enginecheck: There is no engine!");
                        return null;
                    }

                    // If we got a function, call it
                    if(fnc) {
                        return fnc(this.variables.engine, this.variables, this)
                    } else return this.variables.engine;
                },




                /** Calls a generic command inside the engine */
                callGeneric: function(cmd){
                    return this.enginecheck(function(e, v, s) {
                        return e.callFunction(cmd)
                    })
                },


                /** Transmit if this window can be seen or not */
                transmitWindowVisibility: function(isVisible) {
                    this.enginecheck(function(e, v, s) {
                        e.updateElementFlag("#window", "focussed", isVisible)
                    })
                },


                /** Transmits if the given element has been removed in the meantime */
                transmitElementRemoved: function(id) {
                    this.enginecheck(function(e, v, s) {

                        // We also need to remove the ID from our cache, otherwise we could not 
                        // use the ID again when it is created again.
                        v.transmitCache.get("elementCache").cache.remove(id)

                        // If we have a batch call, call that one
                        if (v.batch) {
                            v.batch.updateElementFlag(id, "REMOVED", true);
                            return;
                        }

                        e.updateElementFlag(id, "REMOVED", true);
                    })
                },


                /** Transmits where the element is anchored */
                transmitElementPositionAnchor: function(id, anchor) {
                    this.enginecheck(function(e, v, s) {
                        if (anchor == "window")
                            e.updateElementFlag(id, "FIXED_ON_WINDOW", true);
                    })
                },


               /** Transmits the browser's outline */
               transmitBrowserGeometry: function(x, y, w, h) {
                    this.enginecheck(function(e, v, s) {
                        var p = v.transmitCache.get("windowPosition");
                        var offset = browser.documentOffset();

                        // Can be null when there were problems                        
                        if(!offset) offset = [0, 0]

                        // Correct offset before transmission
                        x += offset[0]
                        y += offset[1]

                        // In case values have been changed
                        if (x != p.x || y != p.y || w != p.w || h != p.h) {
                            p.x = x;
                            p.y = y;
                            p.w = w;
                            p.h = h;
                            text20.browser.log("New browser geometry: " + x + "," + y + " " + w + "x" + h + " (document offset is " + offset[0] + ", " + offset[1] + ")")
                            e.updateBrowserGeometry(x, y, w, h);
                        }
                    })
                },

                /** Sets or gets preferences */
                preference: function(key, value) {
                    return this.enginecheck(function(e, v, s) {
                        if(value) {
                            e.setPreference(key, value);
                            return
                        } else {
                           return e.getPreference(key, "UNDEFINED");
                        }
                    })
                },

                /** Transmits an element's outline */
                transmitElement: function(id, type, content, x, y, w, h) {

                    if(!this.enginecheck()) return

                    var updateElementGeometry = this.variables.engine.updateElementGeometry

                    // If we have a batch, use that one
                    if (this.variables.batch) {
                        updateElementGeometry = this.variables.batch.updateElementGeometry
                    }


                    var elementCache = this.variables.transmitCache.get("elementCache");
                    var cache = elementCache.cache.get(id)

                    if (cache == null) {
                        cache = elementCache.cache.create(id);
                        cache.id = id;
                        cache.type = type;
                        cache.content = content;
                        cache.x = x;
                        cache.y = y;
                        cache.w = w;
                        cache.h = h;

                        updateElementGeometry(id, type, content, x, y, w, h);
                        return;
                    }

                    if (cache.x != x || cache.y != y || cache.w != w || cache.h != h || (type == "image" && cache.content != content)) {
                        cache.x = x;
                        cache.y = y
                        cache.w = w;
                        cache.h = h;
                        cache.content = content;
                        updateElementGeometry(id, type, content, x, y, w, h);
                    }
                },

                /** Transmits the current viewport */
                transmitViewport: function(x, y){
                    if(!this.enginecheck()) return

                    var p = this.variables.transmitCache.get("documentViewport");

                    // In case values have been changed
                    if (x != p.x || y != p.y) {
                        p.x = x;
                        p.y = y;
                        this.variables.engine.updateDocumentViewport(x, y);
                    }
                },

                /** Transmit meta info */
                transmitElementMetaInformation: function(id, key, value) {
                    if(!this.enginecheck()) return

                    if (this.variables.batch) {
                        this.variables.batch.updateElementMeta(id, key, value);
                        return;
                    }

                    this.variables.engine.updateElementMetaInformation(id, key, value);
                },

                /** Drop calbration */
                dropBrowserCalibration: function() {
                     browser.overrideOffset = [0, 0];
                },

                /** Sets a session parameter */
                setSessionParameter: function(key, value){
                    if(!this.enginecheck()) return
                    this.variables.engine.setSessionParameter(key, value);
                },
            }
        },
    },

    core = {
        /** Configure core settings in here ... */
        config: {
            clusters: 1,  // How many clusters we have for transmission (TODO: Move to connector)

            // If set to true, the core will try to find all elements that have onGaze/onFixation/...
            // attributes. This works quite well, but takes some time every new fixation and might
            // add significant overhead for larger pages. If set to false you have to call
            // core.attributed.update('onFixation') -- (change onFixation with what you need) -- every
            // time you add or change the DOM tree and add or remove elements containing such attributes (TODO: Move to connector)
            autoupdateAttributed: true,
            
            // Fixation settings
            fixation: {
                // Minimum required duration to accept a fixation
                minimumDuration: 100,
                
                // Maxium radius in pixel to accept a fixation
                maxFixationRadius: 25,
                
                // Minimum number of events before we consider events a fixation
                minEvents: 1,
            }
        },
        
        
        /** Various gaze related variables and methods */
        gaze: {
            /** The latest gaze position in document coordinates */
            position: [-1, -1],    
        },


        /**  Our cache and handlers for attributed (onFixation, onGazeOver, ...) elements */
        attributed: {
            /** All attributes we should update */
            attributes: ["onGazeOver", "onGazeOut", "onFixation", "onPerusal"],

            /** Cache containing selectors for the given attributes */
            cache: {},

            /** Returns a jQuery object for the given selector */
            get: function(selector) {
                if(core.config.autoupdateAttributed)
                    this.update(selector)

                return this.cache[selector]
            },

            /** Updates the jquery object for the given selector */
            update: function(selector) {
                var cache = this.cache

                if(selector)
                    cache[selector] = $("*[" + selector + "]")
                else {
                    // Update values for each attribute
                    this.attributes.forEach(function f(x){
                        cache[x] = $("*[" + x + "]")
                    })
                }
            }
        },


        /** Should not be access from the outside */
        internal: {
            clustering: {
                // Number of clusters to update
                clusters: 1,

                // If this is set to true, elements will only be transmitted once. Useful if you know the page never changes.
                disableSubsequentUpdates: false,

                // Internal variables
                clusterPTRCreation: 0,
                clusterPTRTransmission: 0,
                numRegistered: 0,
            },

            body: $("body").get(0),

            /** Update all previously registed elements */
            updateRegistered: function(){
                connector.connection.startBatch();

                var basics = this,
                    connection = connector.connection,
                    clustering = core.internal.clustering,
                    dontUpdateAfterRegister = true,
                    registered = false;

                // Process all elements which are currently unprocessed
                $(".untransmitted").each(function(){
                    var self = $(this)
                    var id = self.attr("id")

                    var pos = dom.documentPosition(this),
                        w = parseInt(this.offsetWidth),
                        h = parseInt(this.offsetHeight),
                        type = "unknown",
                        content = null;

                    var text = null,
                        word = null;


                    // Sanity check
                    if (pos == null) { return; }


                    // Check if the element is of the type SPAN
                    if (this.tagName == "SPAN") {
                        // Check type and content (SPAN-test)
                        if (this.firstChild && this.firstChild.nodeValue) {
                            type = "text"
                            content = this.firstChild.nodeValue;

                            // Try to get optional values
                            text = self.attr("_textID")
                            word = self.attr("_wordID")
                        }
                    }

                    // Handle image elements here
                    if (this.tagName == "IMG") {
                        // Setting type to image and content to image-url
                        type = "image"
                        content = this.src;
                    }

                    // Transmit basic data first time
                    connection.transmitElement(id, type, content, pos[0], pos[1], w, h);

                    // Transmit flags first time
                    if (pos[2] == "window") {
                        connection.transmitElementPositionAnchor(id, "window");
                    }

                    // Transmit sequential text information
                    if (text != null) {
                        connection.transmitElementMetaInformation(id, "textID", text);
                        connection.transmitElementMetaInformation(id, "wordID", word);
                    }
                    
                    // Rember that we registered an element
                    registered = true
                }).removeClass("untransmitted");

                // In case subsequent updates are disabled we are finished.
                if (clustering.disableSubsequentUpdates || (dontUpdateAfterRegister && registered)) {
                    connector.connection.endBatch();
                    return;
                }

                // TODO: Shouldn't we stop here when we added new elements, so that they don't get transmitted
                // twice? On the other hand, how would we know which these are?

                // Select what we want to transmit this round
                var cls = ".clusterID" + clustering.clusterPTRTransmission++;

                // Cycle clusterPTRTransmission
                if (clustering.clusterPTRTransmission == core.config.clusters) {
                    clustering.clusterPTRTransmission = 0;
                }

                // Neat. We just select all elements by their class and send them
                $(cls).each(function(){
                    var id = $(this).attr("id")

                    var pos = dom.documentPosition(this),
                        w = parseInt(this.offsetWidth),
                        h = parseInt(this.offsetHeight),
                        type = null,
                        content = null;
                        
                    // If we have an image tag, check the .src again (might have changed
                    if (this.tagName == "IMG") {
                        type = "image"
                        content = this.src;
                    }    
                    
                    connection.transmitElement(id, type, content, pos[0], pos[1], w, h);
                });

                connector.connection.endBatch();
            }
        },

        handler: {
            /**
             * Handles onGaze handler given a gazed screen position.
             *
             * @param {Object} x position in document coordinates
             * @param {Object} y position in document coordinates
             */
            onGazeHandler: function(x, y){
                // After this we know all elements which are under gaze.
                var gazedElement = document.elementFromPoint(x - window.pageXOffset, y - window.pageYOffset),
                    allUnderCurrentGaze = dom.parents(gazedElement);

                // Or ther onGazeOut handler
                core.attributed.get("onGazeOut").each(function(i){
                    // If an element with onGazeOut is under gaze, ignore it.
                    if (allUnderCurrentGaze.indexOf(this) >= 0)
                        return;

                    var elem = $(this)

                    // If the was not under gaze last round, ignore it
                    if (!elem.hasClass("underGazeLastRound"))
                        return;

                    // Okay, in this case we call the handler and set underGazeLastRound ...
                    eval(elem.attr("onGazeOut"))
                    elem.removeClass("underGazeLastRound")
                })


                // First we check which elements might need to have called their onGazeOver handler
                core.attributed.get("onGazeOver").each(function(i){
                    var elem = $(this)

                    // If an element with onGazeOver isn't under gaze, ignore it.
                    if (allUnderCurrentGaze.indexOf(this) < 0) {
                        elem.removeClass("underGazeLastRound")
                        return;
                    }

                    // If it is, we have to check if it already was under gaze last time.
                    if (elem.hasClass("underGazeLastRound"))
                        return;

                    // Okay, in this case we call the handler and set underGazeLastRound ...
                    eval(elem.attr("onGazeOver"))
                })

                // Mark all elements under gaze this round
                allUnderCurrentGaze.forEach(function f(x){
                    $(x).addClass("underGazeLastRound")
                })
            },


            /**
             * Handles onFixation handler.
             *
             * @param {Object} x
             * @param {Object} y
             */
            onFixationHandler: function(x, y){
                // After this we know all elements which are under gaze.
                var gazedElement = document.elementFromPoint(x - window.pageXOffset, y - window.pageYOffset),
                    allUnderCurrentGaze = dom.parents(gazedElement);
                    

                core.attributed.get("onFixation").each(function(i){
                    // If an element with onGazeOut is under gaze, ignore it.
                    if (allUnderCurrentGaze.indexOf(this) < 0)
                        return;

                    var elem = $(this)

                    // Okay, in this case we call the handler and set underGazeLastRound ...
                    eval(elem.attr("onFixation"))
                })
            },

            /**
             * Handles onPerusal events.
             *
             * @param {Object} x
             * @param {Object} y
             */
            onPerusalHandler: function(e){

                core.attributed.get("onPerusal").each(function(i){

                    var pos = dom.documentPosition(this);
                    var x = pos[0]
                    var y = pos[1]
                    var w = parseInt(this.offsetWidth);
                    var h = parseInt(this.offsetHeight);

                    var match = false


                    //
                    // 1. See if the element intersects the perused rectangle
                    //
                    if (e.x <= x && x <= e.x + e.w &&
                    e.y <= y &&
                    y <= e.y + e.h) {
                        match = true;
                    }
                    if (e.x <= x + w && x + w <= e.x + e.w &&
                    e.y <= y &&
                    y <= e.y + e.h) {
                        match = true;
                    }
                    if (e.x <= x && x <= e.x + e.w &&
                    e.y <= y + h &&
                    y + h <= e.y + e.h) {
                        match = true;
                    }
                    if (e.x <= x + w && x + w <= e.x + e.w &&
                    e.y <= y + h &&
                    y + h <= e.y + e.h) {
                        match = true;
                    }

                    if (x <= e.x && e.x <= x + w &&
                    y <= e.y &&
                    e.y <= y + h) {
                        match = true;
                    }

                    if (match) {

                        //
                        // 2. If it does, additionally check if the elment was visible
                        //
                        // FIXME: Does not work properly. We have to hit the element, then get all of its parents
                        // and again, check if they might be equal.
                        //var hitTestResult = document.elementFromPoint(x + w / 2, y + h / 2);
                        //if(hitTestResult != this) return;

                        var elem = $(this)
                        eval(elem.attr("onPerusal"))
                    }
                })
             },

             /**
              * Handles onEmotion handler.
              *
              * @param {Object} x
              * @param {Object} y
              */
             onEmotionHandler: function(x, y){
                 // Check if we are enabled
                 if(!connector.config.enableBrainTracker) return;
                 
                 // After this we know all elements which are under gaze.
                 var gazedElement = document.elementFromPoint(x - window.pageXOffset, y - window.pageYOffset),
                     allUnderCurrentGaze = dom.parents(gazedElement),
                     emotion = connector.extensions.getTrainedPeakEmotion();

                 if (!emotion) return;

                 var map = {
                     "happy" : "onSmile",
                     "interested" : "onInterest",
                     "doubt" : "onFurrow",
                     "bored" : "onBoredom",
                 }

                 var handler = map[emotion.split(" ")[0]];
                 if (!handler) return;

                 core.attributed.get(handler).each(function(i){
                     if (allUnderCurrentGaze.indexOf(this) < 0)
                         return;

                     eval($(this).attr(handler))
                 })
             }
        },

        /** Handles connector callbacks */
        listener: {

            /** Called upon init of the connector. */
            initListener: function(){
                var connection = connector.connection

                // Register handler after a short timeout, otherwise we might get spurious blur events ...
                $(window).oneTime(250, function(){
                    $(this).blur(callbacks.func("blurListener"));
                    $(this).focus(callbacks.func("focusListener"));
                });

                // Update the browser position regularly.
                $(window).everyTime(250, "", function() {

                    // TODO: Get the values, otherwise gaze posistion will be wrong ...
                    var offsetX = 0;
                    var offsetY = 0;

                    var trueX = window.screenX + offsetX;
                    var trueY = window.screenY + offsetY;

                    connection.transmitBrowserGeometry(trueX, trueY, window.innerWidth, window.innerHeight);
                    connection.transmitViewport(window.pageXOffset, window.pageYOffset);
                });
            },

            /** Called when new fixations come in */
            fixationListener: function(param){
                if (param.type != "START")
                    return;

                var x = param.x;
                var y = param.y;
                
                // Update current gaze position
                core.gaze.position[0] = x;
                core.gaze.position[1] = y;
               
                // call onGaze* and onFixation handler
                core.handler.onGazeHandler(x, y)
                core.handler.onFixationHandler(x, y)
                core.handler.onEmotionHandler(x, y)
            },

            /** Called when new reduced gaze events come in */
            reducedGazeListener: function(param){
                var x = param.x;
                var y = param.y;
            },

            /** Called when new perusal events come in */
            perusalListener: function(param){
                core.handler.onPerusalHandler(param);
            },
        },

        /**
         * Call this to register an element for advanced gaze tracking (i.e.,
         * transmition to the atplugin). onGaze handler will work without, but, 
         * for example, the reading detection requires words.
         * 
         * You may also call this function multiple times for already registered 
         * elements in case their content changed in betweed and subsequent updates
         * have been disabled. 
         *
         * Elements not registered are invisible to the engine.
         *
         * @param {Object} element
         */
        register: function(element){
            if(!element) return;
            if(!element.forEach) element = [element]

            var c = this.internal.clustering

            element.forEach(function(_e) {
                var e = $(_e)

                // Tag and ensure it has an ID
                e.addClass("registeredGazeElement untransmitted clusterID" + c.clusterPTRCreation++)

                // Cycle clusterPTR creation
                if (c.clusterPTRCreation == core.config.clusters) {
                    c.clusterPTRCreation = 0;
                }

                dom.ensureID(_e);
            })
        },

        /**
         * Unregisters an element and makes it invisible to the engine again.
         *
         * As a rule of thum, user-controlled sites should register element as 
         * soon as they were created and deregister them only shortly before removal.
         *
         * @param {Object} element
         */
        deregister: function(element){
            if(!element) return;

            if(!element.forEach) {
                element = [element]
            }

            // Start a new batch call to speed up the process
            connector.connection.startBatch();

            element.forEach(function(e) {
                var el = $(e)
                el.removeClass("registeredGazeElement")
                connector.connection.transmitElementRemoved(el.attr("id"));
            })


            // End the batch and transmit
            connector.connection.endBatch();
        },



        /** Call when everything is set up and ready to go */
        init: function() {
            if(navigator && navigator.javaEnabled && !navigator.javaEnabled()) {
                alert("You need to install Java (and enable Applets) to use the Text 2.0 Framework.")
                return;
            } 
            
            // Print version
            text20.browser.log("Initializing text20.js (" + version.build + ").");
            
            var self = this

            // Execute this only when the document is ready ...
            $(document).ready(function() {
                var connection = connector.connection

                // Bring up the currently used connection
                connection.connect()

                // Update element positions regularly
                $(window).everyTime(1000, "", self.internal.updateRegistered);

                // Add initialization listener to the connector
                connector.listener("INITIALIZED", self.listener.initListener);
                connector.listener("fixation", self.listener.fixationListener);
                connector.listener("perusal", self.listener.perusalListener);

                // Register some global callbacks regarding the window visibility
                callbacks.register("blurListener", function(){
                    text20.browser.log("Window blurred")
                    connection.transmitWindowVisibility(false);
                });
                callbacks.register("focusListener", function(){
                    text20.browser.log("Window focussed")
                    connection.transmitWindowVisibility(true);
                });

                // Update all attributed elements
                core.attributed.update()
            })
        }
    }


    // Add namespaces
    text20.strings = strings
    text20.math = math
    text20.listener = listener
    text20.system = system
    text20.browser = browser
    text20.dom = dom
    text20.file = file
    text20.connector = connector
    text20.core = core

    // Set the default connection
    connector.connection = connector.methods.applet

    // Register text20 globally
    window.text20 = text20
})(window);
