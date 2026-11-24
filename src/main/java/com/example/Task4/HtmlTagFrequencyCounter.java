void main() {
    String targetUrl;

    try (Scanner scanner = new Scanner(System.in)) {
        System.out.print("Enter URL: ");
        targetUrl = scanner.nextLine().trim();
    }

    try {
        String html = fetchHTML(targetUrl);
        Map<String, Integer> frequencyMap = countTags(html);

        System.out.println("Tags in ascending lexicographical order: ");
        frequencyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("%s: %d%n", entry.getKey(), entry.getValue()));

        System.out.println("\nTags in ascending encounter frequency order: ");
        frequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry -> System.out.printf("%s: %d%n", entry.getKey(), entry.getValue()));

    } catch (Exception e) {
        System.err.println("Page processing error: " + e.getMessage());
    }
}

private static String fetchHTML(String urlString) throws IOException {
    URL url = URI.create(urlString).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    connection.setConnectTimeout(10000);

    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append('\n');
        }
    }
    return content.toString();
}

private static Map<String, Integer> countTags(String html) {
    Map<String, Integer> frequencyMap = new HashMap<>();
    Pattern pattern = Pattern.compile("<([a-zA-Z][a-zA-Z0-9-]*)\\b[^>]*>");
    Matcher matcher = pattern.matcher(html);

    while (matcher.find()) {
        String tagName = matcher.group(1).toLowerCase();
        frequencyMap.merge(tagName, 1, Integer::sum);
    }
    return frequencyMap;
}