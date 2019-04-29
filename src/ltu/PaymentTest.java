package ltu;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;

public class PaymentTest {
   
   private PaymentImpl test = null;
   private ICalendar cal = null;
   
   private static final String DEFAULT_RULES = "student100loan=7088\nstudent100subsidy=2816\nstudent50loan=3564\nstudent50subsidy=1396\nstudent0loan=0\nstudent0subsidy=0\nfulltimeIncome=85813\nparttimeIncome=128722\n";
   private final int FULL_LOAN = 7088;
   private final int HALF_LOAN = 3564;
   private final int FULL_SUBSIDY = 2816;
   private final int HALF_SUBSIDY = 1396;
   private final String personId30year2016 = "19860101-5622"; //Generiskt personId för användning i alla tester som inte har med ålder att göra

   
   @Before
   public void init() {
      cal = new Calendar2016jan();
      
      try {
         test = new PaymentImpl(cal);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
   }
   /*Age & amount tests
   [ID: 101] The student must be at least 20 years old to receive subsidiary and student loans.
   [ID: 102] The student may receive subsidiary until the year they turn 56.
   [ID: 103] The student may not receive any student loans from the year they turn 47.
   [ID: 501] Full time students are entitled to loan: 7088 SEK / month
   [ID: 502] Full time students are entitled to subsidiary: 2816 SEK / month
   [ID: 505] A person who is entitled to receive a student loan will always receive the full amount.

   personId för följande åldrar, år 2016...
   19 år:   19970101-5622
   20 år:   19960101-5622
   21 år:   19950101-5622
   46 år:   19700101-5622
   47 år:   19690101-5622
   48 år:   19680101-5622
   56 år:   19600101-5622
   57 år:   19590101-5622
   58 år:   19580101-5622*/
   //getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)
   
   @Test
   public void testAge19() {
      assertEquals("[ID:101] 19 year old student does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount("19970101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge20() {
		assertEquals("[ID:101] [ID: 501] [ID: 502] [ID: 505] 20 year old, fulltime and full completion ratio student does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount("19960101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge21() {
		assertEquals("[ID:101] [ID: 501] [ID: 502] [ID: 505] 21 year old, fulltime and full completion ratio student does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount("19950101-5622", 0, 100, 100));
   }
      
   @Test
   public void testAge30() {
		assertEquals("[ID: 501] [ID: 502] [ID: 505] 30 year old, fulltime and full completion ratio student does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 100, 100)); //Egen kontroll av 30 år, 2016
   }
   @Test
   public void testAge56() {
		assertEquals("[ID:102] [ID: 502] 56 year old, fulltime and full completion ratio student does not get FULL_SUBSIDY", FULL_SUBSIDY, test.getMonthlyAmount("19600101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge57() {
		assertEquals("[ID:102] 57 year old student does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount("19590101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge58() {
		assertEquals("[ID:102] 58 year old student does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount("19580101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge46() {
		assertEquals("[ID:103] [ID: 501] [ID: 502] [ID: 505] 46 year old, fulltime and full completion ratio student does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount("19700101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge47() {
		assertEquals("[ID:103] [ID: 502] 47 year old, fulltime and full completion ratio student does not get FULL_SUBSIDY and 0 loan", FULL_SUBSIDY, test.getMonthlyAmount("19690101-5622", 0, 100, 100));
   }
   
   @Test
   public void testAge48() {
		assertEquals("[ID:103] [ID: 502] 48 year old, fulltime and full completion ratio student does not get FULL_SUBSIDY and 0 loan", FULL_SUBSIDY, test.getMonthlyAmount("19680101-5622", 0, 100, 100));
   }

   
   /*Study rate & amount tests
   [ID: 201] The student must be studying at least half time to receive any subsidiary.
   [ID: 202] A student studying less than full time is entitled to 50% subsidiary.
   [ID: 203] A student studying full time is entitled to 100% subsidiary.
   [ID: 501] Full time students are entitled to loan: 7088 SEK / month
   [ID: 502] Full time students are entitled to subsidiary: 2816 SEK / month
   [ID: 503] Less than full time students are entitled to loan: 3564 SEK / month
   [ID: 504] Less than full time students are entitled to subsidiary: 1396 SEK / month
   [ID: 505] A person who is entitled to receive a student loan will always receive the full amount.*/ 
   //getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)
   
   @Test
   public void testStudyRate10() {
      assertEquals("[ID: 201] Student with study rate 10% and full completion ratio does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 0, 10, 100));
   }
   
   @Test
   public void testStudyRate49() {
		assertEquals("[ID: 201] Student with study rate 49% and full completion ratio does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 0, 49, 100));
   }
   
   @Test
   public void testStudyRate50() {
      assertEquals("[ID: 202] [ID: 503] [ID: 504] [ID: 505] Student with study rate 50% and full completion ratio does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 50, 100));
   }
   
   @Test
   public void testStudyRate51() {
      assertEquals("[ID: 202] [ID: 503] [ID: 504] [ID: 505] Student with study rate 51% and full completion ratio does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 51, 100));
   }
   
   @Test
   public void testStudyRate99() {
      assertEquals("[ID: 202] [ID: 503] [ID: 504] [ID: 505] Student with study rate 99% and full completion ratio does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 99, 100));
   }
   
   @Test
   public void testStudyRate100() {
      assertEquals("[ID: 203] [ID: 501] [ID: 502] [ID: 505] Student with study rate 100% and full completion ratio does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 100, 100));
   }
   
   @Test
   public void testStudyRate101() {
      assertEquals("[ID: 203] [ID: 501] [ID: 502] [ID: 505] Student with study rate 101% and full completion ratio does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 101, 100));
   }
   
   @Test
   public void testStudyRate200() {
      assertEquals("[ID: 203] [ID: 501] [ID: 502] [ID: 505] Student with study rate 200% and full completion ratio does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 200, 100));
   }
   
   /*Income and amount tests
   [ID: 301] A student who is studying full time or more is permitted to earn a maximum of 85 813SEK per year in order to receive any subsidiary or student loans.
   [ID: 302] A student who is studying less than full time is allowed to earn a maximum of 128 722SEK per year in order to receive any subsidiary or student loans.
   [ID: 501] Full time students are entitled to loan: 7088 SEK / month
   [ID: 502] Full time students are entitled to subsidiary: 2816 SEK / month
   [ID: 503] Less than full time students are entitled to loan: 3564 SEK / month
   [ID: 504] Less than full time students are entitled to subsidiary: 1396 SEK / month
   [ID: 505] A person who is entitled to receive a student loan will always receive the full amount.*/
   //getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)
   
   @Test
   public void testIncome85812Fulltime() {
      assertEquals("[ID: 301] [ID: 501] [ID: 502] [ID: 505] Student with study rate 100% and income 85 812 sek/y does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 85812, 100, 100));
   }
   
   @Test
   public void testIncome85813Fulltime() {
		assertEquals("[ID: 301] [ID: 501] [ID: 502] [ID: 505] Student with study rate 100% and income 85 813 sek/y does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 85813, 100, 100));
   }
   
   @Test
   public void testIncome85814Fulltime() {
      assertEquals("[ID: 301] Student with study rate 100% and income 85 814 sek/y does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 85814, 100, 100));
   }
   
   @Test
   public void testIncome128722Fulltime() {
      assertEquals("[ID: 301] Student with study rate 100% and income 128 722 sek/y does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 128722, 100, 100));
   }
   
   @Test
   public void testIncome128723Fulltime() {
      assertEquals("[ID: 301] Student with study rate 100% and income 128 723 sek/y does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 128723, 100, 100));
   }
   
   @Test
   public void testIncome85812AboveFulltime() {
      assertEquals("[ID: 301] [ID: 501] [ID: 502] [ID: 505] Student with study rate 101% and income 85 812 sek/y does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 85812, 101, 100));
   }
   
   @Test
   public void testIncome85813AboveFulltime() {
		assertEquals("[ID: 301] [ID: 501] [ID: 502] [ID: 505] Student with study rate 101% and income 85 813 sek/y does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 85813, 101, 100));
   }
   
   @Test
   public void testIncome85814AboveFulltime() {
      assertEquals("[ID: 301] Student with study rate 101% and income 85 814 sek/y does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 85814, 101, 100));
   }
   
   @Test
   public void testIncome128721studyRate99() {
      assertEquals("[ID: 302] [ID: 503] [ID: 504] [ID: 505] Student with study rate 99% and income 128 721 sek/y does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 85814, 99, 100));
   }
   
   @Test
   public void testIncome128722studyRate99() {
      assertEquals("[ID: 302] [ID: 503] [ID: 504] [ID: 505] Student with study rate 99% and income 128 722 sek/y does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 128722, 99, 100));
   }
   
   @Test
   public void testIncome128723studyRate99() {
      assertEquals("[ID: 302] Student with study rate 99% and income 128 723 sek/y does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 128723, 99, 100));
   }
   
   @Test
   public void testIncome128721studyRate50() {
      assertEquals("[ID: 302] [ID: 503] [ID: 504] [ID: 505] Student with study rate 50% and income 128 721 sek/y does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 85814, 50, 100));
   }
   
   @Test
   public void testIncome128722studyRate50() {
      assertEquals("[ID: 302] [ID: 503] [ID: 504] [ID: 505] Student with study rate 50% and income 128 722 sek/y does not get HALF_LOAN+HALF_SUBSIDY", HALF_LOAN+HALF_SUBSIDY, test.getMonthlyAmount(personId30year2016, 128722, 50, 100));
   }
   
   @Test
   public void testIncome128723studyRate50() {
      assertEquals("[ID: 302] Student with study rate 50% and income 128 723 sek/y does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 128723, 50, 100));
   }
   
   /*Completion ratio & amount tests
   [ID: 401] A student must have completed at least 50% of previous studies in order to receive any subsidiary or student loans.
   [ID: 501] Full time students are entitled to loan: 7088 SEK / month
   [ID: 502] Full time students are entitled to subsidiary: 2816 SEK / month
   [ID: 505] A person who is entitled to receive a student loan will always receive the full amount.*/
   //getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)
   
   @Test
   public void testCompletionRatio49() {
      assertEquals("[ID: 401] Student with study rate 100% and 49% completion ratio does not get 0 in subsidiary and student loans", 0, test.getMonthlyAmount(personId30year2016, 0, 100, 49));
   }
   
   @Test
   public void testCompletionRatio50() {
      assertEquals("[ID: 401] [ID: 501] [ID: 502] [ID: 505] Student with study rate 100% and 50% completion ratio does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 100, 50));      assertEquals("[ID: 401] [ID: 501] [ID: 502] [ID: 505] Student with study rate 100% and 51% completion ratio does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 100, 51));
   }
   
   @Test
   public void testCompletionRatio51() {
      assertEquals("[ID: 401] [ID: 501] [ID: 502] [ID: 505] Student with study rate 100% and 51% completion ratio does not get FULL_LOAN+FULL_SUBSIDY", FULL_LOAN+FULL_SUBSIDY, test.getMonthlyAmount(personId30year2016, 0, 100, 51));
   }

   //Exceptions tests   
   //getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)  
   
   @Test (expected = IllegalArgumentException.class)
   public void invalidPersonIdValue() {
      test.getMonthlyAmount(null, 0, 0, 0);
   }

   @Test (expected = IllegalArgumentException.class)
   public void invalidIncomeValue() {
      test.getMonthlyAmount(personId30year2016, -1, 0, 0);
   }

   @Test (expected = IllegalArgumentException.class)
   public void invalidStydyRateValue() {
      test.getMonthlyAmount(personId30year2016, 0, -1, 0);
   }

   @Test (expected = IllegalArgumentException.class)
   public void invalidCompletionRatioValue() {
      test.getMonthlyAmount(personId30year2016, 0, 0, -1);
   }

   @Test (expected = IllegalArgumentException.class)
   public void invalidPersonIdLenghtToShort() {
      test.getMonthlyAmount("198804245622", 0, 0, 0);
   }

   @Test (expected = IllegalArgumentException.class)
   public void invalidPersonIdLenghtToLong() {
      test.getMonthlyAmount("19880424--5622", 0, 0, 0);
   }

   /*Payment day tests
   [ID: 506] Student loans and subsidiary is paid on the last weekday (Monday to Friday) every month.*/
   
   @Test
   public void testPayDayJan2016() {
      cal = new Calendar2016jan();

      try {
         test = new PaymentImpl(cal, DEFAULT_RULES);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
      assertEquals("[ID: 506] Payment day was not the last weekday in January 2016", "20160129", test.getNextPaymentDay());
   }
   
   @Test
   public void testPayDayFeb2016() {
      cal = new Calendar2016feb();

      try {
         test = new PaymentImpl(cal, DEFAULT_RULES);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
      assertEquals("[ID: 506] Payment day was not the last weekday in February 2016", "20160229", test.getNextPaymentDay());
   }
   
   @Test
   public void testPayDayMar2016() {
      cal = new Calendar2016mar();

      try {
         test = new PaymentImpl(cal, DEFAULT_RULES);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
      assertEquals("[ID: 506] Payment day was not the last weekday in March 2016", "20160331", test.getNextPaymentDay());
   }
   
   @Test
   public void testPayDayApr2016() {
      cal = new Calendar2016apr();

      try {
         test = new PaymentImpl(cal, DEFAULT_RULES);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
      assertEquals("[ID: 506] Payment day was not the last weekday in April 2016", "20160429", test.getNextPaymentDay());
   }
   
   @Test
   public void testPayDayMay2016() {
      cal = new Calendar2016may();

      try {
         test = new PaymentImpl(cal, DEFAULT_RULES);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
      assertEquals("[ID: 506] Payment day was not the last weekday in May 2016", "20160531", test.getNextPaymentDay());
   }
   
   @Test
   public void testPayDayJun2016() {
      cal = new Calendar2016jun();

      try {
         test = new PaymentImpl(cal, DEFAULT_RULES);
      } catch (IOException e) {
         System.out.println("IOExeption: " + e);
      }
      assertEquals("[ID: 506] Payment day was not the last weekday in June 2016", "20160630", test.getNextPaymentDay());
   }


}
