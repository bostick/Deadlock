import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportLineCounts {
	
	static int emptyLines = 0;
	static int nonEmptyLines = 0;
	static int files = 0;
	
	public static void main(String[] args) throws IOException {
		
		Path start = FileSystems.getDefault().getPath("src", "main", "java");
		
		LineNumberReporter visitor = new LineNumberReporter();
		
		Files.walkFileTree(start, visitor);
		
		System.out.println("non-empty lines: " + nonEmptyLines);
		System.out.println("empty lines: " + emptyLines);
		System.out.println("files: " + files);
		
	}
	
	static class LineNumberReporter implements FileVisitor<Path> {

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			
			BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
			
			files++;
			
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.isEmpty()) {
					emptyLines++;
				} else {
					nonEmptyLines++;
				}
			}
			reader.close();
			
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}
		
	}
	
}
