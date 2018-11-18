package ohtu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.fluent.Request;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static void main(String[] args) throws IOException {
        // ÄLÄ laita githubiin omaa opiskelijanumeroasi
        String studentNr = "012345678";
        if ( args.length>0) {
            studentNr = args[0];
        }

        String url = "https://studies.cs.helsinki.fi/courses/students/"+studentNr+"/submissions";

		String ohjUrl = "https://studies.cs.helsinki.fi/courses/courseinfo";
		String statsUrl = "https://studies.cs.helsinki.fi/courses/";

        String bodyText = Request.Get(url).execute().returnContent().asString();
        String bodyText2 = Request.Get(ohjUrl).execute().returnContent().asString();

		System.out.println(bodyText2);

        //System.out.println("json-muotoinen data:");
        //System.out.println( bodyText );

        Gson mapper = new Gson();
        Submission[] subs = mapper.fromJson(bodyText, Submission[].class);

        Data[] data = mapper.fromJson(bodyText2, Data[].class);
        
        //System.out.println("Oliot:");
		Map<Integer, List<Submission>> wksubs = new HashMap<>();
        for (Submission submission : subs) {
			wksubs.putIfAbsent(submission.getWeek(), new ArrayList<>());
			wksubs.get(submission.getWeek()).add(submission);
        }

		for (String course : Arrays.stream(data).map(x -> x.getName()).distinct().toArray(String[]::new)) {
			Data courseD = Arrays.stream(data).filter(x -> x.getName().equals(course)).findFirst().get();
			System.out.println(courseD.getFullName());
			ArrayList<Integer> ls = new ArrayList(wksubs.keySet());
			Collections.sort(ls);
			ArrayList<Integer> totals = new ArrayList<>();
			ArrayList<Integer> totals2 = new ArrayList<>();
			ArrayList<Integer> totalsT = new ArrayList<>();
			ls.stream()
				.forEach(x -> {
					System.out.println(" viikko "+x);
					ArrayList<Integer> totalL = new ArrayList<>();
					ArrayList<Integer> totalTL = new ArrayList<>();
					int max = wksubs.get(x).stream().mapToInt(y -> {
						if (y.getCourse().equals(course)) {
							System.out.println("\t"+y);
							totalTL.add(y.getHours());
							totalL.add(y.getExercises().length);
							return y.getExercises().length;	
						}
						return 0;})
						.max().getAsInt();
					int total = totalL.stream().mapToInt(y -> y).sum();
					int totalT = totalTL.stream().mapToInt(y -> y).sum();
					totals.add(total);
					totals2.add(max * totalL.size());
					totalsT.add(totalT);
				});
			System.out.println("yhteensä: "+totals.stream().mapToInt(x -> x).sum()
					+"/"+Arrays.stream(courseD.getExercises()).mapToDouble(x -> x).sum()+" tehtävää "
					+totalsT.stream().mapToInt(x -> x).sum()+" tuntia\n");
			String statsResponse = Request.Get(statsUrl+"/"+course+"/stats").execute().returnContent().asString();
			JsonParser parser = new JsonParser();
			JsonObject parsittuData = parser.parse(statsResponse).getAsJsonObject();
			int pal = 0;
			int teht = 0;
			int aik = 0;
			for (int i = 1; i < parsittuData.size() + 1; i++) {
				JsonObject e = parsittuData.getAsJsonObject(Integer.toString(i));
				pal += e.get("students").getAsInt();
				teht += e.get("exercise_total").getAsInt();
				JsonArray a = e.get("hours").getAsJsonArray();
				for (int j = 0; j < a.size(); j++) {
					try {
					aik += a.get(j).getAsInt();
					} catch (Exception f) {
					}
				}
			}
			System.out.println("Kurssilla yhteensä "+pal+" palautusta, palautettuja tehtäviä "+teht+" kpl, aikaa käytetty yhteensä "+aik+" tuntia\n");
		}	

    }
}
