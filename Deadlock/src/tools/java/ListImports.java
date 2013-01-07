import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListImports {
	
	static Set<String> imports = new HashSet<String>();
	
	public static void main(String[] args) throws IOException {
		
		Path start = FileSystems.getDefault().getPath("..", "Deadlock", "src", "main", "java");
		
		ImportReporter visitor = new ImportReporter();
		
		Files.walkFileTree(start, visitor);
		
		List<String> imports2 = new ArrayList<String>(imports);
		
		Collections.sort(imports2);
		
		for (String i : imports2) {
			System.out.println(i);
		}
		
	}
	
	static class ImportReporter implements FileVisitor<Path> {

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			
			BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
			
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("import")) {
					imports.add(line);
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
