package com.cecs571.spaqrlqueryengine;

import com.webfirmframework.wffweb.tag.html.tables.Table;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class OutputGenerator {

    private String fileName;
    private String fileURI;
    private HTMLFactory html;

    /**
     * Output generator for the SPARQL resultset
     *
     * @param fileName name of the output file
     */
    public OutputGenerator(String fileName) {
        this.fileName = fileName;
        setFileURI();
    }

    /**
     * Define the output file name.
     *
     * @param fileName name of the output file.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        setFileURI();
    }

    /**
     * Write the values for variables retrieved from the SPQRQL result set into
     * the file.
     *
     * @param resultSet result from a SPARQL query execution instance.
     * @param queryVars variables to be retrieved from the result set and
     * written to the file.
     *
     */
    public void writeToFile(ResultSet resultSet, String[] queryVars) {
        generateHTML(resultSet, queryVars);
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(
                new FileOutputStream(new File(fileURI)))) {
            fileWriter.write(html.toHtmlString());
        } catch (IOException ex) {
            Logger.getLogger(OutputGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens the file after it is written by this instance.
     */
    public void openFileInDefaultApp() {
        try {
            if (fileName == null || fileName.equals("")) {
                throw new FileNotFoundException("Define file name with 'setFileName' method: ");
            }
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(fileURI));
        } catch (IOException ex) {
            Logger.getLogger(OutputGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create an HTML file containing data from the resultset.
     *
     * @param resultSet result from a SPARQL query execution instance.
     * @param queryVars variables to be retrieved from the result set and
     * written to the file.
     */
    private void generateHTML(ResultSet resultSet, String[] queryVars) {
        // init HTML file
        html = new HTMLFactory();

        // add table
        Table table = html.addTable();

        // populate header
        int i = 0, numOfVars = queryVars.length;
        html.addHeader(table, queryVars);

        // populate table rows
        String[] row = new String[numOfVars];
        while (resultSet.hasNext()) {
            QuerySolution sol = resultSet.nextSolution();
            for (String var : queryVars) {
                row[i] = removeType(sol.get(var).toString());
                i = (i + 1) % numOfVars;
            }
            html.addRow(table, row);
        }
    }

    /**
     * Remove the type from the query results
     * @param value
     * @return 
     */
    public String removeType(String value) {
        return value.split("\\^")[0];
    }

    /**
     * Generate the URI for the output file based on the file name.
     */
    private void setFileURI() {
        if (this.fileName.contains(".")) {
            this.fileURI = Paths.get("").toAbsolutePath().toString() + "/" + fileName;
        } else {
            this.fileURI = Paths.get("").toAbsolutePath().toString() + "/" + fileName + OutputFormat.HTML;
        }
    }
}
