import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    //Constants
    static final String CATEGORY = "Music";
    static final boolean TEST_MODE = true;


    public static void main(String[] args) throws InterruptedException {

        //Main logic
        if (TEST_MODE) {
            runLocalTest();
            return;
        }

        if (args.length == 0) {
            System.out.println("Missing budget argument");
            return;
        }
        long budget = Long.parseLong(args[0]);

        Scanner scanner = new Scanner(System.in);
        System.out.println(CATEGORY);
        System.out.flush();

        while(scanner.hasNextLine()){
            //Nüüd pean vaatama mis line on
            //Näide round trippist:
            // → ASMR
            //←video.category=Kids,video.viewCount=12345,video.commentCount=987,viewer.subscri
            //bed=Y,viewer.age=25-34,viewer.gender=F,viewer.interests=Video Games;Music
            //→ 5 51
            //← W 12

            String line = scanner.nextLine().trim();

            if (line.startsWith("video.")){
                List<Integer> answers = makingBets(line);
                int minValue = answers.get(0);
                int maxvalue = answers.get(1);

                System.out.println(minValue + " " + maxvalue);
                System.out.flush();


            } else if (line.startsWith("W")) {
                long cost = Long.parseLong(line.split(" ")[1]);
                budget -= cost;
            } else if (line.startsWith("L")) {
                //Lost :(
            } else if (line.startsWith("S")) {
                //Summary


            }

        }

    }

    public static void runLocalTest() {
        try (BufferedReader reader = new BufferedReader(new FileReader("./test.txt"))) {
            String testLine;

            while ((testLine = reader.readLine()) != null) {
                testLine = testLine.trim();

                if (testLine.startsWith("video.")) {
                    double score = scoreGenerator(testLine);
                    System.out.println("INPUT: " + testLine);
                    System.out.println("SCORE: " + score);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Betting function
    public static List<Integer> makingBets(String line){

        List<Integer> answers = new ArrayList<>();
        double score = scoreGenerator(line);



        return answers;
    }

    //Scoring function
    public static double scoreGenerator(String line){
        double score = 0;
        //Siin hakkan lugema igat asja
        Map<String, String> map = new HashMap<>();

        String[] parts = line.split(",");

        for (String part : parts) {
            String[] keyValuePairs = part.split("=", 2);

            if (keyValuePairs.length == 2) {
                map.put(keyValuePairs[0], keyValuePairs[1]);
            }
        }

        System.out.println(map);

        //Nüüd tuleb scorimis loogika

        //User interests / video category part:
        String interests = map.get("viewer.interests");
        if (interests.contains(";")){
            for (String interest: interests.split(";")){

            }
        } else {

        }
        System.out.println(interests);

        //User gender:

        //Comment count:

        //view count:

        //User subscribed (Y/N)

        //user age


        return score;
    }



}

