/**
 * This class represents a bank account whose current balance is a nonnegative
 * amount in US dollars.
 */
public class Account {

    public int balance;
    Account parentAccount = null;

    //constructor
    public Account(int balance, Account parentAccount){
        this.parentAccount = parentAccount;
        //
        this.balance = balance;

        //not sure about the balance//
    }

    /** Initialize an account with the given BALANCE. */
    public Account(int balance) {
        this.balance = balance;
        //
        this.parentAccount = null;
    }

    /** Deposits AMOUNT into the current account. */
    public void deposit(int amount) {
        if (amount < 0) {
            System.out.println("Cannot deposit negative amount.");
        } else {
            this.balance += amount;
        }
    }

    /**
     * Subtract AMOUNT from the account if possible. If subtracting AMOUNT
     * would leave a negative balance, print an error message and leave the
     * balance unchanged.
     */
     public boolean withdraw(int amount) {
       // TODO
       if (amount < 0) {
         System.out.println("Cannot withdraw negative amount.");
         return false;
       } else if (this.balance < amount) {
         if (this.parentAccount == null){
           System.out.println("Insufficient funds");
           return false;
         }else if(this.parentAccount.withdraw(amount - this.balance)){
           // System.out.println(this.parentAccount.balance);
           this.balance = 0;
           System.out.println("set");
           return true;
         }else{
           return false;
         }

       }else {
         this.balance -= amount;
         // System.out.println("WITHDREW " + amount + "left:" + this.balance);
         return true;
       }

     }

    /**
     * Merge account OTHER into this account by removing all money from OTHER
     * and depositing it into this account.
     */
    public void merge(Account other) {
        // TODO
        this.deposit(other.balance);
        other.withdraw(other.balance);
    }

    public static void main(String[] args){
      Account pa = new Account(20);
      Account ca = new Account(20,pa);
      Account cca = new Account(20,ca);
      System.out.println(ca.balance);
      // cca.withdraw(2);
      // System.out.println(cca.balance); //shoukd be 18
      // cca.deposit(2);
      // System.out.println(cca.balance); //21
      cca.withdraw(22);
      System.out.println(cca.balance);  //0
      System.out.println(ca.balance);   //18
      cca.withdraw(22);
      System.out.println(cca.balance);  //0
      System.out.println(ca.balance);   //0
      System.out.println(pa.balance);    //16

      // System.out.println(a.balance);
      //  Account dav = new Account(100);
      //   dav.parentAccount = Account mary;
      //   dav.parentAccount.balance = 500;
      //   Account(500, mary);
      //
      //
      //   dav.withdraw(200);
      //   System.out.println(mary.balance+" and "+dav.balance);
    }
}
