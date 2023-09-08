package com.rc.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.rc.Round;

public class PDGARounds {
    public static ArrayList<Round> getRounds(int num){
        ArrayList<Round> rounds = new ArrayList<>();

        String html = "https://www.pdga.com/player/" + num + "/details";

        Document doc = null;
        try{
            doc = Jsoup.connect(html).get();
        } catch (Exception e) {
            e.printStackTrace();
            return rounds;
        }

        Elements tableElements = doc.select("table#player-results-details");
        Element firstTable = null;
        if(tableElements.size() > 0){
            firstTable = tableElements.get(0);
        }else{
            return rounds;
        }

        List<Node> firstTableRows = firstTable.childNodes().get(3).childNodes();
        int numRows = firstTableRows.size();

        // skip first row as header
        for (int i = 0; i < numRows; i++) {
            Node row = firstTableRows.get(i);
            if (row instanceof Element) {
                //Parse table entries out of DOM
                String tourney = ((TextNode)row.childNode(0).childNode(0).childNode(0)).text();
                String tier = ((TextNode)row.childNode(1).childNode(0)).text();
                int round = 0;
                try{
                    round = Integer.parseInt(((TextNode)row.childNode(4).childNode(0)).text());
                } catch (Exception e){
                    
                    round = 100;//sometimes round is "semis" or "finals"; counting on this -1 value not actually being used
                }
                Date date = getRoundDate(((TextNode)row.childNode(2).childNode(0)).text(), round, tier);
                int rating = Integer.parseInt(((TextNode)row.childNode(6).childNode(0)).text());
                Boolean evaluated = ((TextNode)row.childNode(7).childNode(0)).text().equals("Yes") ? true : false;
                Boolean included = ((TextNode)row.childNode(8).childNode(0)).text().equals("Yes") ? true : false;

                rounds.add(new Round(tourney, tier, date, round, rating, evaluated, included));
            }
        }
        return rounds;
    }
    //Calculate the actual date of the round for tourneys and leagues with date range
    private static Date getRoundDate(String dateStr, int round, String tier){
        Date roundDate = null;

        if(dateStr.length() < 12){
            String[] parts = dateStr.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(parts[2]));
            cal.set(Calendar.MONTH, getMonth(parts[1]));
            cal.set(Calendar.DATE, Integer.parseInt(parts[0]));
            roundDate = cal.getTime();
        }
        else{
            String[] split1 = dateStr.split(" ");
            String[] parts1 = split1[0].split("-");
            String[] parts2 = split1[2].split("-");

            Calendar cal = Calendar.getInstance();

            //Check if date range spans multiple years (bounds account for max 10 week league duration)
            if(getMonth(parts2[1]) < 3 && getMonth(parts1[1]) > 8){
                cal.set(Calendar.YEAR, Integer.parseInt(parts2[2]) - 1);
            } else {
                cal.set(Calendar.YEAR, Integer.parseInt(parts2[2]));
            }
            cal.set(Calendar.MONTH, getMonth(parts1[1]));
            cal.set(Calendar.DATE, Integer.parseInt(parts1[0]));

            if (tier.equals("L")){
                roundDate = getLeagueRoundDate(cal, round);
            } else {
                roundDate = cal.getTime();
            }
        }
        return roundDate;
    }
    private static int getMonth(String monStr){
        int month = -1;
        switch (monStr) {
            case "Jan":  
                month = 0;
                break;
            case "Feb":  
                month = 1;
                break;
            case "Mar":  
                month = 2;
                break;
            case "Apr":  
                month = 3;
                break;
            case "May":  
                month = 4;
                break;
            case "Jun":  
                month = 5;
                break;
            case "Jul":  
                month = 6;
                break;
            case "Aug":  
                month = 7;
                break;
            case "Sep":  
                month = 8;
                break;
            case "Oct": 
                month = 9;
                break;
            case "Nov": 
                month = 10;
                break;
            case "Dec": 
                month = 11;
                break;
        }
        return month;
    }
    private static Date getLeagueRoundDate(Calendar cal, int round){
        cal.add(Calendar.DATE, 7 * (round - 1));
        return cal.getTime();
    }
}
