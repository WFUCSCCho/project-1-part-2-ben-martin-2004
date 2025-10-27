/**
 * @file: Proj1.java
 * @description: Main class for CSC 201 Project 1. Invokes the Parser class to process commands for a BST.
 * @author: Ben Martin
 * @date: October 26, 2025
 */

import java.io.FileNotFoundException;

public class Proj1
{
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage: java Proj1 <inputfile>");
            return;
        }

        String filename = args[0];

        try
        {
            new Parser(filename); // runs the parser
            System.out.println("Processing complete. See output.txt for output.");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error: File not found - " + filename);
        }
    }
}