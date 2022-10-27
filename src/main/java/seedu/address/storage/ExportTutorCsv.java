package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import seedu.address.commons.util.FileUtil;
import seedu.address.model.person.tutor.Tutor;

/**
 * A class to export {@link Tutor} to a csv-readable format.
 */
public class ExportTutorCsv {
    private final Path jsonFilePath;
    private final Path csvFilePath = Paths.get("data", "tutors.csv");

    public ExportTutorCsv(Path jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    /**
     * Reads the .json file and generates a new .csv file.
     * @throws IOException if an error occurred when creating new .csv file,
     *      reading .json file, or writing to .csv file.
     */
    public void generateCsv() throws IOException {
        FileUtil.createFile(this.csvFilePath);
        JsonNode jsonTree = new ObjectMapper().readTree(this.jsonFilePath.toFile());
        createNewCsv(jsonTree);
    }

    /**
     * Creates a .csv file from the {@code JsonNode} object.
     * @param jsonTree {@code JsonNode} object of the .json file.
     * @throws IOException if an error occurred when writing to .csv file.
     */
    private void createNewCsv(JsonNode jsonTree) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        SequenceWriter seqW = csvMapper.writer().writeValues(this.csvFilePath.toFile());
        seqW.write(Arrays.asList("name", "phone", "email", "address", "qualification", "institution", "tagged",
                "tuitionClasses"));
        if (jsonTree.isNull()) {
            seqW.close();
            return;
        }

        ArrayList<JsonNode> list = new ArrayList<>();
        jsonTree.elements().next().elements().forEachRemaining(list::add);
        for (JsonNode item : list) {
            ArrayList<String> arr = new ArrayList<>();
            item.fields().forEachRemaining(header -> {
                switch (header.getKey()) {
                case "tagged":
                    arr.add((handleTagged(header.getValue())));
                    break;
                case "tuitionClasses":
                    arr.add(handleTuitionClass(header.getValue()));
                    break;
                default:
                    arr.add(header.getValue().textValue());
                }
            });
            seqW.write(arr);
        }
        seqW.close();
    }

    /**
     * Transforms {@code JsonNode} of tuitionClasses field to a string.
     * @param fullClassList JsonNode of tuitionClasses that contains additional information
     *                      and cannot be represented by a string properly.
     * @return A string representation of an {@code ArrayList} containing the names of the tuitionClass.
     */
    private String handleTuitionClass(JsonNode fullClassList) {
        ArrayList<String> arr = new ArrayList<>();
        fullClassList.elements().forEachRemaining(classes -> classes.fields().forEachRemaining(id -> {
            if (id.getKey().equals("name")) {
                arr.add(id.getValue().textValue());
            }
        }));
        return arr.toString();
    }

    /**
     * Transforms {@code JsonNode} of tagged field to a string.
     * @param tagged {@code JsonNode} of tagged that cannot be represented by a string properly.
     * @return A string representation of an {@code ArrayList} containing the tags of a tutor.
     */
    private String handleTagged(JsonNode tagged) {
        ArrayList<String> arr = new ArrayList<>();
        tagged.elements().forEachRemaining(values -> arr.add(values.textValue()));
        return arr.toString();
    }
}


