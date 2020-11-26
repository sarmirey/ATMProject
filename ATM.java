import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class ATM {

    private static Scanner readfromHistory;
    public static void main(final String args[]) {

        //Initalizers and necessary varaibles to store important info
        Scanner sc = new Scanner(System.in);
        System.out.print("Please insert your Account Number: ");
        long temp_acc = sc.nextLong();
        long acc = 0;
        System.out.print("Please insert your PIN: ");
        long temp_pin = sc.nextLong();
        long pin = 0;
        double b = 0;
        double atm_b = 0;
        ArrayList<String> list = new ArrayList<String>();
        String filepathdata = "data.csv";
        String filepathATMBalance = "ATM_Balance.csv";
        File file1 = new File(filepathdata);
        File file2 = new File(filepathATMBalance);

        //try/catch to read multiple documents
        try {
            Scanner scanner_data = new Scanner(file1);
            Scanner scanner_ATM = new Scanner(file2);
            scanner_data.next();
            while (scanner_data.hasNext()) {
                String data = scanner_data.next();
                String[] values_data = data.split(",");

                //Checking if ID and PIN given is stored in "database"
                if (temp_acc == Long.parseLong(values_data[0]) && temp_pin == Long.parseLong(values_data[1])) {
                    //Collecting info about ATM_Balance
                    if (scanner_ATM.hasNext()) {
                        String atm_data = scanner_ATM.next();
                        String[] values_ATM = atm_data.split(",");
                        atm_b = Double.parseDouble(values_ATM[0]);
                    }
                    //saving account_num and PIN
                    acc = Long.parseLong(values_data[0]);
                    pin = Long.parseLong(values_data[1]);
                    b = Double.parseDouble(values_data[2]);
                    break;
                }
            }
            scanner_data.close();
            scanner_ATM.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Account number and pin was not found so it stayed at 0, meaning no account was found
        if (acc == 0 && pin == 0) {
            System.out.println("----------------------");
            System.out.println("Authorization failed");
            System.out.println("Please try again");
            System.out.println("----------------------");
            main(null);
        //Account was found so returning a message and sending the appropriate information to parse
        } else {
            System.out.println("----------------------");
            System.out.println("<" + acc + "> Successfully authorized.");
            System.out.println("----------------------");
            Account account_activity = new Account(acc, pin, b, atm_b, readfromHistory(list,acc));
            account_activity.menu();
        }
        sc.close();
    }

    //method to relevant history data from account and saving it into an arraylist
    public static ArrayList<String> readfromHistory(ArrayList<String> l, long a){
        l.removeAll(l);
        String original = String.valueOf(a) + "_history.txt";
        File new_file = new File(original);
        try{
            new_file.createNewFile();
            readfromHistory = new Scanner(new File(original));

            while(readfromHistory.hasNextLine()){
                l.add(readfromHistory.nextLine());
            }
            readfromHistory.close();
        }
        catch (Exception e) {
            e.printStackTrace();;
        }
        return l;
    }
}


//Account object that handles deposits, withdrawals, history retrieval, and log out of their account 
class Account {
    private ArrayList<String> his = new ArrayList<String>();
    private double atm_balance;
    private double balance;
    private final long pin;
    private long account_number;
    private static Scanner sc;
    private static Scanner x;
    private static Scanner update;

    //Constructor to initialize all inital data
    public Account(long account_number, long pin, double balance, double atm_balance, ArrayList<String> his) {
        this.account_number = account_number;
        this.pin = pin;
        this.balance = balance;
        this.atm_balance = atm_balance;
        this.his = his;
    }

    //Withdraw method that handles multiple scenarios that can occur
    public void withdraw(double w) {
        if ((w % 20 != 0)) { //User is trying to take out money that is not a multiple of 20
            System.out.println("-----------");
            System.out.println("Sorry we only take $20 bills");
            System.out.println("-----------");
            menu();
        //Checks if user's balance is less than 0
         }
        else if (balance < 0) {
        System.out.println("-----------");
        System.out.println("Your account is overdrawn! You may not make withdrawals at this time.");
        System.out.println("-----------");
        menu();
        } 
         else if (atm_balance <= 0) { //Not enough balance in the ATM
            System.out.println("-----------");
            System.out.println("Unable to process your withdrawal at this time.");
            System.out.println("-----------");
            menu();
         }
         else if ((atm_balance - w) <= 0) { 
            //You are taking away money from your account
            //but there is not enough money in the ATM to take out
            balance = balance - w; // Taking away money from user
            double leftover = Math.abs(atm_balance - w); // Collecting leftover to add it back to users account
            atm_balance = atm_balance - atm_balance; // Setting atm balance to 0 because machine has no more
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String result = formatter.format(date).toString() + " -" + String.valueOf(w) + " "
                    + String.valueOf(balance);
            his.add(result);
            System.out.println("-----------");
            System.out.println("Unable to dispense full amount requested at this time.");
            System.out.println("-----------");
            balance = balance + leftover;
            menu();
        } 
        //User is taking out more money than he has in his account, but ATM still has enough 
        else if ((balance - w) < 0) {
            atm_balance = atm_balance - w - 5;
            balance = balance - w - 5;
            System.out.println("-----------");
            System.out.println("Amount dispensed: $<" + w + ">");
            System.out.println("You have been charged an overdraft fee of $5. Current balance: <" + balance + ">");
            System.out.println("-----------");
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String result = formatter.format(date).toString() + " -" + String.valueOf(w) + " "
                    + String.valueOf(balance);
            his.add(result);
            menu();
            //Normal Withdrawal
        } else {
            balance = balance - w;
            atm_balance = atm_balance - w;
            System.out.println("-----------");
            System.out.println("Amount dispensed: $<" + w + ">");
            System.out.println("Current balance: <" + balance + ">");
            System.out.println("-----------");
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String result = formatter.format(date).toString() + " -" + String.valueOf(w) + " "
                    + String.valueOf(balance);
            his.add(result);
            menu();
        }
    }   
    //Method to check balance
    public void balance() {
        System.out.println("-----------");
        System.out.println("Current balance: <" + balance + ">");
        System.out.println("-----------");
        menu();
    }

    //method to deposit
    public void deposit(double b) {
        balance = balance + b;
        atm_balance = atm_balance + b;
        System.out.println("-----------");
        System.out.println("Current balance: <" + balance + ">");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = formatter.format(date).toString() + " " + String.valueOf(b) + " " + String.valueOf(balance);
        his.add(result);
        System.out.println("-----------");
        menu();

    }
 
    //method to check your account history
    public void history() {
        if(his.size() == 0){
            System.out.println("-----------");
            System.out.println("No history found");
            System.out.println("-----------");
            menu();
        }
        else{
            for (int i = his.size() - 1; i >= 0; i--) {
                System.out.println(his.get(i));
            }
            menu();
        }
    }

    //Menu Options
    public void menu() {
        System.out.println("Please select one of the following options");
        System.out.println("Withdraw");
        System.out.println("Deposit");
        System.out.println("Balance");
        System.out.println("History");
        System.out.println("Logout");
        System.out.println("End");

        Scanner x = new Scanner(System.in);
        System.out.print("What would you like to do today: ");
        String option = x.nextLine();

        if (option.equals("Withdraw")) {
            System.out.print("How much would you like to withdraw?: ");
            double amount = x.nextDouble();
            withdraw(amount);
        } else if (option.equals("Balance")) {
            balance();
        } else if (option.equals("Deposit")) {
            System.out.print("How much would you like to deposit?: ");
            double amount = x.nextDouble();
            deposit(amount);
        } else if (option.equals("Logout")) {
            logout(account_number, pin, balance, atm_balance, his);
        } else if (option.equals("History")) {
            history();
        } else if (option.equals("End")) {
            System.exit(0);
        } else {
            System.out.println("-----------");
            System.out.println("Couldn't understand request");
            System.out.println("-----------");
            menu();
        }
    }

    //logout method that saves all the info processed 
    public static void logout(long acc, long pin, double bal, double atm_bal, ArrayList<String> history){

        saveAccountInfo(acc, pin, bal, history);
        saveATMBalance(atm_bal);
        saveHistory(history, acc);
        

        System.out.println("-----------");
        System.out.println("Account <" + acc + "> logged out");
        System.out.println("-----------");
        ATM back = new ATM();
        back.main(null);

     }

     //saves history log onto txt file 
     public static void saveHistory(ArrayList<String> list, long account_number){

        String temp = String.valueOf(account_number) + "_historytemp.txt";
        String original = String.valueOf(account_number) + "_history.txt";

        File old_file = new File(original);
        File new_file = new File(temp);
        String a = "";
        try{
            update = new Scanner(new File(original));
            FileWriter fw = new FileWriter(temp,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for(int i = 0; i < list.size(); i++){
                pw.println(list.get(i));
            }

            update.close();
            pw.flush();
            pw.close();
            old_file.delete();
            File trash = new File(original);
            new_file.renameTo(trash);

        }
        catch (Exception e) {
            e.printStackTrace();;
        }
     }

    //saves ATM Balance for future transactions with other  suers
     public static void saveATMBalance(double atmb){
        String temp = "atm_temp.csv";
        String original = "ATM_balance.csv";
        File old_file = new File(original);
        File new_file = new File(temp);
        String a = "";
        try{
            FileWriter fw = new FileWriter(temp,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            x = new Scanner(new File(original));
            x.useDelimiter("[,\n]");

            while(x.hasNext()){
                a = x.next();
                pw.println(atmb);
            }

            x.close();
            pw.flush();
            pw.close();
            old_file.delete();
            File trash = new File(original);
            new_file.renameTo(trash);

        }
        catch (Exception e) {
            e.printStackTrace();;
        }
     }
     //saves account info such as account number, pin, and balance for an account
     public static void saveAccountInfo(long acc, long pin, double bal, ArrayList<String> history){
        String temp = "temp.csv";
        String original = "data.csv";
        File old_file = new File(original);
        File new_file = new File(temp);
        String u = "";
        String p = "";
        String b = "";
        try{
            FileWriter fw = new FileWriter(temp,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            sc = new Scanner(new File(original));
            sc.useDelimiter("[,\n]");

            while(sc.hasNext()){
                u = sc.next();
                p = sc.next();
                b = sc.next();
                if(u.equals(String.valueOf(acc))) {
                    pw.println(acc + "," + pin + "," + bal);
                }
                else {
                    pw.println(u + "," + p + "," + b);
                }
            }
            sc.close();
            pw.flush();
            pw.close();
            old_file.delete();
            File trash = new File(original);
            new_file.renameTo(trash);

        }
        catch (Exception e) {
            e.printStackTrace();;
        }
     }
}