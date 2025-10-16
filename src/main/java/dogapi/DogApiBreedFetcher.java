package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // TODO Task 1: Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.

        // Defensive check – treat null/blank as "not found" per assignment’s mapping rule
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }

        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase(Locale.ROOT) + "/list";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null || !response.isSuccessful()) {
                throw new BreedNotFoundException(breed);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            // API returns { status: "success", message: [ ... ] } on success
            // and { status: "error", message: "...", code: 404 } on invalid breed
            String status = json.optString("status", "");
            if (!"success".equalsIgnoreCase(status)) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray message = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>(message.length());
            for (int i = 0; i < message.length(); i++) {
                subBreeds.add(message.getString(i));
            }
            return subBreeds;
        } catch (Exception e) {
            // Map any failure (IO/JSON/etc.) to BreedNotFoundException as required
            throw new BreedNotFoundException(breed);
        }

        // return statement included so that the starter code can compile and run.
        // return new ArrayList<>();
    }
}