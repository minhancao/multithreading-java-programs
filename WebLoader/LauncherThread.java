import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class LauncherThread extends Thread {
   ArrayList<WebWorker> workers;
   int limit = 3;
   int i = 0;
   WebFrame f;

   public LauncherThread(ArrayList<WebWorker> ww, WebFrame g) {
      // TODO Auto-generated constructor stub
      workers = ww;
      f = g;
      f.numOfThreadsRunning++; // count the launcherthread as one of the
                           // running threads
      f.label.setText("Number of threads running: " + f.numOfThreadsRunning);
   }

   public void run()// start all the workers
   {
      long startTime = System.nanoTime();
      boolean done = false;
      int count = 0;
      int j = 2;
      try {
         // now accessing the shared resource
         for (int i = 0; i < workers.size(); i++) {
            System.out.println("Thread " + j + " is trying to get a permit");
            f.s.acquire();
            System.out.println("Thread " + j + " acquired permit");
            f.table.setValueAt("Downloading...", i, 1);
            j++;
            workers.get(i).start();
         }

         for (int i = 0; i < workers.size(); i++) {
            // this code is here because when i try to reset the progress bar back to 0%
            // this launcher thread run first and set the progress bar to 0% then one of the worker threads would set it to 100% because it's the last download
            workers.get(i).join();
         }
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      f.duration = System.nanoTime() - startTime;
      f.label2.setText("Elapsed Runtime of Download: " + (double)f.duration/1000000000 + " seconds");
      f.numOfThreadsRunning--; // decrement the launcher thread, threadsrunning should now be 0
      f.label.setText("Number of threads running: " + f.numOfThreadsRunning);
      f.bar.setValue(0);
      f.running = false; // running is done
      f.numOfWorkerThreads = 0;
      f.label3.setText("Number of worker threads completed: " + f.numOfWorkerThreads);
      f.label3.setText("State: Ready");
   }
}
