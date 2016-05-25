import java.util.ArrayList;

public class Author {
	public String Name;
	public int CommitCount;
	public ArrayList<String> Files;
	 public Author(String _name){
		 Name = _name;
		 CommitCount = 0;
		 Files = new ArrayList<String>();
	 }
	 public boolean containsFile(String fileName){
		 return Files.contains(fileName);
	 }
}
