/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.classbased;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class ClassBased {


    public static void main(String[] args) throws MalformedURLException {
        URL url;
        if (args.length > 0) {
            url = new File(args[0]).toURI().toURL();
        } else {
            url = ClassBased.class.getResource("classbased.config.xml");
        }

        System.out.println("Loading...");

        ConfigurationManager cm = new ConfigurationManager(url);

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        Microphone microphone = (Microphone) cm.lookup("microphone");

        /* allocate the resource necessary for the recognizer */
        recognizer.allocate();

        printInstructions();

        /* the microphone will keep recording until the program exits */
        if (microphone.startRecording()) {

            while (true) {
                System.out.println
                        ("Start speaking. Press Ctrl-C to quit.\n");

                /*
                * This method will return when the end of speech
                * is reached. Note that the endpointer will determine
                * the end of speech.
                */
                Result result = recognizer.recognize();

                if (result != null) {
                    String resultText = result.getBestResultNoFiller();
                    System.out.println("You said: " + resultText + "\n");
                } else {
                    System.out.println("I can't hear what you said.\n");
                }
            }
        } else {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

    }

    /**
     * Prints out what to say for this demo.
     */
    private static void printInstructions() {
        System.out.println
                ("Sample sentences:\n" +
                        "\n" +
                        "one ticket to boston\n" +
                        "two tickets to washington for tuesday\n" +
                        "\n");
    }
}
