import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class CommitThread extends Thread {

  Author author;

  public CommitThread(Author _author){
	  author = _author;
  }

  public void run(){
    try {

    	File file = new File(ProjectData.fileName);   
		 Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "git log --pretty=\"%H\" --author=\""+author.Name+"\"| while read commit_hash; do git show --oneline --name-only $commit_hash | tail -n+2; done | sort | uniq"},null,file);  
        BufferedReader in = new BufferedReader(  
                                   new InputStreamReader(p.getInputStream()));  
        String line = null;
        while ((line = in.readLine()) != null) {
       	 author.Files.add(line);
        }
        //System.out.println(author.Name + "\t" + author.CommitCount + "\t" + author.Files.size() );
        ProjectData.latch.countDown();
        ProjectData.semaphore.release();
    } catch(Exception e) {
      System.out.println(e.getMessage());      
    }
  }

}