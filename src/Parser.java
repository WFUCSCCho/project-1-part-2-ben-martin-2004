/**************************************************
 * @file: Parser.java
 * @description: Reads commands and CSV dataset, fills a BST of HorrorMovie objects.
 * @author: Ben Martin
 * @date: October 26, 2025
 **************************************************/

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

public class Parser
{

    // BST of HorrorMovie objects
    private final BST<HorrorMovie> mybst = new BST<>();

    public Parser(String filename) throws FileNotFoundException
    {
        // Clear the output file before processing commands
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

                // Combine tokens after "search" into one title string
                String title = String.join(" ", Arrays.copyOfRange(command, 1, command.length)).trim();

                // Normalize (remove quotes and punctuation)
                String normalized = title.replaceAll("[\"',.?!]", "").trim().toLowerCase();

                boolean found = false;
                for (HorrorMovie m : mybst)
                {
                    String movieTitle = m.title().replaceAll("[\"',.?!]", "").trim().toLowerCase();
                    if (movieTitle.equals(normalized))
                    {
                        found = true;
                        break;
                    }
                }

                writeToFile(found ? "found " + title : "not found", "output.txt");
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
            int count = 0;

            while ((line = br.readLine()) != null)
            {
                // Split safely
                String[] cols = line.split(",");

                // Defensive parsing in case of missing values
                String title = cols.length > 2 ? cols[2].trim() : "";
                double rating = parseDoubleSafe(cols, 10); // Using column 11 (index 10)

                if (!title.isEmpty())
                {
                    HorrorMovie movie = new HorrorMovie(title, rating);
                    mybst.insert(movie);
                    count++;
                }
            }

            writeToFile("CSV Loaded: " + csvPath + " (" + count + " movies)", "output.txt");
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
