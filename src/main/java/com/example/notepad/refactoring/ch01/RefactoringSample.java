package com.example.notepad.refactoring.ch01;

import java.util.*;

public class RefactoringSample {
    private static class Play {
        private String name;
        private String type;

        public Play(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    private static class Invoices{
        private String customerName;
        private List<Performances> performances;

        private static class Performances{
            private String playId;
            private int audience;

            public Performances(String playId, int audience) {
                this.playId = playId;
                this.audience = audience;
            }
        }

        public Invoices(String customerName, List<Performances> performances) {
            this.customerName = customerName;
            this.performances = performances;
        }
    }

    public static String statement(Invoices invoice, Map<String, Play> plays) throws Exception {
        var totalAmount = 0;
        var volumeCredits = 0;
        var result = "청구 내역 고객명 : " + invoice.customerName + '\n';

        for(var perf : invoice.performances){
            Play play = plays.get(perf.playId);
            var thisAmount = 0;

            switch(play.type){
                case "tragedy":
                    thisAmount = 40000;
                    if(perf.audience > 30){
                        thisAmount += 1000 * (perf.audience - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if(perf.audience > 20){
                        thisAmount += 10000 + 500 * (perf.audience - 20);
                    }
                    thisAmount += 300 * perf.audience;
                    break;
                default:
                    throw new Exception("알 수 없는 장르");
            }

            volumeCredits += Math.max(perf.audience - 30, 0);
            if("comedy".equals(play.type)) volumeCredits += Math.floor(perf.audience / 5);

            result += play.name + ": " + thisAmount + "원, " + perf.audience + "석\n";
            totalAmount += thisAmount;
        }

        result += "총액: " + totalAmount + "원\n";
        result += "적립 포인트: " + volumeCredits + "점\n";
        return result;
    }

    public static void main(String[] args) throws Exception {
        List<Invoices.Performances> performances = new ArrayList<>();
        performances.add(new Invoices.Performances("hamlet",55));
        performances.add(new Invoices.Performances("as-like",35));
        performances.add(new Invoices.Performances("othello",40));

        Invoices invoices = new Invoices("BigCo", performances);
        Map<String, Play> plays = new HashMap<>();
        plays.put("hamlet",new Play("Hamlet", "tragedy"));
        plays.put("as-like",new Play("As You Like It", "comedy"));
        plays.put("othello",new Play("Othello", "tragedy"));

        System.out.println(statement(invoices, plays));
    }
}
