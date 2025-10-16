package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // TODO Task 2: Complete this class
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        if (fetcher == null) {
            throw new IllegalArgumentException("Underlying fetcher cannot be null");
        }
        this.delegate = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.isBlank()) {
            // Treat null/blank as not found; do not cache
            throw new BreedNotFoundException(String.valueOf(breed));
        }

        String key = breed.toLowerCase(Locale.ROOT);

        // Cache hit → return a defensive copy
        if (cache.containsKey(key)) {
            return new ArrayList<>(cache.get(key));
        }

        // Cache miss → call underlying fetcher (and count the call)
        callsMade++;
        List<String> result = delegate.getSubBreeds(breed); // may throw BreedNotFoundException

        // Only cache successful responses
        cache.put(key, new ArrayList<>(result));

        // Return a defensive copy to avoid external modification
        return new ArrayList<>(result);
        // return statement included so that the starter code can compile and run.
        // return new ArrayList<>();
    }

    public int getCallsMade() {
        return callsMade;
    }
}