StickyNotes
===========

StickyNotes was my first Processing sketch, and it was an attempt to see if I could play music on my office wall in about an hour with just my laptop's camera and a pile of sticky notes.  It works, and it's fun, but it's neither robust nor sophisticated.  The code is a little janky, too.

Requirements
------------

You'll need a recent version of Processing that includes the video and Minim libraries.  I've tested StickyNotes with Processing 1.0.7 on Mac OS X 10.6.2 (Snow Leopard) and on **nothing else**.

Installation
------------

If you're using `git`, just change into your Processing sketchbook's directory and clone the repository.

If not, download and place all of the Processing sketches (`.pde` files) in a directory called `StickyNotes` within your Processing sketchbook directory.  Place the font file (`.vlw`) in a subdirectory of `StickyNotes` called `data`.

Making Music
------------

Aim your computer's camera at a wall that has a few sticky notes on it.  Click a musical note in StickyNotes, then click one of the sticky notes on the wall in the video frame.  This will assign that musical note to the sticky.  Now cover up the sticky note in the video frame (with your hand, foot, whatever) and the musical note will be played.  

It works best if you align a row of sticky notes on the wall above your head so that you can walk beneath them without blocking the camera's view of the notes and then reach up to cover the stickies with your hand to "play" them like a piano keyboard.

Notes
-----

This program is not very smart at all.  In other words, there's no sophisticated video processing going on under the hood: it just remembers the color value of certain pixels in the frame and plays a note if it sees them change beyond a certain threshold.  The stickies really only serve to remind the user where the trigger points in the video frame are.  This means that poor or changing lighting conditions, motion of the camera, and just about anything else, including a stiff breeze, can induce cacophonous results.

Also, chords don't sound great, and I'm not even entirely sure why.

Improvements
------------

A better trigger detection algorithm might be interesting to play around with, although the current brain-dead detector works pretty well in controlled environments, and this is just a toy after all.

I considered adding samples and loops in addition to just tones.  I'd also like to understand why chords sound so weird, but I'm no music expert.

It might also be neat to add a video overlay on the selected trigger points to show you what notes they are, or to do some sort of video animation or visual indication when a trigger is triggered.

It's not likely that I'll do any of this :)

Author
------

Jon Speicher (jon.speicher@gmail.com)

License
-------

    The MIT License

    Copyright (c) 2009 Jonathan Speicher

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.