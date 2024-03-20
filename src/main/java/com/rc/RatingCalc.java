package com.rc;

import java.util.*;
import com.rc.util.*;

class RatingCalc{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter PDGA number");
        String pdgaNum = scanner.nextLine();
        ArrayList<Round> rounds = new ArrayList<>();
        try{
            rounds = PDGARounds.getRounds(Integer.parseInt(pdgaNum));
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Something went wrong.");
            scanner.close();
            return;
        }

        System.out.println("Current rating: " + calculate(rounds));
        System.out.println("Enter rounds NOT yet in your rating to calculate your new rating");
        System.out.println("For normal tourneys, enter date it started. For league enter the actual date of the round");

        boolean first = true;
        while(true){
            System.out.println("Enter new round in the form [date(MM/DD/YYYY) rndNum numHoles rating] not including the brackets");
            if(!first){
                System.out.println("When done, simply click enter");
            }
            String roundStr = scanner.nextLine();
            if(roundStr == ""){
                break;
            } else {
                String[] pieces = roundStr.split(" ");
                String[] parts = pieces[0].split("/");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(parts[2]));
                cal.set(Calendar.MONTH, Integer.parseInt(parts[0]) - 1);
                cal.set(Calendar.DATE, Integer.parseInt(parts[1]));
                rounds.add(new Round("foo", "foo", cal.getTime(), Integer.parseInt(pieces[1]), Integer.parseInt(pieces[2]), Integer.parseInt(pieces[3]), true, true));
            }
            first = false;
        }
        scanner.close();

        rounds = checkDates(rounds, false);
        rounds = checkRating(rounds);
        System.out.println("New rating: " + calculate(rounds));
    }
    private static long calculate(ArrayList<Round> rounds){//misses by 1-2 sometimes... :shrug:

        Collections.sort(rounds, Comparator.comparing(Round::getRoundDate).reversed()
            .thenComparing(Round::getRound, (int1, int2) -> int2 - int1));//Reverse order of round numbers
        
        double total = 0.0;
        int count = 0;
        int dubCount = 0;
        for(Round round : rounds){
            if(round.getEvaluated()){
                dubCount += 1;
            }
            if(round.getIncluded()){
                total += (round.getRating() * round.getNumHoles());
                count += round.getNumHoles();
            }
        };

        long doubled = Math.round((double)dubCount / 4.0);
        for(int i = 0; i < doubled; i++){//most recent 25% are double counted
            if(!rounds.get(i).getIncluded()){
                doubled += 1;
                continue;
            }

            total += (rounds.get(i).getRating() * rounds.get(i).getNumHoles());
            count += rounds.get(i).getNumHoles();
        }
        return Math.round(total / (double)count);
    }
    private static ArrayList<Round> checkDates(ArrayList<Round> rounds, boolean year2){
        Collections.sort(rounds, Comparator.comparing(Round::getRoundDate).reversed()
            .thenComparing(Round::getRound, (int1, int2) -> int2 - int1));

        Date maxDate = rounds.stream().max(Comparator.comparing(Round::getRoundDate)).orElseThrow(NoSuchElementException::new).getRoundDate();
        System.out.println(maxDate.toString());//check evaluated? TODO

        int size = rounds.size();
        int rndCnt = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(maxDate);
        if(!year2){
            cal.add(Calendar.YEAR, -1);
        } else {
            cal.add(Calendar.YEAR, -2);
        }
        maxDate = cal.getTime();
        for(int i = 0; i < size; i++){
            if(rounds.get(i).getRoundDate().compareTo(maxDate) > 0 && rounds.get(i).getEvaluated()){
                rounds.get(i).setIncluded(true);
            } else {
                rounds.get(i).setIncluded(false);
                rndCnt += 1;
            }
        }
        if(year2 && rndCnt > 8){
            for(int i = 8; i < size; i++){
                rounds.get(i).setIncluded(false);
            }
        } else if (!year2 && rndCnt < 8){
            return checkDates(rounds, true);
        }
        return rounds;
    }
    private static ArrayList<Round> checkRating(ArrayList<Round> rounds){
        double cutoff = calculateStandardDeviation(rounds) * 2.5;
        long rating = calculate(rounds);
        cutoff = cutoff > 100 ? 100 : cutoff;
        cutoff = rating - cutoff;
        for(Round round : rounds) {
            if(round.getRating()  < cutoff){
                round.setIncluded(false);
            }
        }
        return rounds;
    }
    private static double calculateStandardDeviation(ArrayList<Round> rounds) {

        // get the sum of array
        double sum = 0.0;
        for(Round round : rounds) {
            if(round.getIncluded()){
                sum += round.getRating();
            }
        }
    
        // get the mean of array
        int length = rounds.size();
        double mean = sum / length;
    
        // calculate the standard deviation
        double standardDeviation = 0.0;
        for (Round round : rounds) {
            if(round.getIncluded()){
                standardDeviation += Math.pow(round.getRating() - mean, 2);
            }
        }
    
        return Math.sqrt(standardDeviation / length);
    }
}