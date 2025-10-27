/**************************************************
 * @file: Parser.java
 * @description: Reads commands and CSV dataset, fills a BST of HorrorMovie objects.
 * @author: Ben Martin
 * @date: October 26, 2025
 **************************************************/

import java.io.*;
import java.util.Scanner;

public class Parser
{

    // BST of HorrorMovie objects
    private final BST<HorrorMovie> mybst = new BST<>();

    public Parser(String filename) throws FileNotFoundException
    {
        // Clear the output file before processing commands
        System.out.println("Working directory: " + new File(".").getAbsolutePath());
        clearFile("output.txt");
        process(new File(filename));
    }

    // Reads the command file (like input.txt)
    public void process(File input) throws FileNotFoundException
    {
        Scanner sc = new Scanner(input);

        while (sc.hasNextLine())
        {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            // Collapse multiple spaces/tabs into one
            line = line.replaceAll("\\s+", " ");
            String[] command = line.split(" ");

            operate_BST(command);
        }

        sc.close();
    }

    // Interpret the command and operate on the BST
    public void operate_BST(String[] command)
    {
        if (command.length == 0) return;

        String action = command[0].toLowerCase();

        switch (action)
        {
            case "csv" ->
            {
                if (command.length < 2)
                {
                    writeToFile("Invalid Command", "output.txt");
                    return;
                }
                loadCsv(command[1]);
            }

            case "print" ->
            {
                writeToFile(mybst.toString(), "output.txt");
            }

            case "search" ->
            {
                if (command.length < 2)
                {
                    writeToFile("Invalid Command", "output.txt");
                    return;
                }

                String title = command[1].trim();
                boolean found = mybst.search(new HorrorMovie(title, 0.0));
                writeToFile(found ? "found " + title : "search failed", "output.txt");
            }

            default ->
            {
                writeToFile("Invalid Command", "output.txt");
            }
        }
    }

    // Reads a CSV file and loads HorrorMovie objects into the BST
    private void loadCsv(String csvPath)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath)))
        {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null)
            {
                // Properly split CSV line while respecting quoted commas
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Defensive parsing in case of missing values
                String title = cols.length > 2 ? cols[2].replace("\"", "").trim() : "";
                double rating = parseDoubleSafe(cols, 10);

                // Skip empty titles or zero ratings
                if (title.isEmpty() || rating == 0.0) continue;

                HorrorMovie movie = new HorrorMovie(title, rating);
                mybst.insert(movie);
            }

            writeToFile("CSV Loaded: " + csvPath, "output.txt");
        }
        catch (IOException e)
        {
            writeToFile("Error loading CSV: " + e.getMessage(), "output.txt");
        }
    }

    // Helpers to safely parse numbers
    private double parseDoubleSafe(String[] cols, int index)
    {
        try
        {
            return Double.parseDouble(cols[index].trim());
        }
        catch (Exception e)
        {
            return 0.0;
        }
    }

    // Append output to a text file
    public void writeToFile(String content, String filePath)
    {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            out.println(content);
        }
        catch (IOException e)
        {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Clears the output file before starting
    private void clearFile(String filePath)
    {
        try (PrintWriter pw = new PrintWriter(filePath))
        {
            pw.print(""); // clears content
        }
        catch (IOException e)
        {
            System.out.println("Could not clear file: " + e.getMessage());
        }
    }
}
