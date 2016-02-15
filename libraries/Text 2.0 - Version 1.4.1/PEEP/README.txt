
Processing Easy Eye Tracker Plugin (PEEP)
===========================================




Installation
------------

1) Drop the 'eyetracking' folder into Processing's 'libraries' directory.

2) [Optional] Edit 'config.properties' and set up your eye tracker

3) Make sure the TrackingServer is running.






Example Usage
-------------

import eyetracking.*;

// This is our tracking device. It provides you with eye data.
EyeTrackingDevice device;

void setup() {
    // Opening the device like this should be fine. Takes ~5 secs to warm-up.
    device = EyeTrackingDevice.open(this);
}


void draw() {
    // In case of emergency.
    device.debug();

    // Is the user there?
    println(device.isLooking);

    // We give you eyes ... (fixations, pixel coordinates in window)
    println(device.x);
    println(device.y);

    // ... raw gaze ...
    println(device.eyes.rawX);
    println(device.eyes.rawY);


    // ... and head (normalized between 0.0
    // and 1.0; 0.5 is the center of the tracking box).
    println(device.head.x);
    println(device.head.y);
    println(device.head.z);

}




Mouse Emulation / Tobii Eye Tracker
------------------------------------

This tracking server comes with two drivers, an eye tracking
simulator and a true Tobii connector. If you have no Tobii
eye tracker you have to use the simulator (DEFAULT).

If you have a Tobii, edit the file 'config.properties' and switch
the gaze adapter to Tobii. You do this by enabling the line

de.dfki.km.text20.trackingserver.remote.impl.TrackingServerRegistryImpl.adapter.id=gazeadapter:tobii

and disabling the line with the simulator. Also make sure you use
the proper TET server. This is usually '127.0.0.1' for a Tobii 1750,
or the IP returned by the diag tool, e.g.:

de.dfki.km.text20.trackingserver.adapter.impl.tobii.TobiiGazeAdapter.tobii.server=TX120-203-83900194.local.





Contact
-------

In case you have questions, send a mail to

ralf.biedert@dfki.de



Copyright
---------

(c) 2011 Ralf Biedert, German Research Center for Artificial Intelligence.




Legal
-----


NO WARRANTY

THE PROGRAM IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL, 
BUT WITHOUT ANY WARRANTY. IT IS PROVIDED "AS IS" WITHOUT WARRANTY 
OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED 
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE 
OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, 
YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW THE AUTHOR WILL BE LIABLE 
TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR 
CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE 
PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED 
INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF 
THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF THE AUTHOR HAS 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.


