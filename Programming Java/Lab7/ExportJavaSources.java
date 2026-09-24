import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class ExportJavaSources {
    public static void main(String[] args) {
        Path root = Paths.get(".").toAbsolutePath().normalize(); // корень проекта
        Path out = root.resolve("all_sources.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(out,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".java"))
                        .sorted()
                        .forEach(p -> dumpFile(writer, p));
            }

            System.out.println("Готово: " + out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dumpFile(BufferedWriter writer, Path file) {
        try {
            String absPath = file.toAbsolutePath().toString();
            writer.write("--- Start of " + absPath + " ---\n");
            Files.lines(file, StandardCharsets.UTF_8).forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            writer.write("--- End of " + absPath + " ---\n\n");
        } catch (IOException e) {
            System.err.println("Ошибка с файлом " + file + ": " + e.getMessage());
        }
    }
}
