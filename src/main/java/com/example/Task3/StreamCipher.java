void main() {
    String filePath = "";

    try (Scanner scanner = new Scanner(System.in)) {
        System.out.print("Enter file path: ");
        filePath = scanner.nextLine().trim();
    }

    String encrypted = "src/main/resources/static/encrypted.txt";
    String decrypted = "src/main/resources/static/decrypted.txt";
    char keyChar = '\u0001';

    try {
        encrypt(filePath, encrypted, keyChar);
        System.out.println("File encrypted into " + encrypted);
        decrypt(encrypted, decrypted, keyChar);
        System.out.println("File decrypted into " + decrypted);
    } catch (IOException e) {
        System.err.println("File reading error: " + e.getMessage());
    }
}

static class EncryptFilterWriter extends FilterWriter{
    private final char key;

    protected EncryptFilterWriter(Writer out, char key) {
        super(out);
        this.key = key;
    }

    @Override
    public void write(int c) throws IOException {
        super.write((char) (c + key));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] encryptedChars = new char[len];
        for (int i = 0; i < len; i++) {
            encryptedChars[i] = (char) (cbuf[off + i] + key);
        }
        super.write(encryptedChars, 0, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }

}

static class DecryptFilterReader extends FilterReader {
    private final char key;

    protected DecryptFilterReader(Reader in, char key) {
        super(in);
        this.key = key;
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        return (c == -1) ? -1 : (char) (c - key);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int count = super.read(cbuf, off, len);
        if (count == -1) {
            return -1;
        }
        for (int i = off; i < off + count; i++) {
            cbuf[i] = (char) (cbuf[i] - key);
        }
        return count;
    }
}

public static void encrypt(String sourceFile, String encryptedFile, char keyChar) throws IOException {
    try (Reader reader = new FileReader(sourceFile, StandardCharsets.UTF_8);
         Writer writer = new EncryptFilterWriter(new FileWriter(encryptedFile, StandardCharsets.UTF_8), keyChar)) {
        char[] buffer = new char[1024];
        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, bytesRead);
        }
    }
}

public static void decrypt(String encryptedFile, String decryptedFile, char keyChar) throws IOException {
    try (Reader reader = new DecryptFilterReader(new FileReader(encryptedFile, StandardCharsets.UTF_8), keyChar);
         Writer writer = new FileWriter(decryptedFile, StandardCharsets.UTF_8)) {
        char[] buffer = new char[1024];
        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, bytesRead);
        }
    }
}