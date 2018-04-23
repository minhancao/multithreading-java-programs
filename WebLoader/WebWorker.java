import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JTable;

public class WebWorker extends Thread {
   /*
    * This is the core web/download i/o code...
    */
   String urlString;
   int rowNum;
   WebFrame f;

   public WebWorker(String url, int r, WebFrame g) {
      urlString = url;
      rowNum = r;
      f = g;
   }

   public void run() {
      System.out.println(this.getName() + "Fetching...." + urlString);
      f.numOfThreadsRunning++; // increment the running threads count
      f.label.setText("Number of threads running: " + f.numOfThreadsRunning);
      try {
         download();
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void download() throws InterruptedException {
      InputStream input = null;
      StringBuilder contents = null;
      try {
         URL url = new URL(urlString);
         URLConnection connection = url.openConnection();

         // Set connect() to throw an IOException
         // if connection does not succeed in this many msecs.
         connection.setConnectTimeout(5000);

         connection.connect();
         input = connection.getInputStream();

         BufferedReader reader = new BufferedReader(new InputStreamReader(input));

         char[] array = new char[1000];
         int len;
         contents = new StringBuilder(1000);
         while ((len = reader.read(array, 0, array.length)) > 0) {
            System.out.println(this.getName() + "Fetching...." + urlString + len);
            contents.append(array, 0, len);
            Thread.sleep(100);
         }

         System.out.print(contents.toString());
         
         PrintWriter out = new PrintWriter("url" + rowNum + ".txt");
         out.write(contents.toString());
         out.close();
         PrintWriter out2 = new PrintWriter("url" + rowNum + ".html"); 
         out2.write(contents.toString());
         out2.close();
         throw new InterruptedException();

      }
      // Otherwise control jumps to a catch...
      catch (MalformedURLException ignored) {
         System.out.println("Exception: " + ignored.toString());
      } catch (InterruptedException exception) {
         // YOUR CODE HERE
         // deal with interruption
         f.barProgress++;
         f.bar.setValue(f.barProgress);
         f.table.setValueAt("Done", rowNum, 1);
         f.numOfThreadsRunning--;
         f.numOfWorkerThreads++;
         f.label.setText("Number of threads running: " + f.numOfThreadsRunning);
         f.label4.setText("Number of worker threads completed: " + f.numOfWorkerThreads);
         System.out.println("Done");
         System.out.println("Exception: " + exception.toString());
         System.out.println(this.getName() + " releasing a permit...");
         f.s.release();
      } catch (IOException ignored) {
         System.out.println("Exception: " + ignored.toString());
      }
      // "finally" clause, to close the input stream
      // in any case
      finally {
         try {
            if (input != null)
               input.close();
         } catch (IOException ignored) {
         }
      }
   }
}
