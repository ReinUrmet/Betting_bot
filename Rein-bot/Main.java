import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

public class Main {

    static final String CATEGORY = "DIY";
    static final double SCORE_THRESHOLD = 0.6;
    static final double BID_MULTIPLIER = 36;

    static long budget;
    static long initialBudget;
    static long totalSpent = 0;
    static int roundNum = 0;
    static final Map<String, String> fieldMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Missing budget argument");
            return;
        }

        budget = Long.parseLong(args[0]);
        initialBudget = budget;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

        out.write(CATEGORY + "\n");
        out.flush();

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.contains("video.category")) {
                roundNum++;
                int[] bid = makingBets(line);
                out.write(bid[0] + " " + bid[1] + "\n");
                out.flush();

            } else if (line.startsWith("W ")) {
                long cost = Long.parseLong(line.split(" ")[1]);
                budget -= cost;
                totalSpent += cost;

            } else if (line.startsWith("L")) {
                // lost, nothing to do

            } else if (line.startsWith("S ")) {
                String[] parts = line.split(" ");
                long points = Long.parseLong(parts[1]);
                long spent = Long.parseLong(parts[2]);
                double efficiency = spent > 0 ? (double) points / spent : 0;
                System.err.println("Summary: points=" + points + " spent=" + spent
                        + " efficiency=" + String.format("%.4f", efficiency)
                        + " totalSpent=" + totalSpent
                        + " budget=" + budget);

            } else {
                System.err.println("Unknown line: " + line);
            }
        }
    }

    public static int[] makingBets(String line) {
        double score = scoreGenerator(line);

        // If score is below threshold, still send a tiny bid so we participate
        if (score < SCORE_THRESHOLD) {
            return new int[]{2, 2};
        }

        // For good impressions: scale maxBid by score.
        int maxBid = (int) Math.min(score * BID_MULTIPLIER, budget);
        int startBid = 1;

        return new int[]{startBid, maxBid};
    }

    public static double scoreGenerator(String line) {
        double score = 0;

        fieldMap.clear();
        String[] parts = line.split(",");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                fieldMap.put(kv[0], kv[1]);
            }
        }

        // Viewer interests - ordered by relevance, first matters most
        String interests = fieldMap.getOrDefault("viewer.interests", "");
        String[] interestList = interests.isEmpty() ? new String[0] : interests.split(";");
        for (int i = 0; i < interestList.length; i++) {
            if (interestList[i].trim().equals(CATEGORY)) {
                if (i == 0) score += 1.0;
                else if (i == 1) score += 0.5;
                else score += 0.2;
            }
        }

        // Video category match
        if (CATEGORY.equals(fieldMap.get("video.category"))) score += 1.0;

        // Gender - DIY skews male
        if ("M".equals(fieldMap.get("viewer.gender"))) score += 0.2;

        // Engagement ratio
        long views = Long.parseLong(fieldMap.getOrDefault("video.viewCount", "1"));
        long comments = Long.parseLong(fieldMap.getOrDefault("video.commentCount", "0"));
        double engagement = views > 0 ? (double) comments / views : 0.0;
        score += Math.min(engagement * 10, 1.0);

        // Subscribed viewers are worth more
        if ("Y".equals(fieldMap.get("viewer.subscribed"))) score += 0.5;

        // Age - DIY audience skews 25-54
        String age = fieldMap.getOrDefault("viewer.age", "");
        if (age.equals("25-34") || age.equals("35-44")) {
            score += 0.5;
        } else if (age.equals("45-54") || age.equals("55+")) {
            score += 0.4;
        } else if (age.equals("18-24")) {
            score += 0.1;
        } else {
            score -= 0.3;
        }

        return score;
    }
}