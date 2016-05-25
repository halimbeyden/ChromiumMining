import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Main {
	private static GitMatrix matrix;
	private static Map<String,Author> authors = new HashMap<String,Author>();
	private static ArrayList<String> files = new ArrayList<String>();
	public static void main(String[] args) throws InterruptedException, IOException {
		showResult();
    }
	private static void showResult(){
		System.out.println("Getting files..");
		  getFiles();
		System.out.println("Getting authors..");
		  getAuthors();
		  try {
				ProjectData.latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Getting commit count..");
		  getCommitCount();  
			System.out.println("Calculating top authors..");
		  calculateTopAuthors();
			System.out.println("Exporting top authors to the file..");
		  exportAuthorsToCSV();
			System.out.println("Matrix is being filled..");
		  matrix = new GitMatrix(files.size(),authors.size());
		  matrix.fillMatrix(files, authors.values().toArray(new Author[authors.size()]));
			System.out.println("Relations between top authors are exporting..");
		  matrix.exporAdjMatrixToCSV("created_matrix");
		  System.out.println("All proccesses are finished. Exported files can be shown in file directory.");
	}
	private static void getFiles() {
		try {  
			 File file = new File(ProjectData.fileName);   
			 Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "git ls-files"},null,file);  
	         BufferedReader in = new BufferedReader(  
	                                    new InputStreamReader(p.getInputStream()));  
	         String line = null;
	         while ((line = in.readLine()) != null) 
	        	 files.add(line);
		  }catch(Exception ex){
			  System.out.println(ex.getMessage());
		  }
	}
	private static void getAuthors(){
		try {  
			 File file = new File(ProjectData.fileName);   
			 Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "git log --pretty=\"%an\" --since=\"6 months ago\""},null,file);  
	         BufferedReader in = new BufferedReader(  
	                                    new InputStreamReader(p.getInputStream()));  
	         String line = null;
	         while ((line = in.readLine()) != null) {
	        	 if(!authors.containsKey(line))
	        		 authors.put(line, new Author(line));
	        	 authors.get(line).CommitCount++;
	         }
			 ProjectData.latch = new CountDownLatch(authors.size());
	         Iterator<Author> iterator = authors.values().iterator();
	         for(int i = 0;i<authors.size();i++){
	        	 ProjectData.semaphore.acquire();
	        	 CommitThread _thread = new CommitThread(iterator.next());
	        	 _thread.start();
	         }
		  }catch(Exception ex){
			  System.out.println(ex.getMessage());
		  }
	}
	private static void getCommitCount() {
		try {  
			 File file = new File(ProjectData.fileName);   
			 Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", ProjectData.commitCountScript},null,file);  
	         BufferedReader in = new BufferedReader(  
	                                    new InputStreamReader(p.getInputStream()));  
	         String line = null;
	         ProjectData.commitCount = Integer.parseInt(in.readLine().trim());
		  }catch(Exception ex){
			  System.out.println(ex.getMessage());
		  }
	}
	private static void calculateTopAuthors(){
		List<Author> authorList = new ArrayList<Author>(authors.values());
		Collections.sort(authorList,new Comparator<Author>(){
			public int compare(Author a1, Author a2) {
		        return a2.CommitCount - a1.CommitCount;
		    }
		});
		int currentCommitCount = 0;
		int index = 0;
		//System.out.println((ProjectData.commitCount*80f/100f));
		while(currentCommitCount < (ProjectData.commitCount*80f/100f)){
			//System.out.println((index+1) + "\t" + authorList.get(index).Name + " --- " + authorList.get(index).CommitCount);
			currentCommitCount+= authorList.get(index).CommitCount;
			index++;
		}
		ProjectData.topAuthorsCount = index;
	}
	private static void printTopEditedFiles(int fileCount){
		try {  
			 File file = new File(ProjectData.fileName);   
			 Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", ProjectData.getTopFilesScript(Math.min(files.size(), fileCount))},null,file);  
			 p.waitFor();
			 BufferedReader in = new BufferedReader(  
	                                    new InputStreamReader(p.getInputStream()));  
	         String line = null;
	         while ((line = in.readLine()) != null) {
	        	 System.out.println(line);
	         }
		  }catch(Exception ex){
			  System.out.println(ex.getMessage());
		  }
	}
	private static void exportAuthorsToCSV(){
		try {
			List<Author> authorList = new ArrayList<Author>(authors.values());
			FileWriter writer = new FileWriter("authors.csv");
			for(int i=0;i<authorList.size();i++){
				writer.append(authorList.get(i).Name + ";" + authorList.get(i).CommitCount);
				if(i!= authorList.size()-1)
					writer.append("\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
