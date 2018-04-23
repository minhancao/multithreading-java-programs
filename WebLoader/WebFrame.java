import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.table.DefaultTableModel;

public class WebFrame extends JFrame {
   ArrayList<String> urls = new ArrayList<String>(0);
   int numOfThreadsRunning = 0;
   int numOfWorkerThreads = 0;
   JTable table;
   JLabel label = new JLabel("Number of threads running: ");
   JLabel label4 = new JLabel("Number of worker threads completed: ");
   long duration = 0;
   JLabel label2 = new JLabel("Elapsed Runtime of Download: ");
   JLabel label3 = new JLabel("State: Ready");
   JProgressBar bar = new JProgressBar(0);
   int barProgress = 0;
   boolean running = false;
   Semaphore s;
   LauncherThread thread;
   JTextField limit = new JTextField();
   
   public WebFrame() {
   }

   public static void main(String[] args) {
      WebFrame frame = new WebFrame();
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader("test.txt")); // put args[0] here
      } catch (FileNotFoundException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      String a;
      try {
         while ((a = br.readLine()) != null) {
            frame.urls.add(a);
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      frame.bar.setMaximum(frame.urls.size());// sets maximum for progressbar

      DefaultTableModel model = new DefaultTableModel(new String[] { "url", "status" }, 0);
      frame.table = new JTable(model);
      frame.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

      for (int i = 0; i < frame.urls.size(); i++) {
         model.addRow(new String[] { frame.urls.get(i), "" });
      }

      ArrayList<WebWorker> ww = new ArrayList<WebWorker>(0);
      JButton fetch = new JButton("Single Thread Fetch");
      fetch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (!frame.running) {
               frame.duration = 0;
               frame.label2.setText("Elapsed Runtime of Download: ");
               frame.numOfWorkerThreads = 0;
               frame.numOfThreadsRunning = 0;
               frame.label4.setText("Number of worker threads completed: " + frame.numOfWorkerThreads);
               frame.label.setText("Number of threads running: " + frame.numOfThreadsRunning);
               frame.label3.setText("State: Running");
               frame.bar.setValue(0);
               ww.clear();
               frame.running = true;
               frame.barProgress = 0;
               frame.bar.setMaximum(frame.urls.size());
               frame.bar.setStringPainted(true);
               for (int i = 0; i < frame.urls.size(); i++) {
                  model.setValueAt("Waiting to start...", i, 1); // WHERE DO WE PUT THIS THING, we're here
                  WebWorker w = new WebWorker(frame.urls.get(i), i, frame);
                  ww.add(w);
               }
               frame.s = new Semaphore(2);
               frame.thread = new LauncherThread(ww, frame); // "special" launcher thread, limit at 1
                                                            // worker
               try {
                  frame.s.acquire();
                  frame.thread.start();
               } catch (InterruptedException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }  
            }
            
         }
      });
   
      JButton stop = new JButton("Stop");
      stop.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if(frame.running) {
               frame.duration = 0;
               frame.label2.setText("Elapsed Runtime of Download: ");
               frame.label3.setText("State: Ready");
               frame.numOfThreadsRunning = 0; // decrement the launcher thread, threadsrunning should now be 0
               frame.label.setText("Number of threads running: " + frame.numOfThreadsRunning);
               frame.numOfWorkerThreads = 0;
               frame.label4.setText("Number of worker threads completed: " + frame.numOfWorkerThreads);
               frame.running = false;
               frame.bar.setValue(0);
               frame.thread.stop();
            for (int i = 0; i < ww.size(); i++) {
               ww.get(i).stop();
               model.setValueAt("Stopped", i, 1);
            }
         }  
         }
      });

      JButton fetchCon = new JButton("Concurrent Fetch");
      fetchCon.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (!frame.running) {
               frame.duration = 0;
               frame.label2.setText("Elapsed Runtime of Download: ");
               frame.numOfWorkerThreads = 0;
               frame.numOfThreadsRunning = 0;
               frame.label4.setText("Number of worker threads completed: " + frame.numOfWorkerThreads);
               frame.label.setText("Number of threads running: " + frame.numOfThreadsRunning);
               frame.label3.setText("State: Running");
               frame.bar.setValue(0);
               ww.clear();
               frame.running = true;
               frame.barProgress = 0;
               frame.bar.setMaximum(frame.urls.size());
               frame.bar.setStringPainted(true);
               for (int i = 0; i < frame.urls.size(); i++) {
                  model.setValueAt("Waiting to start...", i, 1); // WHERE DO WE PUT THIS THING, we're here
                  WebWorker w = new WebWorker(frame.urls.get(i), i, frame);
                  ww.add(w);
               }
               
               frame.s = new Semaphore(Integer.parseInt(frame.limit.getText())+1);
               frame.thread = new LauncherThread(ww, frame); // "special" launcher thread, limit is at
                                                            // 3
                                                            // for now
               try {
                  frame.s.acquire();
                  frame.thread.start();
               } catch (InterruptedException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }
               
               
            }
         }
      });

      JPanel listPane = new JPanel();
      listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
      JScrollPane scrollpane = new JScrollPane(frame.table);
      scrollpane.setPreferredSize(new Dimension(600, 300));
      scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
      frame.bar.setAlignmentX(Component.LEFT_ALIGNMENT);
      fetch.setAlignmentX(Component.LEFT_ALIGNMENT);
      fetchCon.setAlignmentX(Component.LEFT_ALIGNMENT);
      stop.setAlignmentX(Component.LEFT_ALIGNMENT);
      listPane.add(frame.label3);
      listPane.add(scrollpane);
      listPane.add(frame.bar);
      listPane.add(fetch);
      frame.limit.setPreferredSize(new Dimension(100,20));
      JLabel labelLimit = new JLabel("Limit for concurrent threads: ");
      JPanel fetchConPane = new JPanel();
      fetchConPane.setLayout(new FlowLayout(FlowLayout.LEFT));
      fetchConPane.add(fetchCon);
      fetchConPane.add(labelLimit);
      fetchConPane.add(frame.limit);
      fetchConPane.setAlignmentX(Component.LEFT_ALIGNMENT);
      listPane.add(fetchConPane);
      listPane.add(stop);
      listPane.add(frame.label);
      listPane.add(frame.label4);
      listPane.add(frame.label2);

      frame.add(listPane);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
   }
}
