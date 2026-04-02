import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

public class Main {

    //Constants
    static final String CATEGORY = "DIY";
    static final boolean TEST_MODE = false;
    static long budget;


    public static void main(String[] args) throws InterruptedException, IOException {

        //Main logic
        if (TEST_MODE) {
            runLocalTest();
            return;
        }

        if (args.length == 0) {
            System.out.println("Missing budget argument");
            return;
        }
        budget = Long.parseLong(args[0]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

        out.write(CATEGORY + "\n");
        out.flush();

        String line;
        while((line = reader.readLine()) != null){
            line = line.trim();
            //Nüüd pean vaatama mis line on
            //Näide round trippist:
            // → ASMR
            //←video.category=Kids,video.viewCount=12345,video.commentCount=987,viewer.subscri
            //bed=Y,viewer.age=25-34,viewer.gender=F,viewer.interests=Video Games;Music
            //→ 5 51
            //← W 12

            if (line.contains("video.category")){
                List<Integer> answers = makingBets(line);
                int minValue = answers.get(0);
                int maxvalue = answers.get(1);

                out.write(minValue + " " + maxvalue + "\n");
                out.flush();

            } else if (line.startsWith("W")) {
            try {
                long cost = Long.parseLong(line.split(" ")[1]);
                budget -= cost;
            } catch (Exception e) {
                System.err.println("Error parsing W line: " + line);
            }
            } else if (line.startsWith("L")) {
                //Lost :(
            } else if (line.startsWith("S")) {
                //Summary
            } else {
            System.err.println("Unknown line: " + line);
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
        //Have to tune this more later
        int maxBid = (int) Math.min(score * 38, budget);
        int startBid = (int) Math.min(Math.max(1, maxBid / 2), budget);
        //Ei betti mõtetutele kohtadele
        if (score < 0.2) {
            answers.add(0);
            answers.add(0);
            return answers;
        }  else {
            answers.add(startBid);
            answers.add(maxBid);
        }
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

        //Nüüd tuleb scorimis loogika

        //User interests
        String interests = map.getOrDefault("viewer.interests", "");
        String[] interestList = interests.isEmpty() ? new String[0] : interests.split(";");
        for (int i = 0; i < interestList.length; i++) {
            if (interestList[i].trim().equals(CATEGORY)) {
                if (i == 0) score += 1.0;
                else if (i == 1) score += 0.5;
                else score += 0.2;
            }
        }

        //Video category
        String videoCategory = map.get("video.category");
        if (CATEGORY.equals(videoCategory)) score += 1.0;

        //User gender:
        //Ei tea hetkel kas mõjutab midagi
        if ("M".equals(map.get("viewer.gender"))) score += 0.2;

        //Comment count + view count = user engagement
        long views = Long.parseLong(map.getOrDefault("video.viewCount", "0"));
        long comments = Long.parseLong(map.getOrDefault("video.commentCount", "0"));
        double engagement = views > 0 ? (double) comments / views : 0.0;
        score += Math.min(engagement * 10, 1.0);

        //User subscribed (Y/N)
        if ("Y".equals(map.get("viewer.subscribed"))) score += 0.5;

        //user age
        String age = map.getOrDefault("viewer.age", "");
        if (age.equals("25-34") || age.equals("35-44")) {
            score += 0.5;
        } else if (age.equals("45-54") || age.equals("55+")) {
            score += 0.4;
        } else if (age.equals("18-24")) {
            score += 0.1;
        } else { // 13-17
            score -= 0.3;
        }

        return score;
    }
}