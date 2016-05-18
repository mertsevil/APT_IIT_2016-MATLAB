/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optrecursive_testbench;

import Jama.Matrix;
import java.io.File;
import java.util.List;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.ex.*;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;

/**
 *
 * @author Cat
 */
public class OptRecursive_Testbench {

    //Parameter : current processing environment. 
    // This allows us to switch between <processing> nodes in the configuration XML 
    // by using the @env attribute. 
    public static String configurationEnvironment = "none";
    //Parameter: excel files to save/load variables
    //TODO make sure files exist in the given path
    public static String excelFilePath;
    //Parameter: File for Bodymedia read values
    //TODO make sure this file is the same as the xls generated by BodyMedia
    public static String bodymediaFileUrl;
    //Parameter: Email to receive messages
    public static String[] privateMails;

    // Optimization fields:
    private static double Y;
    private static Matrix phi;
    private static Matrix Q_old;
    private static Matrix P_old;
    private static double lamda_old;
    private static double[] upperlimit;
    private static double[] lowerlimit;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Set up configuration here so we can read from the configuration file for our previously-static 
        // variables. 
        boolean configureOK = configureSession();

        System.out.println(excelFilePath + " : " + new File(excelFilePath).exists());
        System.out.println(bodymediaFileUrl + " : " + new File(bodymediaFileUrl).exists());

        //Start Graphical interface
        //XXX restore this next line before merging with master. 
        if (configureOK) {
            //Use testbench to test OptRecursive
            createOptRecursiveDefaultParameters();
            OptRecursive testOptRecursive = new OptRecursive(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
            testOptRecursive.runOptimization();
            //Recursive
            int i = 0;
            while (i < 10) {
                createOptRecursiveSetParametersQP(testOptRecursive.Q_res, testOptRecursive.P);
                testOptRecursive = new OptRecursive(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
                testOptRecursive.runOptimization();
                i++;
            }

        }

        //  ChocaNonLinear ch = new ChocaNonLinear ();
        //   ch.Choca();
    }

    //Is it bad that this method is referencing our now-global variables? Eh, maybe. 
    //This only needs to run here, though. We can expand to a full class with a factory etc. 
    // for all our platform-specific global variables if we need to. 
    public static boolean configureSession() {
        boolean output = false; //be pessimistic. 
        Configurations configs = new Configurations();
        try {
            System.out.println("User directory is " + System.getProperty("user.dir"));
            XMLConfiguration config = configs.xml("config/configuration.xml"); //this is a really nice factory implementation we're eliding
            //use XPATH so we can query attributes. NB that this means we'll be using slash-style lookup as in 
            // "processing/paths/excelFilePath" 
            // instead of 
            // "processing.paths.excelFilePath"
            config.setExpressionEngine(new XPathExpressionEngine());
            configurationEnvironment = config.getString("environment/env");
            System.out.println(configurationEnvironment);
            excelFilePath = config.getString("processing[@env='" + configurationEnvironment + "']/paths/excelFilePath");
            bodymediaFileUrl = config.getString("processing[@env='" + configurationEnvironment + "']/paths/bodymediaFileUrl");
            //HierarchicalConfiguration node = (HierarchicalConfiguration) config.configurationAt("/nodes/node[@id='"+(str)+"']");
            List<String> emails = config.getList(String.class, "processing[@env='" + configurationEnvironment + "']/emails/email");
            privateMails = new String[emails.size()];
            privateMails = emails.toArray(privateMails);
            output = true;
        } catch (ConfigurationException cex) {
            //Something went wrong; we should probably check to see if the configuration file wasn't found, 
            // but otherwise just keep the output as false.
            System.out.println(cex.getMessage());
        }
        return output;
    }

    private static void createOptRecursiveDefaultParameters() {
        Y = 189;
        double[] phiArray = new double[]{-300, -166, -162, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0.3,
            0.3, 0.3, 0.3, 0.2, 0.2, 0.2, 0.2, 0};
        double[][] phiDoubleArray = new double[24][1];
        for (int i = 0; i < phiArray.length; i++) {
            phiDoubleArray[i][0] = phiArray[i];
        }
        phi = new Matrix(phiDoubleArray);

        double[][] Q_oldArray = new double[][]{{0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0}
        };
        Q_old = new Matrix(Q_oldArray);

        double[][] P_oldArray = new double[][]{{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
        };
        P_old = new Matrix(P_oldArray);
        lamda_old = 0.5;

        upperlimit = new double[]{1, 1, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1};

        lowerlimit = new double[]{-1, -1, -1, -0.974082841, -0.932309039, -0.847452389, -0.724680039, -0.584206489,
            -0.448368344, -0.333152936, -0.24418079, -0.177141947, -0.122686145, -0.07576685, -0.049439141, -1,
            -1, -1, -1, 0, 0, 0, 0, -1};

    }

    private static void createOptRecursiveSetParametersQP(Matrix Qprev, Matrix Pprev) {
        Y = 189;
        double[] phiArray = new double[]{-300, -166, -162, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0.3,
            0.3, 0.3, 0.3, 0.2, 0.2, 0.2, 0.2, 0};
        double[][] phiDoubleArray = new double[24][1];
        for (int i = 0; i < phiArray.length; i++) {
            phiDoubleArray[i][0] = phiArray[i];
        }
        phi = new Matrix(phiDoubleArray);
        Q_old = Qprev;
        P_old = Pprev;
        lamda_old = 0.5;

        upperlimit = new double[]{1, 1, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1};

        lowerlimit = new double[]{-1, -1, -1, -0.974082841, -0.932309039, -0.847452389, -0.724680039, -0.584206489,
            -0.448368344, -0.333152936, -0.24418079, -0.177141947, -0.122686145, -0.07576685, -0.049439141, -1,
            -1, -1, -1, 0, 0, 0, 0, -1};

    }

}