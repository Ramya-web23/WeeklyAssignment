import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class UsernameService {
    private final ConcurrentHashMap<String, Integer> userMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> attemptMap = new ConcurrentHashMap<>();

    public boolean checkAvailability(String username) {
        attemptMap.putIfAbsent(username, new AtomicInteger(0));
        attemptMap.get(username).incrementAndGet();
        return !userMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int i = 1;
        while (suggestions.size() < 3) {
            String candidate1 = username + i;
            String candidate2 = username.replace("_", ".") + i;
            if (!userMap.containsKey(candidate1)) suggestions.add(candidate1);
            if (suggestions.size() < 3 && !userMap.containsKey(candidate2)) suggestions.add(candidate2);
            i++;
        }
        return suggestions;
    }

    public void registerUser(String username, int userId) {
        userMap.put(username, userId);
    }

    public String getMostAttempted() {
        String maxUser = null;
        int maxCount = 0;
        for (Map.Entry<String, AtomicInteger> entry : attemptMap.entrySet()) {
            int count = entry.getValue().get();
            if (count > maxCount) {
                maxCount = count;
                maxUser = entry.getKey();
            }
        }
        return maxUser;
    }

    public static void main(String[] args) {
        UsernameService service = new UsernameService();

        service.registerUser("john_doe", 1);

        System.out.println(service.checkAvailability("john_doe"));
        System.out.println(service.checkAvailability("jane_smith"));
        System.out.println(service.suggestAlternatives("john_doe"));
        System.out.println(service.getMostAttempted());
    }
}