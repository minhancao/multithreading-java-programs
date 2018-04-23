import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Bank {
    ArrayList<Account> listOfAccs = new ArrayList<Account>(20);
    BlockingQueue<Transaction> queue;
    private final Transaction nullTrans = new Transaction(-1,0,0);

    public Bank() {
        queue = new LinkedBlockingQueue();
        for(int i=0;i<20;i++)
        {
         Account a = new Account();
         listOfAccs.add(a);
           listOfAccs.get(i).id = i;
           listOfAccs.get(i).balance = 1000;
           listOfAccs.get(i).numOfTransactions = 0;
        }
    }

    // TODO: Add code for actually updating accounts. 
    // Should you make this synchronized? Why or why not?
    // If not, how do you avoid concurrency issues?
    public synchronized void processTransaction(Transaction t) throws InterruptedException {
        try {
        listOfAccs.get(t.fromAccount).balance -= t.amount;
        listOfAccs.get(t.fromAccount).numOfTransactions++;
        listOfAccs.get(t.toAccount).balance += t.amount;
        listOfAccs.get(t.toAccount).numOfTransactions++;     
        if(t.fromAccount == t.toAccount) //if fromAcc is same as toAcc, count as 2 transactions
        {
         listOfAccs.get(t.toAccount).numOfTransactions++;
        }
        }
        
        catch(ArrayIndexOutOfBoundsException e)
        {
         throw new InterruptedException();
        }
    }

    private class Worker extends Thread {
        public void run() {
            try {
                Transaction t;
                do {
                    t = queue.take();
                    processTransaction(t);
                } while (t.fromAccount != -1);
            } catch (InterruptedException e) {
            }
        }
    }


    public static void main(String[] args) {
        Bank bank = new Bank();
        
        // TODO: Replace the following with code to generate as
        // many workers as needed. The number of workers is
        // Given as a commandline argument. 
        /*Worker w1 = bank.new Worker();
        Worker w2 = bank.new Worker();*/
        
        int numOfThreadsToCreate = Integer.parseInt(args[1]); //put args[1] here
        ArrayList<Worker> workers = new ArrayList<Worker>(0);
        for(int i=0;i<numOfThreadsToCreate;i++)
        {
           Worker w = bank.new Worker();
           workers.add(w);  
        }

        try {
           for(int i=0;i<workers.size();i++)
           {
              workers.get(i).start();
           }
            /*w1.start();
            w2.start();*/

            // TODO: replace the following with code for 
            // reading from the file and putting the transactions 
            // into the BlockingQueue
            BufferedReader br = null;
            try {
               br = new BufferedReader(new FileReader(args[0])); //put args[0] here
            } catch (FileNotFoundException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            }
            String a;
            try {
               while((a = br.readLine()) != null)
               {
                  String [] split = a.split(" ");
                  Transaction t = new Transaction(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                  bank.queue.add(t);
               }
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

            /*System.out.println("Putting 10 values from main");
            for (int i = 0; i< 10; i++) {
                bank.queue.put(new Transaction(
                    (int)(Math.random()*10), 
                    (int)(Math.random()*10), 
                    (int)(Math.random()*1000)));
            }*/

            for(int i=0;i<Integer.parseInt(args[1]);i++) //put args[1] here
            {
               bank.queue.put(bank.nullTrans);
            }
            
            // TODO: Add code here to wait for ALL the workers to finish
            /*w1.join();
            w2.join();*/
            for(int i=0;i<workers.size();i++)
            {
               workers.get(i).join();
            }
        } catch (InterruptedException e) {
         
        }
        
        PrintWriter out = null;
      try {
         out = new PrintWriter("resultOfRun.txt");
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
        for(int i = 0;i<bank.listOfAccs.size();i++)
        {
           out.write("acct:" + i + " bal:" + bank.listOfAccs.get(i).balance + " trans:" + bank.listOfAccs.get(i).numOfTransactions);
           out.println();
         System.out.println("acct:" + i + " bal:" + bank.listOfAccs.get(i).balance + " trans:" + bank.listOfAccs.get(i).numOfTransactions);
        }
        out.close();
        
    }
}


//TODO: Create an Account class with id, num of transactions and account balance

