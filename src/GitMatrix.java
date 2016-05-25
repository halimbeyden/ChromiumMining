import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class GitMatrix {
	
	public int matrix[][];
	public ArrayList<String> Files;
	public ArrayList<Author> Authors;

	public GitMatrix(int files,int authors){
		matrix = new int[files][authors];
	}
	public void fillMatrix(ArrayList<String> files,Author[] authors){
		Files = files;
		Authors = new ArrayList<Author>(Arrays.asList(authors));
		Collections.sort(Authors,new Comparator<Author>(){
			public int compare(Author a1, Author a2) {
		        return a2.CommitCount - a1.CommitCount;
		    }
		});
		for(int i = 0;i<Files.size();i++){
			for(int j = 0;j<Authors.size();j++){
				matrix[i][j] = Authors.get(j).containsFile(Files.get(i))?1:0;
			}
		}
	}
	public void print() {
		System.out.print("\t");
		for(int j = 0;j<Authors.size();j++){
			System.out.print(Authors.get(j).Name + "\t");
		}
		for(int i = 0;i<50;i++){
			for(int j = 0;j<matrix[i].length;j++){
				if(j == 0)
					System.out.print(Files.get(i) + "\t");
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println("");
		}
	}
	public void exporAdjMatrixToCSV(String fileName) {
		try
		{
			int size = Math.min(Authors.size(), ProjectData.topAuthorsCount);
			int[][] writingMatrix = new int[size][size];
		    FileWriter writer = new FileWriter(fileName + ".csv");
		    boolean flag = false;
		    for(int i = 0;i<size;i++){
				for(int j = i;j<size;j++){
					flag = false;
					for(int k = 0;k<Files.size();k++){
						if(i!=j && matrix[k][i] == 1 && matrix[k][j] == 1){
							flag = true;
							break;
						}
					}
					writingMatrix[i][j] = flag?1:0;
					writingMatrix[j][i] = flag?1:0;
				}
			}	
		    
		    for(int i = 0;i<size;i++){
				for(int j = 0;j<size;j++){
					writer.append(Integer.toString(writingMatrix[i][j]));
					if(j != size -1)
						writer.append(" ");
				}
			    writer.append('\n');
		    }
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
}
