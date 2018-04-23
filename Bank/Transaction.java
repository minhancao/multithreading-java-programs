public class Transaction {

   int fromAccount;
   int toAccount;
   int amount;

   public Transaction(int from, int to, int amt) {
       fromAccount = from;
       toAccount = to;
       amount = amt;
   }

   public String toString() {
       return "Transaction: from = " + fromAccount + ", to = " + toAccount + " amount = " + amount;
   }

}


