void main() {
    try (Scanner scanner = new Scanner(System.in)) {
        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine().trim();
        findLineWithMaxWords(filePath);
    }
}

public static void findLineWithMaxWords(String filePath) {
    String maxLine = "";
    int maxWords = 0;
    int maxLineNumber = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
        String currentLine;
        int lineNumber = 0;

        while ((currentLine = reader.readLine()) != null) {
            lineNumber++;
            String trimmedLine = currentLine.trim();
            int wordCount = trimmedLine.isEmpty() ? 0 : trimmedLine.split("\\s+").length;

            if (wordCount > maxWords) {
                maxWords = wordCount;
                maxLine = currentLine;
                maxLineNumber = lineNumber;
            }
        }

        if (maxLineNumber == 0) {System.out.println("File is empty or doesn't contain words");}
        else {
            System.out.println("Line " + maxLineNumber + " with " + maxWords + " words:");
            System.out.println(maxLine);
        }

    } catch (IOException e) {
        System.err.println("File reading error: " + e.getMessage());
    }
}