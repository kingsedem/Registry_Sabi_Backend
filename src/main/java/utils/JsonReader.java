package utils;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;



public class JsonReader {

//        public static String getTestData(String key) throws IOException, ParseException {
//            String testDataValue;
//            return  testDataValue = (String) getJsonData().get(key);  //input is the key
//
//        }

    public static String getTestData(String key) {
        String testDataValue = null;
        try {
            testDataValue = (String) getJsonData().get(key); // input is the key
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return testDataValue;
    }

        public static JSONObject getJsonData() throws IOException, ParseException {
            //pass the path of the testData.json file
            File fileName = new File("resources//TestData//testData.json");

            //convert .json file to string
            String json = FileUtils.readFileToString(fileName, UTF_8);

            //parse the string into object
            Object obj = new JSONParser().parse(json);

            //give jsonObject so that I can return it to the function everytime it got called
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject;
        }
}
