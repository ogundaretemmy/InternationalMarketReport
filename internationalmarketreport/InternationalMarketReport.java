/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package internationalmarketreport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Ogundare Tope
 *
 */
public class InternationalMarketReport {

    public static void main(String[] args) {

        try {
            processRecord(args[0]);

            System.out.println("Service Up and Running");

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    private static void processRecord(String csvFile) {
        // read csv and output to screen and file

        String line = "";
        String cvsSplitBy = ",";
        BufferedReader br = null;

        BigDecimal amount;
        BigDecimal AgreedFx;
        BigDecimal PricePerUnit;
        BigDecimal Units;
        String toPrintBuy = "";
        String toPrintSell = "";
        String SetDate;
        String newDate;
        String currency;
        Calendar cal = Calendar.getInstance();

        ArrayList<record> ar = new ArrayList<record>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] result = line.split(cvsSplitBy);

                System.out.println("entry= " + result[0] + " , Buy/Sell=" + result[1] + ", AgreedFx=" + result[2] + ", Currency=" + result[3] + ", InstructionDate=" + result[4] + ", SettlementDate=" + result[5] + ", Units=" + result[6] + ",Price per unit=" + result[7]);
                AgreedFx = new BigDecimal(result[2].trim());
                PricePerUnit = new BigDecimal(result[7].trim());
                Units = new BigDecimal(result[6].trim());
                
                //Computing the Amount unit*Price per unit * Agreed FX rate
                amount = AgreedFx.multiply(PricePerUnit).multiply(Units);

                currency = result[3];
                SetDate = result[5];
                Date dt1 = new SimpleDateFormat("dd MMM yyyy").parse(SetDate);

                cal.setTime(dt1);

                //Curreny check and date conversion to new working day
                if ((currency.compareToIgnoreCase("AED") == 0) || (currency.compareToIgnoreCase("SAR") == 0)) {

                    if ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)) {
                        cal.add(Calendar.DAY_OF_WEEK, 2);

                    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {

                        cal.add(Calendar.DAY_OF_WEEK, 1);
                    }

                } else {

                    if ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)) {
                        cal.add(Calendar.DAY_OF_WEEK, 2);

                    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

                        cal.add(Calendar.DAY_OF_WEEK, 1);

                    }
                }

                //Coverting date back to the former format
                Date newdate = cal.getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy");
                String SetDateNew = format1.format(newdate);

                //adding record to Arraylist
                ar.add(new record(result[0], result[1], AgreedFx, result[3], result[4], SetDateNew, Units, PricePerUnit, amount));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Sorting by amount
        Collections.sort(ar, new MyAmountComp());
//separating Buy and Sell 
        for (int i = 0; i < ar.size(); i++) {

            if (ar.get(i).BuySell.compareToIgnoreCase("B") == 0) {
                toPrintBuy += ar.get(i).entry + "," + ar.get(i).BuySell + "," + ar.get(i).AgreedFx + "," + ar.get(i).Currency + ","
                        + ar.get(i).InstructionDate + "," + ar.get(i).SettlementDate + "," + ar.get(i).Units + "," + ar.get(i).PricePerUnit + "," + ar.get(i).amount + "\n";
            } else {
                toPrintSell += ar.get(i).entry + "," + ar.get(i).BuySell + "," + ar.get(i).AgreedFx + "," + ar.get(i).Currency + ","
                        + ar.get(i).InstructionDate + "," + ar.get(i).SettlementDate + "," + ar.get(i).Units + "," + ar.get(i).PricePerUnit + "," + ar.get(i).amount + "\n";
            }

        }

        //Report print out
        System.out.println("\n***************** Outgoing Report***********************");
        System.out.println("Entity,Buy/Sell,AgreedFx,Currency,InstructionDate,SettlementDate,Units, Price per unit,Amount(USD)");
        System.out.println(toPrintBuy);
        System.out.println("***************** Incoming Report***********************");
        System.out.println("Entity,Buy/Sell,AgreedFx,Currency,InstructionDate,SettlementDate,Units, Price per unit,Amount(USD)");
        System.out.println(toPrintSell);

    }
}  
    
    
    class record {
//Record class to define all the fields

    String entry;
    String BuySell;
    BigDecimal AgreedFx;
    BigDecimal PricePerUnit;
    BigDecimal Units;
    BigDecimal amount;
    String SettlementDate;
    String InstructionDate;
    String Currency;

    // Constructor 
    public record(String entry, String BuySell,
            BigDecimal AgreedFx, String Currency, String InstructionDate,
            String SettlementDate, BigDecimal Units, BigDecimal PricePerUnit, BigDecimal amount) {

        this.entry = entry;
        this.BuySell = BuySell;
        this.AgreedFx = AgreedFx;
        this.Currency = Currency;
        this.InstructionDate = InstructionDate;
        this.SettlementDate = SettlementDate;
        this.Units = Units;
        this.PricePerUnit = PricePerUnit;
        this.amount = amount;
    }

   
}

class MyAmountComp implements Comparator<record> {
//Sorting Class

    @Override
    public int compare(record e1, record e2) {
        if ((e1.amount.compareTo(e2.amount) < 0)) {

            return 1;
        } else {
            return -1;
        }
    }
}
